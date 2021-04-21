package eu.europa.ec.eci.oct.webcommons.restApi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.eci.oct.webcommons.services.api.domain.commons.StringDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.security.AuthenticationTokenDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.security.ChallengeDTO;
import eu.europa.ec.eci.oct.webcommons.services.security.SecurityService;

/**
 * User: franzmh Date: 17/01/17
 */
public class SecurityApiTest extends RestApiTest {

	@Autowired
	SecurityService securityService;

	@Test
	public void challenge() {
		Response response = target(SECURITY + "/challenge").request().get(Response.class);
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		ChallengeDTO challengeDTO = response.readEntity(ChallengeDTO.class);
		assertNotNull(challengeDTO.getChallenge());
		assertTrue(StringUtils.isNotEmpty(challengeDTO.getChallenge()));
		assertTrue(challengeDTO.getChallenge().length() >= 768);
	}

	@Test
	@Ignore
	@Transactional(rollbackFor = Exception.class)
	public void extendSession() throws Exception {
		// to run this test change the value of SecurityServiceImpl
		// SESSION_EXPIRING_MINUTES value to 1
		// TODO: parametrize the session expire for testing. for now to be run manually

		String challengeResult = obtainChallengeResult();
		AuthenticationTokenDTO authenticationToken = authenticationFlow(USER, PWD, challengeResult);
		assertNotNull(authenticationToken);
		assertNotNull(authenticationToken.getCode());
		assertEquals(Status.OK.getStatusCode(), authenticationToken.getCode().intValue());
		assertNotNull(authenticationToken.getExpireTimeMinutes());

		// wait one minute
		logger.debug(
				"Going to sleep for 1 minute and one second, please be patient... If I wake up it means session expired successfully");
		TimeUnit.MINUTES.sleep(1);
		TimeUnit.SECONDS.sleep(1);
		// should not be authenticated anymore, session expired
		Response extendSessionResponse = target(SECURITY + "/extendSession").request()
				.header(HttpHeaders.AUTHORIZATION, BEARER + authorizationToken)
				.post(Entity.entity(authenticationToken.getAuthToken(), MediaType.APPLICATION_JSON));
		assertEquals(Status.UNAUTHORIZED.getStatusCode(), extendSessionResponse.getStatus());
		assertEquals(1, authenticationToken.getExpireTimeMinutes().intValue());

		// now try with refresh
		authenticationToken = authenticationFlow(USER, PWD, challengeResult);
		assertNotNull(authenticationToken);
		assertEquals(1, authenticationToken.getExpireTimeMinutes().intValue());
		assertEquals(Status.OK.getReasonPhrase(), authenticationToken.getStatus());
		logger.debug(
				"Going to sleep for 50 seconds, please be patient....When I wake up I'll try to refresh the session, since it will be 10 seconds to expire!");
		TimeUnit.SECONDS.sleep(50);
		extendSessionResponse = target(SECURITY + "/extendSession").request()
				.header(HttpHeaders.AUTHORIZATION, BEARER + authorizationToken)
				.post(Entity.entity(authenticationToken.getAuthToken(), MediaType.APPLICATION_JSON));
		assertEquals(Response.Status.OK.getStatusCode(), extendSessionResponse.getStatus());
		String newToken = extendSessionResponse.readEntity(AuthenticationTokenDTO.class).getAuthToken();
		logger.debug(
				"Going to sleep for 30 seconds, When I wake up I'll try to see if my session is stil valid. It should be.");
		TimeUnit.SECONDS.sleep(30);
		// should be still possible to extend the session because session is not expired
		extendSessionResponse = target(SECURITY + "/extendSession").request()
				.header(HttpHeaders.AUTHORIZATION, BEARER + newToken)
				.post(Entity.entity(newToken, MediaType.APPLICATION_JSON));
		assertEquals(Response.Status.OK.getStatusCode(), extendSessionResponse.getStatus());
		extendSessionResponse = target(SECURITY + "/logout").request()
				.header(HttpHeaders.AUTHORIZATION, BEARER + newToken)
				.post(Entity.entity(newToken, MediaType.APPLICATION_JSON));
	}

	@Test
	@Transactional(rollbackFor = Exception.class)
	public void authentication() throws Exception {

		String correctUser = USER;
		String correctPwd = PWD;
		String wrongUser = "Life on Mars";
		String wrongPwd = "Life on Mars";
		String wrongResult = "Life on Mars";

		AuthenticationTokenDTO authenticationToken = null;
		long wrongUserStartTime = System.currentTimeMillis();
		authenticationToken = authenticationFlow(wrongUser, wrongPwd, wrongResult);
		long wrongUserEndTime = System.currentTimeMillis();
		long wrongUserResponseTime = wrongUserEndTime - wrongUserStartTime;

		assertNull(authenticationToken);
		String correctResult1 = obtainChallengeResult();
		authenticationToken = authenticationFlow(correctUser, wrongPwd, correctResult1);
		assertNull(authenticationToken);

		String correctResult2 = obtainChallengeResult();
		authenticationToken = authenticationFlow(wrongUser, correctPwd, correctResult2);
		assertNull(authenticationToken);

		String correctResult3 = obtainChallengeResult();
		long correctUserStartTime = System.currentTimeMillis();
		authenticationToken = authenticationFlow(correctUser, correctPwd, correctResult3);
		long correctUserEndTime = System.currentTimeMillis();
		long correctUserResponseTime = correctUserEndTime - correctUserStartTime;

		assertNotNull(authenticationToken);
		assertNotNull(authenticationToken.getCode());
		assertEquals(Status.OK.getStatusCode(), authenticationToken.getCode().intValue());
		assertNotNull(authenticationToken.getExpireTimeMinutes());
		/*
		 * user enumeration vulnerability check. time between wrong and correct user
		 * response should be the same
		 */
		assertTrue(Math.abs(correctUserResponseTime - wrongUserResponseTime) < 200);
		
		/*
		 * same challenge vulnerability: sending the same challenge twice the authentication should fail
		 */
		authenticationToken = authenticationFlow(correctUser, correctPwd, correctResult3);
		assertNull(authenticationToken);
	}

	@Test
	public void shouldLogout() throws Exception {
		login();
		StringDTO stringDTO = new StringDTO(authorizationToken);
		// the logout API is secured. A valid authorisation Token is required.
		Response response = target(SECURITY + "/logout").request()
				.header(HttpHeaders.AUTHORIZATION, BEARER + authorizationToken)
				.post(Entity.entity(stringDTO, MediaType.APPLICATION_JSON));
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		// After the logout, the authToken is invalidated. A call to a secured API must
		// return a 401
		response = logout();
		assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
	}

	private Response logout() {
		StringDTO stringDTO = new StringDTO(authorizationToken);
		Response response;
		response = target(SECURITY + "/logout").request().header(HttpHeaders.AUTHORIZATION, BEARER + authorizationToken)
				.post(Entity.entity(stringDTO, MediaType.APPLICATION_JSON));
		return response;
	}

	@Test
	public void sessionTerminationVulnerability() throws Exception {
		Response authAPIget = target(VERSION + "/securedGet").request()
				.header(HttpHeaders.AUTHORIZATION, BEARER + "invalidtoken").get();
		assertEquals(Status.UNAUTHORIZED.getStatusCode(), authAPIget.getStatus());
		String validToken = login();
		authAPIget = target(VERSION + "/securedGet").request()
				.header(HttpHeaders.AUTHORIZATION, BEARER + validToken).get();
		assertEquals(Status.OK.getStatusCode(), authAPIget.getStatus());
		Response authAPIpost = target(VERSION + "/securedPost").request()
				.header(HttpHeaders.AUTHORIZATION, BEARER + validToken)
				.post(Entity.entity("test", MediaType.TEXT_PLAIN));
		assertEquals(Status.OK.getStatusCode(), authAPIpost.getStatus());
		logout();
		assertFalse(securityService.isAuthenticated(validToken));
		Response authAPIgetAfterLogout = target(VERSION + "/securedGet").request().header(HttpHeaders.AUTHORIZATION, BEARER + validToken)
				.get();
		assertEquals(Status.UNAUTHORIZED.getStatusCode(), authAPIgetAfterLogout.getStatus());
		Response authAPIpostAfterLogout = target(VERSION + "/securedPost").request().header(HttpHeaders.AUTHORIZATION, BEARER + validToken)
				.post(Entity.entity("test", MediaType.TEXT_PLAIN));
		assertEquals(Status.UNAUTHORIZED.getStatusCode(), authAPIpostAfterLogout.getStatus());
	}

}
