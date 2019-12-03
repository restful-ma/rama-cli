package restful.api.metric.analyzer.cli.parser;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.google.protobuf.MessageOrBuilder;
import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import restful.api.metric.analyzer.cli.model.generated.internal.Model;
import restful.api.metric.analyzer.cli.model.generated.internal.Model.Api;
import restful.api.metric.analyzer.cli.model.generated.internal.Model.ContentMediaType;
import restful.api.metric.analyzer.cli.model.generated.internal.Model.DataModel;
import restful.api.metric.analyzer.cli.model.generated.internal.Model.DataModelRelationShip;
import restful.api.metric.analyzer.cli.model.generated.internal.Model.DataType;
import restful.api.metric.analyzer.cli.model.generated.internal.Model.HttpMethod;
import restful.api.metric.analyzer.cli.model.generated.internal.Model.Parameter;
import restful.api.metric.analyzer.cli.model.generated.internal.Model.ParameterLocation;
import restful.api.metric.analyzer.cli.model.generated.internal.Model.Path;
import restful.api.metric.analyzer.cli.model.generated.internal.Model.Property;
import restful.api.metric.analyzer.cli.model.generated.internal.Model.RequestBody;
import restful.api.metric.analyzer.cli.model.generated.internal.Model.SpecificationDescriptor;
import restful.api.metric.analyzer.cli.model.generated.internal.Model.SpecificationFile;
import restful.api.metric.analyzer.cli.model.generated.internal.Model.SpecificationFormat;
import restful.api.metric.analyzer.cli.model.generated.wadl.Application;
import restful.api.metric.analyzer.cli.model.generated.wadl.Doc;
import restful.api.metric.analyzer.cli.model.generated.wadl.Grammars;
import restful.api.metric.analyzer.cli.model.generated.wadl.HTTPMethods;
import restful.api.metric.analyzer.cli.model.generated.wadl.Include;
import restful.api.metric.analyzer.cli.model.generated.wadl.Link;
import restful.api.metric.analyzer.cli.model.generated.wadl.Method;
import restful.api.metric.analyzer.cli.model.generated.wadl.ObjectFactory;
import restful.api.metric.analyzer.cli.model.generated.wadl.Option;
import restful.api.metric.analyzer.cli.model.generated.wadl.Param;
import restful.api.metric.analyzer.cli.model.generated.wadl.ParamStyle;
import restful.api.metric.analyzer.cli.model.generated.wadl.Representation;
import restful.api.metric.analyzer.cli.model.generated.wadl.Request;
import restful.api.metric.analyzer.cli.model.generated.wadl.Resource;
import restful.api.metric.analyzer.cli.model.generated.wadl.ResourceType;
import restful.api.metric.analyzer.cli.model.generated.wadl.Resources;
import restful.api.metric.analyzer.cli.model.generated.wadl.Response;
import restful.api.metric.analyzer.cli.model.generated.wadl.properties.ComplexTypeType;
import restful.api.metric.analyzer.cli.model.generated.wadl.properties.ElementType;
import restful.api.metric.analyzer.cli.model.generated.wadl.properties.SchemaType;
import restful.api.metric.analyzer.cli.model.generated.wadl.properties.SequenceType;

/**
 *
 */
public class WadlParser extends Parser {

	private static final Logger logger = LoggerFactory.getLogger(WadlParser.class);
	private HashMap<String, Property> properties;
	private SpecificationFile.Builder spec;

	private int counter;

	public WadlParser() {
		fileEnding = "wadl";
	}

	@Override
	public SpecificationFile loadPublicUrl(String url) throws ParseException {
		try {
			initVariables(url);
			URL publicUrl = new URL(url);
			unmarshal(publicUrl);
			spec.setTitle(publicUrl.getPath());
			return spec.build();
		} catch (MalformedURLException | JAXBException e) {
			throw new ParseException(e.getMessage(), 0);
		}
	}

	@Override
	public SpecificationFile loadLocalFile(String url) throws ParseException {
		try {
			initVariables(url);
			File file = new File(url);
			unmarshal(file);
			spec.setTitle(file.getName());
			return spec.build();
		} catch (JAXBException e) {
			throw new ParseException(e.getMessage(), 0);
		}
	}

	/**
	 * Freshly initializes used attributes
	 *
	 * @param url file location
	 */
	private void initVariables(String url) {
		properties = new HashMap<>();
		spec = SpecificationFile.newBuilder();
		spec.setSpecificationDescriptor(
				SpecificationDescriptor.newBuilder()
						.setSpecificationFormat(SpecificationFormat.WADL)
						.setVersion("http://wadl.dev.java.net/2009/02"))
				.setApiVersion("1.0.0");
		spec.setFilePath(url);
	}

	/**
	 * Unmarshaller with all WADL classes
	 *
	 * @return
	 * @throws JAXBException
	 */
	private Unmarshaller getUnmarshaller() throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(Application.class, Doc.class, Grammars.class, HTTPMethods.class,
				Include.class, Link.class, Method.class, ObjectFactory.class, Option.class, Param.class,
				ParamStyle.class, Representation.class, Request.class, Resource.class, Resources.class,
				ResourceType.class, Response.class);
		return context.createUnmarshaller();
	}

	private void unmarshal(File file) throws JAXBException  {
		Unmarshaller unmarshaller = getUnmarshaller();
		convertToInternalModel((Application) unmarshaller.unmarshal(file));
	}

	private void unmarshal(URL url) throws JAXBException {
		Unmarshaller unmarshaller = getUnmarshaller();
		convertToInternalModel((Application) unmarshaller.unmarshal(url));
	}

	/**
	 * Converts from the parsed WADL model to the internal protobuf model
	 *
	 * @param application WADL model
	 * @throws JAXBException
	 */
	private void convertToInternalModel(Application application) throws JAXBException {
		if (application.getGrammars() != null) {
			buildProperties(application.getGrammars().getAny());
		}

		for (Resources resources : application.getResources()) {
			Api.Builder api = Api.newBuilder().addBasePath(resources.getBase());
			for (Resource resource : resources.getResource()) {
				addPaths(api, "", resource);
			}

			spec.addApis(api);
		}
	}

	/**
	 * Converts a WADL resource to the internal PATH and appends it to API
	 * <p>
	 * WADl resources are defined recursively, internal PATH is not
	 *
	 * @param api              API to which the converted PATh should be appended
	 * @param existingPathName previous url path in case of recursive resource
	 * @param resource         WADl resource which should be converted
	 */
	private void addPaths(Api.Builder api, String existingPathName, Resource resource) {
		Path.Builder path = Path.newBuilder();
		if (existingPathName.isEmpty()) {
			path.setPathName(resource.getPath());
		} else {
			path.setPathName(existingPathName + resource.getPath());
		}

		for (Object obj : resource.getMethodOrResource()) {
			if (obj instanceof Resource) {
				addPaths(api, path.getPathName(), (Resource) obj);
			} else if (obj instanceof Method) {
				Model.Method method = buildMethod(resource, (Method) obj);
				path.putMethods(method.getHttpMethod().name(), method);
			}
		}

		api.putPaths(path.getPathName(), path.build());
	}

	private Model.Method buildMethod(Resource resource, Method parsedMethod) {
		Model.Method.Builder method = Model.Method.newBuilder()
				.setHttpMethod(HttpMethod.valueOf(parsedMethod.getName().toUpperCase()));

		if (parsedMethod.getId() != null) {
			method.setOperationId(parsedMethod.getId() + "__" + increment());
		} else {
			method.setOperationId("__" + increment());
		}

		if (parsedMethod.getRequest() != null) {
			RequestBody.Builder requestBody = RequestBody.newBuilder();
			addMediaTypes(requestBody, parsedMethod.getRequest().getRepresentation());
			method.addRequestBodies(requestBody);

			addParameters(method, parsedMethod.getRequest().getParam());
		}

		for (Response response : parsedMethod.getResponse()) {
			method.addResponses(buildResponse(response));

			addParameters(method, response.getParam());
		}

		addParameters(method, resource.getParam());

		return method.build();
	}

	private void addParameters(MessageOrBuilder obj, List<Param> params) {
		for (Param param : params) {
			Parameter parameter = buildParameter(param);

			if (obj instanceof Model.Method.Builder) { // generic in case of later model adjustments
				((Model.Method.Builder) obj).putParameters(parameter.getKey(), parameter);
			}
		}
	}

	private Parameter buildParameter(Param param) {
		Parameter.Builder parameter = Parameter.newBuilder()
				.setRequired(param.isRequired())
				.setKey(param.getName());

		if (param.getStyle() != null) {
			parameter.setLocation(ParameterLocation.valueOf(param.getStyle().name()));
		}
		if (param.getType() != null) {
			parameter.setType(convertDataType(param.getType().getLocalPart()));
		}
		return parameter.build();
	}

	/**
	 * Append representation to either internal REQUEST-BODY or RESPONSE
	 *
	 * @param obj             Builder of internal model
	 * @param representations
	 */
	private void addMediaTypes(MessageOrBuilder obj, List<Representation> representations) {
		for (Representation repres : representations) {
			if (repres.getMediaType() == null) {
				failedParseBehaviour();
				continue;
			}
			if (obj instanceof RequestBody.Builder) {
				((RequestBody.Builder) obj).putContentMediaTypes(repres.getMediaType(), buildMediaType(repres));
			} else if (obj instanceof Model.Response.Builder) {
				((Model.Response.Builder) obj).putContentMediaTypes(repres.getMediaType(), buildMediaType(repres));
			}
		}
	}

	/**
	 * Convert WADL representation to internal CONTENT-MEDIA-TYPE
	 *
	 * @param representation
	 * @return
	 */
	private ContentMediaType buildMediaType(Representation representation) {
		ContentMediaType.Builder contentMediaType = ContentMediaType.newBuilder()
				.setMediaType(representation.getMediaType());

		DataModel.Builder dataModel = DataModel.newBuilder()
				.setDataModelRelationShip(DataModelRelationShip.UNDEFINED)
				.setDataType(DataType.OBJECT);

		for (Param param : representation.getParam()) {
			Property.Builder property = Property.newBuilder();
			if (param.getName() != null || param.getType() != null) {
				property.setKey(param.getName());
				property.setType(convertDataType(param.getType().getLocalPart()));
			} else {
				break;
			}
			dataModel.putProperties(property.getKey(), property.build());
		}
		try {
			dataModel.putProperties(representation.getElement().getLocalPart(),
					properties.get(representation.getElement().getLocalPart()));
		} catch (NullPointerException e) {
			failedParseBehaviour();
		}

		return contentMediaType.setDataModel(dataModel)
				.build();
	}

	/**
	 * Convert WADL response to model RESPONSE
	 *
	 * @param parsedResponse
	 * @return
	 */
	private Model.Response buildResponse(Response parsedResponse) {
		Model.Response.Builder response = Model.Response.newBuilder();

		for (long code : parsedResponse.getStatus()) {
			response.addCode(String.valueOf(code));
		}

		addMediaTypes(response, parsedResponse.getRepresentation());

		return response.build();
	}

	/**
	 * Parse WADL grammars based on defined model and append to properties map
	 *
	 * @param encodedProperties
	 * @throws JAXBException
	 */
	private void buildProperties(List<Object> encodedProperties) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(ComplexTypeType.class, ElementType.class, SchemaType.class,
				restful.api.metric.analyzer.cli.model.generated.wadl.properties.ObjectFactory.class, SequenceType.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();

		for (Object entry : encodedProperties) {
			try {
				JAXBElement<SchemaType> root = unmarshaller.unmarshal((ElementNSImpl) entry, SchemaType.class);
				SchemaType schemaType = root.getValue();

				HashMap<String, ComplexTypeType> complexTypes = new HashMap<>();

				for (ComplexTypeType complex : schemaType.getComplexType()) {
					complexTypes.put(complex.getName().getLocalPart(), complex);
				}

				for (ElementType element : schemaType.getElement()) {
					Property property = buildProperty(element, complexTypes);
					properties.put(property.getKey(), property);
				}
			} catch (JAXBException e) {
				failedParseBehaviour();
			}
		}
	}

	/**
	 * Convert parsed grammar to internal PROPERTY
	 *
	 * @param element
	 * @param complexTypes
	 * @return
	 */
	private Property buildProperty(ElementType element, HashMap<String, ComplexTypeType> complexTypes) {
		Property.Builder property = Property.newBuilder()
				.setKey(element.getName())
				.setType(convertDataType(element.getType().getLocalPart()))
				.setMinOccurs(element.getMinOccurs());

		if (property.getType() == DataType.OBJECT) {
			for (ElementType subElement : complexTypes.get(element.getType().getLocalPart()).getSequence().getElement()) {
				property.putSubProperties(element.getType().getLocalPart(), buildProperty(subElement, complexTypes));
			}
		} else {
			if (element.getMaxOccurs() != null) {
				try {
					property.setMaxOccurs(Integer.valueOf(element.getMaxOccurs()));
				} catch (NumberFormatException e) {
					property.setMaxOccurs(Integer.MAX_VALUE);
				}
			}
			if (element.isNillable() != null) {
				property.setNillable(element.isNillable());
			}
		}
		return property.build();
	}

	/**
	 * Converts XML types to internal typs
	 *
	 * @param dataType
	 * @return
	 */
	private DataType convertDataType(String dataType) {
		if (dataType == null) {
			return DataType.STRING;
		}
		dataType = dataType.replace("int", "integer");
		try {
			return DataType.valueOf(dataType.toUpperCase());
		} catch (IllegalArgumentException e) {
			return DataType.OBJECT;
		}
	}

	private void failedParseBehaviour() {
		logger.error("Skipped invalid formatted properties");
	}

	private int increment() {
		return counter++;
	}
}