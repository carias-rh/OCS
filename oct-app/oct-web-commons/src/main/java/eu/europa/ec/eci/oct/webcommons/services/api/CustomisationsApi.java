package eu.europa.ec.eci.oct.webcommons.services.api;

import static eu.europa.ec.eci.oct.webcommons.services.api.domain.ApiResponse.buildError;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.springframework.stereotype.Service;

import eu.europa.ec.eci.oct.entities.ConfigurationParameter;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.ApiResponse;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.customisations.CustomisationsDTO;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;

@Service
@Path("/customisations")
public class CustomisationsApi extends RestApiParent{

	// TODO: refactoring for validate input

	public static String CUS01 = "CUS01: ";
	public static String CUS02 = "CUS02: ";

	@GET
	@Path("/show")
	@Produces(MediaType.APPLICATION_JSON)
	public Response showCustomisations() {
		try {
			List<ConfigurationParameter> configurationParameters = configurationService
					.getAllSettings();
			customisationsDTO = customisationsTransformer.transform(configurationParameters);
			return Response.status(Status.OK).entity(customisationsDTO)
					.header("Cache-Control", "no-store, no-cache, must-revalidate")
					.build();
		} catch (OCTException e) {
			logger.error(e.getMessage());
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(), CUS01
					+ "Unable to retrieve customisations");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse).build();
		}
	}

	@Secured
	@POST
	@Path("/update")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateCustomisations(CustomisationsDTO customisationsDTO) {
		// validate the input
		if (customisationsDTO == null) {
			apiResponse = buildError(Status.EXPECTATION_FAILED.getStatusCode(), CUS02
					+ "input param is null");
			return Response.status(Status.EXPECTATION_FAILED).entity(apiResponse).build();
		}
		List<ConfigurationParameter> configurationParametersDAO = customisationsTransformer
				.transform(customisationsDTO);
		try {
			configurationService.updateParameters(configurationParametersDAO);
			apiResponse = ApiResponse.buildSuccess(Status.OK.getStatusCode(), CUS02 + "Succesful update");
			return Response.status(Status.OK).entity(apiResponse)
					.header("Cache-Control", "no-store, no-cache, must-revalidate")
					.build();
		} catch (OCTException e) {
			logger.error(e.getMessage());
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(), CUS02
					+ "Unable to update customisations");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse).build();
		}
	}
}