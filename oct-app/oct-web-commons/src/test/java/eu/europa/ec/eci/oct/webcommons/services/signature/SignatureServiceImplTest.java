package eu.europa.ec.eci.oct.webcommons.services.signature;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.eci.oct.entities.CountryProperty;
import eu.europa.ec.eci.oct.entities.PropertyGroup;
import eu.europa.ec.eci.oct.entities.export.ExportCount;
import eu.europa.ec.eci.oct.entities.export.ExportCountPerCountry;
import eu.europa.ec.eci.oct.entities.signature.Signature;
import eu.europa.ec.eci.oct.entities.system.Country;
import eu.europa.ec.eci.oct.export.utils.ExportParameter;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.signature.SignatureDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.supportForm.SupportFormDTO;
import eu.europa.ec.eci.oct.webcommons.services.commons.ServicesTest;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTDuplicateSignatureException;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTParameterException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/services-test.xml")
@Transactional
public class SignatureServiceImplTest extends ServicesTest {

	// the signature is used for delete/retreival and is mapped in the DBunit
	// dataset file : oct_signature.xml
	private Signature signature = new Signature();
	{
		signature.setId(1L);
		signature.setUuid("f616d20d-f5b0-48f2-983a-12dd539fea60");
		Country c = new Country();
		c.setId(4L);
		c.setCode("fr");
		signature.setCountryToSignFor(c);
	}

	@Test
	public void getPropertyGroups() {
		logger.info(">> Started getPropertyGroups test");
		// @formatter:off
		/*
		 * by default, we have 3 entries in the oct_property_group table:
		 * -------------------------------------------------------------- | id |
		 * multichoice | name | priority|
		 * -------------------------------------------------------------- | 1 | 0 |
		 * oct.group.general | 2 | | 2 | 0 | oct.group.address | 3 | | 3 | 1 |
		 * oct.group.id | 1 |
		 * --------------------------------------------------------------
		 */
		// @formatter:on
		List<PropertyGroup> propertyGroups = new ArrayList<PropertyGroup>();
		try {
			propertyGroups = signatureService.getPropertyGroups();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		assertNotNull(propertyGroups);
		assertFalse(propertyGroups.isEmpty());
		assertEquals(propertyGroups.size(), 3);

		for (PropertyGroup pg : propertyGroups) {
			int pgId = pg.getId().intValue();
			switch (pgId) {
			case 1:
				assertEquals("oct.group.general", pg.getName());
				assertEquals(2, pg.getPriority());
				assertFalse(pg.isMultichoice());
				break;
			case 2:
				assertEquals("oct.group.address", pg.getName());
				assertEquals(3, pg.getPriority());
				assertFalse(pg.isMultichoice());
				break;
			case 3:
				assertEquals("oct.group.id", pg.getName());
				assertEquals(1, pg.getPriority());
				assertTrue(pg.isMultichoice());
				break;
			default:
				fail("Unexpected value for pgId = " + pgId);
			}
		}
		logger.info(">> Ended getPropertyGroups test");
	}

	@Test
	public void getProperties() {
		logger.info(">> Started getProperties test");

		// for each country
		for (Country country : testCountries) {
			long countryId = country.getId();

			/*
			 * get the property list for the chosen country, for each property group check
			 * if the return list is coherent
			 */
			for (PropertyGroup pg : testPropertyGroups) {
				List<CountryProperty> countryProperties = new ArrayList<CountryProperty>();
				try {
					countryProperties = signatureService.getProperties(country, pg);
				} catch (Exception e) {
					e.printStackTrace();
					fail(e.getMessage());
				}
				assertNotNull(countryProperties);
				/*
				 * is it allowed for a country to have an empty list of countryProperties for a
				 * specified propertyGroup
				 */
				// assertFalse(countryProperties.isEmpty());

				for (CountryProperty cp : countryProperties) {
					/*
					 * check if the countryProperty is existent, coherent and correctly associated
					 * with the right country
					 */
					assertNotNull(cp.getId());
					assertTrue(countryId == cp.getCountry().getId());
					assertNotNull(cp.getCountry());
					assertNotNull(cp.getProperty());
				}
			}
		}
		logger.info(">> Ended getProperties test");
	}

	@Test
	public void getAllCountryProperties() {
		logger.info(">> Started getAllCountryProperties test");
		List<CountryProperty> allCountryProperties = new ArrayList<CountryProperty>();
		try {
			allCountryProperties = signatureService.getAllCountryProperties();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		assertNotNull(allCountryProperties);
		assertFalse(allCountryProperties.isEmpty());

		for (CountryProperty cp : allCountryProperties) {
			assertNotNull(cp.getId());
			assertNotNull(cp.getCountry());
			assertNotNull(cp.getProperty());
			assertNotNull(cp.isRequired());
		}

		logger.info(">> Ended getAllCountryProperties test");
	}

	@Test
	public void getCountryPropertyById() {
		logger.info(">> Started getCountryPropertyById test");

		for (Country country : testCountries) {
			for (CountryProperty cp : testCountryPropertiesMap.get(country)) {
				long countryPropertyId = cp.getId();
				CountryProperty countryPropertyById = null;
				try {
					countryPropertyById = signatureService.getCountryPropertyById(countryPropertyId);
				} catch (Exception e) {
					e.printStackTrace();
					fail(e.getMessage());
				}
				assertNotNull(countryPropertyById);
				assertEquals(cp, countryPropertyById);
			}
		}

		logger.info(">> Ended getCountryPropertyById test");
	}

	@Test
	public void insertSignature() throws Exception {
		logger.info(">> Started insertSignature test");

		/* try to insert a new one */
		SignatureDTO newSignatureDTO = new SignatureDTO();
		Country countryToSignFor = testCountries.get(0);
		newSignatureDTO.setCountry(countryToSignFor.getCode());
		List<CountryProperty> countryProperties = signatureService
				.getCountryPropertiesByCountryCode(countryToSignFor.getCode());
		List<SupportFormDTO> propertyValues = new ArrayList<SupportFormDTO>();
		for (CountryProperty cp : countryProperties) {
			SupportFormDTO pv = new SupportFormDTO();
			pv.setId(cp.getId());
			pv.setValue(cp.getProperty().getName().replace(SupportFormDTO.OCT_PROPERTY_PREFIX, "") + "TEST");
			pv.setLabel(cp.getProperty().getName().replace(SupportFormDTO.OCT_PROPERTY_PREFIX, ""));
			propertyValues.add(pv);
		}
		newSignatureDTO.setProperties(propertyValues);
		String persistedSignatureUUID = null;
		try {
			Signature newSignature = signatureTransformer.transform(newSignatureDTO);
			persistedSignatureUUID = signatureService.insertSignature(newSignature);
			assertFalse(StringUtils.isBlank(persistedSignatureUUID));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		Signature persistedNewSignature = signatureService.findByUuid(persistedSignatureUUID);
		assertNotNull(persistedNewSignature);
		assertNotNull(persistedNewSignature.getId());
		assertNotNull(persistedNewSignature.getUuid());
		assertNotNull(persistedNewSignature.getFingerprint());
		assertEquals(persistedNewSignature.getCountryToSignFor(), countryToSignFor);
		assertNotNull(persistedNewSignature.getSignatoryInfo());

		/* try to insert the already existent signature */
		SignatureDTO newSignatureCloneDTO = new SignatureDTO();
		newSignatureCloneDTO.setCountry(persistedNewSignature.getCountryToSignFor().getCode());
		newSignatureCloneDTO.setProperties(newSignatureDTO.getProperties());

		Signature newSignatureClone = null;
		try {
			newSignatureClone = signatureTransformer.transform(newSignatureCloneDTO);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		try {
			signatureService.insertSignature(newSignatureClone);
			fail("can't insert the same signature again!");
		} catch (Exception e) {
			/* expected DuplicateSignatureException */
			assertEquals(OCTDuplicateSignatureException.class, e.getClass());
		}

		/* try to insert again with different values */
		/* try to insert a new one */
		SignatureDTO newSignatureDTO2 = new SignatureDTO();
		newSignatureDTO2.setCountry(countryToSignFor.getCode());
		List<CountryProperty> countryProperties2 = signatureService
				.getCountryPropertiesByCountryCode(countryToSignFor.getCode());
		List<SupportFormDTO> propertyValues2 = new ArrayList<SupportFormDTO>();
		for (CountryProperty cp : countryProperties2) {
			SupportFormDTO pv = new SupportFormDTO();
			pv.setId(cp.getId());
			pv.setValue(cp.getProperty().getName().replace(SupportFormDTO.OCT_PROPERTY_PREFIX, "") + "TEST2");
			pv.setLabel(cp.getProperty().getName().replace(SupportFormDTO.OCT_PROPERTY_PREFIX, ""));
			propertyValues2.add(pv);
		}
		newSignatureDTO2.setProperties(propertyValues2);

		String persistedSignatureUUID2 = null;
		try {
			Signature newSignature2 = signatureTransformer.transform(newSignatureDTO2);
			persistedSignatureUUID2 = signatureService.insertSignature(newSignature2);
			assertFalse(StringUtils.isBlank(persistedSignatureUUID2));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		Signature persistedNewSignature2 = signatureService.findByUuid(persistedSignatureUUID2);
		assertNotNull(persistedNewSignature2);
		assertNotNull(persistedNewSignature2.getId());
		assertNotNull(persistedNewSignature2.getUuid());
		assertNotNull(persistedNewSignature2.getFingerprint());
		assertEquals(persistedNewSignature2.getCountryToSignFor(), countryToSignFor);
		assertNotNull(persistedNewSignature2.getSignatoryInfo());
		logger.info(">> Ended insertSignature test");
	}

	@Test
	public void deleteSignature() throws OCTException {
		logger.info(">> Started deleteSignature test");

		/* count how many signature are stored before deleting: 2 */
		List<Signature> signaturesPerCountryBeforeDeleting = signatureService.getAllSignatures();
		assertFalse(signaturesPerCountryBeforeDeleting.isEmpty());
		assertEquals(testSignatures.size(), signaturesPerCountryBeforeDeleting.size());

		/* delete the existing signature */
		try {
			signatureService.deleteSignature(testSignatures.get(0));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		/* count how many signature are stored after deleting */
		long signaturesPerCountryAfterDeleting = signatureService.getAllSignatures().size();
		assertEquals(testSignatures.size() - 1, signaturesPerCountryAfterDeleting);

		logger.info(">> Ended deleteSignature test");
	}

	@Test
	public void deleteAllSignature() throws OCTException {
		logger.info(">> Started deleteAllSignature test");

		/* count how many signature are stored before deleting */
		ExportParameter exportParameter = new ExportParameter();
		exportParameter.setCountryCode(signature.getCountryToSignFor().getCode());
		long signaturesPerCountryBeforeDeleting = signatureService.getExportCount(exportParameter).getTotal();
		assertEquals(2L, signaturesPerCountryBeforeDeleting);

		/* delete all the existing signature */
		try {
			signatureService.deleteAllSignatures();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		/* count how many signature are stored after deleting */
		List<Signature> allSignatures = signatureService.getAllSignatures();
		assertEquals(0, allSignatures.size());

		logger.info(">> Ended deleteAllSignature test");
	}

	@Test
	public void getSignatureCounts() throws Exception {
		logger.info(">> Started getSignatureCounts test");

		/* bad input test: we expect zero */
		ExportParameter exportParameter = new ExportParameter();
		exportParameter.setCountryCode("XX");
		long signaturesPerCountry = signatureService.getExportCount(exportParameter).getTotal();
		assertTrue(signaturesPerCountry == 0L);

		/* count how many signature are stored: we expect 2 */
		String testCountryToSignForCode = signature.getCountryToSignFor().getCode();
		exportParameter.getCountries().clear();
		exportParameter.setCountryCode(testCountryToSignForCode);
		signaturesPerCountry = signatureService.getExportCount(exportParameter).getTotal();
		assertTrue(2L == signaturesPerCountry);

		/* count how many signature are stored for another country: we expect 0 */
		String currentCountryCodeForSignature = signature.getCountryToSignFor().getCode();
		for (Country c : testCountries) {
			if (!c.getCode().equalsIgnoreCase(currentCountryCodeForSignature)) {
				exportParameter.getCountries().clear();
				exportParameter.setCountryCode(c.getCode());
				break;
			}
		}
		assertNotSame(currentCountryCodeForSignature, exportParameter.getCountries().get(0));
	}

	@Test
	public void findByUuid() throws OCTException {
		logger.info(">> Started findByUuid test");

		Signature signatureByUuid = null;

		try {
			signatureByUuid = signatureService.findByUuid(signature.getUuid());
		} catch (Exception e) {
			fail(e.getMessage());
		}
		assertNotNull(signatureByUuid);
		assertEquals(signature.getUuid(), signatureByUuid.getUuid());

		logger.info(">> Ended findByUuid test");
	}

	@Test
	public void getLastSignatures() throws Exception {

		int allSignatures = signatureService.getAllSignatures().size();

		// test bad input first
		List<Signature> lastSignatures = new ArrayList<Signature>();
		try {
			lastSignatures = signatureService.getLastSignatures();
		} catch (Exception e) {
			if (!(e instanceof OCTParameterException)) {
				fail("an invalid parameter ranger was accepted");
			}
		}

		assertTrue(lastSignatures.size() <= 5);
		assertTrue(lastSignatures.size() <= allSignatures);
	}

	@Test
	public void testSignaturesByCountryExport() throws Exception {
		ExportParameter exportParameter = new ExportParameter();
		for (Country country : testCountries) {
			exportParameter.setCountryCode(country.getCode());
		}
		ExportCount exportCount = signatureService.getExportCount(exportParameter);
		assertEquals(exportCount.getExportCountPerCountry().size(), testCountries.size());

		for (ExportCountPerCountry exportCountPerCountry : exportCount.getExportCountPerCountry()) {
			String testCountryToSignForCode = signature.getCountryToSignFor().getCode();
			if (exportCountPerCountry.getCountryCode().equals(testCountryToSignForCode)) {
				assertEquals(testCountryToSignForCode, exportCountPerCountry.getCountryCode());
				assertEquals(testSignatures.size(), exportCountPerCountry.getCount());
			} else {
				assertTrue(0 == exportCountPerCountry.getCount());
			}
		}
	}

}
