package eu.europa.ec.eci.oct.webcommons.services.exceptions;

/**
 * User: franzmh
 * Date: 13/03/17
 */
public class TranslationException extends Exception{
    private static final long serialVersionUID = 8212516703302771101L;
    public TranslationException(String message, Throwable cause) {
        super(message, cause);
    }

    public TranslationException(String message) {
        super(message);
    }
}
