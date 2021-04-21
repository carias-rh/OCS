package eu.europa.ec.eci.oct.webcommons.services.persistence;

import java.util.List;

import eu.europa.ec.eci.oct.entities.system.Language;

public interface LanguageDAO {

	List<Language> getLanguages() throws PersistenceException;
	
	Language getLanguageByCode(String code) throws PersistenceException;
	
	List<String> getLanguageCodes() throws PersistenceException;

}
