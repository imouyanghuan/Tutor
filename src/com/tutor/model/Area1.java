package com.tutor.model;

import java.io.Serializable;

/**
 * @author bruce.chen
 * 
 *         2015-9-1
 */
public class Area1 implements Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private boolean isChecked;
	private String lang;
	private String address;
	private String district;
	private int value;

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "Area1 [id=" + id + ", district=" + district + ", isChecked=" + isChecked + ", address=" + address + "]";
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
