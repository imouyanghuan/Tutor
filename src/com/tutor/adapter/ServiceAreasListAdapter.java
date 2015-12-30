package com.tutor.adapter;

import java.util.List;

import android.content.Context;

import com.hk.tutor.R;
import com.tutor.model.Area1;

public class ServiceAreasListAdapter extends TutorBaseAdapter<Area1> {

	public ServiceAreasListAdapter(Context mContext, List<Area1> mData) {
		super(mContext, mData, R.layout.layout_service_area_item);
	}

	@Override
	protected void convert(ViewHolder holder, Area1 area, int position) {
//		LinearLayout llServiceGrade = holder.getView(R.id.ll_service_grade);
//		String district = area.getDistrict();
//		String address = area.getAddress();
//		TextView tvAddress = new TextView(mContext);
//		tvAddress.setPadding(0, 10, 0, 10);
//		tvAddress.setGravity(Gravity.CENTER_VERTICAL);
//		tvAddress.setText(district + " - " + address);
//		llServiceGrade.addView(tvAddress);
		String district = area.getDistrict();
		String address = area.getAddress();
		holder.setText(R.id.tv_area, district + " - " + address);
	}
}
