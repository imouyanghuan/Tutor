package com.tutor.model;

import java.io.Serializable;

/**
 * @author bruce.chen
 * 
 *         2015-9-1
 */
public class CourseItem2 implements Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private String type;
	private String subType;
	private String courseName;
	private String description;
	private boolean selected;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSubType() {
		return subType;
	}

	public void setSubType(String subType) {
		this.subType = subType;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean getSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
