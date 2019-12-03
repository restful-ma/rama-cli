package restful.api.metric.analyzer.cli.parser_to_model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import restful.api.metric.analyzer.cli.model.generated.internal.Model;
import restful.api.metric.analyzer.cli.model.generated.internal.Model.Api;
import restful.api.metric.analyzer.cli.model.generated.internal.Model.ContentMediaType;
import restful.api.metric.analyzer.cli.model.generated.internal.Model.DataModel;
import restful.api.metric.analyzer.cli.model.generated.internal.Model.DataModelRelationShip;
import restful.api.metric.analyzer.cli.model.generated.internal.Model.DataType;
import restful.api.metric.analyzer.cli.model.generated.internal.Model.HttpMethod;
import restful.api.metric.analyzer.cli.model.generated.internal.Model.Method;
import restful.api.metric.analyzer.cli.model.generated.internal.Model.Parameter;
import restful.api.metric.analyzer.cli.model.generated.internal.Model.ParameterLocation;
import restful.api.metric.analyzer.cli.model.generated.internal.Model.Response;
import restful.api.metric.analyzer.cli.model.generated.internal.Model.SpecificationDescriptor;
import restful.api.metric.analyzer.cli.model.generated.internal.Model.SpecificationFile;
import restful.api.metric.analyzer.cli.model.generated.internal.Model.SpecificationFormat;
import restful.api.metric.analyzer.cli.parser.WadlParser;

class WadlToApiModel1Test {

	private static SpecificationFile expectedModel;
	private static String basePath = "http://api.search.yahoo.com/NewsSearchService/V1/";
	private static String pathName = "newsSearch";
	private static String methodId = "search" + "__0";
	private static HttpMethod restType = HttpMethod.GET;
	private static String mediaType = "application/xml";
	private static String responseCode1 = "200";
	private static String responseCode2 = "400";

	@BeforeAll
	static void getFirstExampleApi() {
		Method.Builder method = Method.newBuilder()
				.setHttpMethod(restType)
				.setOperationId(methodId);

		method.putParameters("appid", Parameter.newBuilder()
				.setKey("appid")
				.setType(DataType.STRING)
				.setLocation(ParameterLocation.QUERY)
				.setRequired(true)
				.build());
		method.putParameters("query", Parameter.newBuilder()
				.setKey("query")
				.setType(DataType.STRING)
				.setLocation(ParameterLocation.QUERY)
				.setRequired(true)
				.build());
		method.putParameters("type", Parameter.newBuilder()
				.setKey("type")
				.setLocation(ParameterLocation.QUERY)
				.build());
		method.putParameters("results", Parameter.newBuilder()
				.setKey("results")
				.setType(DataType.INTEGER)
				.setLocation(ParameterLocation.QUERY)
				.build());
		method.putParameters("start", Parameter.newBuilder()
				.setKey("start")
				.setType(DataType.INTEGER)
				.setLocation(ParameterLocation.QUERY)
				.build());
		method.putParameters("sort", Parameter.newBuilder()
				.setKey("sort")
				.setLocation(ParameterLocation.QUERY)
				.build());
		method.putParameters("language", Parameter.newBuilder()
				.setKey("language")
				.setType(DataType.STRING)
				.setLocation(ParameterLocation.QUERY)
				.build());
		method.addResponses(Response.newBuilder()
				.addCode(responseCode1)
				.putContentMediaTypes(mediaType, ContentMediaType.newBuilder()
						.setMediaType(mediaType)
						.setDataModel(DataModel.newBuilder()
								.setDataType(DataType.OBJECT)
								.setDataModelRelationShip(DataModelRelationShip.UNDEFINED))
						.build())
				.build());
		method.addResponses(Response.newBuilder()
				.addCode(responseCode2)
				.putContentMediaTypes(mediaType, ContentMediaType.newBuilder()
						.setMediaType(mediaType)
						.setDataModel(DataModel.newBuilder()
								.setDataType(DataType.OBJECT)
								.setDataModelRelationShip(DataModelRelationShip.UNDEFINED))
						.build())
				.build());

		Model.Path path = Model.Path.newBuilder()
				.setPathName(pathName)
				.putMethods(method.getHttpMethod().toString(), method.build())
				.build();

		Api api = Api.newBuilder()
				.addBasePath(basePath)
				.putPaths(path.getPathName(), path)
				.build();

		expectedModel = SpecificationFile.newBuilder()
				.setSpecificationDescriptor(SpecificationDescriptor.newBuilder().setSpecificationFormat(SpecificationFormat.WADL))
				.addApis(api)
				.build();
	}


	private SpecificationFile parseFile() throws ParseException {
		Path resourceDirectory = Paths.get("src", "test", "resources","WADLParser", "wadl_example_1.xml");
		return new WadlParser().loadLocalFile(resourceDirectory.toString());
	}

	@Test
	void parseErrorFree() throws ParseException {
		SpecificationFile model = parseFile();

		assertNotEquals(null, model);
		assertNotEquals(null, model.getFilePath());
		assertEquals(SpecificationFormat.WADL, model.getSpecificationDescriptor().getSpecificationFormat());
	}

	@Test
	void getBasePath() throws ParseException {
		SpecificationFile model = parseFile();

		assertEquals(expectedModel.getApisCount(), model.getApisCount());
		assertEquals(expectedModel.getApis(0).getBasePathCount(), model.getApis(0).getBasePathCount());
		assertEquals(expectedModel.getApisCount(), model.getApisCount());
		assertEquals(basePath, model.getApis(0).getBasePath(0));
	}

	@Test
	void getPaths() throws ParseException {
		SpecificationFile model = parseFile();

		assertEquals(expectedModel.getApis(0).getPathsCount(), model.getApis(0).getPathsCount());
		assertTrue(model.getApis(0).containsPaths(pathName));
	}

	@Test
	void getMethods() throws ParseException {
		SpecificationFile model = parseFile();

		assertEquals(expectedModel.getApis(0).getPathsMap().get(pathName).getMethodsCount(),
				model.getApis(0).getPathsMap().get(pathName).getMethodsCount());
		assertTrue(model.getApis(0).getPathsMap().get(pathName).containsMethods(HttpMethod.GET.toString()));
		assertEquals(methodId, model.getApis(0).getPathsMap().get(pathName).getMethodsMap()
				.get(HttpMethod.GET.toString()).getOperationId());
	}

	@Test
	void getParameters() throws ParseException {
		SpecificationFile model = parseFile();
		Map<String, Parameter> expectedParameters = expectedModel.getApis(0).getPathsMap().get(pathName)
				.getMethodsMap().get(restType.toString()).getParametersMap();
		Map<String, Parameter> parsedParameters = model.getApis(0).getPathsMap().get(pathName)
				.getMethodsMap().get(restType.toString()).getParametersMap();

		assertEquals(expectedParameters.size(), parsedParameters.size());
		for (Parameter parameter : expectedParameters.values()) {
			assertTrue(parsedParameters.containsKey(parameter.getKey()));
			assertEquals(parameter.getLocation(), parsedParameters.get(parameter.getKey()).getLocation());
			assertEquals(parameter.getType(), parsedParameters.get(parameter.getKey()).getType());
			assertEquals(parameter.getRequired(), parsedParameters.get(parameter.getKey()).getRequired());
		}
	}

	@Test
	void getResponses() throws ParseException {
		SpecificationFile model = parseFile();
		List<Response> expectedResponses = expectedModel.getApis(0).getPathsMap().get(pathName)
				.getMethodsMap().get(restType.toString()).getResponsesList();
		List<Response> parsedResponses = model.getApis(0).getPathsMap().get(pathName)
				.getMethodsMap().get(restType.toString()).getResponsesList();

		assertEquals(expectedResponses.size(), parsedResponses.size());
		for (Response expectedResponse : expectedResponses) {
			boolean correctResponses = false;

			for (Response parsedResponse : parsedResponses) {
				for (String expectedCode : expectedResponse.getCodeList()) {
					for (String parsedCode : parsedResponse.getCodeList()) {
						if (expectedCode.equals(parsedCode)) {
							correctResponses = true;
							assertEquals(expectedResponse.getContentMediaTypesCount(),
									parsedResponse.getContentMediaTypesCount());

							assertTrue(parsedResponse.getContentMediaTypesMap().containsKey(mediaType));
							assertEquals(expectedResponse.getContentMediaTypesMap().get(mediaType).getDataModel(),
									parsedResponse.getContentMediaTypesMap().get(mediaType).getDataModel());
						}
					}
				}
			}

			assertTrue(correctResponses);
		}
	}
}