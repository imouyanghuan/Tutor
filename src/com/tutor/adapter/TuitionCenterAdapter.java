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
import com.tutor.ui.activity.TuitionCenterInfoActivity;
import com.tutor.util.ImageUtils;

/**
 * 补习社適配器
 * 
 * @author jerry.yao
 * 
 */
public class TuitionCenterAdapter extends TutorBaseAdapter<UserInfo> {
	public TuitionCenterAdapter(Context mContext, List<UserInfo> mData) {
		super(mContext, mData, R.layout.layout_tuition_center_item);
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

		// 设置点击监听
		holder.getView(R.id.teacher_list_item_layout).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(mContext, TuitionCenterInfoActivity.class);
				intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_USER_INFO, t);
				mContext.startActivity(intent);
			}
		});
	}

//	private String getCourse(UserInfo t) {
//		StringBuffer sb = new StringBuffer();
//		ArrayList<Course> courses = t.getCourses();
//		if (null != courses && courses.size() > 0) {
//			for (Course course : courses) {
//				ArrayList<CourseItem1> item1s = course.getResult();
//				if (null != item1s && item1s.size() > 0) {
//					for (CourseItem1 item1 : item1s) {
//						ArrayList<CourseItem2> item2s = item1.getResult();
//						if (null != item2s && item2s.size() > 0) {
//							for (CourseItem2 item2 : item2s) {
//								String courseType = item2.getType();
//								if (courseType.contains("Primary School")) {
//									courseType = "P.";
//								} else if (courseType.contains("Secondary School")) {
//									courseType = "S.";
//								}
//								sb.append(courseType + "-" + item2.getSubType() + "-" + item2.getCourseName() + ",");
//							}
//						}
//					}
//				}
//			}
//			// 去掉最后一个逗号
//			if (sb.length() > 0) {
//				sb.delete(sb.length() - 1, sb.length());
//			}
//		}
//		return sb.toString();
//	}
}
