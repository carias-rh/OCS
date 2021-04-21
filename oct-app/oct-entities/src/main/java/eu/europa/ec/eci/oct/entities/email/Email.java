package eu.europa.ec.eci.oct.entities.email;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name = "OCT_EMAIL")
public class Email implements Serializable {

	private static final long serialVersionUID = -8934069897181877623L;

	@GeneratedValue(generator = "IdGenerator", strategy = GenerationType.TABLE)
	@Id
	@TableGenerator(name = "IdGenerator", pkColumnValue = "email_id", table = "HIBERNATE_SEQUENCES", allocationSize = 1, pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
	@Column(name = "id")
	private Long id;

	@Column
	private String emailAddress;

	@Column
	private String comunicationLanguage;

	@Column
	private String signatureIdentifier;

	@Column
	private byte initiativeSubscription;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public byte getInitiativeSubscription() {
		return initiativeSubscription;
	}

	public void setInitiativeSubscription(byte initiativeSubscription) {
		this.initiativeSubscription = initiativeSubscription;
	}

	@Override
	public String toString() {
		return "Email [id=" + id + ", emailAddress=" + emailAddress + ", comunicationLanguage=" + comunicationLanguage
				+ ", signatureIdentifier=" + signatureIdentifier + ", initiativeSubscription=" + initiativeSubscription
				+ "]";
	}
}