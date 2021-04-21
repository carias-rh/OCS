package eu.europa.ec.eci.oct.webcommons.commons;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.europa.ec.eci.oct.entities.CountryProperty;
import eu.europa.ec.eci.oct.entities.admin.Feedback;
import eu.europa.ec.eci.oct.entities.admin.FeedbackRange;
import eu.europa.ec.eci.oct.entities.admin.InitiativeDescription;
import eu.europa.ec.eci.oct.entities.system.Country;
import eu.europa.ec.eci.oct.entities.system.Language;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.signature.SignatureDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.supportForm.SupportFormDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.test.MockApi;
import eu.europa.ec.eci.oct.webcommons.services.enums.PropertyEnum;
import eu.europa.ec.eci.oct.webcommons.services.enums.PropertyGroupEnum;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;
import eu.europa.ec.eci.oct.webcommons.services.security.RequestTokenService;

public class TestUtils {

	protected static Logger logger = LogManager.getLogger(RequestTokenService.class);

	public static SignatureDTO buildTestSignature(String countryCode, boolean isCategoryA)
			throws Exception {
		SignatureDTO signatureDTO = new SignatureDTO();
		signatureDTO.setCountry(countryCode);
		signatureDTO.setOptionalValidation(false);

		List<SupportFormDTO> properties = new ArrayList<SupportFormDTO>();
		if (!isCategoryA) {
			SupportFormDTO document = new SupportFormDTO();
			document.setValue(PropertyEnum.PASSPORT.getName() + "_" + System.currentTimeMillis());
			document.setGroup((int) PropertyGroupEnum.ID.getId());
			document.setLabel(PropertyEnum.PASSPORT.getName());
			document.setRequired(1);

			SupportFormDTO fullFirstNames = new SupportFormDTO();
			fullFirstNames.setValue(PropertyEnum.FULL_FIRST_NAMES.getName() + "_" + System.currentTimeMillis());
			fullFirstNames.setGroup((int) PropertyGroupEnum.ID.getId());
			fullFirstNames.setLabel(PropertyEnum.FULL_FIRST_NAMES.getName());
			fullFirstNames.setRequired(1);

			SupportFormDTO familyNames = new SupportFormDTO();
			familyNames.setValue(PropertyEnum.FAMILY_NAMES.getName() + "_" + System.currentTimeMillis());
			familyNames.setGroup((int) PropertyGroupEnum.ID.getId());
			familyNames.setLabel(PropertyEnum.FAMILY_NAMES.getName());
			familyNames.setRequired(1);

			properties.add(document);
			properties.add(fullFirstNames);
			properties.add(familyNames);

		} else {
			SupportFormDTO fullFirstNames = new SupportFormDTO();
			fullFirstNames.setValue(PropertyEnum.FULL_FIRST_NAMES.getName() + "_" + System.currentTimeMillis());
			fullFirstNames.setGroup((int) PropertyEnum.FULL_FIRST_NAMES.getGroupId());
			fullFirstNames.setLabel(PropertyEnum.FULL_FIRST_NAMES.getName());
			fullFirstNames.setRequired(1);

			SupportFormDTO familyNames = new SupportFormDTO();
			familyNames.setValue(PropertyEnum.FAMILY_NAMES.getName() + "_" + System.currentTimeMillis());
			familyNames.setGroup((int) PropertyEnum.FAMILY_NAMES.getGroupId());
			familyNames.setLabel(PropertyEnum.FAMILY_NAMES.getName());
			familyNames.setRequired(1);

			SupportFormDTO birthDate = new SupportFormDTO();
			birthDate.setValue(getRandomDateString());
			birthDate.setGroup((int) PropertyEnum.DATE_OF_BIRTH.getGroupId());
			birthDate.setLabel(PropertyEnum.DATE_OF_BIRTH.getName());
			birthDate.setRequired(1);

			SupportFormDTO residenceStreet = new SupportFormDTO();
			residenceStreet.setValue(PropertyEnum.RESIDENCE_STREET.getName() + "_" + System.currentTimeMillis());
			residenceStreet.setGroup((int) PropertyEnum.RESIDENCE_STREET.getGroupId());
			residenceStreet.setLabel(PropertyEnum.RESIDENCE_STREET.getName());
			residenceStreet.setRequired(1);

			SupportFormDTO residenceNumber = new SupportFormDTO();
			residenceNumber.setValue(PropertyEnum.RESIDENCE_STREET_NUMBER.getName() + "_" + System.currentTimeMillis());
			residenceNumber.setGroup((int) PropertyEnum.RESIDENCE_STREET_NUMBER.getGroupId());
			residenceNumber.setLabel(PropertyEnum.RESIDENCE_STREET_NUMBER.getName());
			residenceNumber.setRequired(1);

			SupportFormDTO residencePostalCode = new SupportFormDTO();
			String currentMillis = "" + System.currentTimeMillis();
			residencePostalCode.setValue(currentMillis.substring(currentMillis.length() - 5));
			residencePostalCode.setGroup((int) PropertyEnum.RESIDENCE_POSTAL_CODE.getGroupId());
			residencePostalCode.setLabel(PropertyEnum.RESIDENCE_POSTAL_CODE.getName());
			residencePostalCode.setRequired(1);

			SupportFormDTO residenceCity = new SupportFormDTO();
			residenceCity.setValue(PropertyEnum.RESIDENCE_CITY.getName() + "_" + System.currentTimeMillis());
			residenceCity.setGroup((int) PropertyEnum.RESIDENCE_CITY.getGroupId());
			residenceCity.setLabel(PropertyEnum.RESIDENCE_CITY.getName());
			residenceCity.setRequired(1);

			SupportFormDTO residenceCountry = new SupportFormDTO();
			residenceCountry.setValue(countryCode);
			residenceCountry.setGroup((int) PropertyEnum.RESIDENCE_COUNTRY.getGroupId());
			residenceCountry.setLabel(PropertyEnum.RESIDENCE_COUNTRY.getName());
			residenceCountry.setRequired(1);

			properties.add(fullFirstNames);
			properties.add(birthDate);
			properties.add(familyNames);
			properties.add(residenceStreet);
			properties.add(residenceNumber);
			properties.add(residencePostalCode);
			properties.add(residenceCity);
			properties.add(residenceCountry);

		}

		signatureDTO.setProperties(properties);

		return signatureDTO;

	}

	public static String getRandomDateString() {
		String randomDay = "" + getRandomInt(10, 30);
		String randomMonth = "" + getRandomInt(10, 12);
		String randomYear = "" + getRandomInt(1940, 1999);
		return randomDay + "/" + randomMonth + "/" + randomYear;
	}

	public static int getRandomInt(int min, int max) {
		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}

	public static char getRandomChar() {
		Random r = new Random();
		return (char) (r.nextInt(26) + 'a');
	}

	public static List<Feedback> buildTestFeedbacks(int size, List<FeedbackRange> allFeedbackRanges)
			throws OCTException {
		List<Feedback> testFeedbacks = new ArrayList<Feedback>();
		for (int i = 0; i < size; i++) {
			Feedback randomFeedback = new Feedback();
			Random r = new Random();
			int randomFeedbackRangeIndex = r.nextInt((allFeedbackRanges.size() - 1) - 0) + allFeedbackRanges.size() - 1;
			FeedbackRange randomFeedbackRange = allFeedbackRanges.get(randomFeedbackRangeIndex);
			randomFeedback.setFeedbackRange(randomFeedbackRange);
			randomFeedback.setFeedbackComment("[" + i + "-" + System.currentTimeMillis() + "] I passed here at "
					+ new Date() + "  to leave you my comment which is " + randomFeedbackRange);
			logger.info("Inserting testFeedback #" + (i + 1) + " on " + size + ": " + randomFeedback);
			testFeedbacks.add(randomFeedback);
		}
		return testFeedbacks;
	}

	public static List<SignatureDTO> buildTestSignatures(List<CountryProperty> allCountryProperties,
			List<Country> countries, long signaturesSize, boolean randomDistribution) throws Exception {
		List<SignatureDTO> signatures = new ArrayList<SignatureDTO>();
		Map<String, List<CountryProperty>> countryPropertiesMap = new HashMap<String, List<CountryProperty>>();
		for (Country country : countries) {
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
		// int signatureInsertedCounter = 1;
		for (Country country : countries) {
			// logger.info("Country: " + country.getName());
			if (randomDistribution) {
				signaturesSize = getRandomInt(1, (int) signaturesSize);
			}
			for (long i = 0; i < signaturesSize; i++) {
				String countryCode = country.getCode();
				// logger.info("Building testSignature #" + signatureInsertedCounter + " of " +
				// signaturesSize + " for country[" + countryCode + "] and description [" +
				// langCode + "]");
				SignatureDTO signatureDTO = new SignatureDTO();
				signatureDTO.setCountry(countryCode);

				List<SupportFormDTO> propertyValues = new ArrayList<SupportFormDTO>();
				propertyValues = MockApi.getMockedProperties(countryPropertiesMap.get(countryCode));
				signatureDTO.setProperties(propertyValues);
				// signatureInsertedCounter++;
				signatures.add(signatureDTO);
			}
		}
		// logger.info("inserted " + signatureInsertedCounter +
		// " testSignatures ");
		return signatures;
	}

	public static List<InitiativeDescription> buildTestDescriptions(List<Language> allLanguages,
			InitiativeDescription defaultInitiativeDescription) throws OCTException {
		List<InitiativeDescription> descriptions = new ArrayList<InitiativeDescription>();
		int insertedInitiativeDescriptionCounter = 1;
		for (Language language : allLanguages) {
			InitiativeDescription initiativeDescription = new InitiativeDescription();
			initiativeDescription.setLanguage(language);
			logger.info("Building initiativeDescription #" + insertedInitiativeDescriptionCounter + " ["
					+ language.getCode() + "]");
			initiativeDescription
					.setObjectives("[" + language.getCode() + "]" + defaultInitiativeDescription.getObjectives());
			initiativeDescription.setTitle("[" + language.getCode() + "]" + defaultInitiativeDescription.getTitle());
			initiativeDescription.setUrl("[" + language.getCode() + "]" + defaultInitiativeDescription.getUrl());
			insertedInitiativeDescriptionCounter++;
			descriptions.add(initiativeDescription);
		}
		return descriptions;
	}

	public static Boolean getRandomBoolean() {
		return new Random().nextBoolean();
	}

}
