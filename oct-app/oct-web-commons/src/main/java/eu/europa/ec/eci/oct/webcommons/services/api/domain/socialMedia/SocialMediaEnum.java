package eu.europa.ec.eci.oct.webcommons.services.api.domain.socialMedia;

public enum SocialMediaEnum {
	FACEBOOK("facebook"), TWITTER("twitter"), GOOGLE_PLUS("google"), CALL_FOR_ACTION("callForAction");

	SocialMediaEnum(String name) {
		this.name = name;
	}

	private String name;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
