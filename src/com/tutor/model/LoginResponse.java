package com.tutor.model;

/**
 * 登錄
 * 
 * @author bruce.chen
 * 
 *         2015-8-31
 */
public class LoginResponse {

	private int memberId;// 會員id
	private String token;

	public int getMemberId() {
		return memberId;
	}

	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
