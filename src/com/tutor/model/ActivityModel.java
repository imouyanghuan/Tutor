package com.tutor.model;

import java.io.Serializable;

import android.text.TextUtils;

public class ActivityModel implements Serializable, Comparable<ActivityModel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String title;
	private String description;
	private String heldDate;
	private String startTime;
	private String endTime;
	private int value;
	private String lang;
	private int id;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getHeldDate() {
		return heldDate;
	}

	public void setHeldDate(String heldDate) {
		this.heldDate = heldDate;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int compareTo(ActivityModel another) {
		String anotherStartTime = another.getStartTime();
		int anotherStartHour = 0;
		int anotherStartMinute = 0;
		if (!TextUtils.isEmpty(anotherStartTime) && anotherStartTime.length() > 4) {
			anotherStartHour = Integer.parseInt(anotherStartTime.substring(0, 2));
			anotherStartMinute = Integer.parseInt(anotherStartTime.substring(3, 5));
		}
		String startTime = getStartTime();
		int startHour = 0;
		int startMinute = 0;
		if (!TextUtils.isEmpty(startTime) && startTime.length() > 4) {
			startHour = Integer.parseInt(startTime.substring(0, 2));
			startMinute = Integer.parseInt(startTime.substring(3, 5));
		}
		if (startHour > anotherStartHour) {
			// 正
			return 1;
		} else if (startHour < anotherStartHour) {
			// 负
			return -1;
		} else if (startHour == anotherStartHour) {
			// 0
			if (startMinute > anotherStartMinute) {
				// 正
				return 1;
			} else if (startMinute < anotherStartMinute) {
				return -1;
			} else {
				return 0;
			}
		}
		return 0;
	}

	public boolean isSameTime(ActivityModel model) {
		if (model.getStartTime().equals(getStartTime()) && model.getEndTime().equals(getEndTime())) {
			return true;
		}
		return false;
	}
}
