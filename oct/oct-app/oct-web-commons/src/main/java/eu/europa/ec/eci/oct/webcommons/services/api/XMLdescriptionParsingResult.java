package eu.europa.ec.eci.oct.webcommons.services.api;

import java.util.List;

import eu.europa.ec.eci.oct.webcommons.services.api.domain.contact.ContactDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.initiative.InitiativeDescriptionDTO;

public class XMLdescriptionParsingResult {

	private List<ContactDTO> contacts;
	private List<InitiativeDescriptionDTO> descriptions;
	private String ecRegistrationNumber;
	private String ecRegisterUrl;
	private String registrationDate;
    private String deadline;

	public List<ContactDTO> getContacts() {
		return contacts;
	}

	public void setContacts(List<ContactDTO> contacts) {
		this.contacts = contacts;
	}

	public List<InitiativeDescriptionDTO> getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(List<InitiativeDescriptionDTO> descriptions) {
		this.descriptions = descriptions;
	}

	public String getEcRegistrationNumber() {
		return ecRegistrationNumber;
	}

	public void setEcRegistrationNumber(String ecRegistrationNumber) {
		this.ecRegistrationNumber = ecRegistrationNumber;
	}

	public String getEcRegisterUrl() {
		return ecRegisterUrl;
	}

	public void setEcRegisterUrl(String ecRegisterUrl) {
		this.ecRegisterUrl = ecRegisterUrl;
	}

	public String getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(String registrationDate) {
		this.registrationDate = registrationDate;
	}

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    @Override
    public String toString() {
        return "XMLdescriptionParsingResult{" +
                "contacts=" + contacts +
                ", descriptions=" + descriptions +
                ", ecRegistrationNumber='" + ecRegistrationNumber + '\'' +
                ", ecRegisterUrl='" + ecRegisterUrl + '\'' +
                ", registrationDate='" + registrationDate + '\'' +
                ", deadline='" + deadline + '\'' +
                '}';
    }
}
