package eu.europa.ec.eci.oct.webcommons.services.api.domain.country;

import java.io.Serializable;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class CountryDTO implements Serializable {

	private static final long serialVersionUID = 1695974744911130233L;

	public CountryDTO() {
	}

	private String countryCode;
	private String countryName;

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	@Override
	public String toString() {
		return "CountryDTO [countryCode=" + countryCode + ", countryName=" + countryName + "]";
	}

}
