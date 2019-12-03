package restful.api.metric.analyzer.cli.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

import org.junit.jupiter.api.Test;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import restful.api.metric.analyzer.cli.services.ApplicationService;
import restful.api.metric.analyzer.cli.util.JsonBuilder;
import restful.api.metric.analyzer.cli.util.RestfulServiceMapper;

class JsonBuilderTest {

	@Test
	void jsonTest()
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, InstantiationException, ParseException {

		String path =
				"https://raw.githubusercontent.com/OAI/OpenAPI-Specification/master/examples/v3.0/petstore-expanded.yaml";
		ApplicationService applicationService = new ApplicationService();
		applicationService.loadURIPath(path, "openapi");

		applicationService.createRestfulService(false);
		RestfulServiceMapper mapper = new RestfulServiceMapper();

		JsonObject jsonTest = mapper.createJsonObject(applicationService.getRestfulService());
		JsonBuilder jsonBuilder = new JsonBuilder(jsonTest);
		assertEquals(jsonTest, jsonBuilder.getJsonObject());

		Path resourceDirectory = Paths.get("src", "test", "resources", "jsonTest.json");
		applicationService.exportAsJson(resourceDirectory.toString());
		assertEquals(jsonTest, applicationService.getJsonObject());

		String testPrettyJson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create()
				.toJson(jsonTest);

		assertEquals(testPrettyJson, jsonBuilder.getPrettyJson());

	}
}