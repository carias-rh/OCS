package eu.europa.ec.eci.oct.entities.admin;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "OCT_SYSTEM_PREFS")
@Cacheable(false)
public class SystemPreferences implements Serializable {

	private static final long serialVersionUID = 3521715338067950157L;

	@Id
	private Long id;

	@Enumerated(EnumType.STRING)
	private SystemState state;

	@Column
	private byte collecting;

	@Column(unique = true, length = 64)
	private String registrationNumber;

	@Column(unique = true)
	private Date registrationDate;

	@Column(length = 500)
	private String commissionRegisterUrl;

	@Column
	private Date deadline;

	@Column(name = "CERT_FILE_NAME", unique = true, nullable = true, length = 255)
	private String certFileName;

	@Column(name = "CERT_CONTENT_TYPE", unique = true, nullable = true, length = 255)
	private String certContentType;

	@Column(name = "FILE_STORE", length = 255)
	private String fileStoragePath = "";

	@Column
	private Long currentAnnexRevision;

	@Column
	private byte partiallyRegistered;

	@Column(length = 2000)
	private String publicKey;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SystemState getState() {
		return state;
	}

	public void setState(SystemState state) {
		this.state = state;
	}

	public String getRegistrationNumber() {
		return registrationNumber;
	}

	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}

	public Date getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	public String getCommissionRegisterUrl() {
		return commissionRegisterUrl;
	}

	public void setCommissionRegisterUrl(String commissionRegisterUrl) {
		this.commissionRegisterUrl = commissionRegisterUrl;
	}

	public boolean isCollecting() {
		return collecting != 0;
	}

	public void setCollecting(boolean collecting) {
		this.collecting = collecting ? (byte) 1 : (byte) 0;
	}

	public String getFileStoragePath() {
		return fileStoragePath;
	}

	public void setFileStoragePath(String fileStoragePath) {
		this.fileStoragePath = fileStoragePath;
	}

	public String getCertFileName() {
		return certFileName;
	}

	public void setCertFileName(String certFileName) {
		this.certFileName = certFileName;
	}

	public String getCertContentType() {
		return certContentType;
	}

	public void setCertContentType(String certContentType) {
		this.certContentType = certContentType;
	}

	public Date getDeadline() {
		return deadline;
	}

	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	public Long getCurrentAnnexRevision() {
		return currentAnnexRevision;
	}

	public void setCurrentAnnexRevision(Long currentAnnexRevision) {
		this.currentAnnexRevision = currentAnnexRevision;
	}

	public boolean isPartiallyRegistered() {
		return partiallyRegistered != 0;
	}

	public void setPartiallyRegistered(boolean partiallyRegistered) {
		this.partiallyRegistered = partiallyRegistered ? (byte) 1 : (byte) 0;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	@Override
	public String toString() {
		return "SystemPreferences [id=" + id + ", state=" + state + ", collecting=" + collecting
				+ ", registrationNumber=" + registrationNumber + ", registrationDate=" + registrationDate
				+ ", commissionRegisterUrl=" + commissionRegisterUrl + ", deadline=" + deadline + ", certFileName="
				+ certFileName + ", certContentType=" + certContentType + ", fileStoragePath=" + fileStoragePath
				+ ", currentAnnexRevision=" + currentAnnexRevision + ", partiallyRegistered=" + partiallyRegistered
				+ ", publicKey=" + publicKey + "]";
	}

}
