package restful.api.metric.analyzer.cli.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

import org.junit.jupiter.api.Test;

import restful.api.metric.analyzer.cli.metrics.DistinctMessageRatio;
import restful.api.metric.analyzer.cli.model.generated.internal.Model;
import restful.api.metric.analyzer.cli.parser.OpenApi3Parser;

class DistinctMessageRatioTest {
	@Test
	void calculateMetricAndGetNameTest() throws ParseException {
		OpenApi3Parser parser = new OpenApi3Parser();
		Model.SpecificationFile petStoreModel = parser.loadPublicUrl(
				"https://raw.githubusercontent.com/OAI/OpenAPI-Specification/master/examples/v3.0/petstore-expanded.yaml");

		DistinctMessageRatio distinctMessageRatio = new DistinctMessageRatio(petStoreModel);
		distinctMessageRatio.calculateMetric();
		//3 UniqueInput + 3 UniqueOutput  / 12 totalMessages
		assertEquals(0.5, distinctMessageRatio.getMeasurementValue());
		// getName test
		assertEquals("DistinctMessageRatio", distinctMessageRatio.getName());
		assertEquals("DistinctMessageRatio", distinctMessageRatio.getMeasurement().getMetricName());
		assertEquals(0.5, distinctMessageRatio.getMeasurement().getMetricValue());
	}

	@Test
	void calculateMetricTest2() throws ParseException {
		Path resourceDirectory = Paths.get("src", "test", "resources","metrics", "DMR", "CustomerSrv-openapi.yaml");
		OpenApi3Parser parser = new OpenApi3Parser();
		Model.SpecificationFile customerServiceModel = parser.loadLocalFile(resourceDirectory.toString());

		DistinctMessageRatio distinctMessageRatio = new DistinctMessageRatio(customerServiceModel);
		distinctMessageRatio.calculateMetric();
		//4 UniqueInput + 4 UniqueOutput  / 12 totalMessages
		//rounded to 4 decimal places
		assertEquals(0.6667, distinctMessageRatio.getMeasurementValue());
		assertEquals(0.6667, distinctMessageRatio.getMeasurement().getMetricValue());
	}
}