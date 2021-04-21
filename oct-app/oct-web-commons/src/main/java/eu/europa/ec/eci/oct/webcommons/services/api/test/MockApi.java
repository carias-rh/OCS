package eu.europa.ec.eci.oct.webcommons.services.api.test;

import static eu.europa.ec.eci.oct.webcommons.services.api.domain.ApiResponse.buildError;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Service;

import eu.europa.ec.eci.oct.entities.CountryProperty;
import eu.europa.ec.eci.oct.entities.admin.Feedback;
import eu.europa.ec.eci.oct.entities.admin.FeedbackRange;
import eu.europa.ec.eci.oct.entities.admin.SystemPreferences;
import eu.europa.ec.eci.oct.entities.signature.IdentityValue;
import eu.europa.ec.eci.oct.entities.signature.Signature;
import eu.europa.ec.eci.oct.entities.system.Country;
import eu.europa.ec.eci.oct.utils.DateUtils;
import eu.europa.ec.eci.oct.validation.ValidationBean;
import eu.europa.ec.eci.oct.validation.ValidationError;
import eu.europa.ec.eci.oct.validation.ValidationProperty;
import eu.europa.ec.eci.oct.validation.ValidationResult;
import eu.europa.ec.eci.oct.webcommons.services.api.RestApiParent;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.signature.SignatureDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.signature.SignatureValidation;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.supportForm.SupportFormDTO;
import eu.europa.ec.eci.oct.webcommons.services.enums.PropertyEnum;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTDuplicateSignatureException;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;

@Service
@Path("/mocks")
@PropertySource(value = { "classpath:application.properties" })
public class MockApi extends RestApiParent {

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Value("${mock.enabled:true}")
	private boolean mockFeaturesAllowed;

	public static String SIG04 = "SIG04: ";
	public static final String DUPLICATE_IDENTITY_FOUND_MESSAGE = "An identical identity value has already been persisted";

	public static final String AT_VALID_PASSPORT = "A2345678";
	public static final String AT_VALID_ID_CARD = "12345678";
	public static final String AT_VALID_POSTAL_CODE = "1234";
	public static final String PL_VALID_NATIONAL_ID_NO = "84042808112";
	public static final String DE_VALID_POSTAL_CODE = "12345";
	public static final String UK_VALID_POSTAL_CODE1 = "EC1A 1BB";
	public static final String UK_VALID_POSTAL_CODE2 = "W1A 0AX";
	public static final String UK_VALID_POSTAL_CODE3 = "M1 1AE";
	public static final String UK_VALID_POSTAL_CODE4 = "CR2 6XH";
	public static final String UK_VALID_POSTAL_CODE5 = "DN55 1PT";
	public static final String UK_VALID_POSTAL_CODE6 = "B33 8TH";
	public static final String FR_VALID_POSTAL_CODE = "12345";
	public static final String RO_VALID_ID_CARD = "AS123456";
	public static final String RO_VALID_PASSPORT1 = "12345678";
	public static final String RO_VALID_PASSPORT2 = "123456789";
	public static final String RO_VALID_PERSONAL_ID = "1234567891234";
	public static final String LV_VALID_PERSONAL_ID = "12345678912";
	public static final String BG_VALID_PERSONAL_NUMBER = "7311189991";
	public static final String LT_VALID_PERSONAL_NUMBER = "50011110005";
	public static final String LU_VALID_POSTAL_CODE = "1234";
	public static final String MT_VALID_ID_CARD = "1234567A";
	public static final String NL_VALID_POSTAL_CODE = "1234ab";
	public static final String PT_VALID_PASSPORT1 = "A123123";
	public static final String PT_VALID_PASSPORT2 = "AB123123";
	public static final String PT_VALID_ID_CARD = "1234567";
	public static final String PT_VALID_CITIZENS_CARD = "163848998ZW7";
	public static final String SI_VALID_PERSONAL_NUMBER = "1234567891234";
	public static final String CZ_VALID_PASSPORT_1 = "1234567";
	public static final String CZ_VALID_PASSPORT_2 = "12345678";
	public static final String CZ_VALID_ID_CARD_1 = "123456789";
	public static final String CZ_VALID_ID_CARD_2 = "123456AA12";
	public static final String CZ_VALID_ID_CARD_3 = "123456AA";
	public static final String CZ_VALID_ID_CARD_4 = "AA123123";
	public static final String DK_VALID_POSTAL_CODE = "1234";
	public static final String ES_VALID_ID_CARD = "78825841B";
	public static final String ES_VALID_PASSPORT = "AAA123456";
	public static final String HU_VALID_ID_CARD1 = "BB123456";
	public static final String HU_VALID_ID_CARD2 = "123456AA";
	public static final String HU_VALID_ID_CARD3 = "AAAA123456";
	public static final String HU_VALID_ID_CARD4 = "AAAAA123456";
	public static final String HU_VALID_PASSPORT1 = "AB123456";
	public static final String HU_VALID_PASSPORT2 = "AB1234567";
	public static final String HU_VALID_PERSONAL_NUMBER = "12345678901";
	public static final String IE_VALID_POSTAL_CODE = "1a @";
	public static final String SE_VALID_PERSONAL_NUMBER1 = "880731-1234";
	public static final String SE_VALID_PERSONAL_NUMBER2 = "19880731-1234";
	public static final String SE_VALID_PERSONAL_NUMBER3 = "8807311234";
	public static final String SE_VALID_PERSONAL_NUMBER4 = "198807311234";
	public static final String IT_VALID_ID_CARD1 = "AS123456";
	public static final String IT_VALID_ID_CARD2 = "1234567DD";
	public static final String IT_VALID_ID_CARD3 = "EE1234567";
	public static final String IT_VALID_ID_CARD4 = "FF12345678";
	public static final String IT_VALID_ID_CARD5 = "GG12345HH";
	public static final String IT_VALID_PASSPORT1 = "AS1234567";
	public static final String IT_VALID_PASSPORT2 = "A123456";
	public static final String IT_VALID_PASSPORT3 = "123456B";
	public static final String HR_VALID_PERSONAL_ID = "99018651995";
	public static final String BE_VALID_NATIONAL_REGISTER_NUMBER = "79.06.13-651.78";
	public static final String FI_VALID_POSTAL_CODE = "12345";
	public static final String EE_VALID_PERSONAL_NUMBER = "48507201231";
	public static final String GR_VALID_POSTAL_CODE = "12345";
	public static final String CY_VALID_PASSPORT1 = "B123456";
	public static final String CY_VALID_PASSPORT2 = "C123456";
	public static final String CY_VALID_PASSPORT3 = "E123456";
	public static final String CY_VALID_PASSPORT4 = "J123456";
	public static final String CY_VALID_PASSPORT5 = "DP1234567";
	public static final String CY_VALID_PASSPORT6 = "SP1234567";
	public static final String CY_VALID_PASSPORT7 = "K12345678";
	public static final String CY_VALID_ID_CARD = "123";
	public static final String SK_VALID_POSTAL_CODE = "01234";
	
	@POST
	@Path("/insert")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized Response insert(SignatureDTO signatureDTO) {
		if (!mockFeaturesAllowed) {
			logger.warn("MOCK FEATURES DISABLED");
			apiResponse = buildError(Status.UNAUTHORIZED.getStatusCode(), "Unauthorized Request");
			return Response.status(Status.UNAUTHORIZED).entity(apiResponse).build();
		}
		logger.warn("MOCK FEATURES ENABLED");
		// check the collection mode first of all
		boolean collectionMode = false;
		try {
			collectionMode = systemManager.getCollecting();
		} catch (OCTException e3) {
			logger.error(e3.getMessage());
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					SIG04 + " Signature submission error.");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse).build();
		}
		if (!collectionMode) {
			apiResponse = buildError(Status.NOT_ACCEPTABLE.getStatusCode(), SIG04 + " Collection mode is OFF");
			return Response.status(Status.NOT_ACCEPTABLE).entity(apiResponse).build();
		}

		SignatureValidation signatureValidation = new SignatureValidation();
		signatureValidation.setCaptchaValidation(true);
		// check that language code and country code are expected
		try {
			List<String> allCountryCodes = systemManager.getAllCountryCodes();
			if (!allCountryCodes.contains(signatureDTO.getCountry())) {
				apiResponse = buildError(Status.EXPECTATION_FAILED.getStatusCode(),
						SIG04 + " invalid country: " + signatureDTO.getCountry());
				return Response.status(Status.EXPECTATION_FAILED).entity(apiResponse).build();
			}
		} catch (OCTException e3) {
			logger.error(e3.getMessage());
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					SIG04 + " Signature submission error.");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse).build();
		}

		// SignatureDTO validation
		for (SupportFormDTO sDTO : signatureDTO.getProperties()) {
			if (StringUtils.isAllBlank(sDTO.getValue())) {
				apiResponse = buildError(Status.EXPECTATION_FAILED.getStatusCode(),
						SIG04 + " null or empty value for : " + sDTO.getLabel());
				return Response.status(Status.EXPECTATION_FAILED).entity(apiResponse).build();
			}
		}

		// transform the signature
		Signature signature = null;
		try {
			signature = signatureTransformer.transform(signatureDTO);
		} catch (Exception e1) {
			logger.error(e1.getMessage());
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					SIG04 + " Signature submission error.");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse).build();
		}

		// check identity duplicate
		IdentityValue identityValue = null;
		try {
			identityValue = checkIdentityDuplicates(signatureDTO);
		} catch (OCTDuplicateSignatureException e) {
			apiResponse = buildError(Status.CONFLICT.getStatusCode(), SIG04 + DUPLICATE_IDENTITY_FOUND_MESSAGE);
			return Response.status(Status.CONFLICT).entity(apiResponse).build();
		} catch (OCTException e) {
			logger.error(e.getMessage());
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					SIG04 + " Signature submission error.");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse).build();
		}

		// try insert
		try {
			signatureService.insertSignature(signature);
			if (identityValue != null) {
				signatureService.storeIdentityValue(identityValue);
			}
		} catch (OCTDuplicateSignatureException e) {
			logger.error(e.getMessage());
			apiResponse = buildError(Status.CONFLICT.getStatusCode(),
					SIG04 + " Signature submission error. Duplicate signature");
			return Response.status(Status.CONFLICT).entity(apiResponse).build();
		} catch (OCTException e) {
			logger.error(e.getMessage());
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					SIG04 + " Signature submission error.");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse).build();
		}

		signatureValidation.setSignatureIdentifier(signature.getUuid());
		signatureValidation.setCaptchaValidation(true);
		return Response.status(Status.OK).entity(signatureValidation)
				.header("Cache-Control", "no-store, no-cache, must-revalidate").build();

	}

	/**
	 * Validation again the content of SignatureDTO. If the validation fails, a
	 * Response will be returned, null if all is ok.
	 *
	 * @param signatureDTO
	 * @param signatureValidation
	 * @return Response in case of errors, null if all is ok.
	 */
	private Response validateSignature(SignatureDTO signatureDTO, SignatureValidation signatureValidation) {
		ValidationBean validationBean = new ValidationBean();
		validationBean.setNationality(signatureDTO.getCountry());
		for (SupportFormDTO supportForm : signatureDTO.getProperties()) {
			ValidationProperty validationProperty = new ValidationProperty();
			validationProperty.setKey(supportForm.getLabel());
			validationProperty.setValue(supportForm.getValue());
			validationBean.addProperty(validationProperty);
		}

		try {
			ValidationResult validationResult = ruleService.validate(validationBean);
			if (!validationResult.getValidationErrors().isEmpty()) {
				// we have errors on validation
				for (ValidationError validationError : validationResult.getValidationErrors()) {
					signatureValidation.addErrorField(validationError.getKey(), validationError.getErrorKey(),
							validationError.isSkippable());
					logger.debug(validationError.toString());
				}

				if (!validationResult.isValidationSkippable() || !signatureDTO.isOptionalValidation()) {
					apiResponse = buildError(Status.EXPECTATION_FAILED.getStatusCode(),
							SIG04 + "One or more form field values are not correct.");
					return Response.status(Status.EXPECTATION_FAILED).entity(signatureValidation).build();
				}
			}
		} catch (OCTException e) {
			logger.error(e.getMessage());
			apiResponse = buildError(Status.EXPECTATION_FAILED.getStatusCode(),
					SIG04 + " Validation of the signature error. " + e.getMessage());
			return Response.status(Status.EXPECTATION_FAILED).entity(apiResponse).build();
		}
		// all is ok
		return null;
	}

	@GET
	@Path("/mockDB/{stressIndex}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response mockDB(@PathParam("stressIndex") String stressIndex) {
		if (!mockFeaturesAllowed) {
			apiResponse = buildError(Status.UNAUTHORIZED.getStatusCode(), "Unauthorized Request");
			return Response.status(Status.UNAUTHORIZED).entity(apiResponse).build();
		}
		try {
			// signatureService.deleteAllSignatures();
			/* insert all possible descriptions (one for each language) */

			// all signatures will be distributed for each language
			int size = insertTestSignatures(systemManager.getAllCountries(), Integer.parseInt(stressIndex));

			return Response.status(Status.OK).entity(new String("Created " + size + " signatures")).build();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new String(e.getMessage())).build();
		}

	}

	private void insertTestFeedback(String signatureIdentifier) throws OCTException {
		List<FeedbackRange> allFeedbackRanges = reportingService.getFeedbackRanges();
		Feedback randomFeedback = new Feedback();
		int randomFeedbackRangeIndex = getRandomInt(0, allFeedbackRanges.size() - 1);
		FeedbackRange randomFeedbackRange = allFeedbackRanges.get(randomFeedbackRangeIndex);
		randomFeedback.setFeedbackRange(randomFeedbackRange);
		randomFeedback.setFeedbackComment("[" + System.currentTimeMillis() + "] I passed here to leave you my "
				+ randomFeedbackRange.getLabel().substring(13) + " feedback. ");
		randomFeedback.setSignatureIdentifier(signatureIdentifier);
		logger.info("Inserting testFeedback");
		SystemPreferences systemPreferences = systemManager.getSystemPreferences();
		long minMsec = systemPreferences.getRegistrationDate().getTime() + 1000;
		long maxMsec = systemPreferences.getDeadline().getTime() - 1000;
		Date randomDate = DateUtils.getRandomDateBetween(minMsec, maxMsec);
		reportingService.saveFeedback(randomFeedback, randomDate);
	}

	public static int getRandomInt(int min, int max) {
		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}

	public int insertTestSignatures(List<Country> countries, long signaturesSize) throws Exception {
		List<CountryProperty> allCountryProperties = signatureService.getAllCountryProperties();
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
		int signatureInsertedCounter = 1;
		for (Country country : countries) {
			int signaturesSizeTemp = getRandomInt(0, (int) signaturesSize);
			for (long i = 0; i < signaturesSizeTemp; i++) {
				logger.info("Inserting testSignature #" + signatureInsertedCounter + " for country[" + country.getCode()
						+ "] ");
				SignatureDTO signatureDTO = new SignatureDTO();
				signatureDTO.setCountry(country.getCode());
				SystemPreferences systemPreferences = systemManager.getSystemPreferences();
				long minMsec = systemPreferences.getRegistrationDate().getTime() + 1000;
				long maxMsec = systemPreferences.getDeadline().getTime() - 1000;
				Date randomDate = DateUtils.getRandomDateBetween(minMsec, maxMsec);
				assertTrue(DateUtils.isBetween(systemPreferences.getRegistrationDate(), systemPreferences.getDeadline(),
						randomDate));

				List<SupportFormDTO> propertyValues = new ArrayList<SupportFormDTO>();
				String countryCode = country.getCode();
				List<CountryProperty> requiredCountryProperties = countryPropertiesMap.get(countryCode);
				for (CountryProperty requiredCountryProperty : requiredCountryProperties) {
					SupportFormDTO supportFormDTO = new SupportFormDTO();
					supportFormDTO.setId(requiredCountryProperty.getId());
					supportFormDTO.setGroup(requiredCountryProperty.getProperty().getGroup().getId().intValue());
					supportFormDTO.setLabel(requiredCountryProperty.getProperty().getName());
					supportFormDTO.setValue(i + "_" + System.currentTimeMillis() + Math.random());
					propertyValues.add(supportFormDTO);
				}
				// check identity duplicate
				IdentityValue identityValue = null;
				try {
					identityValue = checkIdentityDuplicates(signatureDTO);
				} catch (OCTDuplicateSignatureException e) {
					throw new Exception("Can't insert signature: " + e.getMessage());
				} catch (OCTException e) {
					throw new Exception("Can't insert signature: " + e.getMessage());
				}
				try {
					Signature signature = signatureTransformer.transform(signatureDTO);
					String signatureUUID = signatureService.insertSignature(signature, randomDate);
					if (identityValue != null) {
						signatureService.storeIdentityValue(identityValue);
					}
					signatureInsertedCounter++;
					insertTestFeedback(signatureUUID);
				} catch (Exception e) {
					logger.error(e.getMessage());
					throw new Exception("Can't insert signature: " + e.getMessage());
				}
			}
		}
		return signatureInsertedCounter;
	}

	public static List<SupportFormDTO> getMockedProperties(List<CountryProperty> requiredCountryProperties) {
		List<SupportFormDTO> propertyValues = new ArrayList<>();
		int documentProperties = 0;
		for (CountryProperty requiredCountryProperty : requiredCountryProperties) {
			if (requiredCountryProperty.getProperty().getGroup().isMultichoice()) {
				if (documentProperties > 0) {
					continue;
				} else {
					documentProperties++;
				}
			}
			SupportFormDTO supportFormDTO = new SupportFormDTO();
			supportFormDTO.setId(requiredCountryProperty.getId());
			supportFormDTO.setGroup(requiredCountryProperty.getProperty().getGroup().getId().intValue());
			String propertyName = requiredCountryProperty.getProperty().getName()
					.replace(SupportFormDTO.OCT_PROPERTY_PREFIX, "");
			supportFormDTO.setLabel(propertyName);
			String randomValue = System.currentTimeMillis() + Math.random() + "";
			String value = randomValue.substring(randomValue.length() - 10);
			if (propertyName.equalsIgnoreCase(PropertyEnum.DATE_OF_BIRTH.getName())) {
				value = getRandomDateString();
			}
			if (propertyName.equalsIgnoreCase(PropertyEnum.RESIDENCE_POSTAL_CODE.getName())) {
				value = getRandomInt(1000, 9999) + "";
			}
			if (propertyName.equalsIgnoreCase(PropertyEnum.FULL_FIRST_NAMES.getName())
					|| propertyName.equalsIgnoreCase(PropertyEnum.FAMILY_NAMES.getName())
					|| propertyName.equalsIgnoreCase(PropertyEnum.RESIDENCE_CITY.getName())
					|| propertyName.equalsIgnoreCase(PropertyEnum.RESIDENCE_COUNTRY.getName())
					|| propertyName.equalsIgnoreCase(PropertyEnum.RESIDENCE_STREET.getName())) {
				value = getRandomName();
			}
			if (propertyName.equalsIgnoreCase(PropertyEnum.RESIDENCE_STREET_NUMBER.getName())) {
				value = "" + getRandomInt(1, 999);
				if (System.currentTimeMillis() % 2 == 0) {
					value += getRandomChar();
				}
			}
			supportFormDTO.setValue(value);
			propertyValues.add(supportFormDTO);
		}
		return propertyValues;
	}

	private static String getRandomName() {
		String randomName = ("" + getRandomChar()).toUpperCase();
		int size = getRandomInt(5, 12);
		for (int i = 0; i < size; i++) {
			randomName += ("" + getRandomChar()).toLowerCase();
		}
		return randomName;
	}

	private static String getRandomDateString() {
		String randomDay = "" + getRandomInt(10, 30);
		String randomMonth = "" + getRandomInt(10, 12);
		String randomYear = "" + getRandomInt(1940, 1999);
		return randomDay + "/" + randomMonth + "/" + randomYear;
	}

	public static char getRandomChar() {
		Random r = new Random();
		return (char) (r.nextInt(26) + 'a');
	}
}