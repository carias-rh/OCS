package eu.europa.ec.eci.oct.export.rowMappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.jdbc.core.RowMapper;

import eu.europa.ec.eci.oct.entities.admin.Contact;
import eu.europa.ec.eci.oct.entities.admin.ContactRole;
import eu.europa.ec.eci.oct.entities.system.Country;


public class ContactRowMapper implements RowMapper<Contact> {

	final String FIRST_NAME_COLUMN_LABEL = "FIRSTNAME";
	final String FAMILY_NAME_COLUMN_LABEL = "FAMILYNAME";
	final String EMAIL_COLUMN_LABEL = "EMAIL";
	final String ROLE_ID_COLUMN_LABEL = "ROLE_ID";
	final String COUNTRY_CODE_COLUMN_LABEL = "COUNTRY_CODE";

	@Override
	public Contact mapRow(ResultSet resultSet, int rowNum) throws SQLException {

		String firstName = StringEscapeUtils.escapeXml(resultSet.getString(FIRST_NAME_COLUMN_LABEL));
		String familyName = StringEscapeUtils.escapeXml(resultSet.getString(FAMILY_NAME_COLUMN_LABEL));
		String email = resultSet.getString(EMAIL_COLUMN_LABEL);
		Long contactRoleId = resultSet.getLong(ROLE_ID_COLUMN_LABEL);
		String countryCode = resultSet.getString(COUNTRY_CODE_COLUMN_LABEL);

		Contact contact = new Contact();
		ContactRole contactRole = new ContactRole();
		contactRole.setId(contactRoleId);
		contact.setContactRole(contactRole);
		contact.setEmail(email);
		contact.setFamilyName(familyName);
		contact.setFirstName(firstName);
		Country country = new Country();
		country.setCode(countryCode);
		contact.setCountry(country);

		return contact;
	}

}
