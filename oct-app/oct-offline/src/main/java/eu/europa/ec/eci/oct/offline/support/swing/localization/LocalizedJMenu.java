package eu.europa.ec.eci.oct.offline.support.swing.localization;

import eu.europa.ec.eci.oct.offline.support.localization.adapter.AbstractLocalizationAdapter;

import javax.swing.*;

/**
 * @author: micleva
 * @created: 11/8/11
 * @project OCT
 */
public class LocalizedJMenu extends JMenu {

	private static final long serialVersionUID = 5938396426250362190L;

	public LocalizedJMenu(String messageKey) {
        super(messageKey);

        final JMenu jMenu = this;
        new AbstractLocalizationAdapter(messageKey) {
			private static final long serialVersionUID = 4956585067940498459L;

			@Override
            protected void setLocalizedText(String localizedText) {
                jMenu.setText(localizedText);
            }
        };
    }
}
