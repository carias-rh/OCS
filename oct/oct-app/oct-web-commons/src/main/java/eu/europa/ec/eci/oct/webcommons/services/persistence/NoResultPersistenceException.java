package eu.europa.ec.eci.oct.webcommons.services.persistence;

public class NoResultPersistenceException extends PersistenceException {
	
	private static final long serialVersionUID = -2725665778767744899L;
	
	public NoResultPersistenceException(String message) {
		super(message);		
	}
	public NoResultPersistenceException(String message, Throwable cause) {
		super(message, cause);		
	}

}
