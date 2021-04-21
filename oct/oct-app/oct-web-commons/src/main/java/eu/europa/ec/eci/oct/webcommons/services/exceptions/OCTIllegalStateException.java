package eu.europa.ec.eci.oct.webcommons.services.exceptions;

import eu.europa.ec.eci.oct.entities.admin.SystemState;

public class OCTIllegalStateException extends OCTException {

	private static final long serialVersionUID = -5845291168752417153L;

	private SystemState state;
	
	public OCTIllegalStateException(String message, SystemState state) {
		super(message);		
		this.state = state;
	}

	public SystemState getState() {
		return state;
	}

}
