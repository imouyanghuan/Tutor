package com.tutor.adapter;

import java.util.List;

import android.content.Context;

import com.tutor.R;
import com.tutor.model.Course;

public class SchoolAdapter extends TutorBaseAdapter<Course> {

	public SchoolAdapter(Context mContext, List<Course> mData) {
		super(mContext, mData, R.layout.layout_course_item);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void convert(ViewHolder holder, Course course, int position) {
		holder.setText(R.id.tv_item, course.getName());
	}
}
