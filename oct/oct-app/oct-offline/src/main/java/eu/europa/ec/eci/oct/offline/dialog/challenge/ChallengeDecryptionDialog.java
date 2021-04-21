package eu.europa.ec.eci.oct.offline.dialog.challenge;

import eu.europa.ec.eci.oct.offline.dialog.menu.CopyPasteContextualMenu;
import eu.europa.ec.eci.oct.offline.support.Utils;
import eu.europa.ec.eci.oct.offline.support.swing.localization.LocalizedJButton;
import eu.europa.ec.eci.oct.offline.support.swing.localization.LocalizedJDialog;
import eu.europa.ec.eci.oct.offline.support.swing.localization.LocalizedJLabel;

import javax.swing.*;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author: micleva
 * @created: 11/9/11
 * @project OCT
 */
public class ChallengeDecryptionDialog {

    private JDialog jDialog;
    private JFrame frame;
    private JTextField challengeResponse;

    public ChallengeDecryptionDialog(JFrame owner) {
        jDialog = new LocalizedJDialog(owner, "decrypt.login.challenge.dialog.title");
        this.frame = owner;

        //by default destroy all the objects associated with this dialog upon close
        jDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        //the dialog needs to be modal in order to not allow interactions with the
        // behind components while this one is used
        jDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

        //add widgets to the dialog
        addWidgets();
    }

    private void addWidgets() {
        JPanel challengePanel = new JPanel();
        challengePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        challengePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        challengePanel.setLayout(new BoxLayout(challengePanel, BoxLayout.Y_AXIS));

        //add the
        JLabel label = new LocalizedJLabel("decrypt.login.challenge.dialog.challenge.label");
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        challengePanel.add(label);
        challengePanel.add(Utils.getYSeparator(3));

        JTextArea challengeTextArea = new JTextArea(10, 30);
        challengeTextArea.setBackground(Color.white);
        challengeTextArea.setLineWrap(true);
        challengeTextArea.addMouseListener(new CopyPasteContextualMenu());
        final JScrollPane scrollPane = new JScrollPane(challengeTextArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        challengePanel.add(scrollPane);
        challengePanel.add(Utils.getYSeparator(5));

        challengeResponse = new JTextField(30);
        challengeResponse.setEditable(false);
        challengeResponse.setAlignmentX(Component.LEFT_ALIGNMENT);
        challengeResponse.addMouseListener(new CopyPasteContextualMenu());

        JPanel decryptButtonPanel = new JPanel();
        decryptButtonPanel.setLayout(new BoxLayout(decryptButtonPanel, BoxLayout.X_AXIS));
        decryptButtonPanel.add(Box.createHorizontalGlue());

        JButton decryptButton = new LocalizedJButton("common.decrypt");
        decryptButton.addActionListener(new DecryptActionHandler(challengeTextArea, challengeResponse));
        decryptButtonPanel.add(decryptButton);
        decryptButtonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        challengePanel.add(decryptButtonPanel);

        challengePanel.add(Utils.getYSeparator(10));

        //the challenge response label
        challengePanel.add(new LocalizedJLabel("decrypt.login.challenge.dialog.challenge.response.label"));
        challengePanel.add(Utils.getYSeparator(3));

        //the challenge response text
        challengePanel.add(challengeResponse);
        challengePanel.add(Utils.getYSeparator(5));

        JButton buttonCopy = new LocalizedJButton("decrypt.login.challenge.dialog.button.copyToClipboard");
        buttonCopy.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
			    StringSelection stringSelection = new StringSelection(challengeResponse.getText());
			    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			    clipboard.setContents( stringSelection, null );				
			}
		});
        
        JButton button = new LocalizedJButton("common.close");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jDialog.dispose();
            }
        });
        
        /*
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        challengePanel.add(button);
        buttonCopy.setAlignmentX(Component.RIGHT_ALIGNMENT);
        challengePanel.add(buttonCopy);
*/
        
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        challengePanel.add(buttonPanel);
        buttonPanel.add(buttonCopy);
        buttonPanel.add(Utils.getXSeparator(5));
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(button);
        challengePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        
        jDialog.add(challengePanel);
    }

    public void show() {
        jDialog.pack();
        jDialog.setLocationRelativeTo(frame);
        jDialog.setVisible(true);
    }
}
