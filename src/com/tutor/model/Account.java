package com.tutor.model;

import java.util.Date;

/**
 * @author bruce.chen
 * 
 *         2015-8-31
 */
public class Account extends BaseModel {

	private static final long serialVersionUID = 1L;
	private Integer memberId;
	private Integer role;
	private String email;
	private String pswd;
	private String facebookId;
	private String imAccount;
	private String imPswd;
	private String token;
	private Date createdTime;
	private Integer status;

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public Integer getRole() {
		return role;
	}

	public void setRole(Integer role) {
		this.role = role;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPswd() {
		return pswd;
	}

	public void setPswd(String pswd) {
		this.pswd = pswd;
	}

	public String getFacebookId() {
		return facebookId;
	}

	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}

	public String getImAccount() {
		return imAccount;
	}

	public void setImAccount(String imAccount) {
		this.imAccount = imAccount;
	}

	public String getImPswd() {
		return imPswd;
	}

	public void setImPswd(String imPswd) {
		this.imPswd = imPswd;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Account [memberId=" + memberId + ", role=" + role + ", email=" + email + ", pswd=" + pswd + ", facebookId=" + facebookId + ", imAccount=" + imAccount + ", imPswd=" + imPswd
				+ ", token=" + token + ", createdTime=" + createdTime + ", status=" + status + "]";
	}
}
