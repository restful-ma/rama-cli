package restful.api.metric.analyzer.cli.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

import org.junit.jupiter.api.Test;

import restful.api.metric.analyzer.cli.metrics.AveragePathLength;
import restful.api.metric.analyzer.cli.model.generated.internal.Model;
import restful.api.metric.analyzer.cli.parser.OpenApi3Parser;

class AveragePathLengthTest {

	@Test
	void calculateMetricAndGetNameTest() throws ParseException {
		// calculateMetric test
		Path resourceDirectory = Paths.get("src", "test", "resources", "metrics", "APL", "petstore.yaml");
		OpenApi3Parser parser = new OpenApi3Parser();

		Model.SpecificationFile specFile = parser.loadLocalFile(resourceDirectory.toString());
		AveragePathLength averagePathLength = new AveragePathLength(specFile);
		averagePathLength.calculateMetric();

		assertEquals(1.5, averagePathLength.getMeasurementValue());
		// getName test
		assertEquals("AveragePathLength", averagePathLength.getName());
		assertEquals("AveragePathLength", averagePathLength.getMeasurement().getMetricName());
		assertEquals(1.5, averagePathLength.getMeasurement().getMetricValue());


	}

	@Test
	void calculateMetricAndGetNameJSONTest() throws ParseException {
		// calculateMetric test
		Path resourceDirectory = Paths.get("src", "test", "resources", "metrics", "APL", "petstore.json");
		OpenApi3Parser parser = new OpenApi3Parser();

		Model.SpecificationFile specFile = parser.loadLocalFile(resourceDirectory.toString());
		AveragePathLength averagePathLength = new AveragePathLength(specFile);
		averagePathLength.calculateMetric();

		assertEquals(1.5, averagePathLength.getMeasurementValue());
		// getName test
		assertEquals("AveragePathLength", averagePathLength.getName());
		assertEquals("AveragePathLength", averagePathLength.getMeasurement().getMetricName());
		assertEquals(1.5, averagePathLength.getMeasurement().getMetricValue());


	}

	@Test
	void calculateMetricAttachedSlashesTest() throws ParseException {
		// calculateMetric test
		Path resourceDirectory = Paths.get("src", "test", "resources", "metrics", "APL", "petstore2.yaml");
		OpenApi3Parser parser = new OpenApi3Parser();

		Model.SpecificationFile specFile = parser.loadLocalFile(resourceDirectory.toString());
		AveragePathLength averagePathLength = new AveragePathLength(specFile);
		averagePathLength.calculateMetric();

		assertEquals(1.5, averagePathLength.getMeasurementValue());
		// getName test
		assertEquals("AveragePathLength", averagePathLength.getName());
		assertEquals("AveragePathLength", averagePathLength.getMeasurement().getMetricName());
		assertEquals(1.5, averagePathLength.getMeasurement().getMetricValue());
	}
}