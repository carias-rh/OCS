package eu.europa.ec.eci.oct.webcommons.restApi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europa.ec.eci.oct.entities.signature.Signature;
import eu.europa.ec.eci.oct.entities.system.Country;
import eu.europa.ec.eci.oct.entities.system.Language;
import eu.europa.ec.eci.oct.export.ExportJobRunner;
import eu.europa.ec.eci.oct.export.utils.ExportParameter;
import eu.europa.ec.eci.oct.utils.DateUtils;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.export.ExportParameterDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.export.ExportSignaturesCountDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.export.ExportSignaturesCountDetailDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.signature.SignatureMetadata;
import eu.europa.ec.eci.oct.webcommons.services.enums.CountryEnum;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/rest-api-test.xml")
public class ExportApiTest extends RestApiTest {

	static final String EXPORT_API = "/export";
	static final boolean DELETE_AFTER_GENERATION = false;

	ClassLoader classLoader = getClass().getClassLoader();

	@Autowired
	protected ExportJobRunner exportJobRunner;

	/*
	 * will be multiplied for each language and for each country. For customizing
	 * just add country codes or language codes in the lists below.
	 */
	static final int signaturePerCountry = 4;

	static final List<String> customCountryCodesToSignFor = Arrays.asList(CountryEnum.BELGIUM.getCode(),
			CountryEnum.FRANCE.getCode(), CountryEnum.AUSTRIA.getCode(), CountryEnum.ITALY.getCode());

	public static String EXPORT_PATH;
	List<Country> countriesToSignFor = new ArrayList<Country>();
	List<Language> descriptionLanguagesToSignFor = new ArrayList<Language>();
	Map<String, Integer> insertedSignaturesMap = new HashMap<String, Integer>();
	int newSignaturesSize = 0;

	@Before
	public void setupDb() throws Exception {
		EXPORT_PATH = TEST_FILE_STORAGE_PATH + "export";

		// creating root folder
		new File(EXPORT_PATH).mkdirs();
		cleanOldFiles();

		// customize db for specific tests
		customizeDb();
	}

	@After
	public void cleanStoragePath() throws IOException, OCTException, SQLException {
		EXPORT_PATH = TEST_FILE_STORAGE_PATH + "export";
		if (DELETE_AFTER_GENERATION) {
			// delete exported file from resources path
			cleanOldFiles();
		}
		removeNewSignatures();
		removeExportTables();
	}

	@Test
	@Ignore
	public void testConnectionPoolOverload() throws Exception {

		ExportParameter exportParameter = new ExportParameter();
		exportParameter.setCountries(allCountryCodes);
		ExportParameterDTO exportParameterDTO = exportTransformer.transform(exportParameter);
		JobExecution jobExecution = null;
		int i = 1;
		while (i <= 20) {
			System.err.println(">>>>>>>>>>>>>>>>>> RUNNING OVERLOAD PHASE #" + i);
			try {
				jobExecution = exportJobRunner.runExportTest(exportTransformer.transform(exportParameterDTO));
			} catch (Exception e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
			i++;
		}
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

		if (DELETE_AFTER_GENERATION) {
			cleanOldFiles();
		}
		removeExportTables();
	}

	@Test
	public void exportAll() throws Exception {
		ExportParameter exportParameter = new ExportParameter();
		exportParameter.setCountries(allCountryCodes);
		ExportParameterDTO exportParameterDTO = exportTransformer.transform(exportParameter);
		JobExecution jobExecution = null;
		try {
			jobExecution = exportJobRunner.runExportTest(exportTransformer.transform(exportParameterDTO));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

		if (DELETE_AFTER_GENERATION) {
			cleanOldFiles();
		}
		removeExportTables();
	}

	@Test
	public void exportByDateRange() throws Exception {

		Date sampleStartDate = DateUtils.getRandomDateBetween(MOCK_REGISTRATION_DATE, new Date());

		assertTrue(DateUtils.isBetween(MOCK_REGISTRATION_DATE, MOCK_EXPIRE_DATE, sampleStartDate));

		ExportParameterDTO exportParameterDTO = new ExportParameterDTO();
		exportParameterDTO.setStartDate(DateUtils.formatDate(sampleStartDate));
		exportParameterDTO.setEndDate(MOCK_EXPIRE_DATE_STRING);
		exportParameterDTO.setCountries(allCountryCodes);

		JobExecution jobExecution = null;
		try {
			jobExecution = exportJobRunner.runExportTest(exportTransformer.transform(exportParameterDTO));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

		if (DELETE_AFTER_GENERATION) {
			cleanOldFiles();
		}
	}

	@Test
	public void exportByDateRangeAndSomeCountries() throws Exception {
		Date sampleStartDate = DateUtils.getRandomDateBetween(MOCK_REGISTRATION_DATE, new Date());
		List<String> sampleCountryCodes = new ArrayList<String>();
		int randomIndex = getRandomNumber(allCountryCodes.size() - 1);
		for (int i = 0; i < randomIndex; i++) {
			sampleCountryCodes.add(allCountryCodes.get(i));
		}

		ExportParameterDTO exportParameterDTO = new ExportParameterDTO();
		String sampleStartDateFormatted = DateUtils.formatDate(sampleStartDate);
		exportParameterDTO.setStartDate(sampleStartDateFormatted);
		exportParameterDTO.setEndDate(MOCK_EXPIRE_DATE_STRING);
		exportParameterDTO.setCountries(sampleCountryCodes);

		JobExecution jobExecution = null;
		try {
			jobExecution = exportJobRunner.runExportTest(exportTransformer.transform(exportParameterDTO));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

		if (DELETE_AFTER_GENERATION) {
			cleanOldFiles();
		}
	}

	@Test
	public void exportBySpecificDate() throws Exception {
		Date sampleDate = new Date();

		ExportParameterDTO exportParameterDTO = new ExportParameterDTO();
		String sampleDateFormatted = DateUtils.formatDate(sampleDate);
		exportParameterDTO.setStartDate(sampleDateFormatted);
		exportParameterDTO.setEndDate(sampleDateFormatted);
		exportParameterDTO.setCountries(allCountryCodes);
		assertTrue(DateUtils.isBetween(MOCK_REGISTRATION_DATE, MOCK_EXPIRE_DATE, sampleDate));

		JobExecution jobExecution = null;
		try {
			jobExecution = exportJobRunner.runExportTest(exportTransformer.transform(exportParameterDTO));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

		if (DELETE_AFTER_GENERATION) {
			cleanOldFiles();
		}
	}

	@Test
	public void exportByOneCountryAndSpecificDate() throws Exception {
		List<SignatureMetadata> lastInsertedSignatures = getLastSignatures();
		int randomIndex = getRandomNumber(lastInsertedSignatures.size() - 1);
		SignatureMetadata randomSignature = lastInsertedSignatures.get(randomIndex);
		String randomDateString = randomSignature.getDate();
		Date randomDate = DateUtils.parseDate(randomDateString);
		assertTrue(DateUtils.isBetween(MOCK_REGISTRATION_DATE, MOCK_EXPIRE_DATE, randomDate));
		String sampleCountryCode = randomSignature.getCountry();

		ExportParameterDTO exportParameterDTO = new ExportParameterDTO();
		exportParameterDTO.setStartDate(randomDateString);
		exportParameterDTO.setEndDate(randomDateString);

		List<String> countryCodeList = new ArrayList<String>();
		countryCodeList.add(sampleCountryCode);
		exportParameterDTO.setCountries(countryCodeList);

		JobExecution jobExecution = null;
		try {
			jobExecution = exportJobRunner.runExportTest(exportTransformer.transform(exportParameterDTO));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

		if (DELETE_AFTER_GENERATION) {
			cleanOldFiles();
		}
	}

	@Test
	public void countPerCountryToday() {
		ExportParameterDTO exportParameterDTO = new ExportParameterDTO();
		Date todayDate = new Date();
		assertTrue(DateUtils.isBetween(MOCK_REGISTRATION_DATE, MOCK_EXPIRE_DATE, todayDate));

		String today = DateUtils.formatDate(todayDate);
		exportParameterDTO.setStartDate(today);
		exportParameterDTO.setEndDate(today);

		Response response = null;
		try {
			response = target(EXPORT_API + "/countByCountry").request()
					.header(HttpHeaders.AUTHORIZATION, BEARER + authorizationToken)
					.post(Entity.entity(exportParameterDTO, MediaType.APPLICATION_JSON));
			assertEquals(Status.OK.getStatusCode(), response.getStatus());
			assertNotNull(response);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		ExportSignaturesCountDTO countPerCountry = response.readEntity(ExportSignaturesCountDTO.class);
		assertNotNull(countPerCountry);
		assertNotNull(countPerCountry.getTotal());
		assertNotNull(countPerCountry.getList());
		assertFalse(countPerCountry.getList().isEmpty());
		assertEquals(newSignaturesSize - allMockedSignatures.size(), countPerCountry.getTotal());

		long total = 0L;
		for (ExportSignaturesCountDetailDTO escDTO : countPerCountry.getList()) {
			String countryCode = escDTO.getCountryCode();
			assertNotNull(countryCode);
			long countPerCountryCount = escDTO.getCount();
			assertNotNull(countPerCountryCount);
			long expectedCount = insertedSignaturesMap.get(countryCode).longValue();
			if (allMockedSignaturesCountryCodes.contains(countryCode)) {
				// discard the mocked signatures from the count
				expectedCount -= allMockedSignaturesCountMap.get(countryCode);
			}
			assertEquals(expectedCount, countPerCountryCount);
			total += countPerCountryCount;
		}
		assertEquals(newSignaturesSize - allMockedSignatures.size(), total);

	}

	@Test
	public void countPerCountryOverall() {
		ExportParameterDTO exportParameterDTO = new ExportParameterDTO();
		Date todayDate = new Date();
		String today = DateUtils.formatDate(todayDate);
		exportParameterDTO.setStartDate(MOCK_REGISTRATION_DATE_STRING);
		exportParameterDTO.setEndDate(today);
		exportParameterDTO.setCountries(allCountryCodes);
		assertTrue(DateUtils.isBetween(MOCK_REGISTRATION_DATE, MOCK_EXPIRE_DATE, todayDate));

		Response response = null;
		try {
			response = target(EXPORT_API + "/countByCountry").request()
					.header(HttpHeaders.AUTHORIZATION, BEARER + authorizationToken)
					.post(Entity.entity(exportParameterDTO, MediaType.APPLICATION_JSON));
			assertEquals(Status.OK.getStatusCode(), response.getStatus());
			assertNotNull(response);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		ExportSignaturesCountDTO countPerCountry = response.readEntity(ExportSignaturesCountDTO.class);
		assertNotNull(countPerCountry);
		assertNotNull(countPerCountry.getTotal());
		assertNotNull(countPerCountry.getList());
		assertFalse(countPerCountry.getList().isEmpty());
		assertEquals(newSignaturesSize, countPerCountry.getTotal());

		long total = 0L;
		for (ExportSignaturesCountDetailDTO escDTO : countPerCountry.getList()) {
			String countryCode = escDTO.getCountryCode();
			assertNotNull(countryCode);
			long countPerCountryCount = escDTO.getCount();
			assertNotNull(countPerCountryCount);
			assertEquals(insertedSignaturesMap.get(countryCode).longValue(), countPerCountryCount);
			total += countPerCountryCount;
		}
		assertEquals(newSignaturesSize, total);

	}

	@Test
	public void exportByOneCountry() throws Exception {

		int randomIndex = getRandomNumber(customCountryCodesToSignFor.size() - 1);
		String sampleCountryCode = customCountryCodesToSignFor.get(randomIndex);

		ExportParameterDTO exportParameterDTO = new ExportParameterDTO();
		List<String> countryCodeList = new ArrayList<String>();
		countryCodeList.add(sampleCountryCode);
		exportParameterDTO.setCountries(countryCodeList);
		exportParameterDTO.setEndDate(MOCK_REGISTRATION_DATE_STRING);
		exportParameterDTO.setEndDate(MOCK_EXPIRE_DATE_STRING);

		JobExecution jobExecution = null;
		try {
			jobExecution = exportJobRunner.runExportTest(exportTransformer.transform(exportParameterDTO));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

		if (DELETE_AFTER_GENERATION) {
			cleanOldFiles();
		}
	}

	private void cleanOldFiles() throws IOException {
		if (Files.exists(Paths.get(EXPORT_PATH + "/"))) {
			FileUtils.cleanDirectory(new File(EXPORT_PATH));
		}
	}

	int descriptionsBeforeClean = 0;

	private void customizeDb() throws OCTException, Exception {

		if (!customCountryCodesToSignFor.isEmpty()) {
			for (String countryCode : customCountryCodesToSignFor) {
				countriesToSignFor.add(systemManager.getCountryByCode(countryCode));
			}
		} else {
			countriesToSignFor = allCountries;
		}
		descriptionsBeforeClean = allInitiativeDescriptions.size();
		prepareDb();
	}

	private void prepareDb() throws OCTException, Exception {
		List<Country> customCountries = new ArrayList<Country>();
		for (String customCC : customCountryCodesToSignFor) {
			customCountries.add(allCountriesMap.get(customCC));
		}
		insertedSignaturesMap = insertTestSignatures(customCountries, signaturePerCountry, AVOID_RANDOM_DISTRIBUTION);
		// add also the already mocked signatures
		for (Signature signature : allMockedSignatures) {
			String countryCode = signature.getCountryToSignFor().getCode();
			int countToBeUpdated = insertedSignaturesMap.get(countryCode);
			insertedSignaturesMap.put(countryCode, countToBeUpdated + 1);
		}
		for (String countryCode : insertedSignaturesMap.keySet()) {
			newSignaturesSize += insertedSignaturesMap.get(countryCode);
		}

		long currentSize = getProgressionStatus();
		logger.debug("Inserted " + newSignaturesSize + " new signatures for test.");
		assertEquals(newSignaturesSize, currentSize);

	}

}