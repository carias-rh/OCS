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

import eu.europa.ec.eci.oct.entities.ConfigurationParameter;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;

public interface ConfigurationService {

	/**
	 * Enum containing possible configuration parameters and their default
	 * values.
	 * 
	 * @author Daniel CHIRITA
	 * 
	 */
	public enum Parameter {
		LOGO_PATH("logo_path", "/eu.png"), OPTIONAL_VALIDATION("optional_validation", "false"), SHOW_DISTRIBUTION_MAP(
				"show_distribution_map", "true"), CALLBACK_URL("callback_url", ""), SHOW_SOCIAL_MEDIA(
                "show_social_media", "true"), SHOW_PROGRESSION_BAR("show_progression_bar","true"),SIGNATURE_GOAL(
                "signature_goal","1000000"),SHOW_FACEBOOK("show_facebook", "true"),SHOW_GOOGLE("show_google", "true"),
                SHOW_TWEETER("show_tweeter", "true"),REMOTE_SERVICES("remote_services","false"),
        FACEBOOK_URL("facebook_url",""),GOOGLE_URL("google_url",""),TWITTER_URL("twitter_url",""),
        USER_MANUAL("user_manual",""),DESCRIPTION("description","");

		private String key;
		private String defaultValue;

		private Parameter(String key, String defaultValue) {
			this.key = key;
			this.defaultValue = defaultValue;
		}

		public String getKey() {
			return key;
		}

		public String getDefaultValue() {
			return defaultValue;
		}
	}

	/**
	 * Retrieves a configuration parameter from the settings table. If the
	 * parameter is not found, a new one will be created and prepopulated with
	 * the default value specified by the associated {@link Parameter}.
	 * 
	 * @param parameter
	 * @return
	 * @throws OCTException
	 */
	public ConfigurationParameter getConfigurationParameter(Parameter parameter) throws OCTException;

	/**
	 * Stores a parameter in the settings table. If the parameter already
	 * exists, it will be updated. If not, it will be inserted.
	 * 
	 * @param param
	 * @throws OCTException
	 */
	public void updateParameter(ConfigurationParameter param) throws OCTException;

	public List<ConfigurationParameter> getAllSettings() throws OCTException;

	public void updateParameters(List<ConfigurationParameter> configurationParameters) throws OCTException;
}
