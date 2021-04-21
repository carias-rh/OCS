package eu.europa.ec.eci.oct.webcommons.services.api.domain.signature;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Component;

/**
 * User: franzmh
 * Date: 06/12/16
 */

@Component
public class SignaturesMetadata implements Serializable{
	
	private static final long serialVersionUID = -980189076092201192L;

	public SignaturesMetadata(){}
	
    private int numbers;
    private List<SignatureMetadata> metadatas;

    public int getNumbers() {
        return numbers;
    }

    public void setNumbers(int numbers) {
        this.numbers = numbers;
    }

    public List<SignatureMetadata> getMetadatas() {
        return metadatas;
    }

    public void setMetadatas(List<SignatureMetadata> metadatas) {
        this.metadatas = metadatas;
    }
}
