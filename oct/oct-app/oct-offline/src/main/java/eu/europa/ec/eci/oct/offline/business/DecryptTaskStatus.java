package eu.europa.ec.eci.oct.offline.business;

/**
 * @author: micleva
 * @created: 11/29/11
 * @project OCT
 */
public final class DecryptTaskStatus {

    private final String fileName;
    private final long execTimeInMillis;
    private final boolean successfullyProcessed;
    private final String additionalMessage;

    public DecryptTaskStatus(String fileName, long execTimeInMillis, boolean successfullyProcessed, String additionalMessage) {
        this.fileName = fileName;
        this.execTimeInMillis = execTimeInMillis;
        this.successfullyProcessed = successfullyProcessed;
        this.additionalMessage = additionalMessage;
    }

    public String getFileName() {

        return fileName;
    }

    public long getExecTimeInMillis() {
        return execTimeInMillis;
    }

    public boolean isSuccessfullyProcessed() {
        return successfullyProcessed;
    }

    public String getAdditionalMessage() {
        return additionalMessage;
    }
}
