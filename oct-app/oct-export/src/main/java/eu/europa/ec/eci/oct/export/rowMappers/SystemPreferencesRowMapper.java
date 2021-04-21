package eu.europa.ec.eci.oct.export.rowMappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import eu.europa.ec.eci.oct.entities.admin.SystemPreferences;

public class SystemPreferencesRowMapper implements RowMapper<SystemPreferences> {

	final String FILE_STORE_COLUMN_LABEL = "FILESTORE";
	final String COMMISSION_URL_COLUMN_LABEL = "COMMISSION_URL";
	final String REGISTRATION_NUMBER_COLUMN_LABEL = "REGISTRATION_NUMBER";

	@Override
	public SystemPreferences mapRow(ResultSet resultSet, int rowNum) throws SQLException {

		String fileStore = resultSet.getString(FILE_STORE_COLUMN_LABEL);
		String commissionUrl = resultSet.getString(COMMISSION_URL_COLUMN_LABEL);
		String registrationNumber = resultSet.getString(REGISTRATION_NUMBER_COLUMN_LABEL);
		
		SystemPreferences systemPreferences = new SystemPreferences();
		systemPreferences.setFileStoragePath(fileStore);
		systemPreferences.setCommissionRegisterUrl(commissionUrl);
		systemPreferences.setRegistrationNumber(registrationNumber);

		return systemPreferences;
	}

}
