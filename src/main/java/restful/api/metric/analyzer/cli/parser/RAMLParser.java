package restful.api.metric.analyzer.cli.parser;

import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.common.ValidationResult;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.api.Library;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import restful.api.metric.analyzer.cli.model.generated.internal.Model;

public class RAMLParser extends Parser {

	private static final Logger logger = LoggerFactory.getLogger(RAMLParser.class);
	//HashMap for the DataModel's.
	//The key is the name of the type.
	private HashMap<String, Model.DataModel> dataModelHashMap;

	//Incrementing int value to distinguish operations.
	private int operationid=0;

	/**
	 * Define raml as the file ending for files parsed by this parser
	 */
	public RAMLParser() {
		fileEnding = "raml";
	}

	/**
	 * Start parsing the given RAML file.
	 * @param url
	 * @return the specification in the internal model format
	 */
	@Override
	public Model.SpecificationFile loadPublicUrl(String url) throws ParseException {
		return loadLocalFile(url);
	}
	/**
	 * Parse the specification with raml-java-parser
	 * Check the given file for errors.
	 * @param url
	 * @return the specification in the internal model format
	 */
	@Override
	public Model.SpecificationFile loadLocalFile(String url) throws ParseException {
		dataModelHashMap = new HashMap<>();
		RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(url);
		//Check if java-raml-parser has errors and put them out to console.
		if (ramlModelResult.hasErrors()) {
			for (ValidationResult validationResult : ramlModelResult.getValidationResults()) {
				logger.info(validationResult.getMessage());
			}
			throw new ParseException("Can't be processed", 0);
		} else {
			Api api = ramlModelResult.getApiV10();

			buildDataModelHashMap(api.uses());

			//In case there is no base path
			String basePath = "Empty";
			if (api.baseUri() != null) {
				basePath = api.baseUri().value();
			}

			Model.SpecificationFile.Builder specFileBuilder = Model.SpecificationFile.newBuilder();
			specFileBuilder
					.setSpecificationDescriptor(Model.SpecificationDescriptor.newBuilder()
					.setSpecificationFormat(Model.SpecificationFormat.RAML).setVersion(api.ramlVersion()));
			if(api.title() != null) {
				specFileBuilder.setTitle(api.title().toString());
			}else {
				specFileBuilder.setTitle("");
			}
			if(api.version() != null) {
				specFileBuilder.setApiVersion(api.version().toString());
			}else {
				specFileBuilder.setApiVersion("1.0");
			}
					
			specFileBuilder.setFilePath(url);
			specFileBuilder.addApis(transform(api, basePath));
			return specFileBuilder.build();
		}
	}

	// Limitations for the Parser:
	// types have to be sorted in order of usage with the used types first and the
	// using after
	// type names have to be unique even accros different external libs
	/**
	 * Parse the local types and schemas into the DataModel HashMap
	 * Parse java-raml-parser ressource objects into internal model Path objects
	 * Fill the internal model API object with internal model Path objects
	 * @param api
	 * @param basePath
	 * @return the specification in the internal model format
	 * 
	 */
	private Model.Api transform(Api api, String basePath) {

		Model.Api.Builder apiBuilder = Model.Api.newBuilder();
		apiBuilder.addBasePath(basePath);
		
		//Parse schemas that are defined inside the api file.
		for (TypeDeclaration schema : api.schemas()) {
			parseTypeAndSchema(schema);
		}

		//Parse types hat are defined inside the api file
		for (TypeDeclaration type : api.types()) {
			parseTypeAndSchema(type);
		}

		List<Model.Path> pathList = new ArrayList<>();

		for (Resource resource : api.resources()) {
			pathList.addAll(getPath(resource));
		}

		for (Model.Path path : pathList) {
			apiBuilder.putPaths(path.getPathName(), path);
		}
		return apiBuilder.build(); // Return the Api here
	}

	
	/**
	 * Fill an internal model Path object
	 * @param resource: The individual endpoints
	 * @return interal model Path object
	 */
	private List<Model.Path> getPath(Resource resource) {
		List<Model.Path> pathList = new ArrayList<>();
		Model.Path.Builder pathBuilder = Model.Path.newBuilder();
		pathBuilder.setPathName(resource.resourcePath());
		
		//Fill interal model Method object
		for (Method method : resource.methods()) {
			Model.Method.Builder methodBuilder = Model.Method.newBuilder();
			methodBuilder.setOperationId(String.valueOf(operationid));
			operationid++;
			if (method.method().equals("patch")) {
				methodBuilder.setHttpMethod(Model.HttpMethod.PUT);
			} else {
				methodBuilder.setHttpMethod(Model.HttpMethod.valueOf(method.method().toUpperCase()));
			}

			Model.RequestBody.Builder requestBodyBuilder = Model.RequestBody.newBuilder();
			
			//Fill interal model RequestBody object
			for (TypeDeclaration body : method.body()) {

				Model.ContentMediaType.Builder contentMediaTypeBuilder = Model.ContentMediaType.newBuilder();

				
				contentMediaTypeBuilder.setMediaType(body.name());
				String dataModelName = body.type().replace("[", "").replace("]", "");
				if (!dataModelName.equals("any")) {
					try {
						contentMediaTypeBuilder.setDataModel(dataModelHashMap.get(dataModelName));
					} catch (Exception e) {
						logger.error("Missing data Model: " + dataModelName);
						logger.error("Parser Type value:" + body.type());
						logger.error("location" + pathBuilder.getPathName());
						logger.error(e.getMessage());
					} 
				}
				requestBodyBuilder.putContentMediaTypes(contentMediaTypeBuilder.getMediaType(),contentMediaTypeBuilder.build());
				contentMediaTypeBuilder.clear();
				methodBuilder.addRequestBodies(requestBodyBuilder.build());
			}
			
			//Fill interal model Parameter object with query Parameter data
			for (TypeDeclaration parameter : method.queryParameters()) {
				methodBuilder.putParameters(parameter.name(), getParameter(parameter));
			}

			//Fill interal model Parameter object with URI parameter data
			for (TypeDeclaration parameter : resource.uriParameters()) {
				methodBuilder.putParameters(parameter.name(), getParameter(parameter));
			}

			Model.Response.Builder responseBuilder = Model.Response.newBuilder();

			//Fill interal model Response object
			for (Response response : method.responses()) {

				responseBuilder.addCode(response.code().value());
				if (!response.body().isEmpty()) {
					Model.ContentMediaType.Builder contentMediaTypeBuilder = Model.ContentMediaType.newBuilder();
					for (TypeDeclaration contentMediaType : response.body()) {
						contentMediaTypeBuilder.setMediaType(contentMediaType.name());
						String dataModelName = contentMediaType.type();
						if (!dataModelName.equals("any")) {
							if (dataModelName.contains("|")) {
								dataModelName = dataModelName.split(" | ")[0];
							}
							try {
								if (!dataModelName.contains("[")) {
									contentMediaTypeBuilder.setDataModel(dataModelHashMap.get(dataModelName));
								}else {
									dataModelName = dataModelName.replace("[", "").replace("]", "");
									contentMediaTypeBuilder.setDataModel(dataModelHashMap.get(dataModelName).toBuilder().setDataType(Model.DataType.ARRAY).build());	
								}
							} catch (Exception e) {
								logger.error("Missing data Model: " + dataModelName);
								logger.error(pathBuilder.getPathName());
								logger.error(response.code().value());
								logger.error(e.getMessage());
							} 
						}
						responseBuilder.putContentMediaTypes(contentMediaType.name(), contentMediaTypeBuilder.build());
						contentMediaTypeBuilder.clear();
					}
				}

				methodBuilder.addResponses(responseBuilder.build());
				responseBuilder.clear();

			}
			pathBuilder.putMethods(methodBuilder.getHttpMethod().name(), methodBuilder.build());
		}

		pathList.add(pathBuilder.build());

		if (!resource.resources().isEmpty()) {
			for (Resource subResource : resource.resources()) {
				pathList.addAll(getPath(subResource));
			}
		}

		return pathList;
	}

	/**
	 * Build the DataModel HashMap with the RAML libraries
	 * @param usedList: List of used Library objects
	 */
	private void buildDataModelHashMap(List<Library> usedList) {

		for (Library lib : usedList) {

			if (!lib.uses().isEmpty()) {
				buildDataModelHashMap(lib.uses());
			}
			
			for (TypeDeclaration type : lib.types()) {
				parseTypeAndSchema(type);
			}

			for (TypeDeclaration schema : lib.schemas()) {
				parseTypeAndSchema(schema);
			}

		}

	}

	/**
	 * Extract individual properties from a Node object of the Document tree structure
	 * @param propertyElementNode: XML Node containing the required information for a property
	 * @param pathRoot
	 * @return internal model Property object
	 */
	private Model.Property getProperty(Node propertyElementNode, String pathRoot) {

		Model.Property.Builder propertyBuilder = Model.Property.newBuilder();
		propertyBuilder.setKey(propertyElementNode.getAttributes().getNamedItem("name").getNodeValue()); // key
		if (propertyElementNode.getAttributes().getNamedItem("minOccurs") != null) {
			propertyBuilder.setMinOccurs(
					Integer.valueOf(propertyElementNode.getAttributes().getNamedItem("minOccurs").getNodeValue()));
		}
		if (propertyElementNode.getAttributes().getNamedItem("maxOccurs") != null) {
			String string = propertyElementNode.getAttributes().getNamedItem("maxOccurs").getNodeValue();
			if (string.equals("unbounded")) {
				propertyBuilder.setType(Model.DataType.ARRAY);
			} else {
				propertyBuilder.setMaxOccurs(Integer.valueOf(string));
			}
		}
		if (propertyElementNode.hasChildNodes()) {
			Node complexityTypeNode = propertyElementNode.getFirstChild().getNextSibling();
			if (complexityTypeNode.getNodeName().equals("simpleType")) { // Simple Property
				propertyBuilder.setType(Model.DataType.valueOf(complexityTypeNode.getFirstChild().getNextSibling()
						.getAttributes().getNamedItem("base").getNodeValue().toUpperCase())); // type
			} else { // ComplexProperty
				propertyBuilder.setType(Model.DataType.OBJECT);
				Node subPropertiesNode = complexityTypeNode.getFirstChild().getNextSibling().getFirstChild();
				while (subPropertiesNode.getNextSibling() != null) {
					if (subPropertiesNode.getNodeName().equals("element")) {
						propertyBuilder.putSubProperties(subPropertiesNode.getNodeName(),
								getProperty(subPropertiesNode, pathRoot));
					}
					subPropertiesNode = subPropertiesNode.getNextSibling();
				}
			}
		} else { // referenz Model
			if (containsDataType(
					propertyElementNode.getAttributes().getNamedItem("type").getNodeValue().toUpperCase())) {
				propertyBuilder.setType(Model.DataType.valueOf(
						propertyElementNode.getAttributes().getNamedItem("type").getNodeValue().toUpperCase()));
			} else {
				String type = propertyElementNode.getAttributes().getNamedItem("type").getNodeValue();
				if (propertyElementNode.getAttributes().getNamedItem("maxOccurs") != null) {
					propertyBuilder.setType(Model.DataType.ARRAY);
				} else {
					propertyBuilder.setType(Model.DataType.OBJECT);
				}
				if (!type.equals("date")&& !type.equals("dateTime") && !type.equals("time") && !type.equals("base64Binary")) {
					String stringArray[] = type.split(":");
					try {
						Model.DataModel referenzedModel = dataModelHashMap.get(stringArray[stringArray.length - 1]);
						propertyBuilder.putAllSubProperties(referenzedModel.getPropertiesMap()); // add Properties
					} catch (Exception e) {
						logger.error("Failed to get Properties for" + stringArray[stringArray.length - 1]);
						logger.error(e.getMessage());
					}
				}
			}
		}
		return propertyBuilder.build();
	}

	/**
	 * Test if String is DataType.
	 * @param test the String to be tested.
	 * @return true if String is part of Model.DataType.values().
	 */
	private static boolean containsDataType(String test) {

		for (Model.DataType c : Model.DataType.values()) {
			if (c.name().equals(test)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param type
	 * Parse a RAML type or schema into a internal model DataModel object and put it into the dataModelHashMap
	 */
	private void parseTypeAndSchema(TypeDeclaration type) {
		Model.DataModel.Builder builder = Model.DataModel.newBuilder();
		Document document;
		if (type.toXmlSchema() != null) {
			document = convertStringToXMLDocument(type.toXmlSchema());
		} else {
			return;
		}
		if(document == null || document.getDocumentElement() == null) {
			return;
		}
		Element rootElement;
		rootElement = document.getDocumentElement();

		
		
		Node dataModelNode = rootElement.getFirstChild().getNextSibling();
		// If there is a complex Type
		if (dataModelNode.getNextSibling().getNextSibling() != null) { 
			dataModelNode = dataModelNode.getNextSibling().getNextSibling();
			Node propertyNode = dataModelNode.getFirstChild().getNextSibling().getFirstChild();
			builder.setDataType(Model.DataType.OBJECT);
			while (propertyNode.getNextSibling() != null) {
				if (propertyNode.getNodeName().equals("element")) {
					builder.putProperties(propertyNode.getAttributes().getNamedItem("name").getNodeValue(),
							getProperty(propertyNode, ""));
				}
				propertyNode = propertyNode.getNextSibling();
			}
		//If there is no complex Type
		} else if(dataModelNode.getNodeName().equals("element")) {
			builder.putProperties(dataModelNode.getAttributes().
					getNamedItem("name").getNodeValue(), getProperty(dataModelNode,""));
		} else { 

			Node propertyNode = dataModelNode;
			Model.Property.Builder propertyBuilder = Model.Property.newBuilder();
			propertyBuilder.setKey(propertyNode.getAttributes().getNamedItem("name").getNodeValue());
			if (containsDataType(
					propertyNode.getAttributes().getNamedItem("type").getNodeValue().toUpperCase())) {
				propertyBuilder.setType(Model.DataType.valueOf(
						propertyNode.getAttributes().getNamedItem("type").getNodeValue().toUpperCase()));
			} else {
				String propertyType = propertyNode.getAttributes().getNamedItem("type").getNodeValue();
				if (propertyNode.getAttributes().getNamedItem("maxOccurs") != null) {
					propertyBuilder.setType(Model.DataType.ARRAY);
				} else {
					propertyBuilder.setType(Model.DataType.OBJECT);
				}
				if (!propertyType.equals("date")) {
					String stringArray[] = propertyType.split(":");
					try {
						Model.DataModel referenzedModel = dataModelHashMap
								.get(stringArray[stringArray.length - 1]);
						// add sub Property objects to a Property object
						propertyBuilder.putAllSubProperties(referenzedModel.getPropertiesMap()); 
					} catch (Exception e) {
						logger.error(stringArray[stringArray.length - 1]);
						logger.error(e.getMessage());
					}
				}
			}
			builder.putProperties(propertyNode.getAttributes().getNamedItem("name").getNodeValue(),
					propertyBuilder.build());
		}
		dataModelHashMap.put(type.name(), builder.build());
	}

	/**
	 * Converts the XMLSchema version of objects into a navigatable Document tree structure.
	 * Used mostly for parsing TypeDeclaration objects into internal model DataModel and Property objects.
	 * @param xmlString: The XMLSchema to be converted to a Document tree structure.
	 * @return a Document tree structure.
	 */
	private static Document convertStringToXMLDocument(String xmlString) {
		// Parser that produces DOM object trees from XML content
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		// API to obtain DOM Document instance
		DocumentBuilder builder = null;
		try {
			// Create DocumentBuilder with default configuration
			builder = factory.newDocumentBuilder();

			// Parse the content to Document object
			Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
			return doc;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return null;
	}
	
	/**
	 * Extract internal model Paramter object from the java-raml-parser paramter object
	 * @param parameter java-raml-parser paramter object.
	 * @return internal model Parameter object.
	 */
	private Model.Parameter getParameter(TypeDeclaration parameter){
		Model.Parameter.Builder parameterBuilder = Model.Parameter.newBuilder();
		parameterBuilder.setRequired(parameter.required());
		parameterBuilder.setLocation(Model.ParameterLocation.QUERY);
		parameterBuilder.setKey(parameter.name());

		if (parameter.type() != null) {
			if (containsDataType(parameter.type().toUpperCase().toUpperCase())) {
				parameterBuilder.setType(Model.DataType.valueOf(parameter.type().toUpperCase()));
			} else {
				if (parameter.type().toUpperCase().equals("NUMBER")) {
					parameterBuilder.setType(Model.DataType.INTEGER);
				}
			}
			try {
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
		}
		return parameterBuilder.build();
	}
}