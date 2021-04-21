package eu.europa.ec.eci.oct.webcommons.services.validation;

import java.util.Date;
import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.eci.oct.entities.system.Country;
import eu.europa.ec.eci.oct.entities.system.CountryRule;
import eu.europa.ec.eci.oct.validation.ValidationBean;
import eu.europa.ec.eci.oct.validation.ValidationResult;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;
import eu.europa.ec.eci.oct.webcommons.services.persistence.PersistenceException;

public interface RuleService {

	ValidationResult validate(ValidationBean validationBean) throws OCTException;

    Date getLastUpdateDate(String countryCode) throws OCTException;

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = OCTException.class)
    void updateRuleByCountryCodeByAppending4Test(Country country, String rule) throws PersistenceException;

    CountryRule getCountryRuleByCode(String countryCode) throws OCTException;

    void preloadRules();
    
    void updateRuleByCountry4Test(Country country, String rule) throws PersistenceException;
    
    String getResource(CountryRule cr) throws OCTException;
    
    public List<Country> getCountryCodeList()  throws PersistenceException;
    
    Country getCountryByCode(String code) throws PersistenceException;

}
