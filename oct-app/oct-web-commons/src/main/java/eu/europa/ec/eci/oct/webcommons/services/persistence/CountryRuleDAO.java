package eu.europa.ec.eci.oct.webcommons.services.persistence;

import java.util.Date;
import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.eci.oct.entities.system.Country;
import eu.europa.ec.eci.oct.entities.system.CountryRule;

public interface CountryRuleDAO {

    CountryRule getCountryRuleByCode(String countryCode) throws PersistenceException;

    List<CountryRule> getCountryRules() throws PersistenceException;
    
    @Transactional(readOnly = true)
    String getRuleClob(String countryCode) throws PersistenceException;

    Date getLastUpdateDate(String countryCode) throws PersistenceException;

    void updateRuleByCountryCode4Test(Country country, String rule) throws PersistenceException;

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = PersistenceException.class)
    void updateRuleByCountryCodeByAppending4Test(Country country, String rule) throws PersistenceException;
}
