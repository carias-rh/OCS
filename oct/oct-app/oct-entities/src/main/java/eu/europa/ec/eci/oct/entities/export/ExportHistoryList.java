package eu.europa.ec.eci.oct.entities.export;

import java.io.Serializable;
import java.util.List;

public class ExportHistoryList implements Serializable {

	private static final long serialVersionUID = -6428415640067930798L;

	private List<ExportHistory> exportHistoryList;

	public List<ExportHistory> getExportHistoryList() {
		return exportHistoryList;
	}

	public void setExportHistoryList(List<ExportHistory> exportHistoryList) {
		this.exportHistoryList = exportHistoryList;
	}

	@Override
	public String toString() {
		return "ExportHistoryList [exportHistoryList=" + exportHistoryList + "]";
	}

}
