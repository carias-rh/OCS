package eu.europa.ec.eci.oct.webcommons.services.api.domain.captcha;

import java.io.Serializable;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class CaptchaValidationResultDTO implements Serializable{

	private static final long serialVersionUID = 3121277206066068233L;
	
	public CaptchaValidationResultDTO(){}
	
	private boolean valid;
	
	public CaptchaValidationResultDTO(boolean valid){
		this.valid = valid;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}
}
