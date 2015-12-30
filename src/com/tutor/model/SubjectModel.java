package com.tutor.model;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * @author jerry.yao
 * 
 *         2015-12-4
 */
public class SubjectModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private boolean isChecked;
	private ArrayList<Integer> courseValues;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public ArrayList<Integer> getCourseValues() {
		return courseValues;
	}

	public void setCourseValues(ArrayList<Integer> courseValues) {
		this.courseValues = courseValues;
	}
}
