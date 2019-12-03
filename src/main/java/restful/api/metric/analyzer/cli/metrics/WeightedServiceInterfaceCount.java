package restful.api.metric.analyzer.cli.metrics;

import restful.api.metric.analyzer.cli.model.Measurement;
import restful.api.metric.analyzer.cli.model.generated.internal.Model;

public class WeightedServiceInterfaceCount implements IMetric {

	private final String metricName;
	private Model.SpecificationFile model;
	private int measurementValue;

	/**
	 * empty constructor for java.reflections
	 */
	public WeightedServiceInterfaceCount() {
		metricName = "WeightedServiceInterfaceCount";
	}

	/**
	 * constructor which loads the "InternaApilModel" root object
	 * Model.SpecifactionFile which contains the model of the API.
	 *
	 * @param specFile
	 */
	public WeightedServiceInterfaceCount(Model.SpecificationFile specFile) {
		this();
		this.model = specFile;
	}


	/**
	 * calculates the number of different operations in the specification.
	 */
	public void calculateMetric() {
		Model.Api api = model.getApis(0);

		for (Model.Path path : api.getPathsMap().values()) {
			measurementValue += path.getMethodsCount();
		}
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
		String additionalInformation = ("Number of different operations: " + measurementValue);
		return new Measurement(this.getName(), measurementValue, additionalInformation);
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