package eu.europa.ec.eci.oct.export.rowMappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;

import eu.europa.ec.eci.oct.export.entities.SignatureCountryCount;
import eu.europa.ec.eci.oct.export.writers.SignaturesCountWriter;

public class SignaturesCountRowMapper implements RowMapper<SignatureCountryCount> {
	private final Logger logger = LogManager.getLogger(SignaturesCountWriter.class);

	final String COUNTRY_CODE_COLUMN_LABEL = "COUNTRYCODE";
	final String SIGNATURE_COUNT_COLUMN_LABEL = "COUNT";

	@Override
	public SignatureCountryCount mapRow(ResultSet resultSet, int rowNum) throws SQLException {
		SignatureCountryCount signatureCountryCount = new SignatureCountryCount();
		signatureCountryCount.setCount(resultSet.getLong(SIGNATURE_COUNT_COLUMN_LABEL));
		signatureCountryCount.setCountryCode(resultSet.getString(COUNTRY_CODE_COLUMN_LABEL));
		logger.info(">> reading signatureCountryCount: " + signatureCountryCount);

		return signatureCountryCount;
	}
}
