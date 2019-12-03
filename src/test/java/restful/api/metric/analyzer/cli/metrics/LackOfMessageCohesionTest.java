package restful.api.metric.analyzer.cli.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

import org.junit.jupiter.api.Test;

import restful.api.metric.analyzer.cli.metrics.LackOfMessageLevelCohesion;
import restful.api.metric.analyzer.cli.model.generated.internal.Model;
import restful.api.metric.analyzer.cli.parser.OpenApi3Parser;

class LackOfMessageCohesionTest {

	@Test
	void calculateMetricAndGetNameTest() throws ParseException {
		OpenApi3Parser parser = new OpenApi3Parser();
		Model.SpecificationFile petStoreModel = parser.loadPublicUrl(
				"https://raw.githubusercontent.com/OAI/OpenAPI-Specification/master/examples/v3.0/petstore-expanded.yaml");
		LackOfMessageLevelCohesion lackOfMessageLevelCohesion = new LackOfMessageLevelCohesion(petStoreModel);

		lackOfMessageLevelCohesion.calculateMetric();
		// rounded to 4 decimal places
		assertEquals(0.6556, lackOfMessageLevelCohesion.getMeasurementValue());
		// getName test
		assertEquals("LackOfMessageLevelCohesion", lackOfMessageLevelCohesion.getName());
		assertEquals("LackOfMessageLevelCohesion", lackOfMessageLevelCohesion.getMeasurement().getMetricName());
		assertEquals(0.6556, lackOfMessageLevelCohesion.getMeasurement().getMetricValue());
	}

	@Test
	void calculateMetricTest2() throws ParseException {
		Path resourceDirectory = Paths.get("src", "test", "resources", "metrics","LoMC", "CustomerSrv-openapi.yaml");
		OpenApi3Parser parser = new OpenApi3Parser();
		Model.SpecificationFile customerServiceModel = parser.loadLocalFile(resourceDirectory.toString());
		LackOfMessageLevelCohesion lackOfMessageLevelCohesion = new LackOfMessageLevelCohesion(customerServiceModel);

		lackOfMessageLevelCohesion.calculateMetric();
		// rounded to 4 decimal places
		assertEquals(0.7267, lackOfMessageLevelCohesion.getMeasurementValue());
		assertEquals(0.7267, lackOfMessageLevelCohesion.getMeasurement().getMetricValue());
	}
	
	@Test
	void calculateMetricSimple() throws ParseException {
		Path resourceDirectory = Paths.get("src", "test", "resources", "metrics","LoMC", "ReusableReqBody2.yaml");
		OpenApi3Parser parser = new OpenApi3Parser();
		Model.SpecificationFile customerServiceModel = parser.loadLocalFile(resourceDirectory.toString());
		LackOfMessageLevelCohesion lackOfMessageLevelCohesion = new LackOfMessageLevelCohesion(customerServiceModel);

		lackOfMessageLevelCohesion.calculateMetric();
		// rounded to 4 decimal places
		// Operation pairs if are ordered by appearence of operations
		// 1-2 inSimi = 0 , outSimi = 2/5 , OpSimi = (2/5) * (1/2) = (2/10) 
		// 1-3 inSimi = 3/4, outSimi = 5/6 "(3 has array of pet)", OpSimi = (19/12) * (1/2) = (19/24)
		// 2-3 inSimi = 0, outSimi = 2/6, OpSimi = (2/6) * (1/2) = (2/12)
		// LoMC = (	1-(2/10) + 1-(19/24) + 1-(2/12)	)    / 3 
		//(	1.0-(2.0/10.0) + 1.0-(19.0/24.0) + 1.0-(2.0/12.0))/ 3.0
		// == 0.613888888888...
		
		//rounded to 4 decimal places
		assertEquals(0.6139, lackOfMessageLevelCohesion.getMeasurementValue());
		assertEquals(0.6139, lackOfMessageLevelCohesion.getMeasurement().getMetricValue());
	
	}
}