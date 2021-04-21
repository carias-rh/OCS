package eu.europa.ec.eci.oct.offline.dialog.export;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * @author: micleva
 * @created: 11/11/11
 * @project OCT
 */
public class FileListCellRenderer extends DefaultListCellRenderer {

	private static final long serialVersionUID = 1601867150169100520L;
	
	private final static Icon fileIcon = UIManager.getIcon("FileView.fileIcon");
    private final static Icon folderIcon = UIManager.getIcon("FileView.directoryIcon");

    public Component getListCellRendererComponent(JList list,
                                                  Object value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean hasFocus) {

        JLabel fileNameLabel = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, hasFocus);

        if (value instanceof File) {
            File file = (File) value;

            //create and add the file icon
            fileNameLabel.setText(file.getName());
            fileNameLabel.setToolTipText(file.getAbsolutePath());
            fileNameLabel.setIcon(file.isFile() ? fileIcon : folderIcon);
        }

        return fileNameLabel;
    }
}
