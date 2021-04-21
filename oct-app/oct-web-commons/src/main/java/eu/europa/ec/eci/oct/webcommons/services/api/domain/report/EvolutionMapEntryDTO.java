package eu.europa.ec.eci.oct.webcommons.services.api.domain.report;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class EvolutionMapEntryDTO implements Comparable<EvolutionMapEntryDTO>, Serializable {

	private static final long serialVersionUID = -1714692621655930669L;
	public static final String ALL_COUNTRIES_EVOLUTION_MAP_KEY = "all";

	private String countryCode;

	private Long totalSupporters;

	private String mostActiveMonth;

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	private List<EvolutionMapEntryDetailDTO> evolutionMapEntryDetailDTOs;

	public Long getTotalSupporters() {
		return totalSupporters;
	}

	public void setTotalSupporters(Long totalSupporters) {
		this.totalSupporters = totalSupporters;
	}

	public String getMostActiveMonth() {
		return mostActiveMonth;
	}

	public void setMostActiveMonth(String mostActiveMonth) {
		this.mostActiveMonth = mostActiveMonth;
	}

	public List<EvolutionMapEntryDetailDTO> getEvolutionMapEntryDetailDTOs() {
		return evolutionMapEntryDetailDTOs;
	}

	public void setEvolutionMapEntryDetailDTOs(List<EvolutionMapEntryDetailDTO> evolutionMapEntryDetailDTOs) {
		this.evolutionMapEntryDetailDTOs = evolutionMapEntryDetailDTOs;
	}

	@Override
	public String toString() {
		return "EvolutionMapEntryDTO [countryCode=" + countryCode + ", totalSupporters=" + totalSupporters + ", mostActiveMonth=" + mostActiveMonth + ", evolutionMapEntryDetailDTOs="
				+ evolutionMapEntryDetailDTOs + "]";
	}

	@Override
	public int compareTo(EvolutionMapEntryDTO eme) {
		if (eme.getCountryCode().compareTo(countryCode) < 0) {
			return 1;
		} else if (eme.getCountryCode().compareTo(countryCode) > 0) {
			return -1;
		} else {
			return 0;
		}
	}

}
