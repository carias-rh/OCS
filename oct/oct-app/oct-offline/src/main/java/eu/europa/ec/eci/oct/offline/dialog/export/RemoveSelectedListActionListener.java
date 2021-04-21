package eu.europa.ec.eci.oct.offline.dialog.export;

import eu.europa.ec.eci.oct.offline.support.localization.LocalizationMessageProvider;
import eu.europa.ec.eci.oct.offline.support.localization.LocalizationProvider;
import eu.europa.ec.eci.oct.offline.support.localization.LocalizedItem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

/**
 * @author: micleva
 * @created: 11/11/11
 * @project OCT
 */
public class RemoveSelectedListActionListener implements ActionListener, LocalizedItem {

    private JList jList;
    private boolean displayValidationWhenNoSelection;

    private String validationTitle;
    private String validationMessage;
    private Container parent;

    public RemoveSelectedListActionListener(JList jList, boolean displayValidationWhenNoSelection, Container parent) {
        this.jList = jList;
        this.parent = parent;

        LocalizationProvider localizationProvider = LocalizationProvider.getInstance();
        localizationProvider.registerLocalizedItem(this);

        this.displayValidationWhenNoSelection = displayValidationWhenNoSelection;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        jList.setEnabled(false);
        try {
            int[] selectedIndexes = jList.getSelectedIndices();

            if (selectedIndexes.length > 0) {
                Arrays.sort(selectedIndexes);
                DefaultListModel listModel = (DefaultListModel) jList.getModel();
                for (int i = selectedIndexes.length - 1; i > -1; i--) {
                    listModel.removeElementAt(selectedIndexes[i]);
                }
            } else if (displayValidationWhenNoSelection) {
                JOptionPane.showMessageDialog(parent, validationMessage, validationTitle, JOptionPane.WARNING_MESSAGE);
            }
        } finally {
            jList.setEnabled(true);
        }
    }

    @Override
    public void updateMessages(LocalizationMessageProvider messageProvider) {
        this.validationMessage = messageProvider.getMessage("generated.html.decrypt.export.dialog.files.remove.validation.message");
        this.validationTitle = messageProvider.getMessage("common.validation.dialog.title");
    }
}
