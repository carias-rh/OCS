package eu.europa.ec.eci.oct.webcommons.services.persistence.jpa;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.eci.oct.entities.admin.SocialMedia;
import eu.europa.ec.eci.oct.entities.admin.SocialMediaMessage;
import eu.europa.ec.eci.oct.webcommons.services.persistence.PersistenceException;
import eu.europa.ec.eci.oct.webcommons.services.persistence.SocialMediaDAO;

@Repository
@Transactional
public class JpaSocialMediaDAO extends AbstractJpaDAO implements SocialMediaDAO {

	@Override
	@Transactional(readOnly = true)
	public SocialMediaMessage getSocialMediaMessage(String socialMediaName, String languageCode)
			throws PersistenceException {
		try {
			logger.debug("querying social media message object for media " + socialMediaName + " and language "
					+ languageCode.toLowerCase());

			return (SocialMediaMessage) this.sessionFactory.getCurrentSession().createQuery(
					"FROM SocialMediaMessage WHERE socialMedia.name = :socialMediaName AND language.code = :languageCode")
					.setParameter("socialMediaName", socialMediaName.toLowerCase())
					.setParameter("languageCode", languageCode.toLowerCase()).uniqueResult();
		} catch (Exception e) {
			throw wrapException("getSocialMediaMessage " + socialMediaName + "/" + languageCode, e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public SocialMedia getSocialMediaByName(String socialMediaName) throws PersistenceException {
		try {
			logger.debug("querying social media type for media name  " + socialMediaName);

			SocialMedia sm = (SocialMedia) this.sessionFactory.getCurrentSession()
					.createQuery("FROM SocialMedia WHERE name = :name")
					.setParameter("name", socialMediaName.toLowerCase()).uniqueResult();
			return sm;
		} catch (Exception e) {
			throw wrapException("getSocialMediaByName " + socialMediaName, e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public SocialMedia getSocialMediaById(long socialMediaId) throws PersistenceException {
		try {
			logger.debug("querying social media type for media Id  " + socialMediaId);

			SocialMedia sm = (SocialMedia) this.sessionFactory.getCurrentSession()
					.createQuery("FROM SocialMedia WHERE id = :id").setParameter("id", socialMediaId).uniqueResult();
			return sm;
		} catch (Exception e) {
			throw wrapException("getSocialMediaById " + socialMediaId, e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public long getSocialMediaIdByName(String socialMediaName) throws PersistenceException {
		try {
			logger.debug("querying social media id by name  " + socialMediaName);

			long sm = (Long) this.sessionFactory.getCurrentSession()
					.createQuery("SELECT id FROM SocialMedia WHERE name = :name")
					.setParameter("name", socialMediaName.toLowerCase()).uniqueResult();
			return sm;
		} catch (Exception e) {
			throw wrapException("getSocialMediaIdByName " + socialMediaName, e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = PersistenceException.class)
	public void persistSocialMediaMessage(SocialMediaMessage socialMediaMessage) throws PersistenceException {
		try {
			this.sessionFactory.getCurrentSession().persist(socialMediaMessage);
			this.sessionFactory.getCurrentSession().flush();
		} catch (Exception e) {
			throw wrapException("persistedSocialMediaMessage " + socialMediaMessage, e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = PersistenceException.class)
	public void updateSocialMediaMessage(SocialMediaMessage socialMediaMessage) throws PersistenceException {
		try {
			this.sessionFactory.getCurrentSession().update(socialMediaMessage);
			this.sessionFactory.getCurrentSession().flush();
		} catch (Exception e) {
			throw wrapException("persistedSocialMediaMessage " + socialMediaMessage, e);
		}
	}

}
