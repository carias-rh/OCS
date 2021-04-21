package eu.europa.ec.eci.oct.export.persistence;

import java.util.List;

import eu.europa.ec.eci.oct.entities.export.ExportHistory;
import eu.europa.ec.eci.oct.export.utils.ExportParameter;

public interface ExportHistoryPersistenceDAO {

	public ExportHistory getLastExportHistory() throws Exception;

	public void updateExportHistory(ExportHistory exportHistory) throws Exception;

	public String initExportHistory(ExportParameter exportParameter) throws Exception;

	public List<ExportHistory> getAllExportHistory() throws Exception;
	
	public ExportHistory getExportHistoryByUUID(String uuid) throws Exception;

	public void removeExportHistory(String jobUUID) throws Exception;
	
}
