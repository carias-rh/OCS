package eu.europa.ec.eci.oct.webcommons.services.api.transformer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import eu.europa.ec.eci.oct.entities.admin.SystemPreferences;
import eu.europa.ec.eci.oct.entities.views.EvolutionMapByCountry;
import eu.europa.ec.eci.oct.utils.CommonsConstants;
import eu.europa.ec.eci.oct.utils.DateUtils;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.report.EvolutionMapDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.report.EvolutionMapEntryDTO;
import eu.europa.ec.eci.oct.webcommons.services.api.domain.report.EvolutionMapEntryDetailDTO;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;

@Component
public class ReportingTransformer extends BaseTransformer {

	public EvolutionMapDTO transform(List<EvolutionMapByCountry> evolutionMapsByCountry) throws OCTException {

		SystemPreferences systemPreferences = systemManager.getSystemPreferences();
		Date deadlineDate = systemPreferences.getDeadline();
		Calendar registrationCalendar = Calendar.getInstance();
		registrationCalendar.setTime(deadlineDate);
		int registrationMonth = registrationCalendar.get(Calendar.MONTH) +1;
		int registrationYear = registrationCalendar.get(Calendar.YEAR) - 1;
		Date startDate = registrationCalendar.getTime();
		/*
		 * create the map container for all the details per country plus the all object
		 * with default zero values but structured month/year to be filled later
		 */
		Map<String, Map<Integer, Map<Integer, Long>>> timeMapDefaultValuesContainerByCountry = getTimeMapDefaultValuesContainer(
				startDate, deadlineDate, registrationMonth, registrationYear);

		/* fill the supporters value for each country matching month/year */
		EvolutionMapDTO evolutionMapDTO = new EvolutionMapDTO();
		List<EvolutionMapEntryDTO> evolutionMapEntryDTOs = new ArrayList<EvolutionMapEntryDTO>();
		for (EvolutionMapByCountry embc : evolutionMapsByCountry) {
			Long supporters = embc.getSos();
			Integer month = embc.getMonth();
			if (month != null) {
				Integer year = embc.getYear();
				String countryCode = embc.getCode();
				timeMapDefaultValuesContainerByCountry.get(countryCode).get(year).put(month, supporters);
				Long countToBeIncreased = 0L;
				if (timeMapDefaultValuesContainerByCountry.get(CommonsConstants.ALL_COUNTRIES_KEY).get(year)
						.get(month) != null) {
					countToBeIncreased += timeMapDefaultValuesContainerByCountry.get(CommonsConstants.ALL_COUNTRIES_KEY)
							.get(year).get(month);
				}
				timeMapDefaultValuesContainerByCountry.get(CommonsConstants.ALL_COUNTRIES_KEY).get(year).put(month,
						countToBeIncreased + supporters);
				/*
				 * else{ no sos for that country for month/year : default 0 from the container
				 * map }
				 */
			}
		}

		/* build the DTO structure based on the map values */
		for (String countryCode : timeMapDefaultValuesContainerByCountry.keySet()) {
			EvolutionMapEntryDTO emeDTO = new EvolutionMapEntryDTO();
			List<EvolutionMapEntryDetailDTO> evolutionMapEntryDetailDTOs = new ArrayList<EvolutionMapEntryDetailDTO>();

			int mostActiveMonth = 1;
			int mostActiveYear = registrationYear;
			long totalSupporters = 0L;
			long lastMonthlySupporters = 0L;
			for (Integer year : timeMapDefaultValuesContainerByCountry.get(countryCode).keySet()) {
				for (Integer month : timeMapDefaultValuesContainerByCountry.get(countryCode).get(year).keySet()) {
					Long monthlySupporters = timeMapDefaultValuesContainerByCountry.get(countryCode).get(year)
							.get(month);
					totalSupporters += monthlySupporters;
					if (monthlySupporters > lastMonthlySupporters) {
						mostActiveMonth = month;
						mostActiveYear = year;
						lastMonthlySupporters = monthlySupporters;
					}
					EvolutionMapEntryDetailDTO emedDTO = new EvolutionMapEntryDetailDTO();
					emedDTO.setCount(monthlySupporters);
					emedDTO.setYear(year);
					emedDTO.setMonth(month);
					evolutionMapEntryDetailDTOs.add(emedDTO);
				}
			}
			emeDTO.setCountryCode(countryCode);
			emeDTO.setTotalSupporters(totalSupporters);
			emeDTO.setEvolutionMapEntryDetailDTOs(evolutionMapEntryDetailDTOs);

			emeDTO.setMostActiveMonth(DateUtils.formatString(mostActiveMonth, mostActiveYear));
			evolutionMapEntryDTOs.add(emeDTO);
		}

		evolutionMapDTO.setEvolutionMapEntryDTOs(evolutionMapEntryDTOs);

		return evolutionMapDTO;
	}

	/**
	 * @return Create a map structure for all the countries, for all the years and
	 *         for all the month included in the collection period
	 * @throws OCTException
	 */
	private Map<String, Map<Integer, Map<Integer, Long>>> getTimeMapDefaultValuesContainer(Date registrationDate,
			Date deadlineDate, int registrationMonth, int registrationYear) throws OCTException {

		List<String> countryCodes = systemManager.getAllCountryCodes();

		Calendar deadlineCalendar = Calendar.getInstance();
		deadlineCalendar.setTime(deadlineDate);
		int deadlineMonth = deadlineCalendar.get(Calendar.MONTH) + 1;
		int deadlineYear = deadlineCalendar.get(Calendar.YEAR);
		Map<String, Map<Integer, Map<Integer, Long>>> countryDetailDTOmap = new HashMap<String, Map<Integer, Map<Integer, Long>>>();
		countryCodes.add(CommonsConstants.ALL_COUNTRIES_KEY);
		for (String countryCode : countryCodes) {
			countryDetailDTOmap.put(countryCode,
					getCalendarMap(registrationMonth, registrationYear, deadlineMonth, deadlineYear));
		}

		return countryDetailDTOmap;
	}

	private Map<Integer, Map<Integer, Long>> getCalendarMap(int registrationMonth, int registrationYear,
			int deadlineMonth, int deadlineYear) {

		Map<Integer, Map<Integer, Long>> yearMap = new HashMap<Integer, Map<Integer, Long>>();
		Map<Integer, Long> monthMapReg = new HashMap<Integer, Long>();
		Map<Integer, Long> monthMapDed = new HashMap<Integer, Long>();

		for (int ri = registrationYear; ri <= deadlineYear; ri++) {
			if (ri < deadlineYear) {
				for (int rm = registrationMonth; rm <= 12; rm++) {
					monthMapReg.put(rm, 0L);
					yearMap.put(ri, monthMapReg);
				}
			} else {
				for (int rm = 1; rm <= deadlineMonth; rm++) {
					monthMapDed.put(rm, 0L);
					yearMap.put(ri, monthMapDed);
				}
			}
		}
		return yearMap;
	}

}
