package restful.api.metric.analyzer.cli.metrics;

import restful.api.metric.analyzer.cli.model.Measurement;
import restful.api.metric.analyzer.cli.model.generated.internal.Model;

public interface IMetric {

	/**
	 * calculates the metric value.
	 */
	void calculateMetric();

	/**
	 * puts all information into Measurement
	 *
	 * @return Measurement
	 */
	Measurement getMeasurement();

	/**
	 * sets the API model object for the empty constructor.
	 *
	 * @param model
	 */
	public void setModel(Model.SpecificationFile model);

	/**
	 * returns the value of the metric.
	 */
	double getMeasurementValue();

	/**
	 * returns the name of the metric.
	 *
	 * @return metric name
	 */
	String getName();

}