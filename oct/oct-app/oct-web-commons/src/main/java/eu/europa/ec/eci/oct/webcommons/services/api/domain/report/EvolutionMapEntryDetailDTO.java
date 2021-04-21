package eu.europa.ec.eci.oct.webcommons.services.api.domain.report;

import java.io.Serializable;

import org.springframework.stereotype.Component;

@Component
public class EvolutionMapEntryDetailDTO implements Serializable{

	private static final long serialVersionUID = 5010478119208912727L;
	
	private Long count;
	private int month;
	private int year;

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	@Override
	public String toString() {
		return "EvolutionMapEntryDetailDTO [count=" + count + ", month=" + month + ", year=" + year + "]";
	}

}
