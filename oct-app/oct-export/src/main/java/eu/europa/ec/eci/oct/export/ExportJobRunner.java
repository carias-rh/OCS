package eu.europa.ec.eci.oct.export;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.eci.oct.entities.admin.InitiativeDescription;
import eu.europa.ec.eci.oct.entities.admin.SystemPreferences;
import eu.europa.ec.eci.oct.entities.export.ExportHistory;
import eu.europa.ec.eci.oct.export.entities.ContactStrings;
import eu.europa.ec.eci.oct.export.entities.InitiativeData;
import eu.europa.ec.eci.oct.export.persistence.ExportHistoryPersistenceDAO;
import eu.europa.ec.eci.oct.export.utils.ExportParameter;
import eu.europa.ec.eci.oct.export.utils.ItemProcessorsUtils;
import eu.europa.ec.eci.oct.utils.CommonsConstants;
import eu.europa.ec.eci.oct.utils.DateUtils;

@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class ExportJobRunner {

	private final Logger logger = LogManager.getLogger(ExportJobRunner.class);

	static final String XML_CONTEXT_PATH = "/batch/config/service-context.xml";
	private static final String XML_CONTEXT_PATH_TEST = "/batch/config/service-context-test.xml";
	public final static String EXPORT_FILES_STEP_NAME = "getSignatures";

	protected ExportJobRunner() {
	}

	@Value(value = "filterConditions")
	static String filterConditions;

	@Value(value = "fileSplitLimit")
	static String fileSplitLimit;

	private Job job;
	private JobLauncher jobLauncher;
	private JobExplorer jobExplorer;
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private ApplicationContext applicationContext;
	private ExportHistoryPersistenceDAO exportHistoryPersistenceDAO;

	public static String nowDateString;
	public static final String FILTER_CONDITIONS_PARAM_KEY = "filterConditions";
	public static final String SPLIT_FILE_PARAM_KEY = "splitFileLimit";
	public static final String START_DATE_PARAMETER_KEY = "startDate";
	public static final String END_DATE_PARAMETER_KEY = "endDate";
	public static final String COUNTRIES_PARAMETER_KEY = "countries";

	public static final String JOB_NAME = "export";

	public static long MANUAL_FLOW_SIGNAL = 0;
	public static long GO_SIGNAL = 1;
	public static long STOP_SIGNAL = -1;

	public static Map<String, Integer> countrySignatureCountMap = new HashMap<String, Integer>();
	public static InitiativeDescription defaultDescription = null;
	public static Map<String, Long> signatureCountryCountMap = new HashMap<String, Long>();

	public synchronized static Map<String, Integer> getCountrySignatureCountMap() {
		return countrySignatureCountMap;
	}

	public synchronized static void setDefaultDescription(InitiativeDescription inputDescription) {
		defaultDescription = inputDescription;
	}

	public synchronized static void setSignatureCountryCountMap(Map<String, Long> inputSignatureCountryCountMap) {
		signatureCountryCountMap.clear();
		signatureCountryCountMap.putAll(inputSignatureCountryCountMap);
	}

	public synchronized static Map<String, Long> getSignatureCountryCountMap() {
		return signatureCountryCountMap;
	}

	public static void setTotalCount(Long inputTotalCount) {
		totalCount = inputTotalCount;
	}

	public static Long totalCount = 0l;
	public static ContactStrings contactStrings;
	public static Map<Long, String> countryIdCodeMap;
	public static InitiativeData initiativeData;
	public static SystemPreferences systemPreferences;
	public static String genericFilePath;
	public static String fileStore;
	public static String xmlContextPath;
	public static String exportCompletePath;

	long startTime;

	public JobExecution runExportTest(ExportParameter exportParameter) throws Exception {
		xmlContextPath = XML_CONTEXT_PATH_TEST;
		prodCredentials = false;
		isTestMode = true;
		return run(exportParameter, xmlContextPath);
	}

	public JobExecution runExport(ExportParameter exportParameter) throws Exception {
		xmlContextPath = XML_CONTEXT_PATH;
		isTestMode = false;
		return run(exportParameter, xmlContextPath);
	}

	public JobExecution run(ExportParameter exportParameter, String xmlContextPath) throws Exception {
		initConstants(exportParameter);
		initBean(xmlContextPath);
		if (dataSource == null) {
			dataSource = (DataSource) applicationContext.getBean("dataSource");
		}
		if (jdbcTemplate == null) {
			jdbcTemplate = new JdbcTemplate(dataSource);
		}
		cleanPreviousStatus();
		JobParametersBuilder jobParametersBuilder = initJobParameters(exportParameter);

		String jobUUID = "";
		try {
			jobUUID = exportHistoryPersistenceDAO.initExportHistory(exportParameter);
		} catch (Exception e) {
			logger.error("Error in initExportHistory with exportParameter[" + exportParameter + "]: " + e.getMessage());
		}

		// long initSpentTime = new Date().getTime() - startTime;

		MANUAL_FLOW_SIGNAL = GO_SIGNAL;
		JobExecution jobExecution = null;
		JobParameters jobParameters = jobParametersBuilder.toJobParameters();
		try {
			System.out.println("launchinh export with params:" + exportParameter);
			jobExecution = jobLauncher.run(job, jobParameters);
		} catch (JobExecutionAlreadyRunningException e) {
			logger.error("JobExecutionAlreadyRunningException: " + e.getMessage());
			throw new JobExecutionAlreadyRunningException(e.getMessage());
		} catch (JobRestartException e) {
			logger.error("JobRestartException: " + e.getMessage());
			throw new JobRestartException(e.getMessage());
		} catch (JobInstanceAlreadyCompleteException e) {
			exportHistoryPersistenceDAO.removeExportHistory(jobUUID);
			logger.error("JobInstanceAlreadyCompleteException: " + e.getMessage());
			throw new JobInstanceAlreadyCompleteException(e.getMessage());
		} catch (JobParametersInvalidException e) {
			logger.error("JobParametersInvalidException: " + e.getMessage());
			throw new JobParametersInvalidException(e.getMessage());
		} catch (Exception e) {
			logger.error("Unexpected error: " + e.getMessage());
			throw new Exception(e);

		}
		// long updateHistoryStart = new Date().getTime();
		try {
			updateHistory(jobExecution, startTime);
		} catch (Exception e) {
			logger.error("Error in updateHistory: " + e.getMessage());
			throw new Exception(e);
		}
		// long updateHistoryEnd = new Date().getTime();
		// long updateHistorySpentTime = updateHistoryEnd - updateHistoryStart;
		// long endTime = new Date().getTime();
		// long jobSpentTime = endTime - startTime;
		// evaluateTimePerformances(startTime, jobSpentTime,
		// updateHistorySpentTime, initSpentTime);
		try {
			DataSourceUtils.releaseConnection(this.dataSource.getConnection(), this.dataSource);
		} catch (Exception e) {
			logger.error("Error in updateHistory: " + e.getMessage());
			throw new Exception(e);
		}

		return jobExecution;
	}

	private void cleanPreviousStatus() throws Exception {
		try {
			boolean cleanState = false;
			boolean cleanLaunched = false;
			int guardStop = 0;
			while (!cleanState) {
				if (guardStop > 100) {
					break;
				}
				if (!cleanLaunched) {
					cleanState = cleanBatchTables();
					cleanLaunched = true;
				}
				guardStop++;
			}
		} catch (Exception e) {
			logger.error("Error in cleanBatchTables: " + e.getMessage());
			throw new Exception(e);
		}
	}

	public static final String IDENTITY_FOLDER_PROD = "identity";
	public static final String IDENTITY_TEST_FOLDER_NAME = "identity_test";
	// private final List<String> PROD_ENVS = Arrays.asList("PROD", "SHS-ACC3");
	private final String LOCAL = "local";

	private void initConstants(ExportParameter exportParameter) {
		startTime = new Date().getTime();
		nowDateString = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(startTime);
		signatureCountryCountMap = new HashMap<String, Long>();
		countrySignatureCountMap = new HashMap<String, Integer>();
		totalCount = 0L;
		signatureCountryCountMap.clear();
	}

	String databaseTypeName = "";

	public static Boolean isDataQuality;
	public static Boolean prodCredentials;
	public static Boolean isTestMode;

	private boolean cleanBatchTables() throws Exception {

		logger.debug("clean batch tables started...");
		// clear batch tables
		this.jdbcTemplate.execute("DELETE FROM BATCH_STEP_EXECUTION_CONTEXT");
		this.jdbcTemplate.execute("DELETE FROM BATCH_JOB_EXECUTION_CONTEXT");
		this.jdbcTemplate.execute("DELETE FROM BATCH_STEP_EXECUTION");
		this.jdbcTemplate.execute("DELETE FROM BATCH_JOB_EXECUTION_PARAMS");
		this.jdbcTemplate.execute("DELETE FROM BATCH_JOB_EXECUTION");
		this.jdbcTemplate.execute("DELETE FROM BATCH_JOB_INSTANCE");
		// initialize seq
		if (databaseTypeName.isEmpty()) {
			try {
				Connection connection = this.jdbcTemplate.getDataSource().getConnection();
				databaseTypeName = connection.getMetaData().getDatabaseProductName();
				DataSourceUtils.releaseConnection(connection, dataSource);
			} catch (SQLException e) {
				String errorMessage = e.getMessage();
				logger.error("Error while retrieving the databaseProductName: " + errorMessage);
				throw new Exception("Error while retrieving the databaseProductName: " + errorMessage);
			}
		}
		if (databaseTypeName.equalsIgnoreCase(CommonsConstants.MYSQL_DB_PRODUCT_NAME)) {
			// MYSQL
			this.jdbcTemplate.execute("DELETE FROM BATCH_STEP_EXECUTION_SEQ");
			this.jdbcTemplate.execute("DELETE FROM BATCH_JOB_EXECUTION_SEQ");
			this.jdbcTemplate.execute("DELETE FROM BATCH_JOB_SEQ");
			this.jdbcTemplate.execute(
					"INSERT INTO BATCH_JOB_EXECUTION_SEQ (ID, UNIQUE_KEY) SELECT * FROM (SELECT 0 AS ID, '0' AS UNIQUE_KEY) AS tmp WHERE NOT EXISTS(SELECT * FROM BATCH_JOB_EXECUTION_SEQ)");
			this.jdbcTemplate.execute(
					"INSERT INTO BATCH_STEP_EXECUTION_SEQ (ID, UNIQUE_KEY) SELECT * FROM (SELECT 0 AS ID, '0' AS UNIQUE_KEY) AS tmp WHERE NOT EXISTS(SELECT * FROM BATCH_STEP_EXECUTION_SEQ)");
			this.jdbcTemplate.execute(
					"INSERT INTO BATCH_JOB_SEQ (ID, UNIQUE_KEY) SELECT * FROM (SELECT 0 AS ID, '0' AS UNIQUE_KEY) AS tmp WHERE NOT EXISTS(SELECT * FROM BATCH_JOB_SEQ)");
		} else if (databaseTypeName.equalsIgnoreCase(CommonsConstants.ORACLE_DB_PRODUCT_NAME)) {
			// Oracle
			this.jdbcTemplate.execute("DROP SEQUENCE  BATCH_STEP_EXECUTION_SEQ");
			this.jdbcTemplate.execute("DROP SEQUENCE  BATCH_JOB_EXECUTION_SEQ");
			this.jdbcTemplate.execute("DROP SEQUENCE  BATCH_JOB_SEQ");
			this.jdbcTemplate.execute(
					"CREATE SEQUENCE BATCH_STEP_EXECUTION_SEQ START WITH 0 MINVALUE 0 MAXVALUE 9223372036854775807 NOCYCLE");
			this.jdbcTemplate.execute(
					"CREATE SEQUENCE BATCH_JOB_EXECUTION_SEQ START WITH 0 MINVALUE 0 MAXVALUE 9223372036854775807 NOCYCLE");
			this.jdbcTemplate.execute(
					"CREATE SEQUENCE BATCH_JOB_SEQ START WITH 0 MINVALUE 0 MAXVALUE 9223372036854775807 NOCYCLE");
		} else {
			throw new Exception("Database not supported: " + databaseTypeName + " (only Oracle/MySQL allowed)");
		}
//		this.jdbcTemplate.getDataSource().getConnection().close();
//		DataSourceUtils.releaseConnection(this.dataSource.getConnection(), this.dataSource);
		logger.debug("...clean batch tables ended");
		return true;
	}

	public void stop(String jobUUID) throws Exception {
		logger.debug("stop request for jobUUID[" + jobUUID + "]");
		MANUAL_FLOW_SIGNAL = STOP_SIGNAL;
		try {
			List<JobInstance> jobInstances = this.jobExplorer.getJobInstances(JOB_NAME, 0, 1);
			if (jobInstances != null && !jobInstances.isEmpty()) {
				JobInstance runningJobInstance = jobInstances.get(0);
				JobExecution runningJobExecution = jobExplorer.getJobExecution(runningJobInstance.getId());
				runningJobExecution.setStatus(BatchStatus.STOPPED);
				runningJobExecution.setExitStatus(ExitStatus.STOPPED);
				runningJobExecution.stop();
				for (StepExecution stepExecution : runningJobExecution.getStepExecutions()) {
					stepExecution.setTerminateOnly();
					stepExecution.setExitStatus(ExitStatus.STOPPED);
					stepExecution.setStatus(BatchStatus.STOPPED);
				}
				updateHistory(runningJobExecution, startTime);

			}
		} catch (Exception e) {
			throw new Exception("Error while stopping the export: " + e.getMessage());
		}

		logger.debug("stop completed.");

	}

	private void updateHistory(JobExecution jobExecution, long startTime) throws Exception {
		ExportHistory exportHistory = exportHistoryPersistenceDAO.getLastExportHistory();
		exportHistory.setBatchStatus(jobExecution.getStatus().name());
		ExitStatus exitStatus = jobExecution.getExitStatus();
		exportHistory.setExitStatus(exitStatus.getExitCode());
		long endTime = new Date().getTime();
		String duration = DateUtils.getHumanTimeFromMillisec(endTime - startTime);
		exportHistory.setDuration(duration);
		long jobExecutionId = jobExecution.getJobId();
		for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
			String stepName = stepExecution.getStepName();
			long stepJobId = stepExecution.getJobExecutionId();
			int readCounter = stepExecution.getReadCount();
			String readCount = "readCount:" + readCounter;
			String status = stepExecution.getStatus().name();
			int writeCounter = stepExecution.getWriteCount();
			String writeCount = "writeCount:" + writeCounter;
			if (jobExecutionId == stepJobId) {
				if (stepName.equals(EXPORT_FILES_STEP_NAME)) {
					if (readCounter == 0) {
						exportHistory.setExportProgress(100);
					}
					exportHistory.setExportSummary(status + ": " + readCount + " / " + writeCount);
				}
			}
		}

		String errorMessage = "";
		if (!jobExecution.getAllFailureExceptions().isEmpty()) {
			for (Throwable t : jobExecution.getAllFailureExceptions()) {
				errorMessage += ">> " + t.getMessage() + "\n";
			}
			exportHistory.setBatchStatus(BatchStatus.FAILED.name());
			exportHistory.setExitStatus(ExitStatus.FAILED.getExitCode());
		}
		exportHistory.setErrorMessage(errorMessage);

		exportHistoryPersistenceDAO.updateExportHistory(exportHistory);
	}

	private void initBean(String xmlContextPath) {
		applicationContext = new ClassPathXmlApplicationContext(xmlContextPath);
		jobLauncher = (JobLauncher) applicationContext.getBean("jobLauncher");
		jobExplorer = (JobExplorer) applicationContext.getBean("jobExplorer");
		job = (Job) applicationContext.getBean(JOB_NAME);
		exportHistoryPersistenceDAO = (ExportHistoryPersistenceDAO) applicationContext
				.getBean("exportHistoryPersistenceDAO");
	}

	private JobParametersBuilder initJobParameters(ExportParameter exportParameter) {
		JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
		jobParametersBuilder.addDate("launchTime", new Date());
		boolean countryFilter = false;
		boolean dateFilter = false;

		List<String> countryCodesList = exportParameter.getCountries();
		String countryCodesSQLparameters = "";
		if (!countryCodesList.isEmpty() && countryCodesList.size() != CommonsConstants.TOTAL_COUNTRIES_NUMBER) {
			countryFilter = true;
			for (int i = 0; i < countryCodesList.size(); i++) {
				String countryCode = countryCodesList.get(i);
				countrySignatureCountMap.put(countryCode, 0);
				String countryCodeSLQparameterFormat = "'" + countryCode + "'";
				countryCodesSQLparameters += countryCodeSLQparameterFormat;
				if (i < (countryCodesList.size() - 1)) {
					countryCodesSQLparameters += ",";
				}
			}
		}

		String startDateParameterMsec = "";
		String endDateParameterMsec = "";
		// startDate / endDate
		Date startDateParameter = exportParameter.getStartDate();
		Date endDateParameter = exportParameter.getEndDate();
		if (startDateParameter != null && endDateParameter != null) {
			jobParametersBuilder.addDate(START_DATE_PARAMETER_KEY, exportParameter.getStartDate());
			jobParametersBuilder.addDate(END_DATE_PARAMETER_KEY, exportParameter.getEndDate());
			startDateParameterMsec = startDateParameter.getTime() + "";
			// we miss the time in millisec (default time is 00:00) so
			// if date is the same force to calulate 1 day range
			endDateParameterMsec = "" + (endDateParameter.getTime() + ItemProcessorsUtils.ONE_DAY_MILLISEC);
			dateFilter = true;
		}
		filterConditions = "";
		if (countryFilter || dateFilter) {
			filterConditions += " WHERE ";
			if (countryFilter) {
				filterConditions += " COU.CODE IN (" + countryCodesSQLparameters + ") ";
			}
			if (dateFilter) {
				if (countryFilter) {
					filterConditions += " AND ";
				}
				filterConditions += " SIG.DATEOFSIGNATURE_MSEC >= " + startDateParameterMsec
						+ " AND SIG.DATEOFSIGNATURE_MSEC <= " + endDateParameterMsec;
			}

		}
		jobParametersBuilder.addString(FILTER_CONDITIONS_PARAM_KEY, filterConditions);
		jobParametersBuilder.addString(COUNTRIES_PARAMETER_KEY, countryCodesSQLparameters);
		return jobParametersBuilder;
	}

}
