package eu.europa.ec.eci.oct.webcommons.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import eu.europa.ec.eci.oct.webcommons.services.api.transformer.ContactTransformer;
import eu.europa.ec.eci.oct.webcommons.services.api.transformer.InitiativeDescriptionTransformer;
import eu.europa.ec.eci.oct.webcommons.services.api.transformer.SignatureTransformer;
import eu.europa.ec.eci.oct.webcommons.services.configuration.ConfigurationService;
import eu.europa.ec.eci.oct.webcommons.services.contact.ContactService;
import eu.europa.ec.eci.oct.webcommons.services.initiative.InitiativeService;
import eu.europa.ec.eci.oct.webcommons.services.persistence.AccountDAO;
import eu.europa.ec.eci.oct.webcommons.services.persistence.AuthenticationLockDAO;
import eu.europa.ec.eci.oct.webcommons.services.persistence.ContactDAO;
import eu.europa.ec.eci.oct.webcommons.services.persistence.CountryDAO;
import eu.europa.ec.eci.oct.webcommons.services.persistence.CountryRuleDAO;
import eu.europa.ec.eci.oct.webcommons.services.persistence.EmailDAO;
import eu.europa.ec.eci.oct.webcommons.services.persistence.InitiativeDAO;
import eu.europa.ec.eci.oct.webcommons.services.persistence.LanguageDAO;
import eu.europa.ec.eci.oct.webcommons.services.persistence.PropertyDAO;
import eu.europa.ec.eci.oct.webcommons.services.persistence.ReportingDAO;
import eu.europa.ec.eci.oct.webcommons.services.persistence.SettingsDAO;
import eu.europa.ec.eci.oct.webcommons.services.persistence.SignatureDAO;
import eu.europa.ec.eci.oct.webcommons.services.persistence.SocialMediaDAO;
import eu.europa.ec.eci.oct.webcommons.services.persistence.StepStateDAO;
import eu.europa.ec.eci.oct.webcommons.services.persistence.SystemPreferencesDAO;
import eu.europa.ec.eci.oct.webcommons.services.persistence.TranslationDAO;
import eu.europa.ec.eci.oct.webcommons.services.reporting.ReportingService;
import eu.europa.ec.eci.oct.webcommons.services.signature.SignatureService;
import eu.europa.ec.eci.oct.webcommons.services.system.SystemManager;
import eu.europa.ec.eci.oct.webcommons.services.validation.RuleService;

public class BaseService {

	@Autowired
	protected InitiativeService initiativeService;
	@Autowired
	protected SignatureService signatureService;
	@Autowired
	protected ReportingService reportingService;
	@Autowired
	protected SystemManager systemManager;
	@Autowired
	protected ContactService contactService;
	@Autowired
	protected ContactTransformer contactTransformer;
	@Autowired
	protected InitiativeDescriptionTransformer initiativeDescriptionTransformer;
	@Autowired
	protected SignatureTransformer signatureTransformer;
	@Autowired
	protected ConfigurationService configurationService;
	@Autowired
	protected RuleService ruleService;

	// DAO autowires
	@Autowired
	protected LanguageDAO languageDAO;
	@Autowired
	protected StepStateDAO stepStateDAO;
	@Autowired
	protected SystemPreferencesDAO systemPreferencesDAO;
	@Autowired
	protected InitiativeDAO initiativeDAO;
	@Autowired
	protected SignatureDAO signatureDAO;
	@Autowired
	protected ContactDAO contactDAO;
	@Autowired
	protected AccountDAO accountDAO;
	@Autowired
	protected CountryDAO countryDAO;
    @Autowired
    protected CountryRuleDAO countryRuleDAO;
	@Autowired
	protected PropertyDAO propertyDAO;
	@Autowired
	protected TranslationDAO translationDAO;
	@Autowired
	protected SocialMediaDAO socialMediaDAO;
	@Autowired
	protected ReportingDAO reportingDAO;
	@Autowired
	protected SettingsDAO settingsDAO;
	@Autowired
	protected EmailDAO emailDAO;
	@Autowired
	protected AuthenticationLockDAO authenticationLockDAO;

	protected static final String INPUT_PARAMS_EXPECTATION_FAILED = "One or more input parameters failed expectation";

	protected Logger logger = LogManager.getLogger(BaseService.class);

}
