package com.tutor.model;

import java.io.Serializable;

public class ActivityFlag implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private ActivityModel activityModel;
	private boolean isFlag;
	private int day;

	public ActivityFlag(int id, ActivityModel activityModel, boolean isFlag) {
		this.id = id;
		this.activityModel = activityModel;
		this.isFlag = isFlag;
	}

	public ActivityFlag() {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ActivityModel getActivityModel() {
		return activityModel;
	}

	public void setActivityModel(ActivityModel activityModel) {
		this.activityModel = activityModel;
	}

	public boolean isFlag() {
		return isFlag;
	}

	public void setFlag(boolean isFlag) {
		this.isFlag = isFlag;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}
}
