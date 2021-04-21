package eu.europa.ec.eci.oct.webcommons.services.persistence;

public class PersistenceException extends Exception {

	private static final long serialVersionUID = -1829152334925379089L;

	public PersistenceException(String message, Throwable cause) {
		super(message, cause);		
	}

	public PersistenceException(String message) {
		super(message);		
	}
	
}
