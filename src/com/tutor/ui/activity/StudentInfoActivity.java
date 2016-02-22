package com.tutor.ui.activity;

import java.net.HttpURLConnection;
import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
 * 學生詳情界面
 * 
 * @author bruce.chen
 * 
 *         2015-8-24
 */
public class StudentInfoActivity extends BaseActivity implements OnClickListener {

	private int id;
	private String imId;
	private UserInfo userInfo;
	private ImageView avatarImageView;
	private TextView nickTextView, genderTextView/* , phoneTextView */, gradeTextView, /*
																					 * birthTextView
																					 * ,
																					 * addressTextView
																					 * ,
																					 */timeslotTip, courseTip, introduction;
	private CustomListView timeListView, couresListView, lvAppointmentTimeslot;
	private String titleName;
	private Button button;
	private LinearLayout llAppointmentTimeslot;
	private LinearLayout llIntroduction;
	private TitleBar bar;
	private int broadCastId = -1;
	private ArrayList<CourseItem2> courseItem2s = new ArrayList<CourseItem2>();

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_student_info);
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

	private void setData() {
		if (userInfo == null) {
			return;
		}
		// title
		titleName = userInfo.getNickName();
		if (TextUtils.isEmpty(titleName)) {
			titleName = userInfo.getUserName();
			if (TextUtils.isEmpty(titleName)) {
				titleName = "Student Info";
			}
		}
		bar.setTitle(titleName);
		// nick name
		nickTextView.setText(titleName);
		// im id
		imId = userInfo.getImid();
		// avatar
		ImageUtils.loadImage(avatarImageView, ApiUrl.DOMAIN + userInfo.getAvatar(), userInfo.getGender() != null ? userInfo.getGender() : Constants.General.MALE);
		if (userInfo.isMatched()) {
			button.setEnabled(false);
		} else {
			button.setEnabled(true);
		}
		String introductionString = userInfo.getIntroduction();
		if (!TextUtils.isEmpty(introductionString)) {
			introduction.setText(introductionString);
			llIntroduction.setVisibility(View.VISIBLE);
		} else {
			introduction.setText("");
			llIntroduction.setVisibility(View.GONE);
		}
		int genderStr;
		if (userInfo.getGender() == null) {
			genderStr = R.string.label_ignore1;
		} else if (Constants.General.MALE == userInfo.getGender()) {
			genderStr = R.string.label_male;
		} else {
			genderStr = R.string.label_female;
		}
		genderTextView.setText(genderStr);
		// 电话号码
		// String phone = userInfo.getPhone();
		// if (TextUtils.isEmpty(phone)) {
		// phoneTextView.setText(R.string.label_unknow_phone);
		// } else {
		// phoneTextView.setText(phone);
		// }
		String gradeName = userInfo.getGradeName();
		if (!TextUtils.isEmpty(gradeName)) {
			gradeTextView.setText(gradeName);
		} else {
			gradeTextView.setText("");
		}
		// birthTextView.setText(TextUtils.isEmpty(userInfo.getBirth()) ?
		// getString(R.string.label_unknow) : userInfo.getBirth().substring(0,
		// 11));
		// addressTextView.setText(TextUtils.isEmpty(userInfo.getResidentialAddress())
		// ? getString(R.string.label_unknow_address) :
		// userInfo.getResidentialAddress());
		// 可接受的时间段
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
		for (int i = 0; i < data.size() - 1; i++) {
			for (int j = i + 1; j < data.size(); j++) {
				if ((data.get(i).getCourseName()).equals((data.get(j).getCourseName()))) {
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
	protected void initView() {
		bar = getView(R.id.title_bar);
		// titleName = userInfo.getNickName();
		// if (TextUtils.isEmpty(titleName)) {
		// titleName = userInfo.getUserName();
		// if (TextUtils.isEmpty(titleName)) {
		// titleName = "Student Info";
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
		getView(R.id.btn_chat_with_tutor).setOnClickListener(this);
		button = getView(R.id.btn_to_be_my_student);
		button.setOnClickListener(this);
		nickTextView = getView(R.id.tv_user_name);
		genderTextView = getView(R.id.tv_gender);
		gradeTextView = getView(R.id.tv_grade);
		// phoneTextView = getView(R.id.tv_phone);
		// birthTextView = getView(R.id.tv_birth);
		// addressTextView = getView(R.id.tv_address);
		// match time slot
		llAppointmentTimeslot = getView(R.id.ll_appointment_timeslot);
		lvAppointmentTimeslot = getView(R.id.lv_appointment_timeslot);
		lvAppointmentTimeslot.setFocusable(false);
		timeListView = getView(R.id.lv_timeslot);
		timeListView.setFocusable(false);
		couresListView = getView(R.id.lv_coures);
		couresListView.setFocusable(false);
		timeslotTip = getView(R.id.tv_timeslot_tip);
		courseTip = getView(R.id.tv_course_tip);
		avatarImageView = getView(R.id.iv_avatar);
		// ImageUtils.loadImage(avatarImageView, ApiUrl.DOMAIN +
		// userInfo.getAvatar());
		// self introduction
		llIntroduction = getView(R.id.ll_self_introduction);
		introduction = getView(R.id.tv_self_introduction);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// chat with student
			case R.id.btn_chat_with_tutor:
				if (!TextUtils.isEmpty(imId)) {
					boolean isFriend = ContactManager.getManager().addFriend(StudentInfoActivity.this, imId.toLowerCase(), imId);
					if (isFriend) {
						Intent intent = new Intent(StudentInfoActivity.this, ChatActivity.class);
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
			// to be my student
			case R.id.btn_to_be_my_student:
				if (null != userInfo) {
					Intent intent = new Intent(StudentInfoActivity.this, ToBeMyStudentActivity.class);
					intent.putExtra(Constants.General.BROADCAST_ID, broadCastId);
					intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_USER_INFO, userInfo);
					startActivity(intent);
				}
				break;
			default:
				break;
		}
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
		showDialogRes(R.string.loading);
		RequestParams params = new RequestParams();
		String url = String.format(ApiUrl.BOOTMARK_ADD_STUDENT, String.valueOf(userId));
		HttpHelper.getHelper().get(url, TutorApplication.getHeaders(), params, new ObjectHttpResponseHandler<AddBookmarkResult>(AddBookmarkResult.class) {

			@Override
			public void onFailure(int status, String message) {
				dismissDialog();
				toast(R.string.toast_server_error);
			}

			@Override
			public void onSuccess(AddBookmarkResult result) {
				dismissDialog();
				int statusCode = result.getStatusCode();
				if (statusCode == HttpURLConnection.HTTP_OK) {
					toast(R.string.toast_add_to_bookmark_success);
				} else if (statusCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
					toast(result.getMessage());
				}
			}
		});
	}

	/**
	 * 获取学生详细资料
	 * 
	 * @param id
	 */
	private void getDetails() {
		showDialogRes(R.string.loading);
		RequestParams params = new RequestParams();
		params.put("memberId", id);
		HttpHelper.getHelper().get(ApiUrl.STUDENTINFO, TutorApplication.getHeaders(), params, new ObjectHttpResponseHandler<UserInfoResult>(UserInfoResult.class) {

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
}
