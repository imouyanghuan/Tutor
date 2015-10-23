package com.tutor.adapter;

import java.util.List;

import android.content.Context;

import com.hk.tutor.R;
import com.tutor.model.AbroadConfig;

public class AdviserAdapter extends TutorBaseAdapter<AbroadConfig> {

	public AdviserAdapter(Context mContext, List<AbroadConfig> mData) {
		super(mContext, mData, R.layout.layout_adviser_item);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void convert(ViewHolder holder, AbroadConfig adviser, int position) {
		// TODO Auto-generated method stub
		holder.setText(R.id.tv_item, adviser.getAlias());
	}
}
