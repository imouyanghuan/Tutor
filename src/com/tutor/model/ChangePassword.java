package com.tutor.model;

/**
 * @author bruce.chen
 * 
 *         2015-10-8
 */
public class ChangePassword {

	private String password;
	private String newPassword;

	public ChangePassword(String password, String newPassword) {
		this.password = password;
		this.newPassword = newPassword;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
}
