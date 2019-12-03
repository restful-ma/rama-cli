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

@Entity(name = "RestfulService")
@Table(name = "restfulService")
public class RestfulService {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "serviceTitle")
	private String serviceTitle;

	@Column(name = "apiFileUrl")
	private String apiFileUrl;

	@Column(name = "apiFileString", length = 2500000)
	private String apiFileString;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "restfulSystem_id")
	private RestfulSystem restfulSystem;

	@OneToMany(mappedBy = "restfulService", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Evaluation> evaluationList = new ArrayList<Evaluation>();

	/**
	 * constructor for this class with serviceTitle and a list of evaluations
	 *
	 * @param serviceTitle
	 * @param evaluationList
	 */
	public RestfulService(String serviceTitle, List<Evaluation> evaluationList) {
		this.serviceTitle = serviceTitle;
		this.evaluationList = evaluationList;
	}

	/**
	 * empty constructor
	 */
	public RestfulService() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getServiceTitle() {
		return serviceTitle;
	}

	public void setServiceTitle(String serviceTitle) {
		this.serviceTitle = serviceTitle;
	}

	public String getApiFileString() {
		return apiFileString;
	}

	public void setApiFileString(String apiFileString) {
		this.apiFileString = apiFileString;
	}

	public String getApiFileUrl() {
		return apiFileUrl;
	}

	public void setApiFileUrl(String apiFileUrl) {
		this.apiFileUrl = apiFileUrl;
	}

	@JsonIgnore
	public RestfulSystem getRestfulSystem() {
		return restfulSystem;
	}

	public void setRestfulSystem(RestfulSystem restfulSystem) {
		this.restfulSystem = restfulSystem;
	}

	public List<Evaluation> getEvaluationList() {
		return evaluationList;
	}

	public void setEvaluationList(List<Evaluation> evaluationList) {
		this.evaluationList = evaluationList;
		for (Evaluation evaluation : evaluationList) {
			evaluation.setRestfulService(this);
		}
	}

	public void addEvaluation(Evaluation evaluation) {
		this.evaluationList.add(evaluation);
		evaluation.setRestfulService(this);
	}

}