package eu.europa.ec.eci.oct.webcommons.services.crypto;

import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;

public class OCTCryptoException extends OCTException {
	
	private static final long serialVersionUID = -7536176224528337420L;
	
	public OCTCryptoException(String message) {
		super(message);		
	}
	
	public OCTCryptoException(String message, Throwable cause) {
		super(message, cause);		
	}
}
