package eu.europa.ec.eci.oct.webcommons.services.api.domain.signature;

import java.io.Serializable;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * User: franzmh
 * Date: 28/10/16
 */

@Component
@Scope("prototype")
public class SignatureCount implements Serializable{
	
	private static final long serialVersionUID = -6531334672189888798L;

	public SignatureCount(){}
	
    public long signatureCount;

    public long getSignatureCount() {
        return signatureCount;
    }

    public void setSignatureCount(long signatureCount) {
        this.signatureCount = signatureCount;
    }

    @Override
    public String toString() {
        return "{signature_count:" + signatureCount + "}";
    }
}
