package eu.europa.ec.eci.oct.webcommons.services.api;

import static eu.europa.ec.eci.oct.webcommons.services.api.domain.ApiResponse.buildError;

import java.io.InputStream;
import java.util.Properties;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import eu.europa.ec.eci.oct.webcommons.services.api.domain.VersionDTO;

@Path("/version")
public class VersionApi extends RestApiParent {

	@Secured
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path("/securedGet")
	public Response securedGet() throws Exception {
		return Response.status(Status.OK.getStatusCode()).header("Cache-Control", "no-store, no-cache, must-revalidate")
				.build();
	}

	@POST
	@Secured
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/securedPost")
	public Response securedPost(String string) throws Exception {
		return Response.status(Status.OK.getStatusCode()).entity(string).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response version() throws Exception {
		VersionDTO versionDTO = new VersionDTO();
		try {
			Resource resource = new ClassPathResource("/version.properties");
			InputStream resourceInputStream = resource.getInputStream();
			logger.info("retrieved inputStream: " + resourceInputStream);
			Properties properties = new Properties();
			try {
				properties.load(resourceInputStream);
				String buildDate = properties.getProperty("build.date");
				String apiVersion = properties.getProperty("build.version");
				versionDTO.setBuildDate(buildDate);
				versionDTO.setApiVersion(apiVersion);
				return Response.status(Status.OK.getStatusCode()).entity(versionDTO).build();
			} catch (Exception e) {
				logger.error(e.getMessage());
				apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Unable to retrieve version");
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse).build();
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			apiResponse = buildError(Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Unable to retrieve version");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(apiResponse).build();
		}
	}

}
