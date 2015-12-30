package com.tutor.model;

import java.util.ArrayList;

/**
 * @author jerry.yao
 * 
 *         2015-12-4
 */
public class SubjectCourseModel {

	private String category;
	private ArrayList<SubjectModel> subjects;

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public ArrayList<SubjectModel> getSubjects() {
		return subjects;
	}

	public void setSubjects(ArrayList<SubjectModel> subjects) {
		this.subjects = subjects;
	}
}
