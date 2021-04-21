package eu.europa.ec.eci.oct.export.dto;

import java.io.Serializable;

import org.springframework.stereotype.Component;

@Component
public class ExportStringDTO implements Serializable{

	private static final long serialVersionUID = 8079866007519271831L;
	
	private String value;
	
	public ExportStringDTO(){}

	public ExportStringDTO(String value) {
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
