package eu.europa.ec.eci.oct.webcommons.services.persistence.jpa;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.eci.oct.entities.admin.InitiativeDescription;
import eu.europa.ec.eci.oct.webcommons.services.persistence.InitiativeDAO;
import eu.europa.ec.eci.oct.webcommons.services.persistence.PersistenceException;

@Repository
@Transactional
@SuppressWarnings("unchecked")
public class JpaInitiativeDAO extends AbstractJpaDAO implements InitiativeDAO {

	@Transactional(readOnly = true)
	public List<InitiativeDescription> getAllDescriptions() throws PersistenceException {
		try {
			logger.debug("querying all descriptions for an initiative");
			List<InitiativeDescription> allDescriptions = this.sessionFactory.getCurrentSession().createQuery("FROM InitiativeDescription").list();

			return allDescriptions;
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			throw wrapException("getAllDescriptions", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = Exception.class)
	public void saveInitiativeDescription(InitiativeDescription newInitiativeDescription) throws PersistenceException {
		try {
			this.sessionFactory.getCurrentSession().save(newInitiativeDescription);
			this.sessionFactory.getCurrentSession().flush();
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			throw wrapException("insertInitiativeDescription " + newInitiativeDescription, e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public InitiativeDescription getDescriptionByLanguageCode(String languageCode) throws PersistenceException {
		logger.debug("querying initiative description for languageCode " + languageCode);
		try {
			Query getInitiativeByLanguageQuery = this.sessionFactory.getCurrentSession().createQuery(
					"FROM InitiativeDescription i WHERE i.language.code = :code");
			getInitiativeByLanguageQuery.setParameter("code", languageCode);
			InitiativeDescription initiativeDescriptionByLanguageCode = (InitiativeDescription) getInitiativeByLanguageQuery.uniqueResult();
			return initiativeDescriptionByLanguageCode;
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			throw wrapException("getDescriptionByLanguageCode " + languageCode, e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public InitiativeDescription getDescriptionById(long id) throws PersistenceException {
		logger.debug("querying initiative description for id " + id);
		try {
			Query getInitiativeByIdQuery = this.sessionFactory.getCurrentSession().createQuery("FROM InitiativeDescription i WHERE i.id = :id");
			getInitiativeByIdQuery.setParameter("id", id);
			InitiativeDescription initiativeDescriptionById = (InitiativeDescription) getInitiativeByIdQuery.uniqueResult();
			return initiativeDescriptionById;
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			throw wrapException("getDescriptionById " + id, e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = Exception.class)
	public void updateInitiativeDescription(InitiativeDescription initiativeDescriptionToBeUpdated) throws PersistenceException {
		try {
			this.sessionFactory.getCurrentSession().update(initiativeDescriptionToBeUpdated);
			this.sessionFactory.getCurrentSession().flush();
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			throw wrapException("updateInitiativeDescription " + initiativeDescriptionToBeUpdated, e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = PersistenceException.class)
	public void deleteDescription(InitiativeDescription description) throws PersistenceException {
		try {
			this.sessionFactory.getCurrentSession().delete(description);
			this.sessionFactory.getCurrentSession().flush();
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			throw wrapException("deleteDescription " + description, e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public InitiativeDescription getDefaultDescription() throws PersistenceException {
		logger.debug("querying initiative for default description");
		try {
			Query getDefaultInitiative = this.sessionFactory.getCurrentSession().createQuery("FROM InitiativeDescription i WHERE i.isDefault = :isDefault");
			getDefaultInitiative.setParameter("isDefault", InitiativeDescription.IS_DEFAULT);
			return (InitiativeDescription) getDefaultInitiative.uniqueResult();
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			throw wrapException("getDefaultDescription", e);
		}
	}

}
