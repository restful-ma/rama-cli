package restful.api.metric.analyzer.cli.parser_to_model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import restful.api.metric.analyzer.cli.model.generated.internal.Model;
import restful.api.metric.analyzer.cli.parser.OpenApi3Parser;

/**
 * Test should ensure that the KaiZen OpenApi3 parser model is correctly
 * transformed into the internal model. The handling of parsing invalid(or
 * valid) openApi files is handled in another test class.
 */
class OA3ToApiModelGeneralTest {
	static Model.SpecificationFile specFile;
	static String apiTestUrl = "http://petstore.swagger.io/api";

	@BeforeEach
	void parseFromLocalFile() throws ParseException {
		Path resourceDirectory = Paths.get("src", "test", "resources", "OA3Parser", "petstore-expanded2.yaml");
		OpenApi3Parser parser = new OpenApi3Parser();

		specFile = parser.loadLocalFile(resourceDirectory.toString());
	}

	@Test
	void parseToModel() throws ParseException {
		Path resourceDirectory = Paths.get("src", "test", "resources", "OA3Parser", "petstore-expanded2.yaml");
		OpenApi3Parser parser = new OpenApi3Parser();
		assertDoesNotThrow(() -> parser.loadLocalFile(resourceDirectory.toString()));

		Model.SpecificationFile internalApiModel = parser.loadLocalFile(resourceDirectory.toString());
		assertNotEquals(null, internalApiModel);
	}

	@Test
	void getSpecificationFile() {
		assertEquals(Model.SpecificationFormat.OPENAPI, specFile.getSpecificationDescriptor().getSpecificationFormat());
		assertEquals(1, specFile.getApisCount());
	}

	@Test
	void getApi() {
		assertEquals(1, specFile.getApisCount());
		assertTrue(specFile.getApis(0).getBasePathList().contains("http://petstore.swagger.io/api"));

	}

	@Test
	void getPathsFromApi() {
		assertEquals(2, specFile.getApis(0).getPathsCount());
		assertTrue(specFile.getApis(0).getPathsMap().keySet().contains("/pets"));
		assertTrue(specFile.getApis(0).getPathsMap().keySet().contains("/pets/{id}"));
	}

	@Test
	void getPathNameFromPath() {
		Model.Path pathPets = specFile.getApis(0).getPathsMap().get("/pets");
		Model.Path pathPetsId = specFile.getApis(0).getPathsMap().get("/pets/{id}");

		assertEquals("/pets", pathPets.getPathName());
		assertEquals("/pets/{id}", pathPetsId.getPathName());
	}

	@Test
	void getMethodsOfPath() {
		Model.Path pathPets = specFile.getApis(0).getPathsMap().get("/pets");
		Model.Path pathPetsId = specFile.getApis(0).getPathsMap().get("/pets/{id}");

		assertEquals(2, pathPets.getMethodsCount());
		assertTrue(pathPets.getMethodsMap().containsKey(Model.HttpMethod.GET.toString()));
		assertTrue(pathPets.getMethodsMap().containsKey(Model.HttpMethod.POST.toString()));

		assertEquals(2, pathPetsId.getMethodsMap().size());
		assertTrue(pathPetsId.getMethodsMap().containsKey(Model.HttpMethod.GET.toString()));
		assertTrue(pathPetsId.getMethodsMap().containsKey(Model.HttpMethod.DELETE.toString()));
	}

	@Test
	void propertiesOfMethod() {
		Model.Path pathPets = specFile.getApis(0).getPathsMap().get("/pets");
		Model.Path pathPetsId = specFile.getApis(0).getPathsMap().get("/pets/{id}");
		Model.Method petsGetMethod = pathPets.getMethodsMap().get(Model.HttpMethod.GET.toString());
		Model.Method petsPostMethod = pathPets.getMethodsMap().get(Model.HttpMethod.POST.toString());
		Model.Method petsIdGetMethod = pathPetsId.getMethodsMap().get(Model.HttpMethod.GET.toString());
		Model.Method petsIdDeleteMethod = pathPetsId.getMethodsMap().get(Model.HttpMethod.DELETE.toString());

		assertEquals(Model.HttpMethod.GET, petsGetMethod.getHttpMethod());
		assertEquals("findPets", petsGetMethod.getOperationId());

		assertEquals(Model.HttpMethod.POST, petsPostMethod.getHttpMethod());
		assertEquals("addPet", petsPostMethod.getOperationId());

		assertEquals(Model.HttpMethod.GET, petsIdGetMethod.getHttpMethod());
		assertEquals("find pet by id", petsIdGetMethod.getOperationId());

		assertEquals(Model.HttpMethod.DELETE, petsIdDeleteMethod.getHttpMethod());
		assertEquals("deletePet", petsIdDeleteMethod.getOperationId());
	}

	@Test
	void getParameters() {
		Model.Path pathPets = specFile.getApis(0).getPathsMap().get("/pets");
		Model.Path pathPetsId = specFile.getApis(0).getPathsMap().get("/pets/{id}");
		Model.Method petsGetMethod = pathPets.getMethodsMap().get(Model.HttpMethod.GET.toString());
		Model.Method petsPostMethod = pathPets.getMethodsMap().get(Model.HttpMethod.POST.toString());
		Model.Method petsIdGetMethod = pathPetsId.getMethodsMap().get(Model.HttpMethod.GET.toString());
		Model.Method petsIdDeleteMethod = pathPetsId.getMethodsMap().get(Model.HttpMethod.DELETE.toString());

		// /pets GET method
		assertEquals(2, petsGetMethod.getParametersCount());
		assertTrue(petsGetMethod.containsParameters("tags"));
		assertEquals("tags", petsGetMethod.getParametersMap().get("tags").getKey());
		assertEquals(Model.DataType.ARRAY, petsGetMethod.getParametersMap().get("tags").getType());
		assertEquals(Model.ParameterLocation.QUERY, petsGetMethod.getParametersMap().get("tags").getLocation());
		assertFalse(petsGetMethod.getParametersMap().get("tags").getRequired());

		assertTrue(petsGetMethod.containsParameters("limit"));
		assertEquals("limit", petsGetMethod.getParametersMap().get("limit").getKey());
		assertEquals(Model.DataType.INTEGER, petsGetMethod.getParametersMap().get("limit").getType());
		assertEquals(Model.ParameterLocation.QUERY, petsGetMethod.getParametersMap().get("limit").getLocation());
		assertFalse(petsGetMethod.getParametersMap().get("limit").getRequired());

		// /pets POST method
		assertEquals(0, petsPostMethod.getParametersCount());

		// /pets/{id} GET method
		assertEquals(1, petsIdGetMethod.getParametersCount());
		assertTrue(petsIdGetMethod.containsParameters("id"));
		assertEquals("id", petsIdGetMethod.getParametersMap().get("id").getKey());
		assertEquals(Model.DataType.LONG, petsIdGetMethod.getParametersMap().get("id").getType());
		assertEquals(Model.ParameterLocation.PATH, petsIdGetMethod.getParametersMap().get("id").getLocation());
		assertTrue(petsIdGetMethod.getParametersMap().get("id").getRequired());

		// /pets/{id} DELETE method
		assertEquals(1, petsIdDeleteMethod.getParametersCount());
		assertTrue(petsIdDeleteMethod.containsParameters("id"));
		assertEquals("id", petsIdDeleteMethod.getParametersMap().get("id").getKey());
		assertEquals(Model.DataType.LONG, petsIdDeleteMethod.getParametersMap().get("id").getType());
		assertEquals(Model.ParameterLocation.PATH, petsIdDeleteMethod.getParametersMap().get("id").getLocation());
		assertTrue(petsIdDeleteMethod.getParametersMap().get("id").getRequired());

	}

	@Test
	void getRequestBodies() {
		Model.Path pathPets = specFile.getApis(0).getPathsMap().get("/pets");
		Model.Path pathPetsId = specFile.getApis(0).getPathsMap().get("/pets/{id}");
		Model.Method petsGetMethod = pathPets.getMethodsMap().get(Model.HttpMethod.GET.toString());
		Model.Method petsPostMethod = pathPets.getMethodsMap().get(Model.HttpMethod.POST.toString());
		Model.Method petsIdGetMethod = pathPetsId.getMethodsMap().get(Model.HttpMethod.GET.toString());
		Model.Method petsIdDeleteMethod = pathPetsId.getMethodsMap().get(Model.HttpMethod.DELETE.toString());

		assertEquals(0, petsGetMethod.getRequestBodiesList().size());
		assertEquals(0, petsIdGetMethod.getRequestBodiesList().size());
		assertEquals(0, petsIdDeleteMethod.getRequestBodiesList().size());

		assertEquals(1, petsPostMethod.getRequestBodiesList().size());
		assertEquals(1, petsPostMethod.getRequestBodiesList().get(0).getContentMediaTypesCount());
		assertTrue(
				petsPostMethod.getRequestBodiesList().get(0).getContentMediaTypesMap().containsKey("application/json"));
	}

	@Test
	void getResponses() {
		String appJson = "application/json";

		Model.Path pathPets = specFile.getApis(0).getPathsMap().get("/pets");
		Model.Path pathPetsId = specFile.getApis(0).getPathsMap().get("/pets/{id}");
		Model.Method petsGetMethod = pathPets.getMethodsMap().get(Model.HttpMethod.GET.toString());
		Model.Method petsPostMethod = pathPets.getMethodsMap().get(Model.HttpMethod.POST.toString());
		Model.Method petsIdGetMethod = pathPetsId.getMethodsMap().get(Model.HttpMethod.GET.toString());
		Model.Method petsIdDeleteMethod = pathPetsId.getMethodsMap().get(Model.HttpMethod.DELETE.toString());

		assertEquals(2, petsGetMethod.getResponsesCount());
		assertTrue(petsGetMethod.getResponsesList().stream().anyMatch((resp) -> {
			return resp.getCodeList().contains("200");
		}));
		assertTrue(petsGetMethod.getResponsesList().stream().anyMatch((resp) -> {
			return resp.getCodeList().contains("default");
		}));
		assertEquals(1, petsGetMethod.getResponsesList().stream().filter((resp) -> {
			return resp.getCodeList().contains("200");
		}).findFirst().get().getContentMediaTypesCount());
		assertEquals(1, petsGetMethod.getResponsesList().stream().filter((resp) -> {
			return resp.getCodeList().contains("default");
		}).findFirst().get().getContentMediaTypesCount());
		assertTrue(petsGetMethod.getResponsesList().stream().filter((resp) -> {
			return resp.getCodeList().contains("200");
		}).findFirst().get().getContentMediaTypesMap().containsKey(appJson));
		assertTrue(petsGetMethod.getResponsesList().stream().filter((resp) -> {
			return resp.getCodeList().contains("default");
		}).findFirst().get().getContentMediaTypesMap().containsKey(appJson));

		assertEquals(2, petsPostMethod.getResponsesCount());
		assertEquals(2, petsIdGetMethod.getResponsesCount());

		assertEquals(2, petsIdDeleteMethod.getResponsesCount());
		assertTrue(petsIdDeleteMethod.getResponsesList().stream().anyMatch((resp) -> {
			return resp.getCodeList().contains("204");
		}));
		assertTrue(petsIdDeleteMethod.getResponsesList().stream().anyMatch((resp) -> {
			return resp.getCodeList().contains("default");
		}));
	}

	@Test
	void getMediaTypeFromRequestBody() {
		String appJson = "application/json";

		Model.Path pathPets = specFile.getApis(0).getPathsMap().get("/pets");
		Model.Method petsPostMethod = pathPets.getMethodsMap().get(Model.HttpMethod.POST.toString());

		assertEquals(1, petsPostMethod.getRequestBodiesCount());
		Model.RequestBody reqBody = petsPostMethod.getRequestBodies(0);
		Model.ContentMediaType mediaType = reqBody.getContentMediaTypesOrThrow(appJson);

		assertEquals(appJson, mediaType.getMediaType());
		assertNotNull(mediaType.getDataModel());
	}

	@Test
	void getMediaTypeFromResponse() {
		final String appJson = "application/json";

		Model.Path pathPets = specFile.getApis(0).getPathsMap().get("/pets");
		Model.Method petsGetMethod = pathPets.getMethodsMap().get(Model.HttpMethod.GET.toString());

		// Test response with code 200 from /pet GET method
		Model.Response petsGetMethodResponseCode200 = petsGetMethod.getResponsesList().stream().filter((resp) -> {
			return resp.getCodeList().contains("200");
		}).findFirst().get();
		Model.ContentMediaType mediaType200 = petsGetMethodResponseCode200.getContentMediaTypesOrThrow(appJson);

		assertEquals(1, petsGetMethodResponseCode200.getContentMediaTypesCount());
		assertEquals(appJson, mediaType200.getMediaType());

		// Test response with code default from /pet GET method
		Model.Response petsGetMethodResponseCodeDefault = petsGetMethod.getResponsesList().stream().filter((resp) -> {
			return resp.getCodeList().contains("default");
		}).findFirst().get();
		Model.ContentMediaType mediaTypeDefault = petsGetMethodResponseCodeDefault.getContentMediaTypesOrThrow(appJson);

		assertEquals(1, petsGetMethodResponseCodeDefault.getContentMediaTypesCount());
		assertEquals(appJson, mediaTypeDefault.getMediaType());
	}
	
	@Test
	void getReusableReqBody() throws ParseException {
		Path resourceDirectory = Paths.get("src", "test", "resources","OA3Parser", "ReusableReqBody.yaml");
		OpenApi3Parser parser = new OpenApi3Parser();

		Model.SpecificationFile sF = parser.loadLocalFile(resourceDirectory.toString());
		Model.Api api = sF.getApis(0);
		int operationId = 0;

		for (Model.Path path : api.getPathsMap().values()) {

			for (Model.Method method : path.getMethodsMap().values()) {
				operationId++;
				assertEquals(1,method.getRequestBodiesCount());
				assertEquals(String.valueOf(operationId), method.getOperationId());
			}
		}
		
	}
	
	
}