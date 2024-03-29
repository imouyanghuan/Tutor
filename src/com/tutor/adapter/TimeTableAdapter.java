package com.tutor.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.hk.tutor.R;
import com.tutor.TutorApplication;
import com.tutor.model.TimeTableDetail;
import com.tutor.params.Constants;

public class TimeTableAdapter extends TutorBaseAdapter<TimeTableDetail> {

	public TimeTableAdapter(Context mContext, List<TimeTableDetail> mData) {
		super(mContext, mData, R.layout.listview_item);
	}

	public void refresh(List<TimeTableDetail> newData) {
		this.mList = newData;
		notifyDataSetChanged();
	}

	@Override
	protected void convert(ViewHolder holder, final TimeTableDetail detail, int position) {
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
		String courseName = detail.getCourseName();
		String p = "Primary School";
		String s = "Secondary School";
		if (!TextUtils.isEmpty(courseName)) {
			if (courseName.contains(p)) {
				courseName = courseName.replace(p, "P.");
			} else if (courseName.contains(s)) {
				courseName = courseName.replace(s, "S.");
			}
		}
		tvCourse.setText(courseName);
		TextView tvTitleLabel = holder.getView(R.id.tv_title_label);
		// adjust role
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
		// edit time table
		Button btnEdit = holder.getView(R.id.btn_edit);
		btnEdit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (onEditBtnClickListener != null) {
					onEditBtnClickListener.onEditBtnClick(detail);
				}
			}
		});
	}

	public interface OnEditBtnClickListener {

		public void onEditBtnClick(TimeTableDetail detail);
	}

	private OnEditBtnClickListener onEditBtnClickListener;

	/**
	 * 编辑按钮回调
	 * 
	 * @param onEditBtnClickListener
	 */
	public void setOnEditBtnClickListener(OnEditBtnClickListener onEditBtnClickListener) {
		this.onEditBtnClickListener = onEditBtnClickListener;
	}
}
