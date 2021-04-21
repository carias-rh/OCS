package eu.europa.ec.eci.oct.offline.dialog.export;

import eu.europa.ec.eci.oct.offline.support.swing.localization.LocalizedJMenuItem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author: micleva
 * @created: 11/11/11
 * @project OCT
 */
public class FileListMouseListener extends MouseAdapter {

    private Container parent;

    public FileListMouseListener(Container parent) {
        this.parent = parent;
    }

    public void mousePressed(MouseEvent e){
        if (e.isPopupTrigger())
            showPopup(e);
    }

    public void mouseReleased(MouseEvent e){
        if (e.isPopupTrigger())
            showPopup(e);
    }

    private void showPopup(MouseEvent e){
        final JList jList = (JList) e.getComponent();
        if (jList.getSelectedIndices().length > 0) {

            // open the menu with the remove item
            JPopupMenu filePopupMenu = new JPopupMenu();
            JMenuItem removeItem = new LocalizedJMenuItem("decrypt.export.dialog.files.remove.label");
            removeItem.addActionListener(new RemoveSelectedListActionListener(jList, false, parent));
            filePopupMenu.add(removeItem);

            filePopupMenu.show(e.getComponent(), e.getX(), e.getY());

        }
    }
}
