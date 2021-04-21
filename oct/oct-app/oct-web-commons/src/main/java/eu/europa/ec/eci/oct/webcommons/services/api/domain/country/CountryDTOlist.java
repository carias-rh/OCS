package eu.europa.ec.eci.oct.webcommons.services.api.domain.country;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class CountryDTOlist implements Serializable{
	
	private static final long serialVersionUID = 1346754862195844596L;

	public CountryDTOlist(){}
	
	private List<CountryDTO> countries;

	public List<CountryDTO> getCountries() {
		return countries;
	}

	public void setCountries(List<CountryDTO> countries) {
		this.countries = countries;
	}
	
	public void addCountry(CountryDTO countryDTO){
		if(this.countries == null){
			this.countries = new ArrayList<CountryDTO>();
		}
		this.countries.add(countryDTO);
	}

	@Override
	public String toString() {
		return "CountryDTOlist [countries=" + countries + "]";
	}

}
