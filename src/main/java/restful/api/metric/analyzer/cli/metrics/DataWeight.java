package restful.api.metric.analyzer.cli.metrics;

import java.util.Map.Entry;

import restful.api.metric.analyzer.cli.model.Measurement;
import restful.api.metric.analyzer.cli.model.generated.internal.Model;
import restful.api.metric.analyzer.cli.model.generated.internal.Model.ContentMediaType;

public class DataWeight implements IMetric {

	private final String metricName;
	private Model.SpecificationFile model;
	private double measurementValue;

	private int totalNumberOfInputParameters;
	private int totalNumberOfInputBodyParameters;
	private int totalNumberOfResponseParameters;

	/**
	 * empty constructor for java.reflections
	 */
	public DataWeight() {
		metricName = "DataWeight";
	}

	/**
	 * constructor which loads the "InternaApilModel" root object
	 * Model.SpecifactionFile which contains the model of the API.
	 *
	 * @param specFile
	 */
	public DataWeight(Model.SpecificationFile specFile) {
		this();
		this.model = specFile;
	}

	/**
	 * calculates the DW value
	 */
	public void calculateMetric() {
		totalNumberOfInputBodyParameters = 0;
		totalNumberOfInputParameters = 0;
		totalNumberOfResponseParameters = 0;

		Model.Api api = model.getApis(0);

		for (Model.Path path : api.getPathsMap().values()) {

			for (Model.Method method : path.getMethodsMap().values()) {

				// Count URL parameters
				totalNumberOfInputParameters += method.getParametersCount();

				// Count REQUEST parameters
				if (!method.getRequestBodiesList().isEmpty()
						&& method.getRequestBodies(0).getContentMediaTypesCount() > 0) {

					Model.RequestBody reqBody = method.getRequestBodies(0);

					// Get the first ContentMediaType -- maybe use a refinement and
					// a checker instead what the most suited mediaType should be used for
					// calculating this metric
					Entry<String, ContentMediaType> entry = reqBody.getContentMediaTypesMap().entrySet().iterator().next();
					Model.ContentMediaType mediaType = entry.getValue();
					Model.DataModel dataModel = mediaType.getDataModel();

					totalNumberOfInputBodyParameters += dataModel.getPropertiesCount();

					addUpInputParameters(dataModel);

					if (dataModel.getDataModelsCount() > 0) {
						for (Model.DataModel dM : dataModel.getDataModelsMap().values()) {
							totalNumberOfResponseParameters += dM.getPropertiesCount();
						}
					}
				}

				// Count RESPONSE parameters
				for (Model.Response response : method.getResponsesList()) {
					if (response.getContentMediaTypesCount() > 0) {
						// Get the first ContentMediaType -- maybe use a refinement and
						// a checker instead what the most suited mediaType should be used for
						// calculating this metric
						Entry<String, ContentMediaType> entry = response.getContentMediaTypesMap().entrySet().iterator().next();
						Model.ContentMediaType mediaType = entry.getValue();
						Model.DataModel dataModel = mediaType.getDataModel();

						totalNumberOfResponseParameters += dataModel.getPropertiesCount();

						addUpInputParameters(dataModel);

						if (dataModel.getDataModelsCount() > 0) {
							for (Model.DataModel dM : dataModel.getDataModelsMap().values()) {
								totalNumberOfResponseParameters += dM.getPropertiesCount();
							}
						}
					}
				}
			}
		}

		measurementValue = (double) totalNumberOfInputBodyParameters + (double) totalNumberOfInputParameters
				+ (double) totalNumberOfResponseParameters;

	}

	/**
	 * returns the value of the measurement.
	 */
	public double getMeasurementValue() {
		return this.measurementValue;
	}

	/**
	 * returns the name of the metric.
	 *
	 * @return metric name.
	 */
	public String getName() {
		return this.metricName;
	}

	/**
	 * puts all information into Measurement
	 *
	 * @return Measurement
	 */
	public Measurement getMeasurement() {

		return new Measurement(this.getName(), measurementValue, null);
	}

	/**
	 * sets the Model for the empty constructor.
	 *
	 * @param model
	 */
	public void setModel(Model.SpecificationFile model) {
		this.model = model;
	}

	private void addUpInputParameters(Model.DataModel dataModel) {
		// Array/Object total input parameters
		for (Model.Property prop : dataModel.getPropertiesMap().values()) {
			//Don't count array/object by itself only the properties inside
			if (prop.getType().equals(Model.DataType.ARRAY)) {
				totalNumberOfResponseParameters -= 1;
			}
			if (prop.getType().equals(Model.DataType.OBJECT)) {
				totalNumberOfResponseParameters -= 1;
			}
			totalNumberOfResponseParameters += prop.getSubPropertiesCount();

			for (Model.DataModel dM : prop.getDataModel().getDataModelsMap().values()) {
				totalNumberOfResponseParameters += dM.getPropertiesCount();
			}
		}
	}
}