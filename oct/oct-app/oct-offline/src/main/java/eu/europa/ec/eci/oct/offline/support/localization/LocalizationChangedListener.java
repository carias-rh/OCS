package eu.europa.ec.eci.oct.offline.support.localization;

import java.util.Locale;

/**
 * @author: micleva
 * @created: 11/8/11
 * @project OCT
 */
public interface LocalizationChangedListener {

    /**
     * Called after the localization has been changed and all the LocalizedItems
     * registered to the LocalizationProvider updated
     *
     * @param locale the new locale being used
     */
    void onLocalizationChanged(Locale locale);
}
