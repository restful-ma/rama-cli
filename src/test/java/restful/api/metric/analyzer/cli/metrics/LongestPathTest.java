package restful.api.metric.analyzer.cli.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

import org.junit.jupiter.api.Test;

import restful.api.metric.analyzer.cli.metrics.LongestPath;
import restful.api.metric.analyzer.cli.model.generated.internal.Model;
import restful.api.metric.analyzer.cli.parser.OpenApi3Parser;

class LongestPathTest {

	@Test
	void calculateMetricAndGetNameTest() throws ParseException {
		// calculateMetric test
		Path resourceDirectory = Paths.get("src", "test", "resources", "metrics", "LP", "link-example.yaml");
		OpenApi3Parser parser = new OpenApi3Parser();

		Model.SpecificationFile linkModel = parser.loadLocalFile(resourceDirectory.toString());
		LongestPath longestPath = new LongestPath(linkModel);
		longestPath.calculateMetric();
		assertEquals(7.0, longestPath.getMeasurementValue());
		// getName test
		assertEquals("LongestPath", longestPath.getName());
		assertEquals("LongestPath", longestPath.getMeasurement().getMetricName());
		assertEquals(7.0, longestPath.getMeasurement().getMetricValue());

	}

	@Test
	void calcualteMetricAttachedSlashesTest() throws ParseException {
		// calculateMetric test
		Path resourceDirectory = Paths.get("src", "test", "resources", "metrics", "LP", "link-example2.yaml");
		OpenApi3Parser parser = new OpenApi3Parser();

		Model.SpecificationFile linkModel = parser.loadLocalFile(resourceDirectory.toString());
		LongestPath longestPath = new LongestPath(linkModel);
		longestPath.calculateMetric();
		assertEquals(7.0, longestPath.getMeasurementValue());
		// getName test
		assertEquals("LongestPath", longestPath.getName());
		assertEquals("LongestPath", longestPath.getMeasurement().getMetricName());
		assertEquals(7.0, longestPath.getMeasurement().getMetricValue());
	}
}