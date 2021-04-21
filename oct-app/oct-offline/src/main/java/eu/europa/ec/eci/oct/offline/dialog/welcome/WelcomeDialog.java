package eu.europa.ec.eci.oct.offline.dialog.welcome;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JPanel;

import eu.europa.ec.eci.oct.offline.actions.DefaultActionHandler;
import eu.europa.ec.eci.oct.offline.actions.OfflineToolCommandAction;
import eu.europa.ec.eci.oct.offline.startup.CryptoOfflineMenu;
import eu.europa.ec.eci.oct.offline.startup.CryptoOfflineTool;
import eu.europa.ec.eci.oct.offline.support.localization.LocalizationMessageProvider;
import eu.europa.ec.eci.oct.offline.support.localization.LocalizationProvider;
import eu.europa.ec.eci.oct.offline.support.swing.localization.LocalizedJButton;

public class WelcomeDialog extends JPanel {

	private static final long serialVersionUID = 2251860573057257126L;

    CryptoOfflineTool cryptoOfflineTool;
    JEditorPane editorPane;

	public WelcomeDialog(CryptoOfflineTool cryptoOfflineTool) {

        this.cryptoOfflineTool = cryptoOfflineTool;

        //setup the panel
        setupPanel();

        //add widgets
        addWidgets();
	}
	
	
	   private void addWidgets() {

	        JPanel barPanel = new JPanel(new BorderLayout());
	        JPanel northPanel = new CryptoOfflineMenu(cryptoOfflineTool);
	        barPanel.add(northPanel, BorderLayout.NORTH);

	        this.add(barPanel, BorderLayout.NORTH);

//	        barPanel.add(CryptoOfflineButtonsBar.getInstance(cryptoOfflineTool));
//	        barPanel.add(createDescriptionPanel(), BorderLayout.SOUTH);
	        
	        barPanel.add(createxx(),  BorderLayout.SOUTH);

	        
	        
//	        //status bar
//			JPanel statusPanel = new JPanel();
//			statusPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
//			statusPanel.setPreferredSize(new Dimension(cryptoOfflineTool.getFrame().getWidth(), 10));
//			statusPanel.setMinimumSize(new Dimension(cryptoOfflineTool.getFrame().getWidth(), 10));
//			statusPanel.setSize(new Dimension(cryptoOfflineTool.getFrame().getWidth(), 10));
//			statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
//			JLabel statusLabel = new JLabel("version xx");
//			//statusLabel.setHorizontalAlignment(SwingConstants.RIGHT);
//			//statusPanel.add(Box.createRigidArea(new Dimension(600,0)));
//			statusPanel.add(statusLabel);
//			statusLabel.repaint();
//			
//	        this.add(statusPanel, BorderLayout.CENTER);
	        
	   }

	    public JPanel createxx(){
			LocalizationMessageProvider messageProvider = LocalizationProvider.getInstance().getCurrentMessageProvider();
	    	JPanel panel = new JPanel();
	    	panel.setLayout(new BorderLayout());
	    	JEditorPane editorPane = new JEditorPane();
	    	editorPane.setBorder(BorderFactory.createLoweredBevelBorder());
	    	editorPane.setContentType("text/html");
	    	editorPane.setPreferredSize(new Dimension(150, 300));
	    	editorPane.setEditable(false);
	    	editorPane.setOpaque(false);
	    	editorPane.setText(messageProvider.getMessage("XXXx"));
	    	panel.add(editorPane, BorderLayout.CENTER);
	    	
	    	LocalizedJButton bOkButton = new LocalizedJButton("okok");
	    	panel.add(bOkButton, BorderLayout.SOUTH);
	    	
	    	bOkButton.addActionListener(new DefaultActionHandler(cryptoOfflineTool));
	    	bOkButton.setActionCommand(OfflineToolCommandAction.WELCOME_SCREEN_OK.name());

	    	
	    	return panel;
	    	    	
	    }
	    

	    private void setupPanel() {
	        this.setLayout(new BorderLayout());
	    }
}
