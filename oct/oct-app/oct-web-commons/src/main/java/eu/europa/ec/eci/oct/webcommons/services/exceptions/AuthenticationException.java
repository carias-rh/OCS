package eu.europa.ec.eci.oct.webcommons.services.exceptions;

/**
 * User: franzmh
 * Date: 23/01/17
 */
public class AuthenticationException extends Exception{
    private static final long serialVersionUID = -831194308157036217L;

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthenticationException(String message) {
        super(message);
    }


}
