package eu.europa.ec.eci.oct.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "HIBERNATE_SEQUENCES")
public class HibernateSequences {

	@Id
	private String sequence_name;

	@Column(name = "sequence_next_hi_value")
	private long sequence_next_hi_value;

	public String getSequence_name() {
		return sequence_name;
	}

	public void setSequence_name(String sequence_name) {
		this.sequence_name = sequence_name;
	}

	public long getSequence_next_hi_value() {
		return sequence_next_hi_value;
	}

	public void setSequence_next_hi_value(long sequence_next_hi_value) {
		this.sequence_next_hi_value = sequence_next_hi_value;
	}

	@Override
	public String toString() {
		return "HibernateSequence [sequence_name=" + sequence_name + ", sequence_next_hi_value=" + sequence_next_hi_value + "]";
	}

}
