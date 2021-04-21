package eu.europa.ec.eci.oct.entities.views;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "LASTSIGNATURESVIEW")
public class LastSignatures implements Serializable{

	private static final long serialVersionUID = -7539592974031153824L;
	
	@Id
    @Column
    private Long signatureId;

    public LastSignatures() {
    }

    public LastSignatures(Long signatureId) {
        this.signatureId = signatureId;
    }

    public Long getSignatureId() {
        return signatureId;
    }

    public void setSignatureId(Long signatureId) {
        this.signatureId = signatureId;
    }

}
