package eu.europa.ec.eci.oct.webcommons.services.api.domain.signature;

import java.io.Serializable;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * User: franzmh Date: 28/10/16
 */

@Component
@Scope("prototype")
public class SignatureCountryCount implements Serializable, Comparable<SignatureCountryCount> {

	private static final long serialVersionUID = 5672969523957520659L;

	public SignatureCountryCount() {
	}

	public long count;
	public long treshold;
	public double percentage;
	public String countryCode;

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public long getTreshold() {
		return treshold;
	}

	public void setTreshold(long treshold) {
		this.treshold = treshold;
	}

	public double getPercentage() {
		return percentage;
	}

	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	/* 
	 * this must be coherent with @link(FastSignatureCount) comparator
	 */
	@Override
	public int compareTo(SignatureCountryCount scc) {
		if (Double.compare(scc.getPercentage(), percentage) == 0) {
			if (Long.compare(scc.getCount(), count) == 0) {
				if (Long.compare(scc.getTreshold(), treshold) == 0) {
					return scc.getCountryCode().compareToIgnoreCase(countryCode);
				} else {
					if (Long.compare(scc.getTreshold(), treshold) < 0) {
						return 1;
					} else {
						return -1;
					}
				}
			} else if (Long.compare(scc.getCount(), count) > 0) {
				return 1;
			} else {
				return -1;
			}
		} else if (Double.compare(scc.getPercentage(), percentage) > 0) {
			return 1;
		} else {
			return -1;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SignatureCountryCount other = (SignatureCountryCount) obj;
		if (count != other.count)
			return false;
		if (countryCode == null) {
			if (other.countryCode != null)
				return false;
		} else if (!countryCode.equals(other.countryCode))
			return false;
		if (Double.doubleToLongBits(percentage) != Double.doubleToLongBits(other.percentage))
			return false;
		if (treshold != other.treshold)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SignatureCountryCount [count=" + count + ", treshold=" + treshold + ", percentage=" + percentage + ", countryCode=" + countryCode + "]";
	}

}
