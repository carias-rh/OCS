package eu.europa.ec.eci.oct.webcommons.restApi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europa.ec.eci.oct.webcommons.services.api.domain.FileType;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.file.ImageDTO;

/**
 * User: franzmh date: 15/11/16
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/rest-api-test.xml")
public class CustomFileApiTest extends RestApiTest {
	static final String CUSTOM = "/customfiles";
	static final String DESTINATION_TEST_FOLDER = "./target/sys_pref_file_store/";

	@Test
	public void uploadForm() throws Exception {
		// Invalid file type

		// Upload logo
		File resourceFile = new File(this.getClass().getResource("/file/eu.png").getFile());
		logger.info("logo filename found : " + resourceFile);
		FileDataBodyPart filePart = new FileDataBodyPart("file", resourceFile);
		FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
		FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.field("type", FileType.LOGO.fileType())
				.bodyPart(filePart);
		Response response = target(CUSTOM + "/uploadForm").request(MediaType.APPLICATION_JSON_TYPE)
				.header(HttpHeaders.AUTHORIZATION, BEARER + authorizationToken)
				.post(Entity.entity(multipart, multipart.getMediaType()));

		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		FileUtils.contentEquals(resourceFile, new File(DESTINATION_TEST_FOLDER + resourceFile.getName()));
	}

	@Test
	public void downloadLogo() throws Exception {

		// Upload logo
		File resourceFile = new File(this.getClass().getResource("/file/eu.png").getFile());
		logger.info("logo filename found : " + resourceFile);
		FileDataBodyPart filePart = new FileDataBodyPart("file", resourceFile);
		FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
		FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.field("type", FileType.LOGO.fileType())
				.bodyPart(filePart);
		Response response = target(CUSTOM + "/uploadForm").request(MediaType.APPLICATION_JSON_TYPE)
				.header(HttpHeaders.AUTHORIZATION, BEARER + authorizationToken)
				.post(Entity.entity(multipart, multipart.getMediaType()));

		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		FileUtils.contentEquals(resourceFile, new File(DESTINATION_TEST_FOLDER + resourceFile.getName()));

		// Download the logo
		String type = FileType.LOGO.fileType();
		response = target(CUSTOM + "/download/" + type).request().get(Response.class);
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		String expectedFileName = FileType.LOGO.name() + ".png";
		assertEquals("attachment; filename = " + expectedFileName,
				response.getHeaders().get("content-disposition").get(0));
		InputStream inputStream = response.readEntity(InputStream.class);
		assertNotNull(inputStream);
		FileUtils.copyInputStreamToFile(inputStream, new File(DESTINATION_TEST_FOLDER + "file_to_test.png"));
		FileUtils.contentEquals(new File(this.getClass().getResource("/file/eu.png").getFile()),
				new File(DESTINATION_TEST_FOLDER + "file_to_test.png"));
		FileUtils.deleteQuietly(new File(DESTINATION_TEST_FOLDER + "file_to_test.png"));
		FileUtils.deleteQuietly(new File(DESTINATION_TEST_FOLDER + expectedFileName));
	}

	@Test
	public void testDownloadReceipt_failWrongURLNoLanguage() throws Exception {
		// test with an unexisting signature
		String unvalidUUID = "1233545232";
		Response badResponse = target(CUSTOM + "/receipt/" + unvalidUUID).request().get(Response.class);
		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), badResponse.getStatus());
	}

	@Test
	public void testDownloadReceipt_failWrongLanguage() throws Exception {
		String unvalidUUID = "1233545232";
		Response badResponse = target(CUSTOM + "/receipt/" + unvalidUUID + "/ru").request().get(Response.class);
		assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), badResponse.getStatus());
	}

	@Test
	public void testDownloadReceipt_failNonExistentReceipt() throws Exception {
		String unvalidUUID = "1233545232";
		Response badResponse = target(CUSTOM + "/receipt/" + unvalidUUID + "/en").request().get(Response.class);
		assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), badResponse.getStatus());
	}

	@Test
	public void testDownloadReceipt_ok() throws Exception {
		// test with an existing one
		Response response = target(CUSTOM + "/receipt/" + MOCK_SIGNATURE1_UUID + "/en").request().get(Response.class);
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals("attachment; filename = " + MOCK_REGISTRATION_NUMBER + "-receipt.pdf",
				response.getHeaders().get("content-disposition").get(0));
		InputStream inputStream = response.readEntity(InputStream.class);
		assertNotNull(inputStream);
		FileUtils.copyInputStreamToFile(inputStream,
				new File(DESTINATION_TEST_FOLDER + MOCK_REGISTRATION_NUMBER + "-receipt_to_test.pdf"));
		assertTrue(new File(DESTINATION_TEST_FOLDER + MOCK_REGISTRATION_NUMBER + "-receipt_to_test.pdf").exists());
		assertTrue(FileUtils
				.sizeOf(new File(DESTINATION_TEST_FOLDER + MOCK_REGISTRATION_NUMBER + "-receipt_to_test.pdf")) > 1300);
		FileUtils.deleteQuietly(new File(DESTINATION_TEST_FOLDER + MOCK_REGISTRATION_NUMBER + "-receipt_to_test.pdf"));
	}

	@Test
	public void testDownloadReceipt_fontCyrillicOK() throws Exception {
		// test with an existing one
		Response response = target(CUSTOM + "/receipt/" + MOCK_SIGNATURE1_UUID + "/bg").request().get(Response.class);
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals("attachment; filename = " + MOCK_REGISTRATION_NUMBER + "-receipt.pdf",
				response.getHeaders().get("content-disposition").get(0));
		InputStream inputStream = response.readEntity(InputStream.class);
		assertNotNull(inputStream);
		FileUtils.copyInputStreamToFile(inputStream,
				new File(DESTINATION_TEST_FOLDER + MOCK_REGISTRATION_NUMBER + "-receipt_to_test_bg.pdf"));
		assertTrue(new File(DESTINATION_TEST_FOLDER + MOCK_REGISTRATION_NUMBER + "-receipt_to_test_bg.pdf").exists());
		assertTrue(FileUtils.sizeOf(
				new File(DESTINATION_TEST_FOLDER + MOCK_REGISTRATION_NUMBER + "-receipt_to_test_bg.pdf")) > 1300);
		FileUtils.deleteQuietly(
				new File(DESTINATION_TEST_FOLDER + MOCK_REGISTRATION_NUMBER + "-receipt_to_test_bg.pdf"));
	}

	@Test
	public void downloadEncodedLogo() throws Exception {

		// Upload logo
		File resourceFile = new File(this.getClass().getResource("/file/eu.png").getFile());
		logger.info("logo filename found : " + resourceFile);
		FileDataBodyPart filePart = new FileDataBodyPart("file", resourceFile);
		FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
		FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.field("type", FileType.LOGO.fileType())
				.bodyPart(filePart);
		Response response = target(CUSTOM + "/uploadForm").request(MediaType.APPLICATION_JSON_TYPE)
				.header(HttpHeaders.AUTHORIZATION, BEARER + authorizationToken)
				.post(Entity.entity(multipart, multipart.getMediaType()));

		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		FileUtils.contentEquals(resourceFile, new File(DESTINATION_TEST_FOLDER + resourceFile.getName()));

		response = target(CUSTOM + "/logo").request().get(Response.class);
		ImageDTO imageDTO = response.readEntity(ImageDTO.class);
		FileUtils.writeByteArrayToFile(new File(DESTINATION_TEST_FOLDER + "to_test_" + imageDTO.getName()),
				Base64.decodeBase64(imageDTO.getData()));
		assertNotNull(imageDTO.getName());
		String expectedFileName = FileType.LOGO.name() + ".png";
		assertEquals(expectedFileName, imageDTO.getName());
		assertNotNull(imageDTO.getData());
		assertTrue(FileUtils.contentEquals(new File(this.getClass().getResource("/file/eu.png").getFile()),
				new File(DESTINATION_TEST_FOLDER + expectedFileName)));
		FileUtils.deleteQuietly(new File(DESTINATION_TEST_FOLDER + expectedFileName));
		FileUtils.deleteQuietly(new File(DESTINATION_TEST_FOLDER + expectedFileName));
	}

}
