package eu.europa.ec.eci.oct.webcommons.services.api.domain.security;

import java.io.Serializable;

import org.springframework.stereotype.Component;

@Component
public class AuthenticationTokenDTO implements Serializable {

	private static final long serialVersionUID = 1754586811035378674L;

	private String status;
	private Integer code;
	private String authToken;
	private Integer expireTimeMinutes;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public Integer getExpireTimeMinutes() {
		return expireTimeMinutes;
	}

	public void setExpireTimeMinutes(Integer expireTimeMinutes) {
		this.expireTimeMinutes = expireTimeMinutes;
	}

	@Override
	public String toString() {
		return "AuthenticationTokenDTO [status=" + status + ", code=" + code + ", authToken=" + authToken
				+ ", expireTimeMinutes=" + expireTimeMinutes + "]";
	}
	
	
}
