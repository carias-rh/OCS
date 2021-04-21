package eu.europa.ec.eci.oct.validation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ValidationBean implements Serializable {

	private static final long serialVersionUID = 478278036851163792L;

	private String nationality;
	private List<ValidationProperty> validationPropertyBeanList = new ArrayList<ValidationProperty>();

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public List<ValidationProperty> getProperties() {
		return validationPropertyBeanList;
	}

	public void setProperties(List<ValidationProperty> validationPropertyBeanList) {
		this.validationPropertyBeanList = validationPropertyBeanList;
	}

	public void addProperty(ValidationProperty validationProperty) {
		validationPropertyBeanList.add(validationProperty);
	}

	/**
	 * If a property already exists on the property list, it changes it's value,
	 * otherwise, it adds the new property
	 * 
	 * @param vp
	 */
	public void addOrSetProperty(ValidationProperty vp) {
		int propertySetted = 0;
		for (ValidationProperty validationProperty : validationPropertyBeanList) {
			if (validationProperty.getKey().equalsIgnoreCase(vp.getKey())) {
				validationProperty.setValue(vp.getValue());
				propertySetted = 1;
			}
		}
		if (propertySetted == 0) {
			validationPropertyBeanList.add(vp);
		}
	}

	/**
	 * Removes a specific validationProperty from validationPropertyBeanList
	 * 
	 * @param validationProperty
	 */
	public void removeProperty(ValidationProperty validationProperty) {
		ValidationProperty vpFound = null;
		for (ValidationProperty vp : validationPropertyBeanList) {
			if (vp.getKey().equalsIgnoreCase(validationProperty.getKey())) {
				vpFound = vp;
			}
		}
		if(vpFound != null){
			validationPropertyBeanList.remove(vpFound);
		}
	}

	public String getPropertyValue(ValidationProperty validationProperty) {
		for (ValidationProperty vp : validationPropertyBeanList) {
			if (vp.getKey().equals(validationProperty.getKey())) {
				return vp.getValue();
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return "ValidationBean{" + "nationality='" + nationality + '\'' + ", validationPropertyBeanList="
				+ validationPropertyBeanList + '}';
	}

}
