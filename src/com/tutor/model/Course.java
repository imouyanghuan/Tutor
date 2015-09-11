package com.tutor.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author bruce.chen
 * 
 *         2015-9-1
 */
public class Course implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private ArrayList<CourseItem1> result;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<CourseItem1> getResult() {
		return result;
	}

	public void setResult(ArrayList<CourseItem1> result) {
		this.result = result;
	}
}
