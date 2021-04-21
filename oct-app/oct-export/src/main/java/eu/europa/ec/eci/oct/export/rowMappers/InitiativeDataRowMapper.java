package eu.europa.ec.eci.oct.export.rowMappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;

import eu.europa.ec.eci.oct.export.entities.InitiativeData;
import eu.europa.ec.eci.oct.export.utils.ItemProcessorsUtils;

public class InitiativeDataRowMapper implements RowMapper<InitiativeData> {

	private final Logger logger = LogManager.getLogger(InitiativeDataRowMapper.class);

	final String URL_ON_COMMISSION_REGISTER_COLUMN_LABEL = "URLONCOMMISSIONREGISTER";
	final String REGISTRATION_DATE_COLUMN_LABEL = "REGISTRATIONDATE";
	final String CLOSING_DATE_COLUMN_LABEL = "CLOSINGDATE";
	final String REGISTRATION_NUMBER_COLUMN_LABEL = "REGISTRATIONNUMBER";

	@Override
	public InitiativeData mapRow(ResultSet resultSet, int rowNum) throws SQLException {

		String urlOnCommissionRegister = resultSet.getString(URL_ON_COMMISSION_REGISTER_COLUMN_LABEL);
		Date registrationDate = resultSet.getDate(REGISTRATION_DATE_COLUMN_LABEL);
		Date closingDate = resultSet.getDate(CLOSING_DATE_COLUMN_LABEL);
		String registrationNumber = resultSet.getString(REGISTRATION_NUMBER_COLUMN_LABEL);

		XMLGregorianCalendar registrationDateXML = null;
		XMLGregorianCalendar closingDateXML = null;
		try {
			registrationDateXML = ItemProcessorsUtils.getXMLdate(registrationDate);
			closingDateXML = ItemProcessorsUtils.getXMLdate(closingDate);
		} catch (DatatypeConfigurationException e) {
			logger.error("Error parsing registration date: " + registrationDate);
			System.exit(-1);
		}

		InitiativeData initiativeData = new InitiativeData();
		initiativeData.setRegistrationDate(registrationDateXML);
		initiativeData.setClosingDate(closingDateXML);
		initiativeData.setRegistrationNumber(registrationNumber);
		initiativeData.setUrlOnCommissionRegister(urlOnCommissionRegister);

		return initiativeData;
	}

}
