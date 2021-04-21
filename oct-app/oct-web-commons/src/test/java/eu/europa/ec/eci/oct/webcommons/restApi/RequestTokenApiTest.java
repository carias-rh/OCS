package eu.europa.ec.eci.oct.webcommons.restApi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europa.ec.eci.oct.webcommons.services.api.RequestTokenApi;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.security.TokenConsumeDTO;
import eu.europa.ec.eci.oct.webcommons.services.security.RequestTokenService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/rest-api-test.xml")
public class RequestTokenApiTest extends RestApiTest {

    @Autowired
    protected RequestTokenService requestTokenService;

    @Autowired
    RequestTokenApi requestTokenApi;

    @Test
    public void testSimpleRequestToken() {
        Response response = target("/token/request").request().get(Response.class);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        String token = response.getHeaderString("x-ocs-token");
        assertNotNull(token);
        logger.debug(token);
    }


    @Test
    public void testRequestTokenAndConsume() {
        Response response = target("/token/request").request().get(Response.class);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        String token = response.getHeaderString("x-ocs-token");

        response = target("/token/consume").request(MediaType.APPLICATION_JSON_TYPE).header("x-ocs-token", token).get(Response.class);

        TokenConsumeDTO c = response.readEntity(TokenConsumeDTO.class);
        assertTrue(c.getConsumed());

    }

    @Test
    public void testConsumeATokenNotFound() {

        String token  = "a token";
        Response response = target("/token/consume").request(MediaType.APPLICATION_JSON_TYPE).header("x-ocs-token", token).get(Response.class);

        TokenConsumeDTO c = response.readEntity(TokenConsumeDTO.class);
        assertFalse(c.getConsumed());

    }
}
