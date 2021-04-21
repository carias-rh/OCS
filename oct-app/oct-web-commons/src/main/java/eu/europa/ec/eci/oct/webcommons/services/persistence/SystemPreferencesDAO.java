package eu.europa.ec.eci.oct.webcommons.services.persistence;

import eu.europa.ec.eci.oct.entities.admin.SystemPreferences;

public interface SystemPreferencesDAO {

	SystemPreferences getSystemPreferences() throws PersistenceException;

	void setPreferences(SystemPreferences prefs) throws PersistenceException;

	void setCollecting(boolean collecting) throws PersistenceException;

}
