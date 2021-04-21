package eu.europa.ec.eci.oct.offline.startup;

/**
 * @author: micleva
 * @created: 11/30/11
 * @project OCT
 */
public enum ConfigProperty {

    LOCALIZATION_ENABLED("localization.enabled"),

    HELP_ENABLED("help.enabled"),

    HELP_DIALOG_WIDTH("help.dialog.width"),

    HELP_DIALOG_HEIGHT("help.dialog.height"),

    DECRYPT_THREAD_POOL_PRIORITY("decrypt.thread.pool.priority");

    private String key;

    ConfigProperty(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
