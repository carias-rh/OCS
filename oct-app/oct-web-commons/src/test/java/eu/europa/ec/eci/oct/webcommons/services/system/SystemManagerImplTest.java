package eu.europa.ec.eci.oct.webcommons.services.system;

import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import eu.europa.ec.eci.oct.entities.admin.SystemPreferences;
import eu.europa.ec.eci.oct.entities.admin.SystemState;
import eu.europa.ec.eci.oct.entities.system.Country;
import eu.europa.ec.eci.oct.entities.system.Language;
import eu.europa.ec.eci.oct.webcommons.services.commons.ServicesTest;
import eu.europa.ec.eci.oct.webcommons.services.enums.CountryEnum;
import eu.europa.ec.eci.oct.webcommons.services.enums.LanguageEnum;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;

/**
 * User: franzmh Date: 14/09/16
 */
public class SystemManagerImplTest extends ServicesTest {

	@Autowired
	SystemManager systemManager;

	@Test
	public void getLanguageByCode() throws Exception {
		for (Language expectedLanguage : testAllLanguages) {
			String expectedLanguageCode = expectedLanguage.getCode();
			String expectedLanguageName = expectedLanguage.getName();
			String expectedLanguageLabel = expectedLanguage.getLabel();
			try {
				Language languageToFind = systemManager.getLanguageByCode(expectedLanguageCode);
				assertNotNull(languageToFind);
				assertEquals(expectedLanguageCode, languageToFind.getCode());
				assertEquals(expectedLanguageName, languageToFind.getName());
				assertEquals(expectedLanguageLabel, languageToFind.getLabel());
			} catch (Exception e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
		}
	}
	
	@Test
	public void getCountryByCode() throws Exception {
		for (Country expectedCountry : testCountries) {
			String expectedCountryCode = expectedCountry.getCode();
			String expectedCountryName = expectedCountry.getName();
			String expectedCountryLabel = expectedCountry.getLabel();
			try {
				Country countryToFind = systemManager.getCountryByCode(expectedCountryCode);
				assertNotNull(countryToFind);
				assertEquals(expectedCountryCode, countryToFind.getCode());
				assertEquals(expectedCountryName, countryToFind.getName());
				assertEquals(expectedCountryLabel, countryToFind.getLabel());
			} catch (Exception e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
		}
	}
	
	@Test
	public void getSystemPreferences() throws Exception {
		try {
			SystemPreferences sp = systemManager.getSystemPreferences();
			assertNotNull(sp);
			assertNotNull(sp.getCommissionRegisterUrl());
			assertNotNull(sp.getFileStoragePath());
			assertNotNull(sp.getRegistrationNumber());
			assertNotNull(sp.getDeadline());
			assertNotNull(sp.getId());
			assertNotNull(sp.getRegistrationDate());
			assertNotNull(sp.getState());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void setRegistrationData() throws OCTException {
		String newRegisterURL = "xxx";
		Date newRegistratonDate = new Date();
		String newRegistrationNumber = "1";
		Language newLanguage = systemManager
				.getLanguageByCode(LanguageEnum.BULGARIAN.getLangCode());
		testSystemPreferences.setCommissionRegisterUrl(newRegisterURL);
		testSystemPreferences.setRegistrationDate(newRegistratonDate);
		testSystemPreferences.setRegistrationNumber(newRegistrationNumber);
		
		try {
			systemManager.setRegistrationData(testSystemPreferences);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		SystemPreferences updatedRegistrationDate = systemManager.getSystemPreferences();
		assertEquals(newRegisterURL, updatedRegistrationDate.getCommissionRegisterUrl());
		assertEquals(newRegistratonDate, updatedRegistrationDate.getRegistrationDate());
		assertEquals(newRegistrationNumber, updatedRegistrationDate.getRegistrationNumber());
	}

    @Test
    public void isOnline() throws Exception{
        System.out.println(systemManager.getSystemPreferences().getState().name());
        assertFalse(systemManager.isOnline());
        testSystemPreferences.setState(SystemState.OPERATIONAL);
        assertTrue(systemManager.isOnline());
        testSystemPreferences.setState(SystemState.DEPLOYED);
    }

    @Test
    public void authenticate() throws Exception {
        boolean auth = systemManager.authenticate(USER, PWD);
        assertTrue(auth);
    }
    
    @Test
    public void thresholdConsistency() {
    	int matchedCountries = 0;
    	for(Country c : testCountries) {
    		CountryEnum cE = null;
    		try {
    			cE = CountryEnum.getByCode(c.getCode());
    			matchedCountries++;
    		}catch(Exception e) {
    			fail(e.getMessage());
    		}
    		assertEquals(cE.getTreshold(), c.getThreshold());
    	}
    	assertEquals(testCountriesSizeNumber, matchedCountries);
    }
    
}
