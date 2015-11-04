package com.tutor.ui.activity;

import java.net.HttpURLConnection;
import java.util.ArrayList;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.hk.tutor.R;
import com.loopj.android.http.RequestParams;
import com.mssky.mobile.ui.view.CustomListView;
import com.tutor.TutorApplication;
import com.tutor.adapter.CourseListAdapter;
import com.tutor.adapter.TimeSlotAdapter;
import com.tutor.im.ContactManager;
import com.tutor.im.XMPPConnectionManager;
import com.tutor.model.AddBookmarkResult;
import com.tutor.model.Course;
import com.tutor.model.CourseItem1;
import com.tutor.model.CourseItem2;
import com.tutor.model.Timeslot;
import com.tutor.model.UserInfo;
import com.tutor.model.UserInfoResult;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.CheckTokenUtils;
import com.tutor.util.HttpHelper;
import com.tutor.util.ImageUtils;
import com.tutor.util.ObjectHttpResponseHandler;

/**
 * 老师詳情界面
 * 
 * @author bruce.chen
 * 
 *         2015-8-24
 */
public class TeacherInfoActivity extends BaseActivity implements OnClickListener {

	private int id;
	private String imId;
	private UserInfo userInfo;
	private String titleName;
	private CustomListView timeListView, couresListView;
	private TextView timeslotTip, courseTip, introduction;
	private Button btnRateTutor, btnPlayVideo;
	private Button btnToBeMyTutor;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_teacher_info);
		initData();
	}

	private void initData() {
		Intent intent = getIntent();
		if (intent == null) {
			return;
		}
		userInfo = (UserInfo) intent.getSerializableExtra(Constants.IntentExtra.INTENT_EXTRA_USER_INFO);
		if (userInfo == null) {
			return;
		}
		id = userInfo.getId();
		// 聊天id
		imId = userInfo.getImid();
		initView();
		showDialogRes(R.string.loading);
		getDetails();
	}

	@Override
	protected void initView() {
		TitleBar bar = getView(R.id.title_bar);
		titleName = userInfo.getNickName();
		if (TextUtils.isEmpty(titleName)) {
			titleName = userInfo.getUserName();
			if (TextUtils.isEmpty(titleName)) {
				titleName = "Teacher Info";
			}
		}
		bar.setTitle(titleName);
		bar.initBack(this);
		bar.setRightButton(R.drawable.selector_like, new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (userInfo != null) {
					int userId = userInfo.getId();
					addToBookmark(userId);
				}
			}
		});
		// 评价老师
		btnRateTutor = getView(R.id.btn_rate_tutor);
		btnRateTutor.setOnClickListener(this);
		getView(R.id.btn_chat_with_tutor).setOnClickListener(this);
		btnToBeMyTutor = getView(R.id.btn_to_be_my_tutor);
		btnToBeMyTutor.setOnClickListener(this);
		btnPlayVideo = getView(R.id.btn_play_video);
		btnPlayVideo.setOnClickListener(this);
		// nick
		TextView tvNick = getView(R.id.tv_user_name);
		tvNick.setText(titleName);
		// gender
		TextView tvGender = getView(R.id.tv_gender);
		int genderStr;
		if (userInfo.getGender() == null) {
			genderStr = R.string.label_ignore1;
		} else if (Constants.General.MALE == userInfo.getGender()) {
			genderStr = R.string.label_male;
		} else {
			genderStr = R.string.label_female;
		}
		tvGender.setText(genderStr);
		// major
		TextView tvMajor = getView(R.id.tv_major);
		tvMajor.setText(!TextUtils.isEmpty(userInfo.getMajor()) ? userInfo.getMajor() : "");
		// graduate school
		TextView tvGraduateSchool = getView(R.id.tv_graduate_school);
		tvGraduateSchool.setText(!TextUtils.isEmpty(userInfo.getGraduateSchool()) ? userInfo.getGraduateSchool() : "");
		// current education
		LinearLayout llEducation = getView(R.id.ll_education);
		if (TutorApplication.isCH()) {
			llEducation.setOrientation(LinearLayout.HORIZONTAL);
		} else {
			llEducation.setOrientation(LinearLayout.VERTICAL);
		}
		TextView tvEducation = getView(R.id.tv_education);
		int curEducation = userInfo.getEducationDegree();
		switch (curEducation) {
			case 0:
				tvEducation.setText(getString(R.string.eb_1));
				break;
			case 1:
				tvEducation.setText(getString(R.string.eb_2));
				break;
			case 2:
				tvEducation.setText(getString(R.string.eb_3));
				break;
			case 3:
				tvEducation.setText(getString(R.string.eb_4));
				break;
		}
		// experience
		// TextView tvExperience = getView(R.id.tv_tutor_experience);
		// String experience =
		// String.format(getString(R.string.label_experience_year2),
		// userInfo.getExprience());
		// tvExperience.setText(experience);
		// student count
		TextView tvStudentCount = getView(R.id.tv_student_count);
		tvStudentCount.setText(String.valueOf(userInfo.getStudentCount()));
		// rating
		RatingBar rating = getView(R.id.ratingBar);
		rating.setRating(userInfo.getRatingGrade());
		ImageView ivAvatar = getView(R.id.iv_avatar);
		ImageUtils.loadImage(ivAvatar, ApiUrl.DOMAIN + userInfo.getAvatar());
		// address
		// TextView tvAddress = getView(R.id.tv_address);
		// tvAddress.setText(TextUtils.isEmpty(userInfo.getResidentialAddress())
		// ? getString(R.string.label_unknow_address) :
		// userInfo.getResidentialAddress());
		timeListView = getView(R.id.lv_timeslot);
		couresListView = getView(R.id.lv_coures);
		timeslotTip = getView(R.id.tv_timeslot_tip);
		courseTip = getView(R.id.tv_course_tip);
		//
		// TextView tvPhone = getView(R.id.tv_phone);
		// 电话号码
		// String phone = userInfo.getPhone();
		// if (TextUtils.isEmpty(phone)) {
		// tvPhone.setText(R.string.label_unknow_phone);
		// } else {
		// tvPhone.setText(phone);
		// }
		// TextView tvCurEducationStatus =
		// getView(R.id.tv_current_education_status);
		// int eduStatus = userInfo.getEducationStatus();
		// if (eduStatus == Constants.General.STUDYING) {
		// tvCurEducationStatus.setText(getString(R.string.label_studying));
		// } else {
		// tvCurEducationStatus.setText(getString(R.string.label_graduated));
		// }
		introduction = getView(R.id.tv_self_introduction);
	}

	/**
	 * 添加到Bookmark
	 * 
	 */
	private void addToBookmark(int userId) {
		if (!HttpHelper.isNetworkConnected(this)) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		RequestParams params = new RequestParams();
		String url = String.format(ApiUrl.BOOTMARK_ADD_TUTOR, String.valueOf(userId));
		HttpHelper.get(this, url, TutorApplication.getHeaders(), params, new ObjectHttpResponseHandler<AddBookmarkResult>(AddBookmarkResult.class) {

			@Override
			public void onFailure(int status, String message) {
				toast(R.string.toast_server_error);
			}

			@Override
			public void onSuccess(AddBookmarkResult result) {
				if (result.getStatusCode() == HttpURLConnection.HTTP_OK) {
					toast(R.string.toast_add_to_bookmark_success);
				} else if (result.getStatusCode() == HttpURLConnection.HTTP_INTERNAL_ERROR) {
					toast(result.getMessage());
				}
			}
		});
	}

	/**
	 * 获取老师详细资料 
	 * 
	 * @param id
	 */
	private void getDetails() {
		RequestParams params = new RequestParams();
		params.put("memberId", id);
		HttpHelper.get(this, ApiUrl.TUTORINFO, TutorApplication.getHeaders(), params, new ObjectHttpResponseHandler<UserInfoResult>(UserInfoResult.class) {

			@Override
			public void onFailure(int status, String message) {
				if (0 == status) {
					getDetails();
					return;
				}
				dismissDialog();
				CheckTokenUtils.checkToken(status);
				toast(R.string.toast_server_error);
			}

			@Override
			public void onSuccess(UserInfoResult t) {
				dismissDialog();
				if (t.getStatusCode() == HttpURLConnection.HTTP_OK) {
					userInfo = t.getResult();
					setData();
				} else {
					toast(t.getMessage());
				}
			}
		});
	}

	private void setData() {
		if (userInfo == null) {
			return;
		}
		if (userInfo.isMatched()) {
			btnRateTutor.setEnabled(true);
			btnToBeMyTutor.setEnabled(false);
		} else {
			btnRateTutor.setEnabled(false);
			btnToBeMyTutor.setEnabled(true);
		}
		String introductionString = userInfo.getIntroduction();
		if (TextUtils.isEmpty(introductionString)) {
			introduction.setText(introductionString);
		} else {
			introduction.setText("");
		}
		String introductionVideoString = userInfo.getIntroductionVideo();
		if (TextUtils.isEmpty(introductionVideoString)) {
			btnPlayVideo.setEnabled(false);
		} else {
			btnPlayVideo.setEnabled(true);
		}
		// 时间段
		ArrayList<Timeslot> timeslots = userInfo.getTimeslots();
		if (timeslots != null && timeslots.size() > 0) {
			TimeSlotAdapter timeSlotAdapter = new TimeSlotAdapter(this, timeslots, false);
			timeListView.setAdapter(timeSlotAdapter);
		} else {
			timeListView.setVisibility(View.GONE);
			timeslotTip.setVisibility(View.VISIBLE);
		}
		// 课程
		ArrayList<Course> courses = userInfo.getCourses();
		if (null != courses && courses.size() > 0) {
			ArrayList<CourseItem2> courseItem2s = getCourseItem2(courses);
			CourseListAdapter courseListAdapter = new CourseListAdapter(this, courseItem2s);
			couresListView.setAdapter(courseListAdapter);
		} else {
			couresListView.setVisibility(View.GONE);
			courseTip.setVisibility(View.VISIBLE);
		}
	}

	private ArrayList<CourseItem2> getCourseItem2(ArrayList<Course> courses) {
		ArrayList<CourseItem2> list = new ArrayList<CourseItem2>();
		for (Course course : courses) {
			for (CourseItem1 item1 : course.getResult()) {
				list.addAll(item1.getResult());
			}
		}
		return list;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// chat with student
			case R.id.btn_chat_with_tutor:
				if (!TextUtils.isEmpty(imId)) {
					boolean isFriend = ContactManager.getManager().addFriend(imId.toLowerCase(), imId);
					if (isFriend) {
						Intent intent = new Intent(TeacherInfoActivity.this, ChatActivity.class);
						intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_MESSAGE_TO, imId.toLowerCase() + "@" + XMPPConnectionManager.getManager().getServiceName());
						intent.putExtra(Constants.General.NICKNAME, titleName);
						intent.putExtra(Constants.General.AVATAR, ApiUrl.DOMAIN + userInfo.getAvatar());
						startActivity(intent);
					}
				}
				break;
			// to be my student
			case R.id.btn_to_be_my_tutor:
				if (null != userInfo) {
					Intent intent = new Intent(TeacherInfoActivity.this, ToBeMyStudentActivity.class);
					intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_USER_INFO, userInfo);
					startActivity(intent);
				}
				break;
			// rate tutor
			case R.id.btn_rate_tutor:
				if (userInfo != null) {
					Intent intent = new Intent(TeacherInfoActivity.this, RateTutorActivity.class);
					intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_USER_INFO, userInfo);
					startActivity(intent);
				}
				break;
			// 播放视频
			case R.id.btn_play_video:
				if (userInfo != null && !TextUtils.isEmpty(userInfo.getIntroductionVideo())) {
					try {
						Intent it = new Intent(Intent.ACTION_VIEW);
						Uri uri = Uri.parse(userInfo.getIntroductionVideo());
						it.setDataAndType(uri, "video/*");
						startActivity(it);
					} catch (Exception e) {
						e.printStackTrace();
						toast(R.string.toast_video_unplay);
					}
				}
				break;
			default:
				break;
		}
	}
}
