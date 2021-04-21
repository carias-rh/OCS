package eu.europa.ec.eci.oct.offline.startup;

/**
 * @author: micleva
 * @created: 4/4/12
 * @project ECI
 */
public enum UserConfigProperty {

    LANGUAGE("language"),

    LAST_SELECT_FOLDER("last.selectFolder"),

    LAST_OUTPUT_FOLDER("last.outputFolder"),;

    private String key;

    UserConfigProperty(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
