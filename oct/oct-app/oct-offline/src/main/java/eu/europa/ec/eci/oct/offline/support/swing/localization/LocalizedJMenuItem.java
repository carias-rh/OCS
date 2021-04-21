package eu.europa.ec.eci.oct.offline.support.swing.localization;

import eu.europa.ec.eci.oct.offline.support.localization.adapter.AbstractLocalizationAdapter;

import javax.swing.*;

/**
 * @author: micleva
 * @created: 11/8/11
 * @project OCT
 */
public class LocalizedJMenuItem extends JMenuItem {

	private static final long serialVersionUID = -1084565965030827459L;

	public LocalizedJMenuItem(String messageKey) {
        super(messageKey);

        final JMenuItem jMenuItem = this;
        new AbstractLocalizationAdapter(messageKey) {
			private static final long serialVersionUID = -4180974939119788950L;

			@Override
            protected void setLocalizedText(String localizedText) {
                jMenuItem.setText(localizedText);
            }
        };
    }
}
