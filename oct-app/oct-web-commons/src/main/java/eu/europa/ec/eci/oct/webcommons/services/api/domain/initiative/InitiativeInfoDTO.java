package eu.europa.ec.eci.oct.webcommons.services.api.domain.initiative;

import java.io.Serializable;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import eu.europa.ec.eci.oct.webcommons.services.api.domain.contact.ContactDTO;

@Component
@Scope("prototype")
public class InitiativeInfoDTO implements Serializable {

	private static final long serialVersionUID = -117968694739726135L;

	public InitiativeInfoDTO() {
	}

	private String registrationNumber;
	private String closingDate;
	private String registrationDate;
	private String url;
	private List<ContactDTO> organizers;
	private boolean isPartiallyRegistered;
	private String adminUsername;


	public String getRegistrationNumber() {
		return registrationNumber;
	}

	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}

	public String getClosingDate() {
		return closingDate;
	}

	public void setClosingDate(String closingDate) {
		this.closingDate = closingDate;
	}

	public String getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(String registrationDate) {
		this.registrationDate = registrationDate;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<ContactDTO> getOrganizers() {
		return organizers;
	}

	public void setOrganizers(List<ContactDTO> organizers) {
		this.organizers = organizers;
	}

	public boolean isPartiallyRegistered() {
		return isPartiallyRegistered;
	}

	public void setPartiallyRegistered(boolean isPartiallyRegistered) {
		this.isPartiallyRegistered = isPartiallyRegistered;
	}

	public String getAdminUsername() {
		return adminUsername;
	}

	public void setAdminUsername(String adminUsername) {
		this.adminUsername = adminUsername;
	}

	@Override
	public String toString() {
		return "InitiativeInfoDTO{" +
				"registrationNumber='" + registrationNumber + '\'' +
				", closingDate='" + closingDate + '\'' +
				", registrationDate='" + registrationDate + '\'' +
				", url='" + url + '\'' +
				", organizers=" + organizers +
				", isPartiallyRegistered=" + isPartiallyRegistered +
				", adminUsername='" + adminUsername + '\'' +
				'}';
	}
}
