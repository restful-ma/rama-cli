package restful.api.metric.analyzer.cli.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity(name = "RestfulSystem")
@Table(name = "restfulSystem")
public class RestfulSystem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "systemName")
	private String systemName;

	@OneToMany(mappedBy = "restfulSystem", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<RestfulService> restfulServiceList = new ArrayList<RestfulService>();

	/**
	 * Constructor of this class with systemName and a list of RestfulServices.
	 *
	 * @param systemName
	 * @param restfulServiceList
	 */
	public RestfulSystem(String systemName, List<RestfulService> restfulServiceList) {
		this.systemName = systemName;
		this.restfulServiceList = restfulServiceList;
	}

	/**
	 * empty constructor
	 */
	public RestfulSystem() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public List<RestfulService> getRestfulServiceList() {
		return restfulServiceList;
	}

	public void setRestfulServiceList(List<RestfulService> restfulServiceList) {
		this.restfulServiceList = restfulServiceList;
		for (RestfulService restfulService : restfulServiceList) {
			restfulService.setRestfulSystem(this);
		}
	}

	public void addService(RestfulService restfulService) {
		this.restfulServiceList.add(restfulService);
		restfulService.setRestfulSystem(this);
	}

}