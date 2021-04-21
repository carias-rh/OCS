package eu.europa.ec.eci.oct.webcommons.services.api.domain.export;

import java.io.Serializable;

public class ExportSignaturesCountDetailDTO implements Serializable {

	private static final long serialVersionUID = 1827495129779610600L;

	private long count;
	private String countryCode;
	private String lastExportationDate;

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getLastExportationDate() {
		return lastExportationDate;
	}

	public void setLastExportationDate(String lastExportationDate) {
		this.lastExportationDate = lastExportationDate;
	}

	@Override
	public String toString() {
		return "ExportSignaturesCountDetailDTO [count=" + count + ", countryCode=" + countryCode
				+ ", lastExportationDate=" + lastExportationDate + "]";
	}

}
