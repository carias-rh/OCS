package eu.europa.ec.eci.oct.export.entities;

import java.io.Serializable;

import javax.xml.datatype.XMLGregorianCalendar;

public class InitiativeData implements Serializable {

	private static final long serialVersionUID = 1L;

	private String registrationNumber;
	private XMLGregorianCalendar registrationDate;
	private XMLGregorianCalendar closingDate;
	private String urlOnCommissionRegister;

	public String getRegistrationNumber() {
		return registrationNumber;
	}

	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}

	public XMLGregorianCalendar getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(XMLGregorianCalendar registrationDate) {
		this.registrationDate = registrationDate;
	}

	public XMLGregorianCalendar getClosingDate() {
		return closingDate;
	}

	public void setClosingDate(XMLGregorianCalendar closingDate) {
		this.closingDate = closingDate;
	}

	public String getUrlOnCommissionRegister() {
		return urlOnCommissionRegister;
	}

	public void setUrlOnCommissionRegister(String urlOnCommissionRegister) {
		this.urlOnCommissionRegister = urlOnCommissionRegister;
	}

	@Override
	public String toString() {
		return "InitiativeData [registrationNumber=" + registrationNumber + ", liveDate=" + registrationDate
				+ ", closingDate=" + closingDate + ", urlOnCommissionRegister=" + urlOnCommissionRegister + "]";
	}

}
