package eu.europa.ec.eci.oct.webcommons.services.api.domain.supportForm;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import eu.europa.ec.eci.oct.webcommons.services.enums.PropertyEnum;

@Component
public class SupportFormDTO implements Serializable {

	private static final long serialVersionUID = -6193571308590108890L;

	public static final String OCT_PROPERTY_PREFIX = "oct.property.";

	public static List<String> identityDocumentNumbersLabels = Arrays.asList(PropertyEnum.PASSPORT.getName(),
			PropertyEnum.ID_CARD.getName(), PropertyEnum.PERSONAL_NUMBER.getName(), PropertyEnum.PERSONAL_ID.getName(),
			PropertyEnum.NATIONAL_ID_NUMBER.getName(), PropertyEnum.REGISTRATION_CERTIFICATE.getName(),
			PropertyEnum.CITIZEN_CARD.getName(), PropertyEnum.RESIDENCE_PERMIT.getName(),
			PropertyEnum.PERMANENT_RESIDENCE.getName());

	public SupportFormDTO() {
	}

	public static SupportFormDTO cloneSupportFormDTO(SupportFormDTO supportFormDTOtoBeCloned) {
		SupportFormDTO clonedSupportFormDTO = new SupportFormDTO();
		clonedSupportFormDTO.setGroup(supportFormDTOtoBeCloned.getGroup());
		clonedSupportFormDTO.setLabel(supportFormDTOtoBeCloned.getLabel());
		clonedSupportFormDTO.setValue(supportFormDTOtoBeCloned.getValue());
		clonedSupportFormDTO.setPriority(supportFormDTOtoBeCloned.getPriority());
		clonedSupportFormDTO.setRequired(supportFormDTOtoBeCloned.getRequired());
		clonedSupportFormDTO.setType(supportFormDTOtoBeCloned.getType());
		return clonedSupportFormDTO;
	}

	private long id;
	private long priority;
	private int required;
	private int group;
	private String value;
	private String type;
	private String label;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getPriority() {
		return priority;
	}

	public void setPriority(long priority) {
		this.priority = priority;
	}

	public int getRequired() {
		return required;
	}

	public void setRequired(int required) {
		this.required = required;
	}

	public int getGroup() {
		return group;
	}

	public void setGroup(int group) {
		this.group = group;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return "SupportFormDTO [id=" + id + ", priority=" + priority + ", required=" + required + ", group=" + group
				+ ", value=" + value + ", type=" + type + ", label=" + label + "]";
	}

}
