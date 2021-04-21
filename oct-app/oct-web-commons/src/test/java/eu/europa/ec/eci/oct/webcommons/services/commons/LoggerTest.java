package eu.europa.ec.eci.oct.webcommons.services.commons;

import org.junit.Test;

public class LoggerTest extends ServicesTest {

	@Test
	public void logLevelTests() throws Exception {
		logger.trace("TRACE log level active");
		logger.error("ERROR log level active");
		logger.warn("WARN log level active");
		logger.debug("DEBUG log level active");
		logger.info("INFO log level active");
	}

}
