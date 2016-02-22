package com.tutor.model;

/**
 * 登錄
 * 
 * @author bruce.chen
 * 
 *         2015-8-31
 */
public class LoginResponse {

	private int id;// 會員id
	private String token;
	private int status;
	private boolean isInfoComplete;
	private String phone;
	private String hkidNumber;
	private String residentialAddress;
	
	// 活动相关
	private String identityCode;
	private String alertTitle;
	private String alertContent;
	private String alertLink;


	public boolean isInfoComplete() {
		return isInfoComplete;
	}

	public void setInfoComplete(boolean isInfoComplete) {
		this.isInfoComplete = isInfoComplete;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getHkidNumber() {
		return hkidNumber;
	}

	public void setHkidNumber(String hkidNumber) {
		this.hkidNumber = hkidNumber;
	}

	public String getResidentialAddress() {
		return residentialAddress;
	}

	public void setResidentialAddress(String residentialAddress) {
		this.residentialAddress = residentialAddress;
	}

	
	public String getIdentityCode() {
		return identityCode;
	}

	
	public void setIdentityCode(String identityCode) {
		this.identityCode = identityCode;
	}

	
	public String getAlertTitle() {
		return alertTitle;
	}

	
	public void setAlertTitle(String alertTitle) {
		this.alertTitle = alertTitle;
	}

	
	public String getAlertContent() {
		return alertContent;
	}

	
	public void setAlertContent(String alertContent) {
		this.alertContent = alertContent;
	}

	
	public String getAlertLink() {
		return alertLink;
	}

	
	public void setAlertLink(String alertLink) {
		this.alertLink = alertLink;
	}
}
