package eu.europa.ec.eci.oct.webcommons.services.api.domain;

import java.io.Serializable;

import org.springframework.stereotype.Component;

@Component
public class VersionDTO implements Serializable {

	private static final long serialVersionUID = -4393259005840794334L;

	public VersionDTO() {
	}

	private String apiVersion;
	private String buildDate;
	private String projectVersion;

	public String getApiVersion() {
		return apiVersion;
	}

	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}

	public String getBuildDate() {
		return buildDate;
	}

	public void setBuildDate(String buildDate) {
		this.buildDate = buildDate;
	}

	public String getProjectVersion() {
		return projectVersion;
	}

	public void setProjectVersion(String projectVersion) {
		this.projectVersion = projectVersion;
	}

	@Override
	public String toString() {
		return "VersionDTO [apiVersion=" + apiVersion + ", buildDate=" + buildDate + ", projectVersion=" + projectVersion + "]";
	}

}
