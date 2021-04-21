package eu.europa.ec.eci.oct.webcommons.services.translations;

import eu.europa.ec.eci.oct.entities.system.Language;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.TranslationException;

/**
 * User: franzmh
 * Date: 13/03/17
 */
public interface TranslationService {
    /**
     * Retrieve the translations of a specific key (i.e: title.subtitle.section.a) for a particular
     * language (i.e.: en)
     * @param key the key to translate
     * @param language the language
     * @return the translated value of the key
     * @throws TranslationException
     */
    String getTranslation(String key, Language language) throws TranslationException;

//
//    /**
//     * Retrieve the entire translated content for a specific language
//     * @param code the language code
//     * @return a synchronized map with the translated values (key - translations)
//     * @throws TranslationException
//     */
//
//    Map<String,String> getContents(String code) throws TranslationException;

}
