package eu.europa.ec.eci.oct.export;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import eu.europa.ec.eci.oct.entities.admin.InitiativeDescription;
import eu.europa.ec.eci.oct.entities.export.ExportHistory;
import eu.europa.ec.eci.oct.export.persistence.ExportHistoryPersistenceDAO;
import eu.europa.ec.eci.oct.export.utils.ExportParameter;
import eu.europa.ec.eci.oct.utils.DateUtils;
import eu.europa.ec.eci.oct.utils.XMLutils;

@Component
public class ExportValidation implements Tasklet {
	private final Logger logger = LogManager.getLogger(ExportValidation.class);

	ExecutionContext jobContext;
	// private ContactStrings contactStrings;
	private ExportParameter exportParameter;
	// private SystemPreferences systemPreferences;
	private Map<String, InitiativeDescription> initiativeDescriptionsMap;
	// private String genericFilePath;
	// private Long totalCount;

	private StepExecution stepExecution;

	@Autowired
	ExportHistoryPersistenceDAO exportHistoryPersistenceDAO;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		try {

			this.stepExecution = chunkContext.getStepContext().getStepExecution();
			this.jobContext = this.stepExecution.getExecutionContext();
			initContext();

			Collection<File> exportFiles = FileUtils.listFiles(
					new File(ExportJobRunner.genericFilePath.replace("./", "")), new RegexFileFilter("^(.*?)"),
					DirectoryFileFilter.DIRECTORY);

			int totalExportedSignatures = 0;
			Map<String, Map<String, Integer>> lastFileWithLessSignaturesMap = new HashMap<String, Map<String, Integer>>();
			int validatedFiles = 0;
			int filesToBeValidated = exportFiles.size();
			for (File exportedFile : exportFiles) {

				// stop managing
				if (ExportJobRunner.MANUAL_FLOW_SIGNAL == ExportJobRunner.STOP_SIGNAL) {
					this.stepExecution.setTerminateOnly();
					return null;
				} else {
					DocumentBuilderFactory documentBuilderFactory = XMLutils.getDocumentBuilderFactory();
					Document document = null;
					try {
						DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
						document = documentBuilder.parse(exportedFile);
					} catch (Exception e) {
						fail("Error while parsing the xml file: " + e.getMessage());
					}

					// signature data
					NodeList exportedSignatures = document.getElementsByTagName("signature");
					int exportedSignaturesSize = exportedSignatures.getLength();
					totalExportedSignatures += exportedSignaturesSize;

					boolean isValid = validate(exportedFile, exportedSignaturesSize);
					if (!isValid) {
						fail("Export validation failed.");
					}
					/* update status */

					validatedFiles++;
					int validationPercentage = (int) (validatedFiles * 100 / filesToBeValidated);
					logger.info(">> Validation " + validationPercentage + "% file " + exportedFile.getName()
							+ " successful. [" + validatedFiles + "/" + filesToBeValidated + "]");

					updateExportHistory(validatedFiles, filesToBeValidated, exportedSignaturesSize,
							validationPercentage);
				}
			}
			assertEquals(ExportJobRunner.totalCount.intValue(), totalExportedSignatures,
					"total exported signatures count");
			for (Map<String, Integer> counterMap : lastFileWithLessSignaturesMap.values()) {
				for (Integer counter : counterMap.values()) {
					assertTrue(1 == counter, "Expected 1, found: " + counter);
				}
			}
		} catch (Exception e) {
			updateExportHistoryFailure(e.getMessage());
			cleanExportDirectory();
		}
		return null;
	}

	private void updateExportHistoryFailure(String errorMessage) throws Exception {
		ExportHistory exportHistory = exportHistoryPersistenceDAO.getLastExportHistory();
		exportHistory.setErrorMessage(errorMessage);
		exportHistoryPersistenceDAO.updateExportHistory(exportHistory);
	}

	private void updateExportHistory(int validatedFiles, int filesToBeValidated, int exportedSignaturesSize,
			int validationPercentage) throws Exception {
		ExportHistory exportHistory = exportHistoryPersistenceDAO.getLastExportHistory();
		if (exportedSignaturesSize == 0) {
			validationPercentage = 100;
		}
		exportHistory.setValidationProgress(validationPercentage);
		exportHistory.setValidationSummary(validatedFiles + "/" + filesToBeValidated);
		exportHistoryPersistenceDAO.updateExportHistory(exportHistory);
	}

	private void initContext() {
		JobExecution jobExecution = this.stepExecution.getJobExecution();
		this.jobContext = jobExecution.getExecutionContext();
		// this.countryDescriptionSignatureCountMap =
		// ExportJobRunner.countryDescriptionSignatureCountMap;
		JobParameters jobParameters = jobExecution.getJobParameters();
		this.exportParameter = extractParameters(jobParameters);
	}

	private ExportParameter extractParameters(JobParameters jobParameters) {
		ExportParameter exportParameter = new ExportParameter();
		Date startDate = null;
		Date endDate = null;

		try {
			startDate = jobParameters.getDate(ExportJobRunner.START_DATE_PARAMETER_KEY);
			endDate = jobParameters.getDate(ExportJobRunner.END_DATE_PARAMETER_KEY);
		} catch (NullPointerException npe) {
			// no dates param
		}
		if (startDate != null && endDate != null) {
			exportParameter.setStartDate(startDate);
			exportParameter.setEndDate(endDate);
		}
		String countriesListString = jobParameters.getString(ExportJobRunner.COUNTRIES_PARAMETER_KEY);
		if (!countriesListString.isEmpty()) {
			String[] countryCodes = countriesListString.split(",");
			List<String> countryCodesList = Arrays.asList(countryCodes);
			exportParameter.setCountries(countryCodesList);
		}
		return exportParameter;
	}

	public void assertEquals(Object o1, Object o2, String description) throws Exception {
		if (!o1.equals(o2)) {
			fail(description + " - Expected : " + o1 + ", but found: " + o2);
		}
	}

	public void assertTrue(boolean condition, String description) throws Exception {
		if (!condition) {
			fail("condition expected not matched >> " + description);
		}
	}

	public void assertNotNull(Object o, String expectedNotNullelementDesc) throws Exception {
		if (o == null) {
			fail("unexpected null object: " + expectedNotNullelementDesc);
		}
	}

	public void fail(String failureMessage) throws Exception {
		updateExportHistoryFailure(failureMessage);
		this.stepExecution.addFailureException(new Exception(failureMessage));
		this.stepExecution.getJobExecution().addFailureException(new Exception(failureMessage));
		this.stepExecution.setStatus(BatchStatus.FAILED);
		this.stepExecution.setExitStatus(ExitStatus.FAILED);
		this.stepExecution.getJobExecution().stop();
		cleanExportDirectory();
		return;
	}

	private void cleanExportDirectory() throws IOException {
		if (Files.exists(Paths.get(ExportJobRunner.genericFilePath))) {
			FileUtils.cleanDirectory(new File(ExportJobRunner.genericFilePath));
			FileUtils.deleteDirectory(new File(ExportJobRunner.genericFilePath));
		}
	}

	final String REGISTRATION_NUMBER = "registrationNumber";
	final String FOR_COUNTRY = "forCountry";
	final String START_OF_THE_COLLECTION_PERIOD = "startOfTheCollectionPeriod";
	final String END_OF_THE_COLLECTION_PERIOD = "endOfTheCollectionPeriod";
	final String URL_ON_COMMISSION_REGISTER = "urlOnCommissionRegister";

	public boolean validate(File exportedFile, int expectedSignatureSize) {
		String fileName = exportedFile.getName();
		logger.info("## validating file " + exportedFile.getName() + "...");
		try {
			String countryCodeFileName = fileName.substring(0, 2);
			DocumentBuilderFactory documentBuilderFactory = XMLutils.getDocumentBuilderFactory();
			Document document = null;
			try {
				DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
				document = documentBuilder.parse(exportedFile);
			} catch (Exception e) {
				logger.error("Error while parsing the xml file: " + e.getMessage());
				return false;
			}

			// registration number validation
			String registrationNumberElement = document.getElementsByTagName(REGISTRATION_NUMBER).item(0)
					.getTextContent();
			if (StringUtils.isBlank(registrationNumberElement)) {
				logger.error("### registrationNumberElement is missing");
				return false;
			}
			String expectedRegistrationNumberElement = ExportJobRunner.initiativeData.getRegistrationNumber();
			if (!expectedRegistrationNumberElement.equalsIgnoreCase(registrationNumberElement)) {
				logger.error("### expectedRegistrationNumberElement is " + expectedRegistrationNumberElement
						+ " but in the XML is " + registrationNumberElement);
				return false;
			}

			// country code validation
			String forCountry = document.getElementsByTagName(FOR_COUNTRY).item(0).getTextContent();
			if (StringUtils.isBlank(forCountry)) {
				logger.error("### forCountry is missing");
				return false;
			}
			if (!countryCodeFileName.equalsIgnoreCase(forCountry)) {
				logger.error("### countryCodeFileName is " + countryCodeFileName + " but in the XML is " + forCountry);
				return false;
			}

			// start of the collection period validation
			String startOfTheCollectionPeriod = document.getElementsByTagName(START_OF_THE_COLLECTION_PERIOD).item(0)
					.getTextContent();
			if (StringUtils.isBlank(startOfTheCollectionPeriod)) {
				logger.error("### startOfTheCollectionPeriod is missing");
				return false;
			}
			String expectedStartOfTheCollectionPeriod = "" + ExportJobRunner.initiativeData.getRegistrationDate();
			if (!startOfTheCollectionPeriod.equalsIgnoreCase(expectedStartOfTheCollectionPeriod)) {
				logger.error("###  expectedStartOfTheCollectionPeriod is " + expectedStartOfTheCollectionPeriod
						+ " but in the XML is " + startOfTheCollectionPeriod);
				return false;
			}

			// end of the collection period validation
			String endOfTheCollectionPeriod = document.getElementsByTagName(END_OF_THE_COLLECTION_PERIOD).item(0)
					.getTextContent();
			if (StringUtils.isBlank(endOfTheCollectionPeriod)) {
				logger.error("### endOfTheCollectionPeriod is missing");
				return false;
			}
			String expectedEndOfTheCollectionPeriod = "" + ExportJobRunner.initiativeData.getClosingDate();
			if (!expectedEndOfTheCollectionPeriod.equalsIgnoreCase(endOfTheCollectionPeriod)) {
				logger.error("###  expectedEndOfTheCollectionPeriod is " + expectedEndOfTheCollectionPeriod
						+ " but in the XML is " + endOfTheCollectionPeriod);
				return false;
			}

			// url on commission register
			String urlOnCommissionRegister = document.getElementsByTagName(URL_ON_COMMISSION_REGISTER).item(0)
					.getTextContent();
			if (StringUtils.isBlank(urlOnCommissionRegister)) {
				logger.error("### urlOnCommissionRegister is missing");
				return false;
			}
			String expectedUrlOnCommissionRegister = "" + ExportJobRunner.initiativeData.getUrlOnCommissionRegister();
			if (!expectedUrlOnCommissionRegister.equalsIgnoreCase(urlOnCommissionRegister)) {
				logger.error("###  expectedUrlOnCommissionRegister is " + expectedUrlOnCommissionRegister
						+ " but in the XML is " + urlOnCommissionRegister);
				return false;
			}

			// TODO: not so important, but to be extended with initiative data

			// signature data
			NodeList exportedSignatures = document.getElementsByTagName("signature");
			if (exportedSignatures == null || exportedSignatures.getLength() == 0) {
				logger.error("### signatures are missing");
				return false;
			}
			int exportedSignaturesSize = exportedSignatures.getLength();
			if (expectedSignatureSize != exportedSignaturesSize) {
				logger.error("###  expectedSignatureSize is " + expectedSignatureSize + " but in the XML is "
						+ exportedSignaturesSize);
				return false;
			}
			for (int i = 0; i < exportedSignaturesSize; i++) {
				Element exportedSignature = (Element) exportedSignatures.item(i);

				// signature identifier
				String signatureIdentifier = exportedSignature.getElementsByTagName("signatureIdentifier").item(0)
						.getTextContent();
				if (StringUtils.isBlank(signatureIdentifier)) {
					logger.error("### signature identifier is missing");
					return false;
				}

				// signature submission date
				String submissionDateString = exportedSignature.getElementsByTagName("submissionDate").item(0)
						.getTextContent();
				if (StringUtils.isBlank(submissionDateString)) {
					logger.error("### submission date is missing for signature UUID " + signatureIdentifier);
					return false;
				}
				try {
					DateUtils.parseDate(submissionDateString, DateUtils.EXPORT_DATE_FORMAT);
				} catch (Exception e) {
					logger.error("### submission date is not correctly formatted " + submissionDateString);
					return false;
				}

				// annex revision
				String annexRevision = exportedSignature.getElementsByTagName("annexRevision").item(0).getTextContent();
				if (StringUtils.isBlank(annexRevision)) {
					logger.error("### annexRevision is missing for signature UUID " + signatureIdentifier);
					return false;
				}

				// signatory info
				String signatoryInfo = exportedSignature.getElementsByTagName("signatoryInfo").item(0).getTextContent();
				if (StringUtils.isBlank(signatoryInfo)) {
					logger.error("### signatoryInfo is missing for signature UUID " + signatureIdentifier);
					return false;
				}
			}
		} catch (Exception e) {
			logger.error("Error while validating " + fileName + ": " + e.getMessage());
		}
		logger.info("## file validated successfully.");

		return true;
	}

}
