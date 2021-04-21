package eu.europa.ec.eci.oct.webcommons.services.reporting;

import java.util.Date;
import java.util.List;

import eu.europa.ec.eci.oct.entities.admin.Feedback;
import eu.europa.ec.eci.oct.entities.admin.FeedbackRange;
import eu.europa.ec.eci.oct.entities.views.EvolutionMapByCountry;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.report.DistributionMap;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.report.FeedbackStatsDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.report.ProgressionStatus;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTobjectNotFoundException;

public interface ReportingService {
	List<EvolutionMapByCountry> getEvolutionMapByCountry() throws OCTException;

	List<Feedback> getAllFeedbacks() throws OCTException;

	List<Feedback> getAllFeedbacks(int start, int offset) throws OCTException;

	List<FeedbackRange> getFeedbackRanges() throws OCTException;

	void saveFeedback(Feedback feedback) throws OCTException;

	void saveFeedback(Feedback feedback, Date feedbackDate) throws OCTException;

	FeedbackRange getFeedbackRangeByLabel(String string) throws OCTException;

	DistributionMap getDistributionMap() throws OCTException;

	DistributionMap getTop7DistributionMap() throws OCTException;

	ProgressionStatus getProgressionStatus() throws OCTException;

	FeedbackStatsDTO getFeedbackStats() throws OCTException;

	void deleteAllFeedbacks() throws OCTException;

	Feedback findByUuid(String signatureIdentifier) throws OCTException, OCTobjectNotFoundException;
}
