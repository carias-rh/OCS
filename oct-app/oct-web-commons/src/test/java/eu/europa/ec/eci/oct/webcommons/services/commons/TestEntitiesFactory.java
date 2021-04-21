package eu.europa.ec.eci.oct.webcommons.services.commons;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import eu.europa.ec.eci.oct.entities.CountryProperty;
import eu.europa.ec.eci.oct.entities.PropertyGroup;
import eu.europa.ec.eci.oct.entities.admin.InitiativeDescription;
import eu.europa.ec.eci.oct.entities.admin.SystemPreferences;
import eu.europa.ec.eci.oct.entities.admin.SystemState;
import eu.europa.ec.eci.oct.entities.system.Country;
import eu.europa.ec.eci.oct.entities.system.Language;
import eu.europa.ec.eci.oct.webcommons.services.enums.LanguageEnum;

@Component
public class TestEntitiesFactory {

	protected Logger logger = LogManager.getLogger(ServicesTest.class);

	static Date nowDate = new Date();
	static long JAN_1ST_2017_MSEC = 1483225200000L;
	static Timestamp nowTimestamp = new Timestamp(nowDate.getTime());

	public final static String DEFAULT_DESCRIPTION = "Default Description.";
	public final static Boolean COLLECTING = true;
	public final static Boolean NOT_COLLECTING = false;
	public final static String certContentType = "application/pdf";

	public static final String DEFAULT_CONTACT_EMAIL = "test@contact.email";
	public static final String DEFAULT_CONTACT_NAME = "testContactName";
	public static final String DEFAULT_CONTACT_ORGANIZERS = "testContactOrganizer";

	/**
	 * @return InitiativeDescription with default ENGLISH Language
	 */
	public static InitiativeDescription getInitiativeDescription() {
		return getInitiativeDescription(LanguageEnum.ENGLISH);
	}

	/**
	 * @param languageEnum
	 * @return InitiativeDescription with specified Language
	 * 
	 */

	static InitiativeDescription initiativeDescription;

	public static InitiativeDescription getInitiativeDescription(LanguageEnum languageEnum) {
		initiativeDescription = new InitiativeDescription();
		initiativeDescription.setLanguage(LanguageEnum.getLanguageFromEnum(languageEnum));
		initiativeDescription.setObjectives("testObjectives");
		initiativeDescription.setTitle("testTitle");
		initiativeDescription.setUrl("testURL");
		return initiativeDescription;
	}

	static protected List<Country> testCountries = new ArrayList<Country>();
	static protected Map<Country, List<CountryProperty>> testCountryPropertiesMap;
	static protected List<PropertyGroup> testPropertyGroups;

	public static SystemPreferences getSystemPreferences() {
		SystemPreferences systemPreferences = new SystemPreferences();
		systemPreferences.setCollecting(COLLECTING);
		systemPreferences.setCommissionRegisterUrl("commissionRegisterURL");
		systemPreferences.setDeadline(nowDate);
		systemPreferences.setFileStoragePath("");
		systemPreferences.setRegistrationDate(nowTimestamp);
		systemPreferences.setRegistrationNumber("1234");
		systemPreferences.setState(SystemState.OPERATIONAL);

		return systemPreferences;
	}

	/**
	 * @return default en language
	 */
	public static Language getDefaultLanguage() {
		Language language = new Language();
		language.setCode(LanguageEnum.ENGLISH.getLangCode());
		language.setDisplayOrder(LanguageEnum.ENGLISH.getDisplayOrder());
		// language.setLabel(label);
		language.setName(LanguageEnum.ENGLISH.getName());
		return language;
	}

	// public Map<String, Integer> insertTestSignatures(List<Country>
	// countriesToSignFor, int signaturesSize, List<InitiativeDescription>
	// descriptionsToSignFor, boolean randomDistribution)
	// throws Exception {
	//
	// Map<String, Integer> insertedSignaturesMap = new HashMap<String, Integer>();
	// int signatursAmountToInsert = signaturesSize;
	// if (randomDistribution) {
	// signatursAmountToInsert = getRandomNumber(signaturesSize);
	// }
	//
	// List<CountryProperty> allCountryProperties =
	// signatureService.getAllCountryProperties();
	// Map<String, List<CountryProperty>> countryPropertiesMap = new HashMap<String,
	// List<CountryProperty>>();
	// for (Country country : countriesToSignFor) {
	// String code = country.getCode();
	// for (CountryProperty countryProperty : allCountryProperties) {
	// if (countryProperty.getCountry().getCode().equals(code)) {
	// if (countryPropertiesMap.containsKey(code)) {
	// countryPropertiesMap.get(code).add(countryProperty);
	// } else {
	// List<CountryProperty> countryPropertyList = new ArrayList<CountryProperty>();
	// countryPropertyList.add(countryProperty);
	// countryPropertiesMap.put(code, countryPropertyList);
	// }
	// }
	// }
	// }
	// int signatureInsertedCounter = 0;
	// for (Country country : countriesToSignFor) {
	// String countryCode = country.getCode();
	// if (!insertedSignaturesMap.containsKey(countryCode)) {
	// insertedSignaturesMap.put(countryCode, 0);
	// }
	// for (InitiativeDescription initiativeDescription : descriptionsToSignFor) {
	// for (int i = 0; i < signatursAmountToInsert; i++) {
	//
	// SignatureDTO signatureDTO = new SignatureDTO();
	// signatureDTO.setCountry(country.getCode());
	// signatureDTO.setLanguage(initiativeDescription.getLanguage().getCode());
	//
	// long minMsec = ServicesTest.MOCKED_START_DATE.getTime();
	// long maxMsec = ServicesTest.MOCKED_DEADLINE.getTime();
	// Date randomDate = DateUtils.getRandomDateBetween(minMsec, maxMsec);
	//
	// List<SupportFormDTO> propertyValues = new ArrayList<SupportFormDTO>();
	// List<CountryProperty> requiredCountryProperties =
	// countryPropertiesMap.get(countryCode);
	// for (CountryProperty requiredCountryProperty : requiredCountryProperties) {
	// SupportFormDTO supportFormDTO = new SupportFormDTO();
	// supportFormDTO.setId(requiredCountryProperty.getId());
	// supportFormDTO.setGroup(requiredCountryProperty.getProperty().getGroup().getId().intValue());
	// supportFormDTO.setLabel(requiredCountryProperty.getProperty().getName());
	// supportFormDTO.setValue(i + "_" + System.currentTimeMillis() +
	// Math.random());
	// propertyValues.add(supportFormDTO);
	// }
	// signatureDTO.setProperties(propertyValues);
	// try {
	// Signature signature = signatureTransformer.transform(signatureDTO);
	// String uuid = signatureService.insertSignature(signature, randomDate);
	// int oldCount = insertedSignaturesMap.get(countryCode);
	// int newCount = oldCount + 1;
	// insertedSignaturesMap.put(countryCode, newCount);
	// signatureInsertedCounter++;
	// System.out.println("Inserted testSignature #" + signatureInsertedCounter + "
	// for country[" + country.getCode() + "] and description ["
	// + initiativeDescription.getLanguage().getCode() + "] UUID=" + uuid);
	// } catch (Exception e) {
	// e.printStackTrace();
	// throw new Exception("Can't insert signature: " + e.getMessage());
	// }
	// }
	// }
	// }
	// return insertedSignaturesMap;
	// }
	//
	// public void insertTestDescriptions(List<Language> allLanguages) throws
	// OCTException {
	// int insertedInitiativeDescriptionCounter = 1;
	// for (Language language : allLanguages) {
	// InitiativeDescription initiativeDescription = new InitiativeDescription();
	// initiativeDescription.setLanguage(language);
	// String languageCode = language.getCode();
	// logger.info("Inserting initiativeDescription #" +
	// insertedInitiativeDescriptionCounter + " [" + languageCode + "]");
	// initiativeDescription.setObjectives("[" + languageCode + "]" + "objectives");
	// initiativeDescription.setSubjectMatter("[" + languageCode + "]" + "subject
	// matter");
	// initiativeDescription.setTitle("[" + languageCode + "]" + "title");
	// initiativeDescription.setUrl("http://www.example." + languageCode);
	// initiativeService.saveOrUpdateInitiativeDescription(initiativeDescription);
	// insertedInitiativeDescriptionCounter++;
	// }
	// }

	public int getRandomNumber(int max) {
		return new Random().nextInt(max);
	}

}
