package eu.europa.ec.eci.oct.webcommons.security.requesttoken;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

/**
 * Condition describing whether a {@link RequestToken}
 * should be consumed.
 *
 * @author keschma
 *
 */
public interface TokenConsumeCondition extends Serializable {
	
	/**
	 * Whether a token should be consumed, based on
	 * the given request.
	 * @param request an HttpServletRequest
	 * @return true, if the token should be consumed
	 */
	public boolean isConsumable(HttpServletRequest request);

}
