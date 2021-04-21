package eu.europa.ec.eci.oct.webcommons.services.api;

import static eu.europa.ec.eci.oct.webcommons.services.api.domain.ApiResponse.buildError;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import eu.europa.ec.eci.oct.entities.export.ExportHistory;
import eu.europa.ec.eci.oct.entities.signature.IdentityValue;
import eu.europa.ec.eci.oct.export.persistence.ExportHistoryPersistenceDAO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.ApiResponse;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.commons.LongDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.country.CountryDTOlist;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.customisations.CustomisationsDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.initiative.InitiativeDescriptionDTOext;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.initiative.InitiativeDescriptionsDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.language.LanguageDTOlist;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.signature.SignatureCount;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.signature.SignatureCountryCount;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.signature.SignatureDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.signature.SignaturesMetadata;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.socialMedia.SocialMediaMessageDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.supportForm.SupportFormDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.systemManager.CollectorState;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.systemManager.SystemStateDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.transformer.ContactTransformer;
import eu.europa.ec.eci.oct.webcommons.services.api.transformer.CountryTransformer;
import eu.europa.ec.eci.oct.webcommons.services.api.transformer.CustomisationsTransformer;
import eu.europa.ec.eci.oct.webcommons.services.api.transformer.EmailTransformer;
import eu.europa.ec.eci.oct.webcommons.services.api.transformer.ExportTransformer;
import eu.europa.ec.eci.oct.webcommons.services.api.transformer.FeedbackTransformer;
import eu.europa.ec.eci.oct.webcommons.services.api.transformer.InitiativeDescriptionTransformer;
import eu.europa.ec.eci.oct.webcommons.services.api.transformer.LanguageTransformer;
import eu.europa.ec.eci.oct.webcommons.services.api.transformer.ReportingTransformer;
import eu.europa.ec.eci.oct.webcommons.services.api.transformer.SignatureTransformer;
import eu.europa.ec.eci.oct.webcommons.services.api.transformer.SocialMediaTransformer;
import eu.europa.ec.eci.oct.webcommons.services.api.transformer.SupportFormTransformer;
import eu.europa.ec.eci.oct.webcommons.services.api.transformer.SystemStateTransformer;
import eu.europa.ec.eci.oct.webcommons.services.captcha.CaptchaService;
import eu.europa.ec.eci.oct.webcommons.services.configuration.ConfigurationService;
import eu.europa.ec.eci.oct.webcommons.services.contact.ContactService;
import eu.europa.ec.eci.oct.webcommons.services.email.EmailService;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTDuplicateSignatureException;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;
import eu.europa.ec.eci.oct.webcommons.services.initiative.InitiativeService;
import eu.europa.ec.eci.oct.webcommons.services.reporting.ReportingService;
import eu.europa.ec.eci.oct.webcommons.services.security.RequestTokenService;
import eu.europa.ec.eci.oct.webcommons.services.signature.SignatureService;
import eu.europa.ec.eci.oct.webcommons.services.socialMedia.SocialMediaService;
import eu.europa.ec.eci.oct.webcommons.services.system.SystemManager;
import eu.europa.ec.eci.oct.webcommons.services.translations.TranslationService;
import eu.europa.ec.eci.oct.webcommons.services.validation.RuleService;

public class RestApiParent {

	public static final String UNAUTHORIZED_ACTION_REQUEST = "The current system status doesn't allow this action.";
	public static final String INPUT_PARAMS_EXPECTATION_FAILED = "One or more input parameters failed expectation";
	public static final String OBJECT_NOT_FOUND_FOR_PARAM = "Object not found for param: ";
	public static final String OBJECT_RETURNED_SUCCESSFULLY = "Requested object has been successfuly returned.";
	protected Status PRECONDITION_FAILED = Status.PRECONDITION_FAILED;

	protected Logger logger = LogManager.getLogger(RestApiParent.class);
	@Autowired
	protected ApiResponse apiResponse;
	@Autowired
	protected SystemStateDTO systemStateDTO;
	@Autowired
	protected InitiativeDescriptionsDTO initiativeDescriptionsDTO;
	@Autowired
	protected InitiativeDescriptionDTOext initiativeDescriptionDTOext;
	@Autowired
	protected CustomisationsDTO customisationsDTO;
	@Autowired
	protected SystemManager systemManager;
	@Autowired
	protected ConfigurationService configurationService;
	@Autowired
	protected InitiativeService initiativeService;
	@Autowired
	protected ContactService contactService;
	@Autowired
	protected CustomisationsTransformer customisationsTransformer;
	@Autowired
	protected InitiativeDescriptionTransformer initiativeDescriptionTransformer;
	@Autowired
	protected SystemStateTransformer systemStateTransformer;
	@Autowired
	protected ContactTransformer contactTransformer;
	@Autowired
	protected SignatureService signatureService;
	@Autowired
	protected ReportingService reportingService;
	@Autowired
	protected SignatureCount signatureCount;
	@Autowired
	protected SignatureCountryCount signatureCountryCount;
	@Autowired
	protected CollectorState collectorState;
	@Autowired
	protected CountryDTOlist countries;
	@Autowired
	protected LanguageDTOlist languages;
	@Autowired
	protected CountryTransformer countryTransformer;
	@Autowired
	protected LanguageTransformer languageTransformer;
	@Autowired
	protected SocialMediaTransformer socialMediaTransformer;
	@Autowired
	protected SupportFormTransformer countryPropertyTransformer;
	@Autowired
	protected ReportingTransformer reportingTransformer;
	@Autowired
	protected SocialMediaService socialMediaService;
	@Autowired
	protected SocialMediaMessageDTO socialMediaMessageDTO;
	@Autowired
	protected SignatureTransformer signatureTransformer;
	@Autowired
	protected FeedbackTransformer feedbackTransformer;
	@Autowired
	protected LongDTO longDTO;
	@Autowired
	protected SignaturesMetadata signaturesMetadata;
	@Autowired
	protected ExportTransformer exportTransformer;
	@Autowired
	protected CaptchaService captchaService;
	@Autowired
	protected TranslationService translationService;
	@Autowired
	protected RuleService ruleService;
	@Autowired
	protected ExportHistoryPersistenceDAO exportHistoryPersistenceDAO;
	@Autowired
	protected RequestTokenService requestTokenService;
	@Autowired
	protected EmailService emailService;
	@Autowired
	protected EmailTransformer emailTransformer;

	@Value("${running.environment.profile:local}")
	protected String runningEnv;
	protected static final String ENVIRONMENT_LOCAL = "local";

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Value("${mock.enabled:true}")
	protected boolean mockFeaturesAllowed;

	protected Status INTERNAL_SERVER_ERROR = Status.INTERNAL_SERVER_ERROR;
	protected Status EXPECTATION_FAILED = Status.EXPECTATION_FAILED;
	protected Status OK = Status.OK;
	protected Status BAD_REQUEST = Status.BAD_REQUEST;
	protected Status CONFLICT = Status.CONFLICT;
	protected Status NOT_ACCEPTABLE = Status.NOT_ACCEPTABLE;
	protected Status NOT_MODIFIED = Status.NOT_MODIFIED;
	protected Status ACCEPTED = Status.ACCEPTED;
	protected Status UNAUTHORIZED = Status.UNAUTHORIZED;

	protected Client client = ClientBuilder.newClient().register(MultiPartFeature.class);

	protected void cleanExportFolder() throws OCTException {
		// clean exported files
		try {
			ExportHistory exportHistory = exportHistoryPersistenceDAO.getLastExportHistory();
			if (exportHistory != null) {
				String exportDirectoryPath = exportHistory.getExportDirectoryPath();
				if (StringUtils.isNotBlank(exportDirectoryPath)) {
					if (Files.exists(Paths.get(exportDirectoryPath + "/"))) {
						File directory = new File(exportDirectoryPath);
						FileUtils.cleanDirectory(directory);
						FileUtils.deleteDirectory(directory);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			;
			throw new OCTException("Error while cleaning export directory: " + e.getMessage());
		}
	}

	public Response httpGET(String url, String mediaType, ContainerRequestContext requestContext) {

		WebTarget webTarget = client.target(url);
		logger.info("Performing GET " + url);
		// String newAccessToken = tokenService.exchangeRefreshToken(requestContext,
		// requestContext.getHeaders().getFirst(TokenAuthentificationHelper.AUTHORIZATION_HEADER_REFRESH));
		// logger.info("newAccessToken = " + newAccessToken);
		return webTarget.request(mediaType).get(Response.class);
		// return
		// webTarget.request(mediaType).header(TokenAuthentificationHelper.AUTHORIZATION_HEADER,
		// TokenAuthentificationHelper.BEARER + newAccessToken).get(Response.class);
	}

	public Response httpPOST(String url, String mediaType, Object o) {
		return httpPOST(url, mediaType, o, null);
	}

	public Response httpPOST(String url, String mediaType, Object o, ContainerRequestContext requestContext) {
		logger.info("Performing POST " + url);
		System.err.println("Performing POST " + url);
		WebTarget webTarget = client.target(url);
		// String newAccessToken = tokenService.exchangeRefreshToken(requestContext,
		// requestContext.getHeaders().getFirst(TokenAuthentificationHelper.AUTHORIZATION_HEADER_REFRESH));
		return webTarget.request(mediaType).post(Entity.entity(o, mediaType));
		// return
		// webTarget.request(mediaType).header(TokenAuthentificationHelper.AUTHORIZATION_HEADER,
		// TokenAuthentificationHelper.BEARER + newAccessToken).post(Entity.entity(o,
		// mediaType));
	}

	protected Response errorResponse(Status errorStatus, String errorMessage, Class<?> clazz) {
		logger.error(clazz.getName() + " - " + errorMessage);
		apiResponse = buildError(errorStatus.getStatusCode(), errorMessage);
		return Response.status(errorStatus).entity(apiResponse)
				.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
	}

	protected String serialize(SignatureDTO s) throws OCTException {
		StringBuilder buf = new StringBuilder();

		for (Iterator<SupportFormDTO> iterator = s.getProperties().iterator(); iterator.hasNext();) {
			SupportFormDTO supportFormDTO = iterator.next();
			String propName = SupportFormDTO.OCT_PROPERTY_PREFIX + supportFormDTO.getLabel();
			// System.err.println("PROP NAME = "+supportFormDTO.getLabel());
			// System.err.println("PROP VALUE = "+supportFormDTO.getValue());
			String propValue = supportFormDTO.getValue().toUpperCase();
			buf.append("[").append(propName).append("][").append(propValue).append("]");
		}
		return buf.toString();
	}

	protected IdentityValue checkIdentityDuplicates(SignatureDTO signatureDTO)
			throws OCTException, OCTDuplicateSignatureException {
		/*
		 * if identity document is provided, check if is already existent (task
		 * OCS-1504)
		 */
		IdentityValue identityValue = new IdentityValue();
		String replacingLabel = SupportFormDTO.OCT_PROPERTY_PREFIX;
		// document check
		for (SupportFormDTO supportFormDTO : signatureDTO.getProperties()) {
			String label = supportFormDTO.getLabel();
			String value = supportFormDTO.getValue();
			if (SupportFormDTO.identityDocumentNumbersLabels.contains(label)) {
				// identity property present: to be checked if already present
				identityValue = signatureService.findIdentityDocumentDuplicate(replacingLabel + label, value,
						signatureDTO.getCountry());
			}
		}
		identityValue.setCountryCode(signatureDTO.getCountry());

		return identityValue;
	}

}
