package restful.api.metric.analyzer.cli.metrics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import restful.api.metric.analyzer.cli.model.Measurement;
import restful.api.metric.analyzer.cli.model.generated.internal.Model;
import restful.api.metric.analyzer.cli.model.generated.internal.Model.ContentMediaType;

public class ServiceInterfaceDataCohesion implements IMetric {

	private final String metricName;
	private Model.SpecificationFile model;
	private double measurementValue;

	private double numberOfOperations;
	private Set<String> pairWiseOperationSet;
	private Set<String> pairWiseReturnSet;
	private static final String OPERATION_STRING = "Operation"; 

	/**
	 * empty constructor for java.reflections
	 */
	public ServiceInterfaceDataCohesion() {
		metricName = "ServiceInterfaceDataCohesion";
	}

	/**
	 * constructor which loads the "InternaApilModel" root object
	 * Model.SpecifactionFile which contains the model of the API.
	 *
	 * @param specFile
	 */
	public ServiceInterfaceDataCohesion(Model.SpecificationFile specFile) {
		this();
		this.model = specFile;
	}

	/**
	 * iterates through all operations and check for the cohesion. To calculate the
	 * cohesion, to calculate SIDC the sum of the number of service operations that
	 * have at least one input parameter type in common and the number of service
	 * operation pairs that have the same return type gets divided by the number of
	 * all possible combinations of operation pairs.
	 */
	public void calculateMetric() {
		Multimap<String, String> inputParameterMap = ArrayListMultimap.create();
		Multimap<String, String> returnParameterMap = ArrayListMultimap.create();
		pairWiseOperationSet = new HashSet<>();
		pairWiseReturnSet = new HashSet<>();

		int operationId = 0;

		Model.Api api = model.getApis(0);

		for (Model.Path path : api.getPathsMap().values()) {
			numberOfOperations += path.getMethodsCount();

			for (Model.Method method : path.getMethodsMap().values()) {
				operationId++; // necessary to generate (keys for the map if the operationId is undefined

				// RESPONSES
				// Generate returnParameterMap
				// MultiMap one operationId as key multiple responses as values
				for (Model.Response response : method.getResponsesList()) {
					// Get the first ContentMediaType -- maybe use a refinement and
					// a checker instead what the most suited mediaType should be used for
					// calculating this metric
					if (response.getContentMediaTypesCount() > 0) {
						Entry<String, ContentMediaType> entry = response.getContentMediaTypesMap().entrySet().iterator().next();
						Model.ContentMediaType mediaType = entry.getValue();
						Model.DataModel dataModel = mediaType.getDataModel();

						String operationKey = (method.getOperationId() != null) ? method.getOperationId()
								: OPERATION_STRING + Integer.valueOf(operationId);
						extractPropertiesFromMediaType(dataModel, operationKey, returnParameterMap);
					}
				}

				// INPUT PARAMS URL/RequestBody
				// Generate inputParameterMap
				// MultiMap one operationId as key multiple responses as values
				if (method.getRequestBodiesCount() > 0 && method.getRequestBodies(0).getContentMediaTypesCount() > 0) {
						Entry<String, ContentMediaType> entry = method.getRequestBodies(0).getContentMediaTypesMap()
								.entrySet().iterator().next();
						Model.ContentMediaType mediaType = entry.getValue();
						Model.DataModel dataModel = mediaType.getDataModel();

						String operationKey = (method.getOperationId() != null) ? method.getOperationId()
								: OPERATION_STRING + Integer.valueOf(operationId);
						extractPropertiesFromMediaType(dataModel, operationKey, inputParameterMap);
				}

				// extract (URL-parameters) into inputParameterMap
				for (String paramKey : method.getParametersMap().keySet()) {
					String operationKey = (method.getOperationId() != null) ? method.getOperationId()
							: OPERATION_STRING + Integer.valueOf(operationId);
					inputParameterMap.put(operationKey, paramKey);
				}
			}
		}

		// put all keys in an ArrayList, as we can't iterate using the index in multiMap
		List<String> operationKeyList = new ArrayList<>();
		for (Object key : inputParameterMap.keys()) {
			operationKeyList.add(key.toString());

		}

		List<String> returnKeyList = new ArrayList<>();
		for (Object key : returnParameterMap.keys()) {
			returnKeyList.add(key.toString());

		}

		// Compares all values from one key to all values from another key, and adds
		// these keys to a set, if at least one parameter is the same
		for (int i = 0; i < operationKeyList.size(); i++) {
			for (int k = i + 1; k < operationKeyList.size(); k++) {
				if (!operationKeyList.get(i).equals(operationKeyList.get(k))) {
					Collection<String> list1 = inputParameterMap.get(operationKeyList.get(i));
					Collection<String> list2 = inputParameterMap.get(operationKeyList.get(k));
					for (String stringFromFirstList : list1) {
						for (String stringFromSecondList : list2) {
							if (stringFromFirstList.equals(stringFromSecondList)) {
								pairWiseOperationSet
										.add("[" + operationKeyList.get(i) + ", " + operationKeyList.get(k) + "]");
							}
						}
					}
				}
			}
		}
		// Compares all values from one key to all values from another key, and adds
		// these keys to a set, if at least one return type is the same
		for (int i = 0; i < returnKeyList.size(); i++) {
			for (int k = i + 1; k < returnKeyList.size(); k++) {
				if (!returnKeyList.get(i).equals(returnKeyList.get(k))) {
					Collection<String> list1 = returnParameterMap.get(returnKeyList.get(i));
					Collection<String> list2 = returnParameterMap.get(returnKeyList.get(k));

					for (String stringFromFirstList : list1) {
						for (String stringFromSecondList : list2) {
							if (stringFromFirstList.equals(stringFromSecondList)) {
								pairWiseReturnSet.add("[" + returnKeyList.get(i) + ", " + returnKeyList.get(k) + "]");
							}
						}
					}
				}
			}
		}

		// Maximum number of operation pairs
		double numberOfAllPossibleCombinations = ((numberOfOperations - 1) * numberOfOperations) / 2.0;

		double parameterCardinality = pairWiseOperationSet.size();
		double returnCardinality = pairWiseReturnSet.size();

		// round values to 4 decimal places
		if (numberOfAllPossibleCombinations != 0) {
			double unRoundedMeasurementValue = (parameterCardinality + returnCardinality)
					/ (numberOfAllPossibleCombinations * 2);
			BigDecimal bd = new BigDecimal(Double.toString(unRoundedMeasurementValue));
			bd = bd.setScale(4, RoundingMode.HALF_UP);
			measurementValue = bd.doubleValue();
		} else {
			measurementValue = 0;
		}

	}

	private void extractPropertiesFromMediaType(Model.DataModel dataModel, String operationKey,
			Multimap<String, String> parameterMap) {
		if (!dataModel.getDataType().equals(Model.DataType.OBJECT)
				&& !dataModel.getDataType().equals(Model.DataType.ARRAY)) {
			// this case occurs when the return is only a SimpleType (String,Integer,...)
			parameterMap.put(operationKey, dataModel.getDataType().toString());
		} else {
			// For more complex types like Object/Array necessary to check if they are the
			// same object or if the array contain the same elements
			// Equal Objects have the same count of properties and same
			// properties(alphabetically sorted)
			StringBuilder objRepresentation = new StringBuilder();

			if (dataModel.getDataType().equals(Model.DataType.OBJECT)) {
				objRepresentation.append("Object-");
				// Append all Properties which are not inside allOf/anyOf/onlyOne
				objRepresentation.append(dataModel.getPropertiesCount());
				objRepresentation.append("-");
				dataModel.getPropertiesMap().keySet().stream().sorted()
						.forEach(prop -> objRepresentation.append(prop + "/"));
				// Append all Properties which are inside allOf/anyOf/OnlyOne
				if (dataModel.getDataModelsCount() > 0) {
					objRepresentation.append(dataModel.getDataModelsCount());
					objRepresentation.append(dataModel.getDataModelRelationShip().toString());
					objRepresentation.append("-");
					dataModel.getDataModelsMap().keySet().stream().sorted()
							.forEach(dM -> dataModel.getDataModelsMap().get(dM).getPropertiesMap().keySet().stream()
									.sorted().forEach(prop -> objRepresentation.append(prop + "/")));
				}
				parameterMap.put(operationKey, objRepresentation.toString());
			} else {
				// per metric definition only check for the inner type
				// and ignore the fact that it's inside an array !!!

				// Iterate through different Types inside Array, often there is only one Type
				// inside
				for (String innerArrayType : dataModel.getPropertiesMap().keySet()) {
					objRepresentation.append("Object-");
					// Append all Properties which are not inside allOf/anyOf/onlyOne
					objRepresentation.append(dataModel.getPropertiesMap().get(innerArrayType).getSubPropertiesCount());
					objRepresentation.append("-");
					dataModel.getPropertiesMap().get(innerArrayType).getSubPropertiesMap().keySet().stream().sorted()
							.forEach(prop -> objRepresentation.append(prop + "/"));

					// Append all Properties which are inside allOf/anyOf/OnlyOne
					// innerDataModel describes the Element which is inside the array
					Model.DataModel innerDataModel = dataModel.getPropertiesMap().get(innerArrayType).getDataModel();
					if (innerDataModel.getDataModelsCount() > 0) {
						objRepresentation.append(innerDataModel.getDataModelsCount());
						objRepresentation.append(innerDataModel.getDataModelRelationShip().toString());
						objRepresentation.append("-");
						innerDataModel.getDataModelsMap().keySet().stream().sorted()
								.forEach(dM -> innerDataModel.getDataModelsMap().get(dM).getPropertiesMap().keySet()
										.stream().sorted()
										.forEach(prop -> objRepresentation.append(prop + "/")));
					}
					parameterMap.put(operationKey, objRepresentation.toString());
				}
			}
		}
	}

	/**
	 * returns the value of the measurement.
	 */
	public double getMeasurementValue() {
		return this.measurementValue;
	}

	/**
	 * puts all information into Measurement
	 *
	 * @return Measurement
	 */
	public Measurement getMeasurement() {
		String additionalInformation = ("Metric value range: [0-1]; "
				+ "Set of pairwise operations with at least one common parameter: " + pairWiseOperationSet + "; "
				+ "Set of pairwise operations with common return type: " + pairWiseReturnSet + "; "
				+ "Number of operations: " + numberOfOperations);
		return new Measurement(this.getName(), measurementValue, additionalInformation);
	}

	/**
	 * returns the name of the metric
	 *
	 * @return metric name
	 */
	public String getName() {
		return this.metricName;
	}

	/**
	 * sets the Model for the empty constructor.
	 *
	 * @param model
	 */
	public void setModel(Model.SpecificationFile model) {
		this.model = model;
	}
}