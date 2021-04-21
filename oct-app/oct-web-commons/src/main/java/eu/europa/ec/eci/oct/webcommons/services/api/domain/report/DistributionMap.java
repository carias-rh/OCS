package eu.europa.ec.eci.oct.webcommons.services.api.domain.report;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Component;

import eu.europa.ec.eci.oct.webcommons.services.api.domain.signature.SignatureCountryCount;

@Component
public class DistributionMap implements Serializable{
	
	private static final long serialVersionUID = 1729287608964351480L;

	public DistributionMap(){}

	private List<SignatureCountryCount> signatureCountryCount;

	public List<SignatureCountryCount> getSignatureCountryCount() {
		return signatureCountryCount;
	}

	public void setSignatureCountryCount(List<SignatureCountryCount> signatureCountryCount) {
		this.signatureCountryCount = signatureCountryCount;
	}

	@Override
	public String toString() {
		return "DistributionMap [signatureCountryCount=" + signatureCountryCount + "]";
	}

}
