package eu.europa.ec.eci.oct.export.utils;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExportParameter {

	private Date startDate;
	private Date endDate;
	private List<String> countries = new ArrayList<String>();

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

	public List<String> getCountries() {
		return countries;
	}

	public void setCountries(List<String> countries) {
		this.countries = countries;
	}


	@Override
	public String toString() {
		return "ExportParametersBean [startDate=" + startDate + ", endDate=" + endDate
				+ ", countries=" + countries + "]";
	}

	public void setCountryCode(String countryCode) {
		this.countries.add(countryCode);
	}

}
