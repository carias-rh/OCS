package eu.europa.ec.eci.oct.webcommons.services.exceptions;

/**
 * User: franzmh
 * Date: 21/02/17
 */

public class AuthenticationLockedException extends Exception{
    public AuthenticationLockedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthenticationLockedException(String message) {
        super(message);
    }

}
