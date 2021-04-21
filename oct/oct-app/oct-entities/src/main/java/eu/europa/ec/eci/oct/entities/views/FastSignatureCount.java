package eu.europa.ec.eci.oct.entities.views;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "FASTSIGNATURECOUNTVIEW")
@Cacheable(false)
public class FastSignatureCount implements Comparable<FastSignatureCount> {

	@Id
	private Long countryId;

	@Column(nullable = false)
	private Long count;

	@Column(nullable = false)
	private Long threshold;

	@Column(nullable = false)
	private String countryCode;

	public Long getCountryId() {
		return countryId;
	}

	public void setCountryId(Long countryId) {
		this.countryId = countryId;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public Long getThreshold() {
		return threshold;
	}

	public void setThreshold(Long threshold) {
		this.threshold = threshold;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	@Override
	public String toString() {
		return "FastSignatureCount [countryId=" + countryId + ", count=" + count + ", threshold=" + threshold + ", countryCode=" + countryCode + "]";
	}

	/* 
	 * should keep the coherence with @link(SignatureCountryCount) comparator
	 */
	@Override
	public int compareTo(FastSignatureCount fsc) {
		long fscCount = fsc.getCount();
		long fscThreshold = fsc.getThreshold();
		double fscPercentage = (fscCount / fscThreshold) * 100;
		double thisPercentage = (count / threshold) * 100;
		if (Double.compare(fscPercentage, thisPercentage) == 0) {
			if (Long.compare(fscCount, count) == 0) {
				if (Long.compare(fscThreshold, threshold) == 0) {
					return fsc.getCountryCode().compareToIgnoreCase(countryCode);
				} else {
					if (Long.compare(fscThreshold, threshold) < 0) {
						return 1;
					} else {
						return -1;
					}
				}
			} else if (Long.compare(fscCount, count) > 0) {
				return 1;
			} else {
				return -1;
			}
		} else if (Double.compare(fscPercentage, thisPercentage) > 0) {
			return 1;
		} else {
			return -1;
		}
	}

}
