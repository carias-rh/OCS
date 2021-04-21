package eu.europa.ec.eci.oct.webcommons.services.persistence.jpa;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.eci.oct.entities.CountryProperty;
import eu.europa.ec.eci.oct.entities.Property;
import eu.europa.ec.eci.oct.entities.PropertyGroup;
import eu.europa.ec.eci.oct.entities.system.Country;
import eu.europa.ec.eci.oct.webcommons.services.persistence.PersistenceException;
import eu.europa.ec.eci.oct.webcommons.services.persistence.PropertyDAO;

@Repository
@Transactional
@SuppressWarnings("unchecked")
public class JpaPropertyDAO extends AbstractJpaDAO implements PropertyDAO {

	private final static String QUERY_PROPERTY_SELECT_M = "SELECT cp ";
	private final static String QUERY_PROPERTY = "FROM CountryProperty cp JOIN cp.property p where cp.country = :country and p.group = :group ORDER BY p.priority DESC ";
	private final static String QUERY_COUNTRY_PROPERTY_BY_CODE = "FROM CountryProperty cp JOIN cp.property p where cp.country.code = :countryCode";
	private final static String QUERY_COUNTRY_PROPERTY_BY_CODES = "FROM CountryProperty cp JOIN cp.property p ";

	@Transactional(readOnly = true)
	public List<PropertyGroup> getGroups() throws PersistenceException {
		try {
			List<PropertyGroup> groups = this.sessionFactory.getCurrentSession().createQuery("FROM PropertyGroup")
					.list();
			return groups;
		} catch (Exception e) {
			throw wrapException("getGroups", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<CountryProperty> getProperties(Country c, PropertyGroup g) throws PersistenceException {
		try {

			Query q = this.sessionFactory.getCurrentSession().createQuery(QUERY_PROPERTY_SELECT_M + QUERY_PROPERTY);
			q.setParameter("country", c);
			q.setParameter("group", g);

			List<CountryProperty> props = q.list();

			return props;
		} catch (Exception e) {
			throw wrapException("getProperties " + c + " " + g, e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<CountryProperty> getCountryPropertiesByCountryCode(String countryCode) throws PersistenceException {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("querying countryProperties for countryCode " + countryCode);
			}

			Query q = this.sessionFactory.getCurrentSession()
					.createQuery(QUERY_PROPERTY_SELECT_M + QUERY_COUNTRY_PROPERTY_BY_CODE);
			q.setParameter("countryCode", countryCode);

			List<CountryProperty> cprops = q.list();
			if (logger.isDebugEnabled()) {
				logger.debug("returning list containing " + cprops.size() + " items");
			}

			return cprops;
		} catch (Exception e) {
			throw wrapException("getCountryPropertiesByCountryCode " + countryCode, e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<CountryProperty> getCountryPropertiesByCountryCodes(List<String> countryCodes)
			throws PersistenceException {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("querying countryProperties for countryCodes " + countryCodes);
			}

			StringBuilder whereClause = new StringBuilder();
			whereClause.append("WHERE cp.country.code IN(");
			for (int i = 0; i < countryCodes.size(); i++) {
				String countryCode = countryCodes.get(i);
				whereClause.append("'" + countryCode + "'");
				if (i < countryCodes.size() - 1) {
					whereClause.append(",");
				}
			}
			whereClause.append(")");

			Query q = this.sessionFactory.getCurrentSession()
					.createQuery(QUERY_PROPERTY_SELECT_M + QUERY_COUNTRY_PROPERTY_BY_CODES + whereClause);

			List<CountryProperty> cprops = q.list();
			if (logger.isDebugEnabled()) {
				logger.debug("returning list containing " + cprops.size() + " items");
			}

			return cprops;
		} catch (Exception e) {
			throw wrapException("getCountryPropertiesByCountryCodes ", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public CountryProperty getCountryPropertyById(Long id) throws PersistenceException {
		try {

			Query getCountryPropertyByIdQuery = this.sessionFactory.getCurrentSession()
					.createQuery("FROM CountryProperty cp WHERE cp.id = :id").setParameter("id", id);
			CountryProperty countryPropertyById = (CountryProperty) getCountryPropertyByIdQuery.uniqueResult();
			return countryPropertyById;
		} catch (Exception e) {
			throw wrapException("getCountryPropertyById " + id, e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<CountryProperty> getAllCountryProperties() throws PersistenceException {
		try {
			List<CountryProperty> allCountryProperties = this.sessionFactory.getCurrentSession()
					.createQuery("FROM CountryProperty cp").list();
			return allCountryProperties;
		} catch (Exception e) {
			throw wrapException("getAllCountryProperties", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<Property> getAllProperties() throws PersistenceException {
		try {
			List<Property> allProperties = this.sessionFactory.getCurrentSession().createQuery("FROM Property").list();
			return allProperties;
		} catch (Exception e) {
			throw wrapException("getAllProperties", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Property getPropertyByLabel(String label) throws PersistenceException {
		try {
			return (Property) this.sessionFactory.getCurrentSession().createQuery("FROM Property WHERE name = :label")
					.setParameter("label", label).uniqueResult();
		} catch (Exception e) {
			throw wrapException("getPropertyByLabel(" + label + ")", e);
		}
	}

}
