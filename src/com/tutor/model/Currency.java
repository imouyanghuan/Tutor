package com.tutor.model;

/**
 * @author bruce.chen
 * 
 *         2015-10-10
 */
public class Currency {

	private int value;
	private int type;
	private int id;
	private String text;
	private String lang;

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	@Override
	public String toString() {
		return "Currency [value=" + value + ", type=" + type + ", id=" + id + ", text=" + text + ", lang=" + lang + "]";
	}
}
