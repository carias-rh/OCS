package eu.europa.ec.eci.oct.webcommons.services.api.domain.captcha;

import java.io.Serializable;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class CaptchaDTO implements Serializable{

	private static final long serialVersionUID = -6699091521750079140L;
	
	public CaptchaDTO(){}

	private String id;
	
	private byte[] data;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

}
