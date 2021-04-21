package eu.europa.ec.eci.oct.webcommons.services.persistence.jpa;

import org.hibernate.HibernateException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.eci.oct.entities.admin.SystemPreferences;
import eu.europa.ec.eci.oct.webcommons.services.persistence.PersistenceException;
import eu.europa.ec.eci.oct.webcommons.services.persistence.SystemPreferencesDAO;

@Repository
@Transactional
public class JpaSystemPreferencesDAO extends AbstractJpaDAO implements SystemPreferencesDAO {

	@Override
	@Transactional(readOnly = true)
	public SystemPreferences getSystemPreferences() throws PersistenceException {
		try {
			SystemPreferences systemPreferences = (SystemPreferences) this.sessionFactory.getCurrentSession().createQuery("FROM SystemPreferences")
					.uniqueResult();
			return systemPreferences;
		} catch (HibernateException he) {
			logger.error(he.getMessage());
			throw new PersistenceException(he.getMessage());
		}
	}


	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = PersistenceException.class)
	public void setCollecting(boolean collecting) throws PersistenceException {
		try {
			SystemPreferences prefs = getSystemPreferences();
			prefs.setCollecting(collecting);
			this.sessionFactory.getCurrentSession().update(prefs);
		} catch (HibernateException e) {
			throw wrapException("setCollecting " + collecting, e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = PersistenceException.class)
	public void setPreferences(SystemPreferences prefs) throws PersistenceException {
		try {
			this.sessionFactory.getCurrentSession().update(prefs);
			this.sessionFactory.getCurrentSession().flush();
		} catch (HibernateException e) {
			throw wrapException("setPreferences " + prefs, e);
		}
	}
}