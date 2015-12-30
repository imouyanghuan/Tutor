package com.tutor.model;

import java.io.Serializable;

/**
 * 补习社服务年级
 * 
 * @author jerry.yao
 * 
 */
public class ServiceGrade implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int value;
	private String text;
	private String lang;
	private int type;
	private String category;
	private String createdTime;
	private int id;
	private boolean isChecked;

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
}
