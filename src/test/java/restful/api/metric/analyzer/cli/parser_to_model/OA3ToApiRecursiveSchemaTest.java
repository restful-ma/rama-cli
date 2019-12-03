package restful.api.metric.analyzer.cli.parser_to_model;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

import org.junit.jupiter.api.Test;

import restful.api.metric.analyzer.cli.model.generated.internal.Model;
import restful.api.metric.analyzer.cli.parser.OpenApi3Parser;

/**
 * Test should check if OA3 Parser can parse files with recursive schemas.
 * Check for stackOverflow that could possibly happen while parsing recursive
 */
public class OA3ToApiRecursiveSchemaTest {
	static Model.SpecificationFile specFile;
	static String apiTestUrl = "http://petstore.swagger.io/api";

	@Test
	void parseRecursiveSchemaMix() throws ParseException {
		Path resourceDirectory = Paths.get("src", "test", "resources", "OA3ParserRecursiveSchema",
				"petstore-expanded2-recursiveSchema.yaml");
		OpenApi3Parser parser = new OpenApi3Parser();

		specFile = parser.loadLocalFile(resourceDirectory.toString());
		assertNotEquals(null,specFile);	
	}

	@Test
	void parseRecursiveSchema1() throws ParseException {
		Path resourceDirectory = Paths.get("src", "test", "resources", "OA3ParserRecursiveSchema",
				"azure.com-applicationinsights-eaSubscriptionMigration_API-2017-10-01-swagger.yaml");
		OpenApi3Parser parser = new OpenApi3Parser();

		specFile = parser.loadLocalFile(resourceDirectory.toString());
		assertNotEquals(null,specFile);
	}

	@Test
	void parseRecursiveSchema2() throws ParseException {
		Path resourceDirectory = Paths.get("src", "test", "resources", "OA3ParserRecursiveSchema", "recursiveSchema2.yaml");
		OpenApi3Parser parser = new OpenApi3Parser();

		specFile = parser.loadLocalFile(resourceDirectory.toString());
		assertNotEquals(null,specFile);
	}

	@Test
	void parseRecursiveSchema3() throws ParseException {
		Path resourceDirectory = Paths.get("src", "test", "resources", "OA3ParserRecursiveSchema", "recursiveSchema3.yaml");
		OpenApi3Parser parser = new OpenApi3Parser();

		specFile = parser.loadLocalFile(resourceDirectory.toString());
		assertNotEquals(null,specFile);
	}
	
	@Test
	void parseRecursiveSchema4() throws ParseException {
		Path resourceDirectory = Paths.get("src", "test", "resources", "OA3ParserRecursiveSchema", "zenoti.com-1.0.0-swagger.yaml");
		OpenApi3Parser parser = new OpenApi3Parser();

		specFile = parser.loadLocalFile(resourceDirectory.toString());
		assertNotEquals(null,specFile);
	}
}