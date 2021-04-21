package eu.europa.ec.eci.oct.offline.dialog.menu;

import eu.europa.ec.eci.oct.offline.dialog.menu.item.CopyMenuItemProvider;
import eu.europa.ec.eci.oct.offline.dialog.menu.item.PasteMenuItemProvider;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author: micleva
 * @created: 1/4/12
 * @project OCT
 */
public class CopyPasteContextualMenu extends MouseAdapter {

    public void mousePressed(MouseEvent e){
        if (e.isPopupTrigger())
            showPopup(e);
    }

    public void mouseReleased(MouseEvent e){
        if (e.isPopupTrigger())
            showPopup(e);
    }

    private void showPopup(MouseEvent e){
        final Component component = e.getComponent();
        if (component instanceof JTextComponent) {

            JTextComponent textComponent = (JTextComponent) component;

            if (textComponent.isEnabled()) {
                textComponent.requestFocus();
                textComponent.selectAll();
                // open the menu with the copy/paste items
                JPopupMenu filePopupMenu = new JPopupMenu();
                filePopupMenu.add(CopyMenuItemProvider.forItem(textComponent));
                filePopupMenu.add(PasteMenuItemProvider.forItem(textComponent));

                filePopupMenu.show(e.getComponent(), e.getX(), e.getY());
            }

        }
    }
}
