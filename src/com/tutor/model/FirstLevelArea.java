package com.tutor.model;

import java.io.Serializable;

/**
 * @author jerry.yao
 * 
 *         2015-12-23
 */
public class FirstLevelArea implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int value;
	private String name;
	private String lang;
	private boolean isChecked;
	private int id;

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

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
}
