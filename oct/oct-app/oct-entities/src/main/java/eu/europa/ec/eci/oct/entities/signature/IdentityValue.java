package eu.europa.ec.eci.oct.entities.signature;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name = "OCT_IDENTITY_VALUE")
@Cacheable(false)
public class IdentityValue implements Serializable {

	private static final long serialVersionUID = -7087750891872581981L;

	@GeneratedValue(generator = "IdGenerator", strategy = GenerationType.TABLE)
	@Id
	@TableGenerator(name = "IdGenerator", pkColumnValue = "identity_value_id", table = "HIBERNATE_SEQUENCES", allocationSize = 1, pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
	@Column(name = "id")
	private Long id;

	@Column
	private String countryCode;

	@Column
	private Long propertyId;

	@Column
	private String identityValue;

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public Long getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(Long propertyId) {
		this.propertyId = propertyId;
	}

	public String getIdentityValue() {
		return identityValue;
	}

	public void setIdentityValue(String identityValue) {
		this.identityValue = identityValue;
	}

	@Override
	public String toString() {
		return "IdentityValue [id=" + id + ", countryCode=" + countryCode + ", propertyId=" + propertyId
				+ ", identityValue=" + identityValue + "]";
	}

}