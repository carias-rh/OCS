package eu.europa.ec.eci.oct.webcommons.services.persistence.jpa;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.eci.oct.entities.AuthenticationLock;
import eu.europa.ec.eci.oct.webcommons.services.persistence.AuthenticationLockDAO;
import eu.europa.ec.eci.oct.webcommons.services.persistence.PersistenceException;

@Repository
@Transactional
@SuppressWarnings("unchecked")
public class JpaAuthenticationLockDAO extends AbstractJpaDAO implements AuthenticationLockDAO {

	@Override
	@Transactional(readOnly = true)
	public List<AuthenticationLock> getAllAuthenticationLock() throws PersistenceException {
		try {

			return (List<AuthenticationLock>) this.sessionFactory.getCurrentSession()
					.createQuery("FROM AuthenticationLock").list();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw wrapException("getAllAuthenticationLock", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public AuthenticationLock getAuthenticationLockForUser(String username) throws PersistenceException {
		try {
			return (AuthenticationLock) this.sessionFactory.getCurrentSession()
					.createQuery("FROM AuthenticationLock a WHERE username = :username")
					.setParameter("username", username).uniqueResult();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw wrapException("getAuthenticationLockForUser " + username, e);
		}
	}

}
