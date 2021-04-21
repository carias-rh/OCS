package eu.europa.ec.eci.oct.webcommons.services.api.domain.export;

import java.io.Serializable;

import org.springframework.stereotype.Component;

@Component
public class ExportHistoryDTO implements Serializable {

	private static final long serialVersionUID = -884481796818476990L;
	
	private String jobId;
	private String exportDate;
	private String countriesParam;
	private String dateRangeParam;
	private Integer exportProgress = 0;
	private String exportSummary;
	private Integer validationProgress = 0;
	private String validationSummary;
	private String batchStatus;
	private String exitStatus;
	private String duration;
	
	public String getExportDate() {
		return exportDate;
	}

	public void setExportDate(String exportDate) {
		this.exportDate = exportDate;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getCountriesParam() {
		return countriesParam;
	}

	public void setCountriesParam(String countriesParam) {
		this.countriesParam = countriesParam;
	}

	public String getDateRangeParam() {
		return dateRangeParam;
	}

	public void setDateRangeParam(String dateRangeParam) {
		this.dateRangeParam = dateRangeParam;
	}

	public Integer getExportProgress() {
		return exportProgress;
	}

	public void setExportProgress(Integer exportProgress) {
		this.exportProgress = exportProgress;
	}

	public String getExportSummary() {
		return exportSummary;
	}

	public void setExportSummary(String exportSummary) {
		this.exportSummary = exportSummary;
	}

	public Integer getValidationProgress() {
		return validationProgress;
	}

	public void setValidationProgress(Integer validationProgress) {
		this.validationProgress = validationProgress;
	}

	public String getValidationSummary() {
		return validationSummary;
	}

	public void setValidationSummary(String validationSummary) {
		this.validationSummary = validationSummary;
	}

	public String getBatchStatus() {
		return batchStatus;
	}

	public void setBatchStatus(String batchStatus) {
		this.batchStatus = batchStatus;
	}

	public String getExitStatus() {
		return exitStatus;
	}

	public void setExitStatus(String exitStatus) {
		this.exitStatus = exitStatus;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}


	@Override
	public String toString() {
		return "ExportHistoryDTO [exportDate=" + exportDate + ", countriesParam=" + countriesParam + ", dateRangeParam=" + dateRangeParam + ", exportProgress=" + exportProgress + ", exportSummary="
				+ exportSummary + ", validationProgress=" + validationProgress + ", validationSummary=" + validationSummary + ", batchStatus=" + batchStatus + ", exitStatus=" + exitStatus
				+ ", duration=" + duration + "]";
	}
}