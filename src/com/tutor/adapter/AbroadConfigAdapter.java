package com.tutor.adapter;

import java.util.List;

import android.content.Context;
import android.widget.TextView;

import com.hk.tutor.R;
import com.tutor.model.AbroadConfig;

public class AbroadConfigAdapter extends TutorBaseAdapter<AbroadConfig> {

	public AbroadConfigAdapter(Context mContext, List<AbroadConfig> mData) {
		super(mContext, mData, R.layout.layout_course_selection_item);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void convert(ViewHolder holder, AbroadConfig config, int position) {
		TextView tvItem = holder.getView(R.id.tv_item);
		tvItem.setText(config.getText());
	}
}
