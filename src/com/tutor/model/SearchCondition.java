package com.tutor.model;

import java.io.Serializable;

/**
 * 搜索条件model
 * 
 * @author jerry.yao
 * 
 */
public class SearchCondition implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int courseId;
	private int areaId;
	private Timeslot timeslot;
	private int gender;

	public int getCourseId() {
		return courseId;
	}

	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}

	public int getAreaId() {
		return areaId;
	}

	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}

	public Timeslot getTimeslot() {
		return timeslot;
	}

	public void setTimeslot(Timeslot timeslot) {
		this.timeslot = timeslot;
	}

	
	public int getGender() {
		return gender;
	}

	
	public void setGender(int gender) {
		this.gender = gender;
	}
}
