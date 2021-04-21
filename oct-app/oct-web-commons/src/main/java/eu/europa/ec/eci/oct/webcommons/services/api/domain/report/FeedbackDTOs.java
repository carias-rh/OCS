package eu.europa.ec.eci.oct.webcommons.services.api.domain.report;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class FeedbackDTOs implements Serializable{
	
	private static final long serialVersionUID = 2278364434142760714L;
	
	private List<FeedbackDTO> feedbackDTOlist = new ArrayList<FeedbackDTO>();

	public List<FeedbackDTO> getFeedbackDTOlist() {
		return feedbackDTOlist;
	}
	public void setFeedbackDTOlist(List<FeedbackDTO> feedbackDTOlist) {
		this.feedbackDTOlist = feedbackDTOlist;
	}
	
	@Override
	public String toString() {
		return "FeedbackDTOs [feedbackDTOlist=" + feedbackDTOlist + "]";
	}


}
