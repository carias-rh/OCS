package eu.europa.ec.eci.oct.offline.support.summary;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: micleva
 * @created: 11/14/11
 * @project OCT
 */
public class SelectionSummaryCalculator {

    private List<SummaryCalculatorListener> summaryCalculatorListenerList = new ArrayList<SummaryCalculatorListener>();

    private SelectionSummary sharedSelectionSummary;
    private FileFilter fileFilter;
    Map<File, SummaryEntry> computedFiles = new HashMap<File, SummaryEntry>();

    //the following attributes are used to control the running instances
    private AtomicInteger calculationsRunning = new AtomicInteger(0);
    private static final ReentrantLock computedFilesLock = new ReentrantLock();

    public SelectionSummaryCalculator(FileFilter fileFilter) {
        sharedSelectionSummary = new SelectionSummary();
        this.fileFilter = fileFilter;
    }

    private void synchronousCalculateSize(File[] files, SummaryEntry computationSummary) {
        calculationsRunning.incrementAndGet();

        try {
            long sizeOfFiles = 0;
            int filesNumber = 0;
            List<File> currentFiles = new ArrayList<File>();
            for (File file : files) {
                if (!mapContainsKey(computationSummary.rootFile)) {
                    //if the root file has been removed from the list of files, simply stop this computation
                    break;
                }
                if (file.exists()) {
                    if (file.isFile()) {
                        //add the size to the global shared selection summary
                        sizeOfFiles += file.length();
                        filesNumber++;

                        //add the sub-file and the size to this specific
                        currentFiles.add(file);

                    } else if (file.isDirectory()) {

                        File[] filesInFolder = file.listFiles(fileFilter);
                        synchronousCalculateSize(filesInFolder, computationSummary);
                    }
                }
            }
            if (sizeOfFiles > 0 || filesNumber > 0) {
                //if there were files, it means that the size might have changed. As such, notify all the listeners
                updateSharedSummary(sizeOfFiles, filesNumber, currentFiles, computationSummary);

                notifySummaryListeners(sharedSelectionSummary);
            }
        } finally {
            calculationsRunning.decrementAndGet();
        }
    }

    private void updateSharedSummary(long sizeOfFiles, int filesNumber, List<File> currentFiles, SummaryEntry computationSummary) {
        computedFilesLock.lock();
        try {
            if (computedFiles.containsKey(computationSummary.rootFile)) {
                //of the root folder is still here, update all the values

                sharedSelectionSummary.sizeInBytes += sizeOfFiles;
                sharedSelectionSummary.fileInTotal += filesNumber;

                computationSummary.filesInSelection.addAll(currentFiles);
                computationSummary.sizeInBytes += sizeOfFiles;
            }
        } finally {
            computedFilesLock.unlock();
        }
    }

    public void appendFileToSummary(File file) {
        SummaryEntry computationSummary = addEntryToMap(file);
        //keep a track of the running instances
        synchronousCalculateSize(new File[]{file}, computationSummary);
    }

    public void addSummaryCalculatorListener(SummaryCalculatorListener summaryCalculatorListener) {
        summaryCalculatorListenerList.add(summaryCalculatorListener);
    }

    private void notifySummaryListeners(SelectionSummary selectionSummary) {
        for (SummaryCalculatorListener summaryCalculatorListener : summaryCalculatorListenerList) {
            summaryCalculatorListener.afterSelectionSummaryChanged(selectionSummary);
        }
    }

    public boolean isCalculatorRunning() {
        return calculationsRunning.get() > 0;
    }

    public SelectionSummary getSelectionSummary() {
        return sharedSelectionSummary.copy();
    }

    public Map<File, SummaryEntry> getComputedFiles() {
        return computedFiles;
    }

    public int getTotalNumberOfFiles() {
        return sharedSelectionSummary.fileInTotal;
    }

    public void removeFilesFromSummary(List<File> remainingFiles) {
        removeEntriesFromMapAndUpdateSharedData(remainingFiles);

        notifySummaryListeners(sharedSelectionSummary);
    }

    private SummaryEntry addEntryToMap(File rootFile) {
        computedFilesLock.lock();

        try {
            SummaryEntry computationSummary = new SummaryEntry(rootFile);
            computedFiles.put(rootFile, computationSummary);

            return computationSummary;
        } finally {
            computedFilesLock.unlock();
        }
    }

    private void removeEntriesFromMapAndUpdateSharedData(List<File> remainingFiles) {
        computedFilesLock.lock();

        try {
            List<File> filesToRemove = new ArrayList<File>();
            for (File file : computedFiles.keySet()) {
                if(!remainingFiles.contains(file)) {
                    filesToRemove.add(file);
                }
            }

            for (File rootFile : filesToRemove) {
                SummaryEntry computationSummary = computedFiles.remove(rootFile);

                sharedSelectionSummary.sizeInBytes -= computationSummary.sizeInBytes;
                sharedSelectionSummary.fileInTotal -= computationSummary.filesInSelection.size();
            }
        } finally {
            computedFilesLock.unlock();
        }
    }

    private boolean mapContainsKey(File rootFile) {
        computedFilesLock.lock();

        try {
            return computedFiles.containsKey(rootFile);
        } finally {
            computedFilesLock.unlock();
        }
    }

    public void resetSummaryCalculator() {
        computedFilesLock.lock();

        try {
            computedFiles.clear();
            sharedSelectionSummary.sizeInBytes = 0;
            sharedSelectionSummary.fileInTotal = 0;
        } finally {
            computedFilesLock.unlock();
        }
    }
}
