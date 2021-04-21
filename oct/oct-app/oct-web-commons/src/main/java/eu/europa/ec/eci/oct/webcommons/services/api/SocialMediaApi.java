package eu.europa.ec.eci.oct.webcommons.services.api;

import static eu.europa.ec.eci.oct.webcommons.services.api.domain.ApiResponse.buildError;

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
import org.springframework.stereotype.Service;

import eu.europa.ec.eci.oct.entities.admin.SocialMedia;
import eu.europa.ec.eci.oct.entities.admin.SocialMediaMessage;
import eu.europa.ec.eci.oct.entities.system.Language;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.socialMedia.SocialMediaEnum;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.socialMedia.SocialMediaMessageDTO;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;

@Service
@Path("/socialmedia")
public class SocialMediaApi extends RestApiParent {

	// TODO: refactoring for validate input

	public static String SOC01 = "SOC01: ";
	public static String SOC02 = "SOC02: ";

	@GET
	@Path("/{socialMedia}/{languageCode}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSocialMediaMessage(@PathParam("socialMedia") String socialMedia,
			@PathParam("languageCode") String languageCode) {

		// validation of the input
		if (StringUtils.isBlank(languageCode) || languageCode.length() != Language.DEFAULT_CODE_LENGTH) {
			apiResponse = buildError(Status.EXPECTATION_FAILED.getStatusCode(),
					INPUT_PARAMS_EXPECTATION_FAILED + languageCode);
			return Response.status(Status.EXPECTATION_FAILED).entity(apiResponse).build();
		}

		try {
			boolean isNotRecognizedLanguage = systemManager.isNotARecognizedLanguage(languageCode);
			if (isNotRecognizedLanguage) {
				apiResponse = buildError(Status.EXPECTATION_FAILED.getStatusCode(),
						INPUT_PARAMS_EXPECTATION_FAILED + languageCode);
				return Response.status(Status.EXPECTATION_FAILED).entity(apiResponse).build();
			}
		} catch (OCTException e) {
			logger.error(e.getMessage());
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					SOC01 + "Unable to retrieve socialMedia Message");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse).build();
		}

		if (!socialMedia.equalsIgnoreCase(SocialMediaEnum.FACEBOOK.getName())
				&& !socialMedia.equalsIgnoreCase(SocialMediaEnum.TWITTER.getName())
				&& !socialMedia.equalsIgnoreCase(SocialMediaEnum.GOOGLE_PLUS.getName())
				&& !socialMedia.equalsIgnoreCase(SocialMediaEnum.CALL_FOR_ACTION.getName())) {
			apiResponse = buildError(Status.NO_CONTENT.getStatusCode(),
					SOC01 + INPUT_PARAMS_EXPECTATION_FAILED + socialMedia);
			return Response.status(Status.NO_CONTENT).entity(apiResponse).build();
		}

		SocialMediaMessage socialMediaMessageDAO = null;
		try {
			socialMediaMessageDAO = socialMediaService.getSocialMediaMessage(socialMedia, languageCode);
			if (socialMediaMessageDAO != null) {
				socialMediaMessageDTO = socialMediaTransformer.transform(socialMediaMessageDAO);
			} else {
				socialMediaMessageDTO = new SocialMediaMessageDTO();
				socialMediaMessageDTO.setLanguageCode(languageCode);
				socialMediaMessageDTO.setMessage("");
				socialMediaMessageDTO.setSocialMedia(socialMedia);
			}
			return Response.status(Status.OK).entity(socialMediaMessageDTO)
					.header("Cache-Control", "no-store, no-cache, must-revalidate").build();

		} catch (OCTException e) {
			logger.error(e.getMessage());
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					SOC01 + "Unable to retrieve socialMedia Message");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse).build();
		}
	}


	@Secured
	@POST
	@Path("/update")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateSocialMediaMessage(SocialMediaMessageDTO socialMediaMessageDTO) {
		String languageCode = socialMediaMessageDTO.getLanguageCode();
		// validation of the input
		if (StringUtils.isBlank(languageCode) || languageCode.length() != Language.DEFAULT_CODE_LENGTH) {
			apiResponse = buildError(Status.EXPECTATION_FAILED.getStatusCode(),
					SOC02 + INPUT_PARAMS_EXPECTATION_FAILED + languageCode);
			return Response.status(Status.EXPECTATION_FAILED).entity(apiResponse).build();
		}

		try {
			boolean isNotRecognizedLanguage = systemManager.isNotARecognizedLanguage(languageCode);
			if (isNotRecognizedLanguage) {
				apiResponse = buildError(Status.EXPECTATION_FAILED.getStatusCode(),
						INPUT_PARAMS_EXPECTATION_FAILED + languageCode);
				return Response.status(Status.EXPECTATION_FAILED).entity(apiResponse).build();
			}
		} catch (OCTException e) {
			logger.error(e.getMessage());
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					SOC01 + "Unable to retrieve socialMedia Message");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse).build();
		}

		String socialMediaName = socialMediaMessageDTO.getSocialMedia();
		if (!socialMediaName.equalsIgnoreCase(SocialMediaEnum.FACEBOOK.getName())
				&& !socialMediaName.equalsIgnoreCase(SocialMediaEnum.TWITTER.getName())
				&& !socialMediaName.equalsIgnoreCase(SocialMediaEnum.CALL_FOR_ACTION.getName())) {
			apiResponse = buildError(Status.EXPECTATION_FAILED.getStatusCode(),
					SOC02 + INPUT_PARAMS_EXPECTATION_FAILED + socialMediaMessageDTO.getSocialMedia());
			return Response.status(Status.EXPECTATION_FAILED).entity(apiResponse).build();
		}

		SocialMediaMessage socialMediaMessageDAO = null;
		try {
			socialMediaMessageDAO = socialMediaService.getSocialMediaMessage(socialMediaName, languageCode);
			if (socialMediaMessageDAO == null) {
				socialMediaMessageDAO = new SocialMediaMessage();
				SocialMedia socialMedia = socialMediaService.getSocialMediaByName(socialMediaName);
				Language language = systemManager.getLanguageByCode(languageCode);
				socialMediaMessageDAO.setSocialMedia(socialMedia);
				socialMediaMessageDAO.setLanguage(language);
				socialMediaMessageDAO.setMessage(socialMediaMessageDTO.getMessage());
			}else {
				String newSocialMediaMessage = socialMediaMessageDTO.getMessage().trim();
				socialMediaMessageDAO.setMessage(newSocialMediaMessage);
			}
		} catch (OCTException e) {
			logger.error(e.getMessage());
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					SOC02 + "Unable to retrieve social media message to update.");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse).build();
		}

		try {
			socialMediaService.saveOrUpdateSocialMessage(socialMediaMessageDAO);
			return Response.status(Status.OK).entity(socialMediaMessageDTO)
					.header("Cache-Control", "no-store, no-cache, must-revalidate")
					.build();
		} catch (OCTException e) {
			logger.error(e.getMessage());
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					SOC02 + "Unable to update social media message.");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse).build();
		}
	}
}
