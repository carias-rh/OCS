package eu.europa.ec.eci.oct.webcommons.services.api.domain.signature;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import eu.europa.ec.eci.oct.webcommons.services.api.domain.commons.ErrorField;

@Component
public class SignatureValidation implements Serializable {

	private static final long serialVersionUID = -2768266441810251228L;

	public SignatureValidation() {
	}

	private List<ErrorField> errorFields = new ArrayList<ErrorField>();
	private String signatureIdentifier;
    private boolean captchaValidation;

	public List<ErrorField> getErrorFields() {
		return errorFields;
	}

	public void setErrorFields(List<ErrorField> errorFields) {
		this.errorFields = errorFields;
	}

	public String getSignatureIdentifier() {
		return signatureIdentifier;
	}

	public void setSignatureIdentifier(String signatureIdentifier) {
		this.signatureIdentifier = signatureIdentifier;
	}

    public boolean isCaptchaValidation() {
        return captchaValidation;
    }

    public void setCaptchaValidation(boolean captchaValidation) {
        this.captchaValidation = captchaValidation;
    }

    public void addErrorField(String fieldKey, String errorMessage, boolean isSkippable){
        errorFields.add(new ErrorField(fieldKey, errorMessage, isSkippable));
    }

    @Override
	public String toString() {
		return "SignatureValidationDTO [errorFields=" + errorFields + ", signatureIdentifier="
				+ signatureIdentifier + "]";
	}

}
