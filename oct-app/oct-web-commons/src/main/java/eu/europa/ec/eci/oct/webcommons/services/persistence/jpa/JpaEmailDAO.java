package eu.europa.ec.eci.oct.webcommons.services.persistence.jpa;

import org.hibernate.HibernateException;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.eci.oct.entities.email.Email;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTobjectNotFoundException;
import eu.europa.ec.eci.oct.webcommons.services.persistence.EmailDAO;
import eu.europa.ec.eci.oct.webcommons.services.persistence.PersistenceException;

@Repository
@Transactional
public class JpaEmailDAO extends AbstractJpaDAO implements EmailDAO {

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = PersistenceException.class)
	public void saveEmail(Email email) throws PersistenceException {
		try {
			this.sessionFactory.getCurrentSession().save(email);
			this.sessionFactory.getCurrentSession().flush();
		} catch (Exception e) {
			throw wrapException("saveEmail " + email, e);
		}

	}
	
	@Override
	@Transactional(readOnly = true)
	public Email findByUuid(String signatureIdentifier) throws PersistenceException, OCTobjectNotFoundException {
		try {
			Query getEmailByUuidQuery = this.sessionFactory.getCurrentSession()
					.createQuery("FROM Email e WHERE e.signatureIdentifier = :signatureIdentifier")
					.setParameter("signatureIdentifier", signatureIdentifier);
			Email emailBySignatureIdentifier = (Email) getEmailByUuidQuery.uniqueResult();
			if (emailBySignatureIdentifier == null) {
				throw new OCTobjectNotFoundException();
			}
			return emailBySignatureIdentifier;
		} catch (HibernateException e) {
			throw wrapException("signatureIdentifier " + signatureIdentifier, e);
		}

	}
	
	@Override
	@Transactional(readOnly = true)
	public Email getEmailByAddress(String emailAddress) throws PersistenceException {
		try {
			logger.debug("querying Email by emailAddress: " + emailAddress);
			return (Email) this.sessionFactory.getCurrentSession()
					.createQuery("FROM Email WHERE emailAddress = :emailAddress")
					.setParameter("emailAddress", emailAddress).uniqueResult();
		} catch (Exception e) {
			throw wrapException("getEmailByAddress", e);
		}
	}
}
