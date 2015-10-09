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
	private int courseValue;
	private String courseName;
	private int areaValue;
	private String areaName;
	private Timeslot timeslot;
	private int gender;
	private String genderName;
	private String searchKeyword;


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

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getGenderName() {
		return genderName;
	}

	public void setGenderName(String genderName) {
		this.genderName = genderName;
	}

	public String getSearchKeyword() {
		return searchKeyword;
	}

	public void setSearchKeyword(String searchKeyword) {
		this.searchKeyword = searchKeyword;
	}

	
	public int getCourseValue() {
		return courseValue;
	}

	
	public void setCourseValue(int courseValue) {
		this.courseValue = courseValue;
	}

	
	public int getAreaValue() {
		return areaValue;
	}

	
	public void setAreaValue(int areaValue) {
		this.areaValue = areaValue;
	}
}
