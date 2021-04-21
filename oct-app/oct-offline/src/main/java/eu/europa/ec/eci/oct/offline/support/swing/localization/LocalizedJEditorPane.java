package eu.europa.ec.eci.oct.offline.support.swing.localization;

import java.io.IOException;

import eu.europa.ec.eci.oct.offline.support.localization.LocalizationProvider;
import eu.europa.ec.eci.oct.offline.support.localization.adapter.AbstractLocalizationAdapter;

import javax.swing.*;

/**
 * @author: micleva
 * @created: 11/8/11
 * @project OCT
 */
public class LocalizedJEditorPane extends JEditorPane {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4062399590492822379L;
	
	private AbstractLocalizationAdapter textLabelLocalization;

    public LocalizedJEditorPane(String messageKey) throws IOException {
        //super(messageKey);

        final JEditorPane j = this;
        textLabelLocalization = new AbstractLocalizationAdapter(messageKey) {

			/**
			 * 
			 */
			private static final long serialVersionUID = -1642814588550156938L;

			@Override
            protected void setLocalizedText(String localizedText) {
                j.setText(localizedText);
            }
        };
    }

    public void setLocalizedText(String messageKey, String... messageKeyArgs) {
        if (textLabelLocalization != null) {
            textLabelLocalization.setMessageKey(messageKey);
            textLabelLocalization.setMessageKeyArgs(messageKeyArgs);
            textLabelLocalization.updateMessages(LocalizationProvider.getInstance().getCurrentMessageProvider());
        }
    }
}
