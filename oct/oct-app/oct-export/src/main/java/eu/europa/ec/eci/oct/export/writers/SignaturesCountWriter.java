package eu.europa.ec.eci.oct.export.writers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.item.ItemWriter;

import eu.europa.ec.eci.oct.export.ExportJobRunner;
import eu.europa.ec.eci.oct.export.entities.SignatureCountryCount;

public class SignaturesCountWriter implements ItemWriter<SignatureCountryCount> {

	private final Logger logger = LogManager.getLogger(SignaturesCountWriter.class);

	Map<String, Long> signatureCountryCountMap = new HashMap<String, Long>();
	Long totalCount = 0L;

	@Override
	public void write(List<? extends SignatureCountryCount> signatureCountryCountList) throws Exception {
		for (SignatureCountryCount signatureCountryCount : signatureCountryCountList) {
			String countryCode = signatureCountryCount.getCountryCode();
			long countryCount = signatureCountryCount.getCount();
			if (!signatureCountryCountMap.keySet().contains(countryCode)) {
				signatureCountryCountMap.put(countryCode, 0L);
			}
			signatureCountryCountMap.put(countryCode, countryCount);
			totalCount += countryCount;
		}

		// signaturesTotalCount = totalCount;
		ExportJobRunner.setSignatureCountryCountMap(signatureCountryCountMap);
		ExportJobRunner.setTotalCount(totalCount);
		logger.info(">> obtained signatureCountryCountMap: " + signatureCountryCountMap);
		logger.info(">> obtained totalCount: " + ExportJobRunner.totalCount);
	}

}
