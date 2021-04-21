package eu.europa.ec.eci.oct.webcommons.services.contact;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.eci.oct.entities.admin.Contact;
import eu.europa.ec.eci.oct.entities.admin.ContactRole;
import eu.europa.ec.eci.oct.webcommons.services.BaseService;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;
import eu.europa.ec.eci.oct.webcommons.services.persistence.PersistenceException;

@Service
@Transactional
public class ContactServiceImpl extends BaseService implements ContactService {

	@Override
	@Transactional(readOnly = true)
	public List<Contact> getAllContacts() throws OCTException {
		try {
			return contactDAO.getAllContacts();

		} catch (PersistenceException e) {
			logger.error("persistence problem while fetching all contacts", e);
			throw new OCTException("persistence problem while fetching all contacts", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ContactRole getContactRoleByDescription(String roleDescription) throws OCTException {
		try {
			return contactDAO.getContactRoleByDescription(roleDescription);

		} catch (PersistenceException e) {
			logger.error("persistence problem while getting contactRole by description", e);
			throw new OCTException("persistence problem while getting contactRole by description", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = OCTException.class)
	public void saveContacts(List<Contact> contacts) throws OCTException {
		try {
			contactDAO.saveContacts(contacts);
		} catch (PersistenceException pe) {
			logger.error(pe.getMessage());
			throw new OCTException("Persistence problem while saving contacts: ", pe);
		}

	}

}
