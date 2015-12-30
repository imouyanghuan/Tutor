package com.tutor.adapter;

import java.util.List;

import android.content.Context;

import com.hk.tutor.R;
import com.tutor.model.ServiceGrade;

public class ServiceGradeListAdapter extends TutorBaseAdapter<ServiceGrade> {

	public ServiceGradeListAdapter(Context mContext, List<ServiceGrade> mData) {
		super(mContext, mData, R.layout.layout_service_grade_item);
	}

	@Override
	protected void convert(ViewHolder holder, ServiceGrade serviceGrade, int position) {
		holder.setText(R.id.tv_grade, serviceGrade.getText());
	}
}
