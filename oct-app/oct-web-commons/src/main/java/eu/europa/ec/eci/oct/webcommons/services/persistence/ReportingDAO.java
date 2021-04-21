package eu.europa.ec.eci.oct.webcommons.services.persistence;

import java.util.List;

import eu.europa.ec.eci.oct.entities.admin.Feedback;
import eu.europa.ec.eci.oct.entities.admin.FeedbackRange;
import eu.europa.ec.eci.oct.entities.views.EvolutionMapByCountry;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTobjectNotFoundException;

public interface ReportingDAO {

	List<EvolutionMapByCountry> getEvolutionMapByCountry() throws PersistenceException;

	List<FeedbackRange> getFeedbackRanges() throws PersistenceException;

	List<Feedback> getAllFeedbacks() throws PersistenceException;

	List<Feedback> getAllFeedbacksPaginated(int start, int offset) throws PersistenceException;

	void saveFeedback(Feedback feedback) throws PersistenceException;

	FeedbackRange getFeedbackRangeByLabel(String label) throws PersistenceException;

	long countFeedbackRanges(long feedbackRangeId) throws PersistenceException;

	long countAllFeedbacks() throws PersistenceException;

	void deleteAllFeedbacks() throws PersistenceException;

	Feedback findByUuid(String signatureIdentifier) throws PersistenceException, OCTobjectNotFoundException;

}
