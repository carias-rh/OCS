package eu.europa.ec.eci.oct.webcommons.services.persistence.jpa;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.eci.oct.entities.admin.Contact;
import eu.europa.ec.eci.oct.entities.admin.ContactRole;
import eu.europa.ec.eci.oct.webcommons.services.persistence.ContactDAO;
import eu.europa.ec.eci.oct.webcommons.services.persistence.PersistenceException;

@Repository
@Transactional
public class JpaContactDAO extends AbstractJpaDAO implements ContactDAO {

	@Override
	@Transactional(readOnly = true)
	public List<Contact> getAllContacts() throws PersistenceException {
		try {

			logger.debug("querying all contacts");

			@SuppressWarnings("unchecked")
			List<Contact> allContacts = this.sessionFactory.getCurrentSession().createQuery("FROM Contact").list();

			return allContacts;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw wrapException("getAllContacts", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ContactRole getContactRoleByDescription(String roleDescription) throws PersistenceException {
		try {
			logger.debug("querying contactRole for description " + roleDescription);

			Query getContactRoleByDescriptionQuery = this.sessionFactory.getCurrentSession().createQuery(
					"FROM ContactRole c WHERE c.roleDescription = :roleDescription");
			ContactRole contactRoleByDescription = (ContactRole) getContactRoleByDescriptionQuery.setParameter("roleDescription", roleDescription)
					.uniqueResult();
			return contactRoleByDescription;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw wrapException("getContactRoleByDescription " + roleDescription, e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = PersistenceException.class)
	public void saveContacts(List<Contact> contacts) throws PersistenceException {

		if (!getAllContacts().isEmpty()) {
			// delete old contacts
			logger.info("Removing old contacts...");
			try {
				for (Contact contactToBeRemoved : getAllContacts()) {
					this.sessionFactory.getCurrentSession().delete(contactToBeRemoved);
					this.sessionFactory.getCurrentSession().flush();
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw wrapException("delete old contacts ", e);
			}
		}
		
		// save new ones
		for (Contact contactToBeSaved : contacts) {
			logger.info("Persisting new contact: " + contactToBeSaved);
			try {
				this.sessionFactory.getCurrentSession().save(contactToBeSaved);
				this.sessionFactory.getCurrentSession().flush();
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw wrapException("saveContacts " + contactToBeSaved, e);
			}
		}

	}

}
