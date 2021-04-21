/** ====================================================================
 * Licensed under the European Union Public Licence (EUPL v1.2) 
 * https://joinup.ec.europa.eu/community/eupl/topic/public-consultation-draft-eupl-v12
 * ====================================================================
 *
 * @author Daniel CHIRITA
 * @created: 23/05/2013
 *
 */
package eu.europa.ec.eci.oct.webcommons.services.persistence;

import java.util.List;

import eu.europa.ec.eci.oct.entities.ConfigurationParameter;

public interface SettingsDAO {

	/**
	 * Retrieves all settings from the underlying database.
	 * 
	 * @return
	 * @throws PersistenceException
	 */
	public List<ConfigurationParameter> getAllSettings() throws PersistenceException;

	/**
	 * Retrieves a configuration parameter from the settings table.
	 * 
	 * @param key
	 * @return
	 * @throws PersistenceException
	 */
	public ConfigurationParameter findConfigurationParametereByKey(String key) throws PersistenceException;

	/**
	 * Updates an existing parameter or inserts it if not found in the settings
	 * table.
	 * 
	 * @param param
	 * @return
	 * @throws PersistenceException
	 */
	public ConfigurationParameter updateParameter(ConfigurationParameter param) throws PersistenceException;
	
}
