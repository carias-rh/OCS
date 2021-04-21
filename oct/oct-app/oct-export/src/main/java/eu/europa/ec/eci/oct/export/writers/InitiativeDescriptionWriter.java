package eu.europa.ec.eci.oct.export.writers;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.item.ItemWriter;

import eu.europa.ec.eci.oct.entities.admin.InitiativeDescription;
import eu.europa.ec.eci.oct.export.ExportJobRunner;

public class InitiativeDescriptionWriter implements ItemWriter<InitiativeDescription> {

	private final Logger logger = LogManager.getLogger(InitiativeDescriptionWriter.class);

	@Override
	public void write(List<? extends InitiativeDescription> descriptions) throws Exception {
		ExportJobRunner.setDefaultDescription(descriptions.get(0));
		logger.info(">> obtained descriptions: " + descriptions);
		
	}

}
