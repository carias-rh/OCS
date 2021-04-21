package eu.europa.ec.eci.oct.offline.startup;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JPanel;

import eu.europa.ec.eci.oct.offline.actions.DefaultActionHandler;
import eu.europa.ec.eci.oct.offline.actions.OfflineToolCommandAction;
import eu.europa.ec.eci.oct.offline.support.localization.LocalizationMessageProvider;
import eu.europa.ec.eci.oct.offline.support.localization.LocalizationProvider;
import eu.europa.ec.eci.oct.offline.support.swing.localization.LocalizedJButton;
import eu.europa.ec.eci.oct.offline.support.swing.localization.LocalizedJEditorPane;

/**
 * @author: micleva
 * @created: 11/7/11
 * @project OCT
 */
public class CryptoOfflineContentPanel extends JPanel {

    private static final long serialVersionUID = -3740762807793917860L;

    CryptoOfflineTool cryptoOfflineTool;
    JEditorPane editorPane;
    
    JPanel panelButtonUI;
    JPanel panelDscUI;
    JPanel panelWelcomeUI;
    

    public CryptoOfflineContentPanel(CryptoOfflineTool cryptoOfflineTool) throws IOException {

        this.cryptoOfflineTool = cryptoOfflineTool;
        
	        //setup the panel
	        setupPanel();
	
	        panelButtonUI = CryptoOfflineButtonsBar.getInstance(cryptoOfflineTool);
	        panelDscUI = createDescriptionPanel();
	        panelWelcomeUI = createWelcomePanel();
	        
	        //add widgets
	        defaultPanel();
    }
    
    
    public void swapPanel(){
//    	BorderLayout layout = (BorderLayout)this.getLayout();
//    	this.remove(layout.getLayoutComponent(BorderLayout.CENTER));
    	//this.remove(layout.getLayoutComponent(BorderLayout.SOUTH));
    	this.removeAll();
    	
        JPanel barPanel = new JPanel(new BorderLayout());
        JPanel northPanel = new CryptoOfflineMenu(cryptoOfflineTool);
        barPanel.add(northPanel, BorderLayout.NORTH);
        
        this.setLayout(new BorderLayout());
        this.add(barPanel, BorderLayout.NORTH);
        this.add(panelButtonUI, BorderLayout.CENTER);
        this.add(panelDscUI, BorderLayout.SOUTH);
        this.repaint();
    }
    
    

    private void defaultPanel() {
    	
        JPanel barPanel = new JPanel(new BorderLayout());
        JPanel northPanel = new CryptoOfflineMenu(cryptoOfflineTool);
        barPanel.add(northPanel, BorderLayout.NORTH);

//        this.add(barPanel, BorderLayout.NORTH);

        JPanel pMain = new JPanel(new BorderLayout());
        this.add(barPanel, BorderLayout.NORTH);
        
//        panelButtonUI = CryptoOfflineButtonsBar.getInstance(cryptoOfflineTool);
//        panelDscUI = createDescriptionPanel();
        		
//        this.add(panelButtonUI, BorderLayout.CENTER);
//        this.add(panelDscUI, BorderLayout.SOUTH);
        this.add(panelWelcomeUI, BorderLayout.CENTER);
        
//        this.add(pMain);
        
//        //status bar
//		JPanel statusPanel = new JPanel();
//		statusPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
//		statusPanel.setPreferredSize(new Dimension(cryptoOfflineTool.getFrame().getWidth(), 10));
//		statusPanel.setMinimumSize(new Dimension(cryptoOfflineTool.getFrame().getWidth(), 10));
//		statusPanel.setSize(new Dimension(cryptoOfflineTool.getFrame().getWidth(), 10));
//		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
//		JLabel statusLabel = new JLabel("version xx");
//		//statusLabel.setHorizontalAlignment(SwingConstants.RIGHT);
//		//statusPanel.add(Box.createRigidArea(new Dimension(600,0)));
//		statusPanel.add(statusLabel);
//		statusLabel.repaint();
//		
//        this.add(statusPanel, BorderLayout.CENTER);
        
        
    }
    
    
    public JPanel createDescriptionPanel(){
		LocalizationMessageProvider messageProvider = LocalizationProvider.getInstance().getCurrentMessageProvider();

    	JPanel panel = new JPanel();
    	panel.setLayout(new BorderLayout());
    	editorPane = new JEditorPane();
    	editorPane.setBorder(BorderFactory.createLoweredBevelBorder());
    	editorPane.setContentType("text/html");
    	editorPane.setPreferredSize(new Dimension(200, 460));
    	editorPane.setEditable(false);
    	editorPane.setOpaque(false);
    	editorPane.setText(messageProvider.getMessage("generated.html.menu.content.default.dsc"));
    	panel.add(editorPane, BorderLayout.CENTER);
    	
    	return panel;
    }
    
    public void setDescriptionPanel(String text){
    	editorPane.setText(text);
    }
    

    public JPanel createWelcomePanel() throws IOException{
    	
    
    	
		LocalizationMessageProvider messageProvider = LocalizationProvider.getInstance().getCurrentMessageProvider();
    	JPanel panel = new JPanel();
    	panel.setLayout(new BorderLayout());
    	LocalizedJEditorPane editorPane = null ;
		editorPane = new LocalizedJEditorPane("generated.html.crypto.welcome.screen");
    	editorPane.setBorder(BorderFactory.createLoweredBevelBorder());
    	editorPane.setContentType("text/html");
    	editorPane.setPreferredSize(new Dimension(150, 560));
    	editorPane.setEditable(false);
    	editorPane.setOpaque(false);
    	editorPane.setText(messageProvider.getMessage("generated.html.crypto.welcome.screen"));
    	panel.add(editorPane, BorderLayout.CENTER);
    	
    	LocalizedJButton bOkButton = new LocalizedJButton("crypto.welcome.screen.continue");
    	panel.add(bOkButton, BorderLayout.SOUTH);
    	
    	bOkButton.addActionListener(new DefaultActionHandler(cryptoOfflineTool));
    	bOkButton.setActionCommand(OfflineToolCommandAction.WELCOME_SCREEN_OK.name());

    	
    	return panel;
    	    	
    }
    

    private void setupPanel() {
        this.setLayout(new BorderLayout());
    }

}
