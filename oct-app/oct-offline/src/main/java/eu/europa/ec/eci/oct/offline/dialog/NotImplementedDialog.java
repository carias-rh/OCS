package eu.europa.ec.eci.oct.offline.dialog;

import eu.europa.ec.eci.oct.offline.actions.OfflineToolCommandAction;
import eu.europa.ec.eci.oct.offline.startup.CryptoOfflineTool;
import eu.europa.ec.eci.oct.offline.support.localization.LocalizationMessageProvider;
import eu.europa.ec.eci.oct.offline.support.localization.LocalizationProvider;
import eu.europa.ec.eci.oct.offline.support.localization.LocalizedItem;

import javax.swing.*;
import java.text.MessageFormat;

/**
 * @author: micleva
 * @created: 11/9/11
 * @project OCT
 */
public class NotImplementedDialog implements LocalizedItem {

    private String windowTitle = "Not implemented";
    private MessageFormat messageFormat = null;
    private OfflineToolCommandAction offlineToolAction;

    public NotImplementedDialog(OfflineToolCommandAction offlineToolAction) {

        this.offlineToolAction = offlineToolAction;

        LocalizationProvider localizationProvider = LocalizationProvider.getInstance();
        localizationProvider.registerLocalizedItem(this);
    }

    public void show() {
        String dialogMessage = messageFormat.format(new Object[]{offlineToolAction.name()});

        JOptionPane.showMessageDialog(CryptoOfflineTool.getInstance().getFrame(),
                dialogMessage,
                windowTitle,
                JOptionPane.WARNING_MESSAGE);
    }

    @Override
    public void updateMessages(LocalizationMessageProvider messageProvider) {
        windowTitle = messageProvider.getMessage("action.notImplemented.windowTitle");
        String displayedMessage = messageProvider.getMessage("action.notImplemented.displayedMessage");
        messageFormat = new MessageFormat(displayedMessage);
    }
}
