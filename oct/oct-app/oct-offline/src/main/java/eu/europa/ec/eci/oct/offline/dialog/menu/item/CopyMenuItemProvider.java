package eu.europa.ec.eci.oct.offline.dialog.menu.item;

import eu.europa.ec.eci.oct.offline.support.swing.localization.LocalizedJMenuItem;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;

/**
 * @author: micleva
 * @created: 1/4/12
 * @project OCT
 */
public class CopyMenuItemProvider {

    public static JMenuItem forItem(JTextComponent textComponent) {
        JMenuItem menuItem = new LocalizedJMenuItem("menu.item.copy");
        Action actions[] = textComponent.getActions();
        menuItem.addActionListener(TextUtilities.findAction(actions,
                DefaultEditorKit.copyAction));

        return menuItem;
    }
}
