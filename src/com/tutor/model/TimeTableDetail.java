package com.tutor.model;

import java.io.Serializable;

/**
 * @author jerry.yao
 * 
 *         2015-10-28
 */
public class TimeTableDetail implements Serializable {

	private static final long serialVersionUID = 1L;
	private int memberId;
	private int dayOfWeek;
	private int startHour;
	private int startMinute;
	private int endHour;
	private int endMinute;
	
	private String userName;
	private String courseName;

	public int getMemberId() {
		return memberId;
	}

	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}

	public int getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(int dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public int getStartHour() {
		return startHour;
	}

	public void setStartHour(int startHour) {
		this.startHour = startHour;
	}

	public int getStartMinute() {
		return startMinute;
	}

	public void setStartMinute(int startMinute) {
		this.startMinute = startMinute;
	}

	public int getEndHour() {
		return endHour;
	}

	public void setEndHour(int endHour) {
		this.endHour = endHour;
	}

	public int getEndMinute() {
		return endMinute;
	}

	public void setEndMinute(int endMinute) {
		this.endMinute = endMinute;
	}

	public boolean isRepeat(TimeTableDetail timeslot) {
		// 星期不同,不会冲突
		if (getDayOfWeek() != timeslot.dayOfWeek) {
			return false;
		}
		// 第二个的开始时间在第一个的结束时间之后 , 或者第二个的结束时间在第一个的开始时间之前,不冲突
		if (getStartHour() > timeslot.getEndHour() || getEndHour() < timeslot.getStartHour()) {
			return false;
		}
		// 第二个的开始时间和第一个的结束时间相等,判断分钟(第二个的开始分钟大于第一个的结束分钟,不冲突)
		if (getStartHour() == timeslot.getEndHour() && getStartMinute() >= timeslot.getEndMinute()) {
			return false;
		}
		// 第二个的结束时间等于第一个的开始时间,判断分钟(第二个的结束分钟小于第一个的开始分钟,不冲突)
		if (getEndHour() == timeslot.getStartHour() && getEndMinute() <= timeslot.startMinute) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Timeslot [memberId=" + memberId + ", dayOfWeek=" + dayOfWeek + ", startHour=" + startHour + ", startMinute=" + startMinute + ", endHour=" + endHour + ", endMinute=" + endMinute + "]";
	}

	
	public String getUserName() {
		return userName;
	}

	
	public void setUserName(String userName) {
		this.userName = userName;
	}

	
	public String getCourseName() {
		return courseName;
	}

	
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
}
