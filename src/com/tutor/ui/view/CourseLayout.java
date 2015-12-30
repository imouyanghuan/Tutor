package com.tutor.ui.view;

import java.util.ArrayList;

import android.content.Context;
import android.widget.LinearLayout;

import com.tutor.TutorApplication;
import com.tutor.model.Course;
import com.tutor.model.SubjectCourseModel;
import com.tutor.params.Constants;
import com.tutor.ui.view.CourseItemLayout.OnRefreshListener;
import com.tutor.ui.view.CourseItemLayout.OnSelectedCourseValuesListener;

/**
 * 課程列表
 * 
 * @author bruce.chen
 * 
 *         2015-9-2
 */
public class CourseLayout extends LinearLayout {

	private ArrayList<Course> studentCourses;
	private ArrayList<SubjectCourseModel> tutorCourses;
	/**
	 * 从ToBeMyStudent页面跳转来的和学生一样
	 */
	private boolean isFromToBeMyStudent;
	private ArrayList<CourseItemLayout> courseItemLayouts = new ArrayList<CourseItemLayout>();

	public CourseLayout(Context context) {
		super(context);
	}

	@SuppressWarnings("unchecked")
	public void setCourse(Object obj, OnRefreshListener listener, OnSelectedCourseValuesListener selectedValueListener, boolean isFromToBeMyStudent) {
		this.isFromToBeMyStudent = isFromToBeMyStudent;
		if (TutorApplication.getRole() == Constants.General.ROLE_STUDENT || isFromToBeMyStudent) {
			studentCourses = (ArrayList<Course>) obj;
		} else {
			tutorCourses = (ArrayList<SubjectCourseModel>) obj;
		}
		init(listener, selectedValueListener);
	}

	private void init(OnRefreshListener listener, OnSelectedCourseValuesListener selectedValueListener) {
		setOrientation(LinearLayout.VERTICAL);
		if (TutorApplication.getRole() == Constants.General.ROLE_STUDENT || isFromToBeMyStudent) {
			if (null == studentCourses || studentCourses.size() == 0) {
				return;
			}
			for (Course course : studentCourses) {
				CourseItemLayout courseItemLayout = new CourseItemLayout(getContext());
				courseItemLayout.setData(course, listener, selectedValueListener, isFromToBeMyStudent);
				courseItemLayouts.add(courseItemLayout);
				addView(courseItemLayout);
			}
		} else {
			if (null == tutorCourses || tutorCourses.size() == 0) {
				return;
			}
			CourseItemLayout courseItemLayout = new CourseItemLayout(getContext());
			courseItemLayout.setData(tutorCourses, listener, selectedValueListener, isFromToBeMyStudent);
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
