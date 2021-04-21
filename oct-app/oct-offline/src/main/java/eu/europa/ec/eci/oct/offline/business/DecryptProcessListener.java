package eu.europa.ec.eci.oct.offline.business;

/**
 * @author: micleva
 * @created: 11/17/11
 * @project OCT
 */
public interface DecryptProcessListener {

    /**
     * Fired whenever the decryption state changes
     *
     * @param decryptionState the latest state of the decryption process
     * @param payload any extra information that describes the current state
     */
    public void onDecryptionStateChanged(DecryptionState decryptionState, String payload);

    public void onDecryptionTaskCompleted(int completedTasks, int successfulTask, int failedTasks, DecryptTaskStatus taskStatus);
}
