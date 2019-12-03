package restful.api.metric.analyzer.cli.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

import org.junit.jupiter.api.Test;

import restful.api.metric.analyzer.cli.metrics.ServiceInterfaceDataCohesion;
import restful.api.metric.analyzer.cli.model.generated.internal.Model;
import restful.api.metric.analyzer.cli.parser.OpenApi3Parser;

class ServiceInterfaceDataCohesionTest {

	@Test
	void calculateMetricAndGetNameTest() throws ParseException {
		// calculateMetric test
		OpenApi3Parser parser = new OpenApi3Parser();
		Model.SpecificationFile customerServiceModel = parser.loadPublicUrl(
				"https://raw.githubusercontent.com/OAI/OpenAPI-Specification/master/examples/v3.0/petstore-expanded.yaml");
		ServiceInterfaceDataCohesion serviceInterfaceDataCohesion = new ServiceInterfaceDataCohesion(
				customerServiceModel);

		serviceInterfaceDataCohesion.calculateMetric();
		//rounded to 4 decimal places
		assertEquals(0.5833, serviceInterfaceDataCohesion.getMeasurementValue());
		// getName test
		assertEquals("ServiceInterfaceDataCohesion", serviceInterfaceDataCohesion.getName());
		assertEquals("ServiceInterfaceDataCohesion", serviceInterfaceDataCohesion.getMeasurement().getMetricName());
		assertEquals(0.5833, serviceInterfaceDataCohesion.getMeasurement().getMetricValue());

		String additionalInformation = "Metric value range: [0-1]; Set of pairwise operations with at least one common parameter:"
				+ " [[find pet by id, deletePet]]; Set of pairwise operations with common return type: [[deletePet, addPet], "
				+ "[find pet by id, deletePet], [find pet by id, addPet], [find pet by id, findPets], [deletePet, findPets], "
				+ "[addPet, findPets]]; Number of operations: 4.0";
		assertEquals(additionalInformation, serviceInterfaceDataCohesion.getMeasurement().getAdditionalInformation());
	}

	@Test
	void calculateMetricAndGetNameTestLocal() throws ParseException {
		// calculateMetric test
		OpenApi3Parser parser = new OpenApi3Parser();
		//File has the same name but differs from the online version !!! also addPet Operation is duplicated
		Path resourceDirectory = Paths.get("src", "test", "resources", "metrics", "SIDC", "petstore-expanded.yaml");
		Model.SpecificationFile customerServiceModel = parser.loadLocalFile(resourceDirectory.toString());
		ServiceInterfaceDataCohesion serviceInterfaceDataCohesion = new ServiceInterfaceDataCohesion(
				customerServiceModel);
		//1 operationPair with at least a single common Input
		//6 operationPairs with same return (including array which contains the same return of another op)
		// (1+6) / 20 
		serviceInterfaceDataCohesion.calculateMetric();
		assertEquals(0.35, serviceInterfaceDataCohesion.getMeasurementValue());
		assertEquals(0.35, serviceInterfaceDataCohesion.getMeasurement().getMetricValue());
	}

	@Test
	void calculateMetricTest2() throws ParseException {
		// calculate Metric test
		Path resourceDirectory = Paths.get("src", "test", "resources", "metrics", "SIDC", "CustomerSrv-openapi.yaml");
		OpenApi3Parser parser = new OpenApi3Parser();

		Model.SpecificationFile customerServiceModel = parser.loadLocalFile(resourceDirectory.toString());
		ServiceInterfaceDataCohesion serviceInterfaceDataCohesion = new ServiceInterfaceDataCohesion();
		serviceInterfaceDataCohesion.setModel(customerServiceModel);

		serviceInterfaceDataCohesion.calculateMetric();
		//rounded to 4 decimal places
		assertEquals(0.3667, serviceInterfaceDataCohesion.getMeasurementValue());
		String additionalInformation = "Metric value range: [0-1]; Set of pairwise operations with at least one common parameter: "
				+ "[[getCustomerById, updateCustomer], [getCustomerById, updateAndCheckCreditRating], "
				+ "[createCustomer, updateCustomer], [deleteCustomer, updateAndCheckCreditRating], "
				+ "[updateCustomer, deleteCustomer], [getCustomerById, deleteCustomer], [updateCustomer, updateAndCheckCreditRating]];"
				+ " Set of pairwise operations with common return type: [[getCustomerById, getCustomers], [createCustomer, updateCustomer],"
				+ " [updateCustomer, deleteCustomer], [createCustomer, deleteCustomer]]; Number of operations: 6.0";
		assertEquals(additionalInformation, serviceInterfaceDataCohesion.getMeasurement().getAdditionalInformation());
	}

	@Test
	void calculateMetricTestWithoutOperationId() throws ParseException {
		// calculate Metric test
		Path resourceDirectory = Paths.get("src", "test", "resources", "metrics", "SIDC", "CustomerSrv-openapi-without-operationId.yaml");
		OpenApi3Parser parser = new OpenApi3Parser();

		Model.SpecificationFile customerServiceModel = parser.loadLocalFile(resourceDirectory.toString());
		ServiceInterfaceDataCohesion serviceInterfaceDataCohesion = new ServiceInterfaceDataCohesion();
		serviceInterfaceDataCohesion.setModel(customerServiceModel);

		serviceInterfaceDataCohesion.calculateMetric();
		//rounded to 4 decimal places
		assertEquals(0.3667, serviceInterfaceDataCohesion.getMeasurementValue());
	}
}