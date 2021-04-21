package eu.europa.ec.eci.oct.webcommons.services.api.domain.language;

import java.io.Serializable;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class LanguageDTOlist implements Serializable{
	
	private static final long serialVersionUID = 5955412707343679977L;

	public LanguageDTOlist(){}
	
	private List<LanguageDTO> languages;

	public List<LanguageDTO> getLanguages() {
		return languages;
	}

	public void setLanguages(List<LanguageDTO> languages) {
		this.languages = languages;
	}

	@Override
	public String toString() {
		return "LanguageDTOlist [languages=" + languages + "]";
	}
	
}
