package eu.europa.ec.eci.oct.offline.support.swing.localization;

import eu.europa.ec.eci.oct.offline.support.localization.LocalizationProvider;
import eu.europa.ec.eci.oct.offline.support.localization.adapter.AbstractLocalizationAdapter;

import javax.swing.*;

/**
 * @author: micleva
 * @created: 11/8/11
 * @project OCT
 */
public class LocalizedJLabel extends JLabel {

	private static final long serialVersionUID = -1183143327498804542L;
	
	private AbstractLocalizationAdapter textLabelLocalization;
    private AbstractLocalizationAdapter toolTipLocalization;

    public LocalizedJLabel(String messageKey) {
        super(messageKey);

        final JLabel jLabel = this;
        textLabelLocalization = new AbstractLocalizationAdapter(messageKey) {
			private static final long serialVersionUID = 4347247212352037049L;

			@Override
            protected void setLocalizedText(String localizedText) {
                jLabel.setText(localizedText);
            }
        };
    }


    public LocalizedJLabel(String messageKey, String toolTipKey) {
        super(messageKey);

        setToolTipText(toolTipKey);


        final JLabel jLabel = this;
        textLabelLocalization = new AbstractLocalizationAdapter(messageKey) {
			private static final long serialVersionUID = 775823170387802878L;

			@Override
            protected void setLocalizedText(String localizedText) {
                jLabel.setText(localizedText);
            }
        };

        toolTipLocalization = new AbstractLocalizationAdapter(toolTipKey) {
			private static final long serialVersionUID = 6163475530706228120L;

			@Override
            protected void setLocalizedText(String localizedText) {
                jLabel.setToolTipText(localizedText);
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

    public void setLocalizedToolTipText(String messageKey, String... messageKeyArgs) {
        if(toolTipLocalization != null) {
            toolTipLocalization.setMessageKey(messageKey);
            toolTipLocalization.setMessageKeyArgs(messageKeyArgs);
            toolTipLocalization.updateMessages(LocalizationProvider.getInstance().getCurrentMessageProvider());
        }
    }
}
