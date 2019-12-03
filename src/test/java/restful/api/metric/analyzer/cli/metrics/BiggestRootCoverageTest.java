package restful.api.metric.analyzer.cli.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

import org.junit.jupiter.api.Test;

import restful.api.metric.analyzer.cli.metrics.BiggestRootCoverage;
import restful.api.metric.analyzer.cli.model.generated.internal.Model;
import restful.api.metric.analyzer.cli.parser.OpenApi3Parser;

class BiggestRootCoverageTest {

	@Test
	void calculateMetricAndGetNameTestPetstore() throws ParseException {
		// calculateMetric test
		Path resourceDirectory = Paths.get("src", "test", "resources", "metrics", "BRC", "petstore.yaml");
		OpenApi3Parser parser = new OpenApi3Parser();

		Model.SpecificationFile specFile = parser.loadLocalFile(resourceDirectory.toString());
		BiggestRootCoverage biggestRootCoverage = new BiggestRootCoverage(specFile);
		biggestRootCoverage.calculateMetric();
		assertEquals((1.0), biggestRootCoverage.getMeasurementValue());
		// getName test
		assertEquals("BiggestRootCoverage", biggestRootCoverage.getName());
		assertEquals("BiggestRootCoverage", biggestRootCoverage.getMeasurement().getMetricName());
		assertEquals((1.0), biggestRootCoverage.getMeasurement().getMetricValue());

	}

	@Test
	void calculateMetricTestLinkExample() throws ParseException {
		// calculateMetric test
		Path resourceDirectory = Paths.get("src", "test", "resources", "metrics", "BRC", "link-example.yaml");
		OpenApi3Parser parser = new OpenApi3Parser();

		Model.SpecificationFile specFile = parser.loadLocalFile(resourceDirectory.toString());
		BiggestRootCoverage biggestRootCoverage = new BiggestRootCoverage(specFile);
		biggestRootCoverage.calculateMetric();
		assertEquals((1.0), biggestRootCoverage.getMeasurementValue());
		assertEquals((1.0), biggestRootCoverage.getMeasurement().getMetricValue());

	}

	@Test
	void calculateMetricTestApiWithExamples() throws ParseException {
		// calculateMetric test
		Path resourceDirectory = Paths.get("src", "test", "resources", "metrics", "BRC", "api-with-examples.yaml");
		OpenApi3Parser parser = new OpenApi3Parser();

		Model.SpecificationFile specFile = parser.loadLocalFile(resourceDirectory.toString());
		BiggestRootCoverage biggestRootCoverage = new BiggestRootCoverage(specFile);
		biggestRootCoverage.calculateMetric();
		assertEquals((0.5), biggestRootCoverage.getMeasurementValue());
		assertEquals((0.5), biggestRootCoverage.getMeasurement().getMetricValue());

	}

	@Test
	void calculateMetricTestEmptyRootName() throws ParseException {
		// calculateMetric test
		Path resourceDirectory = Paths.get("src", "test", "resources", "metrics", "BRC", "uspto.yaml");
		OpenApi3Parser parser = new OpenApi3Parser();

		Model.SpecificationFile specFile = parser.loadLocalFile(resourceDirectory.toString());
		BiggestRootCoverage biggestRootCoverage = new BiggestRootCoverage(specFile);
		biggestRootCoverage.calculateMetric();
		assertEquals(1.0, biggestRootCoverage.getMeasurementValue());
		assertEquals(1.0, biggestRootCoverage.getMeasurement().getMetricValue());

	}

		@Test 
	    void calculateMetricWithZeroPaths() throws ParseException {
			// calculateMetric test
			Path resourceDirectory = Paths.get("src", "test", "resources", "metrics", "BRC", "zeroPaths.yaml");
			OpenApi3Parser parser = new OpenApi3Parser();

			Model.SpecificationFile specFile = parser.loadLocalFile(resourceDirectory.toString());
			BiggestRootCoverage biggestRootCoverage = new BiggestRootCoverage(specFile);
			biggestRootCoverage.calculateMetric();
			assertEquals(0, biggestRootCoverage.getMeasurementValue());
			assertEquals(0, biggestRootCoverage.getMeasurement().getMetricValue());
	    }
}