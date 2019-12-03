package restful.api.metric.analyzer.cli.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

import org.junit.jupiter.api.Test;

import restful.api.metric.analyzer.cli.metrics.WeightedServiceInterfaceCount;
import restful.api.metric.analyzer.cli.model.generated.internal.Model;
import restful.api.metric.analyzer.cli.parser.OpenApi3Parser;

class WeightedServiceInterfaceCountTest {


	@Test
	void calculateMetricTestAndGetNameTest() throws ParseException {
		Path resourceDirectory = Paths.get("src", "test", "resources", "metrics", "WSIC", "petstore-expanded.yaml");
		OpenApi3Parser parser = new OpenApi3Parser();

		Model.SpecificationFile specFile = parser.loadLocalFile(resourceDirectory.toString());
		WeightedServiceInterfaceCount weightedServiceInterfaceCount =
				new WeightedServiceInterfaceCount(specFile);

		weightedServiceInterfaceCount.calculateMetric();
		assertEquals(5, weightedServiceInterfaceCount.getMeasurementValue());
		assertEquals("WeightedServiceInterfaceCount", weightedServiceInterfaceCount.getName());
		assertEquals("WeightedServiceInterfaceCount",
				weightedServiceInterfaceCount.getMeasurement().getMetricName());
		assertEquals(5, weightedServiceInterfaceCount.getMeasurement().getMetricValue());
		assertEquals("Number of different operations: 5",
				weightedServiceInterfaceCount.getMeasurement().getAdditionalInformation());
	}

	@Test
	void calculateMetricTest2() throws ParseException {
		Path resourceDirectory = Paths.get("src", "test", "resources", "metrics", "WSIC", "CustomerSrv-openapi.yaml");
		OpenApi3Parser parser = new OpenApi3Parser();

		Model.SpecificationFile specFile = parser.loadLocalFile(resourceDirectory.toString());
		WeightedServiceInterfaceCount weightedServiceInterfaceCount =
				new WeightedServiceInterfaceCount();
		weightedServiceInterfaceCount.setModel(specFile);

		weightedServiceInterfaceCount.calculateMetric();
		assertEquals(6, weightedServiceInterfaceCount.getMeasurementValue());
	}
}