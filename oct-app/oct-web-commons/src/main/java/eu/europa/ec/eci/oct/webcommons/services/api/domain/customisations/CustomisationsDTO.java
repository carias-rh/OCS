package eu.europa.ec.eci.oct.webcommons.services.api.domain.customisations;

import java.io.Serializable;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class CustomisationsDTO implements Serializable {

	private static final long serialVersionUID = 5350100698095887659L;
	public static final String HTTP_PREFIX = "http://";
	private Boolean showProgressionBar;
	private Boolean showDistributionMap;
	private Boolean showRecentSupporters;
	private Boolean optionalValidation;
	private Boolean customLogo = false;
	private Boolean showSocialMedia;
	private Boolean showFacebook;
	private Boolean showTwitter;
	private Boolean showGoogle;
	private Long signatureGoal;
	private String facebookUrl;
	private String twitterUrl;
	private String googleUrl;
	private String callbackUrl;
	private String colorPicker;
	private String lastUpdateSettings;
	private String lastUpdateSocial;
	private String alternateLogoText;
	private Integer background;

	public CustomisationsDTO() {
	}

	public Boolean isShowProgressionBar() {
		return showProgressionBar;
	}

	public void setShowProgressionBar(Boolean showProgressionBar) {
		this.showProgressionBar = showProgressionBar;
	}

	public Boolean isShowDistributionMap() {
		return showDistributionMap;
	}

	public void setShowDistributionMap(Boolean showDistributionMap) {
		this.showDistributionMap = showDistributionMap;
	}

	public Boolean isOptionalValidation() {
		return optionalValidation;
	}

	public void setOptionalValidation(Boolean optionalValidation) {
		this.optionalValidation = optionalValidation;
	}

	public Boolean isCustomLogo() {
		return customLogo;
	}

	public void setCustomLogo(Boolean customLogo) {
		this.customLogo = customLogo;
	}

	public Boolean isShowSocialMedia() {
		return showSocialMedia;
	}

	public void setShowSocialMedia(Boolean showSocialMedia) {
		this.showSocialMedia = showSocialMedia;
	}

	public Boolean isShowFacebook() {
		return showFacebook;
	}

	public void setShowFacebook(Boolean showFacebook) {
		this.showFacebook = showFacebook;
	}

	public Boolean isShowTwitter() {
		return showTwitter;
	}

	public void setShowTwitter(Boolean showTwitter) {
		this.showTwitter = showTwitter;
	}

	public Boolean isShowGoogle() {
		return showGoogle;
	}

	public void setShowGoogle(Boolean showGoogle) {
		this.showGoogle = showGoogle;
	}

	public Long getSignatureGoal() {
		return signatureGoal;
	}

	public void setSignatureGoal(Long signatureGoal) {
		this.signatureGoal = signatureGoal;
	}

	public String getFacebookUrl() {
		return facebookUrl;
	}

	public void setFacebookUrl(String facebookUrl) {
		this.facebookUrl = facebookUrl;
	}

	public String getTwitterUrl() {
		return twitterUrl;
	}

	public void setTwitterUrl(String twitterUrl) {
		this.twitterUrl = twitterUrl;
	}

	public String getGoogleUrl() {
		return googleUrl;
	}

	public void setGoogleUrl(String googleUrl) {
		this.googleUrl = googleUrl;
	}

	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

	public Boolean getShowRecentSupporters() {
		return showRecentSupporters;
	}

	public void setShowRecentSupporters(Boolean showRecentSupporters) {
		this.showRecentSupporters = showRecentSupporters;
	}

	public String getColorPicker() {
		return colorPicker;
	}

	public void setColorPicker(String colorPicker) {
		this.colorPicker = colorPicker;
	}

	public String getLastUpdateSettings() {
		return lastUpdateSettings;
	}

	public void setLastUpdateSettings(String lastUpdateSettings) {
		this.lastUpdateSettings = lastUpdateSettings;
	}

	public String getLastUpdateSocial() {
		return lastUpdateSocial;
	}

	public void setLastUpdateSocial(String lastUpdateSocial) {
		this.lastUpdateSocial = lastUpdateSocial;
	}

	public Integer getBackground() {
		return background;
	}

	public void setBackground(Integer background) {
		this.background = background;
	}

	public String getAlternateLogoText() {
		return alternateLogoText;
	}

	public void setAlternateLogoText(String alternateLogoText) {
		this.alternateLogoText = alternateLogoText;
	}

	@Override
	public String toString() {
		return "CustomisationsDTO [showProgressionBar=" + showProgressionBar + ", showDistributionMap="
				+ showDistributionMap + ", showRecentSupporters=" + showRecentSupporters + ", optionalValidation="
				+ optionalValidation + ", customLogo=" + customLogo + ", showSocialMedia=" + showSocialMedia
				+ ", showFacebook=" + showFacebook + ", showTwitter=" + showTwitter + ", showGoogle=" + showGoogle
				+ ", signatureGoal=" + signatureGoal + ", facebookUrl=" + facebookUrl + ", twitterUrl=" + twitterUrl
				+ ", googleUrl=" + googleUrl + ", callbackUrl=" + callbackUrl + ", colorPicker=" + colorPicker
				+ ", lastUpdateSettings=" + lastUpdateSettings + ", lastUpdateSocial=" + lastUpdateSocial
				+ ", alternateLogoText=" + alternateLogoText + ", background=" + background + "]";
	}

}
