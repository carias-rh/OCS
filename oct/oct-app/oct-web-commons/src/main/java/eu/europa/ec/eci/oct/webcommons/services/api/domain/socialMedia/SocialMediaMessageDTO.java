package eu.europa.ec.eci.oct.webcommons.services.api.domain.socialMedia;

import java.io.Serializable;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class SocialMediaMessageDTO implements Serializable{
	
	private static final long serialVersionUID = 3302168635902297820L;

	public SocialMediaMessageDTO(){}

	private String socialMedia;
	private String message;
	private String languageCode;

	public String getSocialMedia() {
		return socialMedia;
	}

	public void setSocialMedia(String socialMedia) {
		this.socialMedia = socialMedia;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	@Override
	public String toString() {
		return "SocialMediaMessageDTO [socialMedia=" + socialMedia + ", message=" + message
				+ ", languageCode=" + languageCode + "]";
	}

}
