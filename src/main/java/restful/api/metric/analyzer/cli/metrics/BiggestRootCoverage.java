package restful.api.metric.analyzer.cli.metrics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map.Entry;

import restful.api.metric.analyzer.cli.model.Measurement;
import restful.api.metric.analyzer.cli.model.generated.internal.Model;
import restful.api.metric.analyzer.cli.util.PathOperationUtils;

public class BiggestRootCoverage implements IMetric {

	private final String metricName;
	private Model.SpecificationFile model;
	private double measurementValue;

	private String longestRootName;
	private int maxNumberOfOperations = 0;
	private int totalNumberOfOperations = 0;

	/**
	 * empty constructor for java.reflections
	 */
	public BiggestRootCoverage() {
		metricName = "BiggestRootCoverage";
	}

	/**
	 * constructor which loads the "InternaApilModel" root object
	 * Model.SpecifactionFile which contains the model of the API.
	 *
	 * @param specFile
	 */
	public BiggestRootCoverage(Model.SpecificationFile specFile) {
		this();
		this.model = specFile;
	}

	/**
	 * calculates the BRC value
	 */
	public void calculateMetric() {
		HashMap<String, Integer> rootPaths = new HashMap<>();
		Model.Api api = model.getApis(0);

		// add methodCount of each path to it's path root
		for (Model.Path path : api.getPathsMap().values()) {
			//add "/" as first char 
			String pathString = (path.getPathName().length() > 0 && !path.getPathName().startsWith("/")) ?
					"/" + path.getPathName() :
						 path.getPathName();
					
			String rootName = PathOperationUtils.getRootFromPath(pathString);

			// check how many methods this path has and add it to the belonging root
			int methodCounter = rootPaths.containsKey(rootName) ? rootPaths.get(rootName) + path.getMethodsCount()
					: path.getMethodsCount();
			rootPaths.put(rootName, methodCounter);

			totalNumberOfOperations += path.getMethodsCount();
		}

		// find out which rootName has the most operations

		if (!rootPaths.values().isEmpty()) {
			maxNumberOfOperations = rootPaths.values().stream().mapToInt(i -> i).max().getAsInt();
		}

		// Get the longest rootName to use inside the additional Informations
		int temp = 0;
		for (Entry<String, Integer> entry : rootPaths.entrySet()) {
			if (entry.getValue() > temp) {
				longestRootName = entry.getKey();
				temp = entry.getValue();
			}
		}

		// round values to 4 decimal places
		if (totalNumberOfOperations != 0) {
			double unRoundedMeasurementValue = (double) maxNumberOfOperations / (double) totalNumberOfOperations;
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
		String additionalInformation = ("Biggest root coverage: " + longestRootName + " with " + maxNumberOfOperations
				+ " operation(s) from overall " + totalNumberOfOperations + " operation(s)");
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