package eu.europa.ec.eci.oct.webcommons.services.persistence;

import java.util.List;

import eu.europa.ec.eci.oct.entities.system.Country;

public interface CountryDAO {

	List<Country> getCountries() throws PersistenceException;
	Country getCountryByCode(String code) throws PersistenceException;
	Country getCountryById(long countryId) throws PersistenceException;
	List<String> getCountryCodes() throws PersistenceException;

}
