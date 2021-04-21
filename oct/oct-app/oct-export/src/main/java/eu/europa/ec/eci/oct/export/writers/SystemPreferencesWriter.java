package eu.europa.ec.eci.oct.export.writers;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.item.ItemWriter;

import eu.europa.ec.eci.oct.entities.admin.SystemPreferences;
import eu.europa.ec.eci.oct.export.ExportJobRunner;

public class SystemPreferencesWriter implements ItemWriter<SystemPreferences> {

	private final Logger logger = LogManager.getLogger(SystemPreferencesWriter.class);

	// private StepExecution stepExecution;

	@Override
	public void write(List<? extends SystemPreferences> systemPreferences) throws Exception {
		// ExecutionContext stepContext =
		// this.stepExecution.getExecutionContext();
		// stepContext.put("systemPreferences", systemPreferences.get(0));
		ExportJobRunner.systemPreferences = systemPreferences.get(0);
		logger.info(">> obtained systemPreferences: " + ExportJobRunner.systemPreferences);
		
	}

	// @BeforeStep
	// public void saveStepExecution(StepExecution stepExecution) {
	// this.stepExecution = stepExecution;
	// }

}
