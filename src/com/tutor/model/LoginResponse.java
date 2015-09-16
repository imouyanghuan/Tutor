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
}
