package eu.europa.ec.eci.oct.webcommons.services.api.domain.report;

import java.io.Serializable;

import org.springframework.stereotype.Component;

@Component
public class FeedbackStatsDTO implements Serializable{
	
	private static final long serialVersionUID = -4716044069379110751L;

	private long badCount = 0;
	private long fairCount = 0;
	private long fineCount = 0;
	private long goodCount = 0;
	private long totCount = 0;
	
	public long getBadCount() {
		return badCount;
	}
	public void setBadCount(long badCount) {
		this.badCount = badCount;
	}
	public long getFairCount() {
		return fairCount;
	}
	public void setFairCount(long fairCount) {
		this.fairCount = fairCount;
	}
	public long getFineCount() {
		return fineCount;
	}
	public void setFineCount(long fineCount) {
		this.fineCount = fineCount;
	}
	public long getGoodCount() {
		return goodCount;
	}
	public void setGoodCount(long goodCount) {
		this.goodCount = goodCount;
	}
	public long getTotCount() {
		return totCount;
	}
	public void setTotCount(long totCount) {
		this.totCount = totCount;
	}
	@Override
	public String toString() {
		return "FeedbackStatsDTO [badCount=" + badCount + ", fairCount=" + fairCount + ", fineCount=" + fineCount + ", goodCount=" + goodCount + ", totCount=" + totCount + "]";
	}
}
