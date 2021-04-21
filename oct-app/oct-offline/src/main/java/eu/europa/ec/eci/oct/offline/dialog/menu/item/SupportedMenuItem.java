package eu.europa.ec.eci.oct.offline.dialog.menu.item;

import eu.europa.ec.eci.oct.offline.support.swing.localization.LocalizedJMenuItem;

import javax.swing.*;
import javax.swing.text.JTextComponent;

/**
 * @author: micleva
 * @created: 1/4/12
 * @project OCT
 */
class SupportedMenuItem extends LocalizedJMenuItem {

    public SupportedMenuItem(String messageKey) {
        super(messageKey);
    }

    void bindSupportedAction(JTextComponent textComponent, String action) {
        Action actions[] = textComponent.getActions();
        this.addActionListener(TextUtilities.findAction(actions, action));
    }
}
