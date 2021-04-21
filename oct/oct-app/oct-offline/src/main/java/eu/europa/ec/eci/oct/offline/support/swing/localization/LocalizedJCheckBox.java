package eu.europa.ec.eci.oct.offline.support.swing.localization;

import eu.europa.ec.eci.oct.offline.support.localization.LocalizationProvider;
import eu.europa.ec.eci.oct.offline.support.localization.adapter.AbstractLocalizationAdapter;

import javax.swing.*;

/**
 * @author: micleva
 * @created: 11/8/11
 * @project OCT
 */
public class LocalizedJCheckBox extends JCheckBox {

	private static final long serialVersionUID = 2304844211465878376L;
	
	private AbstractLocalizationAdapter textLocalization;
	private AbstractLocalizationAdapter toolTipLocalization;
	
    public LocalizedJCheckBox(String messageKey) {
        this(messageKey, (String) null);
    }

    public LocalizedJCheckBox(String messageKey, Icon icon) {
        this(messageKey, (String) null);
        if(icon != null) {
            setIcon(icon);
        }
    }

    public LocalizedJCheckBox(String messageKey, String tooltipKey) {
    	this(messageKey, tooltipKey, null);
    }


    public LocalizedJCheckBox(String messageKey, String tooltipKey, Icon icon) {
        super(messageKey);

        final JCheckBox jCheckBox = this;
        textLocalization = new AbstractLocalizationAdapter(messageKey) {
			private static final long serialVersionUID = -5586723377126522281L;

			@Override
            protected void setLocalizedText(String localizedText) {
				jCheckBox.setText(localizedText);
            }
        };
        if (tooltipKey != null) {
            toolTipLocalization = new AbstractLocalizationAdapter(tooltipKey) {
				private static final long serialVersionUID = 3634685890461915045L;

				@Override
                protected void setLocalizedText(String localizedText) {
					jCheckBox.setToolTipText(localizedText);
                }
            };
        }

        if(icon != null) {
            setIcon(icon);
        }
        
    }
    
    public void setLocalizedText(String messageKey, String... messageKeyArgs) {
        if (textLocalization != null) {
            textLocalization.setMessageKey(messageKey);
            textLocalization.setMessageKeyArgs(messageKeyArgs);
            textLocalization.updateMessages(LocalizationProvider.getInstance().getCurrentMessageProvider());
        }
    }

    public void setLocalizedName(String messageKey, String... messageKeyArgs) {

    	final JCheckBox jCheckBox = this;
        new AbstractLocalizationAdapter(messageKey) {
			private static final long serialVersionUID = -5586723377126522281L;

			@Override
            protected void setLocalizedText(String localizedText) {
				jCheckBox.setName(localizedText);
            }
        };
    }

    public void setLocalizedToolTipText(String messageKey, String... messageKeyArgs) {
        if(toolTipLocalization != null) {
            toolTipLocalization.setMessageKey(messageKey);
            toolTipLocalization.setMessageKeyArgs(messageKeyArgs);
            toolTipLocalization.updateMessages(LocalizationProvider.getInstance().getCurrentMessageProvider());
        }
    }
}
