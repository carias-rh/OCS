package eu.europa.ec.eci.oct.webcommons.services.api;

import static eu.europa.ec.eci.oct.webcommons.services.api.domain.ApiResponse.buildError;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europa.ec.eci.oct.webcommons.services.api.domain.commons.StringDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.security.AuthenticationDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.security.AuthenticationTokenDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.security.ChallengeDTO;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.AuthenticationException;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.AuthenticationLockedException;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;
import eu.europa.ec.eci.oct.webcommons.services.security.SecurityService;

/**
 * User: franzmh Date: 16/01/17
 */

@Service
@Path("/security")
public class SecurityApi extends RestApiParent {
	public static String SEC01 = "SEC01: ";
	public static String SEC02 = "SEC02: ";
	public static String SEC03 = "SEC03: ";
	public static String SEC04 = "SEC04: ";

	@Autowired
	SecurityService securityService;

	// SEC01
	@GET
	@Path("/challenge")
	@Produces(MediaType.APPLICATION_JSON)
	public Response challenge() {
		ChallengeDTO challengeDTO = new ChallengeDTO();
		try {
			challengeDTO.setChallenge(securityService.generateChallenge());
			return Response.status(Status.OK).entity(challengeDTO)
					// .header("Cache-Control", "no-store, no-cache, must-revalidate")
					.build();
		} catch (OCTException e) {
			logger.error(SEC01 + e.getMessage());
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					SEC01 + "Challenge generation error");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse).type(MediaType.APPLICATION_JSON)
					.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
		}
	}

	// SEC02
	@POST
	@Path("/authenticate")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response authenticate(AuthenticationDTO authenticationDTO) {
		String authToken = null;
		try {
			authToken = securityService.authenticate(authenticationDTO.getUser(), authenticationDTO.getPwd(),
					authenticationDTO.getChallengeResult());
			AuthenticationTokenDTO authenticationTokenDTO = new AuthenticationTokenDTO();
			authenticationTokenDTO.setCode(OK.getStatusCode());
			authenticationTokenDTO.setStatus(OK.getReasonPhrase());
			authenticationTokenDTO.setExpireTimeMinutes(securityService.getSessionExpireTime());
			authenticationTokenDTO.setAuthToken(authToken);
			return Response.status(OK).entity(authenticationTokenDTO)
					.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
		} catch (OCTException e) {
			logger.error(SEC02 + e.getMessage());
			apiResponse = buildError(INTERNAL_SERVER_ERROR.getStatusCode(), SEC02 + UNAUTHORIZED.getReasonPhrase());
			return Response.status(INTERNAL_SERVER_ERROR).entity(apiResponse).type(MediaType.APPLICATION_JSON)
					.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
		} catch (AuthenticationException e) {
			logger.error(SEC02 + e.getMessage());
			apiResponse = buildError(UNAUTHORIZED.getStatusCode(), SEC02 + UNAUTHORIZED.getReasonPhrase());
			return Response.status(UNAUTHORIZED).entity(apiResponse).type(MediaType.APPLICATION_JSON)
					.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
		} catch (AuthenticationLockedException e) {
			logger.error(SEC02 + e.getMessage());
			apiResponse = buildError(UNAUTHORIZED.getStatusCode(), SEC02 + UNAUTHORIZED.getReasonPhrase());
			return Response.status(UNAUTHORIZED).entity(apiResponse).type(MediaType.APPLICATION_JSON)
					.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
		}
	}

	@Secured
	@POST
	@Path("/logout")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response logout(StringDTO authToken) {
		String user = "admin";
		try {
			securityService.logout(authToken.getValue());
			return Response.status(Status.OK).entity("Logout performed for user: " + user)
					// .header("Cache-Control", "no-store, no-cache, must-revalidate")
					.build();
		} catch (OCTException e) {
			apiResponse = buildError(UNAUTHORIZED.getStatusCode(), SEC03 + UNAUTHORIZED.getReasonPhrase());
			return Response.status(UNAUTHORIZED).entity(apiResponse).type(MediaType.APPLICATION_JSON)
					// .header("Cache-Control", "no-store, no-cache, must-revalidate")
					.build();
		} catch (AuthenticationException e) {
			logger.error(SEC03 + e.getMessage());
			apiResponse = buildError(UNAUTHORIZED.getStatusCode(), SEC03 + UNAUTHORIZED.getReasonPhrase());
			return Response.status(UNAUTHORIZED).entity(apiResponse).type(MediaType.APPLICATION_JSON)
					// .header("Cache-Control", "no-store, no-cache, must-revalidate")
					.build();
		}
	}

	@Secured
	@POST
	@Path("/extendSession")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response extendSession(String authToken) {
		try {
			securityService.extendAuthenticationCache(authToken);
			AuthenticationTokenDTO authTokenDTO = new AuthenticationTokenDTO();
			authTokenDTO.setAuthToken(authToken);
			authTokenDTO.setCode(Status.OK.getStatusCode());
			authTokenDTO.setExpireTimeMinutes(securityService.getSessionExpireTime());
			return Response.status(Status.OK).entity(authTokenDTO)
					// .header("Cache-Control", "no-store, no-cache, must-revalidate")
					.build();
		} catch (AuthenticationException e) {
			logger.error(SEC04 + e.getMessage());
			apiResponse = buildError(UNAUTHORIZED.getStatusCode(), SEC04 + UNAUTHORIZED.getReasonPhrase());
			return Response.status(Status.UNAUTHORIZED).entity(apiResponse).type(MediaType.APPLICATION_JSON)
					.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
		}
	}

}
