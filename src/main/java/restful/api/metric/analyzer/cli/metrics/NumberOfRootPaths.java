package restful.api.metric.analyzer.cli.metrics;

import java.util.HashSet;

import restful.api.metric.analyzer.cli.model.Measurement;
import restful.api.metric.analyzer.cli.model.generated.internal.Model;
import restful.api.metric.analyzer.cli.util.PathOperationUtils;

public class NumberOfRootPaths implements IMetric {

	private final String metricName;
	private Model.SpecificationFile model;
	private double measurementValue;

	private HashSet<String> rootPaths;

	/**
	 * empty constructor for java.reflections
	 */
	public NumberOfRootPaths() {
		metricName = "NumberOfRootPaths";
	}

	/**
	 * constructor which loads the "InternaApilModel" root object
	 * Model.SpecifactionFile which contains the model of the API.
	 *
	 * @param specFile
	 */
	public NumberOfRootPaths(Model.SpecificationFile specFile) {
		this();
		this.model = specFile;
	}

	/**
	 * calculates the number of root path resources
	 */
	public void calculateMetric() {
		rootPaths = new HashSet<>();

		Model.Api api = model.getApis(0);

		// add methodCount of each path to it's path root
		for (Model.Path path : api.getPathsMap().values()) {
			rootPaths.add(PathOperationUtils.getRootFromPath(path.getPathName()));
		}

		measurementValue = rootPaths.size();
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
		StringBuilder additionalInformation = new StringBuilder( "all root paths: ");
		int counter = 0;
		// adding all root paths to string
		for (String s : rootPaths) {
			counter++;
			if (counter == rootPaths.size()) {
				additionalInformation.append(s);
			} else {
				additionalInformation.append(s).append(',');
			}
		}

		return new Measurement(this.getName(), measurementValue, additionalInformation.toString());
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