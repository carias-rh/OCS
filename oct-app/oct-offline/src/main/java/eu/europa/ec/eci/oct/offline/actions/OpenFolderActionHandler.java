package eu.europa.ec.eci.oct.offline.actions;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import eu.europa.ec.eci.oct.offline.support.Utils;
import eu.europa.ec.eci.oct.offline.support.crypto.KeyProviderFolderName;
import eu.europa.ec.eci.oct.offline.support.log.OfflineCryptoToolLogger;

/**
 * @author: falloda
 * @created: 05/02/2013
 * @project OCT
 * 
 */
public class OpenFolderActionHandler implements ActionListener{

	private static OfflineCryptoToolLogger log = OfflineCryptoToolLogger.getLogger(OpenFolderActionHandler.class.getName());
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		try {
			String dataFullPath = Utils.getFolderPathInProject(KeyProviderFolderName.KEYS_TO_SEND_FOLDER_NAME);
			File file = new File(dataFullPath);
			Desktop desktop = Desktop.getDesktop();
			desktop.open(file);
		} catch (IOException e) {
			log.debug("Unable to locate data folder", e);
		}
	}
}
