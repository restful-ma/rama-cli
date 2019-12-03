package restful.api.metric.analyzer.cli.parser;

import java.io.File;
import java.net.URI;
import java.text.ParseException;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.reprezen.kaizen.oasparser.model3.OpenApi3;
import com.reprezen.kaizen.oasparser.model3.Operation;
import com.reprezen.kaizen.oasparser.model3.Parameter;
import com.reprezen.kaizen.oasparser.model3.Schema;
import com.reprezen.kaizen.oasparser.model3.Server;

import restful.api.metric.analyzer.cli.model.generated.internal.Model;
import restful.api.metric.analyzer.cli.model.generated.internal.Model.Method.Builder;
import restful.api.metric.analyzer.cli.util.OA3EnumMapper;


public class OpenApi3Parser extends Parser {
	
	private static final Logger logger = LoggerFactory.getLogger(OpenApi3Parser.class);
	private int uniqueOperationIdCounter = 0; // is used when no oasOperationId was set
	private static final String NOT_DEFINED_STRING = "Not Defined";
	private static final String OBJECT_STRING = "object"; 
	private static final String ARRAY_STRING = "array";

	public OpenApi3Parser() {
		fileEnding = "yaml";
	}

	/**
	 * parses a OpenApi v3 file from a public URL
	 */
	@Override
	public Model.SpecificationFile loadPublicUrl(String url) throws ParseException {
		URI modelUri;
		try {
			modelUri = new URI(url);
			OpenApi3 model = new com.reprezen.kaizen.oasparser.OpenApi3Parser().parse(modelUri);
			return getTempOpenApiToInternalApi(model, url);
		} catch (Exception e) {
			logger.error("Failed to parse file from uri",e);
			throw new ParseException(e.getMessage(), 0);
		}
	}

	/**
	 * parses a OpenApi v3 file from a local file path
	 */
	@Override
	public Model.SpecificationFile loadLocalFile(String url) throws ParseException {
		File specFile = new File(url);
		try {
			OpenApi3 model = new com.reprezen.kaizen.oasparser.OpenApi3Parser().parse(specFile);
			return getTempOpenApiToInternalApi(model, url);
		} catch (Exception e) {
			logger.error("Failed to parse file",e);
			throw new ParseException(e.getMessage(), 0);
		}
	}

	/**
	 * Transforms the OpenAPI3 model from the "The KaiZen OpenAPI Parser API" into a
	 * API model which is defined in the .proto file
	 *
	 * @return SpecificationFile which contains the API model that was transformed
	 * from the OpenAPI3 model from the "The KaiZen OpenAPI Parser API"
	 */
	private Model.SpecificationFile getTempOpenApiToInternalApi(OpenApi3 oApi3model, String url) {
		Model.SpecificationFile.Builder internalSpecFile = Model.SpecificationFile.newBuilder();
		internalSpecFile.setFilePath(url);
		String oa3Version = oApi3model.getOpenApi() != null ? oApi3model.getOpenApi() : NOT_DEFINED_STRING;
		String title = (oApi3model.getInfo() != null && oApi3model.getInfo().getTitle() != null) ?
				oApi3model.getInfo().getTitle() : NOT_DEFINED_STRING;
		String apiVersion = (oApi3model.getInfo() != null && oApi3model.getInfo().getVersion() != null) ?
				oApi3model.getInfo().getVersion() : NOT_DEFINED_STRING;

		internalSpecFile.setSpecificationDescriptor(Model.SpecificationDescriptor.newBuilder()
				.setSpecificationFormat(Model.SpecificationFormat.OPENAPI)
				.setVersion(oa3Version))
				.setTitle(title)
				.setApiVersion(apiVersion);

		Model.Api.Builder internalApi = Model.Api.newBuilder();
		
		
		// Iterate through all API paths and transform them into the internal API model
		oApi3model.getPaths().forEach((oasPathName, oasPath) -> {
			Model.Path.Builder internalPath = Model.Path.newBuilder();

			// Iterate through all oasOperation (GET,POST,.. operations)
			// oasOperation might contain (query/path/cookie)parameters, a RequestBody or Response
			for (String oasMethodName : oasPath.getOperations().keySet()) {
				uniqueOperationIdCounter++;
				Operation oasOperation = oasPath.getOperations().get(oasMethodName);
				String oasOperationId = oasOperation.getOperationId() == null ? String.valueOf(uniqueOperationIdCounter) : oasOperation.getOperationId();
				Model.Method.Builder internalMethod = Model.Method.newBuilder();
				EnumValueDescriptor enumValDesc = Model.HttpMethod.getDescriptor()
						.findValueByName(oasMethodName.toUpperCase());
				internalMethod.setHttpMethod(Model.HttpMethod.valueOf(enumValDesc));
				internalMethod.setOperationId(oasOperationId);
				
				// Extract Parameter of Operation
				extractParameters(oasOperation.getParameters(), internalMethod,false);
				// Extract Common Parameters which are shared by all operations
				extractParameters(oasPath.getParameters(),internalMethod,true);
			

				// Extract RequesBody
				if (oasOperation.getRequestBody().hasContentMediaTypes()) {
					extractRequestBody(oasOperation, internalMethod);
				}
				// Extract Responses
				if (oasOperation.hasResponses()) {
					extractResponse(oasOperation, internalMethod);
				}
				internalPath.setPathName(oasPathName);
				internalPath.putMethods(enumValDesc.toString(), internalMethod.build());
			}

			internalApi.putPaths(oasPathName, internalPath.build());
		});
		// add all serverPaths to API
		for (Server server : oApi3model.getServers()) {
			internalApi.addBasePath(server.getUrl());
		}
		internalSpecFile.addApis(internalApi.build());

		return internalSpecFile.build();
	}

	private void extractParameters(List<Parameter> paramList, Builder internalMethod,boolean isCommonParam) {
		for (Parameter oasParam : paramList) {
			String key = oasParam.getName();
			String dataType = oasParam.getSchema().getType();
			String format = oasParam.getSchema().getFormat();
			String location = oasParam.getIn();
			Boolean isRequired = oasParam.isRequired();
			
//			operationParameter overwrites commonParameter
			if(isCommonParam && internalMethod.getParametersMap().containsKey(key)) {
				//skip 
			} else {	
				Model.Parameter.Builder internalParam = Model.Parameter.newBuilder();
				internalParam.setKey(key);
				internalParam.setType(OA3EnumMapper.getEnumForOA3DataType(dataType, format));
				internalParam.setLocation(OA3EnumMapper.getEnumForOA3Location(location));
				internalParam.setRequired(isRequired);
	
				internalMethod.putParameters(key, internalParam.build());
			}
		}
	}

	private void extractRequestBody(Operation oasOperation, Builder internalMethod) {
		Model.RequestBody.Builder internalReqBody = Model.RequestBody.newBuilder();

		// Iterate through all MediaTypes which are accepted by the RequestBody and
		// extract the mediaType/schema and save it into internalReqBody
		oasOperation.getRequestBody().getContentMediaTypes().forEach((mediaTypeKey, mediaTypeValue) -> {
			Model.ContentMediaType.Builder mediaType = Model.ContentMediaType.newBuilder();
			Model.DataModel.Builder dataModelTopLevel = Model.DataModel.newBuilder();
			Schema oasSchema = mediaTypeValue.getSchema();

			// Necessary to avoid parsing of circular referencing properties
			HashSet<String> alreadyParsedProperty = new HashSet<>();

			dataModelTopLevel.setDataType(OA3EnumMapper.getEnumForOA3DataType(oasSchema.getType(), oasSchema.getFormat()));

			// Extract JSON schema keywords and save into dataModelTopLevel
			// Extract keywords with indentation outside of oneOf,allOf,anyOf
			extractProperties(oasSchema, dataModelTopLevel, alreadyParsedProperty);
			extractCombinedProperties(oasSchema, dataModelTopLevel, alreadyParsedProperty);

			mediaType.setMediaType(mediaTypeKey);
			mediaType.setDataModel(dataModelTopLevel.build());

			internalReqBody.putContentMediaTypes(mediaTypeKey, mediaType.build());
		});
		internalMethod.addRequestBodies(internalReqBody);
	}

	private void extractResponse(Operation oasOperation, Builder internalMethod) {
		// Iterate through all responses with different response codes
		oasOperation.getResponses().forEach((code, oasResponse) -> {
			Model.Response.Builder internalResponse = Model.Response.newBuilder();

			// Extract all ContentMediaTypes
			oasResponse.getContentMediaTypes().forEach((mediaTypeKey, mediaTypeValue) -> {
				Model.ContentMediaType.Builder mediaType = Model.ContentMediaType.newBuilder();
				Model.DataModel.Builder dataModelTopLevel = Model.DataModel.newBuilder();
				Schema oasSchema = mediaTypeValue.getSchema();
				
				//Necessary to avoid parsing of circular referencing properties
				HashSet<String> alreadyParsedProperty = new HashSet<>(); 

				dataModelTopLevel.setDataType(OA3EnumMapper.getEnumForOA3DataType(oasSchema.getType(), oasSchema.getFormat()));

				// Extract JSON schema keywords and save into dataModelTopLevel
				// Extract keywords with indentation outside of oneOf,allOf,anyOf
				extractProperties(oasSchema, dataModelTopLevel, alreadyParsedProperty);
				extractCombinedProperties(oasSchema, dataModelTopLevel, alreadyParsedProperty);// Extract "combined" keyword (oneOf,allOf,anyOf)

				mediaType.setMediaType(mediaTypeKey);
				mediaType.setDataModel(dataModelTopLevel.build());

				internalResponse.putContentMediaTypes(mediaTypeKey, mediaType.build());
			});

			internalResponse.addCode(code);
			internalMethod.addResponses(internalResponse.build());
		});
	}

	/**
	 * extract all properties which are not in the scope of allOf,anyOf,onlyOne
	 * <p>
	 * also handles the extraction of subProperties of "complexTypes" e.g. Object,Array
	 * these complexTypes can contain allOF,anyOf,onlyOne which also have to be considered
	 * Different to subProperteis because topLevel properties are  saved into property instead of subPorperty
	 * everything which is inside an object,array is treated as subProperty
	 * <p>
	 * Can be recalled by SubProperties if they have allOf,anyOf,onlyOne therefore pay attention for circular property references !!
	 * When an Object has a subProperty with circular reference on the parentObject
	 * the subProperty will only consist of the name of the parentObject + dataType but NO further subProperties,
	 * they have to be looked up from the parentObject.
	 *
	 * @param oasSchema
	 * @param dataModel
	 * @param alreadyParsedProperty
	 */
	private void extractProperties(Schema oasSchema, Model.DataModel.Builder dataModel, HashSet<String> alreadyParsedProperty) {

		// Extract ARRAY on TOPLEVEL
		if (!oasSchema.getItemsSchema().toString().equals("")) {
			Schema itemSchema = oasSchema.getItemsSchema();
			Model.Property.Builder prop = Model.Property.newBuilder();
			String arrayKey = oasSchema.getItemsSchema().getName() != null ? oasSchema.getItemsSchema().getName() : oasSchema.getItemsSchema().toString();
			prop.setKey(arrayKey);
			prop.setType(Model.DataType.ARRAY);
			// Parse schema inside Array if it was not parsed for current operation before
			if (!alreadyParsedProperty.contains(itemSchema.toString())) {
				alreadyParsedProperty.add(itemSchema.toString());
				extractArray(prop, itemSchema, alreadyParsedProperty);
			}
			dataModel.putProperties(arrayKey, prop.build());
		}

		//Extract Object/SimpleType on TOPLEVEL
		for (String propertyKey : oasSchema.getProperties().keySet()) {
			String type = oasSchema.getProperty(propertyKey).getType();
			String format = oasSchema.getProperty(propertyKey).getFormat();

			Model.Property.Builder internalProperty = Model.Property.newBuilder();
			internalProperty.setKey(propertyKey);
			internalProperty.setType(OA3EnumMapper.getEnumForOA3DataType(type, format));

			// Extract subProperties of objects,arrays
			if (type != null && type.equalsIgnoreCase(OBJECT_STRING)) {
				Schema subPropertySchema = oasSchema.getProperty(propertyKey);
				if (!alreadyParsedProperty.contains(subPropertySchema.toString())) {
					alreadyParsedProperty.add(subPropertySchema.toString());
					extractSubPropertiesObject(internalProperty, subPropertySchema, alreadyParsedProperty);
				}
			} else if (type != null && type.equalsIgnoreCase(ARRAY_STRING)) {
				Schema arrayItemSchema = oasSchema.getItemsSchema();
				if (!alreadyParsedProperty.contains(arrayItemSchema.toString())) {
					alreadyParsedProperty.add(arrayItemSchema.toString());
					extractArray(internalProperty, arrayItemSchema, alreadyParsedProperty);
				}
			}
			// Different to extractSubproperty final result is stored into the TopLevelDataModel which is direct child of contentMedia 
			dataModel.putProperties(propertyKey, internalProperty.build());
		}
	}

	/**
	 * extract subProperties
	 * calling extractSubPropertiesObject on an recursive defined object which subProperties were already parsed once won't extract it again
	 * object doll
	 * - weight:String
	 * - doll:object //self-reference - calling extractSubPropertiesObject on this will do nothing
	 * 
	 * @param internalParentProperty
	 * @param subPropertySchema
	 * @param alreadyParsedProperty
	 */
	private void extractSubPropertiesObject(Model.Property.Builder internalParentProperty, Schema subPropertySchema, HashSet<String> alreadyParsedProperty) {

		for (String subPropertyKey : subPropertySchema.getProperties().keySet()) {
			Model.Property.Builder internalSubProperty = Model.Property.newBuilder();
			if (alreadyParsedProperty.contains(subPropertySchema.getProperty(subPropertyKey).toString())) {
				// don't extract subProperties of this again (this was already done once)
			} else {
				String subType = subPropertySchema.getProperty(subPropertyKey).getType();
				String subFormat = subPropertySchema.getProperty(subPropertyKey).getFormat();

				internalSubProperty.setKey(subPropertyKey);
				internalSubProperty.setType(OA3EnumMapper.getEnumForOA3DataType(subType, subFormat));

				if (subType != null && subType.equalsIgnoreCase(OBJECT_STRING)) {
					Schema subSubPropertySchema = subPropertySchema.getProperty(subPropertyKey);
					if (!alreadyParsedProperty.contains(subSubPropertySchema.toString())) {
						alreadyParsedProperty.add(subSubPropertySchema.toString());
						extractSubPropertiesObject(internalSubProperty, subSubPropertySchema, alreadyParsedProperty);
					}
				} else if (subType != null && subType.equalsIgnoreCase(ARRAY_STRING)) {
					Schema arrayItemSchema = subPropertySchema.getProperty(subPropertyKey).getItemsSchema();
					if (!alreadyParsedProperty.contains(arrayItemSchema.toString())) {
						alreadyParsedProperty.add(arrayItemSchema.toString());
						extractArray(internalSubProperty, arrayItemSchema, alreadyParsedProperty);
					}
				}
				// Different to extractProperty final result is stored into the ParentProperty
				internalParentProperty.putSubProperties(subPropertyKey, internalSubProperty.build());
			}

		}

		//ExtractCombined keyword allOf,anyOf,onlyOne which are a children of Object,Array
		Model.DataModel.Builder dataModelOfProp = Model.DataModel.newBuilder();
		extractCombinedProperties(subPropertySchema, dataModelOfProp, alreadyParsedProperty);
		// Different to extractProperty final result is stored inside the parentProperty
		internalParentProperty.setDataModel(dataModelOfProp.build());

	}

	/**
	 * extracts all items inside an array
	 * 
	 * @param internalProperty
	 * @param subPropertySchema
	 * @param alreadyParsedProperty
	 */
	private void extractArray(Model.Property.Builder internalProperty, Schema subPropertySchema, HashSet<String> alreadyParsedProperty) {
		//https://swagger.io/docs/specification/data-models/data-types/#array
		//Arrays can contain "simpleTypes",Objects or mixed-Types(multiple types)
		
		// ArrayType defined via $ref or also
		// allOf,anyOf,onlyOne are INSIDE the $ref 
		// different handling to Mixed-Type that uses onlyOne in another context
		if (subPropertySchema.getName() != null || !subPropertySchema.hasOneOfSchemas()) {
			
			//Case 1 Array contains directly another Array
			if (subPropertySchema.getType() != null && subPropertySchema.getType().equalsIgnoreCase(ARRAY_STRING)) {
				Schema itemSchema = subPropertySchema.getItemsSchema();
				String arrayKey = itemSchema.getName() != null ? itemSchema.getName() : itemSchema.toString();
				Model.Property.Builder internalSubProperty = Model.Property.newBuilder();
				internalSubProperty.setType(Model.DataType.ARRAY);
				// Parse schema inside Array of Array if it was not parsed for current operation before
				if (!alreadyParsedProperty.contains(itemSchema.toString())) {
					alreadyParsedProperty.add(itemSchema.toString());
					extractArray(internalSubProperty, itemSchema, alreadyParsedProperty);
				}
				internalProperty.putSubProperties(arrayKey, internalSubProperty.build());
			} 
			// Case 2 Array contains normal item (object)
			else {
				for (String subPropertyKey : subPropertySchema.getProperties().keySet()) {
					Model.Property.Builder internalSubProperty = Model.Property.newBuilder();
					String subType = subPropertySchema.getProperty(subPropertyKey).getType();
					String subFormat = subPropertySchema.getProperty(subPropertyKey).getFormat();
					internalSubProperty.setKey(subPropertyKey);
					internalSubProperty.setType(OA3EnumMapper.getEnumForOA3DataType(subType, subFormat));
					// Property of Object is another object -> extract it
					if (subType != null && subType.equalsIgnoreCase(OBJECT_STRING)) {
						Schema subSubPropertySchema = subPropertySchema.getProperty(subPropertyKey);
						if (!alreadyParsedProperty.contains(subSubPropertySchema.toString())) {
							alreadyParsedProperty.add(subSubPropertySchema.toString());
							extractSubPropertiesObject(internalSubProperty, subSubPropertySchema, alreadyParsedProperty);
						}
					} 
					// Property of Object is another array -> extract it
					else if (subType != null && subType.equalsIgnoreCase(ARRAY_STRING)) {
						Schema arrayItemSchema = subPropertySchema.getProperty(subPropertyKey).getItemsSchema();
						if (!alreadyParsedProperty.contains(arrayItemSchema.toString())) {
							alreadyParsedProperty.add(arrayItemSchema.toString());
							extractArray(internalSubProperty, arrayItemSchema, alreadyParsedProperty);
						}
					}

					internalProperty.putSubProperties(subPropertyKey, internalSubProperty.build());
				}
				
				//ExtractCombined keyword allOf,anyOf,onlyOne which are a children of Object,Array
				Model.DataModel.Builder dataModelOfProp = Model.DataModel.newBuilder();
				extractCombinedProperties(subPropertySchema, dataModelOfProp, alreadyParsedProperty);
				// Different to extractProperty final result is stored inside the parentProperty
				internalProperty.setDataModel(dataModelOfProp.build());
			}
		}

		//Mixed-Type(Array contains multiple types)
		//onlyOne is not inside a $ref
		else if (subPropertySchema.hasOneOfSchemas()) {
			//for each type that an array can contain add them as subProperties of the array
			for (Schema s : subPropertySchema.getOneOfSchemas()) {
				for (String subPropertyKey : s.getProperties().keySet()) {
					Model.Property.Builder internalSubProperty = Model.Property.newBuilder();
					String subType = s.getProperty(subPropertyKey).getType();
					String subFormat = s.getProperty(subPropertyKey).getFormat();
					internalSubProperty.setKey(subPropertyKey);
					internalSubProperty.setType(OA3EnumMapper.getEnumForOA3DataType(subType, subFormat));
					if (subType.equalsIgnoreCase(OBJECT_STRING)) {
						Schema subSubPropertySchema = s.getProperty(subPropertyKey);
						if (!alreadyParsedProperty.contains(subSubPropertySchema.toString())) {
							alreadyParsedProperty.add(subSubPropertySchema.toString());
							extractSubPropertiesObject(internalSubProperty, subSubPropertySchema, alreadyParsedProperty);
						}
					} else if (subType.equalsIgnoreCase(ARRAY_STRING)) {
						Schema arrayItemSchema = s.getProperty(subPropertyKey).getItemsSchema();
						if (!alreadyParsedProperty.contains(arrayItemSchema.toString())) {
							alreadyParsedProperty.add(arrayItemSchema.toString());
							extractArray(internalSubProperty, arrayItemSchema, alreadyParsedProperty);
						}
					}

					internalProperty.putSubProperties(subPropertyKey, internalSubProperty.build());
				}
			}
		}
	}

	private void extractCombinedProperties(Schema oasSchema, Model.DataModel.Builder dataModel, HashSet<String> alreadyParsedProperty) {
		if (oasSchema.hasAllOfSchemas()) {
			dataModel.setDataModelRelationShip(Model.DataModelRelationShip.All);
			extractPropFromSchemaList(dataModel, oasSchema.getAllOfSchemas(), alreadyParsedProperty);
		} else if (oasSchema.hasAnyOfSchemas()) {
			dataModel.setDataModelRelationShip(Model.DataModelRelationShip.ANY);
			extractPropFromSchemaList(dataModel, oasSchema.getAnyOfSchemas(), alreadyParsedProperty);
		} else if (oasSchema.hasOneOfSchemas()) {
			dataModel.setDataModelRelationShip(Model.DataModelRelationShip.ONLYONE);
			extractPropFromSchemaList(dataModel, oasSchema.getOneOfSchemas(), alreadyParsedProperty);
		} else {
			dataModel.setDataModelRelationShip(Model.DataModelRelationShip.UNDEFINED);
		}
	}


	/**
	 * @param topLeveDataModel
	 * @param oasSchemaList
	 * @param alreadyParsedProperty
	 * @param any
	 */
	private void extractPropFromSchemaList(Model.DataModel.Builder topLeveDataModel, List<Schema> oasSchemaList, HashSet<String> alreadyParsedProperty) {
		for (Schema oasSchema : oasSchemaList) {
			Model.DataModel.Builder dataModelNew = Model.DataModel.newBuilder();

			dataModelNew.setDataType(OA3EnumMapper.getEnumForOA3DataType(oasSchema.getType(), oasSchema.getFormat()));

			extractProperties(oasSchema, dataModelNew, alreadyParsedProperty);
			extractCombinedProperties(oasSchema, dataModelNew, alreadyParsedProperty);

			String dataModelName = oasSchema.getName() != null ? oasSchema.getName() : oasSchema.toString();
			topLeveDataModel.putDataModels(dataModelName, dataModelNew.build());

		}
	}
}