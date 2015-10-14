package com.tutor.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.tutor.R;
import com.tutor.model.UserInfo;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.activity.StudentInfoActivity;
import com.tutor.util.ImageUtils;

/**
 * 匹配學生列表適配器
 * 
 * @author bruce.chen
 * 
 *         2015-9-7
 */
public class MatchStudentAdapter extends TutorBaseAdapter<UserInfo> {

	public MatchStudentAdapter(Context mContext, List<UserInfo> mData) {
		super(mContext, mData, R.layout.student_list_item);
	}

	public void refresh(List<UserInfo> mData) {
		this.mList = mData;
		notifyDataSetChanged();
	}

	@Override
	protected void convert(ViewHolder holder, final UserInfo t, int position) {
		ImageView avatar = holder.getView(R.id.student_list_item_avatar);
		String path = ApiUrl.DOMAIN + t.getAvatar();
		ImageUtils.loadImage(avatar, path);
		if (!TextUtils.isEmpty(t.getNickName())) {
			holder.setText(R.id.student_list_item_nick, t.getNickName());
		} else if (!TextUtils.isEmpty(t.getUserName())) {
			holder.setText(R.id.student_list_item_nick, t.getUserName());
		}
		if (!TextUtils.isEmpty(t.getResidentialAddress())) {
			holder.setText(R.id.student_list_item_area, t.getResidentialAddress());
		} else {
			holder.setText(R.id.student_list_item_area, R.string.label_unknow_address);
		}
		String gradeName = t.getGradeName();
		if (!TextUtils.isEmpty(gradeName)) {
			holder.setText(R.id.student_list_item_form, gradeName);
		} else {
			holder.setText(R.id.student_list_item_form, ""/*
														 * R.string.
														 * label_unknow_grade
														 */);
		}
		holder.setText(R.id.student_list_item_time, t.getCreatedTime().toString().substring(0, 11));
		// 學生item點擊事件
		holder.getView(R.id.student_list_item_layout).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(mContext, StudentInfoActivity.class);
				intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_USER_INFO, t);
				mContext.startActivity(intent);
			}
		});
	}
}
