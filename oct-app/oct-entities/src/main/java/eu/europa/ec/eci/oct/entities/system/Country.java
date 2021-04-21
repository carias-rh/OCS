package eu.europa.ec.eci.oct.entities.system;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "OCT_COUNTRY")
public class Country implements Serializable {

	private static final long serialVersionUID = 6013365546128974053L;
	public static final String CATEGORY_A = "A";
	public static final String CATEGORY_B = "B";

	@Id
	private Long id;

	@Column(unique = true, nullable = false, insertable = false)
	private String name;

	@Column(unique = true, nullable = false, insertable = false)
	private String code;

	@Column
	private long threshold;

	@Column
	private String category;

	private transient String label;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public long getThreshold() {
		return threshold;
	}

	public void setThreshold(long threshold) {
		this.threshold = threshold;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Override
	public String toString() {
		return "Country [id=" + id + ", name=" + name + ", code=" + code + ", threshold=" + threshold + ", category="
				+ category + "]";
	}

}
