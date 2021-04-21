package eu.europa.ec.eci.oct.webcommons.restApi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europa.ec.eci.oct.entities.system.Country;
import eu.europa.ec.eci.oct.webcommons.commons.TestUtils;
import eu.europa.ec.eci.oct.webcommons.services.api.RestApiParent;
import eu.europa.ec.eci.oct.webcommons.services.api.SignatureApi;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.ApiResponse;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.captcha.CaptchaDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.captcha.CaptchaValidationDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.commons.StringDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.signature.SignatureCaptchaDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.signature.SignatureDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.signature.SignatureValidation;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.signature.SignaturesMetadata;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.supportForm.SupportFormDTO;
import eu.europa.ec.eci.oct.webcommons.services.captcha.CaptchaService;
import eu.europa.ec.eci.oct.webcommons.services.enums.PropertyEnum;
import eu.europa.ec.eci.oct.webcommons.services.enums.PropertyGroupEnum;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/rest-api-test.xml")
public class SignatureApiTest extends RestApiTest {

	@Test
	public void sig03() throws Exception {

		List<String> signatureForTestUUIDs = new ArrayList<String>();
		// less than 5 signatures
		String randomCountryCode = getRandomCountryCode();
		SignatureDTO signatureDTO1 = TestUtils.buildTestSignature(randomCountryCode, isCategoryA(randomCountryCode));
		signatureDTO1.setOptionalValidation(true);
		signatureForTestUUIDs.add(insertSignature(signatureDTO1, false));
		SignatureDTO signatureDTO2 = TestUtils.buildTestSignature(randomCountryCode, isCategoryA(randomCountryCode));
		signatureDTO2.setOptionalValidation(true);
		signatureForTestUUIDs.add(insertSignature(signatureDTO2, false));

		Response response = target(SIGNATURE + "/lastSignatures").request()
				.header(HttpHeaders.AUTHORIZATION, BEARER + authorizationToken).get(Response.class);
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		SignaturesMetadata signaturesMetadata = response.readEntity(SignaturesMetadata.class);
		assertTrue(signaturesMetadata.getMetadatas().size() == EXPECTED_MOCKED_SIGNATURES_SIZE
				+ signatureForTestUUIDs.size());

		// more than 5 signatures, should return 5 aswell
		SignatureDTO signatureDTO3 = TestUtils.buildTestSignature(randomCountryCode, isCategoryA(randomCountryCode));
		signatureDTO3.setOptionalValidation(true);
		signatureForTestUUIDs.add(insertSignature(signatureDTO3, false));
		SignatureDTO signatureDTO4 = TestUtils.buildTestSignature(randomCountryCode, isCategoryA(randomCountryCode));
		signatureDTO4.setOptionalValidation(true);
		signatureForTestUUIDs.add(insertSignature(signatureDTO4, false));

		response = target(SIGNATURE + "/lastSignatures").request()
				.header(HttpHeaders.AUTHORIZATION, BEARER + authorizationToken).get(Response.class);
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		signaturesMetadata = response.readEntity(SignaturesMetadata.class);
		assertTrue(signaturesMetadata.getMetadatas().size() == 5);

		removeNewSignatures();
	}

	private void deleteTestSignature(String testSignatureUUID) throws Exception {
		Response response = target(SIGNATURE + "/delete").request()
				.header(HttpHeaders.AUTHORIZATION, BEARER + authorizationToken)
				.post(Entity.entity(new StringDTO(testSignatureUUID), MediaType.APPLICATION_JSON));
		ApiResponse apiResponse = response.readEntity(ApiResponse.class);
		assertEquals(Status.OK.getStatusCode(), apiResponse.getCode());
		assertEquals(ApiResponse.SUCCESS, apiResponse.getStatus());
	}

	// SIG04
	@Test
	public void sig04() throws Exception {
		String randomCountryCode = getRandomCountryCode();
		SignatureDTO testSignatureToInsert = TestUtils.buildTestSignature(randomCountryCode,
				isCategoryA(randomCountryCode));

		/*
		 * bad input test - only null/empty value check. the validator test will care
		 * about the semantic content of the properties
		 */

		// COUNTRY ATTRIBUTE
		String originalCountry = testSignatureToInsert.getCountry();
		// null country
		testSignatureToInsert.setCountry(null);
		String expectationFailedReturn = insertSignature(testSignatureToInsert, true,
				Status.EXPECTATION_FAILED.getStatusCode());
		assertEquals(Status.EXPECTATION_FAILED.getStatusCode() + "", expectationFailedReturn);
		// null country ok

		// empty country
		testSignatureToInsert.setCountry("");
		expectationFailedReturn = insertSignature(testSignatureToInsert, true,
				Status.EXPECTATION_FAILED.getStatusCode());
		assertEquals(Status.EXPECTATION_FAILED.getStatusCode() + "", expectationFailedReturn);
		// empty country ok

		// not exixting country
		testSignatureToInsert.setCountry("XX");
		expectationFailedReturn = insertSignature(testSignatureToInsert, true,
				Status.EXPECTATION_FAILED.getStatusCode());
		assertEquals(Status.EXPECTATION_FAILED.getStatusCode() + "", expectationFailedReturn);
		// not exixting country ok
		// country attribute error management: ok
		testSignatureToInsert.setCountry(originalCountry);

		// PROPERTIES ATTRIBUTE

		// null and empty values property
		for (SupportFormDTO supportFormDTO : testSignatureToInsert.getProperties()) {
			String originalValue = supportFormDTO.getValue();

			// null value
			supportFormDTO.setValue(null);
			expectationFailedReturn = insertSignature(testSignatureToInsert, true,
					Status.EXPECTATION_FAILED.getStatusCode());
			assertEquals(Status.EXPECTATION_FAILED.getStatusCode() + "", expectationFailedReturn);

			// empty value
			supportFormDTO.setValue("");
			expectationFailedReturn = insertSignature(testSignatureToInsert, true,
					Status.EXPECTATION_FAILED.getStatusCode());
			assertEquals(Status.EXPECTATION_FAILED.getStatusCode() + "", expectationFailedReturn);

			supportFormDTO.setValue(originalValue);
		}

		String insertedSignatureUUID = insertSignature(testSignatureToInsert, false);
		assertNotNull(insertedSignatureUUID);
		assertFalse(StringUtils.isBlank(insertedSignatureUUID));

		deleteTestSignature(insertedSignatureUUID);
	}

	// custom test api for inserting sos
	@Test
	public void customTest() throws Exception {
		SignatureDTO signatureDTO = new SignatureDTO();

		signatureDTO.setCountry("be");

		List<SupportFormDTO> properties = new ArrayList<>();

		SupportFormDTO fullFirstNames = new SupportFormDTO();
		fullFirstNames.setGroup((int) PropertyGroupEnum.GENERAL.getId());
		fullFirstNames.setId(1);
		fullFirstNames.setLabel(PropertyEnum.FULL_FIRST_NAMES.getName());
		fullFirstNames.setValue("abc");
		properties.add(fullFirstNames);

		SupportFormDTO familyNames = new SupportFormDTO();
		familyNames.setGroup((int) PropertyGroupEnum.GENERAL.getId());
		familyNames.setId(2);
		familyNames.setLabel(PropertyEnum.FAMILY_NAMES.getName());
		familyNames.setValue("def");
		properties.add(familyNames);

		SupportFormDTO dateOfBirth = new SupportFormDTO();
		dateOfBirth.setGroup((int) PropertyGroupEnum.GENERAL.getId());
		dateOfBirth.setId(150);
		dateOfBirth.setLabel(PropertyEnum.DATE_OF_BIRTH.getName());
		dateOfBirth.setValue("01/01/1970");
		properties.add(dateOfBirth);

		SupportFormDTO nationalIdNumber = new SupportFormDTO();
		nationalIdNumber.setGroup((int) PropertyGroupEnum.ID.getId());
		nationalIdNumber.setId(3);
		nationalIdNumber.setLabel(PropertyEnum.NATIONAL_ID_NUMBER.getName());
		nationalIdNumber.setValue("85.09.07-123.45");
		properties.add(nationalIdNumber);

		signatureDTO.setProperties(properties);

		Response validationResponse = signatureService.validateSignature(signatureDTO, new SignatureValidation());
		if (validationResponse != null) {
			SignatureValidation signatureValidation = validationResponse.readEntity(SignatureValidation.class);
			assertTrue(signatureValidation.getErrorFields().isEmpty());
		}
	}

	@Test
	public void sig05() throws Exception {
		// delete a specific signature
		String randomCountryCode = getRandomCountryCode();
		SignatureDTO signatureToDelete = TestUtils.buildTestSignature(randomCountryCode,
				isCategoryA(randomCountryCode));
		String signatureToDeleteUUID = insertSignature(signatureToDelete, false);
		StringDTO stringDTO = new StringDTO();
		stringDTO.setValue("");

		// empty value
		Response response = target(SIGNATURE + "/delete").request()
				.header(HttpHeaders.AUTHORIZATION, BEARER + authorizationToken)
				.post(Entity.entity(stringDTO, MediaType.APPLICATION_JSON));
		assertEquals(Status.EXPECTATION_FAILED.getStatusCode(), response.getStatus());
		ApiResponse apiResponse = response.readEntity(ApiResponse.class);
		assertEquals(Status.EXPECTATION_FAILED.getStatusCode(), apiResponse.getCode());
		assertEquals(ApiResponse.ERROR, apiResponse.getStatus());
		assertEquals(SignatureApi.SIG05 + RestApiParent.INPUT_PARAMS_EXPECTATION_FAILED, apiResponse.getMessage());

		// unexisting uuid
		stringDTO.setValue("XXX");
		response = target(SIGNATURE + "/delete").request()
				.header(HttpHeaders.AUTHORIZATION, BEARER + authorizationToken)
				.post(Entity.entity(stringDTO, MediaType.APPLICATION_JSON));
		assertEquals(Status.EXPECTATION_FAILED.getStatusCode(), response.getStatus());
		apiResponse = response.readEntity(ApiResponse.class);
		assertEquals(Status.EXPECTATION_FAILED.getStatusCode(), apiResponse.getCode());
		assertEquals(ApiResponse.ERROR, apiResponse.getStatus());
		assertEquals(SignatureApi.SIG05 + SignatureApi.SIG05_NOTFOUND + stringDTO.getValue(), apiResponse.getMessage());

		// correct input
		stringDTO.setValue(signatureToDeleteUUID);
		response = target(SIGNATURE + "/delete").request()
				.header(HttpHeaders.AUTHORIZATION, BEARER + authorizationToken)
				.post(Entity.entity(stringDTO, MediaType.APPLICATION_JSON));
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		apiResponse = response.readEntity(ApiResponse.class);
		assertEquals(Status.OK.getStatusCode(), apiResponse.getCode());
		assertEquals(ApiResponse.SUCCESS, apiResponse.getStatus());
		assertEquals(SignatureApi.SIG05 + "Signature successfully deleted.", apiResponse.getMessage());
	}

	@Test
	public void insertC() throws Exception {
		SignatureCaptchaDTO signatureCaptchaDTO = new SignatureCaptchaDTO();
		signatureCaptchaDTO.setCaptchaValidationDTO(buildCaptchaValidationDTO());
		String randomCountryCode = getRandomCountryCode();
		signatureCaptchaDTO
				.setSignatureDTO(TestUtils.buildTestSignature(randomCountryCode, isCategoryA(randomCountryCode)));
		Response response = target(SIGNATURE + "/insertC").request()
				.post(Entity.entity(signatureCaptchaDTO, MediaType.APPLICATION_JSON));
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		removeNewSignatures();
	}

	private CaptchaValidationDTO buildCaptchaValidationDTO() throws Exception {
		Response response = target(CaptchaApiTest.CAPTCHA + "/image").request().get(Response.class);
		CaptchaDTO captchaDTO = response.readEntity(CaptchaDTO.class);
		CaptchaValidationDTO captchaValidationDTO = new CaptchaValidationDTO();
		captchaValidationDTO.setId(captchaDTO.getId());
		captchaValidationDTO.setValue("1x1f5h5h2f5");
		captchaValidationDTO.setType(CaptchaService.CAPTCHA_IMAGE_TYPE);
		return captchaValidationDTO;
	}

	@Test
	public void shouldBlockIdentityValuesDuplicates() throws Exception {
		String randomCountryCode = getRandomCountryCode(Country.CATEGORY_B);
		boolean categoryA = isCategoryA(randomCountryCode);
		SignatureDTO testSignatureToInsert = TestUtils.buildTestSignature(randomCountryCode, categoryA);
		assertFalse(testSignatureToInsert.getProperties().isEmpty());
		String uuid = insertSignature(testSignatureToInsert, false);
		String identityValue = "";
		for (SupportFormDTO property : testSignatureToInsert.getProperties()) {
			if (SupportFormDTO.identityDocumentNumbersLabels.contains(property.getLabel())) {
				// get the identity value
				identityValue = property.getValue();
			}
		}

		SignatureDTO testSignatureToInsert2 = TestUtils.buildTestSignature(randomCountryCode, categoryA);
		assertFalse(testSignatureToInsert2.getProperties().isEmpty());
		// System.err.println("testSignatureToInsert2.getProperties():
		// "+testSignatureToInsert2.getProperties());
		for (SupportFormDTO property : testSignatureToInsert2.getProperties()) {
			// System.err.println("PROPERTY: "+property);
			if (SupportFormDTO.identityDocumentNumbersLabels.contains(property.getLabel())) {
				// set the same identityValue
				property.setValue(identityValue);
			}
		}
		insertSignature(testSignatureToInsert2, true, Status.CONFLICT.getStatusCode(),
				SignatureApi.SIG04 + SignatureApi.DUPLICATE_IDENTITY_FOUND_MESSAGE, false);
		removeNewSignatures();
	}

	@Test()
	public void shouldBlockCategoryAduplicates() throws Exception {
		String randomCountryCode = getRandomCountryCode(Country.CATEGORY_A);
		boolean categoryA = isCategoryA(randomCountryCode);
		SignatureDTO testSignatureToInsert = TestUtils.buildTestSignature(randomCountryCode, categoryA);
		assertFalse(testSignatureToInsert.getProperties().isEmpty());
		insertSignature(testSignatureToInsert, false);
		SignatureDTO testSignatureToInsert2 = TestUtils.buildTestSignature(testSignatureToInsert.getCountry(),
				categoryA);
		testSignatureToInsert2.setProperties(testSignatureToInsert.getProperties());
		assertFalse(testSignatureToInsert2.getProperties().isEmpty());
		// System.err.println("testSignatureToInsert2.getProperties():
		// "+testSignatureToInsert2.getProp erties());
		insertSignature(testSignatureToInsert2, true, Status.CONFLICT.getStatusCode(),
				"SIG04:  Signature submission error. Duplicate signature", false);
		removeNewSignatures();
	}

}