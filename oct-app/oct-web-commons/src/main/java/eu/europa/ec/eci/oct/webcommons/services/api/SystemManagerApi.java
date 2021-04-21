package eu.europa.ec.eci.oct.webcommons.services.api;

import static eu.europa.ec.eci.oct.webcommons.services.api.domain.ApiResponse.buildError;
import static eu.europa.ec.eci.oct.webcommons.services.api.domain.ApiResponse.buildSuccess;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import eu.europa.ec.eci.oct.entities.CountryProperty;
import eu.europa.ec.eci.oct.entities.admin.StepState;
import eu.europa.ec.eci.oct.entities.admin.SystemPreferences;
import eu.europa.ec.eci.oct.entities.admin.SystemState;
import eu.europa.ec.eci.oct.entities.system.Country;
import eu.europa.ec.eci.oct.entities.system.Language;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.supportForm.SupportFormDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.systemManager.CollectorState;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.systemManager.InitiativeModeDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.systemManager.InitiativeModeTypeDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.systemManager.StepStateChangeDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.systemManager.StepStateChangeTypeDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.systemManager.SystemStateDTO;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;

@Service
@Path("/manager")
public class SystemManagerApi extends RestApiParent {

	public static String MAN01 = "MAN01: ";
	public static String MAN02 = "MAN02: ";
	public static String MAN03 = "MAN03: ";
	public static String MAN04 = "MAN04: ";
	public static String MAN05 = "MAN05: ";
	public static String MAN06 = "MAN06: ";
	public static String MAN07 = "MAN07: ";
	public static String MAN08 = "MAN08: ";
	public static String MAN09 = "MAN09: ";

	@GET
	@Path("/state")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSystemState() {
		try {
			SystemPreferences systemPreferences = systemManager.getSystemPreferences();
			SystemStateDTO systemStateDTO = systemStateTransformer.transform(systemPreferences);
			return Response.status(Status.OK).entity(systemStateDTO)
					.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
		} catch (OCTException e) {
			logger.error(e.getMessage(), e);
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					MAN09 + "Unable to retrieve system state");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse).build();
		}
	}

	@Secured
	@GET
	@Path("/mode")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getInitiativeMode() {
		try {
			InitiativeModeTypeDTO initiativeModeTypeDTO = InitiativeModeTypeDTO.OFFLINE;
			SystemState systemState = systemManager.getSystemPreferences().getState();
			if (systemState == SystemState.OPERATIONAL) {
				initiativeModeTypeDTO = InitiativeModeTypeDTO.ONLINE;
			}
			InitiativeModeDTO initiativeModeDTO = new InitiativeModeDTO();
			initiativeModeDTO.setMode(initiativeModeTypeDTO.name());

			return Response.status(Status.OK).entity(initiativeModeDTO)
					.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
		} catch (OCTException e) {
			logger.error(e.getMessage(), e);
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					MAN09 + "Unable to retrieve the system state");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse).build();
		}
	}

	@GET
	@Path("/collectorstate")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCollectingState() {
		try {
			boolean collectingMode = systemManager.getCollecting();
			if (collectingMode) {
				collectorState.setCollectionMode(CollectorState.ON);
			} else {
				collectorState.setCollectionMode(CollectorState.OFF);
			}
			return Response.status(Status.OK).entity(collectorState)
					.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
		} catch (OCTException e) {
			logger.error(e.getMessage(), e);
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					MAN01 + "Collection mode unavailable");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse).build();
		}
	}

	@Secured
	@POST
	@Path("/collectorsetstate")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response setCollectingState(CollectorState collectorState) {

		// validate input
		if (collectorState == null || collectorState.getCollectionMode() == null
				|| collectorState.getCollectionMode().isEmpty()) {
			apiResponse = buildError(Status.EXPECTATION_FAILED.getStatusCode(),
					MAN02 + "One or more expected parameters are null.");
			return Response.status(Status.EXPECTATION_FAILED).entity(apiResponse).build();
		}

		boolean collectingMode = collectorState.getCollectionMode().equalsIgnoreCase(CollectorState.ON);
		try {
			systemManager.setCollecting(collectingMode);
			return Response.status(Status.OK).entity(collectorState)
					.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
		} catch (OCTException e) {
			logger.error(e.getMessage(), e);
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					MAN02 + "Error while setting collection state");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse).build();
		}
	}

	@GET
	@Path("/countries")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllCountries() {
		try {
			List<Country> daoCountries = systemManager.getAllCountries();
			countries = countryTransformer.transformCountries(daoCountries);
			return Response.status(Status.OK).entity(countries)
					.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
		} catch (OCTException e) {
			logger.error(e.getMessage(), e);
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					MAN03 + "Cannot retrieve countries list");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse).build();
		}
	}

	@GET
	@Path("/languages")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllLanguages() {
		try {
			List<Language> daoLanguages = systemManager.getAllLanguages();
			languages = languageTransformer.transformLanguages(daoLanguages);
			return Response.status(Status.OK).entity(languages)
					.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
		} catch (OCTException e) {
			logger.error(e.getMessage(), e);
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					MAN04 + "Unable to retrieve languages list");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse).build();
		}
	}

	@Secured
	@GET
	@Path("/goonline")
	@Produces(MediaType.APPLICATION_JSON)
	public Response goOnline() {
		try {
			systemManager.goOnline();
			cleanExportFolder();
			apiResponse = buildSuccess(Status.OK.getStatusCode(), MAN05 + "Operation succesful.");
			return Response.status(Status.OK).header("Cache-Control", "no-store, no-cache, must-revalidate")
					.entity(apiResponse).build();
		} catch (OCTException e) {
			logger.error(e.getMessage(), e);
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					MAN05 + " error while trying to go online");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse).build();
		}
	}

	@Secured
	@GET
	@Path("/gooffline")
	@Produces(MediaType.APPLICATION_JSON)
	public Response goOffline() {
		try {
			systemManager.goOffline();
			apiResponse = buildSuccess(Status.OK.getStatusCode(), "Operation succesful.");
			return Response.status(Status.OK).header("Cache-Control", "no-store, no-cache, must-revalidate")
					.entity(apiResponse).build();
		} catch (OCTException e) {
			logger.error(e.getMessage(), e);
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Error while trying to go offline");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse).build();
		}
	}

	@GET
	@Path("/countryProperties/{countryCode}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCountryPropertiesByCode(@PathParam("countryCode") String countryCode) {

		try {
			// sec check: only collection mode "ON"
			boolean collectionModeON = systemManager.getCollecting();
			if (!collectionModeON) {
				apiResponse = buildError(Status.EXPECTATION_FAILED.getStatusCode(),
						MAN08 + UNAUTHORIZED_ACTION_REQUEST);
				return Response.status(Status.EXPECTATION_FAILED).entity(apiResponse).build();
			}

			// validation of the input
			if (StringUtils.isBlank(countryCode) || countryCode.length() != Language.DEFAULT_CODE_LENGTH) {
				apiResponse = buildError(Status.EXPECTATION_FAILED.getStatusCode(),
						MAN08 + INPUT_PARAMS_EXPECTATION_FAILED + countryCode);
				return Response.status(Status.EXPECTATION_FAILED).entity(apiResponse).build();
			}
			List<CountryProperty> countryProperties = new ArrayList<CountryProperty>();
			countryProperties = signatureService.getCountryPropertiesByCountryCode(countryCode);
			if (countryProperties.isEmpty()) {
				apiResponse = buildError(Status.NO_CONTENT.getStatusCode(),
						MAN08 + OBJECT_NOT_FOUND_FOR_PARAM + countryCode);
				return Response.status(Status.NO_CONTENT).entity(apiResponse).build();
			} else {
				GenericEntity<List<SupportFormDTO>> countryPropertiesDTO = new GenericEntity<List<SupportFormDTO>>(
						countryPropertyTransformer.transform(countryProperties)) {
				};
				return Response.status(Status.OK).entity(countryPropertiesDTO)
						.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
			}
		} catch (OCTException e) {
			logger.error(e.getMessage(), e);
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					"Error while trying to getCountryPropertiesByCode");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse).build();
		}
	}

	@Secured
	@POST
	@Path("/setstepstate")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response setStepState(StepStateChangeDTO stepStateDTO) {

		if (stepStateDTO == null || stepStateDTO.getStep() == null) {
			apiResponse = buildError(Status.EXPECTATION_FAILED.getStatusCode(),
					MAN07 + "One or more expected parameters are null.");
			return Response.status(Status.EXPECTATION_FAILED).entity(apiResponse).build();
		}

		try {
			StepStateChangeTypeDTO.valueOf(stepStateDTO.getStep());
			switch (StepStateChangeTypeDTO.valueOf(stepStateDTO.getStep())) {
			case STRUCTURE:
				systemManager.setStructureAsDone(stepStateDTO.isActive());
				break;
			case PERSONALISE:
				systemManager.setPersonaliseAsDone(stepStateDTO.isActive());
				break;
			case CERTIFICATE:
				systemManager.setCertificateAsDone(stepStateDTO.isActive());
				break;
			case SOCIAL:
				systemManager.setSocialAsDone(stepStateDTO.isActive());
				break;
			case LIVE:
				if (stepStateDTO.isActive()) {
					systemManager.setGoneLive();
				} else {
					systemManager.setNotLive();
				}
				break;
			}

			apiResponse = buildSuccess(Status.OK.getStatusCode(), MAN07 + "Operation succesful.");
			return Response.status(Status.OK).entity(apiResponse)
					.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
		} catch (OCTException e) {
			logger.error(e.getMessage(), e);
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					MAN07 + "Error while setting setStepState");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse).build();
		}
	}

	@Secured
	@GET
	@Path("/stepstate")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStepState() {
		try {
			StepState stepState = systemManager.getStepState();

			return Response.status(Status.OK).entity(stepState)
					.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
		} catch (OCTException e) {
			logger.error(e.getMessage(), e);
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					MAN06 + "getStepState mode unavailable");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse).build();
		}
	}

}
