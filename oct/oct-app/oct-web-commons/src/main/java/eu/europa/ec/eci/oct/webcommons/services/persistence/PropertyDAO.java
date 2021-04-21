package eu.europa.ec.eci.oct.webcommons.services.persistence;

import java.util.List;

import eu.europa.ec.eci.oct.entities.CountryProperty;
import eu.europa.ec.eci.oct.entities.Property;
import eu.europa.ec.eci.oct.entities.PropertyGroup;
import eu.europa.ec.eci.oct.entities.system.Country;

public interface PropertyDAO {

	public List<PropertyGroup> getGroups() throws PersistenceException;

	public List<CountryProperty> getProperties(Country c, PropertyGroup g) throws PersistenceException;

	public CountryProperty getCountryPropertyById(Long id) throws PersistenceException;

	public List<CountryProperty> getAllCountryProperties() throws PersistenceException;

	public List<Property> getAllProperties() throws PersistenceException;

	List<CountryProperty> getCountryPropertiesByCountryCode(String countryCode) throws PersistenceException;

	public List<CountryProperty> getCountryPropertiesByCountryCodes(List<String> countryCodes)
			throws PersistenceException;

	public Property getPropertyByLabel(String string) throws PersistenceException;

}
