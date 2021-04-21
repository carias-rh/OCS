package eu.europa.ec.eci.oct.crypto;

/**
 * Generic cryptography exception for a module
 * 
 * @author marcin.dzierzak@ext.ec.europa.eu
 *
 */
public class CryptoException extends Exception {

	private static final long serialVersionUID = -665718237776325315L;

	/**
	 * Creates exception instance 
	 * 
	 * @param message
	 * @param cause
	 */
	public CryptoException(String message, Throwable cause) {
		super(message, cause);		
	}

	/**
	 * Creates exception instance
	 * 
	 * @param message
	 */
	public CryptoException(String message) {
		super(message);		
	}

}
