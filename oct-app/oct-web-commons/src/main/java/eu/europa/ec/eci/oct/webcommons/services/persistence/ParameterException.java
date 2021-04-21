package eu.europa.ec.eci.oct.webcommons.services.persistence;

import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;

/**
 * User: franzmh
 * Date: 05/12/16
 */
public class ParameterException extends OCTException {
	private static final long serialVersionUID = 3035607189250254438L;

	public ParameterException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParameterException(String message) {
        super(message);
    }
}
