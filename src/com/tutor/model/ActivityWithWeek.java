package com.tutor.model;

import java.util.ArrayList;

public class ActivityWithWeek {

	public ActivityWithWeek(boolean isYellow) {
		this.isYellow = isYellow;
	}

	private String week;
	private ArrayList<TimeTable> timeTables;
	private boolean isYellow;
	
	public String getWeek() {
		return week;
	}
	
	public void setWeek(String week) {
		this.week = week;
	}
	
	public ArrayList<TimeTable> getTimeTables() {
		return timeTables;
	}
	
	public void setTimeTables(ArrayList<TimeTable> timeTables) {
		this.timeTables = timeTables;
	}
	
	public boolean isYellow() {
		return isYellow;
	}
	
	public void setYellow(boolean isYellow) {
		this.isYellow = isYellow;
	}
}
