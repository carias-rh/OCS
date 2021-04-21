package eu.europa.ec.eci.oct.entities.signature;

import java.io.Serializable;
import java.sql.Blob;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import eu.europa.ec.eci.oct.entities.system.Country;

@Entity
@Table(name = "OCT_SIGNATURE")
public class Signature implements Serializable {

	private static final long serialVersionUID = -3053181226446088290L;

	@GeneratedValue(generator = "IdGenerator", strategy = GenerationType.TABLE)
	@Id
	@TableGenerator(name = "IdGenerator", pkColumnValue = "signature_id", table = "HIBERNATE_SEQUENCES", allocationSize = 1, pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
	@Column(name = "id")
	private Long id;

	@Column
	private String uuid;

	@ManyToOne
	private Country countryToSignFor;

	@Column
	private Date dateOfSignature;

	@Column(name = "dateOfSignature_msec")
	private Long dateOfSignatureMsec;

	@Column(name = "fingerprint")
	private String fingerprint;

	@Column
	private Long annexRevision;

	@Column(name = "signatory_info")
	private Blob signatoryInfo;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDateOfSignature() {
		return dateOfSignature;
	}

	public void setDateOfSignature(Date dateOfSignature) {
		this.dateOfSignature = dateOfSignature;
	}

	public long getDateOfSignatureMsec() {
		return dateOfSignatureMsec;
	}

	public void setDateOfSignatureMsec(Long dateOfSignatureMsec) {
		this.dateOfSignatureMsec = dateOfSignatureMsec;
	}

	public Blob getSignatoryInfo() {
		return signatoryInfo;
	}

	public void setSignatoryInfo(Blob signatoryInfo) {
		this.signatoryInfo = signatoryInfo;
	}

	public String getFingerprint() {
		return fingerprint;
	}

	public void setFingerprint(String fingerprint) {
		this.fingerprint = fingerprint;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getUuid() {
		return uuid;
	}

	public void setCountryToSignFor(Country countryToSignFor) {
		this.countryToSignFor = countryToSignFor;
	}

	public Country getCountryToSignFor() {
		return countryToSignFor;
	}

	public Long getAnnexRevision() {
		return annexRevision;
	}

	public void setAnnexRevision(Long annexRevision) {
		this.annexRevision = annexRevision;
	}

	@Override
	public String toString() {
		return "Signature [id=" + id + ", uuid=" + uuid + ", countryToSignFor=" + countryToSignFor
				+ ", dateOfSignature=" + dateOfSignature + ", dateOfSignatureMsec=" + dateOfSignatureMsec
				+ ", fingerprint=" + fingerprint + ", annexRevision=" + annexRevision + "]";
	}
}
