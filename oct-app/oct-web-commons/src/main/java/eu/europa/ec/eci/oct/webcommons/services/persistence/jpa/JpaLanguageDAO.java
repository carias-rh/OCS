package eu.europa.ec.eci.oct.webcommons.services.persistence.jpa;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.eci.oct.entities.system.Language;
import eu.europa.ec.eci.oct.webcommons.services.persistence.LanguageDAO;
import eu.europa.ec.eci.oct.webcommons.services.persistence.PersistenceException;

@Repository
@Transactional
@SuppressWarnings("unchecked")
public class JpaLanguageDAO extends AbstractJpaDAO implements LanguageDAO {

	@Override
	@Transactional(readOnly = true)
	public List<String> getLanguageCodes() throws PersistenceException {
		try {
			logger.debug("querying all languageCodes registered within system");

			List<String> languageCodes = (List<String>) this.sessionFactory.getCurrentSession().createQuery("SELECT l.code FROM Language l").list();

			return languageCodes;
		} catch (Exception e) {
			throw wrapException("getLanguageCodes", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<Language> getLanguages() throws PersistenceException {
		try {
			logger.debug("querying all languages registered within system");

			List<Language> languages = (List<Language>) this.sessionFactory.getCurrentSession().createQuery("FROM Language").list();

			return languages;
		} catch (Exception e) {
			throw wrapException("getLanguages", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Language getLanguageByCode(String code) throws PersistenceException {
		try {
			Query getLanguageByCodeQuery = this.sessionFactory.getCurrentSession().createQuery("FROM Language l WHERE l.code = :code");
			getLanguageByCodeQuery.setParameter("code", code);
			return (Language) getLanguageByCodeQuery.uniqueResult();
		} catch (Exception e) {
			throw wrapException("getLanguageByCode " + code, e);
		}
	}

}
