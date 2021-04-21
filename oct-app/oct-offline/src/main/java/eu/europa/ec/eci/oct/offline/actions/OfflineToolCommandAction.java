package eu.europa.ec.eci.oct.offline.actions;

/**
 * @author: micleva
 * @created: 11/7/11
 * @project OCT
 */
public enum OfflineToolCommandAction {

    /**
     * Actions used to mark the generation of the Private/Public key
     */
    INITIALIZE_CRYPTO_OFFLINE_TOOL,

    /**
     * Actions used to hash a password
     */
    HASH_PASSWORD,

    /**
     * Action marking the decryption of the login challenge
     */
    DECRYPT_LOGIN_CHALLENGE,

    /**
     * Action marking the decryption of exported data
     */
    DECRYPT_EXPORTED_DATA,

    /**
     * Action to open the folder where are stored the public and web admin key
     */
    OPEN_WEB_SECURITY_FOLDER,
    
    /**
     * Displays the help dialog
     */
    SHOW_HELP,
    
    
    WELCOME_SCREEN_OK
}
