package eu.europa.ec.eci.oct.webcommons.restApi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europa.ec.eci.oct.webcommons.services.api.domain.captcha.CaptchaDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.captcha.CaptchaValidationDTO;
import eu.europa.ec.eci.oct.webcommons.services.captcha.CaptchaService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/rest-api-test.xml")
public class CaptchaApiTest extends RestApiTest {

	static final String CAPTCHA = "/captcha";

	static final String TMP_IMAGE_PATH_FILENAME = "./captcha_test.jpeg";
	static final String TMP_AUDIO_PATH_FILENAME = "./captcha_test.wav";

	static boolean CREATE_FILE_FOR_TEST = true;

	/**
	 * Check if gets data and is save in a file to check the validity
	 * 
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 */

	@Test
	public void getImageCaptcha() throws IOException, UnsupportedAudioFileException {
		Response response = target(CAPTCHA + "/image").request().get(Response.class);
		CaptchaDTO captchaDTO = response.readEntity(CaptchaDTO.class);
		try {
			if (CREATE_FILE_FOR_TEST) {
				OutputStream out = null;
				try {
					out = new BufferedOutputStream(new FileOutputStream(TMP_IMAGE_PATH_FILENAME));
					out.write(captchaDTO.getData());
				} catch (Exception e) {
					e.printStackTrace();
					fail(e.getMessage());
				} finally {
					if (out != null) {
						out.close();
					}
					FileUtils.deleteQuietly(new File(TMP_IMAGE_PATH_FILENAME));
				}
			}
			assertNotNull(captchaDTO);
			assertNotNull(captchaDTO.getId());
			assertNotNull(captchaDTO.getData());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Check if gets data and is save in a file to check the validity
	 * 
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 */
	@Test
	public void getAudioCaptcha() throws IOException, UnsupportedAudioFileException {

		Response response = target(CAPTCHA + "/audio").request().get(Response.class);
		CaptchaDTO captchaDTO = response.readEntity(CaptchaDTO.class);

		if (CREATE_FILE_FOR_TEST) {
			OutputStream out = null;
			try {
				out = new BufferedOutputStream(new FileOutputStream(TMP_AUDIO_PATH_FILENAME));
				out.write(captchaDTO.getData());
			} finally {
				if (out != null) {
					out.close();
				}
				FileUtils.deleteQuietly(new File(TMP_AUDIO_PATH_FILENAME));

			}
		}

		assertNotNull(captchaDTO);
		assertNotNull(captchaDTO.getId());
		assertNotNull(captchaDTO.getData());
	}

	@Test
	public void validateBlankParamenters() {
		CaptchaValidationDTO dto = new CaptchaValidationDTO();
		dto.setId("");
		dto.setType("");
		dto.setValue("");
		Response response = target(CAPTCHA + "/validate").request().post(Entity.entity(dto, MediaType.APPLICATION_JSON));
		assertEquals(Response.Status.EXPECTATION_FAILED.getStatusCode(), response.getStatus());
	}

	@Test
	public void validateNullParamenters() {
		CaptchaValidationDTO dto = new CaptchaValidationDTO();
		dto.setId(null);
		dto.setType(null);
		dto.setValue(null);
		Response response = target(CAPTCHA + "/validate").request().post(Entity.entity(dto, MediaType.APPLICATION_JSON));
		assertEquals(Response.Status.EXPECTATION_FAILED.getStatusCode(), response.getStatus());
	}

	@Test
	public void validateWrongTypeParamenters() {
		CaptchaValidationDTO dto = new CaptchaValidationDTO();
		dto.setId("id");
		dto.setType("source");
		dto.setValue("value");
		Response response = target(CAPTCHA + "/validate").request().post(Entity.entity(dto, MediaType.APPLICATION_JSON));
		assertEquals(Response.Status.EXPECTATION_FAILED.getStatusCode(), response.getStatus());
	}

	@Test
	public void validateMissingValueParamenters() {
		CaptchaValidationDTO dto = new CaptchaValidationDTO();
		dto.setId("id");
		dto.setType("source");
		dto.setValue(null);
		Response response = target(CAPTCHA + "/validate").request().post(Entity.entity(dto, MediaType.APPLICATION_JSON));
		assertEquals(Response.Status.EXPECTATION_FAILED.getStatusCode(), response.getStatus());
	}

	@Test
	public void validateImageCaptcha() {
		CaptchaValidationDTO dto = new CaptchaValidationDTO();
		dto.setId("id");
		dto.setType(CaptchaService.CAPTCHA_IMAGE_TYPE);
		dto.setValue("A Wrong Captcha Resolution");
		Response response = target(CAPTCHA + "/validate").request().post(Entity.entity(dto, MediaType.APPLICATION_JSON));
		if (captchaService.isCaptchaEnabled()) {
			assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
		} else {
			assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		}
	}

	@Test
	public void validateAudioCaptcha() {
		CaptchaValidationDTO dto = new CaptchaValidationDTO();
		dto.setId("id");
		dto.setType(CaptchaService.CAPTCHA_AUDIO_TYPE);
		dto.setValue("aValue");
		Response response = target(CAPTCHA + "/validate").request().post(Entity.entity(dto, MediaType.APPLICATION_JSON));
		if (captchaService.isCaptchaEnabled()) {
			assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
		} else {
			assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		}
	}
}
