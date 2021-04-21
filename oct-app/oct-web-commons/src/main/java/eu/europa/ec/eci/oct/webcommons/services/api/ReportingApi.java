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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import eu.europa.ec.eci.oct.entities.admin.Feedback;
import eu.europa.ec.eci.oct.entities.admin.FeedbackRange;
import eu.europa.ec.eci.oct.entities.views.EvolutionMapByCountry;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.commons.StringsDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.report.DistributionMap;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.report.EvolutionMapDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.report.FeedbackDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.report.FeedbackDTOs;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.report.FeedbackStatsDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.report.ProgressionStatus;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;

@Service
@Path("/report")
public class ReportingApi extends RestApiParent {

	public static String REP01 = "REP01: ";
	public static String REP02 = "REP02: ";
	public static String REP03 = "REP03: ";
	public static String REP04 = "REP04: ";
	public static String REP05 = "REP05: ";
	public static String REP06 = "REP06: ";

	@GET
	@Path("/evolutionMap")
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEvolutionMap() {
		try {
			List<EvolutionMapByCountry> evolutionMapsByCountry = reportingService.getEvolutionMapByCountry();
			EvolutionMapDTO dto = reportingTransformer.transform(evolutionMapsByCountry);

			return Response.status(Status.OK).entity(dto).build();

		} catch (OCTException e) {
			logger.error(e.getMessage());
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					REP04 + "Unable to retrieve evolutionMap");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse)
					.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
		}
	}

	@GET
	@Path("/progression")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProgressionStatus() {
		try {
			ProgressionStatus progressionStatus = reportingService.getProgressionStatus();
			return Response.status(Status.OK).entity(progressionStatus).build();
		} catch (OCTException e) {
			logger.error(e.getMessage());
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					REP01 + "Unable to retrieve progression status");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse)
					.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
		}
	}

	@GET
	@Path("/map")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDistributionMap() {
		try {
			DistributionMap distributionMap = reportingService.getDistributionMap();
			return Response.status(Status.OK).entity(distributionMap).build();
		} catch (Exception e) {
			logger.error(e.getMessage());
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					REP02 + "Unable to retrieve distribution map");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse)
					.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
		}
	}

	@GET
	@Path("/top7map")
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTop7Map() {
		try {
			DistributionMap distributionMap = reportingService.getTop7DistributionMap();
			return Response.status(Status.OK).entity(distributionMap).build();
		} catch (Exception e) {
			logger.error(e.getMessage());
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					REP02 + "Unable to retrieve distribution map");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse)
					.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
		}
	}

	@GET
	@Path("/feedbackRange")
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFeedbackRange() {
		List<FeedbackRange> feedbackRangeList = new ArrayList<FeedbackRange>();
		try {
			feedbackRangeList = reportingService.getFeedbackRanges();
		} catch (Exception e) {
			logger.error(e.getMessage());
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					REP05 + "Unable to retrieve feedback range list");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse).build();
		}
		List<String> feedbackRangeLabels = feedbackTransformer.transformFeedbackRangeDAOlist(feedbackRangeList);
		StringsDTO stringsDTO = new StringsDTO();
		stringsDTO.setValues(feedbackRangeLabels);
		return Response.status(Status.OK).entity(stringsDTO)
				.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
	}

	@GET
	@Path("/feedbacks")
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFeedbacks() {
		FeedbackDTOs feedbackDTOs = new FeedbackDTOs();
		List<Feedback> feedbackList = new ArrayList<Feedback>();
		try {
			feedbackList.addAll(reportingService.getAllFeedbacks());
			feedbackDTOs = feedbackTransformer.transformFeedbackDAOlist(feedbackList);
		} catch (Exception e) {
			logger.error(e.getMessage());
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					REP06 + "Unable to retrieve feedback range list");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse).build();
		}
		return Response.status(Status.OK).entity(feedbackDTOs)
				.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
	}

	@GET
	@Path("/feedbackStats")
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFeedbackStats() {

		FeedbackStatsDTO feedbackStatsDTO = new FeedbackStatsDTO();
		try {
			feedbackStatsDTO = reportingService.getFeedbackStats();
		} catch (Exception e) {
			logger.error(e.getMessage());
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					REP06 + "Unable to retrieve feedback stats");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse).build();
		}
		return Response.status(Status.OK).entity(feedbackStatsDTO)
				.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
	}

	// paginated version of getAllFeedbacks
	@GET
	@Path("/feedbacks/{start}/{offset}")
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFeedbacks(@PathParam("start") int start, @PathParam("offset") int offset) {

		List<Feedback> feedbackList = new ArrayList<Feedback>();
		try {
			feedbackList.addAll(reportingService.getAllFeedbacks(start, offset));
		} catch (Exception e) {
			logger.error(e.getMessage());
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					REP06 + "Unable to retrieve feedback range list");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse).build();
		}
		FeedbackDTOs feedbackDTOs = feedbackTransformer.transformFeedbackDAOlist(feedbackList);
		return Response.status(Status.OK).entity(feedbackDTOs)
				.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
	}

	@POST
	@Path("/feedback")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response saveFeedback(FeedbackDTO feedbackDTO) {
		// validate the input
		String signatureIdentifier = feedbackDTO.getSignatureIdentifier();
		if (feedbackDTO == null || feedbackDTO.getRange() == null || StringUtils.isBlank(signatureIdentifier)) {
			apiResponse = buildError(Status.EXPECTATION_FAILED.getStatusCode(),
					REP03 + INPUT_PARAMS_EXPECTATION_FAILED);
			return Response.status(Status.EXPECTATION_FAILED).entity(apiResponse).build();
		}

		Feedback feedbackToSave = null;
		try {
			feedbackToSave = feedbackTransformer.feedbackFromDTOtoDAO(feedbackDTO);
		} catch (OCTException e) {
			logger.error(e.getMessage());
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					REP06 + "Error while persisting feedback");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse).build();
		}
		try {
			reportingService.saveFeedback(feedbackToSave);
		} catch (OCTException e) {
			logger.error(e.getMessage());
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					REP06 + "Error while persisting feedback");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse).build();
		}
		apiResponse = buildSuccess(Status.OK.getStatusCode(), REP03 + "Feedback successfully submitted.");
		return Response.status(Status.OK).entity(apiResponse)
				.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
	}
}