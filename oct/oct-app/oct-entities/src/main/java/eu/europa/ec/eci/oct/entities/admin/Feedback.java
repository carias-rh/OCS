package eu.europa.ec.eci.oct.entities.admin;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name = "OCT_FEEDBACK")
public class Feedback implements Serializable {

	private static final long serialVersionUID = 8115300735221023302L;
	
	public Feedback(){}

	@GeneratedValue(generator = "IdGenerator", strategy = GenerationType.TABLE)
	@Id
	@TableGenerator(name = "IdGenerator", pkColumnValue = "feedback_id", table = "HIBERNATE_SEQUENCES", allocationSize = 1, pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
	@Column(name = "id")
	private Long id;
	
	@ManyToOne(optional = false)
	private FeedbackRange feedbackRange;

	@Column
	private String feedbackComment;

	@Column(nullable = false)
	private Date feedbackDate;
	
	@Column
	private String signatureIdentifier;

	@ManyToOne()
	public FeedbackRange getFeedbackRange() {
		return feedbackRange;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public String getFeedbackComment() {
		return feedbackComment;
	}

	public void setFeedbackComment(String feedbackComment) {
		this.feedbackComment = feedbackComment;
	}

	public Date getFeedbackDate() {
		return feedbackDate;
	}

	public void setFeedbackDate(Date feedbackDate) {
		this.feedbackDate = feedbackDate;
	}
	
	public String getSignatureIdentifier() {
		return signatureIdentifier;
	}
	
	public void setSignatureIdentifier(String signatureIdentifier) {
		this.signatureIdentifier = signatureIdentifier;
	}

	public void setFeedbackRange(FeedbackRange feedbackRange) {
		this.feedbackRange = feedbackRange;
	}

	@Override
	public String toString() {
		return "Feedback [id=" + id + ", feedbackRange=" + feedbackRange + ", feedbackComment=" + feedbackComment
				+ ", feedbackDate=" + feedbackDate + ", signatureIdentifier=" + signatureIdentifier + "]";
	}

}
