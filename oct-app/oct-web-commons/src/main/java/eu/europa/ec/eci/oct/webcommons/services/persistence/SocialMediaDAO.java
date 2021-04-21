package eu.europa.ec.eci.oct.webcommons.services.persistence;

import eu.europa.ec.eci.oct.entities.admin.SocialMedia;
import eu.europa.ec.eci.oct.entities.admin.SocialMediaMessage;

public interface SocialMediaDAO {

	SocialMediaMessage getSocialMediaMessage(String media, String language) throws PersistenceException;

	SocialMedia getSocialMediaByName(String name) throws PersistenceException;
	
	SocialMedia getSocialMediaById(long mediaId) throws PersistenceException;
	
	long getSocialMediaIdByName(String socialMediaName) throws PersistenceException;

	void persistSocialMediaMessage(SocialMediaMessage socialMediaMessage) throws PersistenceException;
	
	void updateSocialMediaMessage(SocialMediaMessage socialMediaMessage) throws PersistenceException;

}
