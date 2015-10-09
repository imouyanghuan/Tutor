package com.tutor.model;

/**
 * 註冊登錄model
 * 
 * @author bruce.chen
 * 
 *         2015-8-28
 */
public class RegisterLoginModel {

	private String Email;// 郵箱
	private String Password;// 密碼
	private String FBOpenID;// Facebook開放id
	private String IMID; // im賬號
	private int AccountType;// 類型.0學生,1老師
	private String jRegistrationID; // JPush的注册id

	public String getEmail() {
		return Email;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public String getPassword() {
		return Password;
	}

	public void setPassword(String password) {
		Password = password;
	}

	public String getFBOpenID() {
		return FBOpenID;
	}

	public void setFBOpenID(String fBOpenID) {
		FBOpenID = fBOpenID;
	}

	public int getAccountType() {
		return AccountType;
	}

	public void setAccountType(int accountType) {
		AccountType = accountType;
	}

	public String getIMID() {
		return IMID;
	}

	public void setIMID(String iMID) {
		IMID = iMID;
	}

	@Override
	public String toString() {
		return "RegisterLoginModel [Email=" + Email + ", Password=" + Password + ", FBOpenID=" + FBOpenID + ", IMID=" + IMID + ", AccountType=" + AccountType + "]";
	}

	
	public String getjRegistrationID() {
		return jRegistrationID;
	}

	
	public void setjRegistrationID(String jRegistrationID) {
		this.jRegistrationID = jRegistrationID;
	}
}
