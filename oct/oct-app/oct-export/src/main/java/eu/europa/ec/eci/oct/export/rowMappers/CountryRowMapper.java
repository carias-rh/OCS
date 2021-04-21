package eu.europa.ec.eci.oct.export.rowMappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import eu.europa.ec.eci.oct.entities.system.Country;

public class CountryRowMapper implements RowMapper<Country> {

	final String ID_COLUMN_LABEL = "ID";
	final String CODE_COLUMN_LABEL = "CODE";

	@Override
	public Country mapRow(ResultSet resultSet, int rowNum) throws SQLException {

		String countryCode = resultSet.getString(CODE_COLUMN_LABEL);
		long countryId = resultSet.getLong(ID_COLUMN_LABEL);

		Country country = new Country();
		country.setCode(countryCode);
		country.setId(countryId);

		return country;
	}

}
