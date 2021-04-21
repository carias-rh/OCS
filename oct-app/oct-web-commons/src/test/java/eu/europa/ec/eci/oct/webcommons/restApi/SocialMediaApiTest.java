package eu.europa.ec.eci.oct.webcommons.restApi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.eci.oct.entities.system.Language;
import eu.europa.ec.eci.oct.utils.DateUtils;
import eu.europa.ec.eci.oct.webcommons.services.api.RestApiParent;
import eu.europa.ec.eci.oct.webcommons.services.api.SocialMediaApi;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.ApiResponse;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.customisations.CustomisationsDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.socialMedia.SocialMediaEnum;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.socialMedia.SocialMediaMessageDTO;
import eu.europa.ec.eci.oct.webcommons.services.enums.LanguageEnum;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/rest-api-test.xml")
@Transactional(propagation = Propagation.REQUIRED)
public class SocialMediaApiTest extends RestApiTest {

	static final String SOCIAL_MEDIA = "/socialmedia";
	static final String CUSTOMISATIONS = "/customisations";

	@Test
	public void soc01() throws OCTException {
		// get social media message

		/*
		 * will test that what we have in the database, for each social media, for each
		 * language, is correctlty retrieved from the rest API
		 */
		List<Language> languages = systemManager.getAllLanguages();
		for (SocialMediaEnum socialMediaEnum : SocialMediaEnum.values()) {
			String socialMediaName = socialMediaEnum.getName();
			for (Language language : languages) {
				String languageCode = language.getCode();
				try {
					String socialMediaMessage = socialMediaService.getMessageForSocialMediaAndLanguage(socialMediaName,
							languageCode);
					if (socialMediaMessage == null) {
						/*
						 * db entry missing for language x and media y: EXPECTATION FAILED status
						 * expected
						 */
						Response response = target(SOCIAL_MEDIA + "/" + socialMediaName + "/" + languageCode).request()
								.get(Response.class);
						assertEquals(Status.OK.getStatusCode(), response.getStatus());
						continue;
					}
					Response response = target(SOCIAL_MEDIA + "/" + socialMediaName + "/" + languageCode).request()
							.get(Response.class);
					assertEquals(Status.OK.getStatusCode(), response.getStatus());
					SocialMediaMessageDTO socialMediaMessageDTO = response.readEntity(SocialMediaMessageDTO.class);
					assertEquals(socialMediaMessage, socialMediaMessageDTO.getMessage());
				} catch (Exception e) {
					e.printStackTrace();
					fail(e.getMessage());
				}
			}
		}
	}

	@Test
	public void soc02() {
		// update social media message

		// 1. FACEBOOK - FR
		SocialMediaMessageDTO newSocialMediaMessageFacebookFR = new SocialMediaMessageDTO();
		newSocialMediaMessageFacebookFR.setLanguageCode(LanguageEnum.FRENCH.getLangCode());
		String newFrenchFacebookMessage = "FR FACEBOOK" + System.currentTimeMillis();
		newSocialMediaMessageFacebookFR.setMessage(newFrenchFacebookMessage);
		newSocialMediaMessageFacebookFR.setSocialMedia(SocialMediaEnum.FACEBOOK.getName());
		try {
			Response frenchFacebookUpdateResponse = target(SOCIAL_MEDIA + "/update").request()
					.header(HttpHeaders.AUTHORIZATION, BEARER + authorizationToken)
					.post(Entity.entity(newSocialMediaMessageFacebookFR, MediaType.APPLICATION_JSON));
			assertEquals(Status.OK.getStatusCode(), frenchFacebookUpdateResponse.getStatus());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		// verify correct update
		Response responseUpdateFacebookFR = target(
				SOCIAL_MEDIA + "/" + SocialMediaEnum.FACEBOOK.getName() + "/" + LanguageEnum.FRENCH.getLangCode())
						.request().get(Response.class);
		assertEquals(Status.OK.getStatusCode(), responseUpdateFacebookFR.getStatus());
		SocialMediaMessageDTO frenchFacebookUpdated = responseUpdateFacebookFR.readEntity(SocialMediaMessageDTO.class);
		assertEquals(newFrenchFacebookMessage, frenchFacebookUpdated.getMessage());

		// 3. TWITTER - EN
		SocialMediaMessageDTO newSocialMediaMessageTwitterEn = new SocialMediaMessageDTO();
		newSocialMediaMessageTwitterEn.setLanguageCode(LanguageEnum.ENGLISH.getLangCode());
		String newEnglishTwitterMessage = "EN TWITTER " + System.currentTimeMillis();
		newSocialMediaMessageTwitterEn.setMessage(newEnglishTwitterMessage);
		newSocialMediaMessageTwitterEn.setSocialMedia(SocialMediaEnum.TWITTER.getName());

		try {
			Response englishTwitterUpdateResponse = target(SOCIAL_MEDIA + "/update").request()
					.header(HttpHeaders.AUTHORIZATION, BEARER + authorizationToken)
					.post(Entity.entity(newSocialMediaMessageTwitterEn, MediaType.APPLICATION_JSON));
			assertEquals(Status.OK.getStatusCode(), englishTwitterUpdateResponse.getStatus());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		// verify correct update
		Response responseTwitterEN = target(
				SOCIAL_MEDIA + "/" + SocialMediaEnum.TWITTER.getName() + "/" + LanguageEnum.ENGLISH.getLangCode())
						.request().get(Response.class);
		assertEquals(Status.OK.getStatusCode(), responseTwitterEN.getStatus());
		SocialMediaMessageDTO englishTwitterUpdated = responseTwitterEN.readEntity(SocialMediaMessageDTO.class);
		assertEquals(newEnglishTwitterMessage, englishTwitterUpdated.getMessage());

		// 3. CALLFORACTION - ES
		SocialMediaMessageDTO newSocialMediaMessageCallForActionEs = new SocialMediaMessageDTO();
		newSocialMediaMessageCallForActionEs.setLanguageCode(LanguageEnum.SPANISH.getLangCode());
		String newSpanishCallForActionMessage1 = "ES CALL FOR ACTION " + System.currentTimeMillis();
		newSocialMediaMessageCallForActionEs.setMessage(newSpanishCallForActionMessage1);
		newSocialMediaMessageCallForActionEs.setSocialMedia(SocialMediaEnum.CALL_FOR_ACTION.getName());

		try {
			Response spanishCallForActionUpdateResponse = target(SOCIAL_MEDIA + "/update").request()
					.header(HttpHeaders.AUTHORIZATION, BEARER + authorizationToken)
					.post(Entity.entity(newSocialMediaMessageCallForActionEs, MediaType.APPLICATION_JSON));
			assertEquals(Status.OK.getStatusCode(), spanishCallForActionUpdateResponse.getStatus());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		// verify correct update
		Response responseCallForActionES = target(SOCIAL_MEDIA + "/" + SocialMediaEnum.CALL_FOR_ACTION.getName() + "/"
				+ LanguageEnum.SPANISH.getLangCode()).request().get(Response.class);
		assertEquals(Status.OK.getStatusCode(), responseCallForActionES.getStatus());
		SocialMediaMessageDTO spanishCallForActionUpdated = responseCallForActionES
				.readEntity(SocialMediaMessageDTO.class);
		assertEquals(newSpanishCallForActionMessage1, spanishCallForActionUpdated.getMessage());

		// 4. unexistent one: we expect not found response
		SocialMediaMessageDTO newSocialMediaMessageXX = new SocialMediaMessageDTO();
		newSocialMediaMessageXX.setLanguageCode(LanguageEnum.GAELIC.getLangCode());
		String newXXMessage = "XX ";
		newSocialMediaMessageXX.setMessage(newXXMessage);
		newSocialMediaMessageXX.setSocialMedia("xx");
		try {
			Response xxResponse = target(SOCIAL_MEDIA + "/update").request()
					.header(HttpHeaders.AUTHORIZATION, BEARER + authorizationToken)
					.post(Entity.entity(newSocialMediaMessageXX, MediaType.APPLICATION_JSON));
			assertEquals(Status.EXPECTATION_FAILED.getStatusCode(), xxResponse.getStatus());
			ApiResponse notFoundResponse = xxResponse.readEntity(ApiResponse.class);
			assertEquals(ApiResponse.ERROR, notFoundResponse.getStatus());
			assertEquals(SocialMediaApi.SOC02 + RestApiParent.INPUT_PARAMS_EXPECTATION_FAILED + "xx",
					notFoundResponse.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		// check that the update has been successful
		Response response = target(CUSTOMISATIONS + "/show").request().get(Response.class);
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		CustomisationsDTO updatedCustomisationsDTO = response.readEntity(CustomisationsDTO.class);
		assertNotNull(updatedCustomisationsDTO.getLastUpdateSocial());
		String today = DateUtils.formatDate(new Date());
		assertEquals(today, updatedCustomisationsDTO.getLastUpdateSocial());

	}

}