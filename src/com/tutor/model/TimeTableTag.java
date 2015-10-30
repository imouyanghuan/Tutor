package com.tutor.model;

public class TimeTableTag {

	public TimeTableTag() {}

	public TimeTableTag(int id, String week, boolean isTag) {
		this.id = id;
		this.week = week;
		this.isTag = isTag;
	}

	private int id;
	private String week;
	private boolean isTag;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getWeek() {
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	public boolean isTag() {
		return isTag;
	}

	public void setTag(boolean isTag) {
		this.isTag = isTag;
	}
}
