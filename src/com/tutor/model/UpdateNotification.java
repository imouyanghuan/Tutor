package com.tutor.model;

public class UpdateNotification {

	private String phone;
	private String hkidNumber;
	private String residentialAddress;
	private int status;
	private int id;

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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
