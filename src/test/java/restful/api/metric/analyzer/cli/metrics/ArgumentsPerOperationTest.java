package restful.api.metric.analyzer.cli.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

import org.junit.jupiter.api.Test;

import restful.api.metric.analyzer.cli.metrics.ArgumentsPerOperation;
import restful.api.metric.analyzer.cli.model.generated.internal.Model;
import restful.api.metric.analyzer.cli.parser.OpenApi3Parser;

class ArgumentsPerOperationTest {

	@Test
	void calculateMetricAndGetNameTestLinkExample() throws ParseException {
		Path resourceDirectory = Paths.get("src", "test", "resources", "metrics","APO", "link-example.yaml");
		OpenApi3Parser parser = new OpenApi3Parser();

		Model.SpecificationFile specFile = parser.loadLocalFile(resourceDirectory.toString());
		ArgumentsPerOperation argumentsPerOperation = new ArgumentsPerOperation(specFile);
		argumentsPerOperation.calculateMetric();
		//13.0 / 6.0 rounded to 4 decimal places
		assertEquals(2.1667, argumentsPerOperation.getMeasurementValue());
		// getName test
		assertEquals("ArgumentsPerOperation", argumentsPerOperation.getName());
		assertEquals("ArgumentsPerOperation", argumentsPerOperation.getMeasurement().getMetricName());
		assertEquals(2.1667, argumentsPerOperation.getMeasurement().getMetricValue());

	}

	@Test
	void calculateMetricAndGetNameTestPetStore() throws ParseException {
//		 calculateMetric test
		Path resourceDirectory = Paths.get("src", "test", "resources", "metrics","APO", "petstore-expanded2.yaml");
		OpenApi3Parser parser = new OpenApi3Parser();
		Model.SpecificationFile specFile = parser.loadLocalFile(resourceDirectory.toString());
		ArgumentsPerOperation argumentsPerOperation = new ArgumentsPerOperation(specFile);
		argumentsPerOperation.calculateMetric();
		// 5.0 / 4.0
		assertEquals(1.25, argumentsPerOperation.getMeasurementValue());
		// getName test
		assertEquals("ArgumentsPerOperation", argumentsPerOperation.getName());
		assertEquals("ArgumentsPerOperation", argumentsPerOperation.getMeasurement().getMetricName());
		assertEquals(1.25, argumentsPerOperation.getMeasurement().getMetricValue());

	}

	@Test
	void calculateMetricTestApiWithExamples() throws ParseException {
		// calculateMetric test
		Path resourceDirectory = Paths.get("src", "test", "resources", "metrics","APO", "api-with-examples.yaml");
		OpenApi3Parser parser = new OpenApi3Parser();
		Model.SpecificationFile specFile = parser.loadLocalFile(resourceDirectory.toString());
		ArgumentsPerOperation argumentsPerOperation = new ArgumentsPerOperation(specFile);
		argumentsPerOperation.calculateMetric();
		assertEquals(0.0, argumentsPerOperation.getMeasurementValue());
		assertEquals(0.0, argumentsPerOperation.getMeasurement().getMetricValue());

	}

	@Test
	void calculateMetricTestUspto() throws ParseException {
		// calculateMetric test
		Path resourceDirectory = Paths.get("src", "test", "resources", "metrics","APO", "uspto.yaml");
		OpenApi3Parser parser = new OpenApi3Parser();
		Model.SpecificationFile specFile = parser.loadLocalFile(resourceDirectory.toString());
		ArgumentsPerOperation argumentsPerOperation = new ArgumentsPerOperation(specFile);
		argumentsPerOperation.calculateMetric();
		// 5 / 3 rounded to 4 decimal places
		assertEquals(1.6667, argumentsPerOperation.getMeasurementValue());
		assertEquals(1.6667, argumentsPerOperation.getMeasurement().getMetricValue());

	}
	
	@Test
	void calculateCommonParamAndOperationParam() throws ParseException {
		// calculateMetric test
		Path resourceDirectory = Paths.get("src", "test", "resources", "metrics","APO", "CommonParamAndOperationParam.yaml");
		OpenApi3Parser parser = new OpenApi3Parser();
		Model.SpecificationFile specFile = parser.loadLocalFile(resourceDirectory.toString());
		ArgumentsPerOperation argumentsPerOperation = new ArgumentsPerOperation(specFile);
		argumentsPerOperation.calculateMetric();
		// limit is common param for all operations in /pet path
		// 6 / 4 rounded to 4 decimal places
		assertEquals(1.5, argumentsPerOperation.getMeasurementValue());
		assertEquals(1.5, argumentsPerOperation.getMeasurement().getMetricValue());

	}
}