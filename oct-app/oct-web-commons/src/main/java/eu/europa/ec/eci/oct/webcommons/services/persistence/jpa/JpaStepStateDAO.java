package eu.europa.ec.eci.oct.webcommons.services.persistence.jpa;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.eci.oct.entities.admin.StepState;
import eu.europa.ec.eci.oct.webcommons.services.persistence.PersistenceException;
import eu.europa.ec.eci.oct.webcommons.services.persistence.StepStateDAO;

@Repository
@Transactional
public class JpaStepStateDAO extends AbstractJpaDAO implements StepStateDAO {

	@Override
	@Transactional(readOnly = true)
	public StepState getStepState() throws PersistenceException {
		try {
			logger.debug("querying StepState");

			StepState stepState = (StepState) this.sessionFactory.getCurrentSession().createQuery("FROM StepState").uniqueResult();
			return stepState;
		} catch (Exception e) {
			throw wrapException("getStepState", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = PersistenceException.class)
	public void setStepState(StepState stepState) throws PersistenceException {
		try {
			this.sessionFactory.getCurrentSession().save(stepState);
			this.sessionFactory.getCurrentSession().flush();
		} catch (Exception e) {
			throw wrapException("setStepState " + stepState, e);
		}

	}

}
