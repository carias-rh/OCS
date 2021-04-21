package eu.europa.ec.eci.oct.offline.dialog.export;

import eu.europa.ec.eci.oct.offline.support.summary.SelectionSummary;
import eu.europa.ec.eci.oct.offline.support.summary.SelectionSummaryCalculator;
import eu.europa.ec.eci.oct.offline.support.summary.SummaryCalculatorListener;
import eu.europa.ec.eci.oct.offline.support.swing.localization.LocalizedJButton;
import eu.europa.ec.eci.oct.offline.support.swing.localization.LocalizedJDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileFilter;

/**
 * @author: micleva
 * @created: 11/10/11
 * @project OCT
 */
public class DecryptExportDialog {
    private JDialog jDialog;
    private JFrame frame;

    //fields used for doing the linkage between the decryption progress panel and the right located panel

    public DecryptExportDialog(JFrame owner) {
        jDialog = new LocalizedJDialog(owner, "decrypt.export.dialog.title");
        this.frame = owner;

        final SelectionSummaryCalculator selectionSummaryCalculator = new SelectionSummaryCalculator(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory() || file.getName().endsWith(".xml");
            }
        });

        //by default destroy all the objects associated with this dialog upon close
        jDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        jDialog.addWindowListener( new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
                selectionSummaryCalculator.resetSummaryCalculator();
			}
		});
        //the dialog needs to be modal in order to not allow interactions with the
        // behind components while this one is used
        jDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

        //add widgets to the dialog
        addWidgets(selectionSummaryCalculator);

        jDialog.setResizable(false);
    }

    private void addWidgets(SelectionSummaryCalculator selectionSummaryCalculator) {

        Container dialogContainer = jDialog.getContentPane();
        dialogContainer.setLayout(new BoxLayout(dialogContainer, BoxLayout.Y_AXIS));

        //create the upper part of the dialog composed of the left and the right side panels
        LeftSidePanel leftSidePanel = new LeftSidePanel(jDialog);
        RightSidePanel rightSidePanel = new RightSidePanel(jDialog);
        JPanel upperPartPanel = buildUpperPanel(leftSidePanel, rightSidePanel, selectionSummaryCalculator);
        upperPartPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        dialogContainer.add(upperPartPanel);

        //add an horizontal separator
        JSeparator horizontalSeparator = new JSeparator(JSeparator.HORIZONTAL);
        horizontalSeparator.setAlignmentX(Component.LEFT_ALIGNMENT);
        dialogContainer.add(horizontalSeparator);

        //now add the progress panel
        DecryptProgressDetailsPanel decryptProgressDetailsPanel = new DecryptProgressDetailsPanel(jDialog);
        decryptProgressDetailsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        dialogContainer.add(decryptProgressDetailsPanel);

        //add another horizontal separator
        horizontalSeparator = new JSeparator(JSeparator.HORIZONTAL);
        horizontalSeparator.setAlignmentX(Component.LEFT_ALIGNMENT);
        dialogContainer.add(horizontalSeparator);

        //add the close button
        JPanel closeButtonPanel = buildCloseButtonPanel();
        closeButtonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        dialogContainer.add(closeButtonPanel);

        //link the progress details panel widgets with the right panel widgets
        DecryptWorkflowHelper decryptWorkflowHelper = new DecryptWorkflowHelper(jDialog, decryptProgressDetailsPanel);
        decryptWorkflowHelper.createDecryptionWorkflow(rightSidePanel, selectionSummaryCalculator);
    }

    private JPanel buildUpperPanel(LeftSidePanel leftSidePanel, RightSidePanel rightPanel, SelectionSummaryCalculator selectionSummaryCalculator) {
        JPanel upperPartPanel = new JPanel();
        upperPartPanel.setLayout(new BoxLayout(upperPartPanel, BoxLayout.X_AXIS));

        //build the left part containing the input files
        JList fileList = leftSidePanel.getFileList();
        upperPartPanel.add(leftSidePanel);

        JSeparator separator = new JSeparator(JSeparator.VERTICAL);
        upperPartPanel.add(separator);

        //build the right part containing the output folder
        upperPartPanel.add(rightPanel);

        final JLabel totalNumberOfFilesLabel = rightPanel.getTotalNumberOfFilesLabel();
        final JLabel selectedFilesSizeLabel = rightPanel.getSelectedFilesSize();
        final DefaultListModel listModel = (DefaultListModel) fileList.getModel();

        //add the updater of the summary labels
        selectionSummaryCalculator.addSummaryCalculatorListener(new SummaryCalculatorListener() {
            @Override
            public void afterSelectionSummaryChanged(SelectionSummary selectionSummary) {

                totalNumberOfFilesLabel.setText(String.valueOf(selectionSummary.getFileInTotal()));
                selectedFilesSizeLabel.setText(selectionSummary.getPrettyFormattedFileSize());
            }
        });

        listModel.addListDataListener(new FileSummaryDataListener(selectionSummaryCalculator));

        return upperPartPanel;
    }

    private JPanel buildCloseButtonPanel() {
        JButton closeButton = new LocalizedJButton("common.close");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jDialog.dispatchEvent(new WindowEvent(jDialog, WindowEvent.WINDOW_CLOSING));
            }
        });

        JPanel closeButtonPanel = new JPanel();
        closeButtonPanel.setLayout(new BoxLayout(closeButtonPanel, BoxLayout.X_AXIS));
        closeButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        closeButtonPanel.add(closeButton);

        return closeButtonPanel;
    }

    public void show() {
        jDialog.pack();
        jDialog.setLocationRelativeTo(frame);
        jDialog.setVisible(true);
    }
}
