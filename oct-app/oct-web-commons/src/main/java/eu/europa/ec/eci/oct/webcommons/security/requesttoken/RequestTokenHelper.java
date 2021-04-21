//package eu.europa.ec.eci.oct.webcommons.security.requesttoken;
//
//import java.security.SecureRandom;
//
//import javax.servlet.http.HttpServletRequest;
//
//import org.apache.log4j.Logger;
//import org.springframework.stereotype.Component;
//
//import eu.europa.ec.eci.oct.utils.CommonsConstants;
//
///**
// * Helper object for request tokens.
// * @see RequestToken
// *
// * @author keschma
// *
// */
//@Component
//public class RequestTokenHelper {
//	
//	private static final Logger LOGGER = Logger.getLogger(RequestTokenHelper.class);
//	
//	private final SecureRandom secureRandom = new SecureRandom();
//	
//	/**
//	 * Produces a token that will always be consumed.
//	 * @param request an HttpServletRequest
//	 * @see #getToken(HttpServletRequest, TokenConsumeCondition)
//	 */
//	public RequestToken getToken(HttpServletRequest request) {	
//		return getToken(request, new TokenConsumeCondition() {			
//			private static final long serialVersionUID = -6512293936079709502L;
//
//			@Override
//			public boolean isConsumable(HttpServletRequest request) {
//				return true;
//			}
//			
//			@Override
//			public String toString() {
//				return "<consume always>";
//			}
//		});
//	}
//	
//	/**
//	 * Produces a token which should be consumed according to the given
//	 * condition. As a side effect, the generated token is registered
//	 * as an attribute on the current session, using
//	 * {@link CommonControllerConstants#SESSION_ATTR_EXPECTED_REQUEST_TOKEN}
//	 * for the attribute name.
//	 * @param request an HttpServletRequest
//	 * @param consumeCondition the condition for consuming this token for a given request
//	 * @return the generated token
//	 */
//	public RequestToken getToken(HttpServletRequest request, TokenConsumeCondition consumeCondition) {	
//		final String value = String.valueOf(secureRandom.nextLong());
//		final RequestToken token = new RequestToken(value, consumeCondition);
//		request.getSession(true).setAttribute(CommonsConstants.SESSION_ATTR_EXPECTED_REQUEST_TOKEN,
//				token);
//		
//		return token;
//	}
//	
//	/**
//	 * Checks whether there is a request token match between a token value
//	 * on the current HTTP request and an expected token registered on the
//	 * session. As a side effect, if the token is configured as consumable
//	 * for this request, it will be removed from the session on a successful
//	 * match.
//	 * @param request an HttpServletRequest
//	 * @return true, if there is a token match, else false
//	 */
//	public boolean checkAndConsume(HttpServletRequest request) {
//		// obtain the expected token from the session
//		final RequestToken expectedToken = (RequestToken)
//			request.getSession(true).getAttribute(CommonsConstants.SESSION_ATTR_EXPECTED_REQUEST_TOKEN);
//		if (expectedToken == null) {
//			LOGGER.warn("No expected request token found on session where one is needed.");
//			return false;
//		}
//		
//		LOGGER.debug("Expected request token on session: " + expectedToken);
//		
//		// obtain the current token value from the request
//		final String currentRequestTokenValue = request.getParameter(CommonsConstants.REQUEST_PARAM_REQUEST_TOKEN);
//		if (currentRequestTokenValue == null) {
//			LOGGER.warn("No request token value found on the request.");
//			return false;
//		}
//		
//		LOGGER.debug("Current token value on request: " + currentRequestTokenValue);
//		
//		// compare and potentially consume the current token
//		if (currentRequestTokenValue.equals(expectedToken.getValue())) {
//			LOGGER.debug("Request token was successfully matched.");
//			if (expectedToken.getConsumeCondition().isConsumable(request)) {
//				LOGGER.debug("Consuming token.");
//				request.getSession(false).removeAttribute(CommonsConstants.SESSION_ATTR_EXPECTED_REQUEST_TOKEN);
//			} else {
//				LOGGER.debug("NOT consuming token (not configured as such).");	
//			}
//			return true;
//		}
//
//		LOGGER.warn("Request token mismatch: expected[session]=" +
//				expectedToken + "; found value[request]=" + currentRequestTokenValue);
//		return false;
//	}
//
//}
