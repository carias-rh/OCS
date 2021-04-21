package eu.europa.ec.eci.oct.webcommons.restApi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europa.ec.eci.oct.entities.admin.Feedback;
import eu.europa.ec.eci.oct.entities.admin.StepState;
import eu.europa.ec.eci.oct.entities.system.Country;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.country.CountryDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.country.CountryDTOlist;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.language.LanguageDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.language.LanguageDTOlist;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.report.FeedbackDTOs;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.report.ProgressionStatus;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.supportForm.SupportFormDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.systemManager.CollectorState;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.systemManager.InitiativeModeDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.systemManager.InitiativeModeTypeDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.systemManager.StepStateChangeDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.systemManager.StepStateChangeTypeDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.systemManager.SystemStateDTO;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/rest-api-test.xml")
public class SystemManagerApiTest extends RestApiTest {

	@Test
	public void man01() {
		getCollectingState();
	}

	@Test
	public void man02() throws OCTException, SQLException {
		// set collecting state
		setCollectingState(CollectorState.OFF);

		// reset collecting mode as default
		setCollectingState(CollectorState.ON);

		restoreMockRestSystemPreferences();

	}

	@Test
	public void man03() {
		// get all countries
		CountryDTOlist countryDTOlist = null;
		try {
			Response response = target(MANAGER + "/countries").request().get(Response.class);
			assertEquals(Status.OK.getStatusCode(), response.getStatus());
			countryDTOlist = response.readEntity(CountryDTOlist.class);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		assertNotNull(countryDTOlist);
		assertNotNull(countryDTOlist.getCountries());
		assertFalse(countryDTOlist.getCountries().isEmpty());
		List<CountryDTO> countries = countryDTOlist.getCountries();
		assertEquals(MOCK_COUNTRIES_SIZE, countries.size());
		for (CountryDTO countryDTO : countries) {
			assertNotNull(countryDTO.getCountryCode());
			assertEquals(2, countryDTO.getCountryCode().length());
			assertNotNull(countryDTO.getCountryName());
			assertFalse(countryDTO.getCountryName().isEmpty());
		}
	}

	@Test
	public void man04() {

		// get all languages
		LanguageDTOlist languageDTOlist = null;
		try {
			Response response = target(MANAGER + "/languages").request().get(Response.class);
			assertEquals(Status.OK.getStatusCode(), response.getStatus());
			languageDTOlist = response.readEntity(LanguageDTOlist.class);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		assertNotNull(languageDTOlist);
		assertNotNull(languageDTOlist.getLanguages());
		assertFalse(languageDTOlist.getLanguages().isEmpty());
		List<LanguageDTO> languages = languageDTOlist.getLanguages();
		assertEquals(MOCK_LANGUAGES_SIZE, languages.size());
		for (LanguageDTO languageDTO : languages) {
			assertNotNull(languageDTO.getLanguageCode());
			assertEquals(2, languageDTO.getLanguageCode().length());
			assertNotNull(languageDTO.getLanguageName());
			assertFalse(languageDTO.getLanguageName().isEmpty());
		}
	}

	@Test
	public void man05() throws OCTException, SQLException {

		// db state in mock script is SETUP, so we can change it
		SystemStateDTO systemState = getSystemState();
		boolean currentIsOnline = systemState.isOnline();
		boolean currentIsCollecting = systemState.isCollecting();
		assertTrue(currentIsOnline == initSystemIsOnline);
		assertTrue(currentIsCollecting == initSystemIsCollecting);

		// go online
		try {

			int signatureCountBefore = getSignatureCount();
			assertTrue(signatureCountBefore > 0);
			int feedbacksCountBefore = getFeedbacksCount();
			assertTrue(feedbacksCountBefore > 0);
			List<Feedback> allFeedbacksBefore = reportingService.getAllFeedbacks();
			assertFalse(allFeedbacksBefore.isEmpty());

			goOnline();

			int signatureCountAfter = getSignatureCount();
			assertTrue(signatureCountAfter == 0);
			int feedbacksCountAfter = getFeedbacksCount();
			assertTrue(feedbacksCountAfter == 0);

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		// restore DB mock status
		restoreMockRestSystemPreferences();
		restoreMockRestTestSignatures();
		restoreMockRestTestFeedbacks();
		restoreMockRestStepState();

	}

	private int getSignatureCount() {
		int signatureCount = 0;
		try {
			Response response = target(ReportingApiTest.REPORT + "/progression").request().get(Response.class);
			assertEquals(Status.OK.getStatusCode(), response.getStatus());
			ProgressionStatus ps = response.readEntity(ProgressionStatus.class);
			assertNotNull(ps);
			assertNotNull(ps.getSignatureCount());
			signatureCount = (int) ps.getSignatureCount();

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		return signatureCount;
	}

	private int getFeedbacksCount() {
		int feedbacksCount = 0;
		try {
			Response response = target(ReportingApiTest.REPORT + "/feedbacks").request().get(Response.class);
			assertEquals(Status.OK.getStatusCode(), response.getStatus());
			FeedbackDTOs feedbackDTOs = response.readEntity(FeedbackDTOs.class);
			assertNotNull(feedbackDTOs);
			assertNotNull(feedbackDTOs.getFeedbackDTOlist());
			feedbacksCount = feedbackDTOs.getFeedbackDTOlist().size();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		return feedbacksCount;
	}

	@Test
	public void getCountryPropertiesByCountryCode() throws OCTException {

		for (Country c : allCountries) {
			String countryCode = c.getCode();
			try {
				Response response = target(MANAGER + "/countryProperties/" + countryCode).request().get(Response.class);
				assertEquals(Status.OK.getStatusCode(), response.getStatus());
				List<SupportFormDTO> result = response.readEntity(new GenericType<List<SupportFormDTO>>() {
				});
				assertNotNull(result);
				assertFalse(result.isEmpty());
			} catch (Exception e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
		}
	}

	@Test
	public void testGetStepstateInitial() {
		Response response = target(MANAGER + "/stepstate").request().get(Response.class);
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		StepState result = response.readEntity(StepState.class);
		assertNotNull(result);
		assertEquals(1, result.getId().longValue());
		assertFalse(result.getStructure());
		assertFalse(result.getPersonalise());
		assertFalse(result.getSocial());
		assertFalse(result.getLive());
	}

	@Test
	public void testResetStepState() throws SQLException {
		// INITIAL STATE
		Response response = target(MANAGER + "/stepstate").request().get(Response.class);
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		StepState result = response.readEntity(StepState.class);
		assertNotNull(result);
		assertEquals(1, result.getId().longValue());
		assertFalse(result.getStructure());
		assertFalse(result.getPersonalise());
		assertFalse(result.getSocial());
		assertFalse(result.getLive());

		// set STRUCTURE true
		StepStateChangeDTO stepStateChangeDTO = new StepStateChangeDTO();
		stepStateChangeDTO.setActive(true);
		stepStateChangeDTO.setStep(StepStateChangeTypeDTO.STRUCTURE.name());

		response = target(MANAGER + "/setstepstate").request()
				.post(Entity.entity(stepStateChangeDTO, MediaType.APPLICATION_JSON));
		assertEquals(Status.OK.getStatusCode(), response.getStatus());

		// check STRUCTURE true
		response = target(MANAGER + "/stepstate").request().get(Response.class);
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		result = response.readEntity(StepState.class);
		assertNotNull(result);
		assertEquals(1, result.getId().longValue());
		assertEquals(true, result.getStructure());
		assertFalse(result.getPersonalise());
		assertFalse(result.getSocial());
		assertFalse(result.getLive());

		response = target(MANAGER + "/setstepstate").request()
				.post(Entity.entity(stepStateChangeDTO, MediaType.APPLICATION_JSON));
		assertEquals(Status.OK.getStatusCode(), response.getStatus());

		// set PERSONALISE true
		stepStateChangeDTO = new StepStateChangeDTO();
		stepStateChangeDTO.setActive(true);
		stepStateChangeDTO.setStep(StepStateChangeTypeDTO.PERSONALISE.name());

		response = target(MANAGER + "/setstepstate").request()
				.post(Entity.entity(stepStateChangeDTO, MediaType.APPLICATION_JSON));
		assertEquals(Status.OK.getStatusCode(), response.getStatus());

		// check PERSONALISE true
		response = target(MANAGER + "/stepstate").request().get(Response.class);
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		result = response.readEntity(StepState.class);
		assertNotNull(result);
		assertEquals(1, result.getId().longValue());
		assertEquals(true, result.getStructure());
		assertEquals(true, result.getPersonalise());
		assertFalse(result.getSocial());
		assertFalse(result.getLive());

		// set SOCIAL true
		stepStateChangeDTO = new StepStateChangeDTO();
		stepStateChangeDTO.setActive(true);
		stepStateChangeDTO.setStep(StepStateChangeTypeDTO.SOCIAL.name());

		response = target(MANAGER + "/setstepstate").request()
				.post(Entity.entity(stepStateChangeDTO, MediaType.APPLICATION_JSON));
		assertEquals(Status.OK.getStatusCode(), response.getStatus());

		// check SOCIAL true
		response = target(MANAGER + "/stepstate").request().get(Response.class);
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		result = response.readEntity(StepState.class);
		assertNotNull(result);
		assertEquals(1, result.getId().longValue());
		assertEquals(true, result.getStructure());
		assertEquals(true, result.getPersonalise());
		assertEquals(true, result.getSocial());
		assertFalse(result.getLive());

		// set LIVE true
		stepStateChangeDTO = new StepStateChangeDTO();
		stepStateChangeDTO.setActive(true);
		stepStateChangeDTO.setStep(StepStateChangeTypeDTO.LIVE.name());

		response = target(MANAGER + "/setstepstate").request()
				.post(Entity.entity(stepStateChangeDTO, MediaType.APPLICATION_JSON));
		assertEquals(Status.OK.getStatusCode(), response.getStatus());

		// check LIVE true
		response = target(MANAGER + "/stepstate").request().get(Response.class);
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		result = response.readEntity(StepState.class);
		assertNotNull(result);
		assertEquals(1, result.getId().longValue());
		assertEquals(true, result.getStructure());
		assertEquals(true, result.getPersonalise());
		assertEquals(true, result.getSocial());
		assertEquals(true, result.getLive());

		restoreMockRestSystemPreferences();
		restoreMockRestStepState();
	}

	@Test
	public void testGetSystemState() {
		// get system state
		try {
			SystemStateDTO systemStateDTO = getSystemState();
			assertTrue(systemStateDTO.isCollecting() == initSystemIsCollecting);
			assertTrue(systemStateDTO.isOnline() == initSystemIsOnline);
		} catch (Exception e) {
			logger.debug(e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetSystemInitiativeMode() throws OCTException {
		Response response = target(MANAGER + "/mode").request().get(Response.class);
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		InitiativeModeDTO result = response.readEntity(InitiativeModeDTO.class);
		assertEquals(InitiativeModeTypeDTO.OFFLINE.name(), result.getMode());
	}

}