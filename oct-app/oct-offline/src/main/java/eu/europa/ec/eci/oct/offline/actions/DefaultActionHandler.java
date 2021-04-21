package eu.europa.ec.eci.oct.offline.actions;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.jfree.io.IOUtils;

import eu.europa.ec.eci.oct.offline.dialog.NotImplementedDialog;
import eu.europa.ec.eci.oct.offline.dialog.challenge.ChallengeDecryptionDialog;
import eu.europa.ec.eci.oct.offline.dialog.export.DecryptExportDialog;
import eu.europa.ec.eci.oct.offline.dialog.hashpass.HashPasswordDialog;
import eu.europa.ec.eci.oct.offline.dialog.initialize.CryptoInitializeDialog;
import eu.europa.ec.eci.oct.offline.startup.CryptoOfflineTool;
import eu.europa.ec.eci.oct.offline.support.Utils;
import eu.europa.ec.eci.oct.offline.support.crypto.CryptographyHelper;
import eu.europa.ec.eci.oct.offline.support.crypto.KeyProviderFolderName;
import eu.europa.ec.eci.oct.offline.support.localization.LocalizationMessageProvider;
import eu.europa.ec.eci.oct.offline.support.localization.LocalizationProvider;
import eu.europa.ec.eci.oct.offline.support.log.OfflineCryptoToolLogger;

/**
 * @author: micleva
 * @created: 11/7/11
 * @project OCT
 */
public class DefaultActionHandler implements ActionListener {
    private static OfflineCryptoToolLogger log = OfflineCryptoToolLogger.getLogger(DefaultActionHandler.class.getName());

    private JFrame cryptoToolFrame;
    private CryptoOfflineTool cryptoOfflineTool;

    public DefaultActionHandler(CryptoOfflineTool cryptoOfflineTool) {
        cryptoToolFrame = cryptoOfflineTool.getFrame();
        this.cryptoOfflineTool = cryptoOfflineTool;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        OfflineToolCommandAction offlineToolAction = OfflineToolCommandAction.valueOf(e.getActionCommand());

        switch (offlineToolAction) {
            case INITIALIZE_CRYPTO_OFFLINE_TOOL:
                CryptoInitializeDialog initializeDialog = CryptoInitializeDialog.getInstance(cryptoToolFrame);
                initializeDialog.openDialog(cryptoToolFrame);
                break;
            case OPEN_WEB_SECURITY_FOLDER:
        		try {
        			String dataFullPath = Utils.getFolderPathInProject(KeyProviderFolderName.CRYPTO_DATA_FOLDER_NAME);
        			File file = new File(dataFullPath);
        			Desktop desktop = Desktop.getDesktop();
        			desktop.open(file);
        		} catch (IOException ioe) {
        			//TODO exception
        			log.debug("Unable to locate data folder", ioe);
        		}            	
            	break;
            case DECRYPT_LOGIN_CHALLENGE:
                ChallengeDecryptionDialog decryptionDialog = new ChallengeDecryptionDialog(cryptoToolFrame);
                if (validateCryptoModule()) {
                    decryptionDialog.show();
                }
                
                break;
            case DECRYPT_EXPORTED_DATA:
                DecryptExportDialog exportDialog = new DecryptExportDialog(cryptoToolFrame);
                if (validateCryptoModule()) {
                    exportDialog.show();
                }
                break;
            case SHOW_HELP:
    			if (Desktop.isDesktopSupported()) {
    			    try {
    			    	InputStream resource = Thread.currentThread().getContextClassLoader().getResourceAsStream("help/guide-eci-en.pdf");
    			    	
    			    	File file = File.createTempFile("User_Guide", ".pdf");  
    			        file.deleteOnExit();  
    			        OutputStream out = new FileOutputStream(file);  
    			        try{  
    			        	IOUtils.getInstance().copyStreams(resource,out); 
    			        }finally{  
    			            out.close();  
    			        }  
    			    	Desktop.getDesktop().open(file);
    			    } catch (IOException ex) {
    			    	//TODO implement error box
    			    	log.debug("Unable to open help file", ex);
					}
    			}

                break;
            case HASH_PASSWORD:

//            	boolean openIt = true;
//            	if(KeyProvider.keyWacExists()){
//            		int dialogButton = JOptionPane.showConfirmDialog(cryptoToolFrame, proceedMessage, titleMessage, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
//            		if(dialogButton == JOptionPane.NO_OPTION){
//            			openIt = false;
//            		}
//            	}
//            	if(openIt){
                	HashPasswordDialog hashPasswordDialog = HashPasswordDialog.getInstance(cryptoToolFrame); 
                    hashPasswordDialog.openDialog(cryptoToolFrame);
//            	}
                break;
            case WELCOME_SCREEN_OK:
            	cryptoOfflineTool.swap();
            	break;
            default:
                NotImplementedDialog notImplementedDialog = new NotImplementedDialog(offlineToolAction);
                notImplementedDialog.show();
        }
    }

    private boolean validateCryptoModule() {
        LocalizationMessageProvider messageProvider = LocalizationProvider.getInstance().getCurrentMessageProvider();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(messageProvider.getMessage("decrypt.export.validate.toFix"));

        boolean isValid = true;
        if (!CryptographyHelper.isCryptoModuleInitialized()) {
            stringBuilder.append('\n').append(messageProvider.getMessage("decrypt.key.notInitialized"));
            isValid = false;
        }

        if (!isValid) {
            JOptionPane.showMessageDialog(cryptoToolFrame,
                    stringBuilder.toString(),
                    messageProvider.getMessage("common.validation.dialog.title"),
                    JOptionPane.WARNING_MESSAGE);
        }

        return isValid;
    }
}
