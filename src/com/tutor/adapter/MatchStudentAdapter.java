package com.tutor.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.tutor.R;
import com.tutor.model.Area;
import com.tutor.model.Area1;
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
		if (TextUtils.isEmpty(t.getAvatar())) {
			ImageUtils.loadImage(avatar, "drawable://" + R.drawable.avatar);
		} else {
			String path = ApiUrl.DOMAIN + t.getAvatar();
			ImageUtils.loadImage(avatar, path);
		}
		if (TextUtils.isEmpty(t.getEmail())) {
			holder.setText(R.id.student_list_item_nick, "Student");
		} else {
			if (t.getEmail().contains("@")) {
				holder.setText(R.id.student_list_item_nick, t.getEmail().substring(0, t.getEmail().lastIndexOf("@")));
			} else {
				holder.setText(R.id.student_list_item_nick, t.getEmail());
			}
		}
		ArrayList<Area> areas = t.getAreas();
		if (null != areas && 0 != areas.size()) {
			StringBuffer sb = new StringBuffer();
			for (Area area : areas) {
				ArrayList<Area1> area1s = area.getResult();
				for (Area1 area1 : area1s) {
					sb.append(area1.getAddress() + ",");
				}
			}
			// 刪除最後一個符號
			sb.deleteCharAt(sb.length() - 1);
			holder.setText(R.id.student_list_item_area, sb.toString());
		} else {
			holder.setText(R.id.student_list_item_area, R.string.label_none_area);
		}
		holder.setText(R.id.student_list_item_form, t.getGender());
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
