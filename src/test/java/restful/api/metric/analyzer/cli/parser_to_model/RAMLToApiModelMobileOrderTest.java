package restful.api.metric.analyzer.cli.parser_to_model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.ParseException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;

import restful.api.metric.analyzer.cli.model.generated.internal.Model;
import restful.api.metric.analyzer.cli.model.generated.internal.Model.SpecificationFile;
import restful.api.metric.analyzer.cli.parser.RAMLParser;

public class RAMLToApiModelMobileOrderTest {

	static org.raml.v2.api.model.v10.api.Api ramlParserResult;
	static SpecificationFile specFile;

	@BeforeEach
	void parseFromLocalFile() throws ParseException {
		RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi("https://raw.githubusercontent.com/raml-org/raml-examples/master/others/mobile-order-api/api.raml");
		assertFalse(ramlModelResult.hasErrors());

		RAMLParser parser = new RAMLParser();
		Model.SpecificationFile specificationFile = parser.loadPublicUrl("https://raw.githubusercontent.com/raml-org/raml-examples/master/others/mobile-order-api/api.raml");

		assertNotNull(specificationFile);

		specFile = specificationFile; // TODO
		ramlParserResult = ramlModelResult.getApiV10();
	}

	@Test
	void checkBaseUrl() {
		assertTrue(specFile.getApis(0).getBasePath(0).equals("http://localhost:8081/api"));
	}

	@Test
	void checkParameters() {

		assertNotNull(specFile.getApis(0).getPathsMap().get("/orders").getMethodsMap().get("GET").getParametersMap().get("userId"));
		assertTrue(specFile.getApis(0).getPathsMap().get("/orders").getMethodsMap().get("GET").getParametersMap().size() == 3);
		Model.Parameter parameter = specFile.getApis(0).getPathsMap().get("/orders").getMethodsMap().get("GET").getParametersMap().get("userId");
		assertTrue((parameter.getKey().equals("userId")) && (parameter.getTypeValue() == 0) && (parameter.getRequired()));
	}

	@Test
	void checkResponses() {
		assertTrue(specFile.getApis(0).getPathsMap().get("/orders").getMethodsMap().get("GET").getResponsesList().size() == 1);
		Model.Response response = specFile.getApis(0).getPathsMap().get("/orders").getMethodsMap().get("GET").getResponsesList().get(0);
		assertTrue(response.getCodeCount() == 1);
		assertNotNull(response.getContentMediaTypesMap().get("application/json"));
	}

	@Test
	void checkContentMediaType() {
		assertTrue(specFile.getApis(0).getPathsMap().get("/orders").getMethodsMap().get("GET").getResponsesList().get(0).getContentMediaTypesMap().size() == 1);
		Model.ContentMediaType cmt = specFile.getApis(0).getPathsMap().get("/orders").getMethodsMap().get("GET").getResponsesList().get(0).getContentMediaTypesMap().get("application/json");
		assertEquals("application/json", cmt.getMediaType());
	}

	@Test
	void checkDataModel() {
		assertNotNull(specFile.getApis(0).getPathsMap().get("/orders").getMethodsMap().get("GET").getResponsesList().get(0).getContentMediaTypesMap().get("application/json").getDataModel());
		Model.DataModel dataModel = specFile.getApis(0).getPathsMap().get("/orders").getMethodsMap().get("GET").getResponsesList().get(0).getContentMediaTypesMap().get("application/json").getDataModel();
		assertEquals(8,dataModel.getDataType().getNumber()); //TODO dataType in raml can be user defined and is outside of the Model scope
		assertTrue(dataModel.getPropertiesCount() == 1);
		assertTrue(dataModel.getPropertiesMap().containsKey("orders"));
		Model.Property ordersProperty = dataModel.getPropertiesMap().get("orders");
		assertEquals(7, ordersProperty.getType().getNumber());
		assertTrue(ordersProperty.containsSubProperties("order_id"));
		assertTrue(ordersProperty.containsSubProperties("creation_date"));
		assertTrue(ordersProperty.containsSubProperties("items"));
		assertEquals(0, ordersProperty.getSubPropertiesMap().get("order_id").getType().getNumber());
		Model.Property itemsProperty = ordersProperty.getSubPropertiesMap().get("items");
		assertTrue(itemsProperty.containsSubProperties("product_id"));
		assertTrue(itemsProperty.containsSubProperties("quantity"));
		assertEquals(2, itemsProperty.getSubPropertiesMap().get("quantity").getType().getNumber());
	}
}