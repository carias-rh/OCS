package eu.europa.ec.eci.oct.entities.admin;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "OCT_FEEDBACK_RANGE")
public class FeedbackRange implements Serializable {

	private static final long serialVersionUID = -3073604744230638941L;
	
	public static final long GOOD = 4;
	public static final long FINE = 3;
	public static final long FAIR = 2;
	public static final long BAD = 1;

	@Id
	private Long id;

	@Column(nullable = false)
	private String label;

	@Column(nullable = false)
	private int displayOrder;

	@Column(nullable = false)
	private int enabled;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}

	public int getEnabled() {
		return enabled;
	}

	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}

	@Override
	public String toString() {
		return "FeedbackRange [label=" + label + ", displayOrder=" + displayOrder + ", enabled="
				+ enabled + ", id=" + id + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + displayOrder;
		result = prime * result + enabled;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		return result;
	}

}
