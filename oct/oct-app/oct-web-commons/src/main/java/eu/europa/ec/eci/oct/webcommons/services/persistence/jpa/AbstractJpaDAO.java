package eu.europa.ec.eci.oct.webcommons.services.persistence.jpa;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import eu.europa.ec.eci.oct.webcommons.services.persistence.PersistenceException;

/**
 * Parent class for all JPA-based DAO implementations.
 * 
 * @author keschma
 *
 */
public abstract class AbstractJpaDAO {
	
	protected Logger logger = LogManager.getLogger(AbstractJpaDAO.class);
		
	/**
	 * Logs an error and produces a persistence layer exception
	 * by wrapping the given reason.
	 * @param errorDetails some informative details about this error
	 * @param e the exception causing this error
	 * @return a suitable instance of PersistenceException
	 */
	protected PersistenceException wrapException(String errorDetails, Exception e) {
		final String errorMessage = "Error: " + errorDetails;
		logger.error(errorMessage, e);
		return new PersistenceException(errorMessage, e);
	}
	
	@Autowired
	protected SessionFactory sessionFactory;
	
}
