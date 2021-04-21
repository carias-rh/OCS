package eu.europa.ec.eci.oct.entities.export;

import java.io.Serializable;
import java.util.List;

public class ExportCount implements Serializable {
	
	public ExportCount(){}

	private static final long serialVersionUID = -8736822138917230256L;

	private long total;
	private List<ExportCountPerCountry> exportCountPerCountry;

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public List<ExportCountPerCountry> getExportCountPerCountry() {
		return exportCountPerCountry;
	}

	public void setExportCountPerCountry(List<ExportCountPerCountry> exportCountPerCountry) {
		this.exportCountPerCountry = exportCountPerCountry;
	}

	@Override
	public String toString() {
		return "ExportCount [total=" + total + ", countries=" + exportCountPerCountry + "]";
	}

}
