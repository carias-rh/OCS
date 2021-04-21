package eu.europa.ec.eci.oct.webcommons.services.api.domain.commons;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class StringsDTO implements Serializable{

	private static final long serialVersionUID = 5292905138791478891L;
	
	public StringsDTO(){}

	private List<String> values;

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	@Override
	public String toString() {
		return "StringsDTO [values=" + values + "]";
	}
	
	
}
