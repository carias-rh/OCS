package eu.europa.ec.eci.oct.webcommons.services.exceptions;

/**
 * User: franzmh
 * Date: 01/12/16
 */
public class ValidationException extends Exception{
    private static final long serialVersionUID = -751472302894062204L;

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationException(String message) {
        super(message);
    }

}
