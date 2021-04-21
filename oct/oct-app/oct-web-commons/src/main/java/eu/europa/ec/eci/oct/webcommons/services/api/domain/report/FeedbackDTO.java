package eu.europa.ec.eci.oct.webcommons.services.api.domain.report;

import org.springframework.stereotype.Component;

@Component
public class FeedbackDTO {

	private String range;
	private String comment;
	private String date;
	private String signatureIdentifier;

	public String getRange() {
		return range;
	}

	public void setRange(String feedbackRange) {
		this.range = feedbackRange;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String feedbackComment) {
		this.comment = feedbackComment;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String feedbackDate) {
		this.date = feedbackDate;
	}

	public String getSignatureIdentifier() {
		return signatureIdentifier;
	}

	public void setSignatureIdentifier(String signatureIdentifier) {
		this.signatureIdentifier = signatureIdentifier;
	}

	@Override
	public String toString() {
		return "FeedbackDTO [range=" + range + ", comment=" + comment + ", date=" + date + ", signatureIdentifier="
				+ signatureIdentifier + "]";
	}

}
