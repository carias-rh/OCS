package eu.europa.ec.eci.oct.webcommons.services.api.domain.export;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class ExportHistoryListDTO implements Serializable{

	private static final long serialVersionUID = -3550680340058950252L;
	
	private List<ExportHistoryDTO> exportHistoryList;

	public List<ExportHistoryDTO> getExportHistoryList() {
		return exportHistoryList;
	}

	public void setExportHistoryList(List<ExportHistoryDTO> exportHistoryList) {
		this.exportHistoryList = exportHistoryList;
	}
}
