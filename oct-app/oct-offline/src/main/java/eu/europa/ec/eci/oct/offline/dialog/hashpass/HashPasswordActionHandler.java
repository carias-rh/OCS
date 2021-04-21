package eu.europa.ec.eci.oct.offline.dialog.hashpass;

import eu.europa.ec.eci.oct.crypto.CryptoException;
import eu.europa.ec.eci.oct.crypto.Cryptography;
import eu.europa.ec.eci.oct.offline.dialog.pwd.PasswordDialog;
import eu.europa.ec.eci.oct.offline.startup.SecurityConstants;
import eu.europa.ec.eci.oct.offline.support.crypto.KeyProvider;
import eu.europa.ec.eci.oct.offline.support.localization.LocalizationMessageProvider;
import eu.europa.ec.eci.oct.offline.support.localization.LocalizationProvider;
import eu.europa.ec.eci.oct.offline.support.log.OfflineCryptoToolLogger;
import org.apache.commons.codec.binary.Hex;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * 
 * @author falloda
 *
 */
public class HashPasswordActionHandler implements ActionListener{

	
	private static OfflineCryptoToolLogger log = OfflineCryptoToolLogger.getLogger(HashPasswordActionHandler.class.getName());

	private PasswordDialog jDialog;

	public HashPasswordActionHandler(PasswordDialog jDialog) {
		this.jDialog = jDialog;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (jDialog.validateUsername() && jDialog.validatePassword(SecurityConstants.MINIMUM_PASSWORD_LENGTH) && jDialog.validateConfPass()) {
			final Component glassPane = jDialog.getGlassPane();
	
        	if(KeyProvider.keyWacExistsbyUsername(jDialog.getUsername())){
                LocalizationMessageProvider messageProvider = LocalizationProvider.getInstance().getCurrentMessageProvider();
            	UIManager.put("OptionPane.yesButtonText", messageProvider.getMessage("OptionPane.yesButtonText"));
            	UIManager.put("OptionPane.noButtonText", messageProvider.getMessage("OptionPane.noButtonText"));
            	UIManager.put("OptionPane.cancelButtonText", messageProvider.getMessage("OptionPane.cancelButtonText"));
            	UIManager.put("OptionPane.titleText", messageProvider.getMessage("OptionPane.decrypt.export.dialog.titleText"));
            	String titleMessage = messageProvider.getMessage("OptionPane.decrypt.export.dialog.titleText");
            	String proceedMessage = messageProvider.getMessage("generated.html.OptionPane.decrypt.export.dialog.proceedMessage", jDialog.getUsername());
	    		int dialogButton = JOptionPane.showConfirmDialog(jDialog.getParentContainer(), proceedMessage, titleMessage, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
	    		if(dialogButton == JOptionPane.NO_OPTION){
	    			return ;
	    		}
        	}

			
			SwingWorker<String, Object> worker = new SwingWorker<String, Object>() {
	
				@Override
				protected String doInBackground() throws Exception {
					try{
			            String pass = new String(jDialog.getPassword());
			            try {
							byte[] bSalt = Cryptography.generateSalt();
							String sSalt = new String(Hex.encodeHex(bSalt));
							String hashPassword = new String(Hex.encodeHex(Cryptography.fingerprintWithSalt(pass, bSalt)));
							KeyProvider.saveCredentials(jDialog.getUsername(), sSalt, hashPassword);
						} catch (CryptoException ex) {
							//TODO implement an error?
							log.debug("Unable to hash the password: " + ex.getMessage(), ex);
			            }
						
					} catch (Exception ex) {
						log.debug("Unable to generate the ash password", ex);
						ex.printStackTrace();
					} finally {
						glassPane.setVisible(false);
					}
					return null;
				}
	
				@Override
				protected void done() {
				}			
			};
	
			
			glassPane.setVisible(true);
			jDialog.getContentPane().removeAll();
			jDialog.getContentPane().add(((HashPasswordDialog)jDialog).buildResponseInitPasswordPanel());
			worker.execute();
		}
	}

}
