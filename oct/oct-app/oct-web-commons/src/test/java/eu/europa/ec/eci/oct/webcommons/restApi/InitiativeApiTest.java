package eu.europa.ec.eci.oct.webcommons.restApi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.eci.oct.entities.admin.Contact;
import eu.europa.ec.eci.oct.entities.admin.ContactRole;
import eu.europa.ec.eci.oct.entities.admin.InitiativeDescription;
import eu.europa.ec.eci.oct.entities.system.Country;
import eu.europa.ec.eci.oct.utils.CommonsConstants;
import eu.europa.ec.eci.oct.webcommons.services.api.InitiativeApi;
import eu.europa.ec.eci.oct.webcommons.services.api.RestApiParent;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.ApiResponse;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.FileType;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.commons.LongDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.contact.ContactDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.contact.ContactsDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.initiative.InitiativeDescriptionDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.initiative.InitiativeDescriptionsDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.initiative.InitiativeInfoDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.language.LanguageDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.language.LanguageDTOlist;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.signature.SignatureMetadata;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.systemManager.StepStateChangeDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.systemManager.StepStateChangeTypeDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.systemManager.SystemStateDTO;
import eu.europa.ec.eci.oct.webcommons.services.enums.CountryEnum;
import eu.europa.ec.eci.oct.webcommons.services.enums.LanguageEnum;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/rest-api-test.xml")
@Transactional()
public class InitiativeApiTest extends RestApiTest {

	public static final String INITIATIVE = "/initiative";

	final String NEW_INITIATIVE_DESCRIPTION_LANG_CODE = LanguageEnum.DUTCH.getLangCode();

	@Autowired
	DataSource dataSourceTest;

	@Test
	public void ini02() {
		// getDefaultDescription
		try {
			Response response = target(INITIATIVE + "/description").request().get(Response.class);
			assertEquals(Status.OK.getStatusCode(), response.getStatus());
			InitiativeDescriptionsDTO initiativeDescriptionsDTO = response.readEntity(InitiativeDescriptionsDTO.class);
			assertNotNull(initiativeDescriptionsDTO);
			InitiativeDescriptionDTO initiativeDescriptionDTO = initiativeDescriptionsDTO.getDescriptions().get(0);
			assertEquals(new Long(1), initiativeDescriptionDTO.getId());
			assertEquals(MOCK_DEFAULT_DESCRIPTION_LANGUAGE_CODE, initiativeDescriptionDTO.getLanguageCode());
			assertEquals(MOCK_DEFAULT_DESCRIPTION_OBJECTIVES, initiativeDescriptionDTO.getObjectives());
			assertEquals(MOCK_DEFAULT_DESCRIPTION_TITLE, initiativeDescriptionDTO.getTitle());
			assertEquals(MOCK_DEFAULT_DESCRIPTION_URL, initiativeDescriptionDTO.getUrl());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void ini03() {
		// get all descriptions
		try {
			InitiativeDescriptionsDTO initiativeDescriptionsDTO = getAllDescriptions();
			assertNotNull(initiativeDescriptionsDTO.getInitiativeInfo());
			InitiativeInfoDTO info = initiativeDescriptionsDTO.getInitiativeInfo();

			assertNotNull(info.getOrganizers());
			List<Contact> daoContacts = contactService.getAllContacts();
			assertEquals(daoContacts.size(), info.getOrganizers().size());
			for (ContactDTO contactDTO : info.getOrganizers()) {
				if (contactDTO.getRole().equalsIgnoreCase(ContactRole.REPRESENTATIVE)
						&& contactDTO.getRole().equalsIgnoreCase(ContactRole.SUBSTITUTE)) {
					assertFalse(StringUtils.isBlank(contactDTO.getEmail()));
				}
				assertFalse(StringUtils.isBlank(contactDTO.getFamilyName()));
				assertFalse(StringUtils.isBlank(contactDTO.getFirstName()));
				assertFalse(StringUtils.isBlank(contactDTO.getRole()));
			}
			assertNotNull(initiativeDescriptionsDTO.getDescriptions());
			List<InitiativeDescriptionDTO> descriptions = initiativeDescriptionsDTO.getDescriptions();
			assertEquals(MOCK_DESCRIPTIONS_SIZE, descriptions.size());
			boolean foundOriginal = false;
			for (InitiativeDescriptionDTO initiativeDescriptionDTO : descriptions) {
				if (initiativeDescriptionDTO.isOriginal()) {
					foundOriginal = true;
					assertEquals(MOCK_DEFAULT_DESCRIPTION_LANGUAGE_CODE, initiativeDescriptionDTO.getLanguageCode());
					assertEquals(MOCK_DEFAULT_DESCRIPTION_OBJECTIVES, initiativeDescriptionDTO.getObjectives());
					assertEquals(MOCK_DEFAULT_DESCRIPTION_TITLE, initiativeDescriptionDTO.getTitle());
					assertEquals(MOCK_DEFAULT_DESCRIPTION_URL, initiativeDescriptionDTO.getUrl());
				}
			}
			assertTrue(foundOriginal);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void ini04() {
		String GET_BY_ID = "/descriptionById/";
		// bad id param block check
		try {
			Response response = target(INITIATIVE + GET_BY_ID + "*").request().get(Response.class);
			assertEquals(Status.EXPECTATION_FAILED.getStatusCode(), response.getStatus());
			ApiResponse apiResponse = response.readEntity(ApiResponse.class);
			assertEquals(ApiResponse.ERROR, apiResponse.getStatus());
			assertEquals(Status.EXPECTATION_FAILED.getStatusCode(), apiResponse.getCode());
			assertEquals(InitiativeApi.INI04 + RestApiParent.INPUT_PARAMS_EXPECTATION_FAILED, apiResponse.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		// good id param but not matching any init desc
		try {
			Response response = target(INITIATIVE + GET_BY_ID + "9999").request().get(Response.class);
			assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
			ApiResponse apiResponse = response.readEntity(ApiResponse.class);
			assertEquals(ApiResponse.ERROR, apiResponse.getStatus());
			assertEquals(Status.NO_CONTENT.getStatusCode(), apiResponse.getCode());
			assertEquals(InitiativeApi.INI04 + RestApiParent.OBJECT_NOT_FOUND_FOR_PARAM + "9999",
					apiResponse.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		// get description by id
		try {
			Response response = target(INITIATIVE + GET_BY_ID + "1").request().get(Response.class);
			assertEquals(Status.OK.getStatusCode(), response.getStatus());
			InitiativeDescriptionsDTO initiativeDescriptionsDTO = response.readEntity(InitiativeDescriptionsDTO.class);
			assertNotNull(initiativeDescriptionsDTO.getDescriptions().get(0));
			assertNotNull(initiativeDescriptionsDTO.getInitiativeInfo());
			InitiativeDescriptionDTO initiativeDescriptionDTO = initiativeDescriptionsDTO.getDescriptions().get(0);
			assertEquals(new Long(1), initiativeDescriptionDTO.getId());
			assertEquals(MOCK_DEFAULT_DESCRIPTION_LANGUAGE_CODE, initiativeDescriptionDTO.getLanguageCode());
			assertEquals(MOCK_DEFAULT_DESCRIPTION_OBJECTIVES, initiativeDescriptionDTO.getObjectives());
			assertEquals(MOCK_DEFAULT_DESCRIPTION_TITLE, initiativeDescriptionDTO.getTitle());
			assertEquals(MOCK_DEFAULT_DESCRIPTION_URL, initiativeDescriptionDTO.getUrl());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void ini05() {
		String GET_BY_LANG_CODE = "/descriptionByLang/";

		// bad langCode param block check
		try {
			Response response = target(INITIATIVE + GET_BY_LANG_CODE + "*").request().get(Response.class);
			assertEquals(Status.EXPECTATION_FAILED.getStatusCode(), response.getStatus());
			ApiResponse apiResponse = response.readEntity(ApiResponse.class);
			assertEquals(ApiResponse.ERROR, apiResponse.getStatus());
			assertEquals(Status.EXPECTATION_FAILED.getStatusCode(), apiResponse.getCode());
			assertEquals(InitiativeApi.INI05 + RestApiParent.INPUT_PARAMS_EXPECTATION_FAILED + "*",
					apiResponse.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		// good id param but not matching any init desc
		try {
			Response response = target(INITIATIVE + GET_BY_LANG_CODE + "xx").request().get(Response.class);
			assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
			ApiResponse apiResponse = response.readEntity(ApiResponse.class);
			assertEquals(ApiResponse.ERROR, apiResponse.getStatus());
			assertEquals(Status.NO_CONTENT.getStatusCode(), apiResponse.getCode());
			assertEquals(InitiativeApi.INI05 + RestApiParent.OBJECT_NOT_FOUND_FOR_PARAM + "xx",
					apiResponse.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		// get description by language
		Response response = target(INITIATIVE + "/descriptionByLang/en").request().get(Response.class);
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		InitiativeDescriptionsDTO initiativeDescriptionsDTO = response.readEntity(InitiativeDescriptionsDTO.class);
		assertNotNull(initiativeDescriptionsDTO);
		assertNotNull(initiativeDescriptionsDTO.getDescriptions());
		assertNotNull(initiativeDescriptionsDTO.getDescriptions().get(0));
		InitiativeDescriptionDTO initiativeDescriptionDTO = initiativeDescriptionsDTO.getDescriptions().get(0);
		assertNotNull(initiativeDescriptionsDTO.getInitiativeInfo());
		assertEquals(new Long(1), initiativeDescriptionDTO.getId());
		assertEquals(MOCK_DEFAULT_DESCRIPTION_LANGUAGE_CODE, initiativeDescriptionDTO.getLanguageCode());
		assertEquals(MOCK_DEFAULT_DESCRIPTION_OBJECTIVES, initiativeDescriptionDTO.getObjectives());
		assertEquals(MOCK_DEFAULT_DESCRIPTION_TITLE, initiativeDescriptionDTO.getTitle());
		assertEquals(MOCK_DEFAULT_DESCRIPTION_URL, initiativeDescriptionDTO.getUrl());
	}

	@Test
	public void ini06() throws OCTException {
		// get contacts

		Response response = target(INITIATIVE + "/contacts").request().get(Response.class);
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		ContactsDTO contactsDTO = response.readEntity(ContactsDTO.class);
		assertNotNull(contactsDTO);
		List<Contact> contactsDAO = contactService.getAllContacts();
		assertEquals(contactsDAO.size(), contactsDTO.getContactDTOlist().size());
		for (ContactDTO contactDTO : contactsDTO.getContactDTOlist()) {
			if (contactDTO.getRole().equalsIgnoreCase(ContactRole.REPRESENTATIVE)
					&& contactDTO.getRole().equalsIgnoreCase(ContactRole.SUBSTITUTE)) {
				assertFalse(StringUtils.isBlank(contactDTO.getEmail()));
			}
			assertFalse(StringUtils.isBlank(contactDTO.getFamilyName()));
			assertFalse(StringUtils.isBlank(contactDTO.getFirstName()));
			assertFalse(StringUtils.isBlank(contactDTO.getRole()));
		}
	}

	@Test
	public void ini08Offline() throws OCTException {
		/*
		 * get days Left in offline mode should return default defined in
		 * CommonsConstants.DAYS_LEFT_DEFAULT
		 */

		Response response = target(INITIATIVE + "/daysleft").request().get(Response.class);
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		LongDTO longDTO = response.readEntity(LongDTO.class);
		assertNotNull(longDTO);
		assertNotNull(longDTO.getValue());
		assertEquals(CommonsConstants.DAYS_LEFT_DEFAULT, longDTO.getValue());
	}

	@Test
	public void ini08Online() throws OCTException, SQLException {
		/*
		 * get days Left online mode should return the difference between today and
		 * expire date
		 */
		StepStateChangeDTO stepStateChangeDTO = new StepStateChangeDTO();
		stepStateChangeDTO.setActive(true);
		stepStateChangeDTO.setStep(StepStateChangeTypeDTO.STRUCTURE.name());
		setStepState(stepStateChangeDTO);
		assertTrue(getStepState().getStructure());

		Response response = target(INITIATIVE + "/daysleft").request().get(Response.class);
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		LongDTO longDTO = response.readEntity(LongDTO.class);
		assertNotNull(longDTO);
		assertNotNull(longDTO.getValue());

		long nowTime = new Date().getTime();
		long expireTime = systemManager.getSystemPreferences().getDeadline().getTime();
		assertTrue(expireTime > nowTime);
		long daysLeft = TimeUnit.DAYS.convert(expireTime - nowTime, TimeUnit.MILLISECONDS);

		assertEquals(daysLeft, longDTO.getValue());

		restoreMockRestStepState();
	}

	@Test
	public void ini09() throws OCTException {
		// get available languages for descriptions
		int expectedAvailableLanguages = initiativeService.getLanguagesForAvailableDescriptions().size();
		Response response = target(INITIATIVE + "/languages").request().get(Response.class);
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		LanguageDTOlist languageDTOlist = response.readEntity(LanguageDTOlist.class);
		assertNotNull(languageDTOlist);
		assertNotNull(languageDTOlist.getLanguages());
		assertFalse(languageDTOlist.getLanguages().isEmpty());

		for (LanguageDTO languageDTO : languageDTOlist.getLanguages()) {
			assertNotNull(languageDTO.getDisplayOrder());
			assertNotNull(languageDTO.getLanguageCode());
			assertNotNull(languageDTO.getLanguageName());
		}

		assertEquals(expectedAvailableLanguages, languageDTOlist.getLanguages().size());
	}

	@Test
	public void ini01XXEvulnerability() throws OCTException {

		// Upload a bad xml description with malicious code
		File xmlDescription = new File(this.getClass().getResource("/file/description_xxe.xml").getFile());
		if (!xmlDescription.exists()) {
			fail("No description.xml found in " + TEST_FILE_STORAGE_PATH + ": can't run the test.");
		}
		try {
			initiativeService.parseXMLdescription(xmlDescription);
			fail("XXE vulnerability active!");
		} catch (Exception e1) {
			e1.printStackTrace();
			// we're safe! for now...
		}
	}

	@Test
	public void ini01BillionLaughsEvulnerability() throws OCTException {

		// Upload a bad xml description with malicious code
		File xmlDescription = new File(this.getClass().getResource("/file/description_billionLaughs.xml").getFile());
		if (!xmlDescription.exists()) {
			fail("No description.xml found in " + TEST_FILE_STORAGE_PATH + ": can't run the test.");
		}
		try {
			initiativeService.parseXMLdescription(xmlDescription);
			fail("XXE vulnerability active!");
		} catch (Exception e1) {
			e1.printStackTrace();
			// we're safe! for now...
		}
	}

	@Test
	public void ini01XMLoffline() throws Exception {

		/*
		 * The mocked data has 2 initiative descriptions: one in english (default) and
		 * one in italian. The descripion xml to be uploaded with test has 2
		 * descriptions, the default is polish (change with english), the other is
		 * spanish (italian has been removed) In offline mode we are allowed to do that,
		 * even with associated signatures to the removed one.
		 */

		// insert signature to italy that will be removed
		List<Country> countriesToSignFor = Arrays.asList(allCountriesMap.get(CountryEnum.ITALY.getCode()));
		int signaturesToAdd = 1;
		insertTestSignatures(countriesToSignFor, signaturesToAdd, false);

		List<SignatureMetadata> recentSignatories = getLastSignatures();
		assertEquals(allMockedSignatures.size() + signaturesToAdd, recentSignatories.size());
		boolean newSignatureIsPresent = false;
		for (SignatureMetadata smd : recentSignatories) {
			if (smd.getCountry().equals(CountryEnum.ITALY.getCode())) {
				newSignatureIsPresent = true;
			}
		}
		assertTrue(newSignatureIsPresent);

		SystemStateDTO currentSystemStateDTO = getSystemState();
		assertFalse(currentSystemStateDTO.isOnline());
		Response response = insertXML(currentSystemStateDTO.isOnline(),
				"/file/description_removal_and_default_change.xml");

		assertEquals(response.getStatus(), Status.OK.getStatusCode());

		/*
		 * we expect now no italian description, no signatures at all, one polish
		 * default and one spanish
		 */

		List<InitiativeDescriptionDTO> descriptionsAfterInsertXML = getAllDescriptions().getDescriptions();
		assertTrue(descriptionsAfterInsertXML.size() == 2);
		for (InitiativeDescriptionDTO descriptionAfterXMLinsert : descriptionsAfterInsertXML) {
			String languageCode = descriptionAfterXMLinsert.getLanguageCode();
			if (!languageCode.equals(LanguageEnum.SPANISH.getLangCode())
					&& !languageCode.equals(LanguageEnum.POLISH.getLangCode())) {
				fail("unexpected description present: " + languageCode);
			}
			if (languageCode.equals(LanguageEnum.POLISH.getLangCode())) {
				assertTrue(descriptionAfterXMLinsert.isOriginal());
			} else {
				assertFalse(descriptionAfterXMLinsert.isOriginal());
			}
		}
		assertTrue(getProgressionStatus() == 0l);

		restoreMockRestSystemPreferences();
		restoreMockRestTestDescriptions();
		removeNewSignatures();
		restoreMockRestStepState();
	}

	@Test
	public void ini01XMLonline() throws Exception {
		restoreMockRestSystemPreferences();
		/*
		 * The mocked data has 2 initiative descriptions: one in english (default) and
		 * one in italian. In online mode is allowed only to add new descriptions. We
		 * test that removals and updates of existing one are blocked
		 */

		// lets go online first
		goOnline();
		SystemStateDTO currentSystemStateDTO = getSystemState();
		assertTrue(currentSystemStateDTO.isOnline());

		List<InitiativeDescriptionDTO> descriptionsBefore = getAllDescriptions().getDescriptions();
		InitiativeDescription originalEnglishDescription = allInitiativeDescriptionsMap
				.get(LanguageEnum.ENGLISH.getLangCode());
		InitiativeDescription originalItalianDescription = allInitiativeDescriptionsMap
				.get(LanguageEnum.ITALIAN.getLangCode());

		// update existing ones (title, subject and url)
		Response response = insertXML(currentSystemStateDTO.isOnline(), "/file/description_with_updates.xml");
		assertEquals(Status.CONFLICT.getStatusCode(), response.getStatus());

		List<InitiativeDescriptionDTO> descriptionsAfterUpdate = getAllDescriptions().getDescriptions();
		// no changes expected
		assertEquals(descriptionsBefore.size(), descriptionsAfterUpdate.size());
		for (InitiativeDescriptionDTO descriptionAfterUpdate : descriptionsAfterUpdate) {
			InitiativeDescription originalToBeComparedWith = null;
			if (descriptionAfterUpdate.getLanguageCode().equals(LanguageEnum.ENGLISH.getLangCode())) {
				originalToBeComparedWith = originalEnglishDescription;
			} else {
				originalToBeComparedWith = originalItalianDescription;
			}
			if (originalToBeComparedWith.getIsDefault() == 1) {
				assertTrue(descriptionAfterUpdate.isOriginal());
			} else {
				assertFalse(descriptionAfterUpdate.isOriginal());
			}
			assertEquals(originalToBeComparedWith.getObjectives(), descriptionAfterUpdate.getObjectives());
			assertEquals(originalToBeComparedWith.getTitle(), descriptionAfterUpdate.getTitle());
			assertEquals(originalToBeComparedWith.getUrl(), descriptionAfterUpdate.getUrl());
			assertEquals(originalToBeComparedWith.getPartialRegistration(),
					descriptionAfterUpdate.getPartialRegistration());
		}

		// lets try removal and change of default
		response = insertXML(currentSystemStateDTO.isOnline(), "/file/description_removal_and_default_change.xml");
		assertEquals(Status.CONFLICT.getStatusCode(), response.getStatus());
		List<InitiativeDescriptionDTO> descriptionsAfterRemoval = getAllDescriptions().getDescriptions();

		// no changes expected
		assertEquals(descriptionsBefore.size(), descriptionsAfterRemoval.size());
		for (InitiativeDescriptionDTO descriptionAfterRemoval : descriptionsAfterRemoval) {
			InitiativeDescription originalToBeComparedWith = null;
			if (descriptionAfterRemoval.getLanguageCode().equals(LanguageEnum.ENGLISH.getLangCode())) {
				originalToBeComparedWith = originalEnglishDescription;
			} else {
				originalToBeComparedWith = originalItalianDescription;
			}
			if (originalToBeComparedWith.getIsDefault() == 1) {
				assertTrue(descriptionAfterRemoval.isOriginal());
			} else {
				assertFalse(descriptionAfterRemoval.isOriginal());
			}
			assertEquals(originalToBeComparedWith.getObjectives(), descriptionAfterRemoval.getObjectives());
			assertEquals(originalToBeComparedWith.getTitle(), descriptionAfterRemoval.getTitle());
			assertEquals(originalToBeComparedWith.getUrl(), descriptionAfterRemoval.getUrl());
			assertEquals(originalToBeComparedWith.getPartialRegistration(),
					descriptionAfterRemoval.getPartialRegistration());
		}

		// changes on registration number
		response = insertXML(currentSystemStateDTO.isOnline(), "/file/description_with_system_data_changes2.xml");
		assertEquals(Status.CONFLICT.getStatusCode(), response.getStatus());
		assertEquals(MOCK_REGISTRATION_DATE, systemManager.getSystemPreferences().getRegistrationDate());
		assertEquals(MOCK_REGISTRATION_NUMBER, systemManager.getSystemPreferences().getRegistrationNumber());

		// changes on system data (registration date/number, etc)
		response = insertXML(currentSystemStateDTO.isOnline(), "/file/description_with_system_data_changes.xml");
		assertEquals(Status.CONFLICT.getStatusCode(), response.getStatus());
		assertEquals(MOCK_REGISTRATION_DATE, systemManager.getSystemPreferences().getRegistrationDate());
		assertEquals(MOCK_REGISTRATION_NUMBER, systemManager.getSystemPreferences().getRegistrationNumber());

		// lets try add a new description (spanish) leaving the others untouched
		response = insertXML(currentSystemStateDTO.isOnline(), "/file/description_with_addition.xml");
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		List<InitiativeDescriptionDTO> descriptionsAfterAddingNewOne = getAllDescriptions().getDescriptions();

		assertTrue(descriptionsAfterAddingNewOne.size() == descriptionsAfterUpdate.size() + 1);

		// one new description expected, the others equals
		for (InitiativeDescriptionDTO descriptionAfter : descriptionsAfterAddingNewOne) {
			String languageCode = descriptionAfter.getLanguageCode();
			if (!languageCode.equals(LanguageEnum.ENGLISH.getLangCode())
					&& !languageCode.equals(LanguageEnum.ITALIAN.getLangCode())) {
				// new one detected, expected spanish
				assertEquals(LanguageEnum.SPANISH.getLangCode(), languageCode);
			} else {
				// existing one detected, should be equal to original one
				InitiativeDescription originalDescriptionToCompareWith = null;
				if (languageCode.equals(LanguageEnum.ENGLISH.getLangCode())) {
					originalDescriptionToCompareWith = originalEnglishDescription;
				} else if (languageCode.equals(LanguageEnum.ITALIAN.getLangCode())) {
					originalDescriptionToCompareWith = originalItalianDescription;
				}
				if (originalDescriptionToCompareWith.getIsDefault() == 1) {
					assertTrue(descriptionAfter.isOriginal());
				} else {
					assertFalse(descriptionAfter.isOriginal());
				}
				assertEquals(originalDescriptionToCompareWith.getObjectives(), descriptionAfter.getObjectives());
				assertEquals(originalDescriptionToCompareWith.getTitle(), descriptionAfter.getTitle());
				assertEquals(originalDescriptionToCompareWith.getUrl(), descriptionAfter.getUrl());
				assertEquals(originalDescriptionToCompareWith.getPartialRegistration(),
						descriptionAfter.getPartialRegistration());
			}
		}

		restoreMockRestSystemPreferences();
		restoreMockRestTestDescriptions();
		restoreMockRestTestFeedbacks();
		restoreMockRestStepState();

	}

	private Response insertXML(boolean isOnline, String filePath) throws OCTException {
		File xmlDescription = new File(this.getClass().getResource(filePath).getFile());
		if (!xmlDescription.exists()) {
			fail("No description.xml found in " + filePath + ": can't run the test.");
		}
		FormDataMultiPart multipart = getMultipartFile(filePath, xmlDescription);

		Response response = null;
		try {
			response = target(INITIATIVE + "/insertXML").request()
					.header(HttpHeaders.AUTHORIZATION, BEARER + authorizationToken)
					.post(Entity.entity(multipart, multipart.getMediaType()));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		return response;

	}

	private FormDataMultiPart formDataMultiPart;

	private FormDataMultiPart getMultipartFile(String filePath, File xmlDescription) {

		FileDataBodyPart filePart = new FileDataBodyPart("file", xmlDescription);
		formDataMultiPart = new FormDataMultiPart();
		FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart
				.field("type", FileType.DESCRIPTION.fileType()).bodyPart(filePart);
		return multipart;
	}

}