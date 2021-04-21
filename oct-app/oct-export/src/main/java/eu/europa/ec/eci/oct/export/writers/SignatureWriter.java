package eu.europa.ec.eci.oct.export.writers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.sql.DataSource;
import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import eu.europa.ec.eci.oct.entities.admin.InitiativeDescription;
import eu.europa.ec.eci.oct.entities.export.ExportHistory;
import eu.europa.ec.eci.oct.export.ExportJobRunner;
import eu.europa.ec.eci.oct.export.entities.SignatureBatch;
import eu.europa.ec.eci.oct.export.persistence.ExportHistoryPersistenceDAO;
import eu.europa.ec.eci.oct.export.rowMappers.InitiativeDescriptionRowMapper;
import eu.europa.ec.eci.oct.export.rowMappers.SignatureRowMapper;
import eu.europa.ec.eci.oct.utils.CommonsConstants;
import eu.europa.ec.eci.oct.utils.XMLutils;

public class SignatureWriter implements ItemWriter<Long> {

	private final Logger logger = LogManager.getLogger(SignatureWriter.class);

	ExecutionContext jobContext;

	@Autowired
	InitiativeDescriptionRowMapper initiativeDescriptionRowMapper;
	private InitiativeDescription defaultInitiativeDescription;

	final String SUPPORT_FORM_OPENING_TAG = "<supportForm>";
	final String SUPPORT_FORM_CLOSING_TAG = "</supportForm>";
	final String FOR_COUNTRY_OPENING_TAG = "<forCountry>";
	final String FOR_COUNTRY_CLOSING_TAG = "</forCountry>";
	final String INITIATIVE_DATA_OPENING_TAG = "<initiativeData>";
	final String INITIATIVE_DATA_CLOSING_TAG = "</initiativeData>";
	final String REGISTRATION_NUMBER_OPENING_TAG = "<registrationNumber>";
	final String REGISTRATION_NUMBER_CLOSING_TAG = "</registrationNumber>";
	final String START_DATE_OPENING_TAG = "<startOfTheCollectionPeriod>";
	final String START_DATE_CLOSING_TAG = "</startOfTheCollectionPeriod>";
	final String CLOSING_DATE_OPENING_TAG = "<endOfTheCollectionPeriod>";
	final String CLOSING_DATE_CLOSING_TAG = "</endOfTheCollectionPeriod>";
	final String URL_COMMISSION_REGISTER_OPENING_TAG = "<urlOnCommissionRegister>";
	final String URL_COMMISSION_REGISTER_CLOSING_TAG = "</urlOnCommissionRegister>";
	final String TITLE_OPENING_TAG = "<title>";
	final String TITLE_CLOSING_TAG = "</title>";
	final String OBJECTIVES_OPENING_TAG = "<objectives>";
	final String OBJECTIVES_CLOSING_TAG = "</objectives>";
	final String CONTACT_PERSONS_LIST_OPENING_TAG = "<registeredContactPersons>";
	final String CONTACT_PERSONS_LIST_CLOSING_TAG = "</registeredContactPersons>";
	final String URL_OPENING_TAG = "<url>";
	final String URL_CLOSING_TAG = "</url>";
	final String SIGNATURES_OPENING_TAG = "<signatures>";
	final String SIGNATURES_CLOSING_TAG = "</signatures>";
	final String SIGNATURE_OPENING_TAG = "<signature>";
	final String SIGNATURE_CLOSING_TAG = "</signature>";
	final String SUBMISSION_DATE_OPENING_TAG = "<submissionDate>";
	final String SUBMISSION_DATE_CLOSING_TAG = "</submissionDate>";
	final String SIGNATURE_IDENTIFIER_OPENING_TAG = "<signatureIdentifier>";
	final String SIGNATURE_IDENTIFIER_CLOSING_TAG = "</signatureIdentifier>";
	final String ANNEX_REVISION_OPENING_TAG = "<annexRevision>";
	final String ANNEX_REVISION_CLOSING_TAG = "</annexRevision>";

	FileWriter fileWriter;
	BufferedWriter bufferedWriter;

	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private ApplicationContext applicationContext;

	@Autowired
	ExportHistoryPersistenceDAO exportHistoryPersistenceDAO;

	@BeforeStep
	public void retrieveInterstepData(StepExecution stepExecution) throws Exception {
		this.stepExecution = stepExecution;
		this.jobContext = this.stepExecution.getExecutionContext();
		initContext();
		initData();
	}

	private long exportedSignatures;
	private long signaturesToBeExported;
	private StepExecution stepExecution;
	SignatureRowMapper rowMapper = new SignatureRowMapper();

	StringBuilder stringBuilder = new StringBuilder();

	@Override
	public void write(List<? extends Long> signatureIds) throws Exception {
		for (Long signatureId : signatureIds) {

			// stop managing
			if (ExportJobRunner.MANUAL_FLOW_SIGNAL == ExportJobRunner.STOP_SIGNAL) {
				logger.error("Interruption detected");
				this.stepExecution.setTerminateOnly();
				return;
			} else {
				SignatureBatch signatureBatch = getSignatureBatchFromId(signatureId);
				String countryCode = signatureBatch.getCountryToSignFor();
				logger.info("processing signature# " + signatureBatch.getSignatureIdentifier() + " for "
						+ signatureBatch.getCountryToSignFor());
				String signatoryInfoString = XMLutils.SIGNATORY_INFO_TAG_OPEN
						+ signatureBatch.getSignatoryInfo()
						+ XMLutils.SIGNATORY_INFO_TAG_CLOSE;
				signatureBatch.setSignatoryInfo(signatoryInfoString);

				String fileContent = "";
				String signatureXml = buildSignatureXml(signatureBatch);
				boolean appendToExistingFile = false;

				int signaturesAlreadyExportedForCountry = 0;
				if (!ExportJobRunner.getCountrySignatureCountMap().containsKey(countryCode)) {
					ExportJobRunner.getCountrySignatureCountMap().put(countryCode, signaturesAlreadyExportedForCountry);
				} else {
					signaturesAlreadyExportedForCountry = ExportJobRunner.getCountrySignatureCountMap()
							.get(countryCode);
				}

				Long signaturesToBeExportedForThisCountry = ExportJobRunner.getSignatureCountryCountMap()
						.get(countryCode);
				signaturesToBeExported = signaturesToBeExportedForThisCountry;
//				System.out.println("signaturesAlreadyExportedForCountry: " + signaturesAlreadyExportedForCountry);
//				System.out.println("signaturesToBeExportedForThisCountry: " + signaturesToBeExportedForThisCountry);
				boolean firstSignature = signaturesAlreadyExportedForCountry == 0;
//				System.out.println("firstSignature: " + firstSignature);
				boolean lastSignature = signaturesAlreadyExportedForCountry == (signaturesToBeExportedForThisCountry
						- 1);
//				System.out.println("lastSignature: " + lastSignature);
				if (firstSignature) {
					// new file to be created, append the header
					String xmlHeader = getXmlHeader(countryCode);
					stringBuilder = new StringBuilder();
					stringBuilder.append(xmlHeader);
//					appendToExistingFile = false;
				}
				stringBuilder.append(signatureXml);
				if (lastSignature) {
					// close the file with footer
					String xmlFooter = getXmlFooter();
					stringBuilder.append(xmlFooter);
//					appendToExistingFile = true;
				}

				fileContent = stringBuilder.toString();

				// logger.info(" FILE CONT: " + fileContent.substring(0, 50)
				// + "...");

				String fileName = getFileName(countryCode);
				// System.err.println(fileContent);
				writeFile(fileName, fileContent, appendToExistingFile, countryCode, lastSignature);
				if (lastSignature) {
					ExportJobRunner.getCountrySignatureCountMap().remove(countryCode);
					stringBuilder = null;
				} else {
					signaturesAlreadyExportedForCountry++;
					ExportJobRunner.getCountrySignatureCountMap().put(countryCode, signaturesAlreadyExportedForCountry);
				}
				exportedSignatures++;

				/* update status */
				updateExportHistory();

			}
		}
	}

	private SignatureBatch getSignatureBatchFromId(Long signatureId) throws Exception {
		// @formatter:off
		String getSignatureBatchFromId = "SELECT	SIG.SIGNATORY_INFO AS SIGNATORYINFO,"
				+ "SIG.COUNTRYTOSIGNFOR_ID AS COUNTRYTOSIGNFOR," + "SIG.UUID AS SIGNATUREIDENTIFIER,"
				+ "SIG.ANNEXREVISION AS ANNEXREVISION," + "SIG.DATEOFSIGNATURE_msec AS SUBMISSIONDATE_msec "
				+ "FROM    OCT_SIGNATURE SIG WHERE SIG.ID = ?";
		// @formatter:on
		SignatureBatch signatureBatch = null;
		// long t0 = System.currentTimeMillis();
		try {
			signatureBatch = (SignatureBatch) jdbcTemplate.queryForObject(getSignatureBatchFromId,
					new Object[] { signatureId }, rowMapper);
			// jdbcTemplate.getDataSource().getConnection().close();
			// DataSourceUtils.releaseConnection(this.dataSource.getConnection(),
			// this.dataSource);
		} catch (Exception e) {
			throw new Exception(
					"Error while mapping the object signatureBatch from id[" + signatureId + "]: " + e.getMessage());
		}
		// long t1 = System.currentTimeMillis();
		// long timeSpentForGettingTheSignatureById = t1-t0;
		// System.err.println("timeSpentForGettingTheSignatureById:
		// "+timeSpentForGettingTheSignatureById+" msec.");
		return signatureBatch;
	}

	private void writeFile(String fileName, String fileContent, boolean appendToExistingFile, String countryCode,
			boolean lastSignature) throws Exception {
		// long t0 = System.currentTimeMillis();
		/* create (if needed) directory */
		String filePath = new StringBuilder().append(ExportJobRunner.genericFilePath).append(File.separatorChar)
				.toString();
		if (!Files.exists(Paths.get(filePath))) {
			new File(filePath).mkdirs();
		}
		File file = new File(filePath + fileName);
		try {
			FileUtils.write(file, fileContent, StandardCharsets.UTF_8, appendToExistingFile);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error writing file.", e);
			throw new Exception("Error writing file " + fileName + ": " + e.getMessage());
		}
		// long t1 = System.currentTimeMillis();
		// long timeSpentForWriting = t1-t0;
		// System.err.println("File writing time with
		// appending["+appendToExistingFile+"] : "+timeSpentForWriting+" msec.");

	}

	private int lastLoggedPercentage = -1;

	private void updateExportHistory() throws Exception {
		int exportedSignaturesPercentage = 100;
		if (signaturesToBeExported > 0) {
			exportedSignaturesPercentage = (int) (exportedSignatures * 100 / ExportJobRunner.totalCount);
		}
		if (exportedSignaturesPercentage % 10 == 0 && exportedSignaturesPercentage != lastLoggedPercentage) {
			// reduce log to ten multiples
			logger.info(">> Exported " + exportedSignaturesPercentage + "% signatures [" + exportedSignatures + "/"
					+ ExportJobRunner.totalCount + "]");
			lastLoggedPercentage = exportedSignaturesPercentage;
		}
		ExportHistory exportHistory = exportHistoryPersistenceDAO.getLastExportHistory();
		String stepName = this.stepExecution.getStepName();
		String stepDescription = stepName;
		if (stepName.equalsIgnoreCase(ExportJobRunner.EXPORT_FILES_STEP_NAME)) {
			stepDescription = " statements of support... ";
		}
		String exportSummary = stepDescription + exportedSignatures + "/" + ExportJobRunner.totalCount;
		if (stepName.equalsIgnoreCase(ExportJobRunner.EXPORT_FILES_STEP_NAME)) {
			exportHistory.setExportProgress(exportedSignaturesPercentage);
			exportHistory.setExportSummary(exportSummary);
		}
		exportHistoryPersistenceDAO.updateExportHistory(exportHistory);
	}

	private String getXmlHeader(String countryCode) {
		StringBuilder buf = new StringBuilder();
		// @formatter:off
		buf.append(SUPPORT_FORM_OPENING_TAG);

		buf.append(FOR_COUNTRY_OPENING_TAG).append(countryCode).append(FOR_COUNTRY_CLOSING_TAG);
		buf.append(INITIATIVE_DATA_OPENING_TAG);
		buf.append(REGISTRATION_NUMBER_OPENING_TAG).append(ExportJobRunner.initiativeData.getRegistrationNumber())
				.append(REGISTRATION_NUMBER_CLOSING_TAG);
		buf.append(START_DATE_OPENING_TAG).append(ExportJobRunner.initiativeData.getRegistrationDate())
				.append(START_DATE_CLOSING_TAG);
		buf.append(CLOSING_DATE_OPENING_TAG).append(ExportJobRunner.initiativeData.getClosingDate())
				.append(CLOSING_DATE_CLOSING_TAG);
		buf.append(URL_COMMISSION_REGISTER_OPENING_TAG)
				.append(ExportJobRunner.initiativeData.getUrlOnCommissionRegister())
				.append(URL_COMMISSION_REGISTER_CLOSING_TAG);
		buf.append(TITLE_OPENING_TAG).append(defaultInitiativeDescription.getTitle()).append(TITLE_CLOSING_TAG);
		String objectives = defaultInitiativeDescription.getObjectives();
		buf.append(OBJECTIVES_OPENING_TAG).append(objectives).append(OBJECTIVES_CLOSING_TAG);
		buf.append(CONTACT_PERSONS_LIST_OPENING_TAG).append(ExportJobRunner.contactStrings.getContactString())
				.append(CONTACT_PERSONS_LIST_CLOSING_TAG);
		buf.append(URL_OPENING_TAG).append(defaultInitiativeDescription.getUrl()).append(URL_CLOSING_TAG);
		buf.append(INITIATIVE_DATA_CLOSING_TAG);

		buf.append(SIGNATURES_OPENING_TAG);
		// @formatter:on
		return buf.toString();
	}

	private String getXmlFooter() {
		StringBuilder buf = new StringBuilder();
		buf.append(SIGNATURES_CLOSING_TAG);
		buf.append(SUPPORT_FORM_CLOSING_TAG);
		return buf.toString();
	}

	private String buildSignatureXml(SignatureBatch signatureBatch) throws DatatypeConfigurationException {
		StringBuilder buf = new StringBuilder();
		// @formatter:off
		buf.append(SIGNATURE_OPENING_TAG);
		buf.append(SIGNATURE_IDENTIFIER_OPENING_TAG).append(signatureBatch.getSignatureIdentifier())
				.append(SIGNATURE_IDENTIFIER_CLOSING_TAG);
		buf.append(ANNEX_REVISION_OPENING_TAG).append(signatureBatch.getAnnexRevision())
				.append(ANNEX_REVISION_CLOSING_TAG);
		buf.append(signatureBatch.getSignatoryInfo());
		buf.append(SIGNATURE_CLOSING_TAG);
		// @formatter:on

		return buf.toString();
	}

	private String getFileName(String countryCode) {
		StringBuilder commonAppend = new StringBuilder().append(countryCode.toUpperCase());
		return commonAppend.append(".xml").toString();
	}

	private void initContext() {
		JobExecution jobExecution = this.stepExecution.getJobExecution();
		this.jobContext = jobExecution.getExecutionContext();
		if (applicationContext == null) {
			applicationContext = new ClassPathXmlApplicationContext(ExportJobRunner.xmlContextPath);
		}
		if (dataSource == null) {
			dataSource = (DataSource) applicationContext.getBean("dataSource");
		}
		if (jdbcTemplate == null) {
			jdbcTemplate = new JdbcTemplate(dataSource);
		}
	}

	private void initData() throws Exception {
		if (defaultInitiativeDescription == null) {
			defaultInitiativeDescription = ExportJobRunner.defaultDescription;
		}
		ExportJobRunner.fileStore = ExportJobRunner.systemPreferences.getFileStoragePath();
		String commonFilePath = ExportJobRunner.fileStore + CommonsConstants.EXPORT_PATH + "/"
				+ ExportJobRunner.nowDateString;
		// String nowString = ""+(System.currentTimeMillis() / 1000 / 60);
		ExportJobRunner.genericFilePath = commonFilePath;

		ExportHistory lastExportHistory = exportHistoryPersistenceDAO.getLastExportHistory();
		lastExportHistory.setExportDirectoryPath(ExportJobRunner.exportCompletePath);
		exportHistoryPersistenceDAO.updateExportHistory(lastExportHistory);
	}

}
