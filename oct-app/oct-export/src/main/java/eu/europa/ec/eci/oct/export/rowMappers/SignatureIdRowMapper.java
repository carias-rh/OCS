package eu.europa.ec.eci.oct.export.rowMappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class SignatureIdRowMapper implements RowMapper<Long> {

	final String SIGNATURE_ID_COLUMN_LABEL = "SIGNATURE_ID";

	@Override
	public Long mapRow(ResultSet resultSet, int rowNum) throws SQLException {

		Long signatureId = resultSet.getLong(SIGNATURE_ID_COLUMN_LABEL);

		return signatureId;
	}
}
