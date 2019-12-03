package restful.api.metric.analyzer.cli.parser_to_model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import restful.api.metric.analyzer.cli.model.generated.internal.Model;
import restful.api.metric.analyzer.cli.model.generated.internal.Model.Api;
import restful.api.metric.analyzer.cli.model.generated.internal.Model.HttpMethod;
import restful.api.metric.analyzer.cli.model.generated.internal.Model.Method;
import restful.api.metric.analyzer.cli.model.generated.internal.Model.SpecificationDescriptor;
import restful.api.metric.analyzer.cli.model.generated.internal.Model.SpecificationFile;
import restful.api.metric.analyzer.cli.model.generated.internal.Model.SpecificationFormat;
import restful.api.metric.analyzer.cli.parser.WADLParser;

class WADLToApiModel0Test {

	private static SpecificationFile expectedModel;
	private static String basePath = "http://localhost:8080/resources/v1/";
	private static String pathName1 = "/{sessionId: \\d{1, 10}}";
	private static String pathName2 = "/{uuid: \\{[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\}}+{id}";


	@BeforeAll
	static void getFirstExampleApi() {
		Method method = Method.newBuilder()
				.setHttpMethod(HttpMethod.GET)
				.setOperationId("")
				.build();

		Model.Path path1 = Model.Path.newBuilder()
				.setPathName(pathName1)
				.putMethods(method.getHttpMethod().toString(), method)
				.build();

		Model.Path path2 = Model.Path.newBuilder()
				.setPathName(pathName2)
				.putMethods(method.getHttpMethod().toString(), method)
				.build();

		Api api = Api.newBuilder()
				.addBasePath(basePath)
				.putPaths(path1.getPathName(), path1)
				.putPaths(path2.getPathName(), path2)
				.build();

		expectedModel = SpecificationFile.newBuilder()
				.setSpecificationDescriptor(SpecificationDescriptor.newBuilder().setSpecificationFormat(SpecificationFormat.WADL))
				.addApis(api)
				.build();

	}

	private SpecificationFile parseFile() throws ParseException {
		Path resourceDirectory = Paths.get("src", "test", "resources","WADLParser", "wadl_example_0.wadl");
		return new WADLParser().loadPublicUrl("https://raw.githubusercontent.com/LucyBot-Inc/api-spec-converter/master/test/input/wadl/regex_paths.wadl");
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
		assertTrue(model.getApis(0).containsPaths(pathName1));
		assertTrue(model.getApis(0).containsPaths(pathName2));
	}

	@Test
	void getMethods() throws ParseException {
		SpecificationFile model = parseFile();

		assertEquals(expectedModel.getApis(0).getPathsMap().get(pathName1).getMethodsCount(),
				model.getApis(0).getPathsMap().get(pathName1).getMethodsCount());
		assertTrue(model.getApis(0).getPathsMap().get(pathName1).containsMethods(HttpMethod.GET.toString()));

		assertEquals(expectedModel.getApis(0).getPathsMap().get(pathName2).getMethodsCount(),
				model.getApis(0).getPathsMap().get(pathName2).getMethodsCount());
		assertTrue(model.getApis(0).getPathsMap().get(pathName2).containsMethods(HttpMethod.GET.toString()));
	}
}