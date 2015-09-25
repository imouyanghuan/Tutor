package com.tutor.adapter;

import java.util.List;

import android.content.Context;

import com.tutor.R;
import com.tutor.model.CourseItem1;

public class GradeAdapter extends TutorBaseAdapter<CourseItem1> {

	public GradeAdapter(Context mContext, List<CourseItem1> mData) {
		super(mContext, mData, R.layout.layout_course_item);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void convert(ViewHolder holder, CourseItem1 courseItem, int position) {
		// TODO Auto-generated method stub
		holder.setText(R.id.tv_item, courseItem.getName());
	}
}
