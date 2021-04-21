package eu.europa.ec.eci.oct.webcommons.services.initiative;

import java.io.File;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eu.europa.ec.eci.oct.entities.ConfigurationParameter;
import eu.europa.ec.eci.oct.entities.admin.Contact;
import eu.europa.ec.eci.oct.entities.admin.ContactRole;
import eu.europa.ec.eci.oct.entities.admin.InitiativeDescription;
import eu.europa.ec.eci.oct.entities.admin.SystemPreferences;
import eu.europa.ec.eci.oct.entities.admin.SystemState;
import eu.europa.ec.eci.oct.entities.system.Language;
import eu.europa.ec.eci.oct.utils.CommonsConstants;
import eu.europa.ec.eci.oct.utils.XMLutils;
import eu.europa.ec.eci.oct.webcommons.services.BaseService;
import eu.europa.ec.eci.oct.webcommons.services.api.XMLdescriptionParsingResult;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.FileType;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.contact.ContactDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.initiative.InitiativeDescriptionDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.initiative.InitiativeDescriptionsDTO;
import eu.europa.ec.eci.oct.webcommons.services.configuration.ConfigurationService;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTIllegalOperationException;
import eu.europa.ec.eci.oct.webcommons.services.persistence.PersistenceException;

@Service
@Transactional
public class InitiativeServiceImpl extends BaseService implements InitiativeService {

	final String INITIATIVE_XML_REGISTRATION_DATE_NAME = "registrationDate";
	final String INITIATIVE_XML_START_DATE_NAME = "startOfTheCollectionPeriod";
	final String INITIATIVE_XML_END_DATE_NAME = "endOfTheCollectionPeriod";
	final String INITIATIVE_XML_REGISTRATION_NUMBER_NAME = "registrationNumber";
	final String INITIATIVE_XML_NAME = "initiative";
	final String INITIATIVE_XML_URL_NAME = "url";
	final String INITIATIVE_XML_LANGUAGE_URL_NAME = "site";
	final String INITIATIVE_XML_TITLE_NAME = "title";
	final String INITIATIVE_XML_LANGUAGE_OBJECTIVES_NAME = "description";
	final String INITIATIVE_XML_LANGUAGE_PARTIALREGURL_NAME = "partialRegistration";
	final String INITIATIVE_XML_ORGANISERS_NAME = "organisers";
	final String INITIATIVE_XML_ORGANISER_NAME = "organiser";
	final String INITIATIVE_XML_ORGANISER_ROLE_NAME = "role";
	final String INITIATIVE_XML_ORGANISER_FIRSTNAME_NAME = "firstName";
	final String INITIATIVE_XML_ORGANISER_FAMILYNAME_NAME = "familyName";
	final String INITIATIVE_XML_ORGANISER_EMAIL_NAME = "email";
	final String INITIATIVE_XML_LANGUAGES_NAME = "languages";
	final String INITIATIVE_XML_LANGUAGE_NAME = "language";
	final String INITIATIVE_XML_LANGUAGE_CODE_NAME = "code";
	final String INITIATIVE_XML_LANGUAGE_ORIGINAL_NAME = "original";
	final String INITIATIVE_XML_LEGAL_ENTITY_COUNTRY_NAME = "country";
	final String INITIATIVE_XML_LEGAL_ENTITY_NAME = "legal entity";

	@Override
	@Transactional(readOnly = true)
	public InitiativeDescription getDefaultDescription() throws OCTException {
		try {
			return initiativeDAO.getDefaultDescription();
		} catch (PersistenceException e) {
			logger.error("persistence problem while fetching description", e);
			throw new OCTException("persistence problem while fetching description", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<InitiativeDescription> getDescriptions() throws OCTException {
		try {
			return initiativeDAO.getAllDescriptions();
		} catch (PersistenceException e) {
			logger.error("persistence problem while fetching all descriptions", e);
			throw new OCTException("persistence problem while fetching all descriptions", e);
		}
	}

	@Transactional(readOnly = true)
	public List<InitiativeDescription> getDescriptionsExcludeDefault() throws OCTException {
		try {
			// let's obtain entire list of descriptions
			List<InitiativeDescription> lid = initiativeDAO.getAllDescriptions();

			// get default description
			InitiativeDescription systemDefaultDesc = getDefaultDescription();

			// identify default in the list
			InitiativeDescription tmpDefDesc = null;
			for (InitiativeDescription id : lid) {
				if (systemDefaultDesc != null && id.getId().equals(systemDefaultDesc.getId())) {
					tmpDefDesc = id;
					break;
				}
			}

			// remove default if found
			if (tmpDefDesc != null) {
				lid.remove(tmpDefDesc);
			} else {
				logger.error("default description not found. data inconsistency.");
				throw new OCTException("default description not found. data inconsistency.");
			}

			return lid;
		} catch (PersistenceException e) {
			logger.error("persistence problem while fetching descriptions", e);
			throw new OCTException("persistence problem while fetching descriptions", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public InitiativeDescription getDescriptionById(long id) throws OCTException {
		try {
			InitiativeDescription description = initiativeDAO.getDescriptionById(id);
			return description;
		} catch (PersistenceException e) {
			logger.error("persistence problem while fetching description", e);
			throw new OCTException("persistence problem while fetching description", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public InitiativeDescription getDescriptionByLanguageCode(String languageCode) throws OCTException {

		try {
			InitiativeDescription id = initiativeDAO.getDescriptionByLanguageCode(languageCode);
			return id;
		} catch (PersistenceException e) {
			logger.error("persistence problem while fetching description", e);
			throw new OCTException("persistence problem while fetching description", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<Language> getUnusedDescriptionLanguages() throws OCTException {
		List<Language> result = new ArrayList<Language>();
		try {
			List<Language> allLanguages = languageDAO.getLanguages();
			List<InitiativeDescription> descriptions = initiativeDAO.getAllDescriptions();

			for (Language language : allLanguages) {
				boolean used = false;
				for (InitiativeDescription description : descriptions) {
					if (language.equals(description.getLanguage())) {
						used = true;
						break;
					}
				}

				if (!used) {
					result.add(language);
				}
			}
		} catch (PersistenceException e) {
			logger.error("persistence problem while retrieving used languages", e);
			throw new OCTException("persistence problem while retrieving used languages", e);
		}
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Language> getLanguagesForAvailableDescriptions() throws OCTException {
		List<Language> result = new ArrayList<Language>();
		for (InitiativeDescription description : getDescriptions()) {
			result.add(description.getLanguage());
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = OCTException.class)
	public void saveOrUpdateInitiativeDescription(InitiativeDescription initiativeDescriptionToBeSaved)
			throws OCTException {
		try {

			InitiativeDescription alreadyExistentDescription = getDescriptionByLanguageCode(
					initiativeDescriptionToBeSaved.getLanguage().getCode());
			if (alreadyExistentDescription == null) {
				initiativeDAO.saveInitiativeDescription(initiativeDescriptionToBeSaved);
			} else {
				if (!systemManager.getSystemPreferences().getState().equals(SystemState.OPERATIONAL)) {
					alreadyExistentDescription.setObjectives(initiativeDescriptionToBeSaved.getObjectives());
					alreadyExistentDescription.setTitle(initiativeDescriptionToBeSaved.getTitle());
					alreadyExistentDescription.setUrl(initiativeDescriptionToBeSaved.getUrl());
					initiativeDAO.updateInitiativeDescription(alreadyExistentDescription);
				} else {
					logger.info(
							"If system State it's already OPERATIONAL, the descriptions can't be updated for language "
									+ initiativeDescriptionToBeSaved.getLanguage().getCode());
				}
			}
		} catch (PersistenceException e) {
			logger.error("persistence problem while adding initiative description", e);
			throw new OCTException("persistence problem while setting default description language", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = OCTException.class)
	public void setDefaultDescription(InitiativeDescription id) throws OCTException {

		try {
			id.setIsDefault(InitiativeDescription.IS_DEFAULT);
			initiativeDAO.saveInitiativeDescription(id);
		} catch (PersistenceException e) {
			logger.error("persistence problem while setting default description language", e);
			throw new OCTException("persistence problem while setting default description language", e);
		}

	}

	@Override
	@Transactional(readOnly = false)
	public void onlineInitiativeDescriptionUpdate(SystemPreferences prefs, List<Contact> contacts,
			List<InitiativeDescription> newDescriptions) throws OCTException {

		logger.info("overwriting initiative descriptions and contacts");

		setRegistrationData(prefs);

		for (InitiativeDescription id : newDescriptions) {
			saveOrUpdateInitiativeDescription(id);
		}
		contactService.saveContacts(contacts);

	}

	@Override
	@Transactional(readOnly = false)
	public void offlineInitiativeDescriptionPersist(SystemPreferences prefs, List<Contact> contacts,
			List<InitiativeDescription> newDescriptions) throws OCTException {

		systemManager.setRegistrationData(prefs);
		signatureService.deleteAllSignatures();
		deleteAllDescriptions();
		for (InitiativeDescription newInitiativeDescription : newDescriptions) {
			saveOrUpdateInitiativeDescription(newInitiativeDescription);
		}
		systemManager.setRegistrationData(prefs);
		contactService.saveContacts(contacts);

	}

	@Override
	@Transactional(readOnly = true)
	public long daysLeft() throws OCTException {
		boolean isStructured = systemManager.getStepState().getStructure();
		if (!isStructured) {
			/*
			 * the user has not configured the system yet with the xml file, we return a
			 * default value
			 */
			return CommonsConstants.DAYS_LEFT_DEFAULT;
		} else {
			try {
				SystemPreferences sysPref = systemPreferencesDAO.getSystemPreferences();
				Date expireDate = sysPref.getDeadline();
				Date nowDate = new Date();
				long diff = expireDate.getTime() - nowDate.getTime();
				long daysLeft = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
				if (daysLeft < 0) {
					// no need to calculate more than -1, it's already expired
					daysLeft = -1;
				}
				return daysLeft;
			} catch (PersistenceException e) {
				logger.error("persistence problem while fetching all descriptions", e);
				throw new OCTException("persistence problem while fetching all descriptions", e);
			}
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = OCTException.class)
	public void setRegistrationData(SystemPreferences prefs) throws OCTException {

		try {
			SystemPreferences systemPreferencesToBeUpdated = systemPreferencesDAO.getSystemPreferences();
			systemPreferencesToBeUpdated.setCommissionRegisterUrl(prefs.getCommissionRegisterUrl());
			systemPreferencesToBeUpdated.setRegistrationDate(prefs.getRegistrationDate());
			systemPreferencesToBeUpdated.setRegistrationNumber(prefs.getRegistrationNumber());
			systemPreferencesToBeUpdated.setDeadline(prefs.getDeadline());
			if (SystemState.DEPLOYED.equals(systemPreferencesToBeUpdated.getState())) {
				systemPreferencesToBeUpdated.setState(SystemState.SETUP);
			}

			systemPreferencesDAO.setPreferences(systemPreferencesToBeUpdated);
		} catch (PersistenceException e) {
			logger.error("persistence problem while fetching preferences", e);
			throw new OCTException("persistence problem while fetching preferences", e);
		}
	}

	private XMLdescriptionParsingResult getParsedObjects(FormDataContentDisposition fileMetaData,
			InputStream inputStream, SystemPreferences systemPreferences) throws OCTException {

		XMLdescriptionParsingResult xmlDescriptionParsingResult = new XMLdescriptionParsingResult();
		File storedXmlDescription = null;
		boolean saved = false;
		try {
			// uploaded file properties
			String storagePath = systemManager.getSystemPreferences().getFileStoragePath();
			String fileName = fileMetaData.getFileName();

			// save the xml
			String filePath = systemManager.saveFileTo(storagePath, fileName, inputStream, FileType.DESCRIPTION);

			/*
			 * read the xml and parse it to generate a contactDTO and a
			 * initiativeDescriptionDTO
			 */
			storedXmlDescription = new File(filePath);
			xmlDescriptionParsingResult = parseXMLdescription(storedXmlDescription);

			// xml is ok, set the right config param
			ConfigurationParameter param = new ConfigurationParameter();
			param.setParam(ConfigurationService.Parameter.DESCRIPTION.getKey());
			param.setValue(fileName);
			configurationService.updateParameter(param);

		} catch (Exception e) {
			String message = e.getMessage();
			if (saved) {
				// delete the saved description file
				storedXmlDescription.delete();
			}
			logger.error(message);
			throw new OCTIllegalOperationException("Error while parsing the xml file: " + message);
		}
		return xmlDescriptionParsingResult;
	}

	public XMLdescriptionParsingResult parseXMLdescription(File storedXmlDescription) throws Exception {

		XMLdescriptionParsingResult result = new XMLdescriptionParsingResult();

		DocumentBuilderFactory documentBuilderFactory = XMLutils.getDocumentBuilderFactory();

		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document xmlDescriptions = documentBuilder.parse(storedXmlDescription);

		// check that the xml belongs to the right initiative matching the
		// registration number
		Element xmlInitiativeInfo = (Element) xmlDescriptions.getElementsByTagName(INITIATIVE_XML_NAME).item(0);
		String xmlRegistrationNumber = xmlInitiativeInfo.getAttribute(INITIATIVE_XML_REGISTRATION_NUMBER_NAME);
		String xmlURL = "";
		if (xmlInitiativeInfo.getAttribute(INITIATIVE_XML_URL_NAME) != null) {
			xmlURL = xmlInitiativeInfo.getAttribute(INITIATIVE_XML_URL_NAME);
		}
		String xmlRegistrationDate = xmlInitiativeInfo.getAttribute(INITIATIVE_XML_REGISTRATION_DATE_NAME).substring(0,
				10);
		// String xmlStartDate =
		// xmlInitiativeInfo.getAttribute(INITIATIVE_XML_START_DATE_NAME);
		String xmldeadline = xmlInitiativeInfo.getAttribute(INITIATIVE_XML_END_DATE_NAME).substring(0, 10);

		result.setEcRegisterUrl(xmlURL);
		result.setEcRegistrationNumber(xmlRegistrationNumber);
		result.setRegistrationDate(xmlRegistrationDate);
		result.setDeadline(xmldeadline);

		/*
		 * get organisers names and emails from xml and create the map for concat the
		 * properties in the proper order
		 */
		List<ContactDTO> contacts = new ArrayList<ContactDTO>();

		Element organisersElement = (Element) xmlDescriptions.getElementsByTagName(INITIATIVE_XML_ORGANISERS_NAME)
				.item(0);
		NodeList organisers = organisersElement.getElementsByTagName(INITIATIVE_XML_ORGANISER_NAME);

		for (int i = 0; i < organisers.getLength(); i++) {
			Element organiser = (Element) organisers.item(i);
			String organiserRole = organiser.getAttribute(INITIATIVE_XML_ORGANISER_ROLE_NAME);
			String firstName = organiser.getElementsByTagName(INITIATIVE_XML_ORGANISER_FIRSTNAME_NAME).item(0)
					.getTextContent();
			String familyName = organiser.getElementsByTagName(INITIATIVE_XML_ORGANISER_FAMILYNAME_NAME).item(0)
					.getTextContent();
			String email = "";
			if (organiser.getElementsByTagName(INITIATIVE_XML_ORGANISER_EMAIL_NAME).item(0) != null) {
				email = organiser.getElementsByTagName(INITIATIVE_XML_ORGANISER_EMAIL_NAME).item(0).getTextContent();
			}
			ContactDTO contactDTO = new ContactDTO();
			contactDTO.setEmail(email);
			contactDTO.setFamilyName(familyName);
			contactDTO.setFirstName(firstName);
			if (organiserRole.equalsIgnoreCase(INITIATIVE_XML_LEGAL_ENTITY_NAME)) {
				String residenceCountry = organiser.getElementsByTagName(INITIATIVE_XML_LEGAL_ENTITY_COUNTRY_NAME)
						.item(0).getTextContent();
				contactDTO.setResidenceCountry(residenceCountry);
				organiserRole = ContactRole.LEGAL_ENTITY;
			}
			contactDTO.setRole(organiserRole);
			contacts.add(contactDTO);
		}

		/* parse the description(s) */
		List<InitiativeDescriptionDTO> initiativeDescriptionDTOlist = new ArrayList<InitiativeDescriptionDTO>();
		Element languagesElement = (Element) xmlDescriptions.getElementsByTagName(INITIATIVE_XML_LANGUAGES_NAME)
				.item(0);
		NodeList languages = languagesElement.getElementsByTagName(INITIATIVE_XML_LANGUAGE_NAME);
		int defaultInitiativeDescriptions = 0;
		for (int i = 0; i < languages.getLength(); i++) {
			Element language = (Element) languages.item(i);
			String languageCode = language.getAttribute(INITIATIVE_XML_LANGUAGE_CODE_NAME);
			String original = language.getAttribute(INITIATIVE_XML_LANGUAGE_ORIGINAL_NAME);
			if (!original.equalsIgnoreCase("true") && !original.equalsIgnoreCase("false")) {
				throw new OCTException("Initiative attribute 'original'[true/false] not expected: " + original);
			}

			boolean isOriginal = Boolean.parseBoolean(original);
			String title = language.getElementsByTagName(INITIATIVE_XML_TITLE_NAME).item(0).getTextContent();
			String objectives = language.getElementsByTagName(INITIATIVE_XML_LANGUAGE_OBJECTIVES_NAME).item(0)
					.getTextContent();
			Node siteItem = language.getElementsByTagName(INITIATIVE_XML_LANGUAGE_URL_NAME).item(0);
			Node partialRegistrationItem = language.getElementsByTagName(INITIATIVE_XML_LANGUAGE_PARTIALREGURL_NAME)
					.item(0);

			InitiativeDescriptionDTO initiativeDescriptionDTO = new InitiativeDescriptionDTO();
			initiativeDescriptionDTO.setLanguageCode(languageCode);
			initiativeDescriptionDTO.setObjectives(objectives);
			initiativeDescriptionDTO.setTitle(title);
			initiativeDescriptionDTO.setOriginal(isOriginal);
			String url = "";
			if (siteItem != null) {
				url = siteItem.getTextContent();
			}
			initiativeDescriptionDTO.setUrl(url);
			String partialRegistration = "";
			if (partialRegistrationItem != null) {
				partialRegistration = partialRegistrationItem.getTextContent();
			}
			initiativeDescriptionDTO.setPartialRegistration(partialRegistration);

			if (isOriginal) {
				defaultInitiativeDescriptions++;
			}
			initiativeDescriptionDTOlist.add(initiativeDescriptionDTO);
		}
		if (defaultInitiativeDescriptions != 1) {
			throw new OCTException("Expected: 1 default description, found: " + defaultInitiativeDescriptions);
		}
		result.setContacts(contacts);
		result.setDescriptions(initiativeDescriptionDTOlist);

		return result;
	}

	@Override
	@Transactional(readOnly = false)
	public InitiativeDescriptionsDTO insertInitiativeDescriptionXML(InputStream inputStream,
			FormDataContentDisposition fileMetaData) throws OCTException, OCTIllegalOperationException {
		SystemPreferences systemPreferences = systemManager.getSystemPreferences();
		SystemState systemState = systemPreferences.getState();
		XMLdescriptionParsingResult parsedObjects = getParsedObjects(fileMetaData, inputStream, systemPreferences);

		/* now we have parsed dtos objects to save: contacts and descriptions */
		List<InitiativeDescriptionDTO> initiativeDescriptionDTOlist = parsedObjects.getDescriptions();
		List<ContactDTO> contactsDTO = parsedObjects.getContacts();
		List<InitiativeDescription> persistableDescriptions = initiativeDescriptionTransformer
				.transformList(initiativeDescriptionDTOlist);
		List<Contact> persistableContacts = contactTransformer.transformListDTO(contactsDTO);
		if (systemState.equals(SystemState.OPERATIONAL)) {
			/*
			 * if system is online: only new descriptions will be inserted. no modification
			 * or delete on existing ones.
			 */
			String changesDetected = detectChanges(parsedObjects);
			if (StringUtils.isBlank(changesDetected)) {
				updateSystemPreferences(systemPreferences, parsedObjects);
				initiativeService.onlineInitiativeDescriptionUpdate(systemPreferences, persistableContacts,
						persistableDescriptions);
			} else {
				throw new OCTIllegalOperationException(
						"Changes detected in original XML file descriptions, not allowed in Online mode: ["
								+ changesDetected + "]");
			}
		} else {

			/*
			 * if system is not online: add/edit/delete are permitted
			 */
			updateSystemPreferences(systemPreferences, parsedObjects);
			initiativeService.offlineInitiativeDescriptionPersist(systemPreferences, persistableContacts,
					persistableDescriptions);
		}

		InitiativeDescriptionsDTO descriptionsDTO = initiativeDescriptionTransformer.transform(getDescriptions());

		return descriptionsDTO;
	}

	private String detectChanges(XMLdescriptionParsingResult parsedObjects) throws OCTException {

		SystemPreferences prefs = systemManager.getSystemPreferences();
		// first check registration data
		String parsedCurrenRegistrationtDate = eu.europa.ec.eci.oct.utils.DateUtils
				.formatDate(prefs.getRegistrationDate(), "yyyy-MM-dd");
		if (!parsedCurrenRegistrationtDate.equals(parsedObjects.getRegistrationDate())) {
			return "registration date";
		}
		String parsedCurrennewDeadlinetDate = eu.europa.ec.eci.oct.utils.DateUtils.formatDate(prefs.getDeadline(),
				"yyyy-MM-dd");
		if (!parsedCurrennewDeadlinetDate.equals(parsedObjects.getDeadline())) {
			return "newDeadline date";
		}

		if (!prefs.getRegistrationNumber().equals(parsedObjects.getEcRegistrationNumber())) {
			return "registration number";
		}
		if (!prefs.getCommissionRegisterUrl().equals(parsedObjects.getEcRegisterUrl())) {
			return "register url";
		}

		// then contacts
		List<Contact> currentContacts = contactService.getAllContacts();
		List<Contact> newContacts = contactTransformer.transformListDTO(parsedObjects.getContacts());
		if (currentContacts.size() != newContacts.size()) {
			return "contact add or removal";
		}
		int matchedContacts = 0;
		for (Contact currentContact : currentContacts) {
			ContactRole currentContactRole = currentContact.getContactRole();
			String currentContactName = currentContact.getFirstName();
			String currentContactSurname = currentContact.getFamilyName();
			String currentContactEmail = "";
			if (StringUtils.isNotBlank(currentContact.getEmail())) {
				currentContactEmail = currentContact.getEmail();
			}
			String currentContactCountry = "";
			if (currentContact.getCountry() != null && StringUtils.isNotBlank(currentContact.getCountry().getCode())) {
				currentContactCountry = currentContact.getCountry().getCode();
			}
			for (Contact newContact : newContacts) {
				ContactRole newContactRole = newContact.getContactRole();
				String newContactName = newContact.getFirstName();
				String newContactSurname = newContact.getFamilyName();
				String newContactEmail = "";
				if (StringUtils.isNotBlank(newContact.getEmail())) {
					newContactEmail = newContact.getEmail();
				}
				String newContactCountry = "";
				if (newContact.getCountry() != null && StringUtils.isNotBlank(newContact.getCountry().getCode())) {
					newContactCountry = newContact.getCountry().getCode();
				}

				if (newContactRole.getRoleDescription().equalsIgnoreCase(currentContactRole.getRoleDescription())
						&& newContactEmail.equalsIgnoreCase(currentContactEmail)
						&& newContactName.equalsIgnoreCase(currentContactName)
						&& newContactCountry.equalsIgnoreCase(currentContactCountry)
						&& newContactSurname.equalsIgnoreCase(currentContactSurname)) {
					matchedContacts++;
				}
			}
		}
		if (matchedContacts != currentContacts.size()) {
			System.err.println("NEW CONTACTS: ");
			System.err.println(newContacts);
			System.err.println("CURRENT CONTACTS");
			System.err.println(currentContacts);
			
			return "contact changes";
		}

		// then descriptions data
		List<InitiativeDescription> currentDescriptions = getDescriptions();
		Map<String, InitiativeDescription> currentDescriptionsMap = new HashMap<String, InitiativeDescription>();
		for (InitiativeDescription currentDescription : currentDescriptions) {
			currentDescriptionsMap.put(currentDescription.getLanguage().getCode(), currentDescription);
		}
		List<InitiativeDescription> newDescriptions = initiativeDescriptionTransformer
				.transformList(parsedObjects.getDescriptions());

		// online rules: no removals. no updates on existing descriptions, only new
		// descriptions allowed
		String changesDetected = "";
		if (newDescriptions.size() >= currentDescriptions.size()) {
			for (InitiativeDescription newDescription : newDescriptions) {
				String languageCode = newDescription.getLanguage().getCode().toLowerCase();
				if (currentDescriptionsMap.keySet().contains(languageCode)) {
					// update of existing one check
					InitiativeDescription currentDescription = currentDescriptionsMap.get(languageCode);
					if (!newDescription.getObjectives().equals(currentDescription.getObjectives())) {
						changesDetected = "objectives";
					} else if (!newDescription.getTitle().equals(currentDescription.getTitle())) {
						changesDetected = "title";
					} else if (currentDescription.getUrl() != null && newDescription.getUrl() != null) {
						if (!currentDescription.getUrl().equals(newDescription.getUrl())) {
							changesDetected = "url";
						}
					} else if (currentDescription.getPartialRegistration() != null
							&& newDescription.getPartialRegistration() != null) {
						if (!currentDescription.getPartialRegistration()
								.equals(newDescription.getPartialRegistration())) {
							changesDetected = "partialRegistration";
						}
					} else if (currentDescription.getUrl() != null && newDescription.getUrl() == null) {
						changesDetected = "url";
					} else if (newDescription.getIsDefault() != currentDescription.getIsDefault()) {
						changesDetected = "default language";
					} else if (!prefs.getCommissionRegisterUrl().equals(parsedObjects.getEcRegisterUrl())) {
						changesDetected = "register url";
					} else if (!prefs.getRegistrationDate().equals(parsedObjects.getRegistrationDate())) {
						changesDetected = "registration date";
					} else if (!prefs.getRegistrationNumber().equals(parsedObjects.getEcRegistrationNumber())) {
						changesDetected = "registration number";
					}
				} else {
					// new description: allowed, except new default
					if (newDescription.getIsDefault() == InitiativeDescription.IS_DEFAULT) {
						changesDetected = "new default";
					}
				}
			}
		} else {
			changesDetected = "language removal";
		}

		return changesDetected;
	}

	private void updateSystemPreferences(SystemPreferences systemPreferences, XMLdescriptionParsingResult parsedObjects)
			throws OCTException {
		SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd");
		try {
			systemPreferences.setRegistrationDate(sp.parse(parsedObjects.getRegistrationDate()));
		} catch (ParseException e) {
			logger.error("setRegistrationDate: " + e.getMessage(), e);
			throw new OCTException(e.getMessage(), e);
		}
		try {
			systemPreferences.setDeadline(sp.parse(parsedObjects.getDeadline()));
		} catch (ParseException e) {
			logger.error("setDeadlineDate: " + e.getMessage(), e);
			throw new OCTException(e.getMessage(), e);
		}
		systemPreferences.setRegistrationNumber(parsedObjects.getEcRegistrationNumber());
		systemPreferences.setCommissionRegisterUrl(parsedObjects.getEcRegisterUrl());
	}

	@Override
	public void deleteAllDescriptions() throws OCTException {
		try {
			for (InitiativeDescription id : getDescriptions()) {
				initiativeDAO.deleteDescription(id);
			}
		} catch (PersistenceException e) {
			logger.error("persistence problem while deleting all descriptions", e);
			throw new OCTException("persistence problem while deleting all descriptions", e);
		}

	}

}
