package eu.europa.ec.eci.oct.webcommons.restApi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europa.ec.eci.oct.entities.admin.Feedback;
import eu.europa.ec.eci.oct.entities.admin.FeedbackRange;
import eu.europa.ec.eci.oct.entities.signature.Signature;
import eu.europa.ec.eci.oct.utils.CommonsConstants;
import eu.europa.ec.eci.oct.webcommons.commons.TestUtils;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.commons.StringsDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.report.DistributionMap;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.report.EvolutionMapDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.report.EvolutionMapEntryDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.report.EvolutionMapEntryDetailDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.report.FeedbackDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.report.FeedbackDTOs;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.report.FeedbackStatsDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.report.ProgressionStatus;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.signature.SignatureCountryCount;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.signature.SignatureDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.transformer.FeedbackTransformer;
import eu.europa.ec.eci.oct.webcommons.services.enums.FeedbackRangeEnum;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/rest-api-test.xml")
public class ReportingApiTest extends RestApiTest {

	public static final String REPORT = "/report";
	public static final int monthsOfCollection = 12;

	@Test
	public void rep01() throws Exception {
		Map<String, Integer> insertedSignaturesMap = insertTestSignatures(allCountries, 5, true);
		int newSignaturesSize = 0;
		for (String countryCode : insertedSignaturesMap.keySet()) {
			newSignaturesSize += insertedSignaturesMap.get(countryCode);
		}

		// get progression status
		ProgressionStatus progressionStatus = new ProgressionStatus();
		Long viewCount = 0l;

		try {
			Response response = target(REPORT + "/progression").request().get(Response.class);
			assertEquals(Status.OK.getStatusCode(), response.getStatus());
			progressionStatus = response.readEntity(ProgressionStatus.class);
			assertEquals(MOCK_CUSTOMISATIONS_SIGNATURE_GOAL.longValue(), progressionStatus.getGoal());
			viewCount = progressionStatus.getSignatureCount();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		assertEquals(EXPECTED_MOCKED_SIGNATURES_SIZE + newSignaturesSize, viewCount.intValue());
		removeNewSignatures();

	}

	@Test
	public void rep02() throws OCTException, Exception {
		Map<String, Integer> insertedSignaturesMap = insertTestSignatures(allCountries, 5, true);
		DistributionMap distributionMap = new DistributionMap();
		List<SignatureCountryCount> signatureCountryCountList = null;

		try {
			Response response = target(REPORT + "/map").request().get(Response.class);
			assertEquals(Status.OK.getStatusCode(), response.getStatus());
			distributionMap = response.readEntity(DistributionMap.class);
			assertNotNull(distributionMap);
			assertNotNull(distributionMap.getSignatureCountryCount());
			assertFalse(distributionMap.getSignatureCountryCount().isEmpty());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		signatureCountryCountList = distributionMap.getSignatureCountryCount();
		assertEquals(MOCK_COUNTRIES_SIZE, signatureCountryCountList.size());
		for (SignatureCountryCount signatureCountryCount : signatureCountryCountList) {
			String countryCode = signatureCountryCount.getCountryCode();
			assertTrue(StringUtils.isNotBlank(countryCode));
			int countPerCountry = (int) signatureCountryCount.getCount();
			assertNotNull(signatureCountryCount.getTreshold());
			if (countryCode.equals(MOCK_SIGNATURES_COUNTRY_FOR_CODE)) {
				assertEquals(insertedSignaturesMap.get(countryCode).intValue() + EXPECTED_MOCKED_SIGNATURES_SIZE,
						countPerCountry);
			} else {
				assertEquals(insertedSignaturesMap.get(countryCode).intValue(), countPerCountry);

			}
		}
		removeNewSignatures();
	}

	@Test
	public void rep03() throws Exception {

		FeedbackDTO notAllowedfeedbackDTO = new FeedbackDTO();
		notAllowedfeedbackDTO.setComment("I submit feedback without having supported");
		notAllowedfeedbackDTO.setSignatureIdentifier("unexistendUUId");
		notAllowedfeedbackDTO.setDate(MOCK_SIGNATURE1_DATE);
		notAllowedfeedbackDTO.setRange(
				FeedbackRangeEnum.BAD.getLabel().replace(FeedbackTransformer.FEEDBACK_RANGE_LABEL_PREFIX, ""));
		try {
			Response response = target(REPORT + "/feedback").request()
					.post(Entity.entity(notAllowedfeedbackDTO, MediaType.APPLICATION_JSON));
			assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		// insert a feedback for an already existent signature with associated feedback
		FeedbackDTO notAllowedAlreadySubmittedFeedbackDTO = new FeedbackDTO();
		notAllowedAlreadySubmittedFeedbackDTO.setComment("");
		notAllowedAlreadySubmittedFeedbackDTO.setSignatureIdentifier(MOCK_SIGNATURE1_UUID);
		notAllowedAlreadySubmittedFeedbackDTO.setDate(MOCK_SIGNATURE1_DATE);
		notAllowedAlreadySubmittedFeedbackDTO.setRange(
				FeedbackRangeEnum.BAD.getLabel().replace(FeedbackTransformer.FEEDBACK_RANGE_LABEL_PREFIX, ""));
		try {
			Response response = target(REPORT + "/feedback").request()
					.post(Entity.entity(notAllowedAlreadySubmittedFeedbackDTO, MediaType.APPLICATION_JSON));
			assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		// insert a signature to have a valid feedback submission
		String randomCountryCode = getRandomCountryCode();
		SignatureDTO signatureToDelete = TestUtils.buildTestSignature(randomCountryCode,
				isCategoryA(randomCountryCode));
		String newSignatureUUID = insertSignature(signatureToDelete, false);

		FeedbackDTO allowedfeedbackDTO = new FeedbackDTO();
		allowedfeedbackDTO.setComment("very very bad");
		allowedfeedbackDTO.setSignatureIdentifier(newSignatureUUID);
		allowedfeedbackDTO.setDate(MOCK_SIGNATURE1_DATE);
		allowedfeedbackDTO.setRange(
				FeedbackRangeEnum.BAD.getLabel().replace(FeedbackTransformer.FEEDBACK_RANGE_LABEL_PREFIX, ""));
		try {
			Response response = target(REPORT + "/feedback").request()
					.post(Entity.entity(allowedfeedbackDTO, MediaType.APPLICATION_JSON));
			assertEquals(Status.OK.getStatusCode(), response.getStatus());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		removeNewSignatures();
		restoreMockRestTestFeedbacks();
	}

	@Test
	public void rep04() throws Exception {
		Map<String, Integer> insertedSignaturesMap = insertTestSignatures(2);

		int newSignaturesSize = 0;
		for (String countryCode : insertedSignaturesMap.keySet()) {
			newSignaturesSize += insertedSignaturesMap.get(countryCode);
		}
		// default mock data test
		Response response = target(REPORT + "/evolutionMap").request().get(Response.class);
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		EvolutionMapDTO evolutionMapDTO = response.readEntity(EvolutionMapDTO.class);

		assertNotNull(evolutionMapDTO);
		List<EvolutionMapEntryDTO> evolutionMapEntryDTOs = evolutionMapDTO.getEvolutionMapEntryDTOs();
		assertNotNull(evolutionMapEntryDTOs);
		assertFalse(evolutionMapEntryDTOs.isEmpty());

		// all countries + "all" item
		assertEquals(allCountries.size() + 1, evolutionMapEntryDTOs.size());

		int overallTotal = 0;
		for (EvolutionMapEntryDTO emeDTO : evolutionMapEntryDTOs) {
			String countryCode = emeDTO.getCountryCode();
			long totalSupportersPerYear = emeDTO.getTotalSupporters();
			if (!countryCode.equals(CommonsConstants.ALL_COUNTRIES_KEY)) {
				assertTrue(allCountryCodes.contains(countryCode));
				overallTotal += totalSupportersPerYear;
			}
			String mostActiveMonth = emeDTO.getMostActiveMonth();
			String mostActiveMonthSynthax = "mm/yyyy";
			assertEquals(mostActiveMonthSynthax.length(), mostActiveMonth.length());
			List<EvolutionMapEntryDetailDTO> details = emeDTO.getEvolutionMapEntryDetailDTOs();
			assertEquals(monthsOfCollection + 1, details.size());
			long monthTotalCount = 0;
			long resultCount = 0;
			for (EvolutionMapEntryDetailDTO detail : details) {
				int month = detail.getMonth();
				long monthCount = detail.getCount();
				monthTotalCount += monthCount;
				assertTrue(month <= monthsOfCollection && month > 0);
				resultCount += monthCount;
			}
			if (!countryCode.equals(CommonsConstants.ALL_COUNTRIES_KEY)) {
				int expectedCount = insertedSignaturesMap.get(countryCode).intValue();
				if (allMockedSignaturesCountMap.containsKey(countryCode)) {
					/*
					 * the view is not discarding mocked signatures, we need to take them in count
					 */
					expectedCount += allMockedSignaturesCountMap.get(countryCode);
				}
				assertEquals(expectedCount, resultCount);
				assertEquals(totalSupportersPerYear, monthTotalCount);
			} else {
				assertEquals(newSignaturesSize + EXPECTED_MOCKED_SIGNATURES_SIZE, totalSupportersPerYear);
			}
		}
		assertEquals(newSignaturesSize + EXPECTED_MOCKED_SIGNATURES_SIZE, overallTotal);

		removeNewSignatures();
	}

	@Test
	public void rep04emptyDb() throws Exception {
		deleteSignatures();

		// default mock data test
		Response response = target(REPORT + "/evolutionMap").request().get(Response.class);
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		EvolutionMapDTO evolutionMapDTO = response.readEntity(EvolutionMapDTO.class);

		assertNotNull(evolutionMapDTO);
		List<EvolutionMapEntryDTO> evolutionMapEntryDTOs = evolutionMapDTO.getEvolutionMapEntryDTOs();
		assertNotNull(evolutionMapEntryDTOs);
		assertFalse(evolutionMapEntryDTOs.isEmpty());

		// all countries + "all" item
		assertEquals(allCountries.size() + 1, evolutionMapEntryDTOs.size());

		int overallTotal = 0;
		for (EvolutionMapEntryDTO emeDTO : evolutionMapEntryDTOs) {
			String countryCode = emeDTO.getCountryCode();
			long totalSupportersPerYear = emeDTO.getTotalSupporters();
			if (!countryCode.equals(CommonsConstants.ALL_COUNTRIES_KEY)) {
				assertTrue(allCountryCodes.contains(countryCode));
				overallTotal += totalSupportersPerYear;
			}
			String mostActiveMonth = emeDTO.getMostActiveMonth();
			String mostActiveMonthSynthax = "mm/yyyy";
			assertEquals(mostActiveMonthSynthax.length(), mostActiveMonth.length());
			List<EvolutionMapEntryDetailDTO> details = emeDTO.getEvolutionMapEntryDetailDTOs();
			assertEquals(monthsOfCollection + 1, details.size());
			long monthTotalCount = 0;
			long resultCount = 0;
			for (EvolutionMapEntryDetailDTO detail : details) {
				int month = detail.getMonth();
				long monthCount = detail.getCount();
				monthTotalCount += monthCount;
				assertTrue(month <= monthsOfCollection && month > 0);
				resultCount += monthCount;
			}
			if (!countryCode.equals(CommonsConstants.ALL_COUNTRIES_KEY)) {
				assertEquals(0, resultCount);
				assertEquals(totalSupportersPerYear, monthTotalCount);
			} else {
				assertEquals(0, totalSupportersPerYear);
			}
		}
		assertEquals(0, overallTotal);
		restoreMockRestTestSignatures();
	}

	@Test
	public void rep05() throws Exception {
		Map<String, Integer> insertedSignaturesMap = insertTestSignatures(10, ALLOW_RANDOM_DISTRIBUTION);
		// add also the already mocked signatures
		for (Signature signature : allMockedSignatures) {
			String countryCode = signature.getCountryToSignFor().getCode();
			int countToBeUpdated = insertedSignaturesMap.get(countryCode);
			insertedSignaturesMap.put(countryCode, countToBeUpdated + 1);
		}
		List<SignatureCountryCount> allCountriesCount = new ArrayList<SignatureCountryCount>();
		for (String countryCode : insertedSignaturesMap.keySet()) {
			SignatureCountryCount scc = new SignatureCountryCount();
			scc.setCountryCode(countryCode);
			Integer count = insertedSignaturesMap.get(countryCode);
			scc.setCount(count);
			long threshold = allCountriesMap.get(countryCode).getThreshold();
			scc.setTreshold(threshold);
			double percentage = 0;
			if (count > 0) {
				percentage = (count * 100) / threshold;
			}
			scc.setPercentage(percentage);
			allCountriesCount.add(scc);
		}
		Collections.sort(allCountriesCount);
		List<SignatureCountryCount> expectedTop7 = allCountriesCount.subList(0, 7);
		assertEquals(CommonsConstants.TOP_COUNTRIES_CHART_SIZE, expectedTop7.size());

		// get top 7 distribution map
		try {
			Response response = target(REPORT + "/top7map").request().get(Response.class);
			assertEquals(Status.OK.getStatusCode(), response.getStatus());
			DistributionMap distributionMap = response.readEntity(DistributionMap.class);
			assertNotNull(distributionMap);
			assertNotNull(distributionMap.getSignatureCountryCount());
			assertFalse(distributionMap.getSignatureCountryCount().isEmpty());

			List<SignatureCountryCount> signatureCountryCountList = distributionMap.getSignatureCountryCount();
			assertEquals(CommonsConstants.TOP_COUNTRIES_CHART_SIZE, signatureCountryCountList.size());

			logger.info("----------EXPECTED---------");
			for (SignatureCountryCount expectedSCC : expectedTop7) {
				assertTrue(StringUtils.isNotBlank(expectedSCC.getCountryCode()));
				assertNotNull(expectedSCC.getCount());
				assertNotNull(expectedSCC.getTreshold());
				logger.info(">> " + expectedSCC);
			}
			logger.info("----------ACTUAL---------");
			for (SignatureCountryCount actualSCC : signatureCountryCountList) {
				assertTrue(StringUtils.isNotBlank(actualSCC.getCountryCode()));
				assertNotNull(actualSCC.getCount());
				assertNotNull(actualSCC.getTreshold());
				logger.info(">>" + actualSCC);
			}
			for (int i = 0; i < CommonsConstants.TOP_COUNTRIES_CHART_SIZE; i++) {
				SignatureCountryCount expectedSCC = expectedTop7.get(i);
				SignatureCountryCount actualApiSCC = signatureCountryCountList.get(i);
				// logger.info("EXPECTED: " + expectedSCC);
				// logger.info("ACTUAL: " + actualApiSCC);
				assertEquals(expectedSCC, actualApiSCC);
			}

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		removeNewSignatures();
	}

	@Test
	public void rep06() throws Exception {
		// get feedbackStats
		try {
			Response response = target(REPORT + "/feedbacks").request()
					.header(HttpHeaders.AUTHORIZATION, BEARER + authorizationToken).get(Response.class);
			assertEquals(Status.OK.getStatusCode(), response.getStatus());
			FeedbackDTOs feedbackDTOs = response.readEntity(FeedbackDTOs.class);
			assertNotNull(feedbackDTOs);
			assertNotNull(feedbackDTOs.getFeedbackDTOlist());
			assertFalse(feedbackDTOs.getFeedbackDTOlist().isEmpty());

			List<FeedbackDTO> feedbackDTOlist = feedbackDTOs.getFeedbackDTOlist();
			List<Feedback> feedbackDAOlist = reportingService.getAllFeedbacks();
			assertEquals(feedbackDAOlist.size(), feedbackDAOlist.size());
			for (FeedbackDTO feedbackDTO : feedbackDTOlist) {
				assertNotNull(feedbackDTO.getDate());
				assertFalse(StringUtils.isBlank(feedbackDTO.getRange()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void rep06b() throws Exception {
		// get feedbackStats
		try {
			Response response = target(REPORT + "/feedbackStats").request()
					.header(HttpHeaders.AUTHORIZATION, BEARER + authorizationToken).get(Response.class);
			assertEquals(Status.OK.getStatusCode(), response.getStatus());
			FeedbackStatsDTO feedbackStatsDTO = response.readEntity(FeedbackStatsDTO.class);
			assertNotNull(feedbackStatsDTO);
			assertNotNull(feedbackStatsDTO.getBadCount());
			assertNotNull(feedbackStatsDTO.getFairCount());
			assertNotNull(feedbackStatsDTO.getFineCount());
			assertNotNull(feedbackStatsDTO.getGoodCount());
			assertNotNull(feedbackStatsDTO.getTotCount());

			List<Feedback> feedbackDAOlist = reportingService.getAllFeedbacks();
			assertTrue(feedbackDAOlist.size() == feedbackStatsDTO.getTotCount());
			long badCount = 0;
			long fairCount = 0;
			long fineCount = 0;
			long goodCount = 0;
			assertEquals(feedbackDAOlist.size(), feedbackDAOlist.size());
			for (Feedback feedback : feedbackDAOlist) {
				switch ((int) feedback.getFeedbackRange().getId()) {
				case (int) FeedbackRange.BAD:
					badCount++;
					break;
				case (int) FeedbackRange.FINE:
					fineCount++;
					break;
				case (int) FeedbackRange.FAIR:
					fairCount++;
					break;
				case (int) FeedbackRange.GOOD:
					goodCount++;
					break;
				default:
					fail("Unrecognized feedbackRange! " + feedback.getFeedbackRange());
				}
			}

			assertTrue(badCount == feedbackStatsDTO.getBadCount());
			assertTrue(fairCount == feedbackStatsDTO.getFairCount());
			assertTrue(fineCount == feedbackStatsDTO.getFineCount());
			assertTrue(goodCount == feedbackStatsDTO.getGoodCount());

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void rep07() {
		// get feedback range list
		try {
			Response response = target(REPORT + "/feedbackRange").request().get(Response.class);
			assertEquals(Status.OK.getStatusCode(), response.getStatus());
			StringsDTO stringsDTO = response.readEntity(StringsDTO.class);
			assertNotNull(stringsDTO);
			assertNotNull(stringsDTO.getValues());
			assertFalse(stringsDTO.getValues().isEmpty());

			List<String> feedbackRangeDTOlist = stringsDTO.getValues();
			List<FeedbackRange> feedbackRangeDAOlist = reportingService.getFeedbackRanges();
			assertEquals(feedbackRangeDAOlist.size(), feedbackRangeDTOlist.size());

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

	}
}