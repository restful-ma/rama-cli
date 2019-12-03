package restful.api.metric.analyzer.cli.parser_to_model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;

import restful.api.metric.analyzer.cli.model.generated.internal.Model;
import restful.api.metric.analyzer.cli.model.generated.internal.Model.Path;
import restful.api.metric.analyzer.cli.model.generated.internal.Model.SpecificationFile;
import restful.api.metric.analyzer.cli.parser.RAMLParser;

public class RAMLToApiModelBankingTest {

	//	static Api internalModel;
	static org.raml.v2.api.model.v10.api.Api ramlParserResult;
	static SpecificationFile specFile;

	@BeforeEach
	void parseFromLocalFile() throws ParseException {
		RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi("https://raw.githubusercontent.com/raml-org/raml-examples/master/others/banking-api/api.raml");
		assertFalse(ramlModelResult.hasErrors());

		RAMLParser parser = new RAMLParser();

		Model.SpecificationFile specificationFile = parser.loadPublicUrl("https://raw.githubusercontent.com/raml-org/raml-examples/master/others/banking-api/api.raml");
		assertNotNull(specificationFile);

		specFile = specificationFile;
		ramlParserResult = ramlModelResult.getApiV10();
	}

	@Test
	void SpecFile() {
		assertNotNull(specFile);
	}

	@Test
	void checkBaseUrl() {
		assertTrue(specFile.getApis(0).getBasePath(0).equals("Empty"));
	}

	@Test
	void checkPaths() {

		List<String> paths = new ArrayList<>();
		paths.add("/customers");
		paths.add("/customers/corporate");
		paths.add("/customers/commercial");
		paths.add("/customers/{customer_id}");
		paths.add("/customers/{customer_id}/accounts");
		paths.add("/customers/{customer_id}/accounts/{account_id}");
		paths.add("/customers/{customer_id}/loans");
		paths.add("/customers/{customer_id}/loans/{loan_id}");
		paths.add("/customers/{customer_id}/loans/schedule");
		paths.add("/customers/{customer_id}/cards");
		paths.add("/customers/{customer_id}/cards/debit");
		paths.add("/customers/{customer_id}/cards/debit/{card_id}");
		paths.add("/customers/{customer_id}/cards/credit");
		paths.add("/customers/{customer_id}/cards/credit/{card_id}");


//		List<String> parserPaths = new ArrayList<>();
//		List<Model.Path> internalApiPaths = (List<Path>) specFile.getApis(0).getPathsMap().values();
//		System.out.println("SIZE" + internalApiPaths.size());

		assertEquals(paths.size(), specFile.getApis(0).getPathsMap().values().size());

		for (Model.Path path : specFile.getApis(0).getPathsMap().values()) {
			assertTrue(paths.contains(path.getPathName()));
		}
	}

	@Test
	void checkMethodsOfPath() {

		Model.Path path1 = (Path) specFile.getApis(0).getPathsMap().values().toArray()[1];
		Model.Path path2 = (Path) specFile.getApis(0).getPathsMap().values().toArray()[2];
		Model.Path path3 = (Path) specFile.getApis(0).getPathsMap().values().toArray()[3];
		Model.Path path5 = (Path) specFile.getApis(0).getPathsMap().values().toArray()[5];
		Model.Path path6 = (Path) specFile.getApis(0).getPathsMap().values().toArray()[6];
		Model.Path path8 = (Path) specFile.getApis(0).getPathsMap().values().toArray()[8];
		Model.Path path11 = (Path) specFile.getApis(0).getPathsMap().values().toArray()[11];
		Model.Path path13 = (Path) specFile.getApis(0).getPathsMap().values().toArray()[13];

//		List<Model.Path> internalApiPaths = (List<Path>) specFile.getApis(0).getPathsMap().values().toArray()[1].;
		assertTrue(path1.getPathName().equals("/customers/corporate") &&
				(path1.getMethodsMap().containsKey("POST")));
		assertTrue(path2.getPathName().equals("/customers/commercial") &&
				(path2.getMethodsMap().containsKey("POST")));
		assertTrue(path3.getPathName().equals("/customers/{customer_id}") &&
				(path3.getMethodsMap().containsKey("PUT")));
		assertTrue(path5.getPathName().equals("/customers/{customer_id}/accounts/{account_id}") &&
				(path5.getMethodsMap().containsKey("DELETE")));
		assertTrue(path6.getPathName().equals("/customers/{customer_id}/loans") &&
				(path6.getMethodsMap().containsKey("POST")));
		assertTrue(path8.getPathName().equals("/customers/{customer_id}/loans/schedule") &&
				path8.getMethodsMap().containsKey("GET"));
		assertTrue(path11.getPathName().equals("/customers/{customer_id}/cards/debit/{card_id}") &&
				path11.getMethodsMap().containsKey("DELETE"));
		assertTrue(path13.getPathName().equals("/customers/{customer_id}/cards/credit/{card_id}") &&
				path13.getMethodsMap().containsKey("DELETE"));
	}


	void getPropertiesFromRequestBody() {
/* TODO
		for(int res = 0; res < ramlParserResult.resources().size(); res++) {
			for (int met = 0; met < ramlParserResult.resources().get(res).methods().size(); met++) {
				HttpMethod enumMethod = HttpMethod.valueOf(ramlParserResult.resources().get(res).methods().get(met).displayName().value().toUpperCase());
				Method internalMethod = internalModel.getPathByName(ramlParserResult.resources().get(res).relativeUri().value())
						.getAllMethods().get(enumMethod);
				for(int prop = 0 ; prop < ramlParserResult.resources().get(res).methods().get(met).body().size();prop++) {
//					assertEquals(ramlParserResult.resources().get(res).methods().get(met).body().get(prop)));

					assertEquals(internalMethod.getRequestBodys().get(0).getProperties().get(prop).getKey(),
							ramlParserResult.resources().get(res).methods().get(met).body().get(prop).name());
					assertEquals(internalMethod.getRequestBodys().get(0).getProperties().get(prop).getType(),
							ramlParserResult.resources().get(res).methods().get(met).body().get(prop).type());
					assertEquals(internalMethod.getRequestBodys().get(0).getProperties().get(prop).getFormat(),
							ramlParserResult.resources().get(res).methods().get(met).body().get(prop).displayName().value());
				}
			}
		}
*/
	}
}