package eu.europa.ec.eci.oct.offline.startup;

public interface SecurityConstants {

	/**
	 * The interval of inactivity after which the application should be locked (in milliseconds). 
	 */
	public final int APPLICATION_TIMEOUT = 5 * 60 * 1000;
	
	/**
	 * The minimum password length (in characters).
	 */
	public final int MINIMUM_PASSWORD_LENGTH = 10;
}
