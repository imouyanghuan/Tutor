package com.tutor.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;

import com.hk.tutor.R;
import com.tutor.model.ServiceGrade;

public class SearchServiceGradeAdapter extends TutorBaseAdapter<ServiceGrade> {

	public SearchServiceGradeAdapter(Context mContext, List<ServiceGrade> mData) {
		super(mContext, mData, R.layout.service_grade_item);
	}

	@Override
	protected void convert(ViewHolder holder, final ServiceGrade serviceGrade, int position) {
		holder.setText(R.id.course_item_gv_item_name, serviceGrade.getText());
		CheckBox cbGrade = holder.getView(R.id.course_item_gv_item_checkbox);
		cbGrade.setChecked(serviceGrade.isChecked());
		holder.setOnclickListener(R.id.course_item_gv_item_checkbox, new OnClickListener() {

			@Override
			public void onClick(View v) {
				serviceGrade.setChecked(!serviceGrade.isChecked());
				if (onCheckedListener != null) {
					onCheckedListener.checkedChanged(serviceGrade);
				}
			}
		});
	}

	public interface OnCheckedListener {

		public void checkedChanged(ServiceGrade serviceGrade);
	}

	public OnCheckedListener onCheckedListener;

	public void setOnCheckedListener(OnCheckedListener onCheckedListener) {
		this.onCheckedListener = onCheckedListener;
	}
}
