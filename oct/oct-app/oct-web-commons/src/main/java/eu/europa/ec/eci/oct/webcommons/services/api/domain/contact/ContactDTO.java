package eu.europa.ec.eci.oct.webcommons.services.api.domain.contact;

import java.io.Serializable;

import org.springframework.stereotype.Component;

@Component
public class ContactDTO implements Serializable {

	private static final long serialVersionUID = -5015587103577746775L;

	public ContactDTO() {
	}

	private String email;
	private String firstName;
	private String familyName;
	private String role;
	private String residenceCountry;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getResidenceCountry() {
		return residenceCountry;
	}

	public void setResidenceCountry(String residenceCountry) {
		this.residenceCountry = residenceCountry;
	}

	@Override
	public String toString() {
		return "ContactDTO [email=" + email + ", firstName=" + firstName + ", familyName=" + familyName + ", role="
				+ role + ", residenceCountry=" + residenceCountry + "]";
	}

}
