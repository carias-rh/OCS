package eu.europa.ec.eci.oct.export.entities;

public class SignatureCountryCount {

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
		return "SignatureCountryCount [countryCode=" + countryCode + ", count=" + count + "]";
	}

}
