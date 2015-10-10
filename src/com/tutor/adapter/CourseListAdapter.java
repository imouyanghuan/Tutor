package com.tutor.adapter;

import java.util.List;

import android.content.Context;

import com.tutor.R;
import com.tutor.model.CourseItem2;

public class CourseListAdapter extends TutorBaseAdapter<CourseItem2> {

	public CourseListAdapter(Context mContext, List<CourseItem2> mData) {
		super(mContext, mData, R.layout.layout_course_item);
	}

	@Override
	protected void convert(ViewHolder holder, CourseItem2 courseItem, int position) {
		holder.setText(R.id.tv_item, " - " + courseItem.getType() + "  " + courseItem.getSubType() + "  " + courseItem.getCourseName());
	}
}
