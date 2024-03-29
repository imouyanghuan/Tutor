package com.tutor.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.hk.tutor.R;
import com.tutor.model.UserInfo;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.activity.TeacherInfoActivity;
import com.tutor.util.ImageUtils;

/**
 * @author bruce.chen
 * 
 *         2015-10-12
 */
public class TeacherListAdapter extends TutorBaseAdapter<UserInfo> {

	public TeacherListAdapter(Context mContext, List<UserInfo> mData) {
		super(mContext, mData, R.layout.teacher_list_item2);
	}

	public void refresh(List<UserInfo> mData) {
		this.mList = mData;
		notifyDataSetChanged();
	}

	@Override
	protected void convert(ViewHolder holder, final UserInfo t, int position) {
		ImageView avatar = holder.getView(R.id.teacher_list_item_avatar);
		ImageUtils.loadImage(avatar, ApiUrl.DOMAIN + t.getAvatar(), t.getGender() != null ? t.getGender() : Constants.General.MALE);
		// 昵称
		String nick = t.getNickName();
		if (TextUtils.isEmpty(nick)) {
			nick = t.getUserName();
		}
		holder.setText(R.id.teacher_list_item_nick, nick);
		// 评分
		RatingBar ratingBar = holder.getView(R.id.teacher_list_item_rating);
		ratingBar.setRating(t.getRatingGrade());
		// 学生数量
		holder.setText(R.id.teacher_list_item_studentNumber, String.format(mContext.getResources().getString(R.string.student_count), t.getStudentCount() + ""));
		// 老师专业
		holder.setText(R.id.teacher_list_item_coures, !TextUtils.isEmpty(t.getMajor()) ? t.getMajor() : "");
		// 是否认证
		if (t.isCertified()) {
			holder.getView(R.id.ll_is_cer).setVisibility(View.VISIBLE);
		} else {
			holder.getView(R.id.ll_is_cer).setVisibility(View.INVISIBLE);
		}
		// 关注度
		holder.setText(R.id.tv_follow_count, t.getFollowCount());
		// 设置点击监听
		holder.getView(R.id.teacher_list_item_layout).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(mContext, TeacherInfoActivity.class);
				intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_USER_INFO, t);
				mContext.startActivity(intent);
			}
		});
	}
}
