package eu.europa.ec.eci.oct.webcommons.services.api.filters;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import eu.europa.ec.eci.oct.webcommons.services.api.Secured;
import eu.europa.ec.eci.oct.webcommons.services.security.Principal;
import eu.europa.ec.eci.oct.webcommons.services.security.SecurityService;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

	@Autowired
	SecurityService securityService;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

		if (authorizationHeader == null) {
			throw new NotAuthorizedException("Unauthorized access");
		}

		String token = authorizationHeader.substring("Bearer".length()).trim();
		Principal principal = securityService.validateAuthToken(token);

		if (principal == null) {
			throw new NotAuthorizedException("Unauthorized access");
		}
	}

}
