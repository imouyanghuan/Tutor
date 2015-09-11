package com.tutor.ui.view;

import java.util.ArrayList;

import com.tutor.R;
import com.tutor.model.Course;
import com.tutor.model.CourseItem1;
import com.tutor.util.ViewHelper;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author bruce.chen
 * 
 *         2015-9-2
 */
public class CourseItemLayout extends LinearLayout {

	private Course course;

	public CourseItemLayout(Context context, Course course) {
		super(context);
		this.course = course;
		init();
	}

	private void init() {
		setOrientation(LinearLayout.VERTICAL);
		if (null == course) {
			return;
		}
		View root = LayoutInflater.from(getContext()).inflate(R.layout.course_item, this);
		TextView title = ViewHelper.get(root, R.id.course_item_title1);
		title.setText(course.getName());
		ArrayList<CourseItem1> arrayList = course.getResult();
		if (null != arrayList) {
			for (CourseItem1 courseItem : arrayList) {
				CourseItem2Layout layout = new CourseItem2Layout(getContext(), courseItem);
				addView(layout);
			}
		}
	}

	public CourseItemLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CourseItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
}
