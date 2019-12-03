package restful.api.metric.analyzer.cli.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

import org.junit.jupiter.api.Test;

import restful.api.metric.analyzer.cli.metrics.NumberOfRootPaths;
import restful.api.metric.analyzer.cli.model.generated.internal.Model;
import restful.api.metric.analyzer.cli.parser.OpenApi3Parser;

class NumberOfRootPathsTest {

	@Test
	void calculateMetricAndGetNameTest() throws ParseException {
		// calculateMetric test
		Path resourceDirectory = Paths.get("src", "test", "resources", "metrics", "NOR", "petstore.yaml");
		OpenApi3Parser parser = new OpenApi3Parser();

		Model.SpecificationFile specFile = parser.loadLocalFile(resourceDirectory.toString());
		NumberOfRootPaths numberOfRootPaths = new NumberOfRootPaths(specFile);
		numberOfRootPaths.calculateMetric();
		assertEquals(1.0, numberOfRootPaths.getMeasurementValue());
		// getName test
		assertEquals("NumberOfRootPaths", numberOfRootPaths.getName());
		assertEquals("NumberOfRootPaths", numberOfRootPaths.getMeasurement().getMetricName());
		assertEquals(1.0, numberOfRootPaths.getMeasurement().getMetricValue());

	}

	@Test
	void calcualteMetricEmptyRootNameTest() throws ParseException {
		// calculateMetric test
		Path resourceDirectory = Paths.get("src", "test", "resources", "metrics", "NOR", "uspto.yaml");
		OpenApi3Parser parser = new OpenApi3Parser();

		Model.SpecificationFile specFile = parser.loadLocalFile(resourceDirectory.toString());
		NumberOfRootPaths numberOfRootPaths = new NumberOfRootPaths(specFile);
		numberOfRootPaths.calculateMetric();
		assertEquals(1.0, numberOfRootPaths.getMeasurementValue());
		// getName test
		assertEquals("NumberOfRootPaths", numberOfRootPaths.getName());
		assertEquals("NumberOfRootPaths", numberOfRootPaths.getMeasurement().getMetricName());
		assertEquals(1.0, numberOfRootPaths.getMeasurement().getMetricValue());

	}

	@Test
	void calcualteMetricTest2() throws ParseException {
		// calculateMetric test
		Path resourceDirectory = Paths.get("src", "test", "resources", "metrics", "NOR", "api-with-examples.yaml");
		OpenApi3Parser parser = new OpenApi3Parser();

		Model.SpecificationFile specFile = parser.loadLocalFile(resourceDirectory.toString());
		NumberOfRootPaths numberOfRootPaths = new NumberOfRootPaths(specFile);
		numberOfRootPaths.calculateMetric();
		assertEquals(2.0, numberOfRootPaths.getMeasurementValue());
		// getName test
		assertEquals("NumberOfRootPaths", numberOfRootPaths.getName());
		assertEquals("NumberOfRootPaths", numberOfRootPaths.getMeasurement().getMetricName());
		assertEquals(2.0, numberOfRootPaths.getMeasurement().getMetricValue());

	}
}