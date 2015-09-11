package com.tutor.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author bruce.chen
 * 
 *         2015-9-1
 */
public class CourseItem1 implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private ArrayList<CourseItem2> result;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<CourseItem2> getResult() {
		return result;
	}

	public void setResult(ArrayList<CourseItem2> result) {
		this.result = result;
	}
}
