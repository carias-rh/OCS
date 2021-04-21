package eu.europa.ec.eci.oct.webcommons.services.exceptions;


/**
 * Uniform exception for the OCT business layer.
 * <p>
 * Note that this exception is configured to automatically
 * roll back EJB transactions around business method
 * calls (<code>@ApplicationException(rollback = true)</code>).
 * <strong>IMPORTANT:</strong> The former annotation must also be added
 * to <em>any child exception</em> that inherits from this class!
 * </p>
 *   
 * @author dzierma
 *
 */
public class OCTException extends Exception {

	private static final long serialVersionUID = -208050000305499951L;

	public OCTException(String message, Throwable cause) {
		super(message, cause);		
	}

	public OCTException(String message) {
		super(message);		
	}

}
