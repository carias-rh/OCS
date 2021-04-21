package eu.europa.ec.eci.oct.offline.dialog.hashpass;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import eu.europa.ec.eci.oct.offline.dialog.pwd.PasswordDialog;
import eu.europa.ec.eci.oct.offline.startup.CryptoOfflineStatus;
import eu.europa.ec.eci.oct.offline.startup.CryptoOfflineButtonsBar;
import eu.europa.ec.eci.oct.offline.startup.CryptoOfflineTool;
import eu.europa.ec.eci.oct.offline.support.Utils;
import eu.europa.ec.eci.oct.offline.support.localization.LocalizationMessageProvider;
import eu.europa.ec.eci.oct.offline.support.localization.LocalizationProvider;
import eu.europa.ec.eci.oct.offline.support.swing.localization.LocalizedJButton;
import eu.europa.ec.eci.oct.offline.support.swing.localization.LocalizedJLabel;

public class HashPasswordDialog extends PasswordDialog{


	private static final long serialVersionUID = 1438277286825220679L;

	private static HashPasswordDialog instance;

	public static final HashPasswordDialog getInstance(Container parent) {
		if (instance == null) {
			instance = new HashPasswordDialog(parent);
		}
		return instance;
	}
	
	public HashPasswordDialog(Container parent) {
		super(parent, true, true);
	}


	@Override
	protected List<JButton> getAdditionalButtons() {
		JButton cancelButton = new LocalizedJButton("common.close");
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				instance.dispose();
			}
		});
			
		return Collections.singletonList(cancelButton);
	}

	@Override
	public JPanel getExplanatoryPanel() {
		
		// set the glass pane
		JPanel glassPane = new JPanel();
		//glassPane.setPreferredSize(jDialog.getSize());
		Color backColor = new Color(192, 192, 192, 100);
		glassPane.setBackground(backColor);
		glassPane.setLayout(new GridBagLayout());

		JPanel insidePanel = new JPanel();
		insidePanel.setLayout(new BoxLayout(insidePanel, BoxLayout.Y_AXIS));
		insidePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		insidePanel.setBackground(Color.white);
		JLabel progressLabel = new LocalizedJLabel("crypto.init.dialog.generate");
		progressLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		insidePanel.add(progressLabel);

		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);
		insidePanel.add(progressBar);

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.CENTER;
		glassPane.add(insidePanel, constraints);
		this.setGlassPane(glassPane);

		
		JPanel explanatoryPanel = new JPanel();
		explanatoryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		explanatoryPanel.setLayout(new BoxLayout(explanatoryPanel, BoxLayout.Y_AXIS));

		JEditorPane editorPane = new JEditorPane();
    	editorPane.setBorder(BorderFactory.createLoweredBevelBorder());
    	editorPane.setContentType("text/html");

    	//editorPane.setPreferredSize(new Dimension(200, 200));
    	editorPane.setEditable(false);

		LocalizationMessageProvider messageProvider = LocalizationProvider.getInstance().getCurrentMessageProvider();
    	String imgsrcwarning = CryptoOfflineTool.class.getClassLoader().getResource("images/16x16/emblem-warning.png").toString();
    	String text = messageProvider.getMessage("generated.html.decrypt.login.challenge.dialog.password.dsc", "'" + imgsrcwarning + "'");
    	
    	editorPane.setText(text);
    	explanatoryPanel.add(editorPane);

		return explanatoryPanel;
	}
	
	public JPanel buildResponseInitPasswordPanel(){
		JPanel explanatoryPanel = new JPanel();
		instance.setSize(new Dimension(500, 400));
		
		explanatoryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		explanatoryPanel.setLayout(new BoxLayout(explanatoryPanel, BoxLayout.Y_AXIS));

		JEditorPane editorPane = new JEditorPane();
    	editorPane.setBorder(BorderFactory.createLoweredBevelBorder());
    	editorPane.setContentType("text/html");

    	//editorPane.setPreferredSize(new Dimension(200, 200));
    	editorPane.setEditable(false);

    	
	    String imgsrcok = this.getClass().getClassLoader().getResource("images/16x16/emblem-default.png").toString();
		LocalizationMessageProvider messageProvider = LocalizationProvider.getInstance().getCurrentMessageProvider();
    	String text = messageProvider.getMessage("generated.html.crypto.init.password.response.dsc", "'" + imgsrcok + "'");
	    
    	editorPane.setText(text);
    	explanatoryPanel.add(editorPane);
    	
        final int separatorXSize = 10;

        JPanel buttonPanel = new JPanel();
        //buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(Utils.getXSeparator(separatorXSize));
        buttonPanel.add(Box.createHorizontalGlue());

        LocalizedJButton closeButton = new LocalizedJButton("common.close");
        closeButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				CryptoOfflineButtonsBar.setButtonStatus(CryptoOfflineStatus.PASSWORD_HASHED);
				instance.dispose();
				instance = null;
				
			}
		});
        buttonPanel.add(closeButton);

        explanatoryPanel.add(buttonPanel);

        //set relative to frame parent
        this.setLocationRelativeTo(parent);

    	return explanatoryPanel;
    }
	

	@Override
	protected ActionListener attachCustomActionHandler() {
		return new HashPasswordActionHandler(this);
	}
}
