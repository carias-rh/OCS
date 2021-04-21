package eu.europa.ec.eci.oct.export.rowMappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.jdbc.core.RowMapper;

import eu.europa.ec.eci.oct.entities.admin.InitiativeDescription;
import eu.europa.ec.eci.oct.entities.system.Language;

public class InitiativeDescriptionRowMapper implements RowMapper<InitiativeDescription> {

	final String ID_COLUMN_LABEL = "ID";
	final String TITLE_COLUMN_LABEL = "TITLE";
	final String OBJECTIVES_COLUMN_LABEL = "OBJECTIVES";
	final String URL_COLUMN_LABEL = "URL";
	final String LANGUAGECODE_COLUMN_LABEL = "LANGUAGE";

	@Override
	public InitiativeDescription mapRow(ResultSet resultSet, int rowNum) throws SQLException {

		long descriptionId = resultSet.getLong(ID_COLUMN_LABEL);
		String title = StringEscapeUtils.escapeXml(resultSet.getString(TITLE_COLUMN_LABEL));
		String objectives = StringEscapeUtils.escapeXml(resultSet.getString(OBJECTIVES_COLUMN_LABEL));
		String url = resultSet.getString(URL_COLUMN_LABEL);
		String languageCode = resultSet.getString(LANGUAGECODE_COLUMN_LABEL);

		InitiativeDescription initiativeDescription = new InitiativeDescription();
		initiativeDescription.setId(descriptionId);
		initiativeDescription.setObjectives(objectives);
		initiativeDescription.setTitle(title);
		initiativeDescription.setUrl(url);
		Language language = new Language();
		language.setCode(languageCode);
		initiativeDescription.setLanguage(language);

		return initiativeDescription;
	}

}
