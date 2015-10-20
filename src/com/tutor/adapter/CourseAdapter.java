package com.tutor.adapter;

import java.util.List;

import android.content.Context;

import com.hk.tutor.R;
import com.tutor.model.CourseItem2;

public class CourseAdapter extends TutorBaseAdapter<CourseItem2> {

	public CourseAdapter(Context mContext, List<CourseItem2> mData) {
		super(mContext, mData, R.layout.layout_course_item);
	}

	@Override
	protected void convert(ViewHolder holder, CourseItem2 courseItem, int position) {
		holder.setText(R.id.tv_item, courseItem.getCourseName());
	}
}
