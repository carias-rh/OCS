package eu.europa.ec.eci.oct.webcommons.services.socialMedia;

import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.eci.oct.entities.ConfigurationParameter;
import eu.europa.ec.eci.oct.entities.admin.SocialMedia;
import eu.europa.ec.eci.oct.entities.admin.SocialMediaMessage;
import eu.europa.ec.eci.oct.utils.DateUtils;
import eu.europa.ec.eci.oct.webcommons.services.BaseService;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;
import eu.europa.ec.eci.oct.webcommons.services.persistence.PersistenceException;

@Service
@Transactional
public class SocialMediaServiceImpl extends BaseService implements SocialMediaService {

	@Override
	@Transactional(readOnly = true)
	public String getMessageForSocialMediaAndLanguage(String media, String language) throws OCTException {
		try {
			SocialMediaMessage socialMediaMessage = socialMediaDAO.getSocialMediaMessage(media, language);

			String message = "";
			if (socialMediaMessage != null) {
				message = socialMediaMessage.getMessage();
			}
			return message;
		} catch (PersistenceException e) {
			logger.error("There was a problem retrieving the social media message " + media + " " + language, e);
			throw new OCTException("There was a problem retrieving the social media message.", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public SocialMediaMessage getSocialMediaMessage(String socialMediaName, String languageCode) throws OCTException {
		try {
			return socialMediaDAO.getSocialMediaMessage(socialMediaName, languageCode);
		} catch (PersistenceException e) {
			logger.error(
					"There was a problem retrieving the social media message " + socialMediaName + " " + languageCode,
					e);
			throw new OCTException("There was a problem retrieving the social media message object.", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = OCTException.class)
	public void saveOrUpdateSocialMessage(SocialMediaMessage socialMediaMessage) throws OCTException {
		try {
			String socialMediaName = socialMediaMessage.getSocialMedia().getName().toLowerCase();
			String socialMediaLanguageCode = socialMediaMessage.getLanguage().getCode().toLowerCase();
			SocialMediaMessage existingMessage = socialMediaDAO.getSocialMediaMessage(socialMediaName,
					socialMediaLanguageCode);
			if (existingMessage != null) {
				existingMessage.setMessage(socialMediaMessage.getMessage());
				socialMediaDAO.updateSocialMediaMessage(existingMessage);
			} else {
				// newMessage
				existingMessage = new SocialMediaMessage();
				existingMessage.setLanguage(socialMediaMessage.getLanguage());
				existingMessage.setMessage(socialMediaMessage.getMessage());
				existingMessage.setSocialMedia(socialMediaMessage.getSocialMedia());
				socialMediaDAO.persistSocialMediaMessage(existingMessage);
			}
			ConfigurationParameter socialLastUpdate = new ConfigurationParameter();
			socialLastUpdate.setParam(ConfigurationParameter.LAST_UPDATE_SOCIAL);
			socialLastUpdate.setValue(DateUtils.formatDate(new Date()));
			settingsDAO.updateParameter(socialLastUpdate);
		} catch (PersistenceException e) {
			logger.error("There was a problem to update the social media", e);
			throw new OCTException("There was a problem to update the social media message.", e);
		}
	}

	@Override
	public String proofOfState() {
		String proofOfState = "SocialMediaService init ";
		return proofOfState;
	}

	@Override
	@Transactional(readOnly = true)
	public SocialMedia getSocialMediaByName(String socialMediaName) throws OCTException {
		try {
			return socialMediaDAO.getSocialMediaByName(socialMediaName);
		} catch (PersistenceException e) {
			logger.error("There was a problem retrieving the social media by name " + socialMediaName, e);
			throw new OCTException("There was a problem retrieving the social media by name " + socialMediaName, e);
		}
	}
}
