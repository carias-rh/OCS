package eu.europa.ec.eci.oct.webcommons.services.persistence.jpa;

import java.sql.Clob;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.eci.oct.entities.system.Country;
import eu.europa.ec.eci.oct.entities.system.CountryRule;
import eu.europa.ec.eci.oct.webcommons.services.persistence.CountryRuleDAO;
import eu.europa.ec.eci.oct.webcommons.services.persistence.PersistenceException;

@Repository
@Transactional
@SuppressWarnings("unchecked")
public class JpaCountryRuleDAO extends AbstractJpaDAO implements CountryRuleDAO {

	@Override
	@Transactional(readOnly = true)
	public CountryRule getCountryRuleByCode(String countryCode) throws PersistenceException {
		try {
			Query q = this.sessionFactory.getCurrentSession()
					.createQuery("FROM CountryRule cr WHERE cr.code = :countryCode");
			q.setParameter("countryCode", countryCode);
			CountryRule cr = (CountryRule) q.uniqueResult();
			return cr;
		} catch (Exception e) {
			logger.error(e);
			throw wrapException("getCountryRuleByCode " + countryCode, e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<CountryRule> getCountryRules() throws PersistenceException {
		try {
			Query q = this.sessionFactory.getCurrentSession().createQuery("FROM CountryRule cr");
			List<CountryRule> list = q.list();
			return list;
		} catch (Exception e) {
			logger.error(e);
			throw wrapException("getCountryRules", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Date getLastUpdateDate(String countryCode) throws PersistenceException {
		try {
			Query q = this.sessionFactory.getCurrentSession()
					.createQuery("SELECT lastUpdateDate FROM CountryRule cr WHERE cr.code = :countryCode");
			q.setParameter("countryCode", countryCode);
			Date lastUpdateDate = (Date) q.uniqueResult();
			return lastUpdateDate;
		} catch (Exception e) {
			logger.error(e);
			throw wrapException("getCountryRuleByCode " + countryCode, e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = PersistenceException.class)
	public void updateRuleByCountryCode4Test(Country country, String rule) throws PersistenceException {
		try {
			CountryRule countryRule = getCountryRuleByCode(country.getCode());
			Clob ruleClob = countryRule.getRule();
			ruleClob.setString(1, rule);
			countryRule.setRule(ruleClob);
			countryRule.setLastUpdateDate(new Date());
			this.sessionFactory.getCurrentSession().update(countryRule);
			this.sessionFactory.getCurrentSession().flush();
		} catch (Exception e) {
			logger.error(e);
			throw wrapException("updateRuleByCountryCode " + country.getCode(), e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = PersistenceException.class)
	public void updateRuleByCountryCodeByAppending4Test(Country country, String rule) throws PersistenceException {
		try {
			CountryRule countryRule = getCountryRuleByCode(country.getCode());
			Clob ruleClob = countryRule.getRule();
			ruleClob.setString(ruleClob.length(), rule);
			countryRule.setRule(ruleClob);
			countryRule.setLastUpdateDate(new Date());
			this.sessionFactory.getCurrentSession().update(countryRule);
			this.sessionFactory.getCurrentSession().flush();
		} catch (Exception e) {
			logger.error(e);
			throw wrapException("updateRuleByCountryCode " + country.getCode(), e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public String getRuleClob(String countryCode) throws PersistenceException {
		try {
			Query q = this.sessionFactory.getCurrentSession()
					.createQuery("SELECT cr.rule FROM CountryRule cr WHERE cr.code = :countryCode");
			q.setParameter("countryCode", countryCode);
			java.sql.Clob clobValue = (java.sql.Clob) q.getSingleResult();
			String result = clobValue.getSubString(1, (int) clobValue.length());
			return result;
		} catch (Exception e) {
			throw wrapException("getCountryRuleByCode " + countryCode, e);
		}
	}
}
