package eu.europa.ec.eci.oct.offline.business;

/**
 * @author: micleva
 * @created: 11/17/11
 * @project OCT
 */
public enum DecryptionState {

    /**
     * After all the data were set to the decrypt process
     */
    INITIAL,

    /**
     * After all the data were set to the decrypt process
     */
    PREPARED,

    /**
     * Shows that the decryption is running
     */
    RUNNING,

    /**
     * Marks the decryption process as paused
     */
    PAUSED,

    /**
     * Marks the decryption process as cancelled
     */
    CANCELLED,

    /**
     * Marks the decryption process as completed
     */
    COMPLETED,

    /**
     * The decrypt process failed for a reason
     */
    FAILED
}
