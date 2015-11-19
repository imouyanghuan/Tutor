package com.tutor.adapter;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hk.tutor.R;
import com.tutor.model.ActivityModel;

public class ActivitiesAdapter extends TutorBaseAdapter<ActivityModel> {

	public ActivitiesAdapter(Context mContext, List<ActivityModel> mData) {
		super(mContext, mData, R.layout.listview_activities_item);
	}

	public void refresh(List<ActivityModel> newData) {
		this.mList = newData;
		notifyDataSetChanged();
	}

	@Override
	protected void convert(ViewHolder holder, final ActivityModel activity, int position) {
		// time
		TextView tvTime = holder.getView(R.id.tv_show_time);
		String tempStartTime = activity.getStartTime();
		String startTime = "";
		if (!TextUtils.isEmpty(tempStartTime)) {
			startTime = tempStartTime.substring(0, 5);
		}
		String tempEndTime = activity.getEndTime();
		String endTime = "";
		if (!TextUtils.isEmpty(tempEndTime)) {
			endTime = tempEndTime.substring(0, 5);
		}
		String time = startTime + " - " + endTime;
		tvTime.setText(time);
		if (position > 0 && activity.isSameTime(getItem(position - 1))) {
			tvTime.setVisibility(View.INVISIBLE);
		} else {
			tvTime.setVisibility(View.VISIBLE);
		}
		// title label
		TextView tvTitleLabel = holder.getView(R.id.tv_title_label);
		tvTitleLabel.setText("Topic : ");
		// title
		TextView tvUserName = holder.getView(R.id.tv_title);
		tvUserName.setText(activity.getTitle());
		// desc label
		LinearLayout llDesc = holder.getView(R.id.ll_desc);
		llDesc.setOrientation(LinearLayout.VERTICAL);
		TextView tvCourseLabel = holder.getView(R.id.tv_course_label);
		tvCourseLabel.setText("Introduction : ");
		// desc
		TextView tvCourse = holder.getView(R.id.tv_course);
		String tempDesc = activity.getDescription();
		String desc = "";
		if (!TextUtils.isEmpty(tempDesc)) {
			desc = Html.fromHtml(tempDesc).toString();
			desc = Html.fromHtml(desc).toString();
		}
		tvCourse.setText(desc);
		// show or hide view
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
		holder.getView(R.id.btn_edit).setVisibility(View.GONE);
	}
}
