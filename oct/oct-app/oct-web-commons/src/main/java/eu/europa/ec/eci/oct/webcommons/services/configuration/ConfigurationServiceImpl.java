package eu.europa.ec.eci.oct.webcommons.services.configuration;

/** ====================================================================
 * Licensed under the European Union Public Licence (EUPL v1.2) 
 * https://joinup.ec.europa.eu/community/eupl/topic/public-consultation-draft-eupl-v12
 * ====================================================================
 *
 * @author Daniel CHIRITA
 * @created: 23/05/2013
 *
 */

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.eci.oct.entities.ConfigurationParameter;
import eu.europa.ec.eci.oct.webcommons.services.BaseService;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;
import eu.europa.ec.eci.oct.webcommons.services.persistence.PersistenceException;

@Service
@Transactional
public class ConfigurationServiceImpl extends BaseService implements ConfigurationService {


	@Override
	@Transactional(readOnly = true)
	public ConfigurationParameter getConfigurationParameter(Parameter parameter) throws OCTException {
		try {

			ConfigurationParameter result = settingsDAO.findConfigurationParametereByKey(parameter.getKey());
			if (result == null) {
				// if not found, prepopulate with default value
				result = new ConfigurationParameter();
				result.setParam(parameter.getKey());
				result.setValue(parameter.getDefaultValue());
			}

			return result;
		} catch (PersistenceException e) {
			logger.error("There was a problem retrieving the configuration parameter: " + parameter.getKey(), e);
			throw new OCTException("There was a problem retrieving the configuration parameter.", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = OCTException.class)
	public void updateParameter(ConfigurationParameter param) throws OCTException {
		try {
			settingsDAO.updateParameter(param);
		} catch (PersistenceException e) {
			logger.error("There was a problem while deleting signature. The message was: " + e.getLocalizedMessage(), e);
			throw new OCTException("There was a problem while deleting signature.", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<ConfigurationParameter> getAllSettings() throws OCTException {
		try {
			return settingsDAO.getAllSettings();
		} catch (PersistenceException e) {
			logger.error("There was a problem while retrieving all settings. The message was: " + e.getLocalizedMessage(), e);
			throw new OCTException("There was a problem while retrieving all settings.", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = OCTException.class)
	public void updateParameters(List<ConfigurationParameter> configurationParameters) throws OCTException {
		for (ConfigurationParameter configurationParameter : configurationParameters) {
			updateParameter(configurationParameter);
		}

	}

}
