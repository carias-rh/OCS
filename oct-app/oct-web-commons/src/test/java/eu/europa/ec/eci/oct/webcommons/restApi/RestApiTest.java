package eu.europa.ec.eci.oct.webcommons.restApi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import eu.europa.ec.eci.oct.crypto.CipherOperation;
import eu.europa.ec.eci.oct.crypto.Cryptography;
import eu.europa.ec.eci.oct.entities.ConfigurationParameter;
import eu.europa.ec.eci.oct.entities.CountryProperty;
import eu.europa.ec.eci.oct.entities.admin.InitiativeDescription;
import eu.europa.ec.eci.oct.entities.admin.StepState;
import eu.europa.ec.eci.oct.entities.admin.SystemPreferences;
import eu.europa.ec.eci.oct.entities.signature.Signature;
import eu.europa.ec.eci.oct.entities.system.Country;
import eu.europa.ec.eci.oct.entities.system.Language;
import eu.europa.ec.eci.oct.utils.DateUtils;
import eu.europa.ec.eci.oct.webcommons.services.api.CaptchaApi;
import eu.europa.ec.eci.oct.webcommons.services.api.CustomFileApi;
import eu.europa.ec.eci.oct.webcommons.services.api.CustomisationsApi;
import eu.europa.ec.eci.oct.webcommons.services.api.ExportApi;
import eu.europa.ec.eci.oct.webcommons.services.api.InitiativeApi;
import eu.europa.ec.eci.oct.webcommons.services.api.ReportingApi;
import eu.europa.ec.eci.oct.webcommons.services.api.RequestTokenApi;
import eu.europa.ec.eci.oct.webcommons.services.api.SecurityApi;
import eu.europa.ec.eci.oct.webcommons.services.api.SignatureApi;
import eu.europa.ec.eci.oct.webcommons.services.api.SocialMediaApi;
import eu.europa.ec.eci.oct.webcommons.services.api.SystemManagerApi;
import eu.europa.ec.eci.oct.webcommons.services.api.VersionApi;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.ApiResponse;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.customisations.CustomisationsDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.initiative.InitiativeDescriptionsDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.report.ProgressionStatus;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.security.AuthenticationDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.security.AuthenticationTokenDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.security.ChallengeDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.signature.SignatureDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.signature.SignatureMetadata;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.signature.SignatureValidation;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.signature.SignaturesMetadata;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.supportForm.SupportFormDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.systemManager.CollectorState;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.systemManager.StepStateChangeDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.systemManager.SystemStateDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.filters.AuthenticationFilter;
import eu.europa.ec.eci.oct.webcommons.services.api.test.MockApi;
import eu.europa.ec.eci.oct.webcommons.services.api.transformer.ContactTransformer;
import eu.europa.ec.eci.oct.webcommons.services.api.transformer.CustomisationsTransformer;
import eu.europa.ec.eci.oct.webcommons.services.api.transformer.ExportTransformer;
import eu.europa.ec.eci.oct.webcommons.services.api.transformer.FeedbackTransformer;
import eu.europa.ec.eci.oct.webcommons.services.api.transformer.InitiativeDescriptionTransformer;
import eu.europa.ec.eci.oct.webcommons.services.api.transformer.ReportingTransformer;
import eu.europa.ec.eci.oct.webcommons.services.api.transformer.SignatureTransformer;
import eu.europa.ec.eci.oct.webcommons.services.captcha.CaptchaService;
import eu.europa.ec.eci.oct.webcommons.services.commons.TestEntitiesFactory;
import eu.europa.ec.eci.oct.webcommons.services.configuration.ConfigurationService;
import eu.europa.ec.eci.oct.webcommons.services.contact.ContactService;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;
import eu.europa.ec.eci.oct.webcommons.services.initiative.InitiativeService;
import eu.europa.ec.eci.oct.webcommons.services.reporting.ReportingService;
import eu.europa.ec.eci.oct.webcommons.services.security.SecurityService;
import eu.europa.ec.eci.oct.webcommons.services.signature.SignatureService;
import eu.europa.ec.eci.oct.webcommons.services.socialMedia.SocialMediaService;
import eu.europa.ec.eci.oct.webcommons.services.system.SystemManager;

/**
 * this test is running with the db script "rest_api_test_db_script.sql" in
 * test/resources/db
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/rest-api-test.xml")
@Transactional(propagation = Propagation.REQUIRED)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, TransactionalTestExecutionListener.class })
public class RestApiTest extends JerseyTest {

	protected static final String MANAGER = "/manager";
	protected static final String SIGNATURE = "/signature";
	protected static final String MOCKS = "/mocks";
	protected static final String SECURITY = "/security";
	protected static final String VERSION = "/version";
	protected static final String INITIATIVE = "/initiative";

	protected final String USER = "admin";
	protected final String PWD = "Qwerty123!";
	protected final String BEARER = "Bearer ";
	private final String ENCODING = "UTF-8";
	private final String PRIVATE_KEY_PWD = "Qwerty123!";
	private final String PRIVATE_KEY_PATH = "/file/private.key";
	private final String SALT_PATH = "/file/crypto.salt";
	protected static int EXPECTED_MOCKED_SIGNATURES_SIZE = 2;
	protected static int EXPECTED_MOCKED_DESCRIPTIONS_SIZE = 2;
	protected static boolean ALLOW_RANDOM_DISTRIBUTION = true;
	protected static boolean AVOID_RANDOM_DISTRIBUTION = false;
	protected final int iterations = 100000;

	protected Logger logger = LogManager.getLogger(RestApiTest.class);
	@Autowired
	protected DataSource dataSourceTest;
	@Autowired
	protected InitiativeService initiativeService;
	@Autowired
	protected SignatureService signatureService;
	@Autowired
	protected SystemManager systemManager;
	@Autowired
	protected CustomisationsTransformer customisationsTransformer;
	@Autowired
	protected FeedbackTransformer feedbackTransformer;
	@Autowired
	protected InitiativeDescriptionTransformer initiativeDescriptionTransformer;
	@Autowired
	protected SignatureTransformer signatureTransformer;
	@Autowired
	protected ConfigurationService configurationService;
	@Autowired
	protected SocialMediaService socialMediaService;
	@Autowired
	protected ReportingService reportingService;
	@Autowired
	protected ContactService contactService;
	@Autowired
	protected TestEntitiesFactory testEntitiesFactory;
	@Autowired
	protected ExportTransformer exportTransformer;
	@Autowired
	protected ContactTransformer contactTransformer;
	@Autowired
	protected ReportingTransformer reportingTransformer;
	@Autowired
	protected CaptchaService captchaService;
	@Autowired
	protected SecurityService securityService;

	protected boolean initSystemIsOnline = false;
	protected boolean initSystemIsCollecting = true;
	protected int MOCK_DESCRIPTIONS_SIZE = 0;
	protected int MOCK_COUNTRIES_SIZE = 0;
	protected int MOCK_LANGUAGES_SIZE = 0;
	protected InitiativeDescription MOCK_INITIATIVE_DESCRIPTION1;
	protected InitiativeDescription MOCK_INITIATIVE_DESCRIPTION2;
	protected Country MOCK_SIGNATURES_COUNTRY_FOR;
	protected String MOCK_SIGNATURES_COUNTRY_FOR_CODE;
	protected String MOCK_SIGNATURE1_DATE;
	protected Date MOCK_SIGNATURE2_DATE;
	protected String MOCK_SIGNATURE2_DATE_STRING;
	protected String MOCK_SIGNATURE1_UUID;
	protected String MOCK_SIGNATURE2_UUID;
	protected int MOCK_SIGNATURE1_YEAR;
	protected int MOCK_SIGNATURE1_MONTH;
	protected int MOCK_SIGNATURE2_YEAR;
	protected int MOCK_SIGNATURE2_MONTH;
	protected Date MOCK_REGISTRATION_DATE;
	protected Date MOCK_EXPIRE_DATE;
	protected String MOCK_REGISTRATION_DATE_STRING;
	protected String MOCK_EXPIRE_DATE_STRING;
	protected int MOCK_DEADLINE_YEAR;
	protected String MOCK_REGISTRATION_NUMBER;
	protected String MOCK_REGISTRATION_URL;
	protected int MOCK_REGISTRATION_YEAR;
	protected String MOCK_DEFAULT_DESCRIPTION_TITLE;
	protected String MOCK_DEFAULT_DESCRIPTION_URL;
	protected String MOCK_DEFAULT_DESCRIPTION_OBJECTIVES;
	protected String MOCK_DEFAULT_DESCRIPTION_LANGUAGE_CODE;
	protected Long MOCK_DEFAULT_DESCRIPTION_LANGUAGE_ID;
	protected String MOCK_CONTACT_ORGANIZERS = "";
	protected String MOCK_CONTACT_EMAIL = "";
	protected String MOCK_CONTACT_NAME = "";
	protected String MOCK_CUSTOMISATIONS_CALLBACK_URL;
	protected String MOCK_CUSTOMISATIONS_FACEBOOK_URL;
	protected String MOCK_CUSTOMISATIONS_GOOGLE_URL;
	protected String MOCK_CUSTOMISATIONS_TWITTER_URL;
	protected Long MOCK_CUSTOMISATIONS_SIGNATURE_GOAL;
	protected Boolean MOCK_CUSTOMISATIONS_IS_CUSTOM_LOGO;
	protected Boolean MOCK_CUSTOMISATIONS_IS_OPTIONAL_VALIDATION;
	protected Boolean MOCK_CUSTOMISATIONS_SHOW_RECENT_SUPPORTERS;
	protected Boolean MOCK_CUSTOMISATIONS_SHOW_DISTRIBUTION_MAP;
	protected Boolean MOCK_CUSTOMISATIONS_SHOW_FACEBOOK;
	protected Boolean MOCK_CUSTOMISATIONS_SHOW_GOOGLE;
	protected Boolean MOCK_CUSTOMISATIONS_SHOW_TWITTER;
	protected Boolean MOCK_CUSTOMISATIONS_SHOW_PROGRESSION_BAR;
	protected Boolean MOCK_CUSTOMISATIONS_SHOW_SOCIAL_MEDIA;
	protected String MOCK_CUSTOMISATIONS_COLOR_PICKER;
	protected Integer MOCK_CUSTOMISATIONS_BACKGROUND;
	protected String MOCK_CUSTOMISATIONS_ALT_LOGO_TXT;
	protected List<Signature> allMockedSignatures = new ArrayList<Signature>();
	protected List<String> allMockedSignaturesCountryCodes = new ArrayList<String>();
	Map<String, Integer> allMockedSignaturesCountMap = new HashMap<String, Integer>();
	protected List<Country> allCountries = new ArrayList<Country>();
	protected List<Country> allCountriesA = new ArrayList<Country>();
	protected List<Country> allCountriesB = new ArrayList<Country>();
	protected List<InitiativeDescription> allInitiativeDescriptions = new ArrayList<InitiativeDescription>();
	protected Map<String, InitiativeDescription> allInitiativeDescriptionsMap = new HashMap<String, InitiativeDescription>();
	protected List<CountryProperty> allCountryProperties = new ArrayList<CountryProperty>();
	protected List<Language> allLanguages = new ArrayList<Language>();
	protected String TEST_FILE_STORAGE_PATH;
	protected String authorizationToken;
	protected List<String> allCountryCodes = new ArrayList<String>();
	protected List<String> allCountryCodesA = new ArrayList<String>();
	protected List<String> allCountryCodesB = new ArrayList<String>();
	protected Map<String, Country> allCountriesMap = new HashMap<String, Country>();
	private JdbcTemplate jdbcTemplate;

	@PostConstruct
	public void initJDBCtemplate() {
		if (this.jdbcTemplate == null) {
			this.jdbcTemplate = new JdbcTemplate(dataSourceTest);
		}
	}

	@Before
	public void init() throws Exception {
		if (allCountries.isEmpty()) {
			allCountries = systemManager.getAllCountries();
		}
		if (allCountryProperties.isEmpty()) {
			allCountryProperties = signatureService.getAllCountryProperties();
		}
		if (allLanguages.isEmpty()) {
			allLanguages = systemManager.getAllLanguages();
		}
		if (allCountryCodes.isEmpty()) {
			for (Country c : allCountries) {
				allCountryCodes.add(c.getCode());
				if (c.getCategory().equalsIgnoreCase(Country.CATEGORY_A)) {
					allCountriesA.add(c);
					allCountryCodesA.add(c.getCode());
				} else {
					allCountriesB.add(c);
					allCountryCodesB.add(c.getCode());
				}
			}
		}
		if (allCountriesMap.isEmpty()) {
			for (Country c : allCountries) {
				allCountriesMap.put(c.getCode(), c);
			}
		}
		if (allInitiativeDescriptions.isEmpty()) {
			allInitiativeDescriptions = initiativeService.getDescriptions();
			for (InitiativeDescription id : allInitiativeDescriptions) {
				allInitiativeDescriptionsMap.put(id.getLanguage().getCode(), id);
			}
		}
		if (allMockedSignatures.isEmpty()) {
			allMockedSignatures = signatureService.getAllSignatures();
		}
		assertEquals(EXPECTED_MOCKED_SIGNATURES_SIZE, allMockedSignatures.size());
		MOCK_DESCRIPTIONS_SIZE = allInitiativeDescriptions.size();
		assertEquals(EXPECTED_MOCKED_DESCRIPTIONS_SIZE, allInitiativeDescriptions.size());
		MOCK_INITIATIVE_DESCRIPTION1 = allInitiativeDescriptions.get(0);
		MOCK_INITIATIVE_DESCRIPTION2 = allInitiativeDescriptions.get(1);
		MOCK_DEFAULT_DESCRIPTION_TITLE = MOCK_INITIATIVE_DESCRIPTION1.getTitle();
		MOCK_DEFAULT_DESCRIPTION_URL = MOCK_INITIATIVE_DESCRIPTION1.getUrl();
		MOCK_DEFAULT_DESCRIPTION_OBJECTIVES = MOCK_INITIATIVE_DESCRIPTION1.getObjectives();
		MOCK_DEFAULT_DESCRIPTION_LANGUAGE_CODE = MOCK_INITIATIVE_DESCRIPTION1.getLanguage().getCode();
		MOCK_DEFAULT_DESCRIPTION_LANGUAGE_ID = MOCK_INITIATIVE_DESCRIPTION1.getLanguage().getId();
		String sameCountryCode = "";
		for (Signature mockedSignature : allMockedSignatures) {
			String mockedSignatureCountryCode = mockedSignature.getCountryToSignFor().getCode();
			allMockedSignaturesCountryCodes.add(mockedSignatureCountryCode);
			if (!allMockedSignaturesCountMap.containsKey(mockedSignatureCountryCode)) {
				allMockedSignaturesCountMap.put(mockedSignatureCountryCode, 0);
			}
			int countToBeUpdated = allMockedSignaturesCountMap.get(mockedSignatureCountryCode);
			allMockedSignaturesCountMap.put(mockedSignatureCountryCode, countToBeUpdated + 1);

			if (!StringUtils.isEmpty(sameCountryCode) && !mockedSignatureCountryCode.equals(sameCountryCode)) {
				fail("Inconsistent signature data in db for testing. Who changed it?");
			}
			sameCountryCode = mockedSignatureCountryCode;
		}
		MOCK_SIGNATURES_COUNTRY_FOR_CODE = sameCountryCode;
		// System.err.println("sameCountryCode = "+sameCountryCode);
		// System.err.println("MOCK_SIGNATURES_COUNTRY_FOR_CODE =
		// "+MOCK_SIGNATURES_COUNTRY_FOR_CODE);
		// System.err.println("systemManager is null? " + systemManager == null);
		MOCK_SIGNATURES_COUNTRY_FOR = systemManager.getCountryByCode(MOCK_SIGNATURES_COUNTRY_FOR_CODE);
		Signature mockSignature1 = allMockedSignatures.get(0);
		Signature mockSignature2 = allMockedSignatures.get(1);
		Date mockSignature1DateOfSignature = mockSignature1.getDateOfSignature();
		MOCK_SIGNATURE1_DATE = DateUtils.formatDate(mockSignature1DateOfSignature);
		Calendar mockSignature1Calendar = Calendar.getInstance();
		mockSignature1Calendar.setTime(mockSignature1DateOfSignature);
		MOCK_SIGNATURE1_YEAR = mockSignature1Calendar.get(Calendar.YEAR);
		MOCK_SIGNATURE1_MONTH = mockSignature1Calendar.get(Calendar.MONTH) + 1;
		Date mockSignature2DateOfSignature = mockSignature2.getDateOfSignature();
		MOCK_SIGNATURE2_DATE = mockSignature2DateOfSignature;
		MOCK_SIGNATURE2_DATE_STRING = DateUtils.formatDate(mockSignature2DateOfSignature);
		Calendar mockSignature2Calendar = Calendar.getInstance();
		mockSignature2Calendar.setTime(mockSignature2DateOfSignature);
		MOCK_SIGNATURE2_YEAR = mockSignature2Calendar.get(Calendar.YEAR);
		MOCK_SIGNATURE2_MONTH = mockSignature2Calendar.get(Calendar.MONTH) + 1;
		MOCK_SIGNATURE1_UUID = mockSignature1.getUuid();
		MOCK_SIGNATURE2_UUID = mockSignature2.getUuid();
		MOCK_COUNTRIES_SIZE = allCountries.size();
		MOCK_LANGUAGES_SIZE = systemManager.getAllLanguages().size();
		SystemPreferences systemPreferences = systemManager.getSystemPreferences();
		MOCK_REGISTRATION_DATE = systemPreferences.getRegistrationDate();
		MOCK_REGISTRATION_DATE_STRING = DateUtils.formatDate(MOCK_REGISTRATION_DATE);
		MOCK_EXPIRE_DATE = systemPreferences.getDeadline();
		MOCK_EXPIRE_DATE_STRING = DateUtils.formatDate(MOCK_EXPIRE_DATE);
		MOCK_REGISTRATION_NUMBER = systemPreferences.getRegistrationNumber();
		MOCK_REGISTRATION_URL = systemPreferences.getCommissionRegisterUrl();
		Calendar deadlineCalendar = Calendar.getInstance();
		deadlineCalendar.setTime(MOCK_EXPIRE_DATE);
		MOCK_DEADLINE_YEAR = deadlineCalendar.get(Calendar.YEAR);
		Calendar registrationCalendar = Calendar.getInstance();
		registrationCalendar.setTime(MOCK_REGISTRATION_DATE);
		MOCK_REGISTRATION_YEAR = registrationCalendar.get(Calendar.YEAR);

		TEST_FILE_STORAGE_PATH = systemManager.getSystemPreferences().getFileStoragePath();
		// create file store dir
		new File(TEST_FILE_STORAGE_PATH).mkdirs();

		feedCustomisationsConstants();
	}

	@Override
	protected Application configure() {

		ResourceConfig resourceConfig = new ResourceConfig();
		resourceConfig.register(InitiativeApi.class);
		resourceConfig.register(SignatureApi.class);
		resourceConfig.register(SystemManagerApi.class);
		resourceConfig.register(SocialMediaApi.class);
		resourceConfig.register(CustomisationsApi.class);
		resourceConfig.register(CustomFileApi.class);
		resourceConfig.register(MultiPartFeature.class);
		resourceConfig.register(ReportingApi.class);
		resourceConfig.register(ExportApi.class);
		resourceConfig.register(RequestTokenApi.class);
		resourceConfig.register(VersionApi.class);
		resourceConfig.register(CaptchaApi.class);
		resourceConfig.register(MockApi.class);
		resourceConfig.register(SecurityApi.class);
		resourceConfig.register(AuthenticationFilter.class);
		resourceConfig.property("contextConfigLocation", "classpath:rest-api-test.xml");
		return resourceConfig;
	}

	@Override
	public void configureClient(org.glassfish.jersey.client.ClientConfig clientConfig) {
		clientConfig.register(MultiPartFeature.class);
	}

	protected long getProgressionStatus() {
		Long viewCount = 0l;
		try {
			Response response = target("/report/progression").request().get(Response.class);
			assertEquals(Status.OK.getStatusCode(), response.getStatus());
			ProgressionStatus progressionStatus = response.readEntity(ProgressionStatus.class);
			viewCount = progressionStatus.getSignatureCount();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		return viewCount;

	}

	private void feedCustomisationsConstants() throws OCTException {
		List<ConfigurationParameter> configurationParameters = configurationService.getAllSettings();
		CustomisationsDTO customisationsDTO = customisationsTransformer.transform(configurationParameters);
		MOCK_CUSTOMISATIONS_CALLBACK_URL = customisationsDTO.getCallbackUrl();
		MOCK_CUSTOMISATIONS_FACEBOOK_URL = customisationsDTO.getFacebookUrl();
		MOCK_CUSTOMISATIONS_GOOGLE_URL = customisationsDTO.getGoogleUrl();
		MOCK_CUSTOMISATIONS_TWITTER_URL = customisationsDTO.getTwitterUrl();
		MOCK_CUSTOMISATIONS_SIGNATURE_GOAL = customisationsDTO.getSignatureGoal();
		MOCK_CUSTOMISATIONS_IS_CUSTOM_LOGO = customisationsDTO.isCustomLogo();
		MOCK_CUSTOMISATIONS_IS_OPTIONAL_VALIDATION = customisationsDTO.isOptionalValidation();
		MOCK_CUSTOMISATIONS_SHOW_RECENT_SUPPORTERS = customisationsDTO.getShowRecentSupporters();
		MOCK_CUSTOMISATIONS_SHOW_DISTRIBUTION_MAP = customisationsDTO.isShowDistributionMap();
		MOCK_CUSTOMISATIONS_SHOW_FACEBOOK = customisationsDTO.isShowFacebook();
		MOCK_CUSTOMISATIONS_SHOW_GOOGLE = customisationsDTO.isShowGoogle();
		MOCK_CUSTOMISATIONS_SHOW_TWITTER = customisationsDTO.isShowTwitter();
		MOCK_CUSTOMISATIONS_SHOW_PROGRESSION_BAR = customisationsDTO.isShowProgressionBar();
		MOCK_CUSTOMISATIONS_SHOW_SOCIAL_MEDIA = customisationsDTO.isShowSocialMedia();
		MOCK_CUSTOMISATIONS_COLOR_PICKER = customisationsDTO.getColorPicker();
		MOCK_CUSTOMISATIONS_BACKGROUND = customisationsDTO.getBackground();
		MOCK_CUSTOMISATIONS_ALT_LOGO_TXT = customisationsDTO.getAlternateLogoText();
	}

	public void setCollectorState(String status) {
		CollectorState collectorStateSetter = new CollectorState();
		collectorStateSetter.setCollectionMode(status);
		try {
			Response response = target("/manager/collectorsetstate").request()
					.post(Entity.entity(collectorStateSetter, MediaType.APPLICATION_JSON));
			assertEquals(Status.OK.getStatusCode(), response.getStatus());
			CollectorState responseCollectorState = response.readEntity(CollectorState.class);
			// check that has been successfully changed
			if (status.equals(CollectorState.OFF)) {
				assertEquals(CollectorState.ON, responseCollectorState.getCollectionMode());
			} else {
				assertEquals(CollectorState.OFF, responseCollectorState.getCollectionMode());
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@After
	public void tearDown() {
		dataSourceTest = null;
		initiativeService = null;
		signatureService = null;
		systemManager = null;
		customisationsTransformer = null;
		feedbackTransformer = null;
		initiativeDescriptionTransformer = null;
		signatureTransformer = null;
		configurationService = null;
		socialMediaService = null;
		reportingService = null;
		contactService = null;
		testEntitiesFactory = null;
		exportTransformer = null;
		contactTransformer = null;
		reportingTransformer = null;
		captchaService = null;

	}

	protected void setStepState(StepStateChangeDTO stepStateDTO) {
		Response response = target(MANAGER + "/setstepstate").request()
				.header(HttpHeaders.AUTHORIZATION, BEARER + authorizationToken)
				.post(Entity.entity(stepStateDTO, MediaType.APPLICATION_JSON));
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}

	protected StepState getStepState() {
		Response response = target(MANAGER + "/stepstate").request()
				.header(HttpHeaders.AUTHORIZATION, BEARER + authorizationToken).get(Response.class);
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		StepState result = response.readEntity(StepState.class);
		return result;
	}

	protected boolean getCollectingState() {
		boolean isCollecting = false;
		try {
			Response response = target(MANAGER + "/collectorstate").request()
					.header(HttpHeaders.AUTHORIZATION, BEARER + authorizationToken).get(Response.class);
			assertEquals(Status.OK.getStatusCode(), response.getStatus());
			CollectorState collectorState = response.readEntity(CollectorState.class);
			isCollecting = collectorState.getCollectionMode().equalsIgnoreCase(CollectorState.ON);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		return isCollecting;
	}

	protected void setCollectingState(String collectorStateSetterMode) {
		CollectorState collectorStateSetter = new CollectorState();
		collectorStateSetter.setCollectionMode(collectorStateSetterMode);
		try {
			Response setCollectingModeResponse = target(MANAGER + "/collectorsetstate").request()
					.header(HttpHeaders.AUTHORIZATION, BEARER + authorizationToken)
					.post(Entity.entity(collectorStateSetter, MediaType.APPLICATION_JSON));
			assertEquals(Status.OK.getStatusCode(), setCollectingModeResponse.getStatus());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	protected void goOffline() {
		Response response = target(MANAGER + "/gooffline").request()
				.header(HttpHeaders.AUTHORIZATION, BEARER + authorizationToken).get(Response.class);
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		ApiResponse success = response.readEntity(ApiResponse.class);
		assertEquals(ApiResponse.SUCCESS, success.getStatus());
		assertEquals("Operation succesful.", success.getMessage());
		assertEquals(Status.OK.getStatusCode(), success.getCode());
	}

	protected void goOnline() {
		Response response = target(MANAGER + "/goonline").request()
				.header(HttpHeaders.AUTHORIZATION, BEARER + authorizationToken).get(Response.class);
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		ApiResponse success = response.readEntity(ApiResponse.class);
		assertEquals(ApiResponse.SUCCESS, success.getStatus());
		assertEquals(SystemManagerApi.MAN05 + "Operation succesful.", success.getMessage());
		assertEquals(Status.OK.getStatusCode(), success.getCode());
	}

	protected SystemStateDTO getSystemState() {
		Response response = target(MANAGER + "/state").request()
				.header(HttpHeaders.AUTHORIZATION, BEARER + authorizationToken).get(Response.class);
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		SystemStateDTO systemStateDTO = response.readEntity(SystemStateDTO.class);
		assertNotNull(systemStateDTO);
		return systemStateDTO;
	}

	protected InitiativeDescriptionsDTO getAllDescriptions() {
		Response response = target(InitiativeApiTest.INITIATIVE + "/alldescriptions").request().get(Response.class);
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		InitiativeDescriptionsDTO initiativeDescriptionsDTO = response.readEntity(InitiativeDescriptionsDTO.class);
		return initiativeDescriptionsDTO;
	}

	protected void restoreMockRestTestDescriptions() throws SQLException {
		deleteSignatures();
		deleteInitiativeDescriptions();
		this.jdbcTemplate.execute(
				"INSERT INTO OCT_INITIATIVE_DESC VALUES(1, '[EN]testObjectives', '[EN]testTitle', 'http://www.rest.test.url.uk', 6, 1, 'partialRegistration url lg 6')");
		this.jdbcTemplate.execute(
				"INSERT INTO OCT_INITIATIVE_DESC VALUES(2, '[IT]testObjectives', '[IT]testTitle', 'http://www.rest.test.url.it', 9, 0, 'partialRegistration url lg 9')");
		restoreMockRestTestSignatures();
		this.jdbcTemplate.getDataSource().getConnection().close();
	}

	protected void deleteSignatures() throws SQLException {
		this.jdbcTemplate.execute("DELETE FROM OCT_SIGNATURE");
		this.jdbcTemplate.getDataSource().getConnection().close();
	}

	protected void deleteInitiativeDescriptions() throws SQLException {
		this.jdbcTemplate.execute("DELETE FROM OCT_INITIATIVE_DESC");
		this.jdbcTemplate.getDataSource().getConnection().close();
	}

	protected void restoreInitDbStatus() throws SQLException {
		restoreMockRestSystemPreferences();
		restoreMockRestTestDescriptions();
		restoreMockRestTestSignatures();
		restoreMockRestTestFeedbacks();
	}

	protected void restoreMockRestStepState() throws SQLException {

		this.jdbcTemplate.execute("DELETE FROM OCT_STEP_STATE");
		this.jdbcTemplate.execute(
				"INSERT INTO OCT_STEP_STATE (`ID`, `STRUCTURE`, `PERSONALISE`, `CERTIFICATE`, `SOCIAL`, `LIVE`) VALUES (1, 0, 0, 0, 0, 0)");
		this.jdbcTemplate.getDataSource().getConnection().close();
	}

	protected void restoreMockRestSystemPreferences() throws SQLException {

		this.jdbcTemplate.execute("DELETE FROM OCT_SYSTEM_PREFS");
		this.jdbcTemplate.execute(
				"INSERT  INTO OCT_SYSTEM_PREFS(id,CERT_CONTENT_TYPE,CERT_FILE_NAME,partiallyRegistered,collecting,commissionRegisterUrl,DEADLINE,FILE_STORE,publicKey,registrationDate,registrationNumber,state, CURRENTANNEXREVISION) VALUES(1,'application/pdf','certificate1379317415564.pdf',0,1,'http://www.rest.test.url.com','2021-01-01 00:00:00','./target/sys_pref_file_store/','30820222300d06092a864886f70d01010105000382020f003082020a0282020100b38518df7db42316f7fcab565054db0cd2f561edd99d334be91913ff4fc32657a48a59d6d801911e0f1eddcc318bf8b355835dbdd58222e5a0d886bd71ace118f478e56bf0e1c79e9dfc23e799418097c1bf319f400b6821b1b16a037a146a9d51f6f1e92e4fc3a5f372f3509f261d79970fb3c1e0e6fea5a1444ca554b0d992f6d9b8e08f51c5669fe4751d1999e9cd753e7f96ec5a62a20e6daebaf67f57367897f173125ea2232ae38e2c27bfe7d613c1baeb156ba4d40b23eaa49fc902e105a8ce721200f869f2805207539bfb4ead0ea04ff93a80a2ede6543a2e0a4ae775faf865203c0ab72278f3827c31da7ad7e94633158de9f442da8f5ffdfcac5b0b171e18e8f100c3a8d1d7bae90d80dc5038b25968801ea820a3173f05ffe50a6ca591cdbb545c54f12093a6e702cc677bb72ff913108e314aa73ed1d039536cbb38fd8a9b9f045576492cfa55540401edd755f2838fcff1a29a4bfcc19c569c27660832628f1d3778ed00d9f397db361a69f12781f609a31efc6a568fb9ac207b0ab073cf2595f517624f72710289b5add4a653d935f264408d2ab0ba81cc94446cc507014694106695599d3edacb0080bffa61f9ce8d6d664425c3a4aa79e84ce3d3c0c8507501276e441931a8c2a7da5bd4a67ce44391f90a75e5d5d8341a02b33227f0e82a6874fee0529100d1b7fd4c09946a0e4d56e2b8ad5180779e350203010001','2020-01-01 00:00:00','ECI(2020)000000','SETUP', '2')");
		this.jdbcTemplate.getDataSource().getConnection().close();

	}

	protected void removeNewSignatures() throws SQLException {
		this.jdbcTemplate.execute("DELETE FROM OCT_SIGNATURE WHERE id > 2");
		this.jdbcTemplate.execute("DELETE FROM OCT_IDENTITY_VALUE WHERE id > 2");
		this.jdbcTemplate.getDataSource().getConnection().close();
	}

	protected void removeAllSignatures() throws SQLException {
		this.jdbcTemplate.execute("DELETE FROM OCT_IDENTITY_VALUE");
		this.jdbcTemplate.execute("DELETE FROM OCT_SIGNATURE");
		this.jdbcTemplate.getDataSource().getConnection().close();
	}

	protected void removeExportTables() throws SQLException {
		this.jdbcTemplate.execute("DELETE FROM OCT_EXPORT_HISTORY");
		this.jdbcTemplate.getDataSource().getConnection().close();
	}

	protected void restoreMockRestTestSignatures() throws SQLException {
		this.jdbcTemplate.execute("DELETE FROM OCT_IDENTITY_VALUE");
		this.jdbcTemplate.execute("DELETE FROM OCT_SIGNATURE");
		this.jdbcTemplate.execute(
				"INSERT INTO OCT_SIGNATURE (ID,DATEOFSIGNATURE,DATEOFSIGNATURE_MSEC,FINGERPRINT,UUID,COUNTRYTOSIGNFOR_ID, ANNEXREVISION, SIGNATORY_INFO) VALUES ('1','2020-01-01 14:16:00','1577884560000','7157a59f3c563c143f1f13ac44d90f027a7795dd8359065a3b24b9d75651f8e5','f616d20d-f5b0-48f2-983a-12dd539fea60','4','2','<signatoryInfo><groups><group><name>oct.group.general</name><properties><property><key>oct.property.fathers.name</key><value>6ceefd066488d22131c5e5e15fb8e55ab1f321fc9838ea24907da5449760060fe38fc190e59481860241c681b96b6c0ae65d926758b98ba1a74ca9d2979469a01eec8a4ed948fd54528adf20f5abb4439b7c3a669340bfe4d6f412cd0cb2c9a9761d3b5122ce8b0f67b1b9207d1b680e5580b90e4e0ddb12cbb22d3f7bec285a0960b6a979fd775bda3531adbd2193336070356785ec94bc30c63887f14250a8ca4d76a42cf9886a27b27e133ede14162d449d95e9b030ca82e12b3fdc7dea2e2f5cae722b1802815759ab89db256bd450c6647a6e44bd964343a6325186c5deeafd0dd757adbc90015ec60502c1272330a1639bf478cef04090cfc8ad0b2a06d6f50f69ed7f4083ca640f7d7fe561ec823f01edd0c90d50bc6a1523eded51d89c96b2c34f95176de2c9143fb6ec003951af8c7ddb54aacd8a4e3ba0a53e5544580d619b69bb1bbb79414f95bc94193d0fc3a5104efb5438d45faa964a6bf9799ca08eb0ee17ab9882ebd2063c004dd37823c76aee46301ff8c35025197db5c6</value></property><property><key>oct.property.firstname</key><value>7c4fd12696c814734d27434367c9b3148b2fa8d5b544dd205cf7add337e7c680576bd3608d9c1b2dd9a8edee65f61b1d44077f344fd4e820d57526864410d6039d4f037c27143c3ae5049cb0382fc10977f0a61090d6aea5da1b9805519b0d7e52b639804d79538cd2ca63f4860acdfe0b45f1edc8ea75b142068050dd2a358e9487e05e7a28b109d49827ea7045e113f80afbc4ab919b88a01793e95e7e26653682fbcab7a6805d00e75791b202f763f861f8366a4c54b292e41a3d6fe2bd4d2471721290d3f50c56a557567d4f10f55356261edabe01a8888916f1cdcc9ecefebf943189c9bbbbecb9d1266f9874eca806bc0258f1d552defe1ab410e686eedb02f61c66e4fe5ebde972eb4c80552a52f36eedcd57b154e00a44bc4f24c11a2ffc4d8442b472553410a63c24380719b5b55502fe797756903e1a684d0a10a7c7c237dcb59517dcd943b654fa55673bc0a868a0cda6ea9f267775183dd17f80ae2d3e3b0674fe516cc3b5d4e9f5d0d57e4cbb6dc865eade36d07fbecbcb9e4f</value></property><property><key>oct.property.lastname</key><value>639b0188c612b4e2d1ece707898ee2710a28e6f799aaa2aaf72e526a4ab6514e8ae27ed78f702992f39673e7a6c57b0fd9ebc2e617fa004c6520b6f6d63418db8715aad174a881ab4c9f1c5fd8fdcaaad46a4fa0d5df89e9701f350338137bff668cbce5cb68a407f8c04d764206f0adbda99c8490d3893cda7a467e457d448b2e639fd6a7d035a1b2b99fe6c3eee6c9ea4b44b8bce785023c4be3e65bb3584dcc5281d450e48e61501077a0100d3636a6eb0941fed46f496a28352794eb376430d53593a7de02e014b571ff19d4692df9c2996412005e4906960a011bca5778360b9c88b875c14429b3ace8a606dafe820a13fc3988c5e1b41c9c63019a42800c1991c31c248684645cea595c30cb1577cfe8f0933f19d849761d56fb0194b71e3456208672f2453032cd8f85c2a75d8364d055c4a53414afe800c6e397e8788a9c04baadd56f3b533622f711fbba0383fbe0c8a6f2d30dd162a62943a43aec3a60a3ed7c3d77db55071dd11487c21201ae2510fa98ae591f73444eae963c4c</value></property><property><key>oct.property.nationality</key><value>2535d9568384b8ef79d68a38f9ed385582b2f9195069691838cf3be101cf8b508ffc689322db63368e7e7445b493314c29e53b6732951a955a9e0047a098f40e4e4595f95c0b2ad803e3a1269335f1b1a3f95793a5386870870dafad2cbb64314104f9a216a303d7b9384a74080ca728cee7f4168d1986c0499f74de679b0e85a22837a60a8bba29714d8065ace7bde4fc9651cb5572e3a03e8dfe9be2af50683f7b4d4a374abc379398fa1bbc6a80f95167b652d313c5f41c4df676c1bce11e7dad3cfa1132974a2bed2cad4af184a12f3bef0d735a08fa405ef23aa9fe89ddf1ca92d314f5bdce81398fd43c2bd654fd5e650497ffc96202d604ca19a6b8bf2a70061c016c0cbda0d03ca16c71f0da6f148d55cc3cd232a836e480d12b6d783d470731da4e0e3dda67eff0eafc6ef76c44611e091da216bd95c3a8ac1b0e3e6410904e7f7091f7fc81060f8c467e73943ab8230e430246ae1b428cd86cb521c1e40aca14a46f1a5ea2a4e8c4e5ed37f037426a2b904bd6ad1478784db65cd6</value></property></properties></group><group><name>oct.group.id</name><properties><property><key>oct.property.personal.number</key><value>7dc846aa1efaf2b254b85f9601b2ca751ebbe67c66c204530dfceb08c9043ced4963b86ee3ae28582887a73650686e6b5156fe0d1b2d9e23369247a01dc132403ae54f61cb648efcf12a7c93c7b9de9e4be9958e01426898a35b2194fdc37565b7bdcb19e728e87a297496d4af91932e5a229adaa431ff0b9cd55338f125f36b31eed0877c78c9aefdbe8f027524e37b04d1c68bc3f0d130cdea879736614e6da069fe77a0f5016ac71a279f3fd78f2b08f71c00d0b1c914838227f00dd2277b7e90f43d07087e643394804a0fa3dac97ff2f0cf811848629e9899de12b55e9a206323dcd94933f1d478bc7fa3da8b852b29533fa4c4efcd5d6db8cdbe91deff6c9a0c7c66a3a10521c66b260887861bc5b43e76f72afff177326464d9e0b21f7f206add634e44d3097b2cd2f9ab17117f2eeca53cc7110d15e839bbd1ee271860a25065d69f498efff7b34316a526d25ba10b0017ee7405626a9b2a22b2b7f500c1da8df840799a9d1285853b958543365819c42fde0472249a0a338b6f00cb</value></property></properties></group></groups></signatoryInfo>')");
		this.jdbcTemplate.execute(
				"INSERT INTO OCT_SIGNATURE (ID,DATEOFSIGNATURE,DATEOFSIGNATURE_MSEC,FINGERPRINT,UUID,COUNTRYTOSIGNFOR_ID, ANNEXREVISION, SIGNATORY_INFO) VALUES ('2','2020-01-02 14:16:00','1577970960000','62c02020e0f2a55be613868183e27f5086715c04e467f1a3b0a842574b324944','6cea7f4e-7b09-4e32-8aa7-865fc3e47338','4','2','<signatoryInfo><groups><group><name>oct.group.general</name><properties><property><key>oct.property.fathers.name</key><value>683e4de4b663e1bce46baa11b48e0bcf3d775d7d37955ab5f782e2bef2b3d9bd2ee046f78d4bbf1c984bc5c5075c2e12c604f37fe8b24644c9e5e04abfe703f02b2e1da91b93d6a1e6d19a59a56df3f7b71119bcfe3c04edbd4f3cd9535010996ca1ca9534ae65eed4903817a5b187a26e2e4d60e67a41b9aaadb9a570c02941166f8a9691a224ced22fdbd91f2677cf0394017fdf16bccd4137a383a893a17885bbe3f8fb3eab4985c55025367273739a7d3448ccb13196a89227e9e6b543e877dffeb6799958e67570f06dbdc1293505c26aed987951a3ec25e9acacfab2d6bf1d0c73d24e46755973edd5f9181a67bb9bdd19cd9db4efe5ccdf678422fb2fa71111a28eabf618051d798fb00ebe11302c071fbd974c9d47c8edb739f3c530c4eba1951936eab9e690a2782bdeb5caba3c2c80977543ece9eef3ff904ae0b91abfa14b51856f706c5490141344747b857eadcf30c17aec7604c77d3c63091b60f80cf51f3418c6ff0b31206869727ae90109d042e17058131e31a7596388b7</value></property><property><key>oct.property.firstname</key><value>565bdeef5ba164d51b4288f0083304024ab5d64cba99a0ad319612d8d2b5c8f309a26af0e029fee3dcd0c736bac3a87615930a41cab68dbe33ce7d4cbc9fe0cad5fd939e6967e23fc58a86ae706f76a160f88ab6b6601abb1343df86ae31518387885fe857f5825e59ef70264bb403c67b16580fd5faf30ba78fa65354b26e6248ca77c5686397330efcab22c962372e8bc152ba793a6d45880acd6f4924af0ff3154caa63200fb0c76ef901c9607608cab86a56c809338b8f866fae9127adb4cd429e051c40988191ebd6c0abc79ef8b46907dc4dfefc9184704c398db70d4b231ce14b3f776722c3b7e314b15e352d64b98a99f1580cd7f32e25ab2e5a0dd357a01ea1a9fa87f5a24cd7ed7d00e6144150dba77fd5f952a08d2fe7ea8086d96d2692823c65331438a046ee51f7d5f61ed744db34730993f57b651f72153bf56ce61b648de16fd55d635f1173eac6584eec371d9d4a72c5769dc38c45a59f47d57a9b9f287046e41d1feff1c07a9b0566abb702f07f422f489e0eaff1e09d63</value></property><property><key>oct.property.lastname</key><value>658d9f9b75f22d91cb60b13548a55b6606d4b50bb2e1bcd1463000012387e3596517633a5ad546ccbcb1e6f471eac0f383178dcc0d2cb00cbe1280c1d3d2bc032dfc462663c30bdc4b4652f3bcb4e7f03f5a6f086ededfa167f62aec815e52dcda2983599be6b6463331ce83836d092e2c230e6e1b0fa69345d81a113e978f27bdd347a4ce18a17b4b0bc071e79aebb0a0548591a81c0ff5ad1ae9314b42d02c1ecd398888ffe366ff3fbef1dd8be525e5c8692645e56e89dd3e1c57e9e561d09b5f09a61f6f086bcd0b05ba562cfc6edda56e8c24dd45defa405683938ba7dcd518ed19fa7329a8b1cee611da829d9e0db7f881afd12c4306818f27de4fe1a2bd0adfa8ada73a1e7b066365da39c20504e4f1ea75afab34f6591289f569b9c06f7fb454ecb7bc257b376b0791b8723225fae569bf0251a1a770b94e7320dcb9bc44cc9f47447d7296495264e0f6710a440677a9230f1639964bfa5541465c3289b0686e1cf366ae1a9e480bf907ab6b9d674266ea5ad1ee53d7d94bc355f9ac</value></property><property><key>oct.property.nationality</key><value>3e21bfbf1d2c8c8b8b65427ad24b07079a38a67d2fdd0ec757b563080196c3cb6953ba52e2df78abaed5a1b2f24cf817d4760346037b41134fb07805215a47a1184bb6cdd629d71187d7ed935106a1172ad741df0058dec1da4bec69f91149830d9639fb0f5ff9a5a28fd388a3657e6ebe1b938beb5f58269b7e7a6fa936352095924eef44da7519e26af74b32e5f89fcd97f422793ce1ffccb64a2de99bf9425e45440123c6533a24f292b2231fd5b7e08b2ad85d3ec33b053ebb12019cd20d1b59d2659985f96a08d73a619e4408d3e263e97187b66074873bac43dafc66c49275a157e806445a7409255227fefe2a8e5d39488a04356f5aaf804f318b2f72ed5c45abc95233923b8c920118ae74a15f1923b58e66027d36f610168cc4c421a268ee5080f0555fee805679ac4aee34535bc1ceadcad77c1ef2321feb7f0fe6565460c4cf08eb3dfd4995a0b3cba8154dd2136f8ffddbfc57a04f3b6a7aa3a414fe5caa54135856c9ae73e4e485628c1a5ab733a8c757303a98111f89885364</value></property></properties></group><group><name>oct.group.id</name><properties><property><key>oct.property.personal.number</key><value>127c4cbae742d2a3f7295923be4d015952edc4e921e8062441a9ebbdd4c4954d7a661edb5de1819b14402a8e0ff09f4a30c23369f746e3f44296baa1d90f1da28d592c1bdda7bc3baee4391bba016f6b9ca22300692524edc9967b0b590a536b3bb030b49a6d48925e072a3487278c130d30e8c67999ef9b3672300ef3111ecd236ec7039372339d8c0d61c801c992418f0a8fc696232d5e81fccd76fb0bc67e4c3e0b2afcb4455dee29253d0be8f37b01ca96841be4d2430670e14f9a790efebba7bcc9d08f5309485c42b95c48745a9d8ea2df9d41c3bf76bccee611d6df2297f29e3f75b443b45312d6caf67b4e8751146dd6a85ee50282b096f9e91d2ecf52f609b1cd849970b0c8f10a88a0d8b14ca7dad32858a67b15190febb16d053fbdf336da2e9a5e5bfe9ef38ab5ad48106aceabdee61e41e9b01786c0a784d9503083dc300a819bcacad8d85379e991e414342e32bca8db4b5ea256022305765d676b0f980ca38730f2eb90ef6bd9faeebcd3c00bf79656b9178d6cc663d220c1</value></property></properties></group></groups></signatoryInfo>')");
		this.jdbcTemplate.execute(
				"INSERT  INTO OCT_IDENTITY_VALUE(id, countryCode, propertyId, identityValue) VALUES (1,'fr',15, 'f616d20d-f5b0-48f2-983a-12dd539fea60')");
		this.jdbcTemplate.execute(
				"INSERT  INTO OCT_IDENTITY_VALUE(id, countryCode, propertyId, identityValue) VALUES (2,'fr', 15, '6cea7f4e-7b09-4e32-8aa7-865fc3e47338')");
		this.jdbcTemplate.getDataSource().getConnection().close();

	}

	protected void restoreMockRestTestFeedbacks() throws SQLException {
		this.jdbcTemplate.execute("DELETE FROM OCT_FEEDBACK");
		this.jdbcTemplate.execute(
				"INSERT  INTO OCT_FEEDBACK(id,feedbackDate,feedbackRange_id,feedbackComment, signatureIdentifier) VALUES (1, '2019-01-01 00:00:00', 1, 'This is a bad feedback :(', 'f616d20d-f5b0-48f2-983a-12dd539fea60')");
		this.jdbcTemplate.execute(
				"INSERT  INTO OCT_FEEDBACK(id,feedbackDate,feedbackRange_id,feedbackComment, signatureIdentifier) VALUES (2, '2019-01-01 00:00:00', 1, 'This is a bad feedback2 :((', '6cea7f4e-7b09-4e32-8aa7-865fc3e47338')");
		this.jdbcTemplate.getDataSource().getConnection().close();

	}

	public synchronized Map<String, Integer> insertTestSignatures(int signaturesSize) throws Exception {
		return insertTestSignatures(allCountries, signaturesSize, false);
	}

	public synchronized Map<String, Integer> insertTestSignatures(int signaturesSize, boolean randomDistribution)
			throws Exception {
		return insertTestSignatures(allCountries, signaturesSize, randomDistribution);
	}

	public synchronized Map<String, Integer> insertTestSignatures(List<Country> countriesToSignFor, int signaturesSize,
			boolean randomDistribution) throws Exception {

		Map<String, Integer> insertedSignaturesMap = new HashMap<String, Integer>();

		List<CountryProperty> allCountryProperties = signatureService.getAllCountryProperties();
		Map<String, List<CountryProperty>> countryPropertiesMap = new HashMap<String, List<CountryProperty>>();
		for (Country country : countriesToSignFor) {
			String code = country.getCode();
			for (CountryProperty countryProperty : allCountryProperties) {
				if (countryProperty.getCountry().getCode().equals(code)) {
					if (countryPropertiesMap.containsKey(code)) {
						countryPropertiesMap.get(code).add(countryProperty);
					} else {
						List<CountryProperty> countryPropertyList = new ArrayList<CountryProperty>();
						countryPropertyList.add(countryProperty);
						countryPropertiesMap.put(code, countryPropertyList);
					}
				}
			}
		}
		int signatureInsertedCounter = 0;
		for (Country country : countriesToSignFor) {
			String countryCode = country.getCode();
			if (!insertedSignaturesMap.containsKey(countryCode)) {
				insertedSignaturesMap.put(countryCode, 0);
			}
			int signatursAmountToInsert = signaturesSize;
			if (randomDistribution) {
				signatursAmountToInsert = getRandomNumber(signaturesSize);
			}
			for (int i = 0; i < signatursAmountToInsert; i++) {

				SignatureDTO signatureDTO = new SignatureDTO();
				signatureDTO.setCountry(country.getCode());

				List<SupportFormDTO> propertyValues = new ArrayList<SupportFormDTO>();
				propertyValues = MockApi.getMockedProperties(countryPropertiesMap.get(countryCode));
				signatureDTO.setProperties(propertyValues);
				signatureDTO.setOptionalValidation(true);
				try {
					insertSignature(signatureDTO, false);
					int oldCount = insertedSignaturesMap.get(countryCode);
					int newCount = oldCount + 1;
					insertedSignaturesMap.put(countryCode, newCount);
					signatureInsertedCounter++;
					System.out.println("Inserted testSignature #" + signatureInsertedCounter + " for country["
							+ country.getCode() + "] ");
				} catch (Exception e) {
					e.printStackTrace();
					throw new Exception("Can't insert signature: " + e.getMessage());
				}
			}
		}
		return insertedSignaturesMap;
	}

	public int getRandomNumber(int max) {
		return new Random().nextInt(max);
	}

	public String getRandomCountryCode() {
		int randomCountryIndex = getRandomNumber(allCountries.size() - 1);
		return allCountries.get(randomCountryIndex).getCode();
	}

	public String getRandomCountryCode(String category) {
		if (category.equalsIgnoreCase(Country.CATEGORY_A)) {
			int randomCountryIndex = getRandomNumber(allCountriesA.size() - 1);
			return allCountriesA.get(randomCountryIndex).getCode();
		} else {
			int randomCountryIndex = getRandomNumber(allCountriesB.size() - 1);
			return allCountriesB.get(randomCountryIndex).getCode();
		}
	}

	public Boolean getRandomBoolean() {
		return new Random().nextBoolean();
	}

	public int getRandomNumberSkipZero(int max) {
		int nextInt = new Random().nextInt(max);
		if (nextInt == 0) {
			nextInt++;
		}
		return nextInt;
	}

	protected boolean isCategoryA(String countryCode) {
		return allCountryCodesA.contains(countryCode);
	}

	protected synchronized String insertSignature(SignatureDTO signatureDTO, boolean isExpectedToFail) {
		return insertSignature(signatureDTO, isExpectedToFail, false);
	}

	protected synchronized String insertSignature(SignatureDTO signatureDTO, boolean isExpectedToFail,
			boolean validate) {
		return insertSignature(signatureDTO, isExpectedToFail, Status.OK.getStatusCode(), "", validate);
	}

	protected synchronized String insertSignature(SignatureDTO signatureDTO, boolean isExpectedToFail,
			int expectedStatusCode) {
		return insertSignature(signatureDTO, isExpectedToFail, expectedStatusCode, "", false);
	}

	protected synchronized String insertSignature(SignatureDTO signatureDTO, boolean couldFail, int expectedStatusCode,
			String expectedMessage, boolean validate) {
		
		String signatureIdentifier = "";
		String apiPath = "/insert";
		if (validate) {
			apiPath += "Validation";
		}
		try {
			Response response = target(SignatureApiTest.MOCKS + apiPath).request()
					.post(Entity.entity(signatureDTO, MediaType.APPLICATION_JSON));
			int responseStatus = response.getStatus();
			if (!couldFail) {
				if (responseStatus != Status.OK.getStatusCode()) {
//					 ApiResponse errorResponse = response.readEntity(ApiResponse.class);
//					 throw new Exception("" + errorResponse);
					throw new Exception("" + responseStatus);
				} else {
					SignatureValidation signatureValidation = response.readEntity(SignatureValidation.class);
					signatureIdentifier = signatureValidation.getSignatureIdentifier();
					assertFalse(StringUtils.isEmpty(signatureIdentifier));
					assertTrue(signatureValidation.getErrorFields().isEmpty());
				}
			} else {
				if (expectedStatusCode != responseStatus) {
					fail("Expected status: " + expectedStatusCode + " but received: " + responseStatus);
				}
				if (expectedStatusCode == Status.OK.getStatusCode()) {
					SignatureValidation signatureValidation = response.readEntity(SignatureValidation.class);
					assertTrue(signatureValidation.getErrorFields().isEmpty());
					signatureIdentifier = signatureValidation.getSignatureIdentifier();
					assertFalse(StringUtils.isEmpty(signatureIdentifier));
				} else {
					ApiResponse apiResponse = response.readEntity(ApiResponse.class);
					assertEquals(expectedStatusCode, responseStatus);
					if (!expectedMessage.isEmpty()) {
						assertEquals(expectedMessage, apiResponse.getMessage());
					}
					return responseStatus + "";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		return signatureIdentifier;
	}

	protected List<SignatureMetadata> getLastSignatures() {
		Response response = target("/signature/lastSignatures").request()
				.header(HttpHeaders.AUTHORIZATION, BEARER + authorizationToken).get(Response.class);
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		SignaturesMetadata signaturesMetadata = response.readEntity(SignaturesMetadata.class);
		return signaturesMetadata.getMetadatas();
	}

	protected byte[] readBytesFromFile(String fileName) throws Exception {
		File file = new File(this.getClass().getResource("/file/" + fileName).getFile());
		return Files.readAllBytes(file.toPath());
	}

	protected String obtainChallengeResult() throws Exception {
		Response response = target(SECURITY + "/challenge").request().get(Response.class);
		ChallengeDTO challengeDTO = response.readEntity(ChallengeDTO.class);
		assertNotNull(challengeDTO);
		String password = PRIVATE_KEY_PWD;
		byte[] keyData = FileUtils
				.readFileToByteArray(new File(this.getClass().getResource(PRIVATE_KEY_PATH).getFile()));
		byte[] saltData = FileUtils.readFileToByteArray(new File(this.getClass().getResource(SALT_PATH).getFile()));
		byte[] encryptedKey = Hex.decodeHex(new String(keyData, ENCODING).toCharArray());
		byte[] key = securityService.decrypt(encryptedKey, password.toCharArray(), saltData, iterations);
		Cryptography cryptography = new Cryptography(CipherOperation.DECRYPT, key);
		byte[] decryptedData = cryptography.perform(Hex.decodeHex(challengeDTO.getChallenge().toCharArray()));
		return new String(decryptedData, ENCODING);
	}

	protected AuthenticationTokenDTO authenticationFlow(String user, String pwd, String result) throws Exception {
		AuthenticationDTO authenticationDTO = new AuthenticationDTO();
		authenticationDTO.setUser(user);
		authenticationDTO.setPwd(pwd);
		authenticationDTO.setChallengeResult(result);
		Response response = target(SECURITY + "/authenticate").request()
				.post(Entity.entity(authenticationDTO, MediaType.APPLICATION_JSON));
		AuthenticationTokenDTO authenticationTokenDTO = null;
		if (response.getStatus() == Status.OK.getStatusCode()) {
			authenticationTokenDTO = response.readEntity(AuthenticationTokenDTO.class);
			authorizationToken = authenticationTokenDTO.getAuthToken();
		} else if (response.getStatus() == Status.INTERNAL_SERVER_ERROR.getStatusCode()
				|| response.getStatus() == Status.EXPECTATION_FAILED.getStatusCode()) {
			// do nothing
		}

		return authenticationTokenDTO;
	}
	
	protected String login() throws Exception {
		String user = USER;
		String pwd = PWD;
		String result;
		result = obtainChallengeResult();
		AuthenticationTokenDTO authenticationToken = authenticationFlow(user, pwd, result);
		assertEquals(Response.Status.OK.getReasonPhrase(), authenticationToken.getStatus());
		return authenticationToken.getAuthToken();
	}
}
