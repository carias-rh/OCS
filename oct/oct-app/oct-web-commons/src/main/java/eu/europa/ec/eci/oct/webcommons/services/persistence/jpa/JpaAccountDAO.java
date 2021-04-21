package eu.europa.ec.eci.oct.webcommons.services.persistence.jpa;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.eci.oct.entities.admin.Account;
import eu.europa.ec.eci.oct.webcommons.services.persistence.AccountDAO;
import eu.europa.ec.eci.oct.webcommons.services.persistence.PersistenceException;

@Repository
@Transactional
public class JpaAccountDAO extends AbstractJpaDAO implements AccountDAO {

	@Override
	@Transactional(readOnly = true)
	public Account getAccountByName(String username) throws PersistenceException {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("querying account by username " + username);
			}
			return (Account) this.sessionFactory.getCurrentSession()
					.createQuery("FROM Account a WHERE a.username = :username").setParameter("username", username).uniqueResult();
		} catch (Exception e) {
			throw wrapException("getAccountByName " + username, e);
		}
	}

	@Override
	@Transactional()
	public Account save(String username) {

		Account account = new Account();
		account.setUsername(username);

		this.sessionFactory.getCurrentSession().save(account);
		this.sessionFactory.getCurrentSession().flush();

		return account;
	}

}
