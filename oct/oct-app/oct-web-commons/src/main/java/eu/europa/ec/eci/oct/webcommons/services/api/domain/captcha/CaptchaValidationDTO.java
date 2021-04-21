package eu.europa.ec.eci.oct.webcommons.services.api.domain.captcha;

import java.io.Serializable;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class CaptchaValidationDTO implements Serializable{
	
	private static final long serialVersionUID = -7853625511754021115L;

	public CaptchaValidationDTO(){}
	
	private String id;
	
	private String type;
	
	private String value;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	

}
