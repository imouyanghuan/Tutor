package com.tutor.model;

import java.io.Serializable;
import java.util.ArrayList;

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
	private int[] courseValues;
	private String courseName;
	private int areaValue;
	private String areaName;
	private Timeslot timeslot;
	private int gender;
	private String genderName;
	private String searchKeyword;
	private boolean isVoluntaryTutoring;
	private boolean isCertified;
	// 补习社服务年级和地区
	private int serviceGradeValue;
	private ArrayList<Integer> areaValues;
	private int gradeValue;
	private String gradeName;

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

	public boolean isVoluntaryTutoring() {
		return isVoluntaryTutoring;
	}

	public void setVoluntaryTutoring(boolean isVoluntaryTutoring) {
		this.isVoluntaryTutoring = isVoluntaryTutoring;
	}

	public int[] getCourseValues() {
		return courseValues;
	}

	public void setCourseValues(int[] courseValues) {
		this.courseValues = courseValues;
	}

	public int getGradeValue() {
		return gradeValue;
	}

	public void setGradeValue(int gradeValue) {
		this.gradeValue = gradeValue;
	}

	public String getGradeName() {
		return gradeName;
	}

	public void setGradeName(String gradeName) {
		this.gradeName = gradeName;
	}

	public boolean isCertified() {
		return isCertified;
	}

	public void setCertified(boolean isCertified) {
		this.isCertified = isCertified;
	}

	
	public int getServiceGradeValue() {
		return serviceGradeValue;
	}

	
	public void setServiceGradeValue(int serviceGradeValue) {
		this.serviceGradeValue = serviceGradeValue;
	}

	
	public ArrayList<Integer> getAreaValues() {
		return areaValues;
	}

	
	public void setAreaValues(ArrayList<Integer> areaValues) {
		this.areaValues = areaValues;
	}
}
