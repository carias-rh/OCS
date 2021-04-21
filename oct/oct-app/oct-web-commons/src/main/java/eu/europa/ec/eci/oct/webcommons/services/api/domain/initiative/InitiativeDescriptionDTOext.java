package eu.europa.ec.eci.oct.webcommons.services.api.domain.initiative;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.stereotype.Component;

@Component
@XmlRootElement
public class InitiativeDescriptionDTOext implements Serializable{
	
	private static final long serialVersionUID = -6557565317222356978L;

	public InitiativeDescriptionDTOext() {
	}

	private InitiativeDescriptionDTO initiativeDescription;
	private InitiativeInfoDTO initiativeInfo;

	public InitiativeDescriptionDTO getInitiativeDescription() {
		return initiativeDescription;
	}

	public void setInitiativeDescription(InitiativeDescriptionDTO initiativeDescription) {
		this.initiativeDescription = initiativeDescription;
	}

	public InitiativeInfoDTO getInitiativeInfo() {
		return initiativeInfo;
	}

	public void setInitiativeInfo(InitiativeInfoDTO initiativeInfo) {
		this.initiativeInfo = initiativeInfo;
	}

	@Override
	public String toString() {
		return "InitiativeDescritptionDTOext [initiativeDescription=" + initiativeDescription
				+ ", initiativeInfo=" + initiativeInfo + "]";
	}

}
