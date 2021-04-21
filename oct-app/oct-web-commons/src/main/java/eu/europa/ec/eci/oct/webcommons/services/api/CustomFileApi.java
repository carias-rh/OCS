package eu.europa.ec.eci.oct.webcommons.services.api;

import static eu.europa.ec.eci.oct.webcommons.services.api.domain.ApiResponse.buildError;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

import eu.europa.ec.eci.oct.entities.admin.InitiativeDescription;
import eu.europa.ec.eci.oct.entities.signature.Signature;
import eu.europa.ec.eci.oct.entities.system.Language;
import eu.europa.ec.eci.oct.utils.DateUtils;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.ApiResponse;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.FileType;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.file.ImageDTO;
import eu.europa.ec.eci.oct.webcommons.services.configuration.ConfigurationService;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;
import eu.europa.ec.eci.oct.webcommons.services.initiative.InitiativeService;
import eu.europa.ec.eci.oct.webcommons.services.system.SystemManager;
import eu.europa.ec.eci.oct.webcommons.utils.FileUtils;

@Service
@Scope("prototype")
@Path("/customfiles")
public class CustomFileApi extends RestApiParent {
	// private static final String TAG = "*** [CustomFileApi]: ";
	public static String FIL01 = "FIL01: ";
	public static String FIL02 = "FIL02: ";
	public static String FIL03 = "FIL03: ";
	public static String FIL04 = "FIL04: ";
	@Autowired
	ImageDTO imageDTO;
	@Autowired
	@Qualifier("systemManager")
	private SystemManager systemManager;
	@Autowired
	private ConfigurationService configurationService;
	@Autowired
	@Qualifier("initiativeService")
	private InitiativeService initiativeService;

	private void exportSignatureReceipt(OutputStream output, String signatureUUID, Date dateOfSignature,
			Language language) throws Exception {

		SimpleDateFormat formatter = new SimpleDateFormat(DateUtils.DEFAULT_DATE_FORMAT);
		String date = formatter.format(dateOfSignature);

		String title = null;
		InitiativeDescription initiativeDescription = initiativeService
				.getDescriptionByLanguageCode(language.getCode());
		if (initiativeDescription == null) {
			title = initiativeService.getDefaultDescription().getTitle();
		} else {
			title = initiativeDescription.getTitle();
		}

		BaseFont font = BaseFont.createFont("fonts/ARIAL.TTF", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
		Font initiativeTitleFont = new Font(font, 20, Font.BOLD);
		Font statementFont = new Font(font, 18, Font.NORMAL);
		Font txtUnderlineFont = new Font(font, 16, Font.UNDERLINE);
		Font txtBoldFont = new Font(font, 16, Font.BOLD);
		Font txtWarningFont = new Font(font, 18, Font.BOLD);
		com.itextpdf.text.Document document = new com.itextpdf.text.Document();

		PdfWriter.getInstance(document, output);
		document.open();
		Paragraph preface = new Paragraph();
		preface.add(new Paragraph(" "));
		preface.add(new Paragraph(title, initiativeTitleFont));
		preface.add(new Paragraph(""));
		preface.add(
				new Paragraph(translationService.getTranslation("public.receipt.pdf.title", language), statementFont));
		preface.add(new Paragraph(" "));
		preface.add(new Paragraph(translationService.getTranslation("public.receipt.pdf.statement", language),
				txtWarningFont));
		preface.add(new Paragraph(" "));
		preface.add(new Paragraph(translationService.getTranslation("public.receipt.pdf.date", language),
				txtUnderlineFont));
		preface.add(new Paragraph(""));
		preface.add(new Paragraph(date, txtBoldFont));
		preface.add(new Paragraph(""));
		preface.add(new Paragraph(translationService.getTranslation("public.receipt.pdf.signature.id", language),
				txtUnderlineFont));
		preface.add(new Paragraph(""));
		preface.add(new Paragraph(signatureUUID, txtBoldFont));
		document.add(preface);
		document.close();
	}

	// FIL01
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path("/download/{type}")
	public Response downloadFile(@PathParam("type") String type) throws Exception {
		String path;
		String filename;
		final String storePath = systemManager.getSystemPreferences().getFileStoragePath();

		if (FileType.LOGO.fileType().equalsIgnoreCase(type)) {
			filename = configurationService.getConfigurationParameter(ConfigurationService.Parameter.LOGO_PATH)
					.getValue();
			path = storePath + filename;
		} else if (FileType.CERTIFICATE.fileType().equalsIgnoreCase(type)) {
			try {
				filename = systemManager.getSystemPreferences().getCertFileName();
				path = systemManager.getSystemPreferences().getFileStoragePath() + filename;
			} catch (Exception e) {
				logger.error(FIL01 + "file download error: cannot retrieve path name info");
				apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
						FIL01 + "cannot retrieve path name info");
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse).build();
			}
		} else {
			logger.error(FIL01 + "file download error: invalid file type");
			apiResponse = buildError(Status.EXPECTATION_FAILED.getStatusCode(),
					FIL01 + "The specified type is incorrect");
			return Response.status(Status.EXPECTATION_FAILED).entity(apiResponse).build();
		}

		final String fullPath = path;
		final String fullName = filename;
		try {
			StreamingOutput fileStream = new StreamingOutput() {
				@Override
				public void write(java.io.OutputStream output) throws IOException, WebApplicationException {
					try {
						java.nio.file.Path path = Paths.get(fullPath);
						byte[] data = Files.readAllBytes(path);
						output.write(data);
						output.flush();
					} catch (Exception e) {
						logger.error(FIL01 + "file not found: " + e.getMessage());
						throw new WebApplicationException("File Not Found: " + e.getMessage());
					}
				}
			};

			return Response.ok(fileStream, MediaType.APPLICATION_OCTET_STREAM)
					.header("content-disposition", "attachment; filename = " + fullName).build();
		} catch (Exception e) {
			logger.error(FIL01 + "file download error: file not found");
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					FIL01 + "file download error: file not found");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse).build();
		}
	}

	// FIL02
	@Secured
	@POST
	@Path("/uploadForm")
	@Consumes({ MediaType.MULTIPART_FORM_DATA })
	public Response uploadForm(@FormDataParam("file") InputStream inputStream,
			@FormDataParam("file") FormDataContentDisposition fileMetaData, @FormDataParam("type") String type)
			throws Exception {
		FileType fileType = null;

		try {
			fileType = FileType.getFileTypeEnumFromTypeName(type);
		} catch (Exception e) {
			apiResponse = buildError(Status.EXPECTATION_FAILED.getStatusCode(),
					FIL02 + "file upload error: invalid file type");
			return Response.status(Status.EXPECTATION_FAILED).entity(apiResponse).build();
		}
		String fileName = fileMetaData.getFileName();
		if (StringUtils.isBlank(type) || FileType.isInvalidType(type)
				|| !FileUtils.isAcceptedExtension(fileName, fileType)) {
			logger.error(FIL02 + "file upload error: invalid file type");
			apiResponse = buildError(Status.EXPECTATION_FAILED.getStatusCode(),
					FIL02 + "file upload error: invalid file type");
			return Response.status(Status.EXPECTATION_FAILED).entity(apiResponse).build();
		}

		try {
			String storePath = systemManager.getSystemPreferences().getFileStoragePath();
			systemManager.saveFileTo(storePath, fileName, inputStream, fileType);

		} catch (IOException e) {
			logger.error(FIL02 + "file save error " + e);
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					FIL02 + "Upload file error: unable to save the file");

			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse).build();
		} catch (OCTException octe) {
			logger.error(FIL02 + "file metadata save error " + octe);
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					FIL02 + "Upload file error: unable to save the file metadata");

			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse).build();

		}

		apiResponse = ApiResponse.buildSuccess(Status.OK.getStatusCode(), FIL02 + "File uploaded successfully");
		return Response.ok("Data uploaded successfully !!")
				.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
	}

	// FIL03
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path("/receipt/{identifier}/{languageCode}")
	public Response downloadReceipt(@PathParam("identifier") String identifier,
			@PathParam("languageCode") String languageCode) throws Exception {

		if (StringUtils.isBlank(identifier) || StringUtils.isBlank(languageCode)) {
			apiResponse = buildError(Status.EXPECTATION_FAILED.getStatusCode(), FIL03 + "Bad input param.");
			return Response.status(Status.EXPECTATION_FAILED).entity(apiResponse).build();
		}

		final Language language = systemManager.getLanguageByCode(languageCode);
		if (language == null) {
			apiResponse = buildError(Status.EXPECTATION_FAILED.getStatusCode(), FIL03 + "Bad language input param.");
			return Response.status(Status.EXPECTATION_FAILED).entity(apiResponse).build();
		}

		Signature signatureByUUID = null;
		try {
			signatureByUUID = signatureService.findByUuid(identifier);
			if (signatureByUUID == null) {
				apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
						FIL03 + "No signature found for identifier " + identifier);
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse).build();
			}
		} catch (Exception e) {
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					FIL03 + "No signature found for identifier " + identifier);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse).build();
		}

		final String signatureUUID = identifier;
		final Date dateOfSignature = signatureByUUID.getDateOfSignature();
		StreamingOutput fileStream = null;
		try {
			fileStream = new StreamingOutput() {
				@Override
				public void write(java.io.OutputStream output) throws IOException, WebApplicationException {
					try {
						exportSignatureReceipt(output, signatureUUID, dateOfSignature, language);
					} catch (Exception e) {
						throw new WebApplicationException("File Not Found !!");
					}
				}
			};
		} catch (WebApplicationException e) {
			logger.error(FIL03 + "Receipt download error " + e);
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(), FIL03 + "Receipt download error");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse).build();
		}

		// TODO: falloda definition of the filename and translate it in the relative
		// language
		String filename = systemManager.getSystemPreferences().getRegistrationNumber();
		filename += "-receipt.pdf";
		return Response.ok(fileStream, MediaType.APPLICATION_OCTET_STREAM)
				.header("content-disposition", "attachment; filename = " + filename)
				.header("application/pdf", "charset=UTF-8").build();
	}

	// FIL04
	@Secured
	@GET
	@Path("/logo")
	@Produces(MediaType.APPLICATION_JSON)
	public Response downloadEncodedLogo() throws Exception {
		String filename = null;
		byte[] encoded = null;
		try {
			filename = configurationService.getConfigurationParameter(ConfigurationService.Parameter.LOGO_PATH)
					.getValue();

			String path = systemManager.getSystemPreferences().getFileStoragePath() + filename;
			File logoFile = new File(path);
			InputStream inputStream = new FileInputStream(logoFile);
			long length = logoFile.length();
			byte[] bytes = new byte[(int) length];

			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length && (numRead = inputStream.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}
			inputStream.close();
			encoded = Base64.encodeBase64(bytes);
		} catch (Exception e) {
			logger.error(FIL04 + "logo image download error " + e);
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(), FIL04 + "logo image download error");

			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse).build();
		}

		imageDTO.setName(filename);
		imageDTO.setData(encoded);
		return Response.status(Status.OK).entity(imageDTO)
				.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
	}

}
