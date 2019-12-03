package restful.api.metric.analyzer.cli.metrics;

import java.math.BigDecimal;
import java.math.RoundingMode;

import restful.api.metric.analyzer.cli.model.Measurement;
import restful.api.metric.analyzer.cli.model.generated.internal.Model;

public class AveragePathLength implements IMetric {

	private final  String metricName;
	private Model.SpecificationFile model;
	private double measurementValue;

	/**
	 * empty constructor for java.reflections
	 */
	public AveragePathLength() {
		metricName = "AveragePathLength";
	}

	/**
	 * constructor which loads the "InternaApilModel" root object
	 * Model.SpecifactionFile which contains the model of the API.
	 *
	 * @param specFile
	 */
	public AveragePathLength(Model.SpecificationFile specFile) {
		this();
		this.model = specFile;
	}

	/**
	 * calculates the APL value
	 */
	public void calculateMetric() {
		int totalPathLengthOfAllPaths = 0;
		Model.Api api = model.getApis(0);

		for (String pathName : api.getPathsMap().keySet()) {

			//add "/" as first char 
			pathName = (pathName.length() > 0 && !pathName.startsWith("/")) ?
					"/" + pathName :
						pathName;
			
			// removing attached /'s as they do not increase complexity
			while (pathName.endsWith("/") && pathName.length() > 1) {
				pathName = pathName.substring(0, pathName.length() - 1);
			}

			int pathLength = Math.toIntExact(pathName.chars().filter(c -> c == '/').count());
			totalPathLengthOfAllPaths += pathLength;
		}

		// round values to 4 decimal places
		if (api.getPathsCount() != 0) {
			double unRoundedMeasurementValue = (double) totalPathLengthOfAllPaths / (double) api.getPathsCount();
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

}