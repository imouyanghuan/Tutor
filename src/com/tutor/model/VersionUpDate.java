package com.tutor.model;

/**
 * @author bruce.chen
 * 
 *         2015-10-14
 */
public class VersionUpDate {

	private String version;
	private String description;
	private String url;
	private boolean isForceUpdate;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isForceUpdate() {
		return isForceUpdate;
	}

	public void setForceUpdate(boolean isForceUpdate) {
		this.isForceUpdate = isForceUpdate;
	}
}
