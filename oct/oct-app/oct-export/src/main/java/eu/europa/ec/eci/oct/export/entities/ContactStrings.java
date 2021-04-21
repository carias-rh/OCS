package eu.europa.ec.eci.oct.export.entities;

import java.io.Serializable;

public class ContactStrings implements Serializable {

	private static final long serialVersionUID = 1707995853735941319L;

	private String contactString;

	public String getContactString() {
		return contactString;
	}

	public void setContactString(String contactString) {
		this.contactString = contactString;
	}

	@Override
	public String toString() {
		return "ContactStrings [contactString=" + contactString + "]";
	}

}
