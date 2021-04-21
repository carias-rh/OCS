package eu.europa.ec.eci.oct.webcommons.services.api;

import static eu.europa.ec.eci.oct.webcommons.services.api.domain.ApiResponse.buildError;
import static eu.europa.ec.eci.oct.webcommons.services.api.domain.ApiResponse.buildSuccess;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import eu.europa.ec.eci.oct.entities.signature.IdentityValue;
import eu.europa.ec.eci.oct.entities.signature.Signature;
import eu.europa.ec.eci.oct.validation.ValidationBean;
import eu.europa.ec.eci.oct.validation.ValidationError;
import eu.europa.ec.eci.oct.validation.ValidationProperty;
import eu.europa.ec.eci.oct.validation.ValidationResult;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.captcha.CaptchaValidationDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.commons.StringDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.signature.SignatureCaptchaDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.signature.SignatureDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.signature.SignatureMetadata;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.signature.SignatureValidation;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.supportForm.SupportFormDTO;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTDuplicateSignatureException;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTParameterException;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTobjectNotFoundException;
import eu.europa.ec.eci.oct.webcommons.services.security.RequestTokenService;

@Service
@Path("/signature")
// @PropertySource(value = { "classpath:application.properties" })
public class SignatureApi extends RestApiParent {

	// @Bean
	// public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
	// return new PropertySourcesPlaceholderConfigurer();
	// }

	public static final String DUPLICATE_IDENTITY_FOUND_MESSAGE = "An identical identity value has already been persisted";
	public static String SIG01 = "SIG01: ";
	public static String SIG02 = "SIG02: ";
	public static String SIG03 = "SIG03: ";
	public static String SIG04 = "SIG04: ";
	public static String SIG05 = "SIG05: ";
	public static String SIG06 = "SIG06: ";
	public static String SIG05_NOTFOUND = " No signature found for uuid ";

	// SIG03
	@GET
	@Path("/lastSignatures")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLastSignatures() {
		List<SignatureMetadata> metadatas = new ArrayList<SignatureMetadata>();
		try {
			List<Signature> signatures = signatureService.getLastSignatures();
			signaturesMetadata.setNumbers(signatures.size());
			for (Signature signature : signatures) {
				metadatas.add(signatureTransformer.transformMetadata(signature));
			}
		} catch (OCTException e) {
			logger.error(e.getMessage());
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					SIG03 + " Signature range unavailable.");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse).build();
		} catch (OCTParameterException e) {
			logger.error(e.getMessage());
			apiResponse = buildError(Status.EXPECTATION_FAILED.getStatusCode(),
					SIG03 + " Signature parameter error. " + e.getMessage());
			return Response.status(Status.EXPECTATION_FAILED).entity(apiResponse).build();
		}
		signaturesMetadata.setMetadatas(metadatas);
		return Response.status(Status.OK).entity(signaturesMetadata)
				.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
	}

	@Value("${mock.enabled:false}")
	private boolean mockFeaturesAllowed;

	// SIG04-01
	@POST
	@Path("/insertC")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response insertWithCaptchaValidation(@HeaderParam(RequestTokenService.X_OCS_TOKEN) String token,
			SignatureCaptchaDTO signatureCaptchaDTO) {
		SignatureDTO signatureDTO = signatureCaptchaDTO.getSignatureDTO();
		CaptchaValidationDTO captchaValidationDTO = signatureCaptchaDTO.getCaptchaValidationDTO();

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

		if (StringUtils.isBlank(captchaValidationDTO.getId()) || StringUtils.isBlank(captchaValidationDTO.getValue())
				|| StringUtils.isBlank(captchaValidationDTO.getType())) {
			apiResponse = buildError(Status.EXPECTATION_FAILED.getStatusCode(),
					SIG04 + " invalid captchaValidationDTO: " + captchaValidationDTO);
			return Response.status(Status.EXPECTATION_FAILED).entity(apiResponse).build();
		}

		SignatureValidation signatureValidation = new SignatureValidation();
		boolean captchaValidationResult = false;
		signatureValidation.setCaptchaValidation(captchaValidationResult);
		if (mockFeaturesAllowed) {
			captchaValidationResult = true;
		} else {
			try {
				captchaValidationResult = captchaService.validateCaptcha(captchaValidationDTO.getId(),
						captchaValidationDTO.getValue(), captchaValidationDTO.getType());
			} catch (Exception e) {
				logger.debug("Error while validating captcha: " + e.getMessage());
				return Response.status(Status.EXPECTATION_FAILED).entity(signatureValidation).build();
			}
		}
		signatureValidation.setCaptchaValidation(captchaValidationResult);
		// check that language code and country code are expected
		if (captchaValidationResult) {
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
			Response validationResponse = validateSignature(signatureDTO, signatureValidation);
			if (validationResponse != null) {
				return validationResponse;
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
				logger.error(e.getMessage());
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
			signatureValidation.setCaptchaValidation(captchaValidationResult);
			return Response.status(Status.OK).entity(signatureValidation)
					.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
		} else {
			return Response.status(Status.EXPECTATION_FAILED).entity(signatureValidation).build();
		}
	}

	// SIG05
	@Secured
	@POST
	@Path("/delete")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(StringDTO stringDTO) {
		// validation of the input
		if (stringDTO == null || StringUtils.isBlank(stringDTO.getValue())) {
			apiResponse = buildError(Status.EXPECTATION_FAILED.getStatusCode(),
					SIG05 + INPUT_PARAMS_EXPECTATION_FAILED);
			return Response.status(Status.EXPECTATION_FAILED).entity(apiResponse).build();
		}

		String signatureIdentifier = stringDTO.getValue();
		try {
			Signature signatureToDelete = signatureService.findByUuid(signatureIdentifier);
			signatureService.deleteSignature(signatureToDelete);
			apiResponse = buildSuccess(Status.OK.getStatusCode(), SIG05 + "Signature successfully deleted.");
			return Response.status(Status.OK).entity(apiResponse).build();
		} catch (OCTException e) {
			logger.error(e.getMessage());
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					SIG05 + " Error while retrieving the signature to delete.");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse).build();
		} catch (OCTobjectNotFoundException e) {
			logger.error(e.getMessage());
			apiResponse = buildError(Status.EXPECTATION_FAILED.getStatusCode(),
					SIG05 + SIG05_NOTFOUND + signatureIdentifier);
			return Response.status(Status.EXPECTATION_FAILED).entity(apiResponse)
					.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
		}
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
		String country = signatureDTO.getCountry();
		validationBean.setNationality(country);

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

}