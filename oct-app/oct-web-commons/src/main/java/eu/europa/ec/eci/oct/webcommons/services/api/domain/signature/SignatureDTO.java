package eu.europa.ec.eci.oct.webcommons.services.api.domain.signature;

import java.io.Serializable;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import eu.europa.ec.eci.oct.webcommons.services.api.domain.supportForm.SupportFormDTO;

@Component
@Scope("prototype")
public class SignatureDTO implements Serializable {

	private static final long serialVersionUID = -5617051513736195619L;

	public SignatureDTO() {
	}

	public static SignatureDTO cloneSignatureDTO(SignatureDTO signatureDTOtoBeCloned) {
		SignatureDTO clonedSignatureDTO = new SignatureDTO();
		clonedSignatureDTO.setCountry(signatureDTOtoBeCloned.getCountry());
		clonedSignatureDTO.setIdentifier(signatureDTOtoBeCloned.getIdentifier());
		clonedSignatureDTO.setProperties(signatureDTOtoBeCloned.getProperties());
		return clonedSignatureDTO;
	}

	List<SupportFormDTO> properties;

	public static final int CURRENT_ANNEX_REVISION_NUMBER = 2;
	private String country;
	private String identifier;
	private String signatoryInfo;

	private boolean optionalValidation;

	public boolean isOptionalValidation() {
		return optionalValidation;
	}

	public void setOptionalValidation(boolean optionalValidation) {
		this.optionalValidation = optionalValidation;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public List<SupportFormDTO> getProperties() {
		return properties;
	}

	public void setProperties(List<SupportFormDTO> properties) {
		this.properties = properties;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getSignatoryInfo() {
		return signatoryInfo;
	}

	public void setSignatoryInfo(String signatoryInfo) {
		this.signatoryInfo = signatoryInfo;
	}

	@Override
	public String toString() {
		return "SignatureDTO [properties=" + properties + ", country=" + country + ", identifier=" + identifier
				+ ", signatoryInfo=" + signatoryInfo + ", optionalValidation=" + optionalValidation + "]";
	}
}
