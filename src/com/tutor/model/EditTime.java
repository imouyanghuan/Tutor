package com.tutor.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 编辑时间段外层model
 * 
 * @author jerry.yao
 * 
 *         2015-11-04
 */
public class EditTime implements Serializable {

	private static final long serialVersionUID = 1L;
	private ArrayList<TimeTableDetail> timeslots;

	public ArrayList<TimeTableDetail> getTimeslots() {
		return timeslots;
	}

	public void setTimeslots(ArrayList<TimeTableDetail> timeslots) {
		this.timeslots = timeslots;
	}
}
