package eu.europa.ec.eci.oct.offline.dialog.export;

import eu.europa.ec.eci.oct.offline.support.summary.SelectionSummaryCalculator;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: micleva
 * @created: 11/14/11
 * @project OCT
 */
public class FileSummaryDataListener implements ListDataListener {

    private SelectionSummaryCalculator summaryCalculator;

    public FileSummaryDataListener(SelectionSummaryCalculator summaryBuilder) {
        this.summaryCalculator = summaryBuilder;
    }

    @Override
    public void intervalAdded(ListDataEvent event) {

        DefaultListModel listModel = (DefaultListModel) event.getSource();
        final File file = (File) listModel.getElementAt(event.getIndex0());

        SwingWorker<String, Object> worker = new SwingWorker<String, Object>() {

            @Override
            protected String doInBackground() throws Exception {

                summaryCalculator.appendFileToSummary(file);
                return null;
            }
        };

        worker.execute();
    }

    @Override
    public void intervalRemoved(ListDataEvent event) {
        DefaultListModel listModel = (DefaultListModel) event.getSource();
        Object[] objects = listModel.toArray();
        final List<File> files = new ArrayList<File>(objects.length);
        for (int i = 0, objectsLength = objects.length; i < objectsLength; i++) {
            Object obj = objects[i];
            files.add((File) obj);
        }

        SwingWorker<String, Object> worker = new SwingWorker<String, Object>() {
            @Override
            protected String doInBackground() throws Exception {
                summaryCalculator.removeFilesFromSummary(files);
                return null;
            }
        };
        worker.execute();
    }

    @Override
    public void contentsChanged(ListDataEvent event) {
        //this scenario is not expected. Editing the list manually is disabled.
        // Just simple add and remove operations exist
    }
}
