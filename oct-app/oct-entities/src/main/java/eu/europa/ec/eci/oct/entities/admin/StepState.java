package eu.europa.ec.eci.oct.entities.admin;


import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "OCT_STEP_STATE")
@Cacheable(false)
public class StepState implements Serializable{
	
	private static final long serialVersionUID = 7106754249235850503L;
	
	@Id
	private Long id;

	@Column
	private byte structure;

	@Column
	private byte personalise;
	
	@Column
	private byte certificate;

	@Column
	private byte social;
	
	@Column
	private byte live;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean getStructure() {
		return structure != 0;
	}

	public void setStructure(boolean structure) {
		this.structure = structure ? (byte) 1 : (byte) 0;
	}

	public boolean getPersonalise() {
		return personalise != 0;
	}

	public void setPersonalise(boolean personalise) {
		this.personalise = personalise ? (byte) 1 : (byte) 0;
	}

	public boolean getSocial() {
		return social != 0;
	}

	public void setSocial(boolean social) {
		this.social = social ? (byte) 1 : (byte) 0;
	}

	public boolean getLive() {
		return live != 0;
	}

	public void setLive(boolean live) {
		this.live = live ? (byte) 1 : (byte) 0;
	}

	public boolean getCertificate() {
		return certificate != 0;
	}

	public void setCertificate(boolean certificate) {
		this.certificate = certificate ? (byte) 1 : (byte) 0;
	}
	
}
