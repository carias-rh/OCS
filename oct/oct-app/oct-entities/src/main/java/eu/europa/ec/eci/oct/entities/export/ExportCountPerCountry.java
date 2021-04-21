package eu.europa.ec.eci.oct.entities.export;

import java.io.Serializable;

public class ExportCountPerCountry implements Serializable{
	
	public ExportCountPerCountry(){}
	
	private static final long serialVersionUID = 757181726254989897L;
	
	private String countryCode;
	private long count;
	
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	
	@Override
	public String toString() {
		return "ExportCountPerCountry [countryCode=" + countryCode + ", count=" + count + "]";
	}
	
	

}
