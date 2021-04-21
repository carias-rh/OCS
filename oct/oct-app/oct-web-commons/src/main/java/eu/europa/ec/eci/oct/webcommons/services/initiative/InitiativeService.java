package eu.europa.ec.eci.oct.webcommons.services.initiative;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import eu.europa.ec.eci.oct.entities.admin.Contact;
import eu.europa.ec.eci.oct.entities.admin.InitiativeDescription;
import eu.europa.ec.eci.oct.entities.admin.SystemPreferences;
import eu.europa.ec.eci.oct.entities.system.Language;
import eu.europa.ec.eci.oct.webcommons.services.api.XMLdescriptionParsingResult;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.initiative.InitiativeDescriptionsDTO;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;

public interface InitiativeService {

	/**
	 * Provide default description
	 * 
	 * @return
	 * @throws OCTException
	 * @throws eu.europa.ec.eci.oct.services.exceptions.OCTException
	 */
	public InitiativeDescription getDefaultDescription() throws OCTException;

	/**
	 * Provide the list of descriptions registered within the system
	 * 
	 * @return
	 * @throws OCTException
	 */
	public List<InitiativeDescription> getDescriptions() throws OCTException;

	/**
	 * Provide the list of descriptions registered within the system, exclude
	 * default one from the list
	 * 
	 * @return
	 * @throws OCTException
	 */
	public List<InitiativeDescription> getDescriptionsExcludeDefault() throws OCTException;

	/**
	 * Provide initiative description by a given identifier
	 * 
	 * @param ud
	 * @return
	 * @throws OCTException
	 */
	public InitiativeDescription getDescriptionById(long id) throws OCTException;

	/**
	 * Provide description for a given language
	 * 
	 * @param lang
	 * @return
	 * @throws OCTException
	 */
	public InitiativeDescription getDescriptionByLanguageCode(String langCode) throws OCTException;

	/**
	 * Provide the list of languages with no description for an initiative
	 * 
	 * @return
	 * @throws OCTException
	 */
	public List<Language> getUnusedDescriptionLanguages() throws OCTException;

	/**
	 * Returns a list with all the available initiative descriptions languages.
	 * 
	 * @return
	 * @throws OCTException
	 */
	public List<Language> getLanguagesForAvailableDescriptions() throws OCTException;

	/**
	 * Add initiative description, overwrite if exists
	 * 
	 * @param id
	 *            - description
	 * @throws OCTException
	 */
	public void saveOrUpdateInitiativeDescription(InitiativeDescription id) throws OCTException;

	/**
	 * Sets description as a default one
	 * 
	 * @param id
	 * @throws OCTException
	 */
	public void setDefaultDescription(InitiativeDescription id) throws OCTException;

	public void onlineInitiativeDescriptionUpdate(SystemPreferences prefs, List<Contact> contacts,
			List<InitiativeDescription> descs) throws OCTException;

	public void offlineInitiativeDescriptionPersist(SystemPreferences systemPreferences,
			List<Contact> persistableContacts, List<InitiativeDescription> persistableDescriptions) throws OCTException;

	public long daysLeft() throws OCTException;

	public void deleteAllDescriptions() throws OCTException;

	public InitiativeDescriptionsDTO insertInitiativeDescriptionXML(InputStream inputStream,
			FormDataContentDisposition fileMetaData) throws OCTException;
	
	public XMLdescriptionParsingResult parseXMLdescription(File xmlDescription) throws Exception;


}
