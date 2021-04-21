package eu.europa.ec.eci.oct.offline.dialog.export;

import eu.europa.ec.eci.oct.offline.dialog.export.details.DecryptDetailsDialog;
import eu.europa.ec.eci.oct.offline.startup.CryptoOfflineTool;
import eu.europa.ec.eci.oct.offline.support.Utils;
import eu.europa.ec.eci.oct.offline.support.swing.localization.LocalizedJButton;
import eu.europa.ec.eci.oct.offline.support.swing.localization.LocalizedJLabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.TextAttribute;
import java.util.Hashtable;
import java.util.Map;

/**
 * @author: micleva
 * @created: 11/14/11
 * @project OCT
 */
public class DecryptProgressDetailsPanel extends JPanel {

	private static final long serialVersionUID = -5053657074644520427L;
	
	private JLabel currentStatusLabel;
    private JLabel elapsedTimeValue;
    private JProgressBar progressBar;

    private JButton minimizeButton;
    private JButton pauseResumeButton;
    private JButton cancelButton;
    private DecryptDetailsDialog detailsDialog;

    public DecryptProgressDetailsPanel(JDialog parent) {
        super();

        detailsDialog = new DecryptDetailsDialog(parent);
        parent.addWindowListener( new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
                detailsDialog.close();
			}
		});

        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel decryptionSummaryLabel = buildDecryptionSummaryLabel();
        decryptionSummaryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.add(decryptionSummaryLabel);
        this.add(Utils.getYSeparator(3));

        //add the status processing label
        JPanel progressStatusPanel = buildStatusProgressPanel();
        progressStatusPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.add(progressStatusPanel);

        //add the elapsed time
        JPanel elapsedTimePanel = buildElapsedTimePanel();
        elapsedTimePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.add(elapsedTimePanel);

        //add the progress bar
        this.add(Utils.getYSeparator(3));

        // just to have an initial max value. Once the process will start,
        // the maximum will be the total number of files to be processed
        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(this.getSize().width, 25));
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.add(progressBar);

        //add buttons bar controlling the decryption process
        JPanel buttonsPanelPanel = buildButtonsPanel();
        buttonsPanelPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.add(buttonsPanelPanel);
    }

    private JPanel buildButtonsPanel() {
        JPanel buttonsPanelPanel = new JPanel();
        buttonsPanelPanel.setLayout(new BoxLayout(buttonsPanelPanel, BoxLayout.X_AXIS));
        buttonsPanelPanel.add(Box.createHorizontalGlue());

        minimizeButton = new LocalizedJButton("decrypt.export.dialog.progress.button.minimize");
        buttonsPanelPanel.add(minimizeButton);
        minimizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame mainFrame = CryptoOfflineTool.getInstance().getFrame();
                mainFrame.setState(JFrame.ICONIFIED);
            }
        });

        pauseResumeButton = new LocalizedJButton(PauseButtonState.PAUSE.getMessageKey());
        pauseResumeButton.setPreferredSize(new Dimension(80, minimizeButton.getHeight()));
        pauseResumeButton.setActionCommand(PauseButtonState.PAUSE.name());
        pauseResumeButton.setEnabled(false);
        buttonsPanelPanel.add(pauseResumeButton);
        cancelButton = new LocalizedJButton("decrypt.export.dialog.progress.button.cancel");
        cancelButton.setEnabled(false);
        buttonsPanelPanel.add(cancelButton);
        return buttonsPanelPanel;
    }

    private JPanel buildElapsedTimePanel() {
        JPanel elapsedTimePanel = new JPanel();
        elapsedTimePanel.setLayout(new BoxLayout(elapsedTimePanel, BoxLayout.X_AXIS));

        JLabel elapsedTimeLabel = new LocalizedJLabel("decrypt.export.dialog.progress.elapsedTime");
        elapsedTimePanel.add(elapsedTimeLabel);
        elapsedTimePanel.add(Utils.getXSeparator(5));

        elapsedTimeValue = new JLabel(Utils.getFormattedElapsedTimeSince(System.currentTimeMillis()));
        elapsedTimePanel.add(elapsedTimeValue);
        return elapsedTimePanel;
    }

    private JPanel buildStatusProgressPanel() {
        JPanel progressStatusPanel = new JPanel();
        progressStatusPanel.setLayout(new BoxLayout(progressStatusPanel, BoxLayout.X_AXIS));

        JLabel statusLabel = new LocalizedJLabel("decrypt.export.dialog.progress.status");
        progressStatusPanel.add(statusLabel);
        progressStatusPanel.add(Utils.getXSeparator(3));

        currentStatusLabel = new LocalizedJLabel("decrypt.export.dialog.progress.status.waiting");
        progressStatusPanel.add(currentStatusLabel);

        progressStatusPanel.add(Box.createHorizontalGlue());
        LocalizedJButton detailsButton = new LocalizedJButton("decrypt.details.dialog.button.label");
        progressStatusPanel.add(detailsButton);

        //add the action for details button
        detailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                detailsDialog.show();
            }
        });

        return progressStatusPanel;
    }

    private JLabel buildDecryptionSummaryLabel() {
        JLabel decryptionSummaryLabel = new LocalizedJLabel("decrypt.export.dialog.progress.title");
        //do the font bold and underlined
        Font summaryFont = decryptionSummaryLabel.getFont();
        summaryFont = summaryFont.deriveFont(Font.BOLD);
        Map<TextAttribute, Object> map = new Hashtable<TextAttribute, Object>();
        map.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        summaryFont = summaryFont.deriveFont(map);
        decryptionSummaryLabel.setFont(summaryFont);
        return decryptionSummaryLabel;
    }

    public void focusMinimizeButton() {
        minimizeButton.requestFocus();
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    public JButton getPauseResumeButton() {
        return pauseResumeButton;
    }

    public JLabel getCurrentStatusLabel() {
        return currentStatusLabel;
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    public JLabel getElapsedTimeValue() {
        return elapsedTimeValue;
    }

    public DecryptDetailsDialog getDetailsDialog() {
        return detailsDialog;
    }
}
