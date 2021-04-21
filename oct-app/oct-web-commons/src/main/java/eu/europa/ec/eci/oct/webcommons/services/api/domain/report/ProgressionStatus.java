package eu.europa.ec.eci.oct.webcommons.services.api.domain.report;

import java.io.Serializable;

import org.springframework.stereotype.Component;

@Component
public class ProgressionStatus implements Serializable{
	
	private static final long serialVersionUID = -8593817855308335840L;

	public ProgressionStatus(){}

	public long signatureCount;
	public long goal;

	public long getSignatureCount() {
		return signatureCount;
	}

	public void setSignatureCount(long signatureCount) {
		this.signatureCount = signatureCount;
	}

	public long getGoal() {
		return goal;
	}

	public void setGoal(long goal) {
		this.goal = goal;
	}

	@Override
	public String toString() {
		return "Progression [signatureCount=" + signatureCount + ", goal=" + goal + "]";
	}

}
