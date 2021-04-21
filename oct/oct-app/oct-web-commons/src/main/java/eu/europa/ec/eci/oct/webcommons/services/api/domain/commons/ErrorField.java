package eu.europa.ec.eci.oct.webcommons.services.api.domain.commons;

import java.io.Serializable;

public class ErrorField implements Serializable {

    private static final long serialVersionUID = 6925165361233078646L;


    private String fieldKey;
    private String errorMessage;
    private boolean skippable = false;

    public ErrorField() {
    }

    public ErrorField(String fieldKey, String errorMessage, boolean isSkippable) {
        this.fieldKey = fieldKey;
        this.errorMessage = errorMessage;
        this.skippable = isSkippable;
    }

    public String getFieldKey() {
        return fieldKey;
    }

    public void setFieldKey(String fieldKey) {
        this.fieldKey = fieldKey;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isSkippable() {
        return skippable;
    }

    public void setSkippable(boolean skippable) {
        this.skippable = skippable;
    }

    @Override
    public String toString() {
        return "ErrorField{" +
                "fieldKey='" + fieldKey + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", skippable=" + skippable +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ErrorField that = (ErrorField) o;

        if (skippable != that.skippable) return false;
        if (fieldKey != null ? !fieldKey.equals(that.fieldKey) : that.fieldKey != null) return false;
        return errorMessage != null ? errorMessage.equals(that.errorMessage) : that.errorMessage == null;

    }

    @Override
    public int hashCode() {
        int result = fieldKey != null ? fieldKey.hashCode() : 0;
        result = 31 * result + (errorMessage != null ? errorMessage.hashCode() : 0);
        result = 31 * result + (skippable ? 1 : 0);
        return result;
    }
}


