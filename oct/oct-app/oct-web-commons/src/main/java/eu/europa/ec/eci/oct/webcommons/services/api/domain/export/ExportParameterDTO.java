package eu.europa.ec.eci.oct.webcommons.services.api.domain.export;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class ExportParameterDTO implements Serializable {

	private static final long serialVersionUID = 1960150352136541655L;

	private List<String> countries = new ArrayList<String>();
	private String startDate;
	private String endDate;
	private long signaturesCount;

	public List<String> getCountries() {
		return countries;
	}

	public void setCountries(List<String> countries) {
		this.countries = countries;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public long getSignaturesCount() {
		return signaturesCount;
	}

	public void setSignaturesCount(long signaturesCount) {
		this.signaturesCount = signaturesCount;
	}

	@Override
	public String toString() {
		return "ExportParameterDTO [countries=" + countries + ", startDate=" + startDate + ", endDate=" + endDate
				+ ", signaturesCount=" + signaturesCount + "]";
	}

}
