package eu.europa.ec.eci.oct.webcommons.services.api;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Service;

import eu.europa.ec.eci.oct.webcommons.services.api.domain.security.TokenConsumeDTO;
import eu.europa.ec.eci.oct.webcommons.services.security.RequestTokenService;

@Service
@Path("/token")
public class RequestTokenApi extends RestApiParent {

    @GET
    @Path("/request")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getToken(){
        String token = requestTokenService.generateAndStore();
        logger.debug("Service call: token/request: Token Generated:[" + token + "]");
        return Response.status(Response.Status.OK).header(RequestTokenService.X_OCS_TOKEN, token).build();
    }


    @GET
    @Path("/consume")
    @Produces(MediaType.APPLICATION_JSON)
    public Response consumeToken(@HeaderParam(RequestTokenService.X_OCS_TOKEN) String token){
        boolean consumed = requestTokenService.consume(token);
        logger.debug("Service call: token/consume: token to be consumed:[" + token + "] consumed?["+consumed+"] ");

        TokenConsumeDTO c = new TokenConsumeDTO();
        c.setConsumed(consumed);
        return Response.status(Response.Status.OK).entity(c).build();
    }
}
