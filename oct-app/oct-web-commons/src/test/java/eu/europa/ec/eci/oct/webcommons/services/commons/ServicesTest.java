package eu.europa.ec.eci.oct.webcommons.services.commons;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import eu.europa.ec.eci.oct.entities.CountryProperty;
import eu.europa.ec.eci.oct.entities.Property;
import eu.europa.ec.eci.oct.entities.PropertyGroup;
import eu.europa.ec.eci.oct.entities.admin.Contact;
import eu.europa.ec.eci.oct.entities.admin.ContactRole;
import eu.europa.ec.eci.oct.entities.admin.Feedback;
import eu.europa.ec.eci.oct.entities.admin.FeedbackRange;
import eu.europa.ec.eci.oct.entities.admin.InitiativeDescription;
import eu.europa.ec.eci.oct.entities.admin.SystemPreferences;
import eu.europa.ec.eci.oct.entities.signature.Signature;
import eu.europa.ec.eci.oct.entities.system.Country;
import eu.europa.ec.eci.oct.entities.system.Language;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.signature.SignatureDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.supportForm.SupportFormDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.test.MockApi;
import eu.europa.ec.eci.oct.webcommons.services.api.transformer.ContactTransformer;
import eu.europa.ec.eci.oct.webcommons.services.api.transformer.ExportTransformer;
import eu.europa.ec.eci.oct.webcommons.services.api.transformer.SignatureTransformer;
import eu.europa.ec.eci.oct.webcommons.services.captcha.CaptchaService;
import eu.europa.ec.eci.oct.webcommons.services.contact.ContactService;
import eu.europa.ec.eci.oct.webcommons.services.enums.CountryEnum;
import eu.europa.ec.eci.oct.webcommons.services.enums.LanguageEnum;
import eu.europa.ec.eci.oct.webcommons.services.enums.PropertyEnum;
import eu.europa.ec.eci.oct.webcommons.services.enums.PropertyGroupEnum;
import eu.europa.ec.eci.oct.webcommons.services.initiative.InitiativeService;
import eu.europa.ec.eci.oct.webcommons.services.persistence.SignatureDAO;
import eu.europa.ec.eci.oct.webcommons.services.reporting.ReportingService;
import eu.europa.ec.eci.oct.webcommons.services.security.SecurityService;
import eu.europa.ec.eci.oct.webcommons.services.signature.SignatureService;
import eu.europa.ec.eci.oct.webcommons.services.system.SystemManager;
import eu.europa.ec.eci.oct.webcommons.services.translations.TranslationService;
import eu.europa.ec.eci.oct.webcommons.services.validation.RuleService;
import junit.framework.TestCase;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/services-test.xml")
@Transactional(propagation = Propagation.REQUIRED)
// @formatter:off
@DatabaseSetup({ "/mockData/hibernate_sequences.xml", "/mockData/oct_lang.xml", "/mockData/oct_property_group.xml",
		"/mockData/oct_property.xml", "/mockData/oct_country.xml", "/mockData/oct_country_property.xml",
		"/mockData/oct_settings.xml", "/mockData/oct_social_media.xml", "/mockData/oct_social_media_message.xml",
		"/mockData/oct_account.xml", "/mockData/oct_initiative_description.xml", "/mockData/oct_signature.xml",
		"/mockData/oct_system_prefs.xml", "/mockData/oct_feedback_range.xml", "/mockData/oct_feedback.xml",
		"/mockData/oct_contact.xml", "/mockData/oct_step_state.xml", "/mockData/FastSignatureCountView.xml",
		"/mockData/oct_authentication_lock.xml","/mockData/oct_translation.xml", "/mockData/SigByCountryView.xml", "/mockData/LastSignaturesView.xml",
		"/mockData/oct_country_rule.xml", "/mockData/oct_identity_value.xml" })
// @formatter:on
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, TransactionalTestExecutionListener.class,
		DbUnitTestExecutionListener.class })
public class ServicesTest extends TestCase {
	protected final String USER = "admin";
	protected final String PWD = "Qwerty123!";
	protected final static String DEFAULT_DESCRIPTION = "Default Description.";
	protected final static String MOCK_SIGNATURE_COUNTRY_FOR_CODE = "fr";
	protected final static long MOCK_SIGNATURE_COUNTRY_FOR_ID = 5l;
	protected static final LanguageEnum DEFAULT_LANGUAGE = LanguageEnum.ENGLISH;
	protected static Date nowDate = new Date();
	protected static Timestamp nowTimestamp = new Timestamp(nowDate.getTime());
	protected static String DEFAULT_REPRESENTATIVE_CONTACT_EMAIL;
	protected static String DEFAULT_REPRESENTATIVE_CONTACT_FIRST_NAME;
	protected static String DEFAULT_REPRESENTATIVE_CONTACT_FAMILY_NAME;
	protected static String DEFAULT_SUBSTITUTE_CONTACT_EMAIL;
	protected static String DEFAULT_SUBSTITUTE_CONTACT_FIRST_NAME;
	protected static String DEFAULT_SUBSTITUTE_CONTACT_FAMILY_NAME;
	protected static String DEFAULT_CONTACT_ORGANIZERS = "testContactOrganizer";
	static protected Map<Country, List<CountryProperty>> testCountryPropertiesMap;
	static Date tomorrowDate = DateUtils.addDays(nowDate, 1);

	public static final String INVALID_POSTAL_CODE = "1nv4l1dp0s";
	public static final String INVALID_DOCUMENT = "1nv4l1dd0cum3nt";
	public static final String AT_VALID_PASSPORT = "a1234567";
	public static final String AT_VALID_ID_CARD = "1234567";
	public static final String AT_VALID_POSTAL_CODE = "1234";
	public static final String PL_VALID_NATIONAL_ID_NO = "12345678901";
	public static final String DE_VALID_POSTAL_CODE = "12345";
	public static final String GB_VALID_POSTAL_CODE1 = "EC1A 1BB";
	public static final String GB_VALID_POSTAL_CODE2 = "W1A 0AX";
	public static final String GB_VALID_POSTAL_CODE3 = "M1 1AE";
	public static final String GB_VALID_POSTAL_CODE4 = "CR2 6XH";
	public static final String GB_VALID_POSTAL_CODE5 = "DN55 1PT";
	public static final String GB_VALID_POSTAL_CODE6 = "B33 8TH";
	public static final String FR_VALID_POSTAL_CODE = "12345";
	public static final String RO_VALID_ID_CARD = "ab123456";
	public static final String RO_VALID_PASSPORT = "ab123456";
	public static final String RO_VALID_PERSONAL_ID = "1234567890123";
	public static final String LV_VALID_PERSONAL_ID = "12345678901";
	public static final String BG_VALID_PERSONAL_NUMBER = "1234567890";
	public static final String LT_VALID_PERSONAL_NUMBER = "12345678901";
	public static final String LU_VALID_POSTAL_CODE = "1234";
	public static final String MT_VALID_ID_CARD = "1234abcde";
	public static final String NL_VALID_POSTAL_CODE = "1234ab";
	public static final String PT_VALID_PASSPORT = "a123456";
	public static final String PT_VALID_CITIZENS_CARD = "123456789ab1";
	public static final String SI_VALID_PERSONAL_NUMBER = "1234567890123";
	public static final String CZ_VALID_PASSPORT_1 = "1234567";
	public static final String CZ_VALID_PASSPORT_2 = "12345678";
	public static final String CZ_VALID_ID_CARD_1 = "123456789";
	public static final String CZ_VALID_ID_CARD_2 = "123456ab12";
	public static final String CZ_VALID_ID_CARD_3 = "123456ab";
	public static final String CZ_VALID_ID_CARD_4 = "ab123456";
	public static final String DK_VALID_POSTAL_CODE = "1234";
	public static final String ES_VALID_ID_CARD = "12345678a";
	public static final String ES_VALID_PASSPORT = "1a";
	public static final String HU_VALID_ID_CARD1 = "123456ab";
	public static final String HU_VALID_ID_CARD2 = "aba123456";
	public static final String HU_VALID_ID_CARD3 = "abab123456";
	public static final String HU_VALID_ID_CARD4 = "ababc123456";
	public static final String HU_VALID_ID_CARD5 = "ab123456";
	public static final String HU_VALID_PASSPORT1 = "ab123456";
	public static final String HU_VALID_PASSPORT2 = "ab1234567";
	public static final String HU_VALID_PERSONAL_NUMBER = "12345678901";
	public static final String IE_VALID_POSTAL_CODE = "1a";
	public static final String SE_VALID_PERSONAL_NUMBER1 = "12345678-1234";
	public static final String SE_VALID_PERSONAL_NUMBER2 = "123456-1234";
	public static final String SE_VALID_PERSONAL_NUMBER3 = "1234567890";
	public static final String SE_VALID_PERSONAL_NUMBER4 = "123456789012";
	public static final String IT_VALID_ID_CARD1 = "ab123456";
	public static final String IT_VALID_ID_CARD2 = "ab12345678";
	public static final String IT_VALID_ID_CARD3 = "1234567ab";
	public static final String IT_VALID_ID_CARD4 = "ab12345cd";
	public static final String IT_VALID_PASSPORT1 = "ab1234567";
	public static final String IT_VALID_PASSPORT2 = "a123456";
	public static final String IT_VALID_PASSPORT3 = "123456a";
	public static final String HR_VALID_PERSONAL_ID = "12345678901";
	public static final String BE_VALID_NATIONAL_ID_NUMBER = "85.01.01-123.12";
	public static final String FI_VALID_POSTAL_CODE = "12345";
	public static final String EE_VALID_PERSONAL_ID = "12345678901";
	public static final String GR_VALID_POSTAL_CODE = "12345";

	public static final String AT = CountryEnum.AUSTRIA.getCode();
	public static final String BE = CountryEnum.BELGIUM.getCode();
	public static final String BG = CountryEnum.BULGARIA.getCode();
	public static final String CY = CountryEnum.CYPRUS.getCode();
	public static final String CZ = CountryEnum.CZECH_REPUBLIC.getCode();
	public static final String DE = CountryEnum.GERMANY.getCode();
	public static final String DK = CountryEnum.DENMARK.getCode();
	public static final String EE = CountryEnum.ESTONIA.getCode();
	public static final String GR = CountryEnum.GREECE.getCode();
	public static final String ES = CountryEnum.SPAIN.getCode();
	public static final String FI = CountryEnum.FINLAND.getCode();
	public static final String FR = CountryEnum.FRANCE.getCode();
	public static final String HR = CountryEnum.CROATIA.getCode();
	public static final String HU = CountryEnum.HUNGARY.getCode();
	public static final String IE = CountryEnum.IRELAND.getCode();
	public static final String IT = CountryEnum.ITALY.getCode();
	public static final String LT = CountryEnum.LITHUANIA.getCode();
	public static final String LU = CountryEnum.LUXEMBURG.getCode();
	public static final String LV = CountryEnum.LATVIA.getCode();
	public static final String MT = CountryEnum.MALTA.getCode();
	public static final String NL = CountryEnum.NETHERLANDS.getCode();
	public static final String PL = CountryEnum.POLAND.getCode();
	public static final String PT = CountryEnum.PORTUGAL.getCode();
	public static final String RO = CountryEnum.ROMANIA.getCode();
	public static final String SE = CountryEnum.SWEDEN.getCode();
	public static final String SI = CountryEnum.SLOVENIA.getCode();
	public static final String SK = CountryEnum.SLOVAKIA.getCode();
	public static final String COMMON_COUNTRY_CODE = "common";

	protected Logger logger = (Logger) LogManager.getLogger(ServicesTest.class);

	@Autowired
	protected InitiativeService initiativeService;
	@Autowired
	protected SystemManager systemManager;
	@Autowired
	protected SignatureService signatureService;
	@Autowired
	protected TestEntitiesFactory testEntitiesFactory;
	@Autowired
	protected ReportingService reportingService;
	@Autowired
	protected ContactService contactService;
	@Autowired
	protected ContactTransformer contactTransformer;
	@Autowired
	protected SignatureTransformer signatureTransformer;
	@Autowired
	protected ExportTransformer exportTransformer;
	@Autowired
	protected CaptchaService captchaService;
	@Autowired
	protected RuleService ruleService;
	@Autowired
	protected TranslationService translationService;
	@Autowired
	protected SignatureDAO signatureDAO;
	@Autowired
	protected SecurityService securityService;

	protected List<InitiativeDescription> testAllDescriptions;
	protected List<Language> testDescriptionLanguages = new ArrayList<Language>();
	protected List<Signature> testSignatures;
	protected InitiativeDescription testInitiativeDescription;
	protected SystemPreferences testSystemPreferences;
	protected List<Contact> testContacts;
	protected List<Country> testCountries = new ArrayList<Country>();
	protected List<String> testCountryCodesA = new ArrayList<>();
	protected List<String> testCountryCodesB = new ArrayList<>();
	protected List<CountryProperty> testCountryProperties = new ArrayList<CountryProperty>();
	protected List<Language> testAllLanguages = new ArrayList<Language>();
	protected int testCountriesSizeNumber;
	protected int testLanguagesSizeNumber;
	protected List<PropertyGroup> testPropertyGroups;
	protected List<Feedback> testFeedbacks;
	protected List<FeedbackRange> testFeedbackRangeList;
	protected String TEST_FILE_STORAGE_PATH;
	protected List<Country> countries = new ArrayList<Country>();
	protected List<Property> propertyList = new ArrayList<Property>();
	protected List<PropertyGroup> propertyGroupList = new ArrayList<PropertyGroup>();
	protected Map<Long, List<CountryProperty>> countryPropertyMap = new HashMap<Long, List<CountryProperty>>();
	protected Map<String, Country> testCountryCodesMap = new HashMap<String, Country>();
	public static Date MOCKED_START_DATE;
	public static Date MOCKED_DEADLINE;

	@Before
	public void init() throws Exception {

		super.setUp();

		logger.info("> Initializing test setUp...");

		testAllDescriptions = initiativeService.getDescriptions();
		for (InitiativeDescription id : testAllDescriptions) {
			testDescriptionLanguages.add(id.getLanguage());
		}
		testSignatures = signatureService.getAllSignatures();
		testInitiativeDescription = initiativeService.getDefaultDescription();
		testContacts = contactService.getAllContacts();
		testSystemPreferences = systemManager.getSystemPreferences();
		TEST_FILE_STORAGE_PATH = testSystemPreferences.getFileStoragePath();
		MOCKED_DEADLINE = testSystemPreferences.getDeadline();
		MOCKED_START_DATE = testSystemPreferences.getRegistrationDate();

		testAllLanguages = systemManager.getAllLanguages();
		assertNotNull(testAllLanguages);
		assertFalse(testAllLanguages.isEmpty());
		testLanguagesSizeNumber = testAllLanguages.size();

		testCountries = systemManager.getAllCountries();
		testPropertyGroups = signatureService.getPropertyGroups();
		testCountryPropertiesMap = new HashMap<Country, List<CountryProperty>>();
		for (Country country : testCountries) {
			String countryCode = country.getCode();
			testCountryCodesMap.put(countryCode, country);
			for (PropertyGroup pg : testPropertyGroups) {
				List<CountryProperty> cps = signatureService.getProperties(country, pg);
				testCountryPropertiesMap.put(country, cps);
			}
			if (country.getCategory().equalsIgnoreCase(Country.CATEGORY_A)) {
				testCountryCodesA.add(countryCode);
			} else if (country.getCategory().equalsIgnoreCase(Country.CATEGORY_B)) {
				testCountryCodesB.add(countryCode);
			}
		}
		assertNotNull(testCountries);
		assertFalse(testCountries.isEmpty());
		testCountriesSizeNumber = testCountries.size();
		if (propertyGroupList.isEmpty()) {
			propertyGroupList = signatureService.getPropertyGroups();
		}
		if (countries.isEmpty()) {
			countries = systemManager.getAllCountries();
		}
		if (propertyList.isEmpty()) {
			propertyList = systemManager.getAllProperties();
		}
		if (testCountryProperties.isEmpty()) {
			testCountryProperties = signatureService.getAllCountryProperties();
		}
		if (countryPropertyMap.isEmpty()) {
			for (CountryProperty cp : testCountryProperties) {
				if (!countryPropertyMap.containsKey(cp.getCountry().getId())) {
					countryPropertyMap.put(cp.getCountry().getId(), new ArrayList<CountryProperty>());
				}
				countryPropertyMap.get(cp.getCountry().getId()).add(cp);
			}
		}
		testFeedbackRangeList = reportingService.getFeedbackRanges();
		testFeedbacks = reportingService.getAllFeedbacks();

		for (Contact contact : testContacts) {
			if (contact.getContactRole().equals(ContactRole.REPRESENTATIVE)) {
				DEFAULT_REPRESENTATIVE_CONTACT_EMAIL = contact.getEmail();
				DEFAULT_REPRESENTATIVE_CONTACT_FIRST_NAME = contact.getFirstName();
				DEFAULT_REPRESENTATIVE_CONTACT_FAMILY_NAME = contact.getFamilyName();
			}
			if (contact.getContactRole().equals(ContactRole.SUBSTITUTE)) {
				DEFAULT_SUBSTITUTE_CONTACT_EMAIL = contact.getEmail();
				DEFAULT_SUBSTITUTE_CONTACT_FIRST_NAME = contact.getFirstName();
				DEFAULT_SUBSTITUTE_CONTACT_FAMILY_NAME = contact.getFamilyName();
			}
		}
		logger.info("> ...test initialization setup successfully completed.");
	}

	public Property getPropertyBy(PropertyEnum pe) throws Exception {
		for (Property p : propertyList) {
			if (pe.getId() == p.getId()) {
				return p;
			}
		}
		throw new Exception("No match found in Property for entry [" + pe + "]");
	}

	public PropertyGroup getPropertyGroupBy(PropertyGroupEnum pge) throws Exception {
		for (PropertyGroup pg : propertyGroupList) {
			if (pge.getId() == pg.getId()) {
				return pg;
			}
		}
		throw new Exception("No match found in PropertyGroup for entry [" + pge + "]");
	}

	public Country getCountryBy(CountryEnum ce) throws Exception {
		for (Country c : countries) {
			if (c.getCode().equalsIgnoreCase(ce.getCode())) {
				return c;
			}
		}
		throw new Exception("No country found in PropertyGroup for code [" + ce + "]");
	}

	protected Signature insertTestSignature() throws Exception {
		SignatureDTO newSignatureDTO = new SignatureDTO();
		Country countryToSignFor = testCountries.get(0);
		newSignatureDTO.setCountry(countryToSignFor.getCode());
		List<CountryProperty> countryProperties = signatureService
				.getCountryPropertiesByCountryCode(countryToSignFor.getCode());
		List<SupportFormDTO> propertyValues = MockApi.getMockedProperties(countryProperties);
		newSignatureDTO.setProperties(propertyValues);

		String persistedSignatureUUID = null;
		try {
			Signature newSignature = signatureTransformer.transform(newSignatureDTO);
			persistedSignatureUUID = signatureService.insertSignature(newSignature);
			assertFalse(StringUtils.isBlank(persistedSignatureUUID));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		return signatureService.findByUuid(persistedSignatureUUID);
	}

}