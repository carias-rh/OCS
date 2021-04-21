package eu.europa.ec.eci.oct.webcommons.services.api.transformer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import eu.europa.ec.eci.oct.entities.admin.Feedback;
import eu.europa.ec.eci.oct.entities.admin.FeedbackRange;
import eu.europa.ec.eci.oct.utils.DateUtils;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.report.FeedbackDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.report.FeedbackDTOs;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;

@Component
public class FeedbackTransformer extends BaseTransformer {

	public static final String FEEDBACK_RANGE_LABEL_PREFIX = "oct.feedback.";

	public List<String> transformFeedbackRangeDAOlist(List<FeedbackRange> feedbackRangeListDAO) {
		if (feedbackRangeListDAO == null || feedbackRangeListDAO.isEmpty()) {
			return null;
		}
		List<String> feedbackRangeDTOlist = new ArrayList<String>();
		for (FeedbackRange feedbackRangeDAO : feedbackRangeListDAO) {
			String feedbackRangeLabel = feedbackRangeDAO.getLabel().replace(FEEDBACK_RANGE_LABEL_PREFIX, "");
			feedbackRangeDTOlist.add(feedbackRangeLabel);
		}
		return feedbackRangeDTOlist;
	}

	public FeedbackRange feedbackRangefromDTOtoDAO(String feedbackRangeLabel) throws OCTException {
		String label = FEEDBACK_RANGE_LABEL_PREFIX + feedbackRangeLabel.toLowerCase();
		FeedbackRange feedbackRange = reportingService.getFeedbackRangeByLabel(label);
		return feedbackRange;
	}

	public FeedbackDTO feedbackFromDAOtoDTO(Feedback feedbackDAO) {
		if (feedbackDAO == null) {
			return null;
		}
		FeedbackDTO feedbackDTO = new FeedbackDTO();
		if (StringUtils.isNotBlank(feedbackDAO.getFeedbackComment())) {
			feedbackDTO.setComment(feedbackDAO.getFeedbackComment());
		}
		feedbackDTO.setDate(DateUtils.formatDate(feedbackDAO.getFeedbackDate(), "dd/MM/yyyy"));
		feedbackDTO.setRange(feedbackDAO.getFeedbackRange().getLabel().replace(FEEDBACK_RANGE_LABEL_PREFIX, ""));
		return feedbackDTO;
	}

	public Feedback feedbackFromDTOtoDAO(FeedbackDTO feedbackDTO) throws OCTException {
		if (feedbackDTO == null) {
			return null;
		}
		Feedback feedbackDAO = new Feedback();
		String comment = "";
		if (feedbackDTO.getComment() != null && !feedbackDTO.getComment().equals("")) {
			comment = feedbackDTO.getComment();
		}
		feedbackDAO.setFeedbackComment(comment);
		FeedbackRange feedbackRangeDAO = feedbackRangefromDTOtoDAO(feedbackDTO.getRange());
		feedbackDAO.setFeedbackRange(feedbackRangeDAO);
		feedbackDAO.setFeedbackDate(new Date());
		feedbackDAO.setSignatureIdentifier(feedbackDTO.getSignatureIdentifier());
		return feedbackDAO;
	}

	public FeedbackDTOs transformFeedbackDAOlist(List<Feedback> feedbackDAOlist) {
		FeedbackDTOs feedbackDTOs = new FeedbackDTOs();
		List<FeedbackDTO> feedbackDTOlist = new ArrayList<FeedbackDTO>();
		for (Feedback feedbackDAO : feedbackDAOlist) {
			FeedbackDTO feedbackDTO = feedbackFromDAOtoDTO(feedbackDAO);
			feedbackDTOlist.add(feedbackDTO);
		}
		if (!feedbackDTOlist.isEmpty()) {
			feedbackDTOs.setFeedbackDTOlist(feedbackDTOlist);
		}
		return feedbackDTOs;
	}

}
