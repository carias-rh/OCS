package eu.europa.ec.eci.oct.webcommons.restApi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europa.ec.eci.oct.utils.DateUtils;
import eu.europa.ec.eci.oct.webcommons.services.api.CustomisationsApi;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.ApiResponse;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.customisations.CustomisationsDTO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/rest-api-test.xml")
public class CustomisationsApiTest extends RestApiTest {

	static final String CUSTOMISATIONS = "/customisations";

	@Test
	public void cus01() {
		// show customisations
		try {
			Response response = target(CUSTOMISATIONS + "/show").request().get(Response.class);
			assertEquals(Status.OK.getStatusCode(), response.getStatus());
			CustomisationsDTO customisationsDTO = response.readEntity(CustomisationsDTO.class);
			assertEquals(MOCK_CUSTOMISATIONS_CALLBACK_URL, customisationsDTO.getCallbackUrl());
			assertEquals(MOCK_CUSTOMISATIONS_FACEBOOK_URL, customisationsDTO.getFacebookUrl());
			assertEquals(MOCK_CUSTOMISATIONS_GOOGLE_URL, customisationsDTO.getGoogleUrl());
			assertEquals(MOCK_CUSTOMISATIONS_TWITTER_URL, customisationsDTO.getTwitterUrl());
			assertEquals(MOCK_CUSTOMISATIONS_SIGNATURE_GOAL, customisationsDTO.getSignatureGoal());
			assertEquals(MOCK_CUSTOMISATIONS_IS_CUSTOM_LOGO, customisationsDTO.isCustomLogo());
			assertEquals(MOCK_CUSTOMISATIONS_IS_OPTIONAL_VALIDATION, customisationsDTO.isOptionalValidation());
			assertEquals(MOCK_CUSTOMISATIONS_SHOW_RECENT_SUPPORTERS, customisationsDTO.getShowRecentSupporters());
			assertEquals(MOCK_CUSTOMISATIONS_SHOW_DISTRIBUTION_MAP, customisationsDTO.isShowDistributionMap());
			assertEquals(MOCK_CUSTOMISATIONS_SHOW_FACEBOOK, customisationsDTO.isShowFacebook());
			assertEquals(MOCK_CUSTOMISATIONS_SHOW_GOOGLE, customisationsDTO.isShowGoogle());
			assertEquals(MOCK_CUSTOMISATIONS_SHOW_TWITTER, customisationsDTO.isShowTwitter());
			assertEquals(MOCK_CUSTOMISATIONS_SHOW_PROGRESSION_BAR, customisationsDTO.isShowProgressionBar());
			assertEquals(MOCK_CUSTOMISATIONS_SHOW_SOCIAL_MEDIA, customisationsDTO.isShowSocialMedia());
			assertEquals(MOCK_CUSTOMISATIONS_BACKGROUND, customisationsDTO.getBackground());
			assertEquals(MOCK_CUSTOMISATIONS_ALT_LOGO_TXT, customisationsDTO.getAlternateLogoText());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void cus02() throws Exception {
		// set customisations
		CustomisationsDTO customisationsDTO = new CustomisationsDTO();
		boolean newOptionalValidation = !MOCK_CUSTOMISATIONS_IS_OPTIONAL_VALIDATION;
		customisationsDTO.setOptionalValidation(newOptionalValidation);
		String newFacebookUrl = "http://www.facebook.com/" + System.currentTimeMillis();
		customisationsDTO.setFacebookUrl(newFacebookUrl);

		try {
			Response response = target(CUSTOMISATIONS + "/update").request()
					.header(HttpHeaders.AUTHORIZATION, BEARER + authorizationToken)
					.post(Entity.entity(customisationsDTO, MediaType.APPLICATION_JSON));
			assertEquals(Status.OK.getStatusCode(), response.getStatus());
			ApiResponse apiSuccess = response.readEntity(ApiResponse.class);
			assertEquals(ApiResponse.SUCCESS, apiSuccess.getStatus());
			assertEquals(Status.OK.getStatusCode(), apiSuccess.getCode());
			assertEquals(CustomisationsApi.CUS02 + "Succesful update", apiSuccess.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		// check that the update has been successful
		Response response = target(CUSTOMISATIONS + "/show").request().get(Response.class);
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		CustomisationsDTO updatedCustomisationsDTO = response.readEntity(CustomisationsDTO.class);
		assertEquals(newFacebookUrl, updatedCustomisationsDTO.getFacebookUrl());
		if (MOCK_CUSTOMISATIONS_IS_OPTIONAL_VALIDATION) {
			assertFalse(updatedCustomisationsDTO.isOptionalValidation());
		} else {
			assertTrue(updatedCustomisationsDTO.isOptionalValidation());
		}
		assertNotNull(updatedCustomisationsDTO.getLastUpdateSettings());
		String today = DateUtils.formatDate(new Date());
		assertEquals(today, updatedCustomisationsDTO.getLastUpdateSettings());
	}

	@Test
	public void removeUrls() throws Exception {
		// set customisations
		CustomisationsDTO customisationsDTO = new CustomisationsDTO();
		String emptyUrl = "";
		customisationsDTO.setFacebookUrl(emptyUrl);
		customisationsDTO.setGoogleUrl(emptyUrl);
		customisationsDTO.setTwitterUrl(emptyUrl);
		customisationsDTO.setCallbackUrl(emptyUrl);

		try {
			Response response = target(CUSTOMISATIONS + "/update").request()
					.header(HttpHeaders.AUTHORIZATION, BEARER + authorizationToken)
					.post(Entity.entity(customisationsDTO, MediaType.APPLICATION_JSON));
			assertEquals(Status.OK.getStatusCode(), response.getStatus());
			ApiResponse apiSuccess = response.readEntity(ApiResponse.class);
			assertEquals(ApiResponse.SUCCESS, apiSuccess.getStatus());
			assertEquals(Status.OK.getStatusCode(), apiSuccess.getCode());
			assertEquals(CustomisationsApi.CUS02 + "Succesful update", apiSuccess.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		// check that the update has been successful
		Response response = target(CUSTOMISATIONS + "/show").request().get(Response.class);
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		CustomisationsDTO updatedCustomisationsDTO = response.readEntity(CustomisationsDTO.class);
		assertEquals(emptyUrl, updatedCustomisationsDTO.getFacebookUrl());

	}

	@Test
	public void incompleteUrlsShouldBeFixed() throws Exception {
		// set customisations
		CustomisationsDTO customisationsDTO = new CustomisationsDTO();
		String incompleteUrl = "www.url.com";
		customisationsDTO.setCallbackUrl(incompleteUrl);

		try {
			Response response = target(CUSTOMISATIONS + "/update").request()
					.header(HttpHeaders.AUTHORIZATION, BEARER + authorizationToken)
					.post(Entity.entity(customisationsDTO, MediaType.APPLICATION_JSON));
			assertEquals(Status.OK.getStatusCode(), response.getStatus());
			ApiResponse apiSuccess = response.readEntity(ApiResponse.class);
			assertEquals(ApiResponse.SUCCESS, apiSuccess.getStatus());
			assertEquals(Status.OK.getStatusCode(), apiSuccess.getCode());
			assertEquals(CustomisationsApi.CUS02 + "Succesful update", apiSuccess.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		// check that the update has been successful
		Response response = target(CUSTOMISATIONS + "/show").request().get(Response.class);
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		CustomisationsDTO updatedCustomisationsDTO = response.readEntity(CustomisationsDTO.class);
		assertEquals(CustomisationsDTO.HTTP_PREFIX + incompleteUrl, updatedCustomisationsDTO.getCallbackUrl());

	}

}