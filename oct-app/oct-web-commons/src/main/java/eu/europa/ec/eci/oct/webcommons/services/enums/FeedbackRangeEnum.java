package eu.europa.ec.eci.oct.webcommons.services.enums;

public enum FeedbackRangeEnum {

	//@formatter:off
	HORRIBLE	(1,   "oct.feedback.horrible",	1,  1),    
	BAD			(2,   "oct.feedback.bad",    	2,  1),    
	FAIR		(3,   "oct.feedback.fair",    	3,  1),    
	FINE		(4,   "oct.feedback.fine",    	4,  1),    
	GOOD		(5,   "oct.feedback.good",    	5,  1),    
	GREAT		(6,   "oct.feedback.great",    	6,  1);
   
	//@formatter:on

	private long id;
	private String label;
	private int displayOrder;
	private int enabled;

	FeedbackRangeEnum(long id, String label, int displayOrder, int enabled) {
		this.id = id;
		this.label = label;
		this.displayOrder = displayOrder;
		this.enabled = enabled;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}

	public int getEnabled() {
		return enabled;
	}

	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}
	
	
}
