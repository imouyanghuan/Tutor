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
import com.tutor.model.BroadCastModel;
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
	private CustomListView timeListView, couresListView, lvAppointmentTimeslot;
	private TextView timeslotTip, courseTip, introduction;
	private Button btnRateTutor, btnPlayVideo;
	private Button btnToBeMyTutor;
	private LinearLayout llAppointmentTimeslot;
	private LinearLayout llIntroduction;
	private TextView tvStudentCount;
	private RatingBar rating;
	private TextView tvMajor;
	private TextView tvGraduateSchool;
	private TitleBar bar;
	private TextView tvGender;
	private ImageView ivAvatar;
	private TextView tvEducation;
	private int broadCastId = -1;
	private TextView tvNickName;
	private TextView tvIsCer, tvFollowCount;
	private ArrayList<CourseItem2> courseItem2s = new ArrayList<CourseItem2>();

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
		if (userInfo != null) {
			id = userInfo.getId();
			// imId = userInfo.getImid();
		} else {
			BroadCastModel broadCastModel = (BroadCastModel) intent.getSerializableExtra(Constants.General.BROADCAST_MODEL);
			if (broadCastModel != null) {
				id = broadCastModel.getMemberId();
				// 通过tag广播的id
				broadCastId = broadCastModel.getBroadcastId();
			}
		}
		// if (userInfo == null) {
		// return;
		// }
		// id = userInfo.getId();
		// // 聊天id
		// imId = userInfo.getImid();
		initView();
		getDetails();
	}

	@Override
	public void onBackPressed() {
		if (!TutorApplication.isMainActivity) {
			Intent intent = null;
			if (TutorApplication.getRole() == Constants.General.ROLE_STUDENT) {
				intent = new Intent(this, StudentMainActivity.class);
			} else {
				intent = new Intent(this, TeacherMainActivity.class);
			}
			startActivity(intent);
			this.finish();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void initView() {
		bar = getView(R.id.title_bar);
		// titleName = userInfo.getNickName();
		// if (TextUtils.isEmpty(titleName)) {
		// titleName = userInfo.getUserName();
		// if (TextUtils.isEmpty(titleName)) {
		// titleName = "Teacher Info";
		// }
		// }
		// bar.setTitle(titleName);
		// bar.initBack(this);
		bar.setBackBtnClick(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		bar.setRightButton(R.drawable.selector_like, new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (userInfo != null) {
					int userId = userInfo.getId();
					addToBookmark(userId);
				}
			}
		});
		// 是否认证
		tvIsCer = getView(R.id.tv_is_cer);
		// 关注度
		tvFollowCount = getView(R.id.tv_follow_count);
		// 评价老师
		btnRateTutor = getView(R.id.btn_rate_tutor);
		btnRateTutor.setOnClickListener(this);
		getView(R.id.btn_chat_with_tutor).setOnClickListener(this);
		btnToBeMyTutor = getView(R.id.btn_to_be_my_tutor);
		btnToBeMyTutor.setOnClickListener(this);
		btnPlayVideo = getView(R.id.btn_play_video);
		btnPlayVideo.setOnClickListener(this);
		// nick
		tvNickName = getView(R.id.tv_user_name);
		// gender
		tvGender = getView(R.id.tv_gender);
		// int genderStr;
		// if (userInfo.getGender() == null) {
		// genderStr = R.string.label_ignore1;
		// } else if (Constants.General.MALE == userInfo.getGender()) {
		// genderStr = R.string.label_male;
		// } else {
		// genderStr = R.string.label_female;
		// }
		// tvGender.setText(genderStr);
		// major
		tvMajor = getView(R.id.tv_major);
		// tvMajor.setText(!TextUtils.isEmpty(userInfo.getMajor()) ?
		// userInfo.getMajor() : "");
		// graduate school
		tvGraduateSchool = getView(R.id.tv_graduate_school);
		// tvGraduateSchool.setText(!TextUtils.isEmpty(userInfo.getGraduateSchool())
		// ? userInfo.getGraduateSchool() : "");
		// current education
		LinearLayout llEducation = getView(R.id.ll_education);
		if (TutorApplication.isCH()) {
			llEducation.setOrientation(LinearLayout.HORIZONTAL);
		} else {
			llEducation.setOrientation(LinearLayout.VERTICAL);
		}
		tvEducation = getView(R.id.tv_education);
		// int curEducation = userInfo.getEducationDegree();
		// switch (curEducation) {
		// case 0:
		// tvEducation.setText(getString(R.string.eb_1));
		// break;
		// case 1:
		// tvEducation.setText(getString(R.string.eb_2));
		// break;
		// case 2:
		// tvEducation.setText(getString(R.string.eb_3));
		// break;
		// case 3:
		// tvEducation.setText(getString(R.string.eb_4));
		// break;
		// }
		// experience
		// TextView tvExperience = getView(R.id.tv_tutor_experience);
		// String experience =
		// String.format(getString(R.string.label_experience_year2),
		// userInfo.getExprience());
		// tvExperience.setText(experience);
		// student count
		tvStudentCount = getView(R.id.tv_student_count);
		// tvStudentCount.setText(String.valueOf(userInfo.getStudentCount()));
		// rating
		rating = getView(R.id.ratingBar);
		// rating.setRating(userInfo.getRatingGrade());
		ivAvatar = getView(R.id.iv_avatar);
		// ImageUtils.loadImage(ivAvatar, ApiUrl.DOMAIN + userInfo.getAvatar());
		// address
		// TextView tvAddress = getView(R.id.tv_address);
		// tvAddress.setText(TextUtils.isEmpty(userInfo.getResidentialAddress())
		// ? getString(R.string.label_unknow_address) :
		// userInfo.getResidentialAddress());
		timeListView = getView(R.id.lv_timeslot);
		timeListView.setFocusable(false);
		// match time slot
		llAppointmentTimeslot = getView(R.id.ll_appointment_timeslot);
		lvAppointmentTimeslot = getView(R.id.lv_appointment_timeslot);
		lvAppointmentTimeslot.setFocusable(false);
		couresListView = getView(R.id.lv_coures);
		couresListView.setFocusable(false);
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
		llIntroduction = getView(R.id.ll_self_introduction);
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
		HttpHelper.getHelper().get(url, TutorApplication.getHeaders(), params, new ObjectHttpResponseHandler<AddBookmarkResult>(AddBookmarkResult.class) {

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
		showDialogRes(R.string.loading);
		RequestParams params = new RequestParams();
		params.put("memberId", id);
		HttpHelper.getHelper().get(ApiUrl.TUTORINFO, TutorApplication.getHeaders(), params, new ObjectHttpResponseHandler<UserInfoResult>(UserInfoResult.class) {

			@Override
			public void onFailure(int status, String message) {
				if (0 == status) {
					getDetails();
					return;
				}
				dismissDialog();
				if (CheckTokenUtils.checkToken(status)) {
					return;
				}
				// toast(R.string.toast_server_error);
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
		// 是否认证
		if (userInfo.isCertified()) {
			tvIsCer.setText(R.string.label_certified);
		} else {
			tvIsCer.setText(R.string.label_un_certified);
		}
		// 关注度
		tvFollowCount.setText(userInfo.getFollowCount());
		// im id
		imId = userInfo.getImid();
		// title
		titleName = userInfo.getNickName();
		if (TextUtils.isEmpty(titleName)) {
			titleName = userInfo.getUserName();
			if (TextUtils.isEmpty(titleName)) {
				titleName = "Teacher Info";
			}
		}
		bar.setTitle(titleName);
		// nick name
		tvNickName.setText(titleName);
		// gender
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
		tvMajor.setText(!TextUtils.isEmpty(userInfo.getMajor()) ? userInfo.getMajor() : "");
		// graduate school
		tvGraduateSchool.setText(!TextUtils.isEmpty(userInfo.getGraduateSchool()) ? userInfo.getGraduateSchool() : "");
		// student count
		tvStudentCount.setText(String.valueOf(userInfo.getStudentCount()));
		// rating
		rating.setRating(userInfo.getRatingGrade());
		// avatar
		ImageUtils.loadImage(ivAvatar, ApiUrl.DOMAIN + userInfo.getAvatar(), userInfo.getGender() != null ? userInfo.getGender() : Constants.General.MALE);
		// current education
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
		if (userInfo.isMatched()) {
			btnRateTutor.setText(R.string.label_rete_tutor);
			btnRateTutor.setTextSize(14);
			btnToBeMyTutor.setEnabled(false);
		} else {
			// 不是该老师的学生
			btnRateTutor.setText(R.string.label_see_comment);
			btnRateTutor.setTextSize(12);
			btnToBeMyTutor.setEnabled(true);
		}
		String introductionString = userInfo.getIntroduction();
		if (!TextUtils.isEmpty(introductionString)) {
			introduction.setText(introductionString);
			llIntroduction.setVisibility(View.VISIBLE);
		} else {
			introduction.setText("");
			llIntroduction.setVisibility(View.GONE);
		}
		String introductionVideoString = userInfo.getIntroductionVideo();
		if (TextUtils.isEmpty(introductionVideoString)) {
			btnPlayVideo.setEnabled(false);
		} else {
			btnPlayVideo.setEnabled(true);
		}
		// add by jerry on 11/13 学生数和评分: 传过来的userInfo有可能没有这个字段
		tvStudentCount.setText(String.valueOf(userInfo.getStudentCount()));
		rating.setRating(userInfo.getRatingGrade());
		tvMajor.setText(!TextUtils.isEmpty(userInfo.getMajor()) ? userInfo.getMajor() : "");
		String graduateStr = userInfo.getGraduateSchool();
		if (!TextUtils.isEmpty(userInfo.getGraduateSchool())) {
			tvGraduateSchool.setText(graduateStr);
			tvGraduateSchool.setVisibility(View.VISIBLE);
		} else {
			tvGraduateSchool.setText("");
			tvGraduateSchool.setVisibility(View.GONE);
		}
		// add end
		// 可接受时间段
		ArrayList<Timeslot> timeslots = userInfo.getTimeslots();
		if (timeslots != null && timeslots.size() > 0) {
			TimeSlotAdapter timeSlotAdapter = new TimeSlotAdapter(this, timeslots, false);
			timeListView.setAdapter(timeSlotAdapter);
		} else {
			timeListView.setVisibility(View.GONE);
			timeslotTip.setVisibility(View.VISIBLE);
		}
		if (userInfo.isMatched()) {
			// 匹配的时间段
			ArrayList<Timeslot> appointmentTimeslots = userInfo.getAppointmentTimeslots();
			if (appointmentTimeslots != null && appointmentTimeslots.size() > 0) {
				TimeSlotAdapter appointmentTimeslotsAdapter = new TimeSlotAdapter(this, appointmentTimeslots, false);
				lvAppointmentTimeslot.setAdapter(appointmentTimeslotsAdapter);
				llAppointmentTimeslot.setVisibility(View.VISIBLE);
			}
		} else {
			llAppointmentTimeslot.setVisibility(View.GONE);
		}
		// 课程
		ArrayList<Course> courses = userInfo.getCourses();
		if (null != courses && courses.size() > 0) {
			ArrayList<CourseItem2> tempCourseItem2s = getCourseItem2(courses);
			if (tempCourseItem2s != null && tempCourseItem2s.size() > 0) {
				courseItem2s.addAll(this.removeDuplicateData(tempCourseItem2s));
			}
			CourseListAdapter courseListAdapter = new CourseListAdapter(this, courseItem2s);
			couresListView.setAdapter(courseListAdapter);
		} else {
			couresListView.setVisibility(View.GONE);
			courseTip.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 去除重复课程名
	 * 
	 * @param data
	 * @return
	 */
	private ArrayList<CourseItem2> removeDuplicateData(ArrayList<CourseItem2> data) {
		// 上面写的那句是多余的，这个是最终的
		for (int i = 0; i < data.size() - 1; i++) {
			for (int j = i + 1; j < data.size(); j++) {
				if ((data.get(i).getCourseName().trim()).equals((data.get(j).getCourseName().trim()))) {
					data.remove(j);
					j--;
				}
			}
		}
		return data;
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
					boolean isFriend = ContactManager.getManager().addFriend(TeacherInfoActivity.this, imId.toLowerCase(), imId);
					if (isFriend) {
						Intent intent = new Intent(TeacherInfoActivity.this, ChatActivity.class);
						intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_MESSAGE_TO, imId.toLowerCase() + "@" + XMPPConnectionManager.getManager().getServiceName());
						intent.putExtra(Constants.General.NICKNAME, titleName);
						intent.putExtra(Constants.General.AVATAR, ApiUrl.DOMAIN + userInfo.getAvatar());
						startActivity(intent);
					} else {
						// toast
						toast(R.string.chat_service_not_available);
					}
				}
				break;
			// to be my tutor
			case R.id.btn_to_be_my_tutor:
				if (null != userInfo) {
					Intent intent = new Intent(TeacherInfoActivity.this, ToBeMyStudentActivity.class);
					intent.putExtra(Constants.General.BROADCAST_ID, broadCastId);
					intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_USER_INFO, userInfo);
					startActivity(intent);
				}
				break;
			// rate tutor
			case R.id.btn_rate_tutor:
				if (userInfo == null)
					return;
				if (userInfo.isMatched()) {
					Intent intent = new Intent(TeacherInfoActivity.this, RateTutorActivity.class);
					intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_USER_INFO, userInfo);
					startActivity(intent);
				} else {
					Intent intent = new Intent(TeacherInfoActivity.this, RateCommentListActivity.class);
					intent.putExtra(Constants.General.TUTOR_ID, userInfo.getId());
					intent.putExtra("role", userInfo.getAccountType());
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
