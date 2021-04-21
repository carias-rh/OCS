package eu.europa.ec.eci.oct.entities.export;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name = "OCT_EXPORT_HISTORY")
public class ExportHistory implements Serializable {

	private static final long serialVersionUID = -7352871494470051305L;

	public ExportHistory() {
	}

	@GeneratedValue(generator = "IdGenerator", strategy = GenerationType.TABLE)
	@Id
	@TableGenerator(name = "IdGenerator", pkColumnValue = "export_history_id", table = "HIBERNATE_SEQUENCES", allocationSize = 1, pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
	@Column(name = "id")
	private Long id;

	@Column
	private String jobId;
	@Column
	private Date exportDate;
	@Column
	private String countriesParam;
	@Column
	private String startDateParam;
	@Column
	private String endDateParam;
	@Column
	private Integer exportProgress = 0;
	@Column
	private String exportSummary;
	@Column
	private Integer validationProgress = 0;
	@Column
	private String validationSummary;
	@Column
	private String batchStatus;
	@Column
	private String exitStatus;
	@Column
	private String duration;
	@Column
	private String errorMessage;
	@Column
	private String exportDirectoryPath;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public Date getExportDate() {
		return exportDate;
	}

	public void setExportDate(Date exportDate) {
		this.exportDate = exportDate;
	}

	public String getCountriesParam() {
		return countriesParam;
	}

	public void setCountriesParam(String countriesParam) {
		this.countriesParam = countriesParam;
	}

	public String getStartDateParam() {
		return startDateParam;
	}

	public void setStartDateParam(String startDateParam) {
		this.startDateParam = startDateParam;
	}

	public String getEndDateParam() {
		return endDateParam;
	}

	public void setEndDateParam(String endDateParam) {
		this.endDateParam = endDateParam;
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

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getExportDirectoryPath() {
		return exportDirectoryPath;
	}

	public void setExportDirectoryPath(String exportDirectoryPath) {
		this.exportDirectoryPath = exportDirectoryPath;
	}

	@Override
	public String toString() {
		return "ExportHistory [id=" + id + ", jobId=" + jobId + ", exportDate=" + exportDate + ", countriesParam="
				+ countriesParam + ", startDateParam=" + startDateParam + ", endDateParam=" + endDateParam
				+ ", exportProgress=" + exportProgress + ", exportSummary=" + exportSummary + ", validationProgress="
				+ validationProgress + ", validationSummary=" + validationSummary + ", batchStatus=" + batchStatus
				+ ", exitStatus=" + exitStatus + ", duration=" + duration + ", errorMessage=" + errorMessage
				+ ", exportDirectoryPath=" + exportDirectoryPath + "]";
	}

}
