package eu.europa.ec.eci.oct.validation;

public class ValidationError {

    private String key;

    private String errorKey;

    private boolean isSkippable = false;

    public ValidationError() {
    }


    public ValidationError(String key, String errorKey) {
        this.key = key;
        this.errorKey = errorKey;
        this.isSkippable = false;
    }

    public ValidationError(String key, String errorKey, boolean isSkippable) {
        this.key = key;
        this.errorKey = errorKey;
        this.isSkippable = isSkippable;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getErrorKey() {
        return errorKey;
    }

    public void setErrorKey(String errorKey) {
        this.errorKey = errorKey;
    }

    public boolean isSkippable() {
        return isSkippable;
    }

    public void setSkippable(boolean skippable) {
        isSkippable = skippable;
    }


    @Override
    public String toString() {
        return "ValidationError{" +
                "key='" + key + '\'' +
                ", errorKey='" + errorKey + '\'' +
                ", isSkippable=" + isSkippable +
                '}';
    }
}
