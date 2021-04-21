package eu.europa.ec.eci.oct.webcommons.services.persistence.jpa;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.eci.oct.entities.system.Country;
import eu.europa.ec.eci.oct.webcommons.services.persistence.CountryDAO;
import eu.europa.ec.eci.oct.webcommons.services.persistence.PersistenceException;

@Repository
@Transactional
@SuppressWarnings("unchecked")
public class JpaCountryDAO extends AbstractJpaDAO implements CountryDAO {

	@Override
	@Transactional(readOnly = true)
	public List<Country> getCountries() throws PersistenceException {
		try {
			logger.debug("querying all countries registered within system");

			List<Country> allCountries = this.sessionFactory.getCurrentSession().createQuery("FROM Country c ORDER BY c.code asc").list();

			return allCountries;
		} catch (Exception e) {
			throw wrapException("getCountries", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<String> getCountryCodes() throws PersistenceException {
		try {
			logger.debug("querying all countryCodes registered within system");

			List<String> countryCodes = (List<String>) this.sessionFactory.getCurrentSession().createQuery("SELECT c.code FROM Country c").list();

			return countryCodes;
		} catch (Exception e) {
			throw wrapException("getCountryCodes", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Country getCountryByCode(String code) throws PersistenceException {
		try {
			Query getCountryByCodeQuery = this.sessionFactory.getCurrentSession().createQuery("FROM Country c WHERE c.code = :code");
			getCountryByCodeQuery.setParameter("code", code);
			Country countryByCode = (Country) getCountryByCodeQuery.uniqueResult();
			return countryByCode;
		} catch (Exception e) {
			throw wrapException("getCountryByCode " + code, e);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public Country getCountryById(long countryId) throws PersistenceException {
		try {
			Query getCountryByIdQuery = this.sessionFactory.getCurrentSession().createQuery("FROM Country c WHERE c.id = :countryId");
			getCountryByIdQuery.setParameter("countryId", countryId);
			Country countryById = (Country) getCountryByIdQuery.uniqueResult();
			return countryById;
		} catch (Exception e) {
			throw wrapException("getCountryById " + countryId, e);
		}
	}

}
