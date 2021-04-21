package eu.europa.ec.eci.oct.webcommons.services.api.transformer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import eu.europa.ec.eci.oct.entities.system.Language;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.language.LanguageDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.language.LanguageDTOlist;

@Component
public class LanguageTransformer extends BaseTransformer{

	public LanguageDTOlist transformLanguages(List<Language> languageDAOlist) {
		if (languageDAOlist == null) {
			return null;
		}
		List<LanguageDTO> languageDTOlist = new ArrayList<LanguageDTO>();
		for (Language languageDAO : languageDAOlist) {
			LanguageDTO languageDTO = transformLanguage(languageDAO);
			languageDTOlist.add(languageDTO);
		}
		LanguageDTOlist languageList = new LanguageDTOlist();
		languageList.setLanguages(languageDTOlist);

		return languageList;
	}

	public LanguageDTO transformLanguage(Language languageDAO) {

		if (languageDAO == null) {
			return null;
		}
		LanguageDTO languageDTO = new LanguageDTO();
		languageDTO.setLanguageCode(languageDAO.getCode());
		languageDTO.setLanguageName(languageDAO.getName());
		languageDTO.setDisplayOrder(languageDAO.getDisplayOrder());

		return languageDTO;
	}

}
