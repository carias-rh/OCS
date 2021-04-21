package eu.europa.ec.eci.oct.offline.startup;

import eu.europa.ec.eci.oct.offline.actions.OfflineToolCommandAction;
import eu.europa.ec.eci.oct.offline.support.crypto.KeyProvider;


/**
 * 
 * @author falloda
 *
 */

public class CryptoOfflineButtonBarVisibility {

	private static CryptoOfflineButtonBarVisibility instance; 
	
	public static final CryptoOfflineButtonBarVisibility getInstance() {
		if (instance == null) {
			instance = new CryptoOfflineButtonBarVisibility();
		}
		return instance;
	}

	public boolean isEnabled(OfflineToolCommandAction actionCommand){
		return isEnabled(actionCommand, getStatus());
	}

	
	public boolean isEnabled(OfflineToolCommandAction actionCommand, CryptoOfflineStatus status){
		
		if(status == CryptoOfflineStatus.FIRST_USAGE){
			switch (actionCommand) {
			case INITIALIZE_CRYPTO_OFFLINE_TOOL:
				return true;
			default:
				return false;
			}
		}
		if(status == CryptoOfflineStatus.INITIALIZED){
			switch (actionCommand) {
			case INITIALIZE_CRYPTO_OFFLINE_TOOL:
				return false;
			default:
				return true;
			}
		}
		if(status == CryptoOfflineStatus.PASSWORD_HASHED){
			switch (actionCommand) {
			case INITIALIZE_CRYPTO_OFFLINE_TOOL:
				return false;
			default:
				return true;
			}
		}

		return false;
	}
	
	private CryptoOfflineStatus getStatus(){
		CryptoOfflineStatus status = CryptoOfflineStatus.FIRST_USAGE;
		
		if (KeyProvider.keyFileExists()) {
			status = CryptoOfflineStatus.INITIALIZED;
		}
		return status;
	}
}
