package eu.europa.ec.eci.oct.offline.startup;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import eu.europa.ec.eci.oct.offline.support.crypto.KeyProvider;
import eu.europa.ec.eci.oct.offline.support.localization.LocalizationMessageProvider;
import eu.europa.ec.eci.oct.offline.support.localization.LocalizationProvider;
import eu.europa.ec.eci.oct.offline.support.swing.localization.LocalizedJButton;

/**
 * 
 * @author falloda
 *
 */
public class CryptoOfflineButtonBarMouseListener implements MouseListener{

	String imgsrcwarning = CryptoOfflineTool.class.getClassLoader().getResource("images/16x16/emblem-warning.png").toString();
	
	private CryptoOfflineTool cryptoOfflineTool;

	
	public CryptoOfflineButtonBarMouseListener(CryptoOfflineTool cryptoOfflineTool){
		this.cryptoOfflineTool = cryptoOfflineTool;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
		LocalizedJButton localizedJButton = (LocalizedJButton)e.getSource();
		localizedJButton.setBackground(Color.LIGHT_GRAY);
		LocalizationMessageProvider messageProvider = LocalizationProvider.getInstance().getCurrentMessageProvider();
		

		if(localizedJButton == CryptoOfflineButtonsBar.getbInitializeSecurity()){
			if(KeyProvider.keyFileExists()){
				cryptoOfflineTool.getContentPanel().setDescriptionPanel(messageProvider.getMessage("generated.html.menu.content.initialized.dsc"));
			}else{
				
				cryptoOfflineTool.getContentPanel().setDescriptionPanel(messageProvider.getMessage("generated.html.menu.content.initialize.dsc", "'" + imgsrcwarning + "'"));
			}
			
			
		}else if(localizedJButton == CryptoOfflineButtonsBar.getbGenerateWebAdminCredential()){
			if(KeyProvider.keyWacsExists()){
				cryptoOfflineTool.getContentPanel().setDescriptionPanel(messageProvider.getMessage("generated.html.menu.content.reHashPassword.dsc"));
			}else{
				cryptoOfflineTool.getContentPanel().setDescriptionPanel(messageProvider.getMessage("generated.html.menu.content.hashPassword.dsc"));
			}
			
		}else if(localizedJButton == CryptoOfflineButtonsBar.getbDecryptLoginChallenge()){
			cryptoOfflineTool.getContentPanel().setDescriptionPanel(messageProvider.getMessage("generated.html.menu.content.decryptChallenge.dsc"));
			
		}else if(localizedJButton == CryptoOfflineButtonsBar.getbDecryptData()){
			cryptoOfflineTool.getContentPanel().setDescriptionPanel(messageProvider.getMessage("generated.html.menu.content.decryptData.dsc"));

		}else if(localizedJButton == CryptoOfflineButtonsBar.getbOpenKeysFolder()){
			cryptoOfflineTool.getContentPanel().setDescriptionPanel(messageProvider.getMessage("generated.html.menu.content.openKeysFolder.dsc"));
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
//		cryptoOfflineTool.getContentPanel().setDescriptionPanel(ContentHelper.getHelp(0));
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

}
