/** ====================================================================
 * Licensed under the European Union Public Licence (EUPL v1.2) 
 * https://joinup.ec.europa.eu/community/eupl/topic/public-consultation-draft-eupl-v12
 * ====================================================================
 *
 * @author Daniel CHIRITA
 * @created: 23/05/2013
 *
 */
package eu.europa.ec.eci.oct.entities;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "OCT_SETTINGS")
@Cacheable(false)
public class ConfigurationParameter {
	
	public static final String CALLBACK_URL = "callback_url";
	public static final String FACEBOOK_URL = "facebook_url";
	public static final String TWITTER_URL = "twitter_url";
	public static final String GOOGLE_URL = "google_url";
	public static final String OPTIONAL_VALIDATION = "optional_validation";
	public static final String SHOW_RECENT_SUPPORTERS = "show_recent_supporters";
	public static final String SHOW_DISTRIBUTION_MAP = "show_distribution_map";
	public static final String SHOW_FACEBOOK = "show_facebook";
	public static final String SHOW_GOOGLE = "show_google";
	public static final String SHOW_TWITTER = "show_tweeter";
	public static final String SHOW_PROGRESSION_BAR = "show_progression_bar";
	public static final String SHOW_SOCIAL_MEDIA = "show_social_media";
	public static final String SIGNATURE_GOAL = "signature_goal";
	public static final String LOGO_PATH = "logo_path";
	public static final String COLOR_PICKER = "color_picker";
	public static final String LAST_UPDATE_SETTINGS = "last_update_settings";
	public static final String LAST_UPDATE_SOCIAL = "last_update_social";
	public static final String BACKGROUND = "background";
	public static final String LOGO_ALT_TXT = "logo_alt_txt";

	@Id
	@Column
	private String param;

	@Column
	private String value;

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "ConfigurationParameter [param=" + param + ", value=" + value + "]";
	}
}
