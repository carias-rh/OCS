package eu.europa.ec.eci.oct.webcommons.services.exceptions;

/**
 * User: franzmh
 * Date: 05/12/16
 */
public class IncompleteSignatureException extends Exception{
    private static final long serialVersionUID = 4532681626969728189L;

    public IncompleteSignatureException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncompleteSignatureException(String message) {
        super(message);
    }
}
