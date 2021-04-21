package eu.europa.ec.eci.oct.webcommons.services.validation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europa.ec.eci.oct.entities.system.Country;
import eu.europa.ec.eci.oct.utils.DateUtils;
import eu.europa.ec.eci.oct.validation.ValidationBean;
import eu.europa.ec.eci.oct.validation.ValidationError;
import eu.europa.ec.eci.oct.validation.ValidationProperty;
import eu.europa.ec.eci.oct.validation.ValidationResult;
import eu.europa.ec.eci.oct.webcommons.services.api.test.MockApi;
import eu.europa.ec.eci.oct.webcommons.services.commons.ServicesTest;
import eu.europa.ec.eci.oct.webcommons.services.enums.PropertyEnum;
import eu.europa.ec.eci.oct.webcommons.services.persistence.PersistenceException;

/**
 * @author pariale all validated document data taken from
 *         https://webgate.ec.europa.eu/CITnet/confluence/display/OCS/2.+ECI-OCS+Test+Data
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/services-test.xml")
public class RuleServiceImplTest extends ServicesTest {

	protected ValidationBean genericValidationBean;
	private ValidationResult validationResult;

	LocalDate now = LocalDate.now();
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateUtils.DEFAULT_DATE_FORMAT);
	String RULES_COMMON_DRL_PATH = "/rules/common/common.drl";

	@Before
	public void setUp() {
		genericValidationBean = new ValidationBean();
	}

	public void initalizeRulesAndBean(String countryCode) throws IOException, PersistenceException {
		initalizeRulesAndBean(countryCode, false);
	}

	public void initalizeRulesAndBean(String countryCode, boolean isEidas)
			throws FileNotFoundException, IOException, PersistenceException {

		Country c = testCountryCodesMap.get(countryCode);
		genericValidationBean.setNationality(countryCode);

		// common properties
		genericValidationBean
				.addProperty(new ValidationProperty(PropertyEnum.FAMILY_NAMES.getName(), "testFamilyNames"));
		genericValidationBean
				.addProperty(new ValidationProperty(PropertyEnum.FULL_FIRST_NAMES.getName(), "testFullFirstNames"));
		if (testCountryCodesA.contains(countryCode)) {
			// group A common properties
			genericValidationBean
					.addProperty(new ValidationProperty(PropertyEnum.RESIDENCE_CITY.getName(), "testResidenceCity"));
			genericValidationBean
					.addProperty(new ValidationProperty(PropertyEnum.RESIDENCE_COUNTRY.getName(), countryCode));
			genericValidationBean.addProperty(
					new ValidationProperty(PropertyEnum.RESIDENCE_STREET.getName(), "testResidenceStreet"));
			genericValidationBean
					.addProperty(new ValidationProperty(PropertyEnum.RESIDENCE_STREET_NUMBER.getName(), "1234ABS"));
		}

		try (FileInputStream inputStream = new FileInputStream(new File(
				RuleServiceImpl.class.getResource("/rules/" + countryCode + "/" + countryCode + ".drl").getFile()))) {
			String drl = IOUtils.toString(inputStream);
			ruleService.updateRuleByCountry4Test(c, drl);
			inputStream.close();
		}

		Country cCommon = new Country();
		cCommon.setCode(COMMON_COUNTRY_CODE);
		try (FileInputStream inputStream = new FileInputStream(
				new File(RuleServiceImpl.class.getResource(RULES_COMMON_DRL_PATH).getFile()))) {
			String drl = IOUtils.toString(inputStream);
			ruleService.updateRuleByCountry4Test(cCommon, drl);
			inputStream.close();
		}

	}

	@Test
	public void postalCodeOutOfNationality() throws Exception {
		/*
		 * when residence country is different from nationality we skip the postal code
		 * validation
		 */
		String validBirthDate = now.minusYears(18).format(formatter);
		// country A (with postal code)
		for (String countryCodeA : testCountryCodesA) {
			initalizeRulesAndBean(countryCodeA);
			genericValidationBean
					.addOrSetProperty(new ValidationProperty(PropertyEnum.DATE_OF_BIRTH.getName(), validBirthDate));
			genericValidationBean.addOrSetProperty(
					new ValidationProperty(PropertyEnum.RESIDENCE_POSTAL_CODE.getName(), INVALID_POSTAL_CODE));
			for (String countryCodeB : testCountryCodesB) {
				genericValidationBean
						.addProperty(new ValidationProperty(PropertyEnum.RESIDENCE_COUNTRY.getName(), countryCodeB));
				validationResult = ruleService.validate(genericValidationBean);
				printErrorFields(validationResult.getValidationErrors());
				assertEquals(0, validationResult.getValidationErrors().size());
			}
		}
	}

	@Test
	public void rules_AT() throws Exception {
		// Get the previously instantiated bean for the country
		initalizeRulesAndBean(AT);

		// valid data
		String validDateOfBirth = now.minusYears(16).format(formatter);
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.ID_CARD.getName(), MockApi.AT_VALID_ID_CARD));
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.PASSPORT.getName(), MockApi.AT_VALID_PASSPORT));
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.DATE_OF_BIRTH.getName(), validDateOfBirth));
		ValidationResult validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());

		// invalid ID card
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.PASSPORT.getName(), MockApi.AT_VALID_PASSPORT));
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateUtils.DEFAULT_DATE_FORMAT);
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.DATE_OF_BIRTH.getName(), validDateOfBirth));
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.ID_CARD.getName(), INVALID_DOCUMENT));
		validationResult = ruleService.validate(genericValidationBean);

		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.ID_CARD.getName(), validationResult.getValidationErrors().get(0).getKey());

		// invalid passport
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.ID_CARD.getName(), MockApi.AT_VALID_ID_CARD));
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.PASSPORT.getName(), INVALID_DOCUMENT));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.PASSPORT.getName(), validationResult.getValidationErrors().get(0).getKey());

		// invalid age
		String invalidDateOfBirth = now.minusYears(15).format(formatter);
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.PASSPORT.getName(), MockApi.AT_VALID_PASSPORT));
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.DATE_OF_BIRTH.getName(), invalidDateOfBirth));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.DATE_OF_BIRTH.getName(), validationResult.getValidationErrors().get(0).getKey());
	}

	@Test
	public void rules_PL() throws Exception {
		initalizeRulesAndBean(PL);

		// invalid document
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.NATIONAL_ID_NUMBER.getName(), INVALID_DOCUMENT));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.NATIONAL_ID_NUMBER.getName(), validationResult.getValidationErrors().get(0).getKey());

		// valid data
		genericValidationBean.addOrSetProperty(
				new ValidationProperty(PropertyEnum.NATIONAL_ID_NUMBER.getName(), MockApi.PL_VALID_NATIONAL_ID_NO));
		ValidationResult validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());
	}

	@Test
	public void rules_DE() throws Exception {
		initalizeRulesAndBean(DE);

		// invalid minimum age
		String invalidBirthDate = now.minusYears(17).format(formatter);
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.DATE_OF_BIRTH.getName(), invalidBirthDate));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.DATE_OF_BIRTH.getName(), validationResult.getValidationErrors().get(0).getKey());

		// restore date of birth
		String validBirthDate = now.minusYears(18).format(formatter);
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.DATE_OF_BIRTH.getName(), validBirthDate));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());

		// invalid postal code
		genericValidationBean.addOrSetProperty(
				new ValidationProperty(PropertyEnum.RESIDENCE_POSTAL_CODE.getName(), INVALID_POSTAL_CODE));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.RESIDENCE_POSTAL_CODE.getName(),
				validationResult.getValidationErrors().get(0).getKey());

		// restore valid postal code
		genericValidationBean.addOrSetProperty(
				new ValidationProperty(PropertyEnum.RESIDENCE_POSTAL_CODE.getName(), MockApi.DE_VALID_POSTAL_CODE));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());
	}

	@Test
	public void rules_FR() throws Exception {

		initalizeRulesAndBean(FR);

		// invalid postal.code
		genericValidationBean.addOrSetProperty(
				new ValidationProperty(PropertyEnum.RESIDENCE_POSTAL_CODE.getName(), INVALID_POSTAL_CODE));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.RESIDENCE_POSTAL_CODE.getName(),
				validationResult.getValidationErrors().get(0).getKey());

		// valid postal.code
		genericValidationBean.addOrSetProperty(
				new ValidationProperty(PropertyEnum.RESIDENCE_POSTAL_CODE.getName(), MockApi.FR_VALID_POSTAL_CODE));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());
	}

	@Test
	public void rules_BE() throws Exception {

		initalizeRulesAndBean(BE);

		// invalid national.id.number
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.NATIONAL_ID_NUMBER.getName(), INVALID_DOCUMENT));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.NATIONAL_ID_NUMBER.getName(), validationResult.getValidationErrors().get(0).getKey());

		// valid national.id.number
		genericValidationBean.addOrSetProperty(new ValidationProperty(PropertyEnum.NATIONAL_ID_NUMBER.getName(),
				MockApi.BE_VALID_NATIONAL_REGISTER_NUMBER));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());
	}

	@Test
	public void rules_RO() throws Exception {

		initalizeRulesAndBean(RO);

		// valid data
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.ID_CARD.getName(), MockApi.RO_VALID_ID_CARD));
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.PASSPORT.getName(), MockApi.RO_VALID_PASSPORT1));
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.PASSPORT.getName(), MockApi.RO_VALID_PASSPORT2));
		genericValidationBean.addOrSetProperty(
				new ValidationProperty(PropertyEnum.PERSONAL_ID.getName(), MockApi.RO_VALID_PERSONAL_ID));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());

		// invalid id card
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.ID_CARD.getName(), INVALID_DOCUMENT));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.ID_CARD.getName(), validationResult.getValidationErrors().get(0).getKey());
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.ID_CARD.getName(), MockApi.RO_VALID_ID_CARD));

		// invalid personal id
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.PERSONAL_ID.getName(), INVALID_DOCUMENT));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.PERSONAL_ID.getName(), validationResult.getValidationErrors().get(0).getKey());
		genericValidationBean.addOrSetProperty(
				new ValidationProperty(PropertyEnum.PERSONAL_ID.getName(), MockApi.RO_VALID_PERSONAL_ID));

		// invalid passport
		genericValidationBean.addOrSetProperty(new ValidationProperty(PropertyEnum.PASSPORT.getName(), "!)(/&%"));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.PASSPORT.getName(), validationResult.getValidationErrors().get(0).getKey());
	}

	@Test
	public void rules_LV() throws Exception {

		initalizeRulesAndBean(LV);

		// invalid personal id
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.PERSONAL_ID.getName(), INVALID_DOCUMENT));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.PERSONAL_ID.getName(), validationResult.getValidationErrors().get(0).getKey());

		// valid personal id
		genericValidationBean.addOrSetProperty(
				new ValidationProperty(PropertyEnum.PERSONAL_ID.getName(), MockApi.LV_VALID_PERSONAL_ID));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());
	}

	@Test
	public void rules_BG() throws Exception {
		initalizeRulesAndBean(BG);

		// valid date
		genericValidationBean.addOrSetProperty(
				new ValidationProperty(PropertyEnum.PERSONAL_NUMBER.getName(), MockApi.BG_VALID_PERSONAL_NUMBER));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());

		// invalid persona.number
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.PERSONAL_NUMBER.getName(), INVALID_DOCUMENT));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.PERSONAL_NUMBER.getName(), validationResult.getValidationErrors().get(0).getKey());
	}

	@Test
	public void rules_CY() throws Exception {

		initalizeRulesAndBean(CY);

		// invalid passport
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.PASSPORT.getName(), INVALID_DOCUMENT));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.PASSPORT.getName(), validationResult.getValidationErrors().get(0).getKey());

		// valid passports
		List<String> validPassports = new ArrayList<>();
		validPassports.add(MockApi.CY_VALID_PASSPORT1);
		validPassports.add(MockApi.CY_VALID_PASSPORT2);
		validPassports.add(MockApi.CY_VALID_PASSPORT3);
		validPassports.add(MockApi.CY_VALID_PASSPORT4);
		validPassports.add(MockApi.CY_VALID_PASSPORT5);
		validPassports.add(MockApi.CY_VALID_PASSPORT6);
		validPassports.add(MockApi.CY_VALID_PASSPORT7);
		for (String validPassport : validPassports) {
			genericValidationBean
					.addOrSetProperty(new ValidationProperty(PropertyEnum.PASSPORT.getName(), validPassport));
			ValidationResult validationResult = ruleService.validate(genericValidationBean);
			printErrorFields(validationResult.getValidationErrors());
			assertEquals(0, validationResult.getValidationErrors().size());
		}

		// valid id card
		List<String> validIdCardsNumbers = new ArrayList<String>();
		String validIdCard = "";
		for (int i = 0; i < 10; i++) {
			validIdCard += i;
			validIdCardsNumbers.add(validIdCard);
		}
		assertEquals(10, validIdCardsNumbers.size());

		for (String validIdCardNumber : validIdCardsNumbers) {
			genericValidationBean
					.addOrSetProperty(new ValidationProperty(PropertyEnum.ID_CARD.getName(), validIdCardNumber));
			ValidationResult validationResult = ruleService.validate(genericValidationBean);
			assertEquals(0, validationResult.getValidationErrors().size());
		}

		// invalid id card
		String invalidCardNumber = validIdCard + "0";
		assertTrue(invalidCardNumber.length() > 10);
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.ID_CARD.getName(), invalidCardNumber));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.ID_CARD.getName(), validationResult.getValidationErrors().get(0).getKey());

	}

	@Test
	public void rules_LT() throws Exception {

		initalizeRulesAndBean(LT);

		// valid data
		genericValidationBean.addOrSetProperty(
				new ValidationProperty(PropertyEnum.PERSONAL_NUMBER.getName(), MockApi.LT_VALID_PERSONAL_NUMBER));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());

		// invalid data
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.PERSONAL_NUMBER.getName(), INVALID_DOCUMENT));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.PERSONAL_NUMBER.getName(), validationResult.getValidationErrors().get(0).getKey());
	}

	@Test
	public void rules_LU() throws Exception {

		initalizeRulesAndBean(LU);

		// invalid postal.code
		genericValidationBean.addOrSetProperty(
				new ValidationProperty(PropertyEnum.RESIDENCE_POSTAL_CODE.getName(), INVALID_POSTAL_CODE));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.RESIDENCE_POSTAL_CODE.getName(),
				validationResult.getValidationErrors().get(0).getKey());

		// valid postal.code
		genericValidationBean.addOrSetProperty(
				new ValidationProperty(PropertyEnum.RESIDENCE_POSTAL_CODE.getName(), MockApi.LU_VALID_POSTAL_CODE));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());

		// invalid age
		String invalidBirthDate = now.minusYears(15).format(formatter);
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.DATE_OF_BIRTH.getName(), invalidBirthDate));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.DATE_OF_BIRTH.getName(), validationResult.getValidationErrors().get(0).getKey());

		// valid age
		String validBirthDate = now.minusYears(16).format(formatter);
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.DATE_OF_BIRTH.getName(), validBirthDate));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());
	}

	@Test
	public void rules_MT() throws Exception {

		initalizeRulesAndBean(MT);

		// invalid id.card
		genericValidationBean.addOrSetProperty(new ValidationProperty(PropertyEnum.ID_CARD.getName(), "!)(/&%\"*"));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.ID_CARD.getName(), validationResult.getValidationErrors().get(0).getKey());

		// valid id.card
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.ID_CARD.getName(), MockApi.MT_VALID_ID_CARD));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());
	}

	@Test
	public void rules_NL() throws Exception {

		initalizeRulesAndBean(NL);

		// invalid postal.code
		genericValidationBean.addOrSetProperty(
				new ValidationProperty(PropertyEnum.RESIDENCE_POSTAL_CODE.getName(), INVALID_POSTAL_CODE));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.RESIDENCE_POSTAL_CODE.getName(),
				validationResult.getValidationErrors().get(0).getKey());

		// valid postal.code
		genericValidationBean.addOrSetProperty(
				new ValidationProperty(PropertyEnum.RESIDENCE_POSTAL_CODE.getName(), MockApi.NL_VALID_POSTAL_CODE));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());

		// invalid age
		String invalidBirthDate = now.minusYears(17).format(formatter);
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.DATE_OF_BIRTH.getName(), invalidBirthDate));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.DATE_OF_BIRTH.getName(), validationResult.getValidationErrors().get(0).getKey());

		// valid age
		String validBirthDate = now.minusYears(18).format(formatter);
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.DATE_OF_BIRTH.getName(), validBirthDate));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());

	}

	@Test
	public void rules_PT() throws Exception {

		initalizeRulesAndBean(PT);

		// invalid passport
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.PASSPORT.getName(), INVALID_DOCUMENT));
		validationResult = ruleService.validate(genericValidationBean);
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(1, validationResult.getValidationErrors().size());
		assertEquals(PropertyEnum.PASSPORT.getName(), validationResult.getValidationErrors().get(0).getKey());

		// valid passport1
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.PASSPORT.getName(), MockApi.PT_VALID_PASSPORT1));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());
		// valid passport2
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.PASSPORT.getName(), MockApi.PT_VALID_PASSPORT2));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());

		// invalid citizens.card
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.CITIZEN_CARD.getName(), INVALID_DOCUMENT));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.CITIZEN_CARD.getName(), validationResult.getValidationErrors().get(0).getKey());

		// valid citizens.card
		genericValidationBean.addOrSetProperty(
				new ValidationProperty(PropertyEnum.CITIZEN_CARD.getName(), MockApi.PT_VALID_CITIZENS_CARD));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());

		// invalid id.card
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.ID_CARD.getName(), INVALID_DOCUMENT));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.ID_CARD.getName(), validationResult.getValidationErrors().get(0).getKey());

		// valid card
		String validCard = "";
		for (int i = 0; i < 8; i++) {
			validCard += i;
			genericValidationBean.addOrSetProperty(new ValidationProperty(PropertyEnum.ID_CARD.getName(), validCard));
			validationResult = ruleService.validate(genericValidationBean);
			assertEquals(0, validationResult.getValidationErrors().size());
		}

	}

	@Test
	public void rules_SK() throws Exception {

		initalizeRulesAndBean(SK);

		// invalid postal code
		genericValidationBean.addOrSetProperty(
				new ValidationProperty(PropertyEnum.RESIDENCE_POSTAL_CODE.getName(), INVALID_POSTAL_CODE));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.RESIDENCE_POSTAL_CODE.getName(),
				validationResult.getValidationErrors().get(0).getKey());

		// valid postal code 1
		genericValidationBean.addOrSetProperty(
				new ValidationProperty(PropertyEnum.RESIDENCE_POSTAL_CODE.getName(), MockApi.SK_VALID_POSTAL_CODE));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());
		// valid postal code 2
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.RESIDENCE_POSTAL_CODE.getName(), "81234"));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());
		// valid postal code 3
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.RESIDENCE_POSTAL_CODE.getName(), "91234"));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());

		// invalid age
		String invalidBirthDate = now.minusYears(17).format(formatter);
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.DATE_OF_BIRTH.getName(), invalidBirthDate));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.DATE_OF_BIRTH.getName(), validationResult.getValidationErrors().get(0).getKey());

		// valid age
		String validBirthDate = now.minusYears(18).format(formatter);
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.DATE_OF_BIRTH.getName(), validBirthDate));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());

	}

	@Test
	public void rules_SI() throws Exception {

		initalizeRulesAndBean(SI);

		// invalid personal.number
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.PERSONAL_NUMBER.getName(), INVALID_DOCUMENT));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.PERSONAL_NUMBER.getName(), validationResult.getValidationErrors().get(0).getKey());

		// valid personal number
		genericValidationBean.addOrSetProperty(
				new ValidationProperty(PropertyEnum.PERSONAL_NUMBER.getName(), MockApi.SI_VALID_PERSONAL_NUMBER));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());

	}

	@Test
	public void rules_CZ() throws Exception {

		initalizeRulesAndBean(CZ);

		// invalid passport
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.PASSPORT.getName(), INVALID_DOCUMENT));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.PASSPORT.getName(), validationResult.getValidationErrors().get(0).getKey());

		// valid passport1
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.PASSPORT.getName(), MockApi.CZ_VALID_PASSPORT_1));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());
		// valid passport2
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.PASSPORT.getName(), MockApi.CZ_VALID_PASSPORT_2));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());

		// invalid id.card
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.ID_CARD.getName(), INVALID_DOCUMENT));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.ID_CARD.getName(), validationResult.getValidationErrors().get(0).getKey());

		// valid id.card1
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.ID_CARD.getName(), MockApi.CZ_VALID_ID_CARD_1));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());
		// valid id.card2
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.ID_CARD.getName(), MockApi.CZ_VALID_ID_CARD_2));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());
		// valid id.card3
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.ID_CARD.getName(), MockApi.CZ_VALID_ID_CARD_3));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());
		// valid id.card4
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.ID_CARD.getName(), MockApi.CZ_VALID_ID_CARD_4));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());
	}

	@Test
	public void rules_DK() throws Exception {

		initalizeRulesAndBean(DK);

		// invalid postal code
		genericValidationBean.addOrSetProperty(
				new ValidationProperty(PropertyEnum.RESIDENCE_POSTAL_CODE.getName(), INVALID_POSTAL_CODE));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.RESIDENCE_POSTAL_CODE.getName(),
				validationResult.getValidationErrors().get(0).getKey());

		// valid postal code
		genericValidationBean.addOrSetProperty(
				new ValidationProperty(PropertyEnum.RESIDENCE_POSTAL_CODE.getName(), MockApi.DK_VALID_POSTAL_CODE));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());

		// invalid age
		String invalidBirthDate = now.minusYears(17).format(formatter);
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.DATE_OF_BIRTH.getName(), invalidBirthDate));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.DATE_OF_BIRTH.getName(), validationResult.getValidationErrors().get(0).getKey());

		// valid age
		String validBirthDate = now.minusYears(18).format(formatter);
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.DATE_OF_BIRTH.getName(), validBirthDate));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());
	}

	@Test
	public void rules_EE() throws Exception {

		initalizeRulesAndBean(EE);

		// invalid personal.id
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.PERSONAL_NUMBER.getName(), INVALID_DOCUMENT));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.PERSONAL_NUMBER.getName(), validationResult.getValidationErrors().get(0).getKey());

		genericValidationBean.addOrSetProperty(
				new ValidationProperty(PropertyEnum.PERSONAL_NUMBER.getName(), MockApi.EE_VALID_PERSONAL_NUMBER));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());

		// invalid age
		String invalidDateOfBirth = now.minusYears(15).format(formatter);
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.DATE_OF_BIRTH.getName(), invalidDateOfBirth));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.DATE_OF_BIRTH.getName(), validationResult.getValidationErrors().get(0).getKey());

		// restore date of birth
		String validBirthDate = now.minusYears(16).format(formatter);
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.DATE_OF_BIRTH.getName(), validBirthDate));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());

	}

	@Test
	public void rules_FI() throws Exception {

		initalizeRulesAndBean(FI);

		// invalid postal.code
		genericValidationBean.addOrSetProperty(
				new ValidationProperty(PropertyEnum.RESIDENCE_POSTAL_CODE.getName(), INVALID_POSTAL_CODE));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());

		genericValidationBean.addOrSetProperty(
				new ValidationProperty(PropertyEnum.RESIDENCE_POSTAL_CODE.getName(), MockApi.FI_VALID_POSTAL_CODE));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());

		// invalid age
		String invalidBirthDate = now.minusYears(17).format(formatter);
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.DATE_OF_BIRTH.getName(), invalidBirthDate));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.DATE_OF_BIRTH.getName(), validationResult.getValidationErrors().get(0).getKey());

		// valid age
		String validBirthDate = now.minusYears(18).format(formatter);
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.DATE_OF_BIRTH.getName(), validBirthDate));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());
	}

	@Test
	public void rules_GR() throws Exception {

		initalizeRulesAndBean(GR);

		genericValidationBean.addOrSetProperty(
				new ValidationProperty(PropertyEnum.RESIDENCE_POSTAL_CODE.getName(), INVALID_POSTAL_CODE));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.RESIDENCE_POSTAL_CODE.getName(),
				validationResult.getValidationErrors().get(0).getKey());

		genericValidationBean.addOrSetProperty(
				new ValidationProperty(PropertyEnum.RESIDENCE_POSTAL_CODE.getName(), MockApi.GR_VALID_POSTAL_CODE));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());

		// invalid age
		String invalidBirthDate = now.minusYears(16).format(formatter);
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.DATE_OF_BIRTH.getName(), invalidBirthDate));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.DATE_OF_BIRTH.getName(), validationResult.getValidationErrors().get(0).getKey());

		// valid age
		String validBirthDate = now.minusYears(17).format(formatter);
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.DATE_OF_BIRTH.getName(), validBirthDate));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());
	}

	@Test
	public void rules_ES() throws Exception {

		initalizeRulesAndBean(ES);

		// invalid id.card
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.ID_CARD.getName(), INVALID_DOCUMENT));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.ID_CARD.getName(), validationResult.getValidationErrors().get(0).getKey());

		// valid id.card
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.ID_CARD.getName(), MockApi.ES_VALID_ID_CARD));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());

		// invalid passport
		genericValidationBean.addOrSetProperty(new ValidationProperty(PropertyEnum.PASSPORT.getName(), "!£$%&"));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.PASSPORT.getName(), validationResult.getValidationErrors().get(0).getKey());

		// valid passport
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.PASSPORT.getName(), MockApi.ES_VALID_PASSPORT));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());
	}

	@Test
	public void rules_HU() throws Exception {

		initalizeRulesAndBean(HU);

		// invalid id.card
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.ID_CARD.getName(), INVALID_DOCUMENT));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.ID_CARD.getName(), validationResult.getValidationErrors().get(0).getKey());

		// valid id.card1
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.ID_CARD.getName(), MockApi.HU_VALID_ID_CARD1));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());
		// valid id.card2
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.ID_CARD.getName(), MockApi.HU_VALID_ID_CARD2));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());
		// valid id.card3
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.ID_CARD.getName(), MockApi.HU_VALID_ID_CARD3));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());
		// valid id.card4
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.ID_CARD.getName(), MockApi.HU_VALID_ID_CARD4));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());

		// invalid passport
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.PASSPORT.getName(), INVALID_DOCUMENT));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.PASSPORT.getName(), validationResult.getValidationErrors().get(0).getKey());

		// valid passport1
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.PASSPORT.getName(), MockApi.HU_VALID_PASSPORT1));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());
		// valid passport2
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.PASSPORT.getName(), MockApi.HU_VALID_PASSPORT2));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());

		// invalid personal.number
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.PERSONAL_NUMBER.getName(), INVALID_DOCUMENT));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.PERSONAL_NUMBER.getName(), validationResult.getValidationErrors().get(0).getKey());

		// valid personal.number
		genericValidationBean.addOrSetProperty(
				new ValidationProperty(PropertyEnum.PERSONAL_NUMBER.getName(), MockApi.HU_VALID_PERSONAL_NUMBER));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());

	}

	@Test
	public void rules_IE() throws Exception {

		initalizeRulesAndBean(IE);

		// invalid postal code: empty value
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.RESIDENCE_POSTAL_CODE.getName(), ""));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.RESIDENCE_POSTAL_CODE.getName(),
				validationResult.getValidationErrors().get(0).getKey());

		// valid postal code
		genericValidationBean.addOrSetProperty(
				new ValidationProperty(PropertyEnum.RESIDENCE_POSTAL_CODE.getName(), MockApi.IE_VALID_POSTAL_CODE));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());

		// invalid age
		String invalidBirthDate = now.minusYears(17).format(formatter);
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.DATE_OF_BIRTH.getName(), invalidBirthDate));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.DATE_OF_BIRTH.getName(), validationResult.getValidationErrors().get(0).getKey());

		// valid age
		String validBirthDate = now.minusYears(18).format(formatter);
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.DATE_OF_BIRTH.getName(), validBirthDate));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());
	}

	@Test
	public void rules_SE() throws Exception {

		initalizeRulesAndBean(SE);

		// invalid personal.number
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.PERSONAL_NUMBER.getName(), INVALID_DOCUMENT));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.PERSONAL_NUMBER.getName(), validationResult.getValidationErrors().get(0).getKey());

		// valid personal.number
		genericValidationBean.addOrSetProperty(
				new ValidationProperty(PropertyEnum.PERSONAL_NUMBER.getName(), MockApi.SE_VALID_PERSONAL_NUMBER1));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());
		// valid personal.number2
		genericValidationBean.addOrSetProperty(
				new ValidationProperty(PropertyEnum.PERSONAL_NUMBER.getName(), MockApi.SE_VALID_PERSONAL_NUMBER2));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());
		// valid personal.number3
		genericValidationBean.addOrSetProperty(
				new ValidationProperty(PropertyEnum.PERSONAL_NUMBER.getName(), MockApi.SE_VALID_PERSONAL_NUMBER3));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());
		// valid personal.number4
		genericValidationBean.addOrSetProperty(
				new ValidationProperty(PropertyEnum.PERSONAL_NUMBER.getName(), MockApi.SE_VALID_PERSONAL_NUMBER4));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());

	}

	@Test
	public void rules_IT() throws Exception {

		initalizeRulesAndBean(IT);

		// invalid id.card
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.ID_CARD.getName(), INVALID_DOCUMENT));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.ID_CARD.getName(), validationResult.getValidationErrors().get(0).getKey());

		// valid id.card1
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.ID_CARD.getName(), MockApi.IT_VALID_ID_CARD1));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());
		// valid id.card2
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.ID_CARD.getName(), MockApi.IT_VALID_ID_CARD2));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());
		// valid id.card3
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.ID_CARD.getName(), MockApi.IT_VALID_ID_CARD3));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());
		// valid id.card4
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.ID_CARD.getName(), MockApi.IT_VALID_ID_CARD4));
		validationResult = ruleService.validate(genericValidationBean);
		// valid id.card5
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.ID_CARD.getName(), MockApi.IT_VALID_ID_CARD5));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());

		// invalid passport
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.PASSPORT.getName(), INVALID_DOCUMENT));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.PASSPORT.getName(), validationResult.getValidationErrors().get(0).getKey());

		// valid passport1
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.PASSPORT.getName(), MockApi.IT_VALID_PASSPORT1));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());
		// valid passport2
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.PASSPORT.getName(), MockApi.IT_VALID_PASSPORT2));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());
		// valid passport3
		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.PASSPORT.getName(), MockApi.IT_VALID_PASSPORT3));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());

	}

	@Test
	public void rules_HR() throws Exception {

		initalizeRulesAndBean(HR);

		genericValidationBean
				.addOrSetProperty(new ValidationProperty(PropertyEnum.PERSONAL_ID.getName(), INVALID_DOCUMENT));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(1, validationResult.getValidationErrors().size());
		printErrorFields(validationResult.getValidationErrors());
		assertEquals(PropertyEnum.PERSONAL_ID.getName(), validationResult.getValidationErrors().get(0).getKey());

		// valid personal.id
		genericValidationBean.addOrSetProperty(
				new ValidationProperty(PropertyEnum.PERSONAL_ID.getName(), MockApi.HR_VALID_PERSONAL_ID));
		validationResult = ruleService.validate(genericValidationBean);
		assertEquals(0, validationResult.getValidationErrors().size());

	}

	public void printErrorFields(List<ValidationError> errors) {
		for (ValidationError e : errors) {
			System.err.println(e);
		}
	}
}
