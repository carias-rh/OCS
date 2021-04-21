package eu.europa.ec.eci.oct.offline.support.crypto;

public enum KeyProviderFolderName {
	
	CRYPTO_DATA_FOLDER_NAME ("data"),
	
	KEYS_TO_SEND_FOLDER_NAME ("data/files_to_send"),
	
	KEYS_TO_KEEP_FOLDER_NAME ("data/files_to_keep"),
	
    LOG_FOLDER ("log");

    private String name;

	KeyProviderFolderName(String name){
        this.name = name;
	}
     public String getName() {
        return name;
    }

}
