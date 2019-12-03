package restful.api.metric.analyzer.cli.metrics;

import restful.api.metric.analyzer.cli.model.Measurement;
import restful.api.metric.analyzer.cli.model.generated.internal.Model;

public class LongestPath implements IMetric {

	private final String metricName;
	private Model.SpecificationFile model;
	private double measurementValue;

	private int longestPathLength;
	private String longestPathString;

	/**
	 * empty constructor for java.reflections
	 */
	public LongestPath() {
		metricName = "LongestPath";
	}

	/**
	 * constructor which loads the "InternaApilModel" root object
	 * Model.SpecifactionFile which contains the model of the API.
	 *
	 * @param specFile
	 */
	public LongestPath(Model.SpecificationFile specFile) {
		this();
		this.model = specFile;
	}

	/**
	 * calculates the length of the longest path
	 */
	public void calculateMetric() {
		Model.Api api = model.getApis(0);
		longestPathLength = 0;
		for (Model.Path path : api.getPathsMap().values()) {
			//add "/" as first char 
			String pathString = (path.getPathName().length() > 0 && !path.getPathName().startsWith("/")) ?
					"/" + path.getPathName() :
						 path.getPathName();
					

			// removing attached /'s as they do not increase complexity
			while (pathString.endsWith("/") && pathString.length() > 1) {
				pathString = pathString.substring(0, pathString.length() - 1);
			}

			String pathStringHelp = pathString.replace("/", "");

			int pathLength = pathString.length() - pathStringHelp.length();

			if (pathLength > longestPathLength) {
				longestPathLength = pathLength;
				longestPathString = pathString;
			}
		}
		measurementValue = longestPathLength;
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
		String additionalInformation = ("Longest path: " + longestPathString +", Length: " + longestPathLength);
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