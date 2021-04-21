package eu.europa.ec.eci.oct.webcommons.services.api.transformer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import eu.europa.ec.eci.oct.entities.ConfigurationParameter;
import eu.europa.ec.eci.oct.utils.DateUtils;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.customisations.CustomisationsDTO;
import eu.europa.ec.eci.oct.webcommons.services.configuration.ConfigurationService;

@Component
public class CustomisationsTransformer extends BaseTransformer {

	public List<ConfigurationParameter> transform(CustomisationsDTO customisationsDTO) {
		List<ConfigurationParameter> configurationParametersDAO = new ArrayList<ConfigurationParameter>();
		boolean socialUpdate = false;
		boolean settingsUpdate = false;
		String newCallbackUrl = "";
		if (!StringUtils.isBlank(customisationsDTO.getCallbackUrl())) {
			newCallbackUrl = checkUrlConsistency(customisationsDTO.getCallbackUrl());
			settingsUpdate = true;
		}
		ConfigurationParameter callbackUrl = new ConfigurationParameter();
		callbackUrl.setParam(ConfigurationParameter.CALLBACK_URL);
		callbackUrl.setValue(newCallbackUrl);
		configurationParametersDAO.add(callbackUrl);

		String newFacebookUrl = "";
		if (!StringUtils.isBlank(customisationsDTO.getFacebookUrl())) {
			newFacebookUrl = checkUrlConsistency(customisationsDTO.getFacebookUrl());
			socialUpdate = true;
		}
		ConfigurationParameter facebookUrl = new ConfigurationParameter();
		facebookUrl.setParam(ConfigurationParameter.FACEBOOK_URL);
		facebookUrl.setValue(newFacebookUrl);
		configurationParametersDAO.add(facebookUrl);

		String newGoogleUrl = "";
		if (!StringUtils.isBlank(customisationsDTO.getGoogleUrl())) {
			socialUpdate = true;
			newGoogleUrl = checkUrlConsistency(customisationsDTO.getGoogleUrl());
		}
		ConfigurationParameter googleUrl = new ConfigurationParameter();
		googleUrl.setParam(ConfigurationParameter.GOOGLE_URL);
		googleUrl.setValue(newGoogleUrl);
		configurationParametersDAO.add(googleUrl);

		String newTwitterUrl = "";
		if (!StringUtils.isBlank(customisationsDTO.getTwitterUrl())) {
			socialUpdate = true;
			newTwitterUrl = checkUrlConsistency(customisationsDTO.getTwitterUrl());
		}
		ConfigurationParameter twitterUrl = new ConfigurationParameter();
		twitterUrl.setParam(ConfigurationParameter.TWITTER_URL);
		twitterUrl.setValue(newTwitterUrl);
		configurationParametersDAO.add(twitterUrl);

		if (customisationsDTO.getSignatureGoal() != null) {
			settingsUpdate = true;
			ConfigurationParameter signatureGoal = new ConfigurationParameter();
			signatureGoal.setParam(ConfigurationParameter.SIGNATURE_GOAL);
			signatureGoal.setValue("" + customisationsDTO.getSignatureGoal());
			configurationParametersDAO.add(signatureGoal);
		}

		if (customisationsDTO.isShowFacebook() != null) {
			settingsUpdate = true;
			ConfigurationParameter showFacebook = new ConfigurationParameter();
			showFacebook.setParam(ConfigurationParameter.SHOW_FACEBOOK);
			showFacebook.setValue(Boolean.toString(customisationsDTO.isShowFacebook()));
			configurationParametersDAO.add(showFacebook);
		}

		if (customisationsDTO.isShowGoogle() != null) {
			settingsUpdate = true;
			ConfigurationParameter showGoogle = new ConfigurationParameter();
			showGoogle.setParam(ConfigurationParameter.SHOW_GOOGLE);
			showGoogle.setValue(Boolean.toString(customisationsDTO.isShowGoogle()));
			configurationParametersDAO.add(showGoogle);
		}

		if (customisationsDTO.isShowTwitter() != null) {
			settingsUpdate = true;
			ConfigurationParameter showTwitter = new ConfigurationParameter();
			showTwitter.setParam(ConfigurationParameter.SHOW_TWITTER);
			showTwitter.setValue(Boolean.toString(customisationsDTO.isShowTwitter()));
			configurationParametersDAO.add(showTwitter);
		}

		if (customisationsDTO.isShowSocialMedia() != null) {
			settingsUpdate = true;
			ConfigurationParameter showSocialMedia = new ConfigurationParameter();
			showSocialMedia.setParam(ConfigurationParameter.SHOW_SOCIAL_MEDIA);
			showSocialMedia.setValue(Boolean.toString(customisationsDTO.isShowSocialMedia()));
			configurationParametersDAO.add(showSocialMedia);
		}

		if (customisationsDTO.getShowRecentSupporters() != null) {
			settingsUpdate = true;
			ConfigurationParameter remoteServices = new ConfigurationParameter();
			remoteServices.setParam(ConfigurationParameter.SHOW_RECENT_SUPPORTERS);
			remoteServices.setValue(Boolean.toString(customisationsDTO.getShowRecentSupporters()));
			configurationParametersDAO.add(remoteServices);
		}

		if (customisationsDTO.isShowProgressionBar() != null) {
			settingsUpdate = true;
			ConfigurationParameter showProgressionBar = new ConfigurationParameter();
			showProgressionBar.setParam(ConfigurationParameter.SHOW_PROGRESSION_BAR);
			showProgressionBar.setValue(Boolean.toString(customisationsDTO.isShowProgressionBar()));
			configurationParametersDAO.add(showProgressionBar);
		}

		if (customisationsDTO.isShowDistributionMap() != null) {
			settingsUpdate = true;
			ConfigurationParameter showDistributionMap = new ConfigurationParameter();
			showDistributionMap.setParam(ConfigurationParameter.SHOW_DISTRIBUTION_MAP);
			showDistributionMap.setValue(Boolean.toString(customisationsDTO.isShowDistributionMap()));
			configurationParametersDAO.add(showDistributionMap);
		}

		if (customisationsDTO.isOptionalValidation() != null) {
			settingsUpdate = true;
			ConfigurationParameter optionalValidation = new ConfigurationParameter();
			optionalValidation.setParam(ConfigurationParameter.OPTIONAL_VALIDATION);
			optionalValidation.setValue(Boolean.toString(customisationsDTO.isOptionalValidation()));
			configurationParametersDAO.add(optionalValidation);
		}
		if (customisationsDTO.getColorPicker() != null) {
			settingsUpdate = true;
			ConfigurationParameter colorPicker = new ConfigurationParameter();
			colorPicker.setParam(ConfigurationParameter.COLOR_PICKER);
			colorPicker.setValue(customisationsDTO.getColorPicker());
			configurationParametersDAO.add(colorPicker);
		}
		if (customisationsDTO.getBackground() != null) {
			settingsUpdate = true;
			ConfigurationParameter background = new ConfigurationParameter();
			background.setParam(ConfigurationParameter.BACKGROUND);
			background.setValue(customisationsDTO.getBackground() + "");
			configurationParametersDAO.add(background);
		}
		if (customisationsDTO.getBackground() != null) {
			settingsUpdate = true;
			ConfigurationParameter background = new ConfigurationParameter();
			background.setParam(ConfigurationParameter.BACKGROUND);
			background.setValue(customisationsDTO.getBackground() + "");
			configurationParametersDAO.add(background);
		}
		String newLogoAltText = "";
		if (!StringUtils.isBlank(customisationsDTO.getAlternateLogoText())) {
			newLogoAltText = customisationsDTO.getAlternateLogoText();
			settingsUpdate = true;
		}
		ConfigurationParameter altLogoTxt = new ConfigurationParameter();
		altLogoTxt.setParam(ConfigurationParameter.LOGO_ALT_TXT);
		altLogoTxt.setValue(newLogoAltText);
		configurationParametersDAO.add(altLogoTxt);

		if (settingsUpdate) {
			ConfigurationParameter lastUpdate = new ConfigurationParameter();
			lastUpdate.setParam(ConfigurationParameter.LAST_UPDATE_SETTINGS);
			lastUpdate.setValue(DateUtils.formatDate(new Date()));
			configurationParametersDAO.add(lastUpdate);
		}
		if (socialUpdate) {
			ConfigurationParameter lastUpdate = new ConfigurationParameter();
			lastUpdate.setParam(ConfigurationParameter.LAST_UPDATE_SOCIAL);
			lastUpdate.setValue(DateUtils.formatDate(new Date()));
			configurationParametersDAO.add(lastUpdate);
		}

		return configurationParametersDAO;
	}

	private String checkUrlConsistency(String url) {
		if (!url.startsWith("http://") && !url.startsWith("https://")) {
			return CustomisationsDTO.HTTP_PREFIX + url;
		}
		return url;
	}

	public CustomisationsDTO transform(List<ConfigurationParameter> configurationParameters) {

		CustomisationsDTO customisationsDTO = new CustomisationsDTO();

		for (ConfigurationParameter configurationParameter : configurationParameters) {
			String param = configurationParameter.getParam().trim();

			String value = "";
			if (configurationParameter.getValue() != null) {
				value = configurationParameter.getValue().trim();
			}

			if (param.equalsIgnoreCase(ConfigurationParameter.CALLBACK_URL)) {
				customisationsDTO.setCallbackUrl(value);
				continue;
			}
			if (param.equalsIgnoreCase(ConfigurationParameter.FACEBOOK_URL)) {
				customisationsDTO.setFacebookUrl(value);
				continue;
			}
			if (param.equalsIgnoreCase(ConfigurationParameter.TWITTER_URL)) {
				customisationsDTO.setTwitterUrl(value);
			}
			if (param.equalsIgnoreCase(ConfigurationParameter.GOOGLE_URL)) {
				customisationsDTO.setGoogleUrl(value);
				continue;
			}
			if (param.equalsIgnoreCase(ConfigurationParameter.OPTIONAL_VALIDATION)) {
				customisationsDTO.setOptionalValidation(value.equalsIgnoreCase("true") ? true : false);
				continue;
			}
			if (param.equalsIgnoreCase(ConfigurationParameter.SHOW_RECENT_SUPPORTERS)) {
				customisationsDTO.setShowRecentSupporters(value.equalsIgnoreCase("true") ? true : false);
				continue;
			}
			if (param.equalsIgnoreCase(ConfigurationParameter.SHOW_DISTRIBUTION_MAP)) {
				customisationsDTO.setShowDistributionMap(value.equalsIgnoreCase("true") ? true : false);
				continue;
			}
			if (param.equalsIgnoreCase(ConfigurationParameter.SHOW_FACEBOOK)) {
				customisationsDTO.setShowFacebook(value.equalsIgnoreCase("true") ? true : false);
				continue;
			}
			if (param.equalsIgnoreCase(ConfigurationParameter.SHOW_GOOGLE)) {
				customisationsDTO.setShowGoogle(value.equalsIgnoreCase("true") ? true : false);
				continue;
			}
			if (param.equalsIgnoreCase(ConfigurationParameter.SHOW_TWITTER)) {
				customisationsDTO.setShowTwitter(value.equalsIgnoreCase("true") ? true : false);
				continue;
			}
			if (param.equalsIgnoreCase(ConfigurationParameter.SHOW_PROGRESSION_BAR)) {
				customisationsDTO.setShowProgressionBar(value.equalsIgnoreCase("true") ? true : false);
				continue;
			}
			if (param.equalsIgnoreCase(ConfigurationParameter.SHOW_SOCIAL_MEDIA)) {
				customisationsDTO.setShowSocialMedia(value.equalsIgnoreCase("true") ? true : false);
				continue;
			}

			if (param.equalsIgnoreCase(ConfigurationParameter.LOGO_PATH)) {
				if (value.equals("") || value.equals(ConfigurationService.Parameter.LOGO_PATH.getDefaultValue())) {
					customisationsDTO.setCustomLogo(false);
				} else {
					customisationsDTO.setCustomLogo(true);
				}
				continue;
			}

			if (param.equalsIgnoreCase(ConfigurationParameter.SIGNATURE_GOAL)) {
				try {
					customisationsDTO.setSignatureGoal(Long.parseLong(value));
				} catch (NumberFormatException nfe) {
					// do nothing
				}
				continue;
			}
			if (param.equalsIgnoreCase(ConfigurationParameter.COLOR_PICKER)) {
				customisationsDTO.setColorPicker(value);
				continue;
			}
			if (param.equalsIgnoreCase(ConfigurationParameter.BACKGROUND)) {
				customisationsDTO.setBackground(Integer.parseInt(value));
				continue;
			}
			if (param.equalsIgnoreCase(ConfigurationParameter.LOGO_ALT_TXT)) {
				customisationsDTO.setAlternateLogoText(value);
				continue;
			}

			if (param.equalsIgnoreCase(ConfigurationParameter.LAST_UPDATE_SETTINGS)) {
				customisationsDTO.setLastUpdateSettings(value);
				continue;
			}

			if (param.equalsIgnoreCase(ConfigurationParameter.LAST_UPDATE_SOCIAL)) {
				customisationsDTO.setLastUpdateSocial(value);
				continue;
			}
		}

		return customisationsDTO;
	}

}
