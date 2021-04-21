package eu.europa.ec.eci.oct.webcommons.services.api.transformer;

import java.util.List;

import org.springframework.stereotype.Component;

import eu.europa.ec.eci.oct.entities.system.Country;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.country.CountryDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.country.CountryDTOlist;

@Component
public class CountryTransformer extends BaseTransformer{

	public CountryDTOlist transformCountries(List<Country> countryDAOlist) {
		
		if (countryDAOlist == null) {
			return null;
		}
		
		CountryDTOlist countryList = new CountryDTOlist();
		for (Country countryDAO : countryDAOlist) {
			CountryDTO countryDTO = transformCountry(countryDAO);
			countryList.addCountry(countryDTO);
		}

		return countryList;
	}

	public CountryDTO transformCountry(Country countryDAO) {

		if (countryDAO == null) {
			return null;
		}
		CountryDTO countryDTO = new CountryDTO();
		countryDTO.setCountryCode(countryDAO.getCode());
		countryDTO.setCountryName(countryDAO.getName());

		return countryDTO;
	}

}
