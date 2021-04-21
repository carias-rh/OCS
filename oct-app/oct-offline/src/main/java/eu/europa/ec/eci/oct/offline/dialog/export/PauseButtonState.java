package eu.europa.ec.eci.oct.offline.dialog.export;

/**
 * @author: micleva
 * @created: 11/17/11
 * @project OCT
 */
public enum PauseButtonState {

    PAUSE("decrypt.export.dialog.progress.button.pause"),

    RESUME("decrypt.export.dialog.progress.button.resume");

    private String messageKey;

    PauseButtonState(String msgKey) {
        this.messageKey = msgKey;
    }

    public String getMessageKey() {
        return messageKey;
    }
}
