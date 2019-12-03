package restful.api.metric.analyzer.cli.metrics;

import java.math.BigDecimal;
import java.math.RoundingMode;

import restful.api.metric.analyzer.cli.model.Measurement;
import restful.api.metric.analyzer.cli.model.generated.internal.Model;

public class ArgumentsPerOperation implements IMetric {

	private final String metricName;
	private Model.SpecificationFile model;
	private double measurementValue;

	/**
	 * empty constructor for java.reflections
	 */
	public ArgumentsPerOperation() {
		metricName = "ArgumentsPerOperation";
	}

	/**
	 * constructor which loads the "InternaApilModel" root object
	 * Model.SpecifactionFile which contains the model of the API.
	 *
	 * @param specFile
	 */
	public ArgumentsPerOperation(Model.SpecificationFile specFile) {
		this();
		this.model = specFile;
	}

	/**
	 * calculates the APO value
	 */
	public void calculateMetric() {
		int totalNumberOfOperations = 0;
		int totalNumberOfParameters = 0;
		Model.Api api = model.getApis(0);

		for(Model.Path path : api.getPathsMap().values()) {
			totalNumberOfOperations += path.getMethodsCount();
			for(Model.Method method: path.getMethodsMap().values()) {
				
				totalNumberOfParameters += method.getParametersCount();
				
				if (method.getRequestBodiesCount() > 0) {
					totalNumberOfParameters++;
				}
			}
		}
		
		// round values to 4 decimal places
		if (totalNumberOfOperations != 0) {
			double unRoundedMeasurementValue =  (double) totalNumberOfParameters / (double) totalNumberOfOperations;
			  BigDecimal bd = new BigDecimal(Double.toString(unRoundedMeasurementValue));
			  bd = bd.setScale(4, RoundingMode.HALF_UP);
			  measurementValue = bd.doubleValue();
		} else {
			measurementValue = 0;
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
		return metricName;
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

}