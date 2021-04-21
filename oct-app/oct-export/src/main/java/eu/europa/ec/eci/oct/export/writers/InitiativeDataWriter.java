package eu.europa.ec.eci.oct.export.writers;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.item.ItemWriter;

import eu.europa.ec.eci.oct.export.ExportJobRunner;
import eu.europa.ec.eci.oct.export.entities.InitiativeData;

public class InitiativeDataWriter implements ItemWriter<InitiativeData> {
	
	private final Logger logger = LogManager.getLogger(InitiativeDataWriter.class);


//	private StepExecution stepExecution;

	@Override
	public void write(List<? extends InitiativeData> initiativeData) throws Exception {
//		ExecutionContext stepContext = this.stepExecution.getExecutionContext();
//		stepContext.put("initiativeData", initiativeData.get(0));
		ExportJobRunner.initiativeData = initiativeData.get(0);
		logger.info(">> obtained initiativeData: " + ExportJobRunner.initiativeData);
	}

//	@BeforeStep
//	public void saveStepExecution(StepExecution stepExecution) {
//		this.stepExecution = stepExecution;
//	}

}
