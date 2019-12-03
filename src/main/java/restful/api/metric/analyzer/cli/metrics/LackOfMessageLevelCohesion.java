package restful.api.metric.analyzer.cli.metrics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import restful.api.metric.analyzer.cli.model.Measurement;
import restful.api.metric.analyzer.cli.model.generated.internal.Model;
import restful.api.metric.analyzer.cli.model.generated.internal.Model.ContentMediaType;
import restful.api.metric.analyzer.cli.model.generated.internal.Model.Method;
import restful.api.metric.analyzer.cli.model.generated.internal.Model.Property;

public class LackOfMessageLevelCohesion implements IMetric {

	private final String metricName;
	private Model.SpecificationFile model;
	private double measurementValue;

	/**
	 * empty constructor for java.reflections
	 */
	public LackOfMessageLevelCohesion() {
		metricName = "LackOfMessageLevelCohesion";
	}

	public LackOfMessageLevelCohesion(Model.SpecificationFile specFile) {
		this();
		this.model = specFile;
	}

	@Override
	public void calculateMetric() {
		HashMap<String, Double> similarityOfOperation = new HashMap<>();
		HashMap<String, Model.Method> operationMap = new HashMap<>();
		Model.Api api = model.getApis(0);
		int operationId = 0;
		ArrayList<String> operationList = new ArrayList<>();

		for (Model.Path path : api.getPathsMap().values()) {
			operationId++; //necessary to generate (keys for the map if the operationId is undefined

			//retrieve all possible operation
			for (Model.Method method : path.getMethodsMap().values()) {
				String operationIdKey = method.getOperationId() != null ? method.getOperationId() : String.valueOf(operationId);
				operationMap.put(operationIdKey, method);
				operationList.add(operationIdKey);
			}
		}
		//Calculate similarities between each possible operation pair
		for (int i = 0; i < operationList.size(); i++) {
			for (int j = i + 1; j < operationList.size(); j++) {
				String operationPair = operationList.get(i) + "," + operationList.get(j);
				double messageSimilarity = calculateSimilarity(operationMap.get(operationList.get(i)), operationMap.get(operationList.get(j)));
				similarityOfOperation.put(operationPair, messageSimilarity);
			}
		}
		double similarityDiffTotal = 0;
		//Calc how much similarity is missing from the optimal value 1 for each operationPair
		//And summing all missing differences
		for (Entry<String, Double> entry : similarityOfOperation.entrySet()) {
			similarityDiffTotal += (1.0 - entry.getValue());
		}

		
		// round values to 4 decimal places
		if ( similarityOfOperation.size() != 0) {
			double unRoundedMeasurementValue  = similarityDiffTotal / (double) similarityOfOperation.size();
			BigDecimal bd = new BigDecimal(Double.toString(unRoundedMeasurementValue));
			bd = bd.setScale(4, RoundingMode.HALF_UP);
			measurementValue = bd.doubleValue();
		} else {
			measurementValue = 0;
		}

	}

	/**
	 * calculate Similarity of properties between two operations
	 * (count of identical inputProperties of OP1 and OP2) / total union of INPUT Properties of both operations
	 * +
	 * (count of identical returnProperties of OP1 and OP2) / total union of  RETURN Properties of both operations
	 * <p>
	 * similarity the sum of both divided by 2
	 * <p>
	 * Properties are identical if their name and type are the same
	 *
	 * @param method
	 * @param method2
	 * @return
	 */
	private double calculateSimilarity(Method method, Method method2) {
		//Calculate INPUT similarity

		//Retrieve all Properties which are Inside the two comparing operations 
		HashSet<String> inputPropertiesM1 = extractInputProperties(method);
		HashSet<String>	inputPropertiesM2 = extractInputProperties(method2);

		// Union
		HashSet<String> unionIn = new HashSet<>();
		unionIn.addAll(inputPropertiesM1);
		unionIn.addAll(inputPropertiesM2);

		// Common Properties
		int commonIn = 0;
		if (inputPropertiesM1.size() > inputPropertiesM2.size()) {
			for (String s : inputPropertiesM1) {
				if (inputPropertiesM2.contains(s)) {
					commonIn++;
				}
			}
		} else {
			for (String s : inputPropertiesM2) {
				if (inputPropertiesM1.contains(s)) {
					commonIn++;
				}
			}
		}
		double inputSimilarity = !unionIn.isEmpty() ? (double) commonIn / (double) unionIn.size() : 0;

		
		//CALCULATE RETURN similarity

		//Retrieve all Properties which are Inside the two comparing operations 
		HashSet<String> outPropertiesM1 = extractOutputProperties(method);
		HashSet<String> outPropertiesM2 = extractOutputProperties(method2);

		// Union
		HashSet<String> unionOut = new HashSet<>();
		unionOut.addAll(outPropertiesM1);
		unionOut.addAll(outPropertiesM2);

		// Common Properties
		int commonOut = 0;
		if (outPropertiesM1.size() > outPropertiesM2.size()) {
			for (String s : outPropertiesM1) {
				if (outPropertiesM2.contains(s)) {
					commonOut++;
				}
			}
		} else {
			for (String s : outPropertiesM2) {
				if (outPropertiesM1.contains(s)) {
					commonOut++;
				}
			}
		}

		double outSimilarity = !unionOut.isEmpty() ? (double) commonOut / (double) unionOut.size() : 0;
	
		return (inputSimilarity + outSimilarity) / 2;
	}

	private HashSet<String> extractOutputProperties(Method method) {
		//From all Responses
		HashSet<String> properties = new HashSet<>();
		for (Model.Response response : method.getResponsesList()) {
			if (response.getContentMediaTypesCount() > 0) {
				Entry<String, ContentMediaType> entry = response.getContentMediaTypesMap().entrySet().iterator().next();
				Model.ContentMediaType mediaType = entry.getValue();
				Model.DataModel dataModel = mediaType.getDataModel();
				extractPropAndCombiProp(properties, dataModel);
			}
		}
		return properties;
	}


	private HashSet<String> extractInputProperties(Method method) {
		HashSet<String> properties = new HashSet<>();
		//From ReqBody
		if (method.getRequestBodiesCount() > 0 && method.getRequestBodies(0).getContentMediaTypesCount() > 0) {
			Entry<String, ContentMediaType> entry = method.getRequestBodies(0).getContentMediaTypesMap().entrySet().iterator().next();
			Model.ContentMediaType mediaType = entry.getValue();
			Model.DataModel dataModel = mediaType.getDataModel();
			extractPropAndCombiProp(properties, dataModel);
		}
		//From URL-Param
		for (String propName : method.getParametersMap().keySet()) {
			properties.add(propName + method.getParametersMap().get(propName).getType().toString());
		}
		return properties;
	}

	/**
	 * extraction of properties and combined properties which are defined via allOf,oneOf,onlyOne
	 *
	 * @param properties
	 * @param dataModel
	 */
	private void extractPropAndCombiProp(HashSet<String> properties, Model.DataModel dataModel) {
		for (String propName : dataModel.getPropertiesMap().keySet()) {
			//extract properties
			properties.add(propName + dataModel.getPropertiesMap().get(propName).getType().toString());
			//extract all subProperties too if ComplexTypes
			if (dataModel.getPropertiesMap().get(propName).getType().equals(Model.DataType.OBJECT) ||
					dataModel.getPropertiesMap().get(propName).getType().equals(Model.DataType.ARRAY)) {
				extractSubProperties(dataModel.getPropertiesMap().get(propName).getSubPropertiesMap(), properties);
			}
		}
		//add all properties from allOf,anyOf,onlyOne too
		if (!dataModel.getDataModelRelationShip().equals(Model.DataModelRelationShip.UNDEFINED)) {
			for (Model.DataModel dM : dataModel.getDataModelsMap().values()) {
				extractPropAndCombiProp(properties, dM);
			}
		}
	}

	/**
	 * difference to extractPropAndCombiProp() is that this methods expect a subPropertiesMap instead of a dataModel
	 *
	 * @param subPropertiesMap
	 * @param properties
	 */
	private void extractSubProperties(Map<String, Property> subPropertiesMap, HashSet<String> properties) {
		for (Entry<String, Property> entry : subPropertiesMap.entrySet()) {
			
			properties.add(entry.getKey() + entry.getValue().getType().toString());
			if (entry.getValue().getType().equals(Model.DataType.OBJECT) && !properties.contains(entry.getKey())) {
				extractSubProperties(entry.getValue().getSubPropertiesMap(), properties);
			}
			//add all properties from allOf,anyOf,onlyOne of the subProperty
			if (!entry.getValue().getDataModel().getDataModelRelationShip().equals(Model.DataModelRelationShip.UNDEFINED)) {
				for (Model.DataModel dM : entry.getValue().getDataModel().getDataModelsMap().values()) {
					extractPropAndCombiProp(properties, dM);
				}
			}
		}
	}

	@Override
	public Measurement getMeasurement() {
		return new Measurement(this.getName(), measurementValue, null);
	}

	@Override
	public double getMeasurementValue() {
		return this.measurementValue;
	}

	@Override
	public String getName() {
		return this.metricName;
	}

	/**
	 * sets the Model for the empty constructor.
	 *
	 * @param model
	 */
	@Override
	public void setModel(Model.SpecificationFile model) {
		this.model = model;
	}
}