package eu.europa.ec.eci.oct.webcommons.services.initiative;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europa.ec.eci.oct.entities.admin.Contact;
import eu.europa.ec.eci.oct.entities.admin.InitiativeDescription;
import eu.europa.ec.eci.oct.entities.system.Language;
import eu.europa.ec.eci.oct.utils.CommonsConstants;
import eu.europa.ec.eci.oct.webcommons.services.commons.ServicesTest;
import eu.europa.ec.eci.oct.webcommons.services.enums.LanguageEnum;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;

@RunWith(SpringJUnit4ClassRunner.class)
public class InitiativeServiceImplTest extends ServicesTest {

	@Test
	public void getDefaultDescription() throws Exception {
		logger.info(">> Started getDefaultDescription test");
		initiativeService.setDefaultDescription(testInitiativeDescription);
		String defaultDescription = testInitiativeDescription.getTitle();
		InitiativeDescription initiativeDescription = initiativeService.getDefaultDescription();
		assertNotNull(initiativeDescription);
		assertEquals(initiativeDescription.getTitle(), defaultDescription);
		logger.info(">> Ended getDefaultDescription test");
	}

	@Test
	public void getDescriptions() throws Exception {
		logger.info(">> Started getDescriptions test");
		List<InitiativeDescription> descs = initiativeService.getDescriptions();
		assertNotNull(descs);
		assertFalse(descs.isEmpty());
		logger.info(">> Ended getDescriptions test");
	}

	@Test
	public void getDescriptionsExcludeDefault() throws Exception {
		logger.info(">> Started getDescriptionsExcludeDefault test");
		initiativeService.setDefaultDescription(testInitiativeDescription);
		List<InitiativeDescription> descs = initiativeService.getDescriptionsExcludeDefault();
		assertNotNull(descs);
		// only the default stays
		assertTrue(descs.size() == 1);
		logger.info(">> Ended getDescriptionsExcludeDefault test");
	}

	@Test
	public void getDescriptionById() throws Exception {
		logger.info(">> Started getDescriptionById test");
		InitiativeDescription initiativeDescription = initiativeService
				.getDescriptionById(testInitiativeDescription.getId());
		assertEquals(initiativeDescription, testInitiativeDescription);
		logger.info(">> Ended getDescriptionById test");
	}

	@Test
	public void getDescriptionByLang() throws Exception {
		logger.info(">> Started getDescriptionByLang test");
		InitiativeDescription initiativeDescription = initiativeService
				.getDescriptionByLanguageCode(testInitiativeDescription.getLanguage().getCode());
		assertNotNull(initiativeDescription);
		assertEquals(testInitiativeDescription.getLanguage().getCode(), initiativeDescription.getLanguage().getCode());
		logger.info(">> Ended getDescriptionByLang test");
	}

	@Test
	public void getUnusedDescriptionLanguages() throws Exception {
		logger.info(">> Started getUnusedDescriptionLanguages test");
		List<Language> availableLanguages = systemManager.getAllLanguages();
		assertNotNull(availableLanguages);
		assertFalse(availableLanguages.isEmpty());
		List<Language> unusedLanguages = initiativeService.getUnusedDescriptionLanguages();
		assertNotNull(unusedLanguages);
		int usedLanguages = initiativeService.getDescriptions().size();
		assertTrue(availableLanguages.size() - usedLanguages == unusedLanguages.size());
		logger.info(">> Ended getUnusedDescriptionLanguages test");
	}

	public void getLanguagesForAvailableDescriptions() throws Exception {
		logger.info(">> Started getLanguagesForAvailableDescriptions test");
		String code = LanguageEnum.ENGLISH.getLangCode();
		List<Language> langs = initiativeService.getLanguagesForAvailableDescriptions();
		assertNotNull(langs);
		assertNotNull(langs.get(0));
		assertNotNull(langs.get(0).getCode());
		assertEquals(langs.get(0).getCode(), code);
		logger.info(">> Ended getLanguagesForAvailableDescriptions test");
	}

	@Test
	public void shouldOverwriteAlreadyExistentInitiativeDescription() throws OCTException {
		logger.info(">> Started shouldOverwriteAlreadyExistentInitiativeDescription test");

		// test with already existent -> should overwrite
		InitiativeDescription alreadyExistentInitiativeDescription = testInitiativeDescription;
		InitiativeDescription overwrittenInitiativeDescription = null;
		try {
			initiativeService.saveOrUpdateInitiativeDescription(alreadyExistentInitiativeDescription);
			overwrittenInitiativeDescription = initiativeService
					.getDescriptionByLanguageCode(alreadyExistentInitiativeDescription.getLanguage().getCode());
		} catch (OCTException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		assertNotNull(overwrittenInitiativeDescription);
		Long testInitiativeDescriptionId = testInitiativeDescription.getId();
		Long overwrittenInitiativeDescriptionId = overwrittenInitiativeDescription.getId();
		/*
		 * the default initiativeDescription created in the setup of the test has
		 * English as language
		 */
		assertEquals(LanguageEnum.ENGLISH.getLangCode(), overwrittenInitiativeDescription.getLanguage().getCode());
		assertTrue(testInitiativeDescriptionId == overwrittenInitiativeDescriptionId);

		logger.info(">> Ended shouldOverwriteAlreadyExistentInitiativeDescription test");
	}

	@Test
	public void shouldSaveNewInitiativeDescription() throws OCTException {
		logger.info(">> Started shouldSaveNewInitiativeDescription test");

		// create a new initiativeDescription
		InitiativeDescription newInitiativeDescription = new InitiativeDescription();
		List<Language> languagesForAvailableDescriptions = initiativeService.getLanguagesForAvailableDescriptions();
		assertFalse(languagesForAvailableDescriptions.isEmpty());
		Language newLanguage = systemManager.getLanguageByCode(LanguageEnum.BULGARIAN.getLangCode());
		newInitiativeDescription.setLanguage(newLanguage);
		newInitiativeDescription.setTitle("blabla");
		InitiativeDescription savedNewInitiativeDescription = null;
		try {
			initiativeService.saveOrUpdateInitiativeDescription(newInitiativeDescription);
		} catch (OCTException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		savedNewInitiativeDescription = initiativeService.getDescriptionByLanguageCode(newLanguage.getCode());
		assertNotNull(savedNewInitiativeDescription);
		Long testInitiativeDescriptionId = testInitiativeDescription.getId();
		Long savedNewInitiativeDescriptionId = savedNewInitiativeDescription.getId();
		assertEquals(newLanguage.getCode(), savedNewInitiativeDescription.getLanguage().getCode());

		/*
		 * the automatic generation of the id proceed in sequential n+1 mode, so we
		 * expect that the new initiativeDescripion will have the id(n) = id(n-1) +1
		 */
		assertTrue(testInitiativeDescriptionId < savedNewInitiativeDescriptionId);
		logger.info(">> Ended shouldSaveNewInitiativeDescription test");
	}

	public void setDefaultDescription() throws OCTException {
		logger.info(">> Started setDefaultDescription test");
		/*
		 * let's update one field of the existing intiativeDescription and then check if
		 * then it will be retrieved as expected
		 */
		String newTitle = "NEW_DEFAULT_TITLE";
		testInitiativeDescription.setTitle(newTitle);
		initiativeService.setDefaultDescription(testInitiativeDescription);

		InitiativeDescription defaultDescription = null;
		try {
			defaultDescription = initiativeService.getDefaultDescription();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		assertEquals(newTitle, defaultDescription.getTitle());

		logger.info(">> Ended setDefaultDescription test");
	}

	@Test
	public void overwriteSetup() throws OCTException {
		logger.info(">> Started overwriteSetup test");
		/*
		 * change the already existent db entities, overwrite and check if everything
		 * has been overwritten without recreation
		 */

		int sizeOfDescriptionsBeforeOverwriting = initiativeService.getDescriptions().size();

		/*
		 * create a new initiativeDescription and insert it in the list for the
		 * overwriting (recreate = false)
		 */
		InitiativeDescription newInitiativeDescriptionToBeRecreated = new InitiativeDescription();
		newInitiativeDescriptionToBeRecreated.setTitle("blabla");
		Language newDefaultLang = systemManager.getLanguageByCode(LanguageEnum.CROATIAN.getLangCode());
		newInitiativeDescriptionToBeRecreated.setLanguage(newDefaultLang);
		List<InitiativeDescription> newInitiativeDescriptionsList = new ArrayList<InitiativeDescription>();
		newInitiativeDescriptionsList.add(newInitiativeDescriptionToBeRecreated);
		newInitiativeDescriptionsList.addAll(testAllDescriptions);

		try {
			initiativeService.onlineInitiativeDescriptionUpdate(systemManager.getSystemPreferences(), testContacts,
					newInitiativeDescriptionsList);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		int sizeOfDescriptionsAfterOverwriting = initiativeService.getDescriptions().size();

		/*
		 * check that the new description size is not the same of the list we sent,
		 * because we expect the old description will not be removed and the new one
		 * will be added
		 */
		List<InitiativeDescription> currentInitiativeDescriptions = initiativeService.getDescriptions();
		assertTrue(sizeOfDescriptionsAfterOverwriting == sizeOfDescriptionsBeforeOverwriting + 1);

		List<InitiativeDescription> oldInitiativeDescriptions = new ArrayList<InitiativeDescription>();
		InitiativeDescription newInitiativeDescription = null;
		for (InitiativeDescription initD : currentInitiativeDescriptions) {
			/*
			 * match the initiativeDescription we are looking for by the lang code
			 */
			if (initD.getLanguage().getCode().equals(newDefaultLang.getCode())) {
				newInitiativeDescription = initD;
			} else {
				oldInitiativeDescriptions.add(initD);
			}
		}
		assertNotNull(newInitiativeDescription);
		assertFalse(oldInitiativeDescriptions.isEmpty());

		/*
		 * check that the new description has been added
		 */
		assertEquals(newInitiativeDescriptionToBeRecreated.getObjectives(), newInitiativeDescription.getObjectives());
		assertEquals(newInitiativeDescriptionToBeRecreated.getTitle(), newInitiativeDescription.getTitle());
		assertEquals(newInitiativeDescriptionToBeRecreated.getUrl(), newInitiativeDescription.getUrl());
		assertEquals(newInitiativeDescriptionToBeRecreated.getLanguage(), newInitiativeDescription.getLanguage());
		assertEquals(LanguageEnum.CROATIAN.getLangCode(), newInitiativeDescription.getLanguage().getCode());

		logger.info(">> Ended overwriteSetup test");
	}

	@Test
	public void daysLeftOnline() throws OCTException {
		// set online first, otherwise the default value will be returned
		systemManager.setStructureAsDone(true);

		long nowTime = new Date().getTime();
		Date eciDataTimestamp = systemManager.getSystemPreferences().getDeadline();
		assertNotNull(eciDataTimestamp);
		long expireTime = eciDataTimestamp.getTime();
		assertTrue(expireTime > nowTime);
		long expectedDaysLeft = TimeUnit.DAYS.convert(expireTime - nowTime, TimeUnit.MILLISECONDS);

		Long daysLeft = null;
		try {
			daysLeft = initiativeService.daysLeft();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		assertNotNull(daysLeft);
		assertEquals(expectedDaysLeft, daysLeft.longValue());
	}

	@Test
	public void daysLeftOffline() throws OCTException {
		long nowTime = new Date().getTime();
		Date eciDataTimestamp = systemManager.getSystemPreferences().getDeadline();
		assertNotNull(eciDataTimestamp);
		long expireTime = eciDataTimestamp.getTime();
		assertTrue(expireTime > nowTime);
		Long daysLeft = null;
		try {
			daysLeft = initiativeService.daysLeft();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		assertEquals(CommonsConstants.DAYS_LEFT_DEFAULT, daysLeft.intValue());
	}

	@Test
	public void saveContacts() throws Exception {

		List<Contact> contacts = contactService.getAllContacts();
		List<Contact> contactsEdited = new ArrayList<Contact>();
		for (Contact c : contacts) {
			Contact ec = new Contact();
			ec.setContactRole(c.getContactRole());
			ec.setFamilyName(c.getFamilyName());
			ec.setFirstName(c.getFirstName());
			ec.setEmail("newEmail" + c.getId());
			contactsEdited.add(ec);
		}
		try {
			contactService.saveContacts(contactsEdited);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
