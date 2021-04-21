package eu.europa.ec.eci.oct.webcommons.services.api;

import static eu.europa.ec.eci.oct.webcommons.services.api.domain.ApiResponse.buildError;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.stereotype.Service;

import eu.europa.ec.eci.oct.entities.admin.Contact;
import eu.europa.ec.eci.oct.entities.admin.InitiativeDescription;
import eu.europa.ec.eci.oct.entities.system.Language;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.contact.ContactDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.contact.ContactsDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.initiative.InitiativeDescriptionsDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.language.LanguageDTOlist;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTIllegalOperationException;

@Service
@Path("/initiative")
public class InitiativeApi extends RestApiParent {

	public static String INI01 = "INI01: ";
	public static String INI02 = "INI02: ";
	public static String INI03 = "INI03: ";
	public static String INI04 = "INI04: ";
	public static String INI05 = "INI05: ";
	public static String INI06 = "INI06: ";
	public static String INI08 = "INI08: ";
	public static String INI09 = "INI09: ";
	public static String formatExceptionMessage = "oct.s4.xml.upload.error.invalid.format";
	public static String illegalOperationExceptionMessage = "oct.s4.xml.upload.error.illegal.operation";

	@Secured
	@POST
	@Path("/insertXML")
	@Consumes({ MediaType.MULTIPART_FORM_DATA })
	@Produces(MediaType.APPLICATION_JSON)
	public Response insertInitiativeDescription(@FormDataParam("file") InputStream inputStream, @FormDataParam("file") FormDataContentDisposition fileMetaData)
			throws Exception {

		try {
			InitiativeDescriptionsDTO initiativeDescriptionsDTO = initiativeService.insertInitiativeDescriptionXML(inputStream, fileMetaData);
			return Response.status(Status.OK).entity(initiativeDescriptionsDTO)
					.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
		} catch (OCTException e) {
			String errorMessage = e.getMessage();
			logger.error(errorMessage);
			Status status = Status.INTERNAL_SERVER_ERROR;
			if (e instanceof OCTIllegalOperationException) {
				apiResponse = buildError(Status.CONFLICT.getStatusCode(), illegalOperationExceptionMessage);
				status = Status.CONFLICT;
			} else {
				apiResponse = buildError(status.getStatusCode(), formatExceptionMessage);
			}
			return Response.status(status).entity(apiResponse).header("Cache-Control", "no-store, no-cache, must-revalidate").build();
		}
	}

	@GET
	@Path("/description")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDefaultInitiative() {
		logger.info("Initiative api: getting default description");
		try {
			InitiativeDescription defaultDescription = initiativeService.getDefaultDescription();
			if (defaultDescription == null) {
				apiResponse = buildError(Status.NO_CONTENT.getStatusCode(),
						INI02 + Status.NO_CONTENT.getReasonPhrase());
				// return Response.status(Status.NO_CONTENT).entity(apiResponse).build();
				// //pending request with Edge
				return Response.status(Status.NO_CONTENT).build();
			}
			List<InitiativeDescription> initiativeDescriptionsDAO = new ArrayList<InitiativeDescription>();
			initiativeDescriptionsDAO.add(defaultDescription);
			initiativeDescriptionsDTO = initiativeDescriptionTransformer.transform(initiativeDescriptionsDAO);
			return Response.status(Status.OK).entity(initiativeDescriptionsDTO)
					.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					INI02 + "Unable to retrieve default description");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse)
					.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
		}
	}

	@GET
	@Path("/alldescriptions")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllInitiativeDescriptions() {
		try {
			List<InitiativeDescription> initiativeDescriptionsDAO = initiativeService.getDescriptions();
			initiativeDescriptionsDTO = initiativeDescriptionTransformer.transform(initiativeDescriptionsDAO);
			return Response.status(Status.OK).entity(initiativeDescriptionsDTO)
					.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
		} catch (OCTException e) {
			logger.error(e.getMessage());
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					INI03 + "Unable to retrieve all initiative descriptions");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse)
					.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
		}
	}

	@GET
	@Path("/descriptionById/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getInitiativeDescriptionById(@PathParam("id") String id) {

		// validation of the input
		boolean emptyInput = StringUtils.isBlank(id);
		boolean NaN = false;
		long initiativeDescriptionId = -1;
		try {
			initiativeDescriptionId = Long.parseLong(id);
		} catch (NumberFormatException nfe) {
			NaN = true;
		}
		if (NaN || emptyInput) {
			apiResponse = buildError(Status.EXPECTATION_FAILED.getStatusCode(),
					INI04 + INPUT_PARAMS_EXPECTATION_FAILED);
			return Response.status(Status.EXPECTATION_FAILED).entity(apiResponse).build();
		}

		InitiativeDescription initiativeDescriptionById = null;
		try {
			initiativeDescriptionById = initiativeService.getDescriptionById(initiativeDescriptionId);
			if (initiativeDescriptionById == null) {
				apiResponse = buildError(Status.NO_CONTENT.getStatusCode(), INI04 + OBJECT_NOT_FOUND_FOR_PARAM + id);
				return Response.status(Status.NO_CONTENT).entity(apiResponse).build();
			}
			List<InitiativeDescription> initiativeDescriptionsDAO = new ArrayList<InitiativeDescription>();
			initiativeDescriptionsDAO.add(initiativeDescriptionById);
			initiativeDescriptionsDTO = initiativeDescriptionTransformer.transform(initiativeDescriptionsDAO);
			return Response.status(Status.OK).entity(initiativeDescriptionsDTO)
					.header("Cache-Control", "no-store, no-cache, must-revalidate").build();

		} catch (OCTException e) {
			logger.error(e.getMessage());
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					INI04 + "Unable to retrieve description by id");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse)
					.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
		}
	}

	@GET
	@Path("/descriptionByLang/{languageCode}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getInitiativeDescriptionByLanguageCode(@PathParam("languageCode") String languageCode) {

		// validation of the input
		if (StringUtils.isBlank(languageCode) || languageCode.length() != Language.DEFAULT_CODE_LENGTH) {
			apiResponse = buildError(Status.EXPECTATION_FAILED.getStatusCode(),
					INI05 + INPUT_PARAMS_EXPECTATION_FAILED + languageCode);
			return Response.status(Status.EXPECTATION_FAILED).entity(apiResponse).build();
		}

		InitiativeDescription initiativeDescriptionByLang = null;
		try {
			initiativeDescriptionByLang = initiativeService.getDescriptionByLanguageCode(languageCode);
			if (initiativeDescriptionByLang == null) {
				apiResponse = buildError(Status.NO_CONTENT.getStatusCode(),
						INI05 + OBJECT_NOT_FOUND_FOR_PARAM + languageCode);
				return Response.status(Status.NO_CONTENT).entity(apiResponse).build();
			}
			List<InitiativeDescription> initiativeDescriptionsDAO = new ArrayList<InitiativeDescription>();
			initiativeDescriptionsDAO.add(initiativeDescriptionByLang);
			initiativeDescriptionsDTO = initiativeDescriptionTransformer.transform(initiativeDescriptionsDAO);
			return Response.status(Status.OK).entity(initiativeDescriptionsDTO)
					.header("Cache-Control", "no-store, no-cache, must-revalidate").build();

		} catch (OCTException e) {
			logger.error(e.getMessage());
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					INI05 + "Unable to retrieve description by Lang code");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse)
					.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
		}
	}

	@GET
	@Path("/contacts")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getInitiativeContacts() {
		try {
			List<Contact> contactsDAO = contactService.getAllContacts();
			List<ContactDTO> contactsDTOlist = contactTransformer.transformListDAO(contactsDAO);
			ContactsDTO contactsDTO = new ContactsDTO();
			contactsDTO.setContactDTOlist(contactsDTOlist);
			return Response.status(Status.OK).entity(contactsDTO).build();
		} catch (Exception e) {
			logger.error(e.getMessage());
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					INI06 + "Unable to retrieve contacts");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse)
					.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
		}
	}

	@GET
	@Path("/daysleft")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDaysLeft() {
		try {
			long daysLeft = initiativeService.daysLeft();
			longDTO.setValue(daysLeft);
			return Response.status(Status.OK).entity(longDTO).build();
		} catch (Exception e) {
			logger.error(e.getMessage());
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					INI08 + "Unable to retrieve daysLeft");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse)
					.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
		}
	}

	@GET
	@Path("/languages")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLanguagesForDescriptions() {
		try {
			List<Language> languages = initiativeService.getLanguagesForAvailableDescriptions();
			LanguageDTOlist languagesDTO = languageTransformer.transformLanguages(languages);
			return Response.status(Status.OK).entity(languagesDTO).build();
		} catch (Exception e) {
			logger.error(e.getMessage());
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					INI09 + "Unable to retrieve languages for descriptions");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse)
					.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
		}
	}

}
