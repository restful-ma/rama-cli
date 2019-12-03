package restful.api.metric.analyzer.cli.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Map;

import org.junit.jupiter.api.Test;

import restful.api.metric.analyzer.cli.services.ApplicationService;
import restful.api.metric.analyzer.cli.util.RestfulServiceMapper;
import restful.api.metric.analyzer.cli.util.PdfCreator;

public class PdfCreatorTest {

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

		Map<String, Object> pdfMap = mapper.createMapObject(applicationService.getRestfulService());
		PdfCreator pdfCreator = new PdfCreator(pdfMap);

		Path resourceDirectory = Paths.get("src", "test", "resources", "pdfTest.pdf");
		applicationService.exportAsPDF(resourceDirectory.toString());

		assertEquals(pdfCreator.createPdf(resourceDirectory.toString()), pdfCreator.getDocument());
	}
}