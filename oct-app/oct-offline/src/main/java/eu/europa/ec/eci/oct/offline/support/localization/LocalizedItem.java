package eu.europa.ec.eci.oct.offline.support.localization;

/**
 * @author: micleva
 * @created: 11/8/11
 * @project OCT
 */
public interface LocalizedItem {

    /**
     * Called whenever is needed for the LocalizedItem to updated its message according to the provided
     * Localization message provider. More exactly, it will be called the first time when a LocalizedItem
     * register to the LocalizationProvider and afterwords whenever the localization is changed
     *
     * @param messageProvider the localization message provider providing messages according to the current locale
     */
    void updateMessages(LocalizationMessageProvider messageProvider);
}
