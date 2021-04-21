package eu.europa.ec.eci.oct.webcommons.services.system;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import eu.europa.ec.eci.oct.entities.Property;
import eu.europa.ec.eci.oct.entities.admin.StepState;
import eu.europa.ec.eci.oct.entities.admin.SystemPreferences;
import eu.europa.ec.eci.oct.entities.system.Country;
import eu.europa.ec.eci.oct.entities.system.CountryRule;
import eu.europa.ec.eci.oct.entities.system.Language;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.FileType;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;

/**
 * User: franzmh
 * Date: 14/09/16
 */

public interface SystemManager {
	
	/**
 	 * 
 	 * 
 	 * @param user
 	 * @param password
 	 * @return
 	 * @throws OCTException
 	 */
 	public boolean authenticate(String user, String password) throws OCTException;
	
	/**
	 * System configuration provider
	 * 
	 * @return - system configuration instance
	 * @throws OCTException
	 */
    Language getLanguageByCode(String code) throws OCTException;
    
 // TODO move it from here!
 	public void setRegistrationData(SystemPreferences prefs) throws OCTException;

 	/**
 	 * Enables production (online) mode
 	 * 
 	 * @throws OCTException
 	 *             - if transition illegal
 	 */
 	public void goOnline() throws OCTException;
 	
 	/**
	 * System configuration provider
	 * 
	 * @return - system configuration instance
	 * @throws OCTException
	 */
	public SystemPreferences getSystemPreferences() throws OCTException;

 	/**
 	 * Provides the list of state members registered within the system
 	 * 
 	 * @return - the list of countries
 	 * @throws OCTException
 	 */
 	public List<Country> getAllCountries() throws OCTException;

 	/**
 	 * Provides country by given code (ISO 3166-1)
 	 * 
 	 * @param code
 	 *            - language code
 	 * @return country instance
 	 * @throws OCTException
 	 */
 	public Country getCountryByCode(String code) throws OCTException;
 	public Country getCountryById(long countryId) throws OCTException;

 	/**
 	 * Provides the list of languages registered within the system
 	 * 
 	 * @return
 	 * @throws OCTException
 	 */
 	public List<Language> getAllLanguages() throws OCTException;

 	/**
 	 * Counts hash for a given input value
 	 * 
 	 * @param input
 	 *            - input value
 	 * @return counted hash value
 	 * @throws OCTException
 	 */
 	public String hash(String input) throws OCTException;
 	
 	/**
 	 * Encrypts given phrase
 	 * 
 	 * @param phrase
 	 * @return
 	 * @throws OCTException
 	 */
 	public String generateChallenge(String phrase) throws OCTException;

 	/**
 	 * Sets collector status (on/off)
 	 * 
 	 * @param collecting
 	 * @throws OCTException
 	 */
 	public void setCollecting(boolean collecting) throws OCTException;

 	/**
 	 * Provides collector state
 	 * 
 	 * @return
 	 * @throws OCTException
 	 */
 	public boolean getCollecting() throws OCTException;

 	/**
 	 * Checks the availability of transiting into online mode. Checks whether initiative setup comes from importing ECI
 	 * data and whether certificate has been installe dwithin the system
 	 * 
 	 * @throws OCTException
 	 */
 	void checkOnlineAvailability() throws OCTException;

 	/**
 	 * Used to decide, based on system state, whether the public part of the application needs to be password protected.
 	 * 
 	 * @return true if public part needs password protection, false otherwise
 	 * @throws OCTException
 	 */
 	public boolean isWebPasswordProtected() throws OCTException;

	/**
	 * @return all the properties from oct_propeties table
	 * @throws OCTException
	 */
	public List<Property> getAllProperties()throws OCTException;
	
	/**
	 * Return the id associated to the Country code
	 * @param countryCode
	 * @return
	 * @throws OCTException
	 */
	public long getCountryIdFromCode(String countryCode) throws OCTException;

	void goOffline() throws OCTException;

	List<String> getAllCountryCodes() throws OCTException;
	
	List<String> getAllLanguageCodes() throws OCTException;
	
	public void setStructureAsDone(boolean isStructureDone) throws OCTException;
	
	public void setPersonaliseAsDone(boolean isPersonaliseDone) throws OCTException;
	
	public void setSocialAsDone(boolean isSocialDone) throws OCTException;
	
	public void setCertificateAsDone(boolean isCertificateDone) throws OCTException;
	
	public void setGoneLive() throws OCTException;
	
	public StepState getStepState() throws OCTException;

	public void setNotLive() throws OCTException;

    public boolean isOnline() throws OCTException;

	boolean isOffline() throws OCTException;

	public String saveFileTo(String storePath, String fileName, InputStream inputStream, FileType fileType) throws IOException, OCTException;

	boolean isNotARecognizedLanguage(String languageCode) throws OCTException;

    public CountryRule getCountryRuleByCode(String countryCode) throws OCTException;

	void installCertificate(Certificate c) throws OCTException;

}
