package eu.europa.ec.eci.oct.entities.views;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SIGBYCOUNTRYVIEW")
public class EvolutionMapByCountry implements Serializable {

	private static final long serialVersionUID = -6328955474123147737L;

	@Id
	@Column
	private Long sos;
	@Id
	@Column
	private Integer month;
	@Id
	@Column
	private Integer year;
	@Id
	@Column
	private String code;

	public EvolutionMapByCountry() {
	}

	public EvolutionMapByCountry(Long sos, Integer month, Integer year, String code) {
		this.sos = sos;
		this.month = month;
		this.year = year;
		this.code = code;
	}

	public Long getSos() {
		return sos;
	}

	public void setSos(Long sos) {
		this.sos = sos;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		EvolutionMapByCountry that = (EvolutionMapByCountry) o;

		if (sos != null ? !sos.equals(that.sos) : that.sos != null)
			return false;
		if (month != null ? !month.equals(that.month) : that.month != null)
			return false;
		if (year != null ? !year.equals(that.year) : that.year != null)
			return false;
		return code != null ? code.equals(that.code) : that.code == null;

	}

	@Override
	public int hashCode() {
		int result = sos != null ? sos.hashCode() : 0;
		result = 31 * result + (month != null ? month.hashCode() : 0);
		result = 31 * result + (year != null ? year.hashCode() : 0);
		result = 31 * result + (code != null ? code.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "EvolutionMapByCountry [sos=" + sos + ", month=" + month + ", year=" + year + ", code=" + code + "]";
	}

}
