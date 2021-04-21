package eu.europa.ec.eci.oct.webcommons.services.api;

import static eu.europa.ec.eci.oct.webcommons.services.api.domain.ApiResponse.buildError;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import eu.europa.ec.eci.oct.entities.email.Email;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.email.EmailDTO;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTobjectNotFoundException;

@Service
@Path("/email")
public class EmailApi extends RestApiParent {

	public static String EMA01 = "EMA01: ";

	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces(MediaType.APPLICATION_JSON)
	public Response saveEmail(EmailDTO emailDTO) {
		logger.info("EmailApi: save email");

		String emailAddress = emailDTO.getEmailAddress();
		if (StringUtils.isAllBlank(emailAddress) || StringUtils.isAllBlank(emailDTO.getComunicationLanguage())
				|| StringUtils.isAllBlank(emailDTO.getSignatureIdentifier()) || emailDTO.getEmailId() != null
				|| emailDTO.getInitiativeSubscription() == null) {
			apiResponse = buildError(Status.EXPECTATION_FAILED.getStatusCode(),
					EMA01 + INPUT_PARAMS_EXPECTATION_FAILED);
			return Response.status(Status.EXPECTATION_FAILED).entity(apiResponse).build();
		}
		emailAddress = emailAddress.toLowerCase();
		emailDTO.setEmailAddress(emailAddress);
		try {
			Email emailToBeSaved = emailTransformer.transform(emailDTO);
			boolean isAlreadyExistent = true;
			try {
				emailService.getEmailByAddress(emailToBeSaved.getEmailAddress());
			} catch (OCTobjectNotFoundException e) {
				isAlreadyExistent = false;
			}
			if (isAlreadyExistent) {
				apiResponse = buildError(Status.CONFLICT.getStatusCode(), EMA01 + "Email already existent");
				return Response.status(Status.CONFLICT).entity(apiResponse).build();
			}
			if (emailDTO.getInitiativeSubscription()) {
				emailService.saveEmail(emailToBeSaved);
			}
			return Response.status(Status.OK)
					.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
		} catch (OCTException e) {
			String errorMessage = EMA01 + e.getMessage();
			logger.error(errorMessage);
			apiResponse = buildError(INTERNAL_SERVER_ERROR.getStatusCode(), errorMessage);
			return Response.status(INTERNAL_SERVER_ERROR).entity(apiResponse)
					.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
		} catch (OCTobjectNotFoundException e) {
			String errorMessage = EMA01 + e.getMessage();
			logger.error("no signature associated to submit email: "+errorMessage);
			apiResponse = buildError(PRECONDITION_FAILED.getStatusCode(), errorMessage);
			return Response.status(PRECONDITION_FAILED).entity(apiResponse)
					.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
		}
	}
}
