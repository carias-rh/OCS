package eu.europa.ec.eci.oct.webcommons.services.api;

import static eu.europa.ec.eci.oct.webcommons.services.api.domain.ApiResponse.buildError;

import java.security.SecureRandom;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.octo.captcha.service.CaptchaServiceException;

import eu.europa.ec.eci.oct.webcommons.services.api.domain.ApiResponse;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.captcha.CaptchaDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.captcha.CaptchaValidationDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.captcha.CaptchaValidationResultDTO;
import eu.europa.ec.eci.oct.webcommons.services.captcha.CaptchaService;

@Service
@Scope("prototype")
@Path("/captcha")
public class CaptchaApi extends RestApiParent {

	@Context
	protected HttpHeaders headers;

	@GET
	@Path("/image")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getImage() {

		CaptchaDTO captchaDTO = new CaptchaDTO();

		try {
			Long l = new SecureRandom().nextLong();
			String captchaId = l.toString();

			captchaDTO.setId(captchaId);
			captchaDTO.setData(captchaService.generateImageCaptcha(captchaId));
		} catch (Exception e) {
			logger.error(e);
			ApiResponse apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					"CAP01 - unable to generate image captcha");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse).build();
		}

		return Response.status(Status.OK).entity(captchaDTO)
				.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
	}

	@GET
	@Path("/audio")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAudio() {

		CaptchaDTO captchaDTO = new CaptchaDTO();

		try {
			Long l = new SecureRandom().nextLong();
			String captchaId = l.toString();

			captchaDTO.setId(captchaId);
			captchaDTO.setData(captchaService.generateAudioCaptcha(captchaId));
		} catch (Exception e) {
			logger.error(e);
			ApiResponse apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					"CAP02 - unable to generate audio captcha");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse).build();
		}

		return Response.status(Status.OK).entity(captchaDTO)
				.header("Cache-Control", "no-store, no-cache, must-revalidate").build();

	}

	@POST
	@Path("/validate")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response validate(CaptchaValidationDTO c) {

		boolean valid;
		if (StringUtils.isBlank(c.getId()) || StringUtils.isBlank(c.getType()) || StringUtils.isBlank(c.getValue())
				|| !((c.getType().equals(CaptchaService.CAPTCHA_IMAGE_TYPE)
						|| c.getType().equals(CaptchaService.CAPTCHA_AUDIO_TYPE)))) {
			return Response.status(Status.EXPECTATION_FAILED)
					.entity(buildError(Status.EXPECTATION_FAILED.getStatusCode(), "CAP03 - Invalid input parameters"))
					.build();
		}

		try {
			valid = captchaService.validateCaptcha(c.getId(), c.getValue(), c.getType());
		} catch (CaptchaServiceException cse) {
			return Response.status(Status.UNAUTHORIZED)
					.entity(buildError(Status.UNAUTHORIZED.getStatusCode(), "CAP03 - Incorrect captcha resolution."))
					.build();
		} catch (Exception e) {
			logger.error(e);
			ApiResponse apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					"CAP03 - unable to validate captcha");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse).build();
		}
		return Response.status(Status.OK).entity(new CaptchaValidationResultDTO(valid))
				.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
	}

}
