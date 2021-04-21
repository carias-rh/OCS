package eu.europa.ec.eci.oct.webcommons.services.exceptions;

/**
 * User: franzmh
 * Date: 05/12/16
 */
public class OCTParameterException extends Exception {
    private static final long serialVersionUID = -9179108096090859960L;

    public OCTParameterException(String message, Throwable cause) {
        super(message, cause);
    }

    public OCTParameterException(String message) {
        super(message);
    }


}
