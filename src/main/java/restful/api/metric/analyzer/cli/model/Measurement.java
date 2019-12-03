package restful.api.metric.analyzer.cli.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Table measurement mapped to the table ApiAnalysis. Each measurement has the columns metricName,
 * metricValue and additionalInformation.
 *
 * @author Kai
 */

@Entity(name = "Measurement")
@Table(name = "measurement")
public class Measurement {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private String metricName;

	@Column
	private double metricValue;

	@Lob
	@Column
	private String additionalInformation;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "evaluation_id")
	private Evaluation evaluation;

	/**
	 * Constructor for this class with metricName, metricValue and additionalInformation.
	 *
	 * @param metricName
	 * @param metricValue
	 * @param additionalInformation
	 */
	public Measurement(String metricName, double metricValue, String additionalInformation) {
		this.metricName = metricName;
		this.metricValue = metricValue;
		this.additionalInformation = additionalInformation;
	}

	/**
	 * empty constructor
	 */
	public Measurement() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMetricName() {
		return metricName;
	}

	public void setMetricName(String metricName) {
		this.metricName = metricName;
	}

	public double getMetricValue() {
		return metricValue;
	}

	public void setMetricValue(double metricValue) {
		this.metricValue = metricValue;
	}

	public String getAdditionalInformation() {
		return additionalInformation;
	}

	public void setAdditionalInformation(String additionalInformation) {
		this.additionalInformation = additionalInformation;
	}

	@JsonIgnore
	public Evaluation getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(Evaluation evaluation) {
		this.evaluation = evaluation;
	}

}