package eu.europa.ec.eci.oct.webcommons.services.api.domain.language;

import java.io.Serializable;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class LanguageDTO implements Serializable{
	
	private static final long serialVersionUID = 311568128458629635L;

	public LanguageDTO(){}

	private String languageCode;
	private String languageName;
	private long displayOrder;

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public String getLanguageName() {
		return languageName;
	}

	public void setLanguageName(String languageName) {
		this.languageName = languageName;
	}

	public long getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(long displayOrder) {
		this.displayOrder = displayOrder;
	}

	@Override
	public String toString() {
		return "LanguageDTO [languageCode=" + languageCode + ", languageName=" + languageName
				+ ", displayOrder=" + displayOrder + "]";
	}

}
