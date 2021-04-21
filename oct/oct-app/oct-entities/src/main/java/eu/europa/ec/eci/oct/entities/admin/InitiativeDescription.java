package eu.europa.ec.eci.oct.entities.admin;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import eu.europa.ec.eci.oct.entities.system.Language;

@Entity
@Table(name = "OCT_INITIATIVE_DESC")
@Cacheable(false)
public class InitiativeDescription implements Serializable {

	private static final long serialVersionUID = 2462518290220931962L;
	public static final int IS_DEFAULT = 1;

	@GeneratedValue(generator = "IdGenerator", strategy = GenerationType.TABLE)
	@Id
	@TableGenerator(name = "IdGenerator", pkColumnValue = "initiative_description_id", table = "HIBERNATE_SEQUENCES", allocationSize = 1, pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
	@Column(name = "id")
	private Long id;

	@Column
	private String title;

	@Column
	private String objectives;

	@Column
	private String url;

	@Column
	private int isDefault;

	@Column
	private String partialRegistration;

	@ManyToOne
	@JoinColumn(name = "language_id")
	private Language language;

	public int getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(int isDefault) {
		this.isDefault = isDefault;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getObjectives() {
		return objectives;
	}

	public void setObjectives(String objectives) {
		this.objectives = objectives;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPartialRegistration() {
		return partialRegistration;
	}

	public void setPartialRegistration(String partialRegistration) {
		this.partialRegistration = partialRegistration;
	}

	@Override
	public String toString() {
		return "InitiativeDescription{" +
				"id=" + id +
				", title='" + title + '\'' +
				", objectives='" + objectives + '\'' +
				", url='" + url + '\'' +
				", isDefault=" + isDefault +
				", partialRegistration='" + partialRegistration + '\'' +
				", language=" + language +
				'}';
	}
}
