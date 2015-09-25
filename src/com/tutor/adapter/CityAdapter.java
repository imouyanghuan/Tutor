package com.tutor.adapter;

import java.util.List;

import android.content.Context;

import com.tutor.R;
import com.tutor.model.Area;

public class CityAdapter extends TutorBaseAdapter<Area> {

	public CityAdapter(Context mContext, List<Area> mData) {
		super(mContext, mData, R.layout.layout_course_item);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void convert(ViewHolder holder, Area area, int position) {
		// TODO Auto-generated method stub
		holder.setText(R.id.tv_item, area.getName());
	}
}
