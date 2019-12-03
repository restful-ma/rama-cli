package restful.api.metric.analyzer.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

import org.junit.jupiter.api.Test;

import restful.api.metric.analyzer.cli.services.ApplicationService;

class ApplicationServiceTest {

	ApplicationService applicationService;

	@Test
	void loadUriPathTest() throws ParseException {
		String path =
				"https://raw.githubusercontent.com/OAI/OpenAPI-Specification/master/examples/v3.0/petstore-expanded.yaml";
		applicationService = new ApplicationService();
		applicationService.loadURIPath(path, "openapi");
		assertEquals("Swagger Petstore", applicationService.getModel().getTitle());
	}

	@Test
	void localFileTestAndGetNameAndMetricTest() throws ParseException {
		Path resourceDirectory = Paths.get("src", "test", "resources","OA3OldFiles", "petstore-expanded.yaml");
		applicationService = new ApplicationService();
		applicationService.loadLocalFilePath(resourceDirectory.toString(), "openapi");
		assertEquals("Swagger Petstore", applicationService.getModel().getTitle());
	}
}