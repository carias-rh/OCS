package eu.europa.ec.eci.oct.offline.dialog.export;

import eu.europa.ec.eci.oct.offline.business.*;
import eu.europa.ec.eci.oct.offline.dialog.export.details.DecryptDetailsDialog;
import eu.europa.ec.eci.oct.offline.support.Utils;
import eu.europa.ec.eci.oct.offline.support.localization.LocalizationMessageProvider;
import eu.europa.ec.eci.oct.offline.support.localization.LocalizationProvider;
import eu.europa.ec.eci.oct.offline.support.log.OfflineCryptoToolLogger;
import eu.europa.ec.eci.oct.offline.support.summary.SelectionSummaryCalculator;
import eu.europa.ec.eci.oct.offline.support.summary.SummaryEntry;
import eu.europa.ec.eci.oct.offline.support.swing.localization.LocalizedJButton;
import eu.europa.ec.eci.oct.offline.support.swing.localization.LocalizedJLabel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: micleva
 * @created: 11/17/11
 * @project OCT
 */
public class DecryptWorkflowHelper {
    private static OfflineCryptoToolLogger log = OfflineCryptoToolLogger.getLogger(DecryptWorkflowHelper.class.getName());

    private final JDialog mainDialog;
    private final DecryptProgressDetailsPanel decryptProgressDetailsPanel;

    //fields used by the decryption process initiator
    final private DecryptProcess decryptProcess;

    public DecryptWorkflowHelper(JDialog mainDialog, DecryptProgressDetailsPanel decryptProgressDetailsPanel) {
        this.mainDialog = mainDialog;
        this.decryptProgressDetailsPanel = decryptProgressDetailsPanel;

        this.decryptProcess = new DecryptProcess();
        addDecryptionStateListener();
    }

    public void createDecryptionWorkflow(final RightSidePanel rightSidePanel, final SelectionSummaryCalculator selectionSummaryCalculator) {

        //add the start button action
        final JButton startDecryptionButton = rightSidePanel.getStartDecryptionButton();
        startDecryptionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                boolean isValid = true;
                JProgressBar progressBar = decryptProgressDetailsPanel.getProgressBar();
                progressBar.setValue(progressBar.getMinimum());

                LocalizationMessageProvider messageProvider = LocalizationProvider.getInstance().getCurrentMessageProvider();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(messageProvider.getMessage("decrypt.export.validate.toFix"));

                File outputFolder = rightSidePanel.getOutputFolder();

                if (outputFolder == null || !outputFolder.exists()) {
                    stringBuilder.append('\n').append(messageProvider.getMessage("decrypt.export.validate.noOutputFolder"));
                    isValid = false;
                }
                if (selectionSummaryCalculator.getSelectionSummary().getFileInTotal() <= 0) {
                    stringBuilder.append('\n').append(messageProvider.getMessage("decrypt.export.validate.noFilesSelected"));
                    isValid = false;
                }

                if (isValid) {

                    //disable the access to all the elements of the panel and enable the progress details panel
                    switchAccessibleComponents(true);

                    initiateDecryptionProcess(selectionSummaryCalculator, outputFolder,
                            rightSidePanel.getSelectedOutputFileType());
                } else {
                    JOptionPane.showMessageDialog(mainDialog,
                            stringBuilder.toString(),
                            messageProvider.getMessage("common.validation.dialog.title"),
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        //add the cancel button action
        final JButton cancelButton = decryptProgressDetailsPanel.getCancelButton();
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //remove the focus from the cancel button
                decryptProgressDetailsPanel.requestFocus();

                LocalizedJLabel statusLabel = (LocalizedJLabel) decryptProgressDetailsPanel.getCurrentStatusLabel();
                statusLabel.setLocalizedText("decrypt.export.dialog.progress.status.cancelling");

                //disable the cancel and pause buttons
                cancelButton.setEnabled(false);
                decryptProgressDetailsPanel.getPauseResumeButton().setEnabled(false);

                decryptProcess.cancel();
            }
        });

        //add the pause  button action
        final LocalizedJButton pauseResumeButton = (LocalizedJButton) decryptProgressDetailsPanel.getPauseResumeButton();
        pauseResumeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PauseButtonState buttonState = PauseButtonState.valueOf(e.getActionCommand());
                if (buttonState.equals(PauseButtonState.PAUSE)) {
                    decryptProcess.pause();

                    //set the button value to resume
                    setPauseResumeButtonToState(pauseResumeButton, PauseButtonState.RESUME);
                } else {
                    decryptProcess.resume();

                    //set the button value to resume
                    setPauseResumeButtonToState(pauseResumeButton, PauseButtonState.PAUSE);
                }
            }
        });
    }

    private void setPauseResumeButtonToState(LocalizedJButton pauseResumeButton, PauseButtonState pauseButtonState) {
        pauseResumeButton.setLocalizedText(pauseButtonState.getMessageKey());
        pauseResumeButton.setActionCommand(pauseButtonState.name());
    }

    private void initiateDecryptionProcess(final SelectionSummaryCalculator selectionSummaryCalculator,
                                           final File outputFolder, final FileType outputFileType) {

        //set the elapsed time to 0
        decryptProgressDetailsPanel.getElapsedTimeValue().setText(Utils.getFormattedElapsedTimeSince(System.currentTimeMillis()));

        LocalizedJLabel statusLabel = (LocalizedJLabel) decryptProgressDetailsPanel.getCurrentStatusLabel();
        statusLabel.setLocalizedText("decrypt.export.dialog.progress.status.computingFilesToProcess");

        decryptProcess.reset();
        decryptProgressDetailsPanel.getDetailsDialog().resetTableData();
        SwingWorker<Object, Object> filesRetrieval = new SwingWorker<Object, Object>() {
            private final ReentrantLock lock = new ReentrantLock();
            private final Condition summaryCalculatorRunning = lock.newCondition();

            @Override
            protected Object doInBackground() throws Exception {
                try {
                    lock.lock();
                    while (selectionSummaryCalculator.isCalculatorRunning()
                            && decryptProcess.getCurrentDecryptionState().equals(DecryptionState.INITIAL)) {
                        //wait for the calculator to finish
                        summaryCalculatorRunning.await(300, TimeUnit.MICROSECONDS);
                    }

                } catch (InterruptedException e) {
                    //do nothing
                    log.debug("Could not wait for the Summary Calculator to finish the execution", e);
                } finally {
                    lock.unlock();
                }
                if (decryptProcess.getCurrentDecryptionState().equals(DecryptionState.INITIAL)) {
                    Map<File, SummaryEntry> files = selectionSummaryCalculator.getComputedFiles();
                    decryptProcess.initiate(files, outputFolder, outputFileType, selectionSummaryCalculator.getTotalNumberOfFiles());
                }

                return null;
            }
        };

        filesRetrieval.execute();
    }

    private void switchAccessibleComponents(boolean decryptProgressComponentsActive) {
        if (decryptProgressComponentsActive) {
            mainDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            Utils.switchComponentsIncludingChildrenState(mainDialog.getComponents(), false);
            Utils.switchComponentsIncludingChildrenState(decryptProgressDetailsPanel.getComponents(), true);

            //pause/resume disabled
            decryptProgressDetailsPanel.getPauseResumeButton().setEnabled(false);

            decryptProgressDetailsPanel.focusMinimizeButton();
        } else {
            mainDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            Utils.switchComponentsIncludingChildrenState(mainDialog.getComponents(), true);

            //disable the cancel and pause buttons
            decryptProgressDetailsPanel.getCancelButton().setEnabled(false);
            decryptProgressDetailsPanel.getPauseResumeButton().setEnabled(false);

            mainDialog.requestFocus();
        }
    }

    private void addDecryptionStateListener() {

        final JProgressBar progressBar = decryptProgressDetailsPanel.getProgressBar();

        final Timer elapsedTimeUpdater = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JLabel elapsedTime = decryptProgressDetailsPanel.getElapsedTimeValue();
                elapsedTime.setText(Utils.getFormattedElapsedTimeSince(decryptProcess.getDecryptionStartTime()));
            }
        });

        decryptProcess.addDecryptProcessListener(new DecryptProcessListener() {

            private LocalizedJLabel statusLabel = (LocalizedJLabel) decryptProgressDetailsPanel.getCurrentStatusLabel();
            private LocalizedJButton pauseResumeButton = (LocalizedJButton) decryptProgressDetailsPanel.getPauseResumeButton();
            private DecryptDetailsDialog detailsDialog = decryptProgressDetailsPanel.getDetailsDialog();

            @Override
            public void onDecryptionTaskCompleted(int completedTasks, int successfulTask, int failedTasks, DecryptTaskStatus taskStatus) {
                progressBar.setValue(completedTasks);
                detailsDialog.updateProcessedFiles(completedTasks);
                detailsDialog.updateSuccessfulFiles(successfulTask);
                detailsDialog.updateFailedFiles(failedTasks);
                detailsDialog.logDecryptTaskStatus(taskStatus);
            }

            @Override
            public void onDecryptionStateChanged(DecryptionState decryptionState, String statusPayload) {
                switch (decryptionState) {
                    case PREPARED:
                        statusLabel.setLocalizedText("decrypt.export.dialog.progress.status.initializingDecryption", statusPayload);

                        int filesToProcess = decryptProcess.getTotalFilesToProcess();
                        if (filesToProcess > 0) {
                            progressBar.setMaximum(filesToProcess);
                        }
                        break;
                    case CANCELLED:
                        statusLabel.setLocalizedText("decrypt.export.dialog.progress.status.cancelled");
                        //disable the decryption summary and enable back all the other panels
                        switchAccessibleComponents(false);
                        //set the pause resume button to pause all the time after a cancel
                        setPauseResumeButtonToState(pauseResumeButton, PauseButtonState.PAUSE);
                        elapsedTimeUpdater.stop();
                        break;
                    case PAUSED:
                        statusLabel.setLocalizedText("decrypt.export.dialog.progress.status.paused");
                        elapsedTimeUpdater.stop();
                        break;
                    case RUNNING:
                        statusLabel.setLocalizedText("decrypt.export.dialog.progress.status.decrypting",
                                statusPayload);
                        pauseResumeButton.setEnabled(true);
                        elapsedTimeUpdater.start();
                        break;
                    case COMPLETED:
                        statusLabel.setLocalizedText("decrypt.export.dialog.progress.status.completed",
                                statusPayload);
                        switchAccessibleComponents(false);
                        elapsedTimeUpdater.stop();

                        break;
                    case FAILED:
                        statusLabel.setLocalizedText("decrypt.export.dialog.progress.status.failed",
                                statusPayload);
                        switchAccessibleComponents(false);
                        elapsedTimeUpdater.stop();

                        break;
                }
            }
        });
    }
}
