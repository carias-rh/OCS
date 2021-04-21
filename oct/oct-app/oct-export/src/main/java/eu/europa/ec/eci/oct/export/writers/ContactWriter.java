package eu.europa.ec.eci.oct.export.writers;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.item.ItemWriter;

import eu.europa.ec.eci.oct.entities.admin.Contact;
import eu.europa.ec.eci.oct.entities.admin.ContactRole;
import eu.europa.ec.eci.oct.export.ExportJobRunner;
import eu.europa.ec.eci.oct.export.entities.ContactStrings;

public class ContactWriter implements ItemWriter<Contact> {

	private final Logger logger = LogManager.getLogger(ContactWriter.class);

	// private StepExecution stepExecution;

	@Override
	public void write(List<? extends Contact> contacts) throws Exception {

		boolean newRep = false;
		boolean newSub = false;
		boolean isLegalEntity = false;
		for (Contact contact : contacts) {
			int role = contact.getContactRole().getId().intValue();
			if (role == ContactRole.NEW_REPRESENTATIVE_ID) {
				newRep = true;
			}
			if (role == ContactRole.NEW_SUBSTITUTE_ID) {
				newSub = true;
			}
			if (role == ContactRole.LEGAL_ENTITY_ID) {
				isLegalEntity = true;
			}
		}

		String representative = "";
		String substitute = "";
		String legalEntity = "";
		for (Contact contact : contacts) {
			int role = contact.getContactRole().getId().intValue();
			String email = contact.getEmail();
			String firstName = contact.getFirstName() + " ";
			String familyName = "";
			if(StringUtils.isNotBlank(contact.getFamilyName())) {
				familyName = contact.getFamilyName().toUpperCase() + " ";
			}
			String personContactString = firstName + familyName + email;
			switch (role) {
			case ContactRole.REPRESENTATIVE_ID:
				if (!newRep) {
					representative = personContactString;
				} else {
					// ignore it, just take the new one
				}
				break;
			case ContactRole.NEW_REPRESENTATIVE_ID:
				representative = personContactString;
				break;
			case ContactRole.SUBSTITUTE_ID:
				if (!newSub) {
					substitute = personContactString;
				} else {
					// ignore it, just take the new one
				}
				break;
			case ContactRole.NEW_SUBSTITUTE_ID:
				substitute = personContactString;
				break;
			case ContactRole.MEMBER_ID:
				// ignored
				break;
			case ContactRole.LEGAL_ENTITY_ID:
				String countryCode = contact.getCountry().getCode();
				legalEntity += firstName + ", " + countryCode.toUpperCase();
				break;
			default:
				throw new Exception("Contact role not recognised: " + role);
			}
		}
		String contactPersonsList = representative + ", " + substitute;
		if (isLegalEntity) {
			contactPersonsList += ", LEGAL ENTITY: " + legalEntity;
		}

		ContactStrings contactStrings = new ContactStrings();
		contactStrings.setContactString(contactPersonsList);
		ExportJobRunner.contactStrings = contactStrings;
		logger.info(">> obtained contactStrings: " + ExportJobRunner.contactStrings);

		// clean step context
		contactStrings = null;
	}

	// @BeforeStep
	// public void saveStepExecution(StepExecution stepExecution) {
	// this.stepExecution = stepExecution;
	// }

}
