package eu.europa.ec.eci.oct.validation;

import java.util.ArrayList;
import java.util.List;

import com.opencsv.bean.CsvBindByName;

public class CSVSignatureTestBean {

	@CsvBindByName
	private String id;

	@CsvBindByName
	private String expectedResult;

	@CsvBindByName
	private String nationality;

	@CsvBindByName
	private String expectedReason;

	@CsvBindByName(column = "street")
	private String street;

	public void setStreet(String street) {
		addProperty("street", street);
	}

	@CsvBindByName(column = "postal.code")
	private String postalCode;

	public void setPostalCode(String postalCode) {
		addProperty("postal.code", postalCode);
	}

	@CsvBindByName(column = "city")
	private String city;

	public void setCity(String city) {
		addProperty("city", city);
	}

	@CsvBindByName(column = "country")
	private String country;

	public void setCountry(String country) {
		addProperty("country", country);
	}

	@CsvBindByName(column = "date.of.birth")
	private String dateOfBirth;

	public void setDateOfBirth(String dateOfBirth) {
		addProperty("date.of.birth", dateOfBirth);
	}

	@CsvBindByName(column = "issuing.authority")
	private String issuingAuthority;

	public void setIssuingAuthority(String issuingAuthority) {
		addProperty("issuing.authority", issuingAuthority);
	}

	@CsvBindByName(column = "passport")
	private String passport;

	public void setPassport(String passport) {
		addProperty("passport", passport);
	}

	@CsvBindByName(column = "id.card")
	private String idCard;

	public void setIdCard(String idCard) {
		addProperty("id.card", idCard);
	}

	@CsvBindByName(column = "residence.permit")
	private String residencePermit;

	public void setResidencePermit(String residencePermit) {
		addProperty("residence.permit", residencePermit);
	}

	@CsvBindByName(column = "personal.number")
	private String personalNumber;

	public void setPersonalNumber(String personalNumber) {
		addProperty("personal.number", personalNumber);
	}

	@CsvBindByName(column = "personal.id")
	private String personalId;

	public void setPersonalId(String personalId) {
		addProperty("personal.id", personalId);
	}

	@CsvBindByName(column = "permanent.residence")
	private String permanentResidence;

	public void setPermanentResidence(String permanentResidence) {
		addProperty("permanent.residence", permanentResidence);
	}

	@CsvBindByName(column = "national.id.number")
	private String nationalIdNumber;

	public void setNationalIdNumber(String nationalIdNumber) {
		addProperty("national.id.number", nationalIdNumber);
	}

	@CsvBindByName(column = "registration.certificate")
	private String registrationCertificate;

	public void setRegistrationCertificate(String registrationCertificate) {
		addProperty("registration.certificate", registrationCertificate);
	}

	@CsvBindByName(column = "citizens.card")
	private String citizensCard;

	public void setCitizensCard(String citizensCard) {
		addProperty("citizens.card", citizensCard);
	}

	@CsvBindByName(column = "full.first.names")
	private String fullFirstNames;

	public void setFullFirstNames(String fullFirstNames) {
		addProperty("full.first.names", fullFirstNames);
	}

	@CsvBindByName(column = "family.names")
	private String familyNames;

	public void setFamilyNames(String familyNames) {
		addProperty("family.names", familyNames);
	}

	@CsvBindByName(column = "street.number")
	private String streetNumber;

	public void setStreetNumber(String streetNumber) {
		addProperty("street.number", streetNumber);
	}

	private List<ValidationProperty> properties = new ArrayList<ValidationProperty>();

	public void addProperty(String key, String value) {

		// not needed
		// String octPrefix = "oct.property.";

		if (value.equalsIgnoreCase("{skip}")) {
			// skip the properties (it means that is not needed for the test)
			return;
		} else {
			ValidationProperty v = new ValidationProperty();
			v.setKey(key);
			if (value.equalsIgnoreCase("{null}")) {
				v.setValue(null);
			} else if (value.equalsIgnoreCase("{empty}")) {
				v.setValue("");
			} else {
				v.setValue(value);
			}
			properties.add(v);
		}
	}

	public String getExpectedReason() {
		return expectedReason;
	}

	public void setExpectedReason(String expectedReason) {
		this.expectedReason = expectedReason;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getExpectedResult() {
		return expectedResult;
	}

	public void setExpectedResult(String expectedResult) {
		this.expectedResult = expectedResult;
	}

	public String getNationality() {
		return nationality;
	}
	
	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getStreet() {
		return street;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public String getCity() {
		return city;
	}

	public String getCountry() {
		return country;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public String getIssuingAuthority() {
		return issuingAuthority;
	}

	public String getPassport() {
		return passport;
	}

	public String getIdCard() {
		return idCard;
	}

	public String getResidencePermit() {
		return residencePermit;
	}

	public String getPersonalNumber() {
		return personalNumber;
	}

	public String getPersonalId() {
		return personalId;
	}

	public String getPermanentResidence() {
		return permanentResidence;
	}

	public String getNationalIdNumber() {
		return nationalIdNumber;
	}

	public String getRegistrationCertificate() {
		return registrationCertificate;
	}

	public String getCitizensCard() {
		return citizensCard;
	}

	public String getFullFirstNames() {
		return fullFirstNames;
	}

	public String getFamilyNames() {
		return familyNames;
	}

	public String getStreetNumber() {
		return streetNumber;
	}

	public List<ValidationProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<ValidationProperty> properties) {
		this.properties = properties;
	}

	@Override
	public String toString() {
		return "CSVSignatureTestBean{" +
				"id='" + id + '\'' +
				", expectedResult='" + expectedResult + '\'' +
				", nationality='" + nationality + '\'' +
				", expectedReason='" + expectedReason + '\'' +
				", street='" + street + '\'' +
				", postalCode='" + postalCode + '\'' +
				", city='" + city + '\'' +
				", country='" + country + '\'' +
				", dateOfBirth='" + dateOfBirth + '\'' +
				", issuingAuthority='" + issuingAuthority + '\'' +
				", passport='" + passport + '\'' +
				", idCard='" + idCard + '\'' +
				", residencePermit='" + residencePermit + '\'' +
				", personalNumber='" + personalNumber + '\'' +
				", personalId='" + personalId + '\'' +
				", permanentResidence='" + permanentResidence + '\'' +
				", nationalIdNumber='" + nationalIdNumber + '\'' +
				", registrationCertificate='" + registrationCertificate + '\'' +
				", citizensCard='" + citizensCard + '\'' +
				", fullFirstNames='" + fullFirstNames + '\'' +
				", familyNames='" + familyNames + '\'' +
				", streetNumber='" + streetNumber + '\'' +
				", properties=" + properties +
				'}';
	}
}
