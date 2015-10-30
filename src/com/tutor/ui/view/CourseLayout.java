package com.tutor.ui.view;

import java.util.ArrayList;

import android.content.Context;
import android.widget.LinearLayout;

import com.tutor.model.Course;
import com.tutor.ui.view.CourseItemLayout.OnRefreshListener;

/**
 * 課程列表
 * 
 * @author bruce.chen
 * 
 *         2015-9-2
 */
public class CourseLayout extends LinearLayout {

	private ArrayList<Course> courses;
	private ArrayList<CourseItemLayout> courseItemLayouts = new ArrayList<CourseItemLayout>();

	public CourseLayout(Context context, ArrayList<Course> list, OnRefreshListener listener) {
		super(context);
		courses = list;
		init(listener);
	}

	private void init(OnRefreshListener listener) {
		setOrientation(LinearLayout.VERTICAL);
		if (null == courses || courses.size() == 0) {
			return;
		}
		for (Course course : courses) {
			CourseItemLayout courseItemLayout = new CourseItemLayout(getContext(), course, listener);
			courseItemLayouts.add(courseItemLayout);
			addView(courseItemLayout);
		}
	}

	public void reFresh() {
		for (CourseItemLayout itemLayout : courseItemLayouts) {
			itemLayout.refresh();
		}
	}
}
