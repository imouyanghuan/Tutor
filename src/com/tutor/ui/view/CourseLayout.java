package com.tutor.ui.view;

import java.util.ArrayList;

import com.tutor.model.Course;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * 課程列表
 * 
 * @author bruce.chen
 * 
 *         2015-9-2
 */
public class CourseLayout extends LinearLayout {

	private ArrayList<Course> courses;

	public CourseLayout(Context context, ArrayList<Course> list) {
		super(context);
		courses = list;
		init();
	}

	public CourseLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CourseLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		setOrientation(LinearLayout.VERTICAL);
		if (null == courses || courses.size() == 0) {
			return;
		}
		for (Course course : courses) {
			CourseItemLayout courseItemLayout = new CourseItemLayout(getContext(), course);
			addView(courseItemLayout);
		}
	}
}
