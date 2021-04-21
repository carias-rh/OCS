package eu.europa.ec.eci.oct.export.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.eci.oct.entities.export.ExportHistory;
import eu.europa.ec.eci.oct.export.ExportJobRunner;
import eu.europa.ec.eci.oct.export.utils.ExportParameter;
import eu.europa.ec.eci.oct.utils.CommonsConstants;
import eu.europa.ec.eci.oct.utils.DateUtils;

@Transactional
@Repository
@SuppressWarnings("unchecked")
public class ExportHistoryPersistenceHibernate implements ExportHistoryPersistenceDAO {

	private final Logger logger = LogManager.getLogger(ExportJobRunner.class);

	@Autowired
	protected SessionFactory sessionFactory;

	@Override
	@Transactional(readOnly = true)
	public ExportHistory getLastExportHistory() throws Exception {
		logger.debug("getLastExportHistory...");
		ExportHistory lastExportHistory = null;
		try {
			List<ExportHistory> exportHistoryList = (List<ExportHistory>) this.sessionFactory.getCurrentSession().createQuery("FROM ExportHistory ORDER BY id DESC").list();
			if (!exportHistoryList.isEmpty()) {
				lastExportHistory = exportHistoryList.get(0);
			}
		} catch (HibernateException he) {
			throw new Exception("Error in getLastExportHistory: " + he.getMessage());
		}
		logger.debug("returning lastExportHistory: " + lastExportHistory);
		return lastExportHistory;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = Exception.class)
	public void updateExportHistory(ExportHistory exportHistory) throws Exception {
		logger.debug("updateExportHistory: " + exportHistory);

		String errorMessage = exportHistory.getErrorMessage();
		if (errorMessage != null && !errorMessage.isEmpty()) {
			if (errorMessage.length() > 2000) {
				String reducedErrorMessage = errorMessage.substring(0, 1996) + "...";
				exportHistory.setErrorMessage(reducedErrorMessage);
			}
		}
		try {
			this.sessionFactory.getCurrentSession().update(exportHistory);
			this.sessionFactory.getCurrentSession().flush();
		} catch (HibernateException he) {
			throw new Exception("Error in saveOrUpdateExportHistory: " + he.getMessage());
		}
		logger.debug("updated: " + exportHistory);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = Exception.class)
	public String initExportHistory(ExportParameter exportParameter) throws Exception {
		logger.debug("initExportHistory: " + exportParameter);
		ExportHistory exportHistory = new ExportHistory();
		exportHistory.setExportDate(new Date());
		exportHistory.setBatchStatus(BatchStatus.STARTED.name());
		List<String> countries = exportParameter.getCountries();
		String countryParam = "";
		if (countries.isEmpty() || countries.size() == CommonsConstants.TOTAL_COUNTRIES_NUMBER) {
			countryParam = "*";
		} else {
			for (String countryCode : countries) {
				countryParam += countryCode.toUpperCase() + " ";
			}
		}
		exportHistory.setCountriesParam(countryParam);

		if (exportParameter.getStartDate() != null) {
			exportHistory.setStartDateParam(DateUtils.formatDate(exportParameter.getStartDate()));
		}
		if (exportParameter.getEndDate() != null) {
			exportHistory.setEndDateParam(DateUtils.formatDate(exportParameter.getEndDate()));
		}

		String uuid = UUID.randomUUID().toString();
		exportHistory.setJobId(uuid);
		try {
			this.sessionFactory.getCurrentSession().save(exportHistory);
			this.sessionFactory.getCurrentSession().flush();
		} catch (HibernateException he) {
			throw new Exception("Error in initExportHistory: " + he.getMessage());
		}
		logger.debug("initExportHistory done: " + exportHistory);
		return uuid;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ExportHistory> getAllExportHistory() throws Exception {
		logger.debug("getAllExportHistory...");
		List<ExportHistory> exportHistoryList = new ArrayList<ExportHistory>();
		try {
			exportHistoryList = this.sessionFactory.getCurrentSession().createQuery("FROM ExportHistory ORDER BY id DESC").list();
		} catch (HibernateException he) {
			throw new Exception("Error in getLastExportHistory: " + he.getMessage());
		}
		logger.debug("returning all export history: " + exportHistoryList);
		return exportHistoryList;
	}

	@Override
	@Transactional(readOnly = true)
	public ExportHistory getExportHistoryByUUID(String jobUUID) throws Exception {
		logger.debug("getExportHistoryByUUID[" + jobUUID + "]");
		ExportHistory exportHistoryByUUID = null;
		try {
			exportHistoryByUUID = (ExportHistory) this.sessionFactory.getCurrentSession().createQuery("FROM ExportHistory WHERE jobId = :jobUUID").setParameter("jobUUID", jobUUID).uniqueResult();
		} catch (HibernateException he) {
			throw new Exception("Error in getExportHistoryByUUID[" + jobUUID + "]:  " + he.getMessage());
		}

		logger.debug("returning exportHistoryByUUID: " + exportHistoryByUUID);
		return exportHistoryByUUID;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = Exception.class)
	public void removeExportHistory(String jobUUID) throws Exception {
		logger.debug("removeExportHistory[" + jobUUID + "]");
		try {
			ExportHistory toBeDeleted = getExportHistoryByUUID(jobUUID);
			if (toBeDeleted != null) {
				this.sessionFactory.getCurrentSession().delete(toBeDeleted);
				this.sessionFactory.getCurrentSession().flush();
			}
		} catch (HibernateException he) {
			throw new Exception("Error in removeExportHistory[" + jobUUID + "]:  " + he.getMessage());
		}
		logger.debug("removed");
	}
}
