package eu.europa.ec.eci.oct.webcommons.services.translations;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.eci.oct.entities.system.Language;
import eu.europa.ec.eci.oct.webcommons.services.BaseService;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.TranslationException;

/**
 * User: franzmh Date: 13/03/17
 */
@Service
@Transactional
public class TranslationServiceImpl extends BaseService implements TranslationService {

	@Override
	@Transactional(readOnly = true)
	public String getTranslation(String key, Language language) throws TranslationException {
		String translation = translationDAO.getDescriptionByLanguage(key, language);
		return translation;
	}
}
