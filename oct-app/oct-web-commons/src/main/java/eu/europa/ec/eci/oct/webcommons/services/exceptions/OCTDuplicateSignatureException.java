package eu.europa.ec.eci.oct.webcommons.services.exceptions;


public class OCTDuplicateSignatureException extends Exception {

	private static final long serialVersionUID = -7170578125455241888L;

	public OCTDuplicateSignatureException(String message, Throwable cause) {
		super(message, cause);		
	}

	public OCTDuplicateSignatureException(String message) {
		super(message);		
	}


}
