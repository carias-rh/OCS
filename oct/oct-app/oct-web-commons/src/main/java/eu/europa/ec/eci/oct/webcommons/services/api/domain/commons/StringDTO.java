package eu.europa.ec.eci.oct.webcommons.services.api.domain.commons;

import java.io.Serializable;

import org.springframework.stereotype.Component;

@Component
public class StringDTO implements Serializable{

	private static final long serialVersionUID = 8079866007519271831L;
	
	private String value;
	
	public StringDTO(){}

	public StringDTO(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "StringDTO [value=" + value + "]";
	}
	
}
