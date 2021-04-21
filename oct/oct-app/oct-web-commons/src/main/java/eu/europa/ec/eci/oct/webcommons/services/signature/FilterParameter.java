package eu.europa.ec.eci.oct.webcommons.services.signature;

import java.util.Date;

public class FilterParameter {

	private Date startDate;
	private Date endDate;
	private String countryCode;
	private long descriptionId;
	private long descriptionLanguageId;
	private long countryId;
	private int start = 0;
	private int offset = 0;
	
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public long getDescriptionLanguageId() {
		return descriptionLanguageId;
	}
	public void setDescriptionLanguageId(long descriptionLanguageId) {
		this.descriptionLanguageId = descriptionLanguageId;
	}
	public long getCountryId() {
		return countryId;
	}
	public void setCountryId(long countryId) {
		this.countryId = countryId;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	public long getDescriptionId() {
		return descriptionId;
	}
	public void setDescriptionId(long descriptionId) {
		this.descriptionId = descriptionId;
	}
	@Override
	public String toString() {
		return "FilterParameter [startDate=" + startDate + ", endDate=" + endDate + ", countryCode=" + countryCode + ", descriptionId=" + descriptionId
				+ ", descriptionLanguageId=" + descriptionLanguageId + ", countryId=" + countryId + ", start=" + start + ", offset=" + offset + "]";
	}

	
}
