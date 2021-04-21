package eu.europa.ec.eci.oct.webcommons.security.requesttoken;

import java.io.Serializable;

/**
 * A token for a request. The token mechanism can be used for protecting
 * forms against cross-site request forgery attacks as follows:
 * <ol>
 * <li>for a controller serving a form, generate a token in the GET
 * call serving the form, register it on the session and expose it on the model,</li>
 * <li>in the view, transmit the current request token as a hidden parameter,</li>
 * <li>in the controller handling the form POST, check for the presence of a
 * token on the request and compare it against the expected token on the session</li>
 * <li>if the request token matches the expected session token, remove the token from
 * the session and serve the page, else block.</li>
 * </ol>
 * Currently, a request token consists of a value and a condition
 * defining when the token should be consumed.
 *
 * @author keschma
 *
 */
public class RequestToken implements Serializable {

	private static final long serialVersionUID = 7543285940660553792L;
	
	private final String value;
	
	private final TokenConsumeCondition consumeCondition;
	
	/**
	 * Constructor for tokens that will be consumed depending on the
	 * given condition.
	 * @param value the token value
	 * @param consumeCondition the condition for consuming this token
	 */
	public RequestToken(String value, TokenConsumeCondition consumeCondition) {
		this.value = value;
		this.consumeCondition = consumeCondition;
	}
	
	/**
	 * Returns the value of this token.
	 * @return
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * Returns the condition for consuming this token.
	 * @return
	 */
	public TokenConsumeCondition getConsumeCondition() {
		return consumeCondition;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final RequestToken other = (RequestToken) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RequestToken [value=" + value + ", consumeCondition="
				+ consumeCondition + "]";
	}
		
}
