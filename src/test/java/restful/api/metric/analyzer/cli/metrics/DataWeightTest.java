package restful.api.metric.analyzer.cli.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

import org.junit.jupiter.api.Test;

import restful.api.metric.analyzer.cli.metrics.DataWeight;
import restful.api.metric.analyzer.cli.model.generated.internal.Model;
import restful.api.metric.analyzer.cli.parser.OpenApi3Parser;

class DataWeightTest {

	@Test
	void calculateMetricAndGetNameTest() throws ParseException {
		// calculateMetric test
		Path resourceDirectory = Paths.get("src", "test", "resources", "metrics", "DW", "petstore-expanded2.yaml");
		OpenApi3Parser parser = new OpenApi3Parser();

		Model.SpecificationFile petStoreModel = parser.loadLocalFile(resourceDirectory.toString());
		DataWeight dw = new DataWeight(petStoreModel);
		dw.calculateMetric();
		assertEquals(23.0, dw.getMeasurementValue());
		// getName test
		assertEquals("DataWeight", dw.getName());
		assertEquals("DataWeight", dw.getMeasurement().getMetricName());
		assertEquals(23.0, dw.getMeasurement().getMetricValue());

	}
}