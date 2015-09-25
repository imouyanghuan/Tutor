package com.tutor.adapter;

import java.util.List;

import android.content.Context;

import com.tutor.R;
import com.tutor.model.Area1;

public class CountryAdapter extends TutorBaseAdapter<Area1> {

	public CountryAdapter(Context mContext, List<Area1> mData) {
		super(mContext, mData, R.layout.layout_course_item);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void convert(ViewHolder holder, Area1 country, int position) {
		// TODO Auto-generated method stub
		holder.setText(R.id.tv_item, country.getAddress());
	}
}
