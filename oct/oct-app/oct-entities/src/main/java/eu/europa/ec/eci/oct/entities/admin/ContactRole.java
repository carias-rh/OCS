package eu.europa.ec.eci.oct.entities.admin;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "OCT_CONTACT_ROLE")
public class ContactRole implements Serializable {

	private static final long serialVersionUID = 5439692427863243836L;

	public static final String MEMBER = "member";
	public static final String SUBSTITUTE = "substitute";
	public static final String REPRESENTATIVE = "representative";
	public static final String LEGAL_ENTITY = "entity";
	public static final String NEW_REPRESENTATIVE = "newRepresentative";
	public static final String NEW_SUBSTITUTE = "newSubstitute";
	public static final String OTHER = "other";
	public static final int REPRESENTATIVE_ID = 1;
	public static final int SUBSTITUTE_ID = 2;
	public static final int MEMBER_ID = 3;
	public static final int LEGAL_ENTITY_ID = 4;
	public static final int NEW_REPRESENTATIVE_ID = 5;
	public static final int NEW_SUBSTITUTE_ID = 6;
	public static final int OTHER_ID = 7;

	@Id
	private Long id;

	@Column(nullable = false, length = 20)
	private String roleDescription;

	public String getRoleDescription() {
		return roleDescription;
	}

	public void setRoleDescription(String roleDescription) {
		this.roleDescription = roleDescription;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "ContactRole [id=" + id + ", roleDescription=" + roleDescription + "]";
	}

}
