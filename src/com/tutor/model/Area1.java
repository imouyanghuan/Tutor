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
	private String district;
	private boolean selected;
	private String address;

	public boolean getSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
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
		return "Area1 [id=" + id + ", district=" + district + ", selected=" + selected + ", address=" + address + "]";
	}
}
