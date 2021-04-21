package eu.europa.ec.eci.oct.offline.dialog.export.details;

import eu.europa.ec.eci.oct.offline.business.DecryptTaskStatus;
import eu.europa.ec.eci.oct.offline.support.Utils;
import eu.europa.ec.eci.oct.offline.support.localization.LocalizationMessageProvider;
import eu.europa.ec.eci.oct.offline.support.localization.LocalizationProvider;
import eu.europa.ec.eci.oct.offline.support.swing.localization.LocalizedJButton;
import eu.europa.ec.eci.oct.offline.support.swing.localization.LocalizedJDialog;
import eu.europa.ec.eci.oct.offline.support.swing.localization.LocalizedJLabel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;

/**
 * @author: micleva
 * @created: 11/28/11
 * @project OCT
 */
public class DecryptDetailsDialog {

    private static final BigDecimal ONE_THOUSAND = new BigDecimal(1000);

    private JDialog jDialog;

    private JLabel processedFilesValue;
    private JLabel successfulFilesValue;
    private JLabel failedFilesValue;

    private JDialog parent;
    private boolean positionToRight;
    private DefaultTableModel tableModel;
    private LocalizedJButton moveButton;

    public DecryptDetailsDialog(JDialog parent) {
        super();

        this.parent = parent;

        jDialog = new LocalizedJDialog(parent, "decrypt.details.dialog.details");
        jDialog.setModalityType(Dialog.ModalityType.MODELESS);
        jDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

        Container dialogContainer = jDialog.getContentPane();
        dialogContainer.setLayout(new BoxLayout(dialogContainer, BoxLayout.Y_AXIS));

        //overall file progress values
        JPanel overallDetailsPanel = buildOverallFileProgress();
        overallDetailsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        dialogContainer.add(overallDetailsPanel);

        //add a separator
        JSeparator horizontalSeparator = new JSeparator(JSeparator.HORIZONTAL);
        horizontalSeparator.setAlignmentX(Component.LEFT_ALIGNMENT);
        dialogContainer.add(horizontalSeparator);

        JPanel tablePanel = buildTablePanel();
        tablePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        dialogContainer.add(tablePanel);

        //add the glue
        dialogContainer.add(Box.createVerticalGlue());

        //add a hide button
        JPanel buttonsPanel = buildButtonsPanel();
        buttonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        dialogContainer.add(buttonsPanel);
    }

    private JPanel buildTablePanel() {

        LocalizationMessageProvider messageProvider = LocalizationProvider.getInstance().getCurrentMessageProvider();
        String fileNameColumnLabel = messageProvider.getMessage("decrypt.details.dialog.table.fileName");
        String execTimeColumnLabel = messageProvider.getMessage("decrypt.details.dialog.table.execTime");
        String successColumnLabel = messageProvider.getMessage("decrypt.details.dialog.table.success");
        String additionalMessageColumnLabel = messageProvider.getMessage("decrypt.details.dialog.table.additionalMsg");

        tableModel = new DefaultTableModel(new Object[]{fileNameColumnLabel, execTimeColumnLabel,
                successColumnLabel, additionalMessageColumnLabel}, 0);

        JTable table = new JTable(tableModel) {
			private static final long serialVersionUID = -2172178703055504374L;

			public Component prepareRenderer(TableCellRenderer renderer,
                                             int rowIndex, int vColIndex) {
                Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
                if (c instanceof JComponent) {
                    JComponent jc = (JComponent) c;
                    jc.setToolTipText((String) getValueAt(rowIndex, vColIndex));
                }
                return c;
            }
        };

        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);

        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.X_AXIS));
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tablePanel.add(scrollPane);

        return tablePanel;
    }

    private JPanel buildButtonsPanel() {
        JButton hideButton = new LocalizedJButton("decrypt.details.dialog.hide.label");
        hideButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jDialog.dispatchEvent(new WindowEvent(jDialog, WindowEvent.WINDOW_CLOSING));
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        buttonPanel.add(hideButton);

        moveButton = new LocalizedJButton("decrypt.details.dialog.move.bottom");
        moveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (positionToRight) {
                    setPositionToBottom();
                } else {
                    setPositionToRight();
                }
            }
        });

        buttonPanel.add(moveButton);

        return buttonPanel;
    }

    private JPanel buildOverallFileProgress() {
        JPanel overallDetailsPanel = new JPanel();
        overallDetailsPanel.setLayout(new BoxLayout(overallDetailsPanel, BoxLayout.Y_AXIS));
        overallDetailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel overallProgressLabel = new LocalizedJLabel("decrypt.details.dialog.file.overall");
        //do the font bold and underlined
        Font summaryFont = overallProgressLabel.getFont();
        summaryFont = summaryFont.deriveFont(Font.BOLD);
        overallProgressLabel.setFont(summaryFont);
        overallProgressLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        overallDetailsPanel.add(overallProgressLabel);

        //add the total files processed
        JPanel totalFileProcessedPanel = new JPanel();
        totalFileProcessedPanel.setLayout(new BoxLayout(totalFileProcessedPanel, BoxLayout.X_AXIS));
        JLabel filedLabel = new LocalizedJLabel("decrypt.details.dialog.file.total");
        totalFileProcessedPanel.add(filedLabel);
        totalFileProcessedPanel.add(Utils.getXSeparator(5));

        processedFilesValue = new JLabel("0");
        totalFileProcessedPanel.add(processedFilesValue);
        totalFileProcessedPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        overallDetailsPanel.add(totalFileProcessedPanel);

        JPanel fileSuccessfulPanel = new JPanel();
        fileSuccessfulPanel.setLayout(new BoxLayout(fileSuccessfulPanel, BoxLayout.X_AXIS));
        filedLabel = new LocalizedJLabel("decrypt.details.dialog.file.successful");
        fileSuccessfulPanel.add(filedLabel);
        fileSuccessfulPanel.add(Utils.getXSeparator(5));

        successfulFilesValue = new JLabel("0");
        successfulFilesValue.setForeground(new Color(34, 139, 34));
        fileSuccessfulPanel.add(successfulFilesValue);
        fileSuccessfulPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        overallDetailsPanel.add(fileSuccessfulPanel);

        JPanel fileFailedPanel = new JPanel();
        fileFailedPanel.setLayout(new BoxLayout(fileFailedPanel, BoxLayout.X_AXIS));
        filedLabel = new LocalizedJLabel("decrypt.details.dialog.file.failed");
        fileFailedPanel.add(filedLabel);
        fileFailedPanel.add(Utils.getXSeparator(5));

        failedFilesValue = new JLabel("0");
        failedFilesValue.setForeground(new Color(178, 34, 34));
        fileFailedPanel.add(failedFilesValue);
        fileFailedPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        overallDetailsPanel.add(fileFailedPanel);

        return overallDetailsPanel;
    }

    public void updateProcessedFiles(int processedFiles) {
        processedFilesValue.setText(String.valueOf(processedFiles));
    }

    public void updateSuccessfulFiles(int successfulFiles) {
        successfulFilesValue.setText(String.valueOf(successfulFiles));
    }

    public void updateFailedFiles(int failedFiles) {
        failedFilesValue.setText(String.valueOf(failedFiles));
    }

    public void show() {
        if(jDialog.isVisible()) {
            jDialog.setVisible(false);
        }

        jDialog.pack();
        setPositionToRight();
    }

    private void setPositionToRight() {
        jDialog.setVisible(false);

        // from right position is possible to moe it to bottom
        moveButton.setLocalizedText("decrypt.details.dialog.move.bottom");

        Point point = parent.getLocationOnScreen();
        Dimension parentSize = parent.getSize();
        //put it on the right with 3 pixels distance between the dialogs
        jDialog.setLocation(point.x + parentSize.width + 3, point.y);

        jDialog.setSize(350, parentSize.height);
        jDialog.setVisible(true);
        positionToRight = true;
    }

    private void setPositionToBottom() {
        jDialog.setVisible(false);

        //from bottom is possible to move it on the right
        moveButton.setLocalizedText("decrypt.details.dialog.move.right");

        Point point = parent.getLocationOnScreen();
        Dimension parentSize = parent.getSize();
        //put it at the bottom with 3 pixels distance between the dialogs
        jDialog.setLocation(point.x, point.y + parentSize.height + 3);

        jDialog.setSize(parentSize.width, 350);
        jDialog.setVisible(true);
        positionToRight = false;
    }

    public void close() {
        jDialog.dispose();
    }

    public void logDecryptTaskStatus(DecryptTaskStatus taskStatus) {
        BigDecimal time = new BigDecimal(taskStatus.getExecTimeInMillis());
        time = time.divide(ONE_THOUSAND, BigDecimal.ROUND_HALF_UP, 2).setScale(2, BigDecimal.ROUND_HALF_UP);
        tableModel.addRow(new Object[]
                {
                        taskStatus.getFileName(),
                        String.valueOf(time),
                        String.valueOf(taskStatus.isSuccessfullyProcessed()),
                        taskStatus.getAdditionalMessage()
                });
    }

    public void resetTableData() {
        tableModel.getDataVector().clear();
        tableModel.fireTableDataChanged();
        updateFailedFiles(0);
        updateProcessedFiles(0);
        updateSuccessfulFiles(0);
    }
}
