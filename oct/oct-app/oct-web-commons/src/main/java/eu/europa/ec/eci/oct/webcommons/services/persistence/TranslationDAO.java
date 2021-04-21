package eu.europa.ec.eci.oct.webcommons.services.persistence;

import eu.europa.ec.eci.oct.entities.system.Language;

/**
 * User: franzmh
 * Date: 13/03/17
 */
public interface TranslationDAO {

    String getDescriptionByLanguage(String key, Language language);

}
