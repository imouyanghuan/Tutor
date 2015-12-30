package com.tutor.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

import com.hk.tutor.R;
import com.tutor.model.FirstLevelArea;

public class SearchServiceAreaAdapter extends TutorBaseAdapter<FirstLevelArea> {

	public SearchServiceAreaAdapter(Context mContext, List<FirstLevelArea> mData) {
		super(mContext, mData, R.layout.service_grade_item);
	}

	@Override
	protected void convert(ViewHolder holder, final FirstLevelArea area, int position) {
		holder.setText(R.id.course_item_gv_item_name, area.getName());
		holder.setOnclickListener(R.id.course_item_gv_item_checkbox, new OnClickListener() {

			@Override
			public void onClick(View v) {
				area.setChecked(!area.isChecked());
			}
		});
	}

	public interface OnCheckedChangeListener {

		public void checkedChanged(FirstLevelArea area);
	}

	public OnCheckedChangeListener onCheckedChangeListener;

	public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
		this.onCheckedChangeListener = onCheckedChangeListener;
	}
}
