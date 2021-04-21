package eu.europa.ec.eci.oct.webcommons.services.api.transformer;

import org.springframework.stereotype.Component;

import eu.europa.ec.eci.oct.entities.admin.SocialMediaMessage;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.socialMedia.SocialMediaMessageDTO;

@Component
public class SocialMediaTransformer extends BaseTransformer {

	public SocialMediaMessageDTO transform(SocialMediaMessage socialMediaMessageDAO) {
		SocialMediaMessageDTO socialMediaMessageDTO = new SocialMediaMessageDTO();
		socialMediaMessageDTO.setLanguageCode(socialMediaMessageDAO.getLanguage().getCode());
		socialMediaMessageDTO.setMessage(socialMediaMessageDAO.getMessage());
		socialMediaMessageDTO.setSocialMedia(socialMediaMessageDAO.getSocialMedia().getName());
		return socialMediaMessageDTO;
	}

}
