package eu.europa.ec.eci.oct.export.writers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.item.ItemWriter;

import eu.europa.ec.eci.oct.entities.system.Country;
import eu.europa.ec.eci.oct.export.ExportJobRunner;

public class CountryWriter implements ItemWriter<Country> {
	
	private final Logger logger = LogManager.getLogger(CountryWriter.class);

	// private StepExecution stepExecution;

	@Override
	public void write(List<? extends Country> countries) throws Exception {
		
		Map<Long, String> countryIdCodeMap = new HashMap<Long, String>();
		for(Country c : countries){
			countryIdCodeMap.put(c.getId(), c.getCode());
		}
		ExportJobRunner.countryIdCodeMap = countryIdCodeMap;
		logger.info(">> obtained countries: " + ExportJobRunner.countryIdCodeMap);
		
		// clean step context
		countries = null;
	}

	// @BeforeStep
	// public void saveStepExecution(StepExecution stepExecution) {
	// this.stepExecution = stepExecution;
	// }

}
