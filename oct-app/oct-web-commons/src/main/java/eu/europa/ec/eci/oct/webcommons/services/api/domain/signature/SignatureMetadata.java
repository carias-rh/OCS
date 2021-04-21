package eu.europa.ec.eci.oct.webcommons.services.api.domain.signature;

import java.io.Serializable;

import org.springframework.stereotype.Component;

/**
 * User: franzmh
 * Date: 06/12/16
 */

@Component
public class SignatureMetadata implements Serializable{
	
	private static final long serialVersionUID = -8759570552632034718L;

	public SignatureMetadata(){}
	
    private String date;
    private String country;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
