package eu.europa.ec.eci.oct.webcommons.services.api;

import static eu.europa.ec.eci.oct.webcommons.services.api.domain.ApiResponse.buildError;
import static eu.europa.ec.eci.oct.webcommons.services.api.domain.ApiResponse.buildSuccess;

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

import org.springframework.batch.core.BatchStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import eu.europa.ec.eci.oct.entities.export.ExportCount;
import eu.europa.ec.eci.oct.entities.export.ExportHistory;
import eu.europa.ec.eci.oct.export.ExportJobRunner;
import eu.europa.ec.eci.oct.export.utils.ExportParameter;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.commons.StringDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.export.ExportHistoryDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.export.ExportHistoryListDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.export.ExportParameterDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.export.ExportSignaturesCountDTO;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;

@Service
@Scope("prototype")
@Path("/export")
public class ExportApi extends RestApiParent {

	public static String EXP01 = "EXP01: ";
	public static String EXP02 = "EXP02: ";
	public static String EXP03 = "EXP03: ";

	@Autowired
	ExportJobRunner exportJobRunner;

	final String EXPORT_ASYNC_THREAD_NAME = "export_async_thread";

	/* NEW IMPLEMENTATION : ASYNCHRONOUS */
	@Secured
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Async
	public Response exportAsync(ExportParameterDTO exportParameterDTO) throws Exception {
		ExportParameter exportParameter = exportTransformer.transform(exportParameterDTO);
		try {
			Runnable exportTask = () -> {
				try {
					exportJobRunner.runExport(exportParameter);
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			};

			Thread exportThread = new Thread(exportTask, EXPORT_ASYNC_THREAD_NAME);
			exportThread.start();
			apiResponse = buildSuccess(ACCEPTED.getStatusCode(), EXP01 + "Export launched.");
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			logger.error(errorMessage);
			apiResponse = buildError(INTERNAL_SERVER_ERROR.getStatusCode(),
					EXP02 + "Unable to export: " + errorMessage);
			return Response.status(INTERNAL_SERVER_ERROR).entity(apiResponse).build();
		}

		return Response.status(Status.ACCEPTED).entity(apiResponse).build();
	}

	@Secured
	@Path("/stop")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response stopExport(StringDTO jobIdDTO) throws Exception {

		String jobUUID = jobIdDTO.getValue();
		if (jobIdDTO == null || jobUUID == null || jobUUID.isEmpty()) {
			return Response.status(BAD_REQUEST).entity(apiResponse).build();
		}
		Response response = Response.status(OK).entity(BatchStatus.STOPPED).build();
		logger.info("Stop export request received.");
		try {
			for (Thread t : Thread.getAllStackTraces().keySet()) {
				if (t.getName().equals(EXPORT_ASYNC_THREAD_NAME)) {
					t.interrupt();
				}
			}
			exportJobRunner.stop(jobUUID);
		} catch (Exception e) {
			/*
			 * exception comes from a job that is not running (i.e. server stuck or crash
			 * and pending job, so careless of the return, update the history table or the
			 * UI will keep on asking the status.
			 */
		}
		ExportHistory exportHistory = exportHistoryPersistenceDAO.getExportHistoryByUUID(jobUUID);
		String status = BatchStatus.STOPPED.name();
		exportHistory.setBatchStatus(status);
		exportHistoryPersistenceDAO.updateExportHistory(exportHistory);
		cleanExportFolder();
		return response;
	}

	@Secured
	@Path("/count")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response countSignatures(ExportParameterDTO exportParameterDTO) throws Exception {

		if (exportParameterDTO == null) {
			return Response.status(BAD_REQUEST).entity(apiResponse).build();
		}

		if (exportParameterDTO.getCountries() == null || exportParameterDTO.getCountries().isEmpty()) {
			return Response.status(BAD_REQUEST).entity(apiResponse).build();
		}

		ExportParameter epb = exportTransformer.transform(exportParameterDTO);
		try {
			long signaturesToBeExportedCount = signatureService.getSignaturesCountForExport(epb);
			signatureCount.setSignatureCount(signaturesToBeExportedCount);
			return Response.status(OK).entity(signatureCount).build();
		} catch (OCTException e) {
			logger.error(e.getMessage());
			apiResponse = buildError(INTERNAL_SERVER_ERROR.getStatusCode(), EXP02 + "Unable to count signatures");
			return Response.status(INTERNAL_SERVER_ERROR).entity(apiResponse).build();
		}
	}

	@Secured
	@POST
	@Path("/countByCountry")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSignaturesCountAllAndByCountry(ExportParameterDTO exportParameterDTO) {
		try {
			if (exportParameterDTO == null) {
				return Response.status(BAD_REQUEST).entity(apiResponse).build();
			}

			ExportParameter epb = exportTransformer.transform(exportParameterDTO);

			ExportCount exportCount = signatureService.getExportCount(epb);

			ExportSignaturesCountDTO exportSignaturesCountDTO = exportTransformer.transform(exportCount);

			return Response.status(OK).entity(exportSignaturesCountDTO).build();
		} catch (Exception e) {
			logger.error(e.getMessage());
			apiResponse = buildError(INTERNAL_SERVER_ERROR.getStatusCode(),
					EXP03 + "count signature by country unavailable");
			return Response.status(INTERNAL_SERVER_ERROR).entity(apiResponse).type(MediaType.APPLICATION_JSON).build();
		}
	}

	@Secured
	@GET
	@Path("/status")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getExportStatus() throws Exception {

		ExportHistory exportHistory = null;
		try {
			exportHistory = exportHistoryPersistenceDAO.getLastExportHistory();
			// exportHistory = exportJobRunner.getExportHistory();
		} catch (Exception e) {
			logger.error(e.getMessage());
			apiResponse = buildError(INTERNAL_SERVER_ERROR.getStatusCode(), "Error while getting export info.");
			return Response.status(INTERNAL_SERVER_ERROR).entity(apiResponse).type(MediaType.APPLICATION_JSON).build();
		}
		ExportHistoryDTO exportHistoryDTO = exportTransformer.transformExportHistory(exportHistory);

		return Response.status(OK).entity(exportHistoryDTO)
				.header("Cache-Control", "no-store, no-cache, must-revalidate").build();
	}

	@Secured
	@GET
	@Path("/history")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getExportHistory() throws Exception {

		List<ExportHistory> exportHistoryList = null;
		try {
			exportHistoryList = exportHistoryPersistenceDAO.getAllExportHistory();
		} catch (Exception e) {
			logger.error(e.getMessage());
			apiResponse = buildError(INTERNAL_SERVER_ERROR.getStatusCode(), "Error while getting export info.");
			return Response.status(INTERNAL_SERVER_ERROR).entity(apiResponse).type(MediaType.APPLICATION_JSON).build();
		}
		ExportHistoryListDTO exportHistoriesDTO = new ExportHistoryListDTO();
		exportHistoriesDTO.setExportHistoryList(exportTransformer.transformExportHistoryList(exportHistoryList));
		return Response.status(OK).entity(exportHistoriesDTO)
				.header("Cache-Control", "no-store, no-cache, must-revalidate").build();

	}

	@Secured
	@GET
	@Path("/history/{start}/{offset}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getExportHistory(@PathParam("start") int start, @PathParam("start") int offset) throws Exception {

		List<ExportHistory> exportHistoryList = null;
		try {
			exportHistoryList = exportHistoryPersistenceDAO.getAllExportHistory();
		} catch (Exception e) {
			logger.error(e.getMessage());
			apiResponse = buildError(INTERNAL_SERVER_ERROR.getStatusCode(), "Error while getting export info.");
			return Response.status(INTERNAL_SERVER_ERROR).entity(apiResponse).type(MediaType.APPLICATION_JSON).build();
		}
		ExportHistoryListDTO exportHistoriesDTO = new ExportHistoryListDTO();
		exportHistoriesDTO.setExportHistoryList(exportTransformer.transformExportHistoryList(exportHistoryList));
		return Response.status(OK).entity(exportHistoriesDTO)
				.header("Cache-Control", "no-store, no-cache, must-revalidate").build();

	}
}
