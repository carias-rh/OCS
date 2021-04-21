package eu.europa.ec.eci.oct.webcommons.services.api.domain.initiative;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class InitiativeDescriptionsDTO implements Serializable{
	
	private static final long serialVersionUID = -3064851328742734946L;

	public InitiativeDescriptionsDTO(){}

	private InitiativeInfoDTO initiativeInfo;
	private List<InitiativeDescriptionDTO> descriptions;

	public InitiativeInfoDTO getInitiativeInfo() {
		return initiativeInfo;
	}

	public void setInitiativeInfo(InitiativeInfoDTO initiativeInfo) {
		this.initiativeInfo = initiativeInfo;
	}

	public List<InitiativeDescriptionDTO> getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(List<InitiativeDescriptionDTO> descriptions) {
		this.descriptions = descriptions;
	}

	public void addDescription(InitiativeDescriptionDTO initiativeDescriptionDTO) {
		if (this.descriptions == null) {
			this.descriptions = new ArrayList<InitiativeDescriptionDTO>();
		}
		this.descriptions.add(initiativeDescriptionDTO);
	}

	@Override
	public String toString() {
		return "InitiativeDescriptionsDTO{" +
				"initiativeInfo=" + initiativeInfo +
				", descriptions=" + descriptions +
				'}';
	}
}
