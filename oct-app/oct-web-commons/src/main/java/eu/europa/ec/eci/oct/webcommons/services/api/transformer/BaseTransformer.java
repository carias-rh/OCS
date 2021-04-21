package eu.europa.ec.eci.oct.webcommons.services.api.transformer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import eu.europa.ec.eci.oct.export.persistence.ExportHistoryPersistenceDAO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.signature.SignatureMetadata;
import eu.europa.ec.eci.oct.webcommons.services.contact.ContactService;
import eu.europa.ec.eci.oct.webcommons.services.initiative.InitiativeService;
import eu.europa.ec.eci.oct.webcommons.services.persistence.LanguageDAO;
import eu.europa.ec.eci.oct.webcommons.services.persistence.PropertyDAO;
import eu.europa.ec.eci.oct.webcommons.services.reporting.ReportingService;
import eu.europa.ec.eci.oct.webcommons.services.signature.SignatureService;
import eu.europa.ec.eci.oct.webcommons.services.socialMedia.SocialMediaService;
import eu.europa.ec.eci.oct.webcommons.services.system.SystemManager;

public class BaseTransformer {

	@Autowired
	protected SystemManager systemManager;
	@Autowired
	protected InitiativeService initiativeService;
	@Autowired
	protected ContactService contactService;
	@Autowired
	protected SignatureService signatureService;
	@Autowired
	protected SocialMediaService socialMediaService;
	@Autowired
	protected SignatureMetadata signatureMetadata;
	@Autowired
	protected ReportingService reportingService;
	@Autowired
	protected LanguageDAO languageDAO;
	@Autowired
	protected PropertyDAO propertyDAO;
	@Autowired
	protected ExportHistoryPersistenceDAO exportHistoryPersistenceDAO;

	protected Logger logger = LogManager.getLogger(BaseTransformer.class);
	
}
