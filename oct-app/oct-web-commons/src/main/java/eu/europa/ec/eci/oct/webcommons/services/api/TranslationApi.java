package eu.europa.ec.eci.oct.webcommons.services.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import eu.europa.ec.eci.oct.entities.system.Language;

@Path("/translations")
public class TranslationApi extends RestApiParent {

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/byKey/{key}/{languageCode}")
    public Response getByKey(@PathParam("key") String key,
                             @PathParam("languageCode") String languageCode) throws Exception {

        Language language = systemManager.getLanguageByCode(languageCode);
        String s = translationService.getTranslation(key, language);
        return Response.status(Response.Status.OK.getStatusCode()).entity(s).build();
    }

}
