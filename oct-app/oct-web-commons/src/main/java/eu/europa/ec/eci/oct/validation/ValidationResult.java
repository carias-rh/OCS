package eu.europa.ec.eci.oct.validation;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {

    private String countryCode;

    private List<ValidationError> validationErrors = new ArrayList<ValidationError>();

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public List<ValidationError> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(List<ValidationError> validationErrors) {
        this.validationErrors = validationErrors;
    }

    public void addValidationError(ValidationError validationResultErrors){
        this.validationErrors.add(validationResultErrors);
    }

    public void addValidationError(String key, String errorKey){
        this.validationErrors.add(new ValidationError(key,errorKey));
    }

    public void addValidationError(String key, String errorKey, boolean isSkippable){
        this.validationErrors.add(new ValidationError(key,errorKey, isSkippable));
    }

    public boolean isValidationSkippable(){
        for (ValidationError v: validationErrors) {
            if(!v.isSkippable()){
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "ValidationResult{" +
                "countryCode='" + countryCode + '\'' +
                ", validationErrors=" + validationErrors +
                '}';
    }
}
