package eu.europa.ec.eci.oct.offline.support.swing.localization;

import eu.europa.ec.eci.oct.offline.support.localization.adapter.AbstractLocalizationAdapter;

import javax.swing.*;
import java.awt.*;

/**
 * @author: micleva
 * @created: 11/9/11
 * @project OCT
 */
public class LocalizedJDialog extends JDialog {

	private static final long serialVersionUID = 4686128210955779288L;

	public LocalizedJDialog(Frame owner, String titleKey) {
        super(owner, titleKey);

        final JDialog jDialog = this;
        new AbstractLocalizationAdapter(titleKey) {
			private static final long serialVersionUID = -5364734650078954878L;

			@Override
            protected void setLocalizedText(String localizedText) {
                jDialog.setTitle(localizedText);
            }
        };
    }

    public LocalizedJDialog(JDialog owner, String titleKey) {
        super(owner, titleKey);

        final JDialog jDialog = this;
        new AbstractLocalizationAdapter(titleKey) {
			private static final long serialVersionUID = -2349539574664473681L;

			@Override
            protected void setLocalizedText(String localizedText) {
                jDialog.setTitle(localizedText);
            }
        };
    }
}
