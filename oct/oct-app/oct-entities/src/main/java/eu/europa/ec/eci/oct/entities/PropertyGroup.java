package eu.europa.ec.eci.oct.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "OCT_PROPERTY_GROUP")
public class PropertyGroup {

	@Id
	private Long id;

	@Column(unique = true, nullable = false, insertable = false, length = 64)
	private String name;

	@Column(nullable = false)
	private int priority;

	@Column
	private byte multichoice = 0;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return new StringBuffer().append(getName()).append(" [").append("]").toString();
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public void setMultichoice(boolean multichoice) {
		this.multichoice = multichoice ? (byte) 1 : (byte) 0;
	}

	public boolean isMultichoice() {
		return multichoice != 0;
	}
}
