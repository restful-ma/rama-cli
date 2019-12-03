package restful.api.metric.analyzer.cli.metrics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Map.Entry;

import restful.api.metric.analyzer.cli.model.Measurement;
import restful.api.metric.analyzer.cli.model.generated.internal.Model;
import restful.api.metric.analyzer.cli.model.generated.internal.Model.ContentMediaType;

public class DistinctMessageRatio implements IMetric {

	private final String metricName;
	private Model.SpecificationFile model;
	private double measurementValue;

	/**
	 * empty constructor for java.reflections
	 */
	public DistinctMessageRatio() {
		metricName = "DistinctMessageRatio";
	}

	public DistinctMessageRatio(Model.SpecificationFile specFile) {
		this();
		this.model = specFile;
	}

	@Override
	public void calculateMetric() {
		int totalInputOutputCount = 0;
		HashSet<String> uniqueInputMessages = new HashSet<>();
		HashSet<String> uniqueReturnMessages = new HashSet<>();


		Model.Api api = model.getApis(0);

		for (Model.Path path : api.getPathsMap().values()) {

			for (Model.Method method : path.getMethodsMap().values()) {
				totalInputOutputCount += method.getResponsesCount();
				// Generate uniqueRETURNMessages
				for (Model.Response response : method.getResponsesList()) {
					if (response.getContentMediaTypesCount() > 0) {
						Entry<String, ContentMediaType> entry = response.getContentMediaTypesMap().entrySet().iterator().next();
						Model.ContentMediaType mediaType = entry.getValue();
						Model.DataModel dataModel = mediaType.getDataModel();
						String urlParams = ""; //urlParams aren't part of the returnMessage therefore empty string
						extractPropertiesAsUniqueStrings(dataModel, uniqueReturnMessages, urlParams);
					}
				}

				// Generate uniqueINPUTMessages
				// URL-Parameter and RequestBody Parameter are combined ! per Metric interpretation
				if (method.getRequestBodiesCount() > 0) {
					totalInputOutputCount += 1;
					if (method.getRequestBodies(0).getContentMediaTypesCount() > 0) {
						Entry<String, ContentMediaType> entry = method.getRequestBodies(0).getContentMediaTypesMap().entrySet().iterator().next();
						Model.ContentMediaType mediaType = entry.getValue();
						Model.DataModel dataModel = mediaType.getDataModel();

						StringBuilder urlParamsBuilder = new StringBuilder();
						method.getParametersMap().keySet().stream().sorted().forEach(urlParamsBuilder::append);
						extractPropertiesAsUniqueStrings(dataModel, uniqueInputMessages, urlParamsBuilder.toString());
					}
				} else {
					//Sometimes Operations have no RequestBody -> only use URL-Parameter as uniqueINPUTMessages
					if (method.getParametersCount() > 0) {
						totalInputOutputCount += 1;
						StringBuilder urlParamsBuilder = new StringBuilder();
						method.getParametersMap().keySet().stream().sorted().forEach(urlParamsBuilder::append);
						uniqueInputMessages.add(urlParamsBuilder.toString());
					}
				}
			}
		}

		int distinctMessages = uniqueInputMessages.size() + uniqueReturnMessages.size();
		
		// round values to 4 decimal places
		if (totalInputOutputCount != 0) {
			double unRoundedMeasurementValue = (double) distinctMessages / (double) totalInputOutputCount;
			  BigDecimal bd = new BigDecimal(Double.toString(unRoundedMeasurementValue));
			  bd = bd.setScale(4, RoundingMode.HALF_UP);
			  measurementValue = bd.doubleValue();
		} else {
			measurementValue = 0;
		}

	}

	private void extractPropertiesAsUniqueStrings(Model.DataModel dataModel, HashSet<String> uniqueMessages, String urlParams) {
		if (!dataModel.getDataType().equals(Model.DataType.OBJECT) && !dataModel.getDataType().equals(Model.DataType.ARRAY)) {
			//this case occurs when the return is only a SimpleType (String,Integer,...)
			uniqueMessages.add(dataModel.getDataType().toString());
		} else {
			//For more complex types like Object/Array necessary to check  if they are the same object or if the array contain the same elements
			//Equal Objects have the same count of properties and same properties(alphabetically sorted)
			//Equal Objects also have the same "elements" inside their allOf,AnyOf,OnlyOne
			StringBuilder objRepresentation = new StringBuilder();

			if (dataModel.getDataType().equals(Model.DataType.OBJECT)) {
				objRepresentation.append("Object-");
				//Append all Properties which are not inside allOf/anyOf/onlyOne
				objRepresentation.append(dataModel.getPropertiesCount());
				objRepresentation.append("-");
				dataModel.getPropertiesMap().keySet().stream().sorted().forEach(prop -> objRepresentation.append(prop + "/"));
				//Append all Properties which are inside allOf/anyOf/OnlyOne
				objRepresentation.append(dataModel.getDataModelsCount());
				objRepresentation.append(dataModel.getDataModelRelationShip().toString());
				objRepresentation.append("-");
				dataModel.getDataModelsMap().keySet().stream().sorted().forEach(dM ->
						dataModel.getDataModelsMap().get(dM).getPropertiesMap().keySet().stream().sorted().forEach(prop -> objRepresentation.append(prop + "/"))
				);

				uniqueMessages.add(objRepresentation.append(urlParams).toString());
			} else {
				//for each type thats inside an array
				objRepresentation.append("Array-");
				for (String innerArrayType : dataModel.getPropertiesMap().keySet()) {
					//Append all Properties which are not inside allOf/anyOf/onlyOne
					objRepresentation.append(dataModel.getPropertiesMap().get(innerArrayType).getSubPropertiesCount());
					objRepresentation.append("-");
					dataModel.getPropertiesMap().get(innerArrayType).getSubPropertiesMap().keySet().stream().sorted().forEach(prop -> objRepresentation.append(prop + "/"));
					// Append all Properties which are inside allOf/anyOf/OnlyOne
					// innerDatamModel describes the Element which is inside the array
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
					uniqueMessages.add(objRepresentation.append(urlParams).toString());
				}
			}
		}
	}

	/**
	 * puts all information into Measurement
	 *
	 * @return Measurement
	 */
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