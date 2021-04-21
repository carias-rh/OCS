package eu.europa.ec.eci.oct.webcommons.services.validation;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.europa.ec.eci.oct.webcommons.services.persistence.CountryRuleDAO;
import org.kie.api.KieBase;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.command.CommandFactory;
import org.kie.internal.utils.KieHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.eci.oct.entities.system.Country;
import eu.europa.ec.eci.oct.entities.system.CountryRule;
import eu.europa.ec.eci.oct.validation.ValidationBean;
import eu.europa.ec.eci.oct.validation.ValidationError;
import eu.europa.ec.eci.oct.validation.ValidationProperty;
import eu.europa.ec.eci.oct.validation.ValidationResult;
import eu.europa.ec.eci.oct.webcommons.services.BaseService;
import eu.europa.ec.eci.oct.webcommons.services.enums.PropertyEnum;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;
import eu.europa.ec.eci.oct.webcommons.services.persistence.PersistenceException;

@Service
@Transactional
public class RuleServiceImpl extends BaseService implements RuleService {

	public static final String COMMON_RULES_CODE = "common";
	public static final int POSTAL_CODE_DIGIT_LIMIT = 10;

	private Boolean preloadRules = false;

	private static HashMap<String, Map.Entry<Date, KieBase>> kieBaseStoreMap = new HashMap<String, Map.Entry<Date, KieBase>>();

	public RuleServiceImpl() {
	}

	public RuleServiceImpl(Boolean preloadRules) {
		this.preloadRules = preloadRules;
	}

	public void initialize() {
		if (preloadRules) {
			preloadRules();
		}
	}

	public void preloadRules() {
		try {
			List<Country> countryList = countryDAO.getCountries();
			for (Country c : countryList) {
				getKieBase(c.getCode());
			}
		} catch (PersistenceException e) {
			logger.error(e);
			throw new RuntimeException("Error getting getCountryRules in preloadKieSessions", e);
		} catch (OCTException e) {
			logger.error(e);
			throw new RuntimeException("Error getting getCountryRules in preloadKieSessions", e);
		}
	}

	public ValidationResult validate(ValidationBean validationBean) throws OCTException {

		String nationality = validationBean.getNationality();
		StatelessKieSession kieSession = getKieBase(nationality);
		ValidationResult validationResult = new ValidationResult();
		List cmds = new ArrayList();
		cmds.add(CommandFactory.newSetGlobal("nationality", nationality, true));
		cmds.add(CommandFactory.newSetGlobal("validationResult", validationResult, true));
		boolean skipPostalCodeValidation = false;
		for (ValidationProperty vb : validationBean.getProperties()) {
			/*
			 * skip validation on residence postal code if residence country and nationality
			 * are different
			 */
			String value = vb.getValue();
			if (vb.getKey().equalsIgnoreCase(PropertyEnum.RESIDENCE_COUNTRY.getName())
					&& !value.equalsIgnoreCase(nationality)) {
				skipPostalCodeValidation = true;
			}
		}
		/* but if postal code is not valid block it anyway to avoid malicious inputs */
		for (ValidationProperty vb : validationBean.getProperties()) {
			String value = vb.getValue();
			if (vb.getKey().equalsIgnoreCase(PropertyEnum.RESIDENCE_POSTAL_CODE.getName())) {
				if (value.length() > POSTAL_CODE_DIGIT_LIMIT) {
					skipPostalCodeValidation = false;
				}
			}
		}

		if (skipPostalCodeValidation) {
			// remove postal code
			List<ValidationProperty> validationPropertiesWithoutPostalCode = new ArrayList<>();
			for (ValidationProperty vp : validationBean.getProperties()) {
				if (!vp.getKey().equalsIgnoreCase(PropertyEnum.RESIDENCE_POSTAL_CODE.getName())) {
					validationPropertiesWithoutPostalCode.add(vp);
				}
			}
			validationBean.getProperties().clear();
			validationBean.setProperties(validationPropertiesWithoutPostalCode);
		}

		for (ValidationProperty vb : validationBean.getProperties()) {
			// logger.info("validating: "+vb.getKey() + " / "+ vb.getValue());
			cmds.add(CommandFactory.newInsert(vb));
		}

		ExecutionResults results = kieSession.execute(CommandFactory.newBatchExecution(cmds));
		// logger.info("validated with errors[" +
		// validationResult.getValidationErrors().size()+"]");
		for (ValidationError ve : validationResult.getValidationErrors()) {
			logger.info("Not valid fields: " + ve.getKey() + "[" + ve.getErrorKey() + "]");
		}

		return validationResult;
	}

	/**
	 * Add a temporary property for matching and country of residence format:
	 * postal.code_{country}
	 * 
	 * @param validationBean
	 */
	// private void addPostalCodeByCountry(ValidationBean validationBean) {
	// ValidationProperty vpCountry = null;
	// ValidationProperty vpPostalCode = null;
	//
	// for (ValidationProperty vp : validationBean.getProperties()) {
	// if
	// (vp.getKey().equalsIgnoreCase(PropertyEnum.RESIDENCE_POSTAL_CODE.getName()))
	// {
	// vpPostalCode = vp;
	// }
	// if (vp.getKey().equalsIgnoreCase(PropertyEnum.RESIDENCE_COUNTRY.getName())) {
	// vpCountry = vp;
	// }
	// }
	//
	// if (vpCountry != null && vpPostalCode != null && vpCountry.getValue() != null
	// && !vpCountry.getValue().equals("")) {
	// validationBean.getProperties().add(new ValidationProperty(
	// vpPostalCode.getKey() + "_" + vpCountry.getValue(),
	// vpPostalCode.getValue()));
	// }
	// }

	private StatelessKieSession getKieBase(String country) throws OCTException {
		StatelessKieSession kieSession = null;
		Map.Entry<Date, KieBase> storeEntry = kieBaseStoreMap.get(country);
		if (storeEntry == null || refreshKieBaseStore(country)) {
			KieHelper kieHelper = new KieHelper();
			CountryRule crCountry = getCountryRuleByCode(country);
			CountryRule crCommon = getCountryRuleByCode(COMMON_RULES_CODE);
			kieHelper.addContent(getResource(crCommon), ResourceType.DRL);
			kieHelper.addContent(getResource(crCountry), ResourceType.DRL);
			Date lastUpdateDate = crCountry.getLastUpdateDate();
			crCommon = null;
			crCountry = null;
			// compiling to check of any errors;
			Results results = kieHelper.verify();

			if (results.hasMessages()) {
				logger.error("KIE compilation errors! check the rule files : COMMON and " + country);
				for (Message err : results.getMessages()) {
					logger.error(err.toString());
				}
				throw new OCTException("KIE compilation errors! check the rule files : COMMON and " + country);
			}
			KieBase kieBase = kieHelper.build();
			kieSession = kieBase.newStatelessKieSession();
			kieHelper = null;
			// kSessionMap.put(country, new AbstractMap.SimpleEntry<Date,
			// StatelessKieSession>(lastUpdateDate, kieSession));
			kieBaseStoreMap.put(country, new AbstractMap.SimpleEntry<Date, KieBase>(lastUpdateDate, kieBase));
			logger.info("********************** new created KieSession '" + country + "': " + kieSession);
			logger.info("Kie rule for country '" + country + "' correctly verified and cached");
		} else {
			kieSession = storeEntry.getValue().newStatelessKieSession();
			logger.info("********************** old cached KieSession '" + country + "': " + kieSession);
		}
		return kieSession;
	}

	@Override
	public String getResource(CountryRule cr) throws OCTException {
		try {

//			return cr.getRule().getSubString(1, (int) cr.getRule().length());
			return countryRuleDAO.getRuleClob(cr.getCode());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new OCTException("Error reading BLOB from oct_country_rules > " + cr.getCode(), e);
		}

	}

	private boolean refreshKieBaseStore(String countryCode) throws OCTException {
		Date countryCodeKieDate = getLastUpdateDate(countryCode);
		if (kieBaseStoreMap.get(countryCode).getKey().compareTo(countryCodeKieDate) != 0) {
			return true;
		}
		return false;
	}

	@Override
	@Transactional(readOnly = true)
	public Date getLastUpdateDate(String countryCode) throws OCTException {
		try {
			return countryRuleDAO.getLastUpdateDate(countryCode);
		} catch (PersistenceException e) {
			logger.error("There was a problem while retrieving getLastUpdateDate " + countryCode + "  "
					+ e.getLocalizedMessage(), e);
			throw new OCTException("There was a roblem while retrieving getCountryRuleByCode ", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<Country> getCountryCodeList() throws PersistenceException {
		return countryDAO.getCountries();
	}

	@Override
	@Transactional(readOnly = true)
	public Country getCountryByCode(String code) throws PersistenceException {
		return countryDAO.getCountryByCode(code);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = OCTException.class)
	public void updateRuleByCountry4Test(Country country, String rule) throws PersistenceException {
		countryRuleDAO.updateRuleByCountryCode4Test(country, rule);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = OCTException.class)
	public void updateRuleByCountryCodeByAppending4Test(Country country, String rule) throws PersistenceException {
		countryRuleDAO.updateRuleByCountryCodeByAppending4Test(country, rule);
	}

	@Override
	@Transactional(readOnly = true)
	public CountryRule getCountryRuleByCode(String countryCode) throws OCTException {
		try {
			return countryRuleDAO.getCountryRuleByCode(countryCode);
		} catch (PersistenceException e) {
			logger.error("There was a problem while retrieving getCountryRuleByCode " + countryCode + "  "
					+ e.getLocalizedMessage(), e);
			throw new OCTException("There was a roblem while retrieving getCountryRuleByCode ", e);
		}
	}
}
