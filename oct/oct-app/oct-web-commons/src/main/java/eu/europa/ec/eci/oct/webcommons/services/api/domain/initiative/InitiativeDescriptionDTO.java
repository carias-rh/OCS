package eu.europa.ec.eci.oct.webcommons.services.api.domain.initiative;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.stereotype.Component;
@Component
@XmlRootElement
public class InitiativeDescriptionDTO implements Serializable {

	private static final long serialVersionUID = -6542061393137842237L;

	private Long id;
	private String languageCode;
	private Boolean original;
	private String title;
	private String objectives;
	private String url;
	private String partialRegistration;

	public InitiativeDescriptionDTO(){}

	public Long getId(){
		return id;
	}

	public void setId(Long id){
		this.id = id;
	}

	public String getLanguageCode(){
		return this.languageCode;
	}

	public void setLanguageCode(String languageCode){
		this.languageCode = languageCode;
	}

	public Boolean isOriginal(){
		return original;
	}

	public void setOriginal(Boolean original){
		this.original = original;
	}

	public String getTitle(){
		return this.title;
	}

	public void setTitle(String title){
		this.title = title;
	}

	public String getPartialRegistration(){
		return this.partialRegistration;
	}

	public void setPartialRegistration(String partialRegistration){
		this.partialRegistration = partialRegistration;
	}

	public String getUrl(){
		return this.url;
	}

	public void setUrl(String url){
		this.url = url;
	}

	public String getObjectives(){
		return this.objectives;
	}

	public void setObjectives(String objectives){
		this.objectives = objectives;
	}

	@Override
	public String toString() {
		return "InitiativeDescriptionDTO{" +
				"id=" + id +
				", languageCode='" + languageCode + '\'' +
				", original=" + original +
				", title='" + title + '\'' +
				", objectives='" + objectives + '\'' +
				", url='" + url + '\'' +
				", partialRegistration='" + partialRegistration + '\'' +
				'}';
	}
}