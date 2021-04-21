package eu.europa.ec.eci.oct.webcommons.services.api.domain.security;

import java.io.Serializable;

import org.springframework.stereotype.Component;

@Component
public class ChallengeDTO implements Serializable {

	private static final long serialVersionUID = 4758001989770469412L;
	private String challenge;

	public String getChallenge() {
		return challenge;
	}

	public void setChallenge(String challenge) {
		this.challenge = challenge;
	}
}
