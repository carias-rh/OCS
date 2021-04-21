package eu.europa.ec.eci.oct.webcommons.services.system;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.eci.oct.crypto.CryptoException;
import eu.europa.ec.eci.oct.crypto.Cryptography;
import eu.europa.ec.eci.oct.entities.ConfigurationParameter;
import eu.europa.ec.eci.oct.entities.Property;
import eu.europa.ec.eci.oct.entities.admin.Account;
import eu.europa.ec.eci.oct.entities.admin.StepState;
import eu.europa.ec.eci.oct.entities.admin.SystemPreferences;
import eu.europa.ec.eci.oct.entities.admin.SystemState;
import eu.europa.ec.eci.oct.entities.system.Country;
import eu.europa.ec.eci.oct.entities.system.CountryRule;
import eu.europa.ec.eci.oct.entities.system.Language;
import eu.europa.ec.eci.oct.webcommons.services.BaseService;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.FileType;
import eu.europa.ec.eci.oct.webcommons.services.configuration.ConfigurationService;
import eu.europa.ec.eci.oct.webcommons.services.crypto.CryptographyService;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTMissingSetupDateException;
import eu.europa.ec.eci.oct.webcommons.services.persistence.PersistenceException;
import eu.europa.ec.eci.oct.webcommons.services.persistence.SystemPreferencesDAO;

/**
 * User: franzmh Date: 14/09/16
 */
@Service
public class SystemManagerImpl extends BaseService implements SystemManager {

	@Override
	@Transactional(readOnly = true)
	public Language getLanguageByCode(String code) throws OCTException {
		try {
			return languageDAO.getLanguageByCode(code);
		} catch (PersistenceException e) {
			logger.error("There was a roblem while retrieving language with code " + code + ". Message: "
					+ e.getLocalizedMessage(), e);
			throw new OCTException("Fetching language " + code + " failed.", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public SystemPreferences getSystemPreferences() throws OCTException {
		try {
			SystemPreferences sf = systemPreferencesDAO.getSystemPreferences();
			return sf;
		} catch (PersistenceException e) {
			logger.error("persistence problem while fetching preferences", e);
			throw new OCTException("persistence problem while fetching preferences", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = OCTException.class)
	public void setRegistrationData(SystemPreferences prefs) throws OCTException {
		try {
			if (SystemState.DEPLOYED.equals(prefs.getState())) {
				prefs.setState(SystemState.SETUP);
			}
			systemPreferencesDAO.setPreferences(prefs);
		} catch (PersistenceException e) {
			logger.error("persistence problem while fetching preferences", e);
			throw new OCTException("persistence problem while fetching preferences", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = Exception.class)
	public void goOnline() throws OCTException {

		try {
			signatureService.deleteAllSignatures();
			reportingService.deleteAllFeedbacks();
			setGoneLive();
			SystemPreferences prefs = systemPreferencesDAO.getSystemPreferences();

			if (prefs.getDeadline() == null) {
				throw new OCTMissingSetupDateException("Missinng setup insertion date");
			}
			prefs.setState(SystemState.OPERATIONAL);
			prefs.setCollecting(true);
			systemPreferencesDAO.setPreferences(prefs);
		} catch (PersistenceException e) {
			logger.error("persistence problem while changing system state", e);
			throw new OCTException("persistence problem while changing system state", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = Exception.class)
	public void goOffline() throws OCTException {

		try {
			SystemPreferences prefs = systemPreferencesDAO.getSystemPreferences();

			if (prefs.getDeadline() == null) {
				throw new OCTMissingSetupDateException("Missinng setup insertion date");
			}
			systemManager.setNotLive();
			prefs.setState(SystemState.SETUP);

			systemPreferencesDAO.setPreferences(prefs);
		} catch (PersistenceException e) {
			logger.error("persistence problem while changing system state", e);
			throw new OCTException("persistence problem while changing system state", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public void checkOnlineAvailability() throws OCTException {

		try {
			SystemPreferences prefs = systemPreferencesDAO.getSystemPreferences();

			if (prefs.getDeadline() == null) {
				throw new OCTMissingSetupDateException("Missinng setup insertion date");
			}
		} catch (PersistenceException e) {
			logger.error("persistence problem while changing system state", e);
			throw new OCTException("persistence problem while changing system state", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = OCTException.class)
	public void setCollecting(boolean collecting) throws OCTException {
		try {
			systemPreferencesDAO.setCollecting(collecting);
		} catch (PersistenceException e) {
			logger.error("persistence problem while setting collector state to " + collecting, e);
			throw new OCTException("persistence problem while setting collector state to " + collecting, e);
		}

	}

	@Override
	@Transactional(readOnly = true)
	public boolean getCollecting() throws OCTException {
		try {
			return systemPreferencesDAO.getSystemPreferences().isCollecting();
		} catch (PersistenceException e) {
			logger.error("persistence problem while getting collector state", e);
			throw new OCTException("persistence problem while getting collector state", e);
		}

	}

	@Override
	@Transactional(readOnly = true)
	public List<Country> getAllCountries() throws OCTException {
		logger.debug("obtaining country list");
		try {
			List<Country> countries = countryDAO.getCountries();
			if (logger.isDebugEnabled()) {
				logger.debug("Country list contains " + (countries == null ? "0" : countries.size()) + " elements");
			}

			logger.debug("obtained " + countries.size() + " countries");
			return countries;
		} catch (PersistenceException e) {
			logger.error("problem while obtaining countries. message: " + e.getLocalizedMessage(), e);
			throw new OCTException("obtaining country list failed", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Country getCountryByCode(String code) throws OCTException {
		try {
			Country result = countryDAO.getCountryByCode(code);
			if (logger.isDebugEnabled()) {
				logger.debug("Country matching code " + code + ": "
						+ (result == null ? "Nothing found!" : result.toString()));
			}

			return result;
		} catch (PersistenceException e) {
			logger.error("There was a problem while retrieving the country for code " + code + ". Message: "
					+ e.getLocalizedMessage(), e);
			throw new OCTException("Getting property groups for " + code + " failed.", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Country getCountryById(long countryId) throws OCTException {
		try {
			Country result = countryDAO.getCountryById(countryId);
			if (logger.isDebugEnabled()) {
				logger.debug("Country matching id " + countryId + ": "
						+ (result == null ? "Nothing found!" : result.toString()));
			}
			return result;
		} catch (PersistenceException e) {
			logger.error("There was a problem while retrieving the country for id " + countryId + ". Message: "
					+ e.getLocalizedMessage(), e);
			throw new OCTException("Getting property groups for " + countryId + " failed.", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<Language> getAllLanguages() throws OCTException {
		// logger.info("obtaining language list");

		try {
			List<Language> langs = languageDAO.getLanguages();
			if (logger.isDebugEnabled()) {
				logger.debug("Language list contains " + (langs == null ? "0" : langs.size()) + " elements");
			}

			return langs;
		} catch (PersistenceException e) {
			logger.error("problem while obtaining languages. message: " + e.getLocalizedMessage(), e);
			throw new OCTException("obtaining language list failed", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public boolean authenticate(String user, String password) throws OCTException {
		Account account;
		String fingerPrint;
		try {
			account = accountDAO.getAccountByName(user);
			if (account == null)
				return false;

			byte[] bHash = Cryptography.fingerprintWithSalt(password, Hex.decodeHex(account.getSalt().toCharArray()));
			fingerPrint = new String(Hex.encodeHex(bHash));
			return fingerPrint.equals(account.getPassHash());
		} catch (PersistenceException pe) {
			logger.error(
					"problem while fetching account info and/or the public key. message: " + pe.getLocalizedMessage(),
					pe);
			throw new OCTException("problem while fetching account info and/or the public key.", pe);

		} catch (CryptoException ce) {
			logger.error("problem to generate a new fingerprint: " + ce.getLocalizedMessage(), ce);
			throw new OCTException("problem to generate a new fingerprint.", ce);
		} catch (DecoderException de) {
			logger.error("problem while decoding the salt. message: " + de.getLocalizedMessage(), de);
			throw new OCTException("problem while decoding the salt.", de);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public String generateChallenge(String phrase) throws OCTException {
		CryptographyService crypto = CryptographyService
				.getService(getSystemPreferences().getPublicKey().toCharArray());
		byte[] encryptedChallenge = crypto.encrypt(phrase);
		return new String(Hex.encodeHex(encryptedChallenge));
	}


	@Override
	@Transactional(readOnly = true)
	public String hash(String input) throws OCTException {
		CryptographyService crypto = CryptographyService
				.getService(getSystemPreferences().getPublicKey().toCharArray());
		return new String(Hex.encodeHex(crypto.fingerprint(input)));
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isWebPasswordProtected() throws OCTException {
		SystemState state = getSystemPreferences().getState();
		return state != SystemState.OPERATIONAL;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Property> getAllProperties() throws OCTException {
		logger.info("obtaining property list");

		try {
			List<Property> properties = propertyDAO.getAllProperties();
			if (logger.isDebugEnabled()) {
				logger.debug("Property list contains " + (properties == null ? "0" : properties.size()) + " elements");
			}

			return properties;
		} catch (PersistenceException e) {
			logger.error("problem while obtaining properties. message: " + e.getLocalizedMessage(), e);
			throw new OCTException("obtaining properties list failed", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public long getCountryIdFromCode(String countryCode) throws OCTException {
		logger.info("Getting countryId from countryCode " + countryCode);
		long countryId = -1;
		try {
			Country countryByCode = countryDAO.getCountryByCode(countryCode);
			if (countryByCode == null) {
				throw new OCTException("no country found for code " + countryCode);
			} else {
				countryId = countryByCode.getId().longValue();
				logger.info("Returning countryId " + countryId + " from countryCode " + countryCode);
				return countryId;
			}
		} catch (PersistenceException pe) {
			logger.error(pe.getMessage());
			throw new OCTException("obtaining countryId from code failed", pe);
		}
	}

	@Override
	public List<String> getAllCountryCodes() throws OCTException {
		List<String> countryCodes = new ArrayList<String>();
		try {
			countryCodes = countryDAO.getCountryCodes();
		} catch (PersistenceException pe) {
			logger.error(pe.getMessage());
			throw new OCTException("obtaining countryCodes failed", pe);
		}
		return countryCodes;
	}

	@Override
	public CountryRule getCountryRuleByCode(String countryCode) throws OCTException {
		CountryRule countryRule = null;
		try {
			countryRule = countryRuleDAO.getCountryRuleByCode(countryCode);
		} catch (PersistenceException pe) {
			logger.error(pe.getMessage());
			throw new OCTException("obtaining getCountryRuleByCode failed", pe);
		}
		return countryRule;
	}

	@Override
	public List<String> getAllLanguageCodes() throws OCTException {
		List<String> languageCodes = new ArrayList<String>();
		try {
			languageCodes = languageDAO.getLanguageCodes();
		} catch (PersistenceException pe) {
			logger.error(pe.getMessage());
			throw new OCTException("obtaining languageCodes failed", pe);
		}
		return languageCodes;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = OCTException.class)
	public void setStructureAsDone(boolean isStructureDone) throws OCTException {
		try {
			StepState stepState = getInternalStepState();
			stepState.setStructure(isStructureDone);
			stepStateDAO.setStepState(stepState);
		} catch (PersistenceException e) {
			logger.error("setStructureAsDone failed.: " + e.getLocalizedMessage(), e);
			throw new OCTException("setStructureAsDone failed.", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = OCTException.class)
	public void setPersonaliseAsDone(boolean isPersonaliseDone) throws OCTException {
		try {
			StepState stepState = getInternalStepState();
			stepState.setPersonalise(isPersonaliseDone);
			stepStateDAO.setStepState(stepState);
		} catch (PersistenceException e) {
			logger.error("setPersonaliseAsDone failed.: " + e.getLocalizedMessage(), e);
			throw new OCTException("setPersonaliseAsDone failed.", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = OCTException.class)
	public void setSocialAsDone(boolean isSocialDone) throws OCTException {
		try {
			StepState stepState = getInternalStepState();
			stepState.setSocial(isSocialDone);
			stepStateDAO.setStepState(stepState);
		} catch (PersistenceException e) {
			logger.error("setSocialAsDone failed.: " + e.getLocalizedMessage(), e);
			throw new OCTException("setSocialAsDone failed.", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = OCTException.class)
	public void setGoneLive() throws OCTException {
		try {
			StepState stepState = getInternalStepState();
			stepState.setLive(true);
			stepStateDAO.setStepState(stepState);
		} catch (PersistenceException e) {
			logger.error("setGoneLive failed.: " + e.getLocalizedMessage(), e);
			throw new OCTException("setGoneLive failed.", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = OCTException.class)
	public void setNotLive() throws OCTException {
		try {
			StepState stepState = getInternalStepState();
			stepState.setLive(false);
			stepStateDAO.setStepState(stepState);
		} catch (PersistenceException e) {
			logger.error("setNotLive failed.: " + e.getLocalizedMessage(), e);
			throw new OCTException("setNotLive failed.", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public StepState getStepState() throws OCTException {
		return getInternalStepState();
	}

	@Override
	public boolean isOnline() throws OCTException {
		return getSystemPreferences().getState().equals(SystemState.OPERATIONAL);
	}

	@Override
	public boolean isOffline() throws OCTException {
		return getSystemPreferences().getState().equals(SystemState.SETUP);
	}

	private StepState getInternalStepState() throws OCTException {
		try {
			StepState stepState = stepStateDAO.getStepState();
			return stepState;
		} catch (PersistenceException e) {
			logger.error("getInternalStepState failed.: " + e.getLocalizedMessage(), e);
			throw new OCTException("getInternalStepState failed.", e);
		}
	}

	public String saveFileTo(String uploadPath, String filename, InputStream inputStream, FileType type)
			throws IOException, OCTException {

		ConfigurationParameter param;
		// replace filename with system standard to avoid path traversal vulnerability
		String sanitizedFileName = filename.replaceAll("../", "");
		String fileExtension = sanitizedFileName.substring(sanitizedFileName.lastIndexOf(".") + 1).trim();
		String canonicalFileName = type.name() + "." + fileExtension;

		switch (type) {
		case DESCRIPTION:
			/* not managed here for xml bomb attack management: see OCS-428 task */
			break;
		case LOGO:
			param = new ConfigurationParameter();
			param.setParam(ConfigurationService.Parameter.LOGO_PATH.getKey());
			param.setValue(canonicalFileName);
			configurationService.updateParameter(param);
			break;
		case CERTIFICATE:
			Certificate certificate = new Certificate();
			certificate.setFileName(canonicalFileName);
			certificate.setContentType("application/pdf");
			systemManager.installCertificate(certificate);
			break;
			
		default:
			throw new OCTException("Incompatible file type: " + type.fileType());
		}

		int read;
		byte[] bytes = new byte[1024];
		String systemFileStore = getSystemPreferences().getFileStoragePath();
		OutputStream out = new FileOutputStream(new File(systemFileStore + canonicalFileName));
		while ((read = inputStream.read(bytes)) != -1) {
			out.write(bytes, 0, read);
		}
		out.flush();
		out.close();

		return systemFileStore + canonicalFileName;

	}

	@Override
	public boolean isNotARecognizedLanguage(String languageCode) throws OCTException {
		return !getAllLanguageCodes().contains(languageCode);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = OCTException.class)
	public void installCertificate(Certificate c) throws OCTException {

		// TODO: verify system state
		try {
			SystemPreferencesDAO spdao = systemPreferencesDAO;
			SystemPreferences sf = spdao.getSystemPreferences();

			sf.setCertFileName(c.getFileName());
			sf.setCertContentType(c.getContentType());

			spdao.setPreferences(sf);
		} catch (PersistenceException e) {
			logger.error("persistence problem while installing certificate", e);
			throw new OCTException("persistence problem while installing certificate", e);
		}
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = OCTException.class)
	public void setCertificateAsDone(boolean isCertificateDone) throws OCTException {
		try {
			StepState stepState = getInternalStepState();
			stepState.setCertificate(isCertificateDone);
			stepStateDAO.setStepState(stepState);
		} catch (PersistenceException e) {
			logger.error("setCertificateAsDone failed.: " + e.getLocalizedMessage(), e);
			throw new OCTException("setCertificateAsDone failed.", e);
		}
	}

}
