package restful.api.metric.analyzer.cli.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity(name = "Evaluation")
@Table(name = "evaluation")
public class Evaluation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "evaluationName")
	private String evaluationName;

	@Column(name = "serviceVersion")
	private String version;

	@Column(name = "serviceFileName")
	private String fileName;

	@Column(name = "apiFormat")
	private String apiFormat;

	@Column(name = "measurementDate")
	private String measurementDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "restfulService_id")
	private RestfulService restfulService;

	@OneToMany(mappedBy = "evaluation", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Measurement> measurementList = new ArrayList<Measurement>();

	/**
	 * Constructor for this class with evaluationName, serviceVersion,
	 * serviceFileName, apiFileUrl, apiFormat, measurementDate and a list of
	 * measurements.
	 *
	 * @param evaluationName
	 * @param version
	 * @param fileName
	 * @param apiFileUrl
	 * @param apiFormat
	 * @param measurementDate
	 * @param measurementList
	 */
	public Evaluation(String evaluationName, String version, String fileName, String apiFormat, String measurementDate,
			List<Measurement> measurementList) {
		this.evaluationName = evaluationName;
		this.version = version;
		this.fileName = fileName;
		this.apiFormat = apiFormat;
		this.measurementDate = measurementDate;
		this.measurementList = measurementList;
	}

	/**
	 * empty constructor
	 */
	public Evaluation() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEvaluationName() {
		return evaluationName;
	}

	public void setEvaluationName(String evaluationName) {
		this.evaluationName = evaluationName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getApiFormat() {
		return apiFormat;
	}

	public void setApiFormat(String apiFormat) {
		this.apiFormat = apiFormat;
	}

	public String getMeasurementDate() {
		return measurementDate;
	}

	public void setMeasurementDate(String measurementDate) {
		this.measurementDate = measurementDate;
	}

	@JsonIgnore
	public RestfulService getRestfulService() {
		return restfulService;
	}

	public void setRestfulService(RestfulService restfulService) {
		this.restfulService = restfulService;
	}

	public List<Measurement> getMeasurementList() {
		return measurementList;
	}

	public void setMeasurementList(List<Measurement> measurementList) {
		this.measurementList = measurementList;
		for (Measurement measurement : measurementList) {
			measurement.setEvaluation(this);
		}
	}

	public void addMeasurement(Measurement measurement) {
		this.measurementList.add(measurement);
		measurement.setEvaluation(this);
	}

}