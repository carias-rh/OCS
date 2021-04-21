package eu.europa.ec.eci.oct.webcommons.services.reporting;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europa.ec.eci.oct.entities.admin.Feedback;
import eu.europa.ec.eci.oct.entities.admin.FeedbackRange;
import eu.europa.ec.eci.oct.entities.signature.Signature;
import eu.europa.ec.eci.oct.entities.views.EvolutionMapByCountry;
import eu.europa.ec.eci.oct.utils.CommonsConstants;
import eu.europa.ec.eci.oct.utils.DateUtils;
import eu.europa.ec.eci.oct.webcommons.commons.TestUtils;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.report.DistributionMap;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.signature.SignatureCountryCount;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.signature.SignatureDTO;
import eu.europa.ec.eci.oct.webcommons.services.commons.ServicesTest;
import eu.europa.ec.eci.oct.webcommons.services.enums.CountryEnum;
import eu.europa.ec.eci.oct.webcommons.services.enums.FeedbackRangeEnum;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;

@RunWith(SpringJUnit4ClassRunner.class)
public class ReportingServiceImplTest extends ServicesTest {

	@Test
	public void getEvolutionMapByCountry() {
		// mock default db: only france has 2 signatures
		List<EvolutionMapByCountry> progressionMaps = new ArrayList<EvolutionMapByCountry>();
		try {
			progressionMaps = reportingService.getEvolutionMapByCountry();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			fail(e.getMessage());
		}
		assertNotNull(progressionMaps);
		assertFalse(progressionMaps.isEmpty());
		assertTrue(progressionMaps.size() == testCountries.size() + 1);
		for (EvolutionMapByCountry progressionMap : progressionMaps) {
			assertNotNull(progressionMap.getMonth());
			assertNotNull(progressionMap.getYear());
			Long sos = progressionMap.getSos();
			assertNotNull(sos);
			String code = progressionMap.getCode();
			assertNotNull(code);
			if(code.equalsIgnoreCase(CountryEnum.FRANCE.getCode()) || code.equalsIgnoreCase(CommonsConstants.ALL_COUNTRIES_KEY)){
				assertTrue(testSignatures.size() == sos);
			}else{
				assertTrue(0 == sos);
			}
		}
	}

	@Test
	public void getAllFeedbacks() {

		List<Feedback> allFeedbacks = new ArrayList<Feedback>();
		try {
			allFeedbacks = reportingService.getAllFeedbacks();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		assertNotNull(allFeedbacks);
		assertFalse(allFeedbacks.isEmpty());
		assertEquals(testFeedbacks.size(), allFeedbacks.size());

		for (Feedback feedback : allFeedbacks) {
			assertNotNull(feedback.getId());
			assertNotNull(feedback.getFeedbackComment());
			assertNotNull(feedback.getFeedbackRange());
			assertNotNull(feedback.getFeedbackRange().getLabel());
			assertNotNull(feedback.getFeedbackRange().getDisplayOrder());
			assertNotNull(feedback.getFeedbackRange().getEnabled());
		}
	}

	@Test
	public void getAllFeedbackRanges() {

		List<FeedbackRange> feedbackRangeList = new ArrayList<FeedbackRange>();
		try {
			feedbackRangeList = reportingService.getFeedbackRanges();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		assertNotNull(feedbackRangeList);
		assertFalse(feedbackRangeList.isEmpty());
		assertEquals(testFeedbackRangeList.size(), feedbackRangeList.size());
		for (FeedbackRange feedbackRange : feedbackRangeList) {
//			System.err.println("iterating: " + feedbackRange);
			int id = (int) feedbackRange.getId();
			String label = "";
			int displayOrder = -1;
			int enabled = -1;
			switch (id) {
			case 1:
				label = FeedbackRangeEnum.HORRIBLE.getLabel();
				displayOrder = FeedbackRangeEnum.HORRIBLE.getDisplayOrder();
				enabled = FeedbackRangeEnum.HORRIBLE.getEnabled();
				break;
			case 2:
				label = FeedbackRangeEnum.BAD.getLabel();
				displayOrder = FeedbackRangeEnum.BAD.getDisplayOrder();
				enabled = FeedbackRangeEnum.BAD.getEnabled();
				break;
			case 3:
				label = FeedbackRangeEnum.FAIR.getLabel();
				displayOrder = FeedbackRangeEnum.FAIR.getDisplayOrder();
				enabled = FeedbackRangeEnum.FAIR.getEnabled();
				break;
			case 4:
				label = FeedbackRangeEnum.FINE.getLabel();
				displayOrder = FeedbackRangeEnum.FINE.getDisplayOrder();
				enabled = FeedbackRangeEnum.FINE.getEnabled();
				break;
			case 5:
				label = FeedbackRangeEnum.GOOD.getLabel();
				displayOrder = FeedbackRangeEnum.GOOD.getDisplayOrder();
				enabled = FeedbackRangeEnum.GOOD.getEnabled();
				break;
			case 6:
				label = FeedbackRangeEnum.GREAT.getLabel();
				displayOrder = FeedbackRangeEnum.GREAT.getDisplayOrder();
				enabled = FeedbackRangeEnum.GREAT.getEnabled();
				break;
			default:
				fail("Unknown feedbackRange!");
			}
			assertEquals(label, feedbackRange.getLabel());
			assertEquals(displayOrder, feedbackRange.getDisplayOrder());
			assertEquals(enabled, feedbackRange.getEnabled());
		}
	}

	@Test
	public void saveFeedback() throws Exception {
		// test bad input
		Feedback badFeeback = null;
		try {
			reportingService.saveFeedback(badFeeback);
			fail("Null feedback should have been rejected from saving");
		} catch (Exception e) {
		}
		badFeeback = new Feedback();
		try {
			reportingService.saveFeedback(badFeeback);
			fail("Null feedback attributes should have been rejected from saving");
		} catch (Exception e) {
		}
		badFeeback.setFeedbackComment("ok");
		try {
			reportingService.saveFeedback(badFeeback);
			fail("Null feedback range should have been rejected from saving");
		} catch (Exception e) {
		}
		// test bad feedbackRange
		FeedbackRange badFeedbackRange = new FeedbackRange();
		badFeedbackRange.setId(999);
		badFeeback.setFeedbackRange(badFeedbackRange);
		try {
			reportingService.saveFeedback(badFeeback);
			fail("Feedback with a not existent feedbackRange should have been rejected from saving");
		} catch (Exception e) {
		}

		int feedbacksBeforeSaving = testFeedbacks.size();

		Feedback feedbackToSaveNoUUID = new Feedback();
		FeedbackRange feedbackRange = testFeedbackRangeList.get(0);
		feedbackToSaveNoUUID.setFeedbackComment(feedbackRange.getLabel());
		feedbackToSaveNoUUID.setFeedbackRange(feedbackRange);
		try {
			reportingService.saveFeedback(feedbackToSaveNoUUID);
			fail("A feedback with no signature identifier was able to be persisted");
		} catch (Exception e) {
			// expected behaviour
		}

		// insert a new signature for a new feedback
		Signature newSignature = insertTestSignature();
		Feedback feedbackToSaveWithUUID = new Feedback();
		feedbackToSaveWithUUID.setFeedbackComment(feedbackRange.getLabel());
		feedbackToSaveWithUUID.setFeedbackRange(feedbackRange);
		feedbackToSaveWithUUID.setSignatureIdentifier(newSignature.getUuid());
		try {
			reportingService.saveFeedback(feedbackToSaveWithUUID);
		} catch (Exception e) {
			fail(e.getMessage());
		}

		// try again with same signatureUUID
		try {
			reportingService.saveFeedback(feedbackToSaveWithUUID);
			fail("A feedback already assigned to a signature UUID was able to be persisted");
		} catch (Exception e) {
			// expected behaviour
		}

		List<Feedback> allFeedbacks = reportingService.getAllFeedbacks();
		for (Feedback feedback : allFeedbacks) {
			// System.err.println("SAVED FEEDBACK = " + feedback);
			assertNotNull(feedback.getId());
			assertNotNull(feedback.getFeedbackDate());
			assertNotNull(feedback.getFeedbackComment());
			assertNotNull(feedback.getFeedbackRange());
			assertNotNull(feedback.getFeedbackRange().getLabel());
			assertNotNull(feedback.getFeedbackRange().getDisplayOrder());
			assertNotNull(feedback.getFeedbackRange().getEnabled());
			assertNotNull(feedback.getFeedbackRange().getId());
			assertNotNull(feedback.getSignatureIdentifier());
		}
		int feedbacksAfterSaving = allFeedbacks.size();
		assertEquals(feedbacksBeforeSaving + 1, feedbacksAfterSaving);

	}

	@Test
	public void getTop7DistributionMap() throws Exception {
		
		List<SignatureDTO> signaturesForTest = TestUtils.buildTestSignatures(testCountryProperties, testCountries, 5, true);
//		int i = 1;
		for (SignatureDTO sDTO : signaturesForTest) {
			try {
				Signature s = signatureTransformer.transform(sDTO);
				Date randomDate = DateUtils.getRandomDateBetween(MOCKED_START_DATE, MOCKED_DEADLINE);
				assertTrue(DateUtils.isBetween(MOCKED_START_DATE, MOCKED_DEADLINE, randomDate));

//				logger.info("Persisting signature #"+i+" of "+signaturesForTest.size());
//				System.err.println(s);
				signatureService.insertSignature(s, randomDate);
//				i++;
			} catch (Exception e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
		}
//		logger.info("persisting finished...");
		assertTrue(signatureService.getAllSignatures().size() >= signaturesForTest.size());
		DistributionMap top7DistributionMap = null;
		try {
			top7DistributionMap = reportingService.getTop7DistributionMap();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		assertNotNull(top7DistributionMap);
		assertNotNull(top7DistributionMap.getSignatureCountryCount());
		assertFalse(top7DistributionMap.getSignatureCountryCount().isEmpty());
//		System.err.println(top7DistributionMap);
		assertTrue(top7DistributionMap.getSignatureCountryCount().size() == 7);

		for (SignatureCountryCount scc : top7DistributionMap.getSignatureCountryCount()) {
//			System.err.println(scc);
			assertFalse(StringUtils.isBlank(scc.getCountryCode()));
			assertNotNull(scc.getCount());
			assertNotNull(scc.getTreshold());
		}

	}
	
	@Test
	public void deleteAllFeedbacks() throws OCTException{
		List<Feedback> allFeedbacks = reportingService.getAllFeedbacks();
		if(allFeedbacks.isEmpty()){
			List<Feedback> randomFeedbacks = TestUtils.buildTestFeedbacks(10, reportingService.getFeedbackRanges());
			for (Feedback feedback : randomFeedbacks) {
				reportingService.saveFeedback(feedback);
			}
			allFeedbacks = reportingService.getAllFeedbacks();
		}
		assertFalse(allFeedbacks.isEmpty());
		
		try{
			reportingService.deleteAllFeedbacks();
		}catch(Exception e){
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		allFeedbacks = reportingService.getAllFeedbacks();
		assertTrue(allFeedbacks.isEmpty());
	}
}
