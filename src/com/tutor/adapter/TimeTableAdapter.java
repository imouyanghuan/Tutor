package com.tutor.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.hk.tutor.R;
import com.tutor.TutorApplication;
import com.tutor.model.TimeTableDetail;
import com.tutor.params.Constants;

public class TimeTableAdapter extends TutorBaseAdapter<TimeTableDetail> {

	public TimeTableAdapter(Context mContext, List<TimeTableDetail> mData) {
		super(mContext, mData, R.layout.listview_item);
	}

	@Override
	protected void convert(ViewHolder holder, TimeTableDetail detail, int position) {
		TextView tvTime = holder.getView(R.id.tv_show_time);
		int endMinute = detail.getEndMinute();
		String endM = "";
		if (endMinute < 10) {
			endM = "0" + endMinute;
		} else {
			endM = String.valueOf(endMinute);
		}
		int startMinute = detail.getStartMinute();
		String startM = "";
		if (startMinute < 10) {
			startM = "0" + startMinute;
		} else {
			startM = String.valueOf(startMinute);
		}
		tvTime.setText(detail.getStartHour() + ":" + startM + " - " + detail.getEndHour() + ":" + endM);
		TextView tvUserName = holder.getView(R.id.tv_title);
		tvUserName.setText(detail.getUserName());
		TextView tvCourse = holder.getView(R.id.tv_course);
		tvCourse.setText(detail.getCourseName());
		TextView tvTitleLabel = holder.getView(R.id.tv_title_label);
		int role = TutorApplication.getRole();
		if (role == Constants.General.ROLE_STUDENT) {
			tvTitleLabel.setText(R.string.label_teacher);
		} else {
			tvTitleLabel.setText(R.string.label_label_student);
		}
		if (position == 0) {
			holder.getView(R.id.top_view).setVisibility(View.INVISIBLE);
		} else {
			holder.getView(R.id.top_view).setVisibility(View.VISIBLE);
		}
		if (position == getCount() - 1) {
			holder.getView(R.id.bottom_view).setVisibility(View.INVISIBLE);
		} else {
			holder.getView(R.id.bottom_view).setVisibility(View.VISIBLE);
		}
	}
}
