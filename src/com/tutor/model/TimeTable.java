package com.tutor.model;

import java.io.Serializable;
import java.util.ArrayList;

public class TimeTable implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5902748325489255305L;
	private String userName;
	private String courseName;
	private ArrayList<Timeslot> timeslots;

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

	public ArrayList<Timeslot> getTimeslots() {
		return timeslots;
	}

	public void setTimeslots(ArrayList<Timeslot> timeslots) {
		this.timeslots = timeslots;
	}
}
