package eu.europa.ec.eci.oct.webcommons.services.reporting;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.eci.oct.entities.admin.Feedback;
import eu.europa.ec.eci.oct.entities.admin.FeedbackRange;
import eu.europa.ec.eci.oct.entities.views.EvolutionMapByCountry;
import eu.europa.ec.eci.oct.entities.views.FastSignatureCount;
import eu.europa.ec.eci.oct.utils.CommonsConstants;
import eu.europa.ec.eci.oct.webcommons.services.BaseService;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.report.DistributionMap;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.report.FeedbackStatsDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.report.ProgressionStatus;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.signature.SignatureCountryCount;
import eu.europa.ec.eci.oct.webcommons.services.configuration.ConfigurationService;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTobjectNotFoundException;
import eu.europa.ec.eci.oct.webcommons.services.persistence.PersistenceException;

@Service
@Transactional
public class ReportingServiceImpl extends BaseService implements ReportingService {

	@Override
	@Transactional(readOnly = true)
	public List<EvolutionMapByCountry> getEvolutionMapByCountry() throws OCTException {

		List<EvolutionMapByCountry> evolutionMapByCountry = new ArrayList<EvolutionMapByCountry>();
		try {
			evolutionMapByCountry = reportingDAO.getEvolutionMapByCountry();

		} catch (PersistenceException e) {
			logger.error("persistence problem while fetching getEvolutionMapByCountry", e);
			throw new OCTException("persistence problem while fetching getEvolutionMapByCountry", e);
		}

		return evolutionMapByCountry;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Feedback> getAllFeedbacks() throws OCTException {
		return getAllFeedbacks(0, 0);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Feedback> getAllFeedbacks(int start, int offset) throws OCTException {

		List<Feedback> allFeedbackComments = new ArrayList<Feedback>();
		try {
			List<Feedback> allFeedbacksCommentsDAO = new ArrayList<Feedback>();
			if (offset >= 1) {
				allFeedbacksCommentsDAO = reportingDAO.getAllFeedbacksPaginated(start, offset);
			} else {
				allFeedbacksCommentsDAO = reportingDAO.getAllFeedbacks();
			}
			return allFeedbacksCommentsDAO.isEmpty() ? allFeedbackComments : allFeedbacksCommentsDAO;
		} catch (PersistenceException e) {
			logger.error("persistence problem while fetching all feedbacks", e);
			throw new OCTException("persistence problem while fetching all feedbacks", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<FeedbackRange> getFeedbackRanges() throws OCTException {
		try {
			return reportingDAO.getFeedbackRanges();

		} catch (PersistenceException e) {
			logger.error("persistence problem while fetching all feedbackRanges", e);
			throw new OCTException("persistence problem while fetching all feedbackRanges", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = OCTException.class)
	public void saveFeedback(Feedback feedback, Date feedbackDate) throws OCTException {
		if (feedback == null || feedback.getFeedbackRange() == null) {
			throw new OCTException("One or more required parameters are missing");
		}
		List<FeedbackRange> availableFeedbackRanges = getFeedbackRanges();
		FeedbackRange daoFeedbackRange = null;
		boolean match = false;
		for (FeedbackRange availableFeedbackRange : availableFeedbackRanges) {
			String label = availableFeedbackRange.getLabel();
			if (label.equalsIgnoreCase(feedback.getFeedbackRange().getLabel())) {
				daoFeedbackRange = availableFeedbackRange;
				match = true;
			}
		}
		if (!match) {
			throw new OCTException("Feedback range not matching the available ones");
		}
		feedback.setFeedbackRange(daoFeedbackRange);
		feedback.setFeedbackDate(feedbackDate);
		try {
			reportingDAO.saveFeedback(feedback);
		} catch (PersistenceException e) {
			logger.error("persistence problem while saving feedback", e);
			throw new OCTException("persistence problem while saving feedback", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = OCTException.class)
	public void saveFeedback(Feedback feedback) throws OCTException {
		if (StringUtils.isBlank(feedback.getSignatureIdentifier()) || feedback.getFeedbackRange() == null) {
			throw new OCTException("Invalid feedback");
		}
		String signatureIdentifier = feedback.getSignatureIdentifier();

		// check if the signature UUID exists
		try {
			signatureService.findByUuid(signatureIdentifier);
		} catch (OCTException e) {
			logger.error("Unable to fetch signature by UUID " + signatureIdentifier);
			throw new OCTException(e.getMessage());
		} catch (OCTobjectNotFoundException o) {
			throw new OCTException("Signature not found for UUID " + signatureIdentifier);
		}

		// check if the feedback has been already given for this signature
		boolean isFeedbackAlreadyExistent;
		try {
			reportingService.findByUuid(signatureIdentifier);
			isFeedbackAlreadyExistent = true;
		} catch (OCTobjectNotFoundException o) {
			isFeedbackAlreadyExistent = false;
		} catch (OCTException e) {
			logger.error("Unable to fetch feedback by UUID " + signatureIdentifier);
			throw new OCTException(e.getMessage());
		}

		if (isFeedbackAlreadyExistent) {
			throw new OCTException("Feedback already present for UUID " + signatureIdentifier);
		}

		try {
			saveFeedback(feedback, new Date());
		} catch (OCTException o) {
			logger.error("Error while persisting feedback " + feedback + o.getMessage());
			throw new OCTException("Error while persisting feedback " + o.getMessage());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public FeedbackRange getFeedbackRangeByLabel(String label) throws OCTException {
		FeedbackRange feedbackRangeDAO = null;
		try {
			feedbackRangeDAO = reportingDAO.getFeedbackRangeByLabel(label);

		} catch (PersistenceException e) {
			logger.error("persistence problem while get FeedbackRange by label", e);
			throw new OCTException("persistence problem while get FeedbackRange by label", e);
		}

		return feedbackRangeDAO;
	}

	@Override
	@Transactional(readOnly = true)
	public DistributionMap getDistributionMap() throws OCTException {
		int allCountriesSize = systemManager.getAllCountries().size();
		return getDistributionMap(allCountriesSize);
	}

	@Override
	@Transactional(readOnly = true)
	public DistributionMap getTop7DistributionMap() throws OCTException {
		return getDistributionMap(CommonsConstants.TOP_COUNTRIES_CHART_SIZE);
	}

	private DistributionMap getDistributionMap(int requiredSize) throws OCTException {
		DistributionMap distributionMap = new DistributionMap();
		List<FastSignatureCount> allFastSignatureCounts = signatureService.getFastSignatureCounts(requiredSize);
		List<SignatureCountryCount> signatureCountryCountList = signatureTransformer
				.fromFSCtoSignatureCount(allFastSignatureCounts);
		distributionMap.setSignatureCountryCount(signatureCountryCountList);
		return distributionMap;
	}

	@Override
	@Transactional(readOnly = true)
	public ProgressionStatus getProgressionStatus() throws OCTException {
		long total = signatureService.getFastSignatureCountTotal();

		long signatureGoal = Long.valueOf(configurationService
				.getConfigurationParameter(ConfigurationService.Parameter.SIGNATURE_GOAL).getValue());

		ProgressionStatus progressionStatus = new ProgressionStatus();
		progressionStatus.setGoal(signatureGoal);
		progressionStatus.setSignatureCount(total);
		return progressionStatus;
	}

	@Override
	@Transactional(readOnly = true)
	public FeedbackStatsDTO getFeedbackStats() throws OCTException {
		FeedbackStatsDTO feedbackStatsDTO = new FeedbackStatsDTO();

		try {

			long badCount = reportingDAO.countFeedbackRanges(FeedbackRange.BAD);
			feedbackStatsDTO.setBadCount(badCount);

			long fairCount = reportingDAO.countFeedbackRanges(FeedbackRange.FAIR);
			feedbackStatsDTO.setFairCount(fairCount);

			long fineCount = reportingDAO.countFeedbackRanges(FeedbackRange.FINE);
			feedbackStatsDTO.setFineCount(fineCount);

			long goodCount = reportingDAO.countFeedbackRanges(FeedbackRange.GOOD);
			feedbackStatsDTO.setGoodCount(goodCount);

			feedbackStatsDTO.setTotCount(badCount + fairCount + fineCount + goodCount);

		} catch (PersistenceException e) {
			logger.error("persistence problem while countFeedbackRanges", e);
			throw new OCTException("persistence problem while countFeedbackRanges", e);
		}

		return feedbackStatsDTO;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = OCTException.class)
	public void deleteAllFeedbacks() throws OCTException {
		try {
			reportingDAO.deleteAllFeedbacks();
		} catch (PersistenceException e) {
			logger.error("persistence problem while deleting all the feedbacks", e);
			throw new OCTException("persistence problem while deleting all the feedbacks", e);
		}

	}

	@Override
	@Transactional(readOnly = true)
	public Feedback findByUuid(String signatureIdentifier) throws OCTobjectNotFoundException, OCTException {
		Feedback feedback = null;
		try {
			feedback = reportingDAO.findByUuid(signatureIdentifier);
		} catch (OCTobjectNotFoundException e) {
			logger.warn("No feedback found for UUID: " + signatureIdentifier + ": " + e.getMessage());
			throw new OCTobjectNotFoundException(
					"No feedback found for UUID: " + signatureIdentifier + ": " + e.getMessage());
		} catch (PersistenceException pe) {
			logger.warn("Error fetching feedback for UUID" + signatureIdentifier + ": " + pe.getMessage());
			throw new OCTException("Error fetching feedback for UUID: " + signatureIdentifier + ": " + pe.getMessage());
		}
		return feedback;
	}

}
