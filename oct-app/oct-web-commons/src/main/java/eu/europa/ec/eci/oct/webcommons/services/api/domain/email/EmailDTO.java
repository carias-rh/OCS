package eu.europa.ec.eci.oct.webcommons.services.api.domain.email;

import java.io.Serializable;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class EmailDTO implements Serializable {

	private static final long serialVersionUID = 4616664407521391601L;

	public EmailDTO() {
	}

	private String emailAddress;
	private String comunicationLanguage;
	private String signatureIdentifier;
	private Long emailId;
	private Boolean initiativeSubscription;

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getComunicationLanguage() {
		return comunicationLanguage;
	}

	public void setComunicationLanguage(String comunicationLanguage) {
		this.comunicationLanguage = comunicationLanguage;
	}

	public String getSignatureIdentifier() {
		return signatureIdentifier;
	}

	public void setSignatureIdentifier(String signatureIdentifier) {
		this.signatureIdentifier = signatureIdentifier;
	}

	public Long getEmailId() {
		return emailId;
	}

	public void setEmailId(Long emailId) {
		this.emailId = emailId;
	}

	public Boolean getInitiativeSubscription() {
		return initiativeSubscription;
	}

	public void setInitiativeSubscription(Boolean initiativeSubscription) {
		this.initiativeSubscription = initiativeSubscription;
	}

	@Override
	public String toString() {
		return "EmailDTO [emailAddress=" + emailAddress + ", comunicationLanguage=" + comunicationLanguage
				+ ", signatureIdentifier=" + signatureIdentifier + ", emailId=" + emailId + ", initiativeSubscription="
				+ initiativeSubscription + "]";
	}

}
