package eu.europa.ec.eci.oct.entities.admin;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import eu.europa.ec.eci.oct.entities.system.Country;

@Entity
@Table(name = "OCT_CONTACT")
@Cacheable(false)
public class Contact implements Serializable {

	private static final long serialVersionUID = -2404011345308498126L;

	@GeneratedValue(generator = "IdGenerator", strategy = GenerationType.TABLE)
	@Id
	@TableGenerator(name = "IdGenerator", pkColumnValue = "contact_id", table = "HIBERNATE_SEQUENCES", allocationSize = 1, pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
	@Column(name = "id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "contactRole_id")
	private ContactRole contactRole;

	@Column
	private String firstName;

	@Column
	private String familyName;

	@Column
	private String email;

	@ManyToOne
	@JoinColumn(name = "country_id")
	private Country country;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ContactRole getContactRole() {
		return contactRole;
	}

	public void setContactRole(ContactRole contactRole) {
		this.contactRole = contactRole;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getFamilyName() {
		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	@Override
	public String toString() {
		return "Contact [id=" + id + ", contactRole=" + contactRole + ", firstName=" + firstName + ", familyName="
				+ familyName + ", email=" + email + ", country=" + country + "]";
	}

}
