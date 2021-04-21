package eu.europa.ec.eci.oct.webcommons.services.socialMedia;

import eu.europa.ec.eci.oct.entities.admin.SocialMedia;
import eu.europa.ec.eci.oct.entities.admin.SocialMediaMessage;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;

public interface SocialMediaService {


    /**
     * @param media
     * @param language
     * @return
     * @throws OCTException
     */
    String getMessageForSocialMediaAndLanguage(String media, String language) throws OCTException;

    /**
     * @param media
     * @param language
     * @return
     * @throws OCTException
     */
    SocialMediaMessage getSocialMediaMessage(String media, String language) throws OCTException;

    /**
     * @param socialMediaMessage
     * @throws OCTException
     */
    void saveOrUpdateSocialMessage(SocialMediaMessage socialMediaMessage) throws OCTException;

    /**
     * Media type enumeration (facebook, tweeter, ...)
     */
    enum MediaType {
        FACEBOOK("facebook", "facebook"), TWITTER("twitter", "twitter"), GOOGLE(
                "google", "google"), CALL_FOR_ACTION("callForAction", "callForAction");

        private String key;
        private String defaultValue;

        private MediaType(String key, String defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }

        public String key() {
            return key;
        }

        public String value() {
            return defaultValue;
        }
    }

    enum MediaLanguage {
        CS("CS", "cs"), DA("DA", "da"), DE("DE", "de"), ET("ET", "et"), EL("EL", "el"), EN("EN", "en"), ES("ES", "es"),
        FR("FR", "fr"), IT("IT", "it"), LV("LV", "lv"), LT("LT", "lt"), GA("GA", "ga"), HU("HU", "hu"), MT("MT", "mt"),
        NL("NL", "nl"), PL("PL", "pl"), PT("PT", "pt"), RO("RO", "ro"), SK("SK", "sk"), SL("SL", "sl"), FI("FI", "fi"),
        SV("SV", "sv"), BG("BG", "bg"), HR("HR", "hr");

        private String key;
        private String defaultValue;

        private MediaLanguage(String key, String defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }

        public String key() {
            return key;
        }

        public String value() {
            return defaultValue;
        }
    }

    String proofOfState();

	SocialMedia getSocialMediaByName(String socialMediaName) throws OCTException;

}
