package eu.europa.ec.eci.oct.webcommons.services.persistence.jpa;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.eci.oct.entities.admin.Feedback;
import eu.europa.ec.eci.oct.entities.admin.FeedbackRange;
import eu.europa.ec.eci.oct.entities.views.EvolutionMapByCountry;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTobjectNotFoundException;
import eu.europa.ec.eci.oct.webcommons.services.persistence.PersistenceException;
import eu.europa.ec.eci.oct.webcommons.services.persistence.ReportingDAO;

@Repository
@Transactional
@SuppressWarnings("unchecked")
public class JpaReportingDAO extends AbstractJpaDAO implements ReportingDAO {

	@Override
	@Transactional(readOnly = true)
	public List<EvolutionMapByCountry> getEvolutionMapByCountry() throws PersistenceException {
		try {
			logger.debug("querying all getEvolutionMapByCountry");
			List<EvolutionMapByCountry> evolutionMapList = this.sessionFactory.getCurrentSession()
					.createQuery("FROM EvolutionMapByCountry").list();
			return evolutionMapList;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw wrapException("getEvolutionMapByCountry", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<FeedbackRange> getFeedbackRanges() throws PersistenceException {
		try {
			logger.debug("querying all feedback ranges");

			List<FeedbackRange> feedbackRangeList = this.sessionFactory.getCurrentSession()
					.createQuery("FROM FeedbackRange fr ORDER BY fr.displayOrder").list();
			return feedbackRangeList;

		} catch (Exception e) {
			throw wrapException("getFeedbackRanges", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public long countAllFeedbacks() throws PersistenceException {
		try {
			logger.debug("countAllFeedbacks");

			long countAllFeedbacks = (Long) this.sessionFactory.getCurrentSession()
					.createQuery("SELECT COUNT(f) FROM Feedback f").uniqueResult();
			return countAllFeedbacks;
		} catch (Exception e) {
			throw wrapException("countAllFeedbacks", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public long countFeedbackRanges(long feedbackRangeId) throws PersistenceException {
		try {
			logger.debug("countFeedbackRanges");

			long countFeedbackRanges = (Long) this.sessionFactory.getCurrentSession()
					.createQuery("SELECT COUNT(f) FROM Feedback f WHERE f.feedbackRange.id = :feedbackRangeId")
					.setParameter("feedbackRangeId", feedbackRangeId).uniqueResult();
			return countFeedbackRanges;
		} catch (Exception e) {
			throw wrapException("countFeedbackRanges", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<Feedback> getAllFeedbacks() throws PersistenceException {
		try {
			logger.debug("querying all feedback");

			List<Feedback> feedbacks = this.sessionFactory.getCurrentSession()
					.createQuery("FROM Feedback f ORDER BY f.feedbackDate DESC").list();
			return feedbacks;
		} catch (Exception e) {
			throw wrapException("getAllFeedbacks", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<Feedback> getAllFeedbacksPaginated(int start, int offset) throws PersistenceException {
		try {
			logger.debug("querying all feedback with pagination " + start + "/" + offset);

			List<Feedback> feedbacks = this.sessionFactory.getCurrentSession()
					.createQuery("FROM Feedback f ORDER BY f.feedbackDate DESC")
					.setFirstResult(start)
					.setMaxResults(offset)
					.list();
			return feedbacks;
		} catch (Exception e) {
			throw wrapException("getAllFeedbacks", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = PersistenceException.class)
	public void saveFeedback(Feedback feedback) throws PersistenceException {
		try {
			this.sessionFactory.getCurrentSession().save(feedback);
			this.sessionFactory.getCurrentSession().flush();
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			throw wrapException("saveFeedback " + feedback, e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public FeedbackRange getFeedbackRangeByLabel(String label) throws PersistenceException {
		logger.debug("querying feedback range for label " + label);
		try {
			FeedbackRange feedbackRangeByLabel = (FeedbackRange) this.sessionFactory.getCurrentSession()
					.createQuery("FROM FeedbackRange fr WHERE fr.label = :label").setParameter("label", label)
					.uniqueResult();
			return feedbackRangeByLabel;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw wrapException("getFeedbackRangeByLabel for " + label, e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = PersistenceException.class)
	public void deleteAllFeedbacks() throws PersistenceException {
		logger.debug("deleting all feedbacks...");
		try {
			this.sessionFactory.getCurrentSession().createSQLQuery("DELETE FROM OCT_FEEDBACK").executeUpdate();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw wrapException("deleteAllFeedbacks", e);
		}

	}

	@Override
	@Transactional(readOnly = true)
	public Feedback findByUuid(String signatureIdentifier) throws PersistenceException, OCTobjectNotFoundException {
		try {
			Query getFeedbackByUuidQuery = this.sessionFactory.getCurrentSession()
					.createQuery("FROM Feedback f WHERE f.signatureIdentifier = :signatureIdentifier")
					.setParameter("signatureIdentifier", signatureIdentifier);
			Feedback feedbackBySignatureIdentifier = (Feedback) getFeedbackByUuidQuery.uniqueResult();
			if (feedbackBySignatureIdentifier == null) {
				throw new OCTobjectNotFoundException();
			}
			return feedbackBySignatureIdentifier;
		} catch (HibernateException e) {
			throw wrapException("signatureIdentifier " + signatureIdentifier, e);
		}
	}

}
