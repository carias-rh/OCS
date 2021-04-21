package eu.europa.ec.eci.oct.offline.business;

import eu.europa.ec.eci.oct.offline.startup.ConfigProperty;
import eu.europa.ec.eci.oct.offline.startup.CryptoOfflineConfig;
import eu.europa.ec.eci.oct.offline.support.concurrent.PausableThreadPoolExecutor;
import eu.europa.ec.eci.oct.offline.support.log.OfflineCryptoToolLogger;
import eu.europa.ec.eci.oct.offline.support.summary.SummaryEntry;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;

/**
 * @author: micleva
 * @created: 11/16/11
 * @project OCT
 */
public class DecryptProcess {

    private static OfflineCryptoToolLogger log = OfflineCryptoToolLogger.getLogger(DecryptProcess.class.getName());

    private List<DecryptProcessListener> listeners = new ArrayList<DecryptProcessListener>();

    private Map<File, SummaryEntry> inputFilesBySelection;
    private File outputFolder;
    private DecryptionState currentDecryptionState = DecryptionState.INITIAL;
    private long decryptionStartTime;

    private PausableThreadPoolExecutor poolExecutor;
    private CompletionService<DecryptTaskStatus> completionService;
    private int totalNumberOfFiles;
    private String totalNumberOfFilesStr;

    public void initiate(Map<File, SummaryEntry> inputFilesBySelection, File outputFolder, FileType outputFileType, int totalNumberOfFiles) {

        //allow initialization only if the decryption is in the prepared or cancelled state
        assertDecryptionState(DecryptionState.INITIAL);

        this.inputFilesBySelection = inputFilesBySelection;
        this.outputFolder = outputFolder;
        this.totalNumberOfFiles = totalNumberOfFiles;
        this.totalNumberOfFilesStr = String.valueOf(totalNumberOfFiles);

        updateCurrentState(DecryptionState.PREPARED, totalNumberOfFilesStr);

        // and start the decryption process
        startDecryption(outputFileType);
    }

    private void startDecryption(final FileType outputFileType) {
        //allow starting the decryption only if the decryption is in the prepared
        assertDecryptionState(DecryptionState.PREPARED);

        try {

            SwingWorker<Object, Object> taskCreatorWorker = new SwingWorker<Object, Object>() {
                @Override
                protected Object doInBackground() throws Exception {

                    //create new decryption tasks as long the process is not cancelled
                    for (SummaryEntry summaryEntry : inputFilesBySelection.values()) {
                        if (!isSubmissionOfNewTaskAllowed()) {
                            break;
                        }
                        for (File file : summaryEntry.getFilesInSelection()) {
                            if (!isSubmissionOfNewTaskAllowed()) {
                                break;
                            }
                            DecryptionTask decryptionTask = new DecryptionTask(file, summaryEntry.getRootFile(),
                                    outputFolder, outputFileType);
                            completionService.submit(decryptionTask);
                        }
                    }
                    return null;
                }
            };

            taskCreatorWorker.execute();
            decryptionStartTime = System.currentTimeMillis();
            updateCurrentState(DecryptionState.RUNNING, totalNumberOfFilesStr);

            SwingWorker<Object, Object> taskConsumerWorker = new SwingWorker<Object, Object>() {
                @Override
                protected Object doInBackground() throws Exception {

                    int completedTasks = 0;
                    int successfulTasks = 0;
                    int failedTasks = 0;
                    int tasksCreated = totalNumberOfFiles;
                    while (completedTasks < tasksCreated
                            && !currentDecryptionState.equals(DecryptionState.CANCELLED)) {

                        //get the next finished decryption
                        DecryptTaskStatus taskStatus = completionService.take().get();
                        completedTasks++;
                        if (taskStatus.isSuccessfullyProcessed()) {
                            successfulTasks++;
                        } else {
                            failedTasks++;
                        }
                        notifyTaskCompleted(completedTasks, successfulTasks, failedTasks, taskStatus);
                    }
                    //once completed, update the status
                    if (!currentDecryptionState.equals(DecryptionState.CANCELLED)) {
                        updateCurrentState(DecryptionState.COMPLETED, totalNumberOfFilesStr);
                    }
                    poolExecutor.shutdown();

                    return null;
                }
            };

            taskConsumerWorker.execute();
        } catch (Throwable e) {
            log.debug("Unable to start the decryption process!", e);
            updateCurrentState(DecryptionState.FAILED, e.getMessage());
        }
    }

    private boolean isSubmissionOfNewTaskAllowed() {
        return !currentDecryptionState.equals(DecryptionState.CANCELLED);
    }

    public void cancel() {

        poolExecutor.shutdownNow();
        updateCurrentState(DecryptionState.CANCELLED, null);
    }

    public void pause() {
        assertDecryptionState(DecryptionState.RUNNING);

        //pause the process
        poolExecutor.pause();
        updateCurrentState(DecryptionState.PAUSED, null);
    }

    public void resume() {
        assertDecryptionState(DecryptionState.PAUSED);
        //resume the process
        poolExecutor.resume();

        updateCurrentState(DecryptionState.RUNNING, totalNumberOfFilesStr);
    }

    public void addDecryptProcessListener(DecryptProcessListener decryptProcessListener) {
        listeners.add(decryptProcessListener);
    }

    public int getTotalFilesToProcess() {
        return totalNumberOfFiles;
    }

    private synchronized void updateCurrentState(DecryptionState decryptionState, String statusPayload) {
        currentDecryptionState = decryptionState;
        notifyStatusChanged(statusPayload);
    }

    private void notifyStatusChanged(String statusPayload) {
        for (DecryptProcessListener listener : listeners) {
            listener.onDecryptionStateChanged(currentDecryptionState, statusPayload);
        }
    }

    private void notifyTaskCompleted(int completedTasks, int successfulTask, int failedTasks, DecryptTaskStatus taskStatus) {
        for (DecryptProcessListener listener : listeners) {
            listener.onDecryptionTaskCompleted(completedTasks, successfulTask, failedTasks, taskStatus);
        }
    }

    private void assertDecryptionState(DecryptionState... states) {
        boolean decryptionInState = false;
        for (DecryptionState decryptionState : states) {
            decryptionInState |= decryptionState.equals(currentDecryptionState);
        }
        if (!decryptionInState) {
            throw new IllegalStateException("Expected state(s): " + Arrays.toString(states) + "; got: " + currentDecryptionState);
        }
    }

    public DecryptionState getCurrentDecryptionState() {
        return currentDecryptionState;
    }

    public void reset() {
        CryptoOfflineConfig config = CryptoOfflineConfig.getInstance();
        int corePoolSize = Math.max(2, Runtime.getRuntime().availableProcessors());
        int threadPriority = config.getIntegerConfigValue(ConfigProperty.DECRYPT_THREAD_POOL_PRIORITY, 3);

        poolExecutor = new PausableThreadPoolExecutor(corePoolSize, threadPriority);
        completionService = new ExecutorCompletionService<DecryptTaskStatus>(poolExecutor);

        updateCurrentState(DecryptionState.INITIAL, null);
    }

    public long getDecryptionStartTime() {
        return decryptionStartTime;
    }
}
