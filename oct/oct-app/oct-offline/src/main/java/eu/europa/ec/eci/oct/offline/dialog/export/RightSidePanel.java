package eu.europa.ec.eci.oct.offline.dialog.export;

import eu.europa.ec.eci.oct.offline.business.FileType;
import eu.europa.ec.eci.oct.offline.startup.CryptoOfflineConfig;
import eu.europa.ec.eci.oct.offline.startup.UserConfigProperty;
import eu.europa.ec.eci.oct.offline.support.Utils;
import eu.europa.ec.eci.oct.offline.support.swing.localization.LocalizedJButton;
import eu.europa.ec.eci.oct.offline.support.swing.localization.LocalizedJLabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * @author: micleva
 * @created: 11/14/11
 * @project OCT
 */
public class RightSidePanel extends JPanel {

    private static final long serialVersionUID = -1302465560571524404L;

    private Container parent;

    // fields which will be needed outside
    private File outputFolder;
    private JButton startDecryptionButton;
    private JLabel totalNumberOfFilesLabel;
    private JLabel selectedFilesSize;

    private JComboBox fileTypesSelection;
    private File lastAccessedFolder;

    public RightSidePanel(Container parent) {
        super();

        this.parent = parent;

        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // show the summary panel
        JPanel summaryPanel = buildSelectionSummaryPanel();
        summaryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.add(summaryPanel);

        JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
//        separator.setBackground(Color.blue);
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.add(separator);

        //build the output folders and the output file type
        JPanel outputFolderAndTypePanel = buildOutputFolderAndTypePanel();
        outputFolderAndTypePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.add(outputFolderAndTypePanel);

        this.add(Box.createVerticalGlue());

        //build the output folders and the output file type
        JPanel buttonsPanel = buildButtonsPanel();
        buttonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.add(buttonsPanel);

        //initialize the lastAccessedFolder
        String lastOutputFolder = CryptoOfflineConfig.getInstance().getStringUserConfigValue(UserConfigProperty.LAST_OUTPUT_FOLDER);

        if (lastOutputFolder != null) {
            lastAccessedFolder = new File(lastOutputFolder);
        }
    }

    private JPanel buildButtonsPanel() {

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));

        startDecryptionButton = new LocalizedJButton("decrypt.export.dialog.start.decryption.process");
        buttonsPanel.add(startDecryptionButton);

        return buttonsPanel;
    }

    private JPanel buildSelectionSummaryPanel() {
        JPanel selectionSummary = new JPanel();
        selectionSummary.setLayout(new BoxLayout(selectionSummary, BoxLayout.Y_AXIS));
//        selectionSummary.setMaximumSize(new Dimension(500, 150));

        JLabel label = new LocalizedJLabel("decrypt.export.dialog.files.summary.title");
        label.setFont(new Font("", Font.BOLD, 12));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        selectionSummary.add(label);
        selectionSummary.add(Utils.getYSeparator(2));

        //build the line displaying the number of files selected
        JPanel summaryLinePanel = new JPanel();
        summaryLinePanel.setLayout(new BoxLayout(summaryLinePanel, BoxLayout.X_AXIS));
        summaryLinePanel.add(new LocalizedJLabel("decrypt.export.dialog.files.summary.selected.files.total",
                "decrypt.export.dialog.files.summary.selected.files.total.tooltip"));
        summaryLinePanel.add(Utils.getXSeparator(3));
        totalNumberOfFilesLabel = new JLabel("0");
        summaryLinePanel.add(totalNumberOfFilesLabel);
        summaryLinePanel.add(Box.createGlue());

        summaryLinePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        selectionSummary.add(summaryLinePanel);

        //build the line displaying the file size
        summaryLinePanel = new JPanel();
        summaryLinePanel.setLayout(new BoxLayout(summaryLinePanel, BoxLayout.X_AXIS));
        summaryLinePanel.add(new LocalizedJLabel("decrypt.export.dialog.files.summary.selected.files.size.total",
                "decrypt.export.dialog.files.summary.selected.files.size.total.tooltip"));
        summaryLinePanel.add(Utils.getXSeparator(3));
        selectedFilesSize = new JLabel("0");
        summaryLinePanel.add(selectedFilesSize);

        summaryLinePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        selectionSummary.add(summaryLinePanel);

        return selectionSummary;
    }

    private JPanel buildOutputFolderAndTypePanel() {

        JPanel outputFolderAndTypePanel = new JPanel();
        outputFolderAndTypePanel.setLayout(new BoxLayout(outputFolderAndTypePanel, BoxLayout.Y_AXIS));

        //the first line, representing the
        JPanel outputFolderPanel = new JPanel();
        outputFolderPanel.setLayout(new BoxLayout(outputFolderPanel, BoxLayout.X_AXIS));

        JLabel label = new LocalizedJLabel("decrypt.export.dialog.output.label");
        outputFolderPanel.add(label);

        //add the folder name
        final JTextField outputFolderText = new JTextField();
        outputFolderText.setEditable(false);
        outputFolderText.setPreferredSize(new Dimension(150, 25));
        outputFolderPanel.add(Box.createHorizontalGlue());
        outputFolderPanel.add(outputFolderText);

        outputFolderPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        outputFolderAndTypePanel.add(outputFolderPanel);

        //add the browse button, alone on one line, on the right
        JButton browseFolder = new LocalizedJButton("decrypt.export.dialog.output.browse");
        browseFolder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser(lastAccessedFolder);
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                int returnVal = chooser.showOpenDialog(parent);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    outputFolder = chooser.getSelectedFile();
                    outputFolderText.setText(outputFolder.getAbsolutePath());
                    outputFolderText.setToolTipText(outputFolder.getAbsolutePath());
                    outputFolderText.setCaretPosition(0);
                }
                //if the folder has been changed, store it
                if (!lastAccessedFolder.equals(chooser.getCurrentDirectory())) {
                    lastAccessedFolder = chooser.getCurrentDirectory();
                    CryptoOfflineConfig.getInstance().writeUserConfigValue(
                            UserConfigProperty.LAST_OUTPUT_FOLDER, lastAccessedFolder.getAbsolutePath());
                }
            }
        });
        JPanel browsePanel = new JPanel();
        browsePanel.setLayout(new BoxLayout(browsePanel, BoxLayout.X_AXIS));

        browsePanel.add(Box.createHorizontalGlue());
        browsePanel.add(browseFolder);
        browsePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        outputFolderAndTypePanel.add(browsePanel);

        JPanel outputFileTypeSelectionPanel = new JPanel();
        outputFileTypeSelectionPanel.setLayout(new BoxLayout(outputFileTypeSelectionPanel, BoxLayout.X_AXIS));
//        outputFileTypeSelectionPanel.setMaximumSize(new Dimension(500, 150));

        label = new LocalizedJLabel("decrypt.export.dialog.output.file.type");
//        label.setPreferredSize(new Dimension(85, 25));
        outputFileTypeSelectionPanel.add(label);
        fileTypesSelection = new JComboBox(FileType.values());
        fileTypesSelection.setPreferredSize(new Dimension(150, 25));
        fileTypesSelection.setSelectedIndex(0);
        outputFileTypeSelectionPanel.add(Box.createHorizontalGlue());
        outputFileTypeSelectionPanel.add(fileTypesSelection);

        outputFileTypeSelectionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        outputFolderAndTypePanel.add(outputFileTypeSelectionPanel);

        JPanel outputPanel = new JPanel();
        outputPanel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.CENTER;
        outputPanel.add(outputFolderAndTypePanel, constraints);

        return outputPanel;
    }

    public File getOutputFolder() {
        return outputFolder;
    }

    public JButton getStartDecryptionButton() {
        return startDecryptionButton;
    }

    public JLabel getTotalNumberOfFilesLabel() {
        return totalNumberOfFilesLabel;
    }

    public JLabel getSelectedFilesSize() {
        return selectedFilesSize;
    }

    public FileType getSelectedOutputFileType() {
        return (FileType) fileTypesSelection.getSelectedItem();
    }
}
