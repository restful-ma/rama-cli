package restful.api.metric.analyzer.cli.parser_to_model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import restful.api.metric.analyzer.cli.model.generated.internal.Model;
import restful.api.metric.analyzer.cli.model.generated.internal.Model.DataType;
import restful.api.metric.analyzer.cli.parser.OpenApi3Parser;

public class OA3ToApiModelSchemaTest {
	static Model.SpecificationFile specFile;
	static String apiTestUrl = "http://petstore.swagger.io/api";

	@BeforeEach
	void parseFromLocalFile() throws ParseException {
		Path resourceDirectory = Paths.get("src", "test", "resources", "OA3Parser", "petstore-expanded2.yaml");
		OpenApi3Parser parser = new OpenApi3Parser();

		specFile = parser.loadLocalFile(resourceDirectory.toString());
	}

	@Test
	void getSchemaFromReqBody() {
		String appJson = "application/json";

		Model.Path pathPets = specFile.getApis(0).getPathsMap().get("/pets");
		Model.Method petsPostMethod = pathPets.getMethodsMap().get(Model.HttpMethod.POST.toString());

		assertEquals(1, petsPostMethod.getRequestBodiesCount());
		Model.RequestBody reqBody = petsPostMethod.getRequestBodies(0);
		Model.ContentMediaType mediaType = reqBody.getContentMediaTypesOrThrow(appJson);
		Model.DataModel dataModel = mediaType.getDataModel();

		// DataModel doesn't contain oneOf,anyOf,allOf,
		// therefore NewPet is directly in the dataModel of the top level
		assertNotNull(dataModel);
		assertEquals(Model.DataType.OBJECT, dataModel.getDataType());
		assertEquals(2, dataModel.getPropertiesCount());
		assertTrue(dataModel.containsProperties("tag"));
		assertTrue(dataModel.containsProperties("name"));
		assertEquals(Model.DataModelRelationShip.UNDEFINED, dataModel.getDataModelRelationShip());
		assertEquals(0, dataModel.getDataModelsCount());

	}

	@Test
	void getSchemaFromResponse() {
		String appJson = "application/json";

		Model.Path pathPets = specFile.getApis(0).getPathsMap().get("/pets");
		Model.Method petsGetMethod = pathPets.getMethodsMap().get(Model.HttpMethod.GET.toString());

		// Test response with code 200 from /pet GET method
		Model.Response petsGetMethodResponseCode200 = petsGetMethod.getResponsesList().stream().filter((resp) -> {
			return resp.getCodeList().contains("200");
		}).findFirst().get();
		Model.ContentMediaType mediaType200 = petsGetMethodResponseCode200.getContentMediaTypesOrThrow(appJson);
		Model.DataModel dataModel200 = mediaType200.getDataModel();
		// Test the DataModel of response with code 200 from /pet GET method
		// type, properties, dataRelationship, (sub)dataModels
		assertNotNull(dataModel200);
		assertEquals(Model.DataType.ARRAY, dataModel200.getDataType());
		assertEquals(1, dataModel200.getPropertiesCount());
		assertEquals(Model.DataModelRelationShip.UNDEFINED, dataModel200.getDataModelRelationShip());
		assertEquals(0, dataModel200.getDataModelsCount());

		// Test the DataModel of response with code default from /pet GET method
		Model.Response petsGetMethodOfRespCodeDefault = petsGetMethod.getResponsesList().stream().filter((resp) -> {
			return resp.getCodeList().contains("default");
		}).findFirst().get();
		Model.ContentMediaType mediaTypeDefault = petsGetMethodOfRespCodeDefault.getContentMediaTypesOrThrow(appJson);
		Model.DataModel dataModelDefault = mediaTypeDefault.getDataModel();
		// Test the DataModel of response with code default from /pet GET method
		assertNotNull(dataModelDefault);
		assertEquals(Model.DataType.OBJECT, dataModelDefault.getDataType());
		assertEquals(2, dataModelDefault.getPropertiesCount());
		assertTrue(dataModelDefault.containsProperties("code"));
		assertTrue(dataModelDefault.containsProperties("message"));
		assertEquals(Model.DataModelRelationShip.UNDEFINED, dataModelDefault.getDataModelRelationShip());
		assertEquals(0, dataModelDefault.getDataModelsCount());
	}

	/**
	 * Test complex schema of response from one.yaml file
	 */
	@Test
	void nestedSchemaResponse() throws ParseException {
		String appJson = "application/json";

		Path resourceDirectory = Paths.get("src", "test", "resources", "OA3Parser", "one.yaml");
		OpenApi3Parser parser = new OpenApi3Parser();

		Model.SpecificationFile spec = parser.loadLocalFile(resourceDirectory.toString());
		Model.Api api = spec.getApis(0);
		Model.Path path = api.getPathsMap().get("/pets");
		Model.Method method = path.getMethodsMap().get(Model.HttpMethod.POST.toString());

		// Test nested schema response with code 300 from /pets GET method
		Model.Response response300 = method.getResponsesList().stream().filter((resp) -> {
			return resp.getCodeList().contains("300");
		}).findFirst().get();
		Model.ContentMediaType mediatype300 = response300.getContentMediaTypesOrThrow(appJson);
		Model.DataModel dataModel300 = mediatype300.getDataModel();

		assertNotNull(dataModel300);
		assertEquals(Model.DataType.OBJECT, dataModel300.getDataType());
		// defined properties are inside allOf indentation level, therefore considered
		// as an extra SubDataModel/SubSchema
		assertEquals(2, dataModel300.getPropertiesCount());
		assertTrue(dataModel300.containsProperties("testProp1"));
		assertTrue(dataModel300.containsProperties("testProp2"));
		assertEquals(Model.DataModelRelationShip.All, dataModel300.getDataModelRelationShip());
		assertEquals(2, dataModel300.getDataModelsCount());
		assertTrue(dataModel300.containsDataModels("Cat"));
		assertTrue(dataModel300.containsDataModels("Dog"));

		// DataModel has 2 subDataModels Cat, Dog

		// Cat schema ###
		// # Test Cat subDataModel which describes the schema Cat
		Model.DataModel catModel = dataModel300.getDataModelsOrThrow("Cat");
		assertEquals(Model.DataType.OBJECT, catModel.getDataType());
		// defined properties are inside oneOf indentation level, therefore considered
		// as an extra SubDataModel/SubSchema
		assertEquals(0, catModel.getPropertiesCount());
		assertEquals(Model.DataModelRelationShip.ONLYONE, catModel.getDataModelRelationShip());
		assertEquals(2, catModel.getDataModelsCount());
		assertTrue(catModel.containsDataModels("Pet"));
		assertTrue(catModel.containsDataModels("{\"type\":\"object\",\"properties\":{\"hunts\":{\"type\":\"boolean\"},\"age\":{\"type\":\"integer\"}}}"));

		// ## Test Pet subDataModel which describes a subSchema of Cat
		Model.DataModel petModel = catModel.getDataModelsOrThrow("Pet");
		assertEquals(Model.DataType.OBJECT, petModel.getDataType());
		// defined properties are inside oneOf indentation level, therefore considered
		// as an extra SubDataModel/SubSchema
		assertEquals(0, petModel.getPropertiesCount());
		assertEquals(Model.DataModelRelationShip.All, petModel.getDataModelRelationShip());
		assertEquals(2, petModel.getDataModelsCount());
		assertTrue(petModel.containsDataModels("NewPet"));
		assertTrue(petModel.containsDataModels("{\"required\":[\"id\"],\"properties\":{\"id\":{\"type\":\"integer\",\"format\":\"int64\"}}}"));

		// ## Test undefinedName subDataModel which describes a subSchema of Cat
		// (the properties inside allOf)
		Model.DataModel undefModel = catModel.getDataModelsOrThrow("{\"type\":\"object\",\"properties\":{\"hunts\":{\"type\":\"boolean\"},\"age\":{\"type\":\"integer\"}}}");
		assertEquals(Model.DataType.OBJECT, undefModel.getDataType());
		assertEquals(2, undefModel.getPropertiesCount());
		assertTrue(undefModel.containsProperties("hunts"));
		assertTrue(undefModel.containsProperties("age"));
		assertEquals(Model.DataModelRelationShip.UNDEFINED, undefModel.getDataModelRelationShip());
		assertEquals(0, undefModel.getDataModelsCount());

		// ### Test NewPet subDataModel which describes a subSchema of Pet
		Model.DataModel newPetModel = petModel.getDataModelsOrThrow("NewPet");
		assertEquals(Model.DataType.OBJECT, newPetModel.getDataType());
		assertEquals(2, newPetModel.getPropertiesCount());
		assertTrue(newPetModel.containsProperties("name"));
		assertTrue(newPetModel.containsProperties("tag"));
		assertEquals(Model.DataModelRelationShip.UNDEFINED, newPetModel.getDataModelRelationShip());
		assertEquals(0, newPetModel.getDataModelsCount());

		// ### Test undefinedName subDataModel which describes a subSchema of Pet
		// (the properties inside allOf)
		Model.DataModel undef2Model = petModel.getDataModelsOrThrow("{\"required\":[\"id\"],\"properties\":{\"id\":{\"type\":\"integer\",\"format\":\"int64\"}}}");
		assertEquals(Model.DataType.OBJECT, undef2Model.getDataType());
		assertEquals(1, undef2Model.getPropertiesCount());
		assertTrue(undef2Model.containsProperties("id"));
		assertEquals(Model.DataModelRelationShip.UNDEFINED, undef2Model.getDataModelRelationShip());
		assertEquals(0, undef2Model.getDataModelsCount());

		// Dog schema ###
		// # Test Dog subDataModel which describes the schema Dog
		Model.DataModel dogModel = dataModel300.getDataModelsOrThrow("Dog");
		assertEquals(DataType.OBJECT, dogModel.getDataType());
		assertEquals(3, dogModel.getPropertiesCount());
		assertTrue(dogModel.containsProperties("bark"));
		assertTrue(dogModel.containsProperties("breed"));
		assertTrue(dogModel.containsProperties("food"));
		assertEquals(Model.DataModelRelationShip.All, dogModel.getDataModelRelationShip());
		assertEquals(1, dogModel.getDataModelsCount());
		assertTrue(dogModel.containsDataModels("Pet"));

		// ## Test Pet subDataModel which describes a subSchema of Dog
		Model.DataModel petDogModel = dogModel.getDataModelsOrThrow("Pet");
		assertEquals(Model.DataType.OBJECT, petDogModel.getDataType());
		// defined properties are inside oneOf indentation level, therefore considered
		// as an extra SubDataModel/SubSchema
		assertEquals(0, petDogModel.getPropertiesCount());
		assertEquals(Model.DataModelRelationShip.All, petDogModel.getDataModelRelationShip());
		assertEquals(2, petDogModel.getDataModelsCount());
		assertTrue(petDogModel.containsDataModels("NewPet"));
		assertTrue(petModel.containsDataModels("{\"required\":[\"id\"],\"properties\":{\"id\":{\"type\":\"integer\",\"format\":\"int64\"}}}"));

		// ### Test NewPet subDataModel which describes a subSchema of Pet
		Model.DataModel newPetDogModel = petDogModel.getDataModelsOrThrow("NewPet");
		assertEquals(Model.DataType.OBJECT, newPetDogModel.getDataType());
		assertEquals(2, newPetDogModel.getPropertiesCount());
		assertTrue(newPetDogModel.containsProperties("name"));
		assertTrue(newPetDogModel.containsProperties("tag"));
		assertEquals(Model.DataModelRelationShip.UNDEFINED, newPetDogModel.getDataModelRelationShip());
		assertEquals(0, newPetDogModel.getDataModelsCount());

	}

	/**
	 * Test complex schema of reqBody from one.yaml file
	 * SubSchemas of Dog and Cat are the same as in nestedSchemaResponse, therefore not tested here again
	 */
	@Test
	void nestedSchemaReqBody() throws ParseException {
		String appJson = "application/json";

		Path resourceDirectory = Paths.get("src", "test", "resources", "OA3Parser", "one.yaml");
		OpenApi3Parser parser = new OpenApi3Parser();

		Model.SpecificationFile spec = parser.loadLocalFile(resourceDirectory.toString());
		Model.Api api = spec.getApis(0);
		Model.Path path = api.getPathsMap().get("/pets");
		Model.Method method = path.getMethodsMap().get(Model.HttpMethod.POST.toString());

		assertEquals(1, method.getRequestBodiesCount());
		Model.RequestBody reqBody = method.getRequestBodies(0);
		Model.ContentMediaType mediaType = reqBody.getContentMediaTypesOrThrow(appJson);

		assertEquals(appJson, mediaType.getMediaType());
		Model.DataModel dataModel = mediaType.getDataModel();

		assertNotNull(dataModel);
		assertEquals(Model.DataType.OBJECT, dataModel.getDataType());
		assertEquals(0, dataModel.getPropertiesCount());
		assertEquals(Model.DataModelRelationShip.ONLYONE, dataModel.getDataModelRelationShip());
		assertEquals(2, dataModel.getDataModelsCount());

		// DataModel has 2 subDataModels Cat, Dog

		// # Test Cat subDataModel which describes the schema Cat
		Model.DataModel catModel = dataModel.getDataModelsOrThrow("Cat");
		assertEquals(Model.DataType.OBJECT, catModel.getDataType());
		// defined properties are inside oneOf indentation level, therefore considered
		// as an extra SubDataModel/SubSchema
		assertEquals(0, catModel.getPropertiesCount());
		assertEquals(Model.DataModelRelationShip.ONLYONE, catModel.getDataModelRelationShip());
		assertEquals(2, catModel.getDataModelsCount());
		assertTrue(catModel.containsDataModels("Pet"));
		assertTrue(catModel.containsDataModels("{\"type\":\"object\",\"properties\":{\"hunts\":{\"type\":\"boolean\"},\"age\":{\"type\":\"integer\"}}}"));

		// # Test Dog subDataModel which describes the schema Dog
		Model.DataModel dogModel = dataModel.getDataModelsOrThrow("Dog");
		assertEquals(DataType.OBJECT, dogModel.getDataType());
		assertEquals(3, dogModel.getPropertiesCount());
		assertTrue(dogModel.containsProperties("bark"));
		assertTrue(dogModel.containsProperties("breed"));
		assertTrue(dogModel.containsProperties("food"));
		assertEquals(Model.DataModelRelationShip.All, dogModel.getDataModelRelationShip());
		assertEquals(1, dogModel.getDataModelsCount());
		assertTrue(dogModel.containsDataModels("Pet"));
	}

	@Test
	void getNestedObject() throws ParseException {
		String appJson = "application/json";

		Path resourceDirectory = Paths.get("src", "test", "resources",  "OA3Parser", "petstore-expanded2-nestedObjects.yaml");
		OpenApi3Parser parser = new OpenApi3Parser();

		Model.SpecificationFile spec = parser.loadLocalFile(resourceDirectory.toString());
		Model.Api api = spec.getApis(0);
		Model.Path path = api.getPathsMap().get("/pets");
		Model.Method postMethod = path.getMethodsMap().get(Model.HttpMethod.POST.toString());
		Model.Response response200 = postMethod.getResponses(0);
		Model.ContentMediaType mediaType = response200.getContentMediaTypesOrThrow(appJson);

		Model.DataModel data = mediaType.getDataModel();
		//the pet Schema has an allOf with two DataModels inside "NewPet" and another DataModel with a not set name (therefore the complete schema string is used as name)
		Model.DataModel petData = data.getDataModelsOrThrow("{\"required\":[\"id\"],\"properties\":{\"id\":{\"type\":\"integer\",\"format\":\"int64\"},\"objExample\":{\"type\":\"object\",\"properties\":{\"objPropA\":{\"type\":\"string\"},\"objPropB\":{\"type\":\"integer\",\"format\":\"int32\"},\"objPropC\":{\"type\":\"object\",\"properties\":{\"nestedPropA\":{\"type\":\"string\"},\"nestedPropB\":{\"type\":\"object\",\"properties\":{\"nestedNestedPropA\":{\"type\":\"string\"}}}}}}}}}");
		assertNotNull(petData);

		assertEquals(2, petData.getPropertiesCount());
		assertTrue(petData.containsProperties("id"));
		assertTrue(petData.containsProperties("objExample"));
		assertEquals(Model.DataType.OBJECT, petData.getPropertiesOrThrow("objExample").getType());
		assertEquals(3, petData.getPropertiesOrThrow("objExample").getSubPropertiesCount());
		assertTrue(petData.getPropertiesOrThrow("objExample").containsSubProperties("objPropA"));
		assertTrue(petData.getPropertiesOrThrow("objExample").containsSubProperties("objPropB"));
		assertTrue(petData.getPropertiesOrThrow("objExample").containsSubProperties("objPropC"));
		//objPropB is a Object
		Model.Property objPropC = petData.getPropertiesOrThrow("objExample").getSubPropertiesOrThrow("objPropC");
		assertEquals(2, objPropC.getSubPropertiesCount());
		assertTrue(objPropC.containsSubProperties("nestedPropA"));
		assertEquals(Model.DataType.STRING, objPropC.getSubPropertiesOrThrow("nestedPropA").getType());
		assertTrue(objPropC.containsSubProperties("nestedPropB"));
		assertEquals(Model.DataType.OBJECT, objPropC.getSubPropertiesOrThrow("nestedPropB").getType());
		// get subProperties from nestedNestedProbA of Object nestedProbB
		Model.Property nestedPropB = objPropC.getSubPropertiesOrThrow("nestedPropB");
		assertEquals(1, nestedPropB.getSubPropertiesCount());
		assertTrue(nestedPropB.containsSubProperties("nestedNestedPropA"));
		assertEquals(Model.DataType.STRING, nestedPropB.getSubPropertiesOrThrow("nestedNestedPropA").getType());

	}
}