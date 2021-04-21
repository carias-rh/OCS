package eu.europa.ec.eci.oct.offline.support.localization.adapter;

import java.io.Serializable;

import eu.europa.ec.eci.oct.offline.support.localization.LocalizationMessageProvider;
import eu.europa.ec.eci.oct.offline.support.localization.LocalizationProvider;
import eu.europa.ec.eci.oct.offline.support.localization.LocalizedItem;

/**
 * To be used by any class that needs support for internationalization
 *
 * @author: micleva
 * @created: 11/8/11
 * @project OCT
 */
public abstract class AbstractLocalizationAdapter implements LocalizedItem, Serializable {

	private static final long serialVersionUID = -5859906314986760431L;
	
	private String messageKey;
    private String [] messageKeyArgs;

    protected AbstractLocalizationAdapter(String messageKey, String... args) {
        this.messageKey = messageKey;
        this.messageKeyArgs = args;
        LocalizationProvider localizationProvider = LocalizationProvider.getInstance();
        localizationProvider.registerLocalizedItem(this);
    }

    @Override
    public void updateMessages(LocalizationMessageProvider messageProvider) {
        setLocalizedText(messageProvider.getMessage(messageKey, messageKeyArgs));
    }

    protected abstract void setLocalizedText(String localizedText);

    @Override
    protected void finalize() throws Throwable {
        try {
            LocalizationProvider.getInstance().removeLocalizedItem(this);
        } finally {
            super.finalize();
        }
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public void setMessageKeyArgs(String[] messageKeyArgs) {
        this.messageKeyArgs = messageKeyArgs;
    }
}
