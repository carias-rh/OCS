package eu.europa.ec.eci.oct.offline.dialog.export;

import eu.europa.ec.eci.oct.offline.startup.CryptoOfflineConfig;
import eu.europa.ec.eci.oct.offline.startup.UserConfigProperty;
import eu.europa.ec.eci.oct.offline.support.Utils;
import eu.europa.ec.eci.oct.offline.support.swing.localization.LocalizedJButton;
import eu.europa.ec.eci.oct.offline.support.swing.localization.LocalizedJLabel;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * @author: micleva
 * @created: 11/14/11
 * @project OCT
 */
public class LeftSidePanel extends JPanel {

    private static final long serialVersionUID = 795320871795341960L;

    private JList fileList;
    private Container parent;
    private File lastAccessedFolder;

    public LeftSidePanel(Container parent) {
        super();
        this.parent = parent;

        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        //add the buttons panel for loading files
        JPanel addFilesButtonPanel = new JPanel();
        addFilesButtonPanel.setLayout(new BoxLayout(addFilesButtonPanel, BoxLayout.X_AXIS));
        addFilesButtonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton loadFilesButton = new LocalizedJButton("decrypt.export.dialog.addFiles");
        addFilesButtonPanel.add(loadFilesButton);
        addFilesButtonPanel.add(Utils.getXSeparator(10));
        JButton loadFoldersButton = new LocalizedJButton("decrypt.export.dialog.addFolders");
        addFilesButtonPanel.add(loadFoldersButton);

        //add the panel containing all the buttons
        this.add(addFilesButtonPanel);
        this.add(Utils.getYSeparator(5));

        //procees with adding the files to be processed label and list
        this.add(new LocalizedJLabel("decrypt.export.dialog.files.toProcess"));

        fileList = new JList(new DefaultListModel());
        fileList.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createEtchedBorder(),
                        BorderFactory.createEmptyBorder(2, 2, 2, 2))));
        fileList.setCellRenderer(new FileListCellRenderer());
        fileList.addMouseListener(new FileListMouseListener(parent));

        JScrollPane scroll = new JScrollPane(fileList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        scroll.setPreferredSize(new Dimension(180, 200));
        scroll.setMinimumSize(new Dimension(180, 200));
        this.add(scroll);

        //set the action for adding files
        loadFilesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "OCT Exported Data Files .xml", "xml");

                File[] selectedFiles = selectFiles(false, filter);
                DefaultListModel listModel = (DefaultListModel) fileList.getModel();
                for (File selectedFile : selectedFiles) {
                    listModel.addElement(selectedFile);
                }
            }
        });

        //set the action for adding folders
        loadFoldersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File[] selectedFiles = selectFiles(true, null);
                DefaultListModel listModel = (DefaultListModel) fileList.getModel();
                for (File selectedFile : selectedFiles) {
                    listModel.addElement(selectedFile);
                }
            }
        });

        this.add(Utils.getYSeparator(5));

        //add the remove and remove all buttons
        JPanel removeFilesButtonPanel = new JPanel();
        removeFilesButtonPanel.setLayout(new BoxLayout(removeFilesButtonPanel, BoxLayout.X_AXIS));
        removeFilesButtonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton removeButton = new LocalizedJButton("decrypt.export.dialog.files.remove.label",
                "decrypt.export.dialog.files.remove.selected.tooltip");
        removeButton.addActionListener(new RemoveSelectedListActionListener(fileList, true, parent));
        removeFilesButtonPanel.add(removeButton);
        removeFilesButtonPanel.add(Utils.getXSeparator(10));
        JButton removeAllButton = new LocalizedJButton("decrypt.export.dialog.files.removeAll.label",
                "decrypt.export.dialog.files.remove.all.tooltip");
        removeFilesButtonPanel.add(removeAllButton);

        //add the action for remove all
        removeAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((DefaultListModel) fileList.getModel()).clear();
            }
        });

        this.add(removeFilesButtonPanel);

        //initialize the lastAccessedFolder
        String lastSelectedFolder = CryptoOfflineConfig.getInstance().getStringUserConfigValue(UserConfigProperty.LAST_SELECT_FOLDER);

        if (lastSelectedFolder != null) {
            lastAccessedFolder = new File(lastSelectedFolder);
        }
    }

    public JList getFileList() {
        return fileList;
    }

    private File[] selectFiles(boolean foldersOnly, FileFilter fileFilter) {
        JFileChooser chooser = new JFileChooser(lastAccessedFolder);
        chooser.setMultiSelectionEnabled(true);

        if (foldersOnly) {
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        } else if (fileFilter != null) {
            chooser.setFileFilter(fileFilter);
        }

        int returnVal = chooser.showOpenDialog(parent);

        File[] selectedFiles;
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            selectedFiles = chooser.getSelectedFiles();
        } else {
            selectedFiles = new File[0];
        }
        if (!lastAccessedFolder.equals(chooser.getCurrentDirectory())) {
            lastAccessedFolder = chooser.getCurrentDirectory();
            CryptoOfflineConfig.getInstance().writeUserConfigValue(
                    UserConfigProperty.LAST_SELECT_FOLDER, lastAccessedFolder.getAbsolutePath());
        }

        return selectedFiles;
    }
}
