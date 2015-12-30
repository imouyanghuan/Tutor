package com.tutor.ui.activity;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import android.content.Intent;
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
import com.tutor.adapter.ServiceAreasListAdapter;
import com.tutor.adapter.ServiceGradeListAdapter;
import com.tutor.model.Area;
import com.tutor.model.Area1;
import com.tutor.model.BroadCastModel;
import com.tutor.model.EditProfileResult;
import com.tutor.model.Notification;
import com.tutor.model.ServiceGrade;
import com.tutor.model.UserInfo;
import com.tutor.model.UserInfoResult;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.CheckTokenUtils;
import com.tutor.util.HttpHelper;
import com.tutor.util.ImageUtils;
import com.tutor.util.JsonUtil;
import com.tutor.util.ObjectHttpResponseHandler;

/**
 * 补习社詳情界面
 * 
 * @author jerry.yao
 * 
 *         2015-12-11
 */
public class TuitionCenterInfoActivity extends BaseActivity implements OnClickListener {

	private int id;
	// private String imId;
	private UserInfo userInfo;
	private String titleName;
	private CustomListView lvServiceGrade, lvArea;
	private TextView serviceGradeTip, areaTip, introduction;
	private Button btnPlayVideo;
	private Button btnJoinTuitionCenter;
	private LinearLayout llIntroduction;
	private TextView tvStudentCount;
	private RatingBar rating;
	private TitleBar bar;
	private ImageView ivAvatar;
	private int broadCastId = -1;
	private TextView tvNickName;
	private TextView tvEmail;
	private TextView tvTelNumber;
	private TextView tvAddress;
	private LinearLayout llRate;
	private ImageView ivPan;
	private TextView tvRate;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_tuition_center_info);
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
		titleName = userInfo.getNickName();
		if (TextUtils.isEmpty(titleName)) {
			titleName = userInfo.getUserName();
			if (TextUtils.isEmpty(titleName)) {
				titleName = "Tuition Center Info";
			}
		}
		bar.setTitle(titleName);
		bar.setBackBtnClick(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		// email
		tvEmail = getView(R.id.tv_email);
		// 电话
		tvTelNumber = getView(R.id.tv_tel_number);
		// 地址
		tvAddress = getView(R.id.tv_tuition_center_address);
		// 评价老师
		llRate = getView(R.id.ll_rate);
		llRate.setOnClickListener(this);
		ivPan = getView(R.id.iv_pan);
		tvRate = getView(R.id.tv_rate);
		getView(R.id.btn_chat_with_tutor).setOnClickListener(this);
		btnJoinTuitionCenter = getView(R.id.btn_join_tuition_center);
		btnJoinTuitionCenter.setOnClickListener(this);
		btnPlayVideo = getView(R.id.btn_play_video);
		btnPlayVideo.setOnClickListener(this);
		// nick
		tvNickName = getView(R.id.tv_user_name);
		// gender
		// tvGender = getView(R.id.tv_gender);
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
		// tvMajor = getView(R.id.tv_major);
		// tvMajor.setText(!TextUtils.isEmpty(userInfo.getMajor()) ?
		// userInfo.getMajor() : "");
		// graduate school
		// tvGraduateSchool = getView(R.id.tv_graduate_school);
		// tvGraduateSchool.setText(!TextUtils.isEmpty(userInfo.getGraduateSchool())
		// ? userInfo.getGraduateSchool() : "");
		// current education
		// LinearLayout llEducation = getView(R.id.ll_education);
		// if (TutorApplication.isCH()) {
		// llEducation.setOrientation(LinearLayout.HORIZONTAL);
		// } else {
		// llEducation.setOrientation(LinearLayout.VERTICAL);
		// }
		// tvEducation = getView(R.id.tv_education);
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
		tvStudentCount.setText(String.valueOf(userInfo.getStudentCount()));
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
		lvServiceGrade = getView(R.id.lv_timeslot);
		lvServiceGrade.setFocusable(false);
		// match time slot
		// llAppointmentTimeslot = getView(R.id.ll_appointment_timeslot);
		// lvAppointmentTimeslot = getView(R.id.lv_appointment_timeslot);
		// lvAppointmentTimeslot.setFocusable(false);
		lvArea = getView(R.id.lv_coures);
		lvArea.setFocusable(false);
		serviceGradeTip = getView(R.id.tv_timeslot_tip);
		areaTip = getView(R.id.tv_course_tip);
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
	 * 获取老师详细资料
	 * 
	 * @param id
	 */
	private void getDetails() {
		showDialogRes(R.string.loading);
		RequestParams params = new RequestParams();
		params.put("memberId", id);
		HttpHelper.getHelper().get(ApiUrl.TUITION_CENTER_INFO, TutorApplication.getHeaders(), params, new ObjectHttpResponseHandler<UserInfoResult>(UserInfoResult.class) {

			@Override
			public void onFailure(int status, String message) {
				if (0 == status) {
					// getDetails();
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
		tvEmail.setText(!TextUtils.isEmpty(userInfo.getContactEmail()) ? userInfo.getContactEmail() : getString(R.string.label_none));
		tvTelNumber.setText(!TextUtils.isEmpty(userInfo.getPhone()) ? userInfo.getPhone() : getString(R.string.label_none));
		tvAddress.setText(!TextUtils.isEmpty(userInfo.getResidentialAddress()) ? userInfo.getResidentialAddress() : getString(R.string.label_none));
		// im id
		// imId = userInfo.getImid();
		// title
		titleName = userInfo.getNickName();
		if (TextUtils.isEmpty(titleName)) {
			titleName = userInfo.getUserName();
			if (TextUtils.isEmpty(titleName)) {
				titleName = "Tuition Center Info";
			}
		}
		bar.setTitle(titleName);
		// nick name
		tvNickName.setText(titleName);
		// gender
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
		// tvMajor.setText(!TextUtils.isEmpty(userInfo.getMajor()) ?
		// userInfo.getMajor() : "");
		// graduate school
		// tvGraduateSchool.setText(!TextUtils.isEmpty(userInfo.getGraduateSchool())
		// ? userInfo.getGraduateSchool() : "");
		// student count
		tvStudentCount.setText(String.valueOf(userInfo.getStudentCount()));
		// rating
		rating.setRating(userInfo.getRatingGrade());
		// avatar
		ImageUtils.loadImage(ivAvatar, ApiUrl.DOMAIN + userInfo.getAvatar(), userInfo.getGender() != null ? userInfo.getGender() : Constants.General.MALE);
		// current education
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
		if (userInfo.isMatched()) {
			llRate.setEnabled(true);
			ivPan.setEnabled(true);
			tvRate.setEnabled(true);
			btnJoinTuitionCenter.setEnabled(false);
		} else {
			llRate.setEnabled(false);
			ivPan.setEnabled(false);
			tvRate.setEnabled(false);
			btnJoinTuitionCenter.setEnabled(true);
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
		// tvStudentCount.setText(String.valueOf(userInfo.getStudentCount()));
		// rating.setRating(userInfo.getRatingGrade());
		// tvMajor.setText(!TextUtils.isEmpty(userInfo.getMajor()) ?
		// userInfo.getMajor() : "");
		// tvGraduateSchool.setText(!TextUtils.isEmpty(userInfo.getGraduateSchool())
		// ? userInfo.getGraduateSchool() : "");
		// add end
		// 服务年级
		ArrayList<ServiceGrade> serviceGrades = userInfo.getServiceGrades();
		if (serviceGrades != null && serviceGrades.size() > 0) {
			ServiceGradeListAdapter serviceGradeAdapter = new ServiceGradeListAdapter(this, serviceGrades);
			lvServiceGrade.setAdapter(serviceGradeAdapter);
		} else {
			lvServiceGrade.setVisibility(View.GONE);
			serviceGradeTip.setVisibility(View.VISIBLE);
		}
		// if (userInfo.isMatched()) {
		// // 匹配的时间段
		// ArrayList<Timeslot> appointmentTimeslots =
		// userInfo.getAppointmentTimeslots();
		// if (appointmentTimeslots != null && appointmentTimeslots.size() > 0)
		// {
		// TimeSlotAdapter appointmentTimeslotsAdapter = new
		// TimeSlotAdapter(this, appointmentTimeslots, false);
		// lvAppointmentTimeslot.setAdapter(appointmentTimeslotsAdapter);
		// llAppointmentTimeslot.setVisibility(View.VISIBLE);
		// }
		// } else {
		// llAppointmentTimeslot.setVisibility(View.GONE);
		// }
		// 服务地区
		ArrayList<Area> areas = userInfo.getAreas();
		if (null != areas && areas.size() > 0) {
			ArrayList<Area1> areas1List = new ArrayList<Area1>();
			for (int i = 0; i < areas.size(); i++) {
				ArrayList<Area1> areas1 = areas.get(i).getResult();
				if (areas1 != null && areas1.size() > 0) {
					areas1List.addAll(areas1);
				}
			}
			ServiceAreasListAdapter serviceAreaAdapter = new ServiceAreasListAdapter(this, areas1List);
			lvArea.setAdapter(serviceAreaAdapter);
		} else {
			lvArea.setVisibility(View.GONE);
			areaTip.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// join tuition center
			case R.id.btn_join_tuition_center:
				// 直接发送加入api
				joinRequest();
				break;
			// rate tutor
			case R.id.ll_rate:
				if (userInfo != null) {
					Intent intent = new Intent(TuitionCenterInfoActivity.this, RateTutorActivity.class);
					intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_USER_INFO, userInfo);
					startActivity(intent);
				}
				break;
			default:
				break;
		}
	}

	// 发送邀请
	private void joinRequest() {
		Notification notification = new Notification();
		notification.setBroadcastId(broadCastId);
		notification.setSource(TutorApplication.getMemberId());
		notification.setDestination(userInfo.getId());
		notification.setOrientation(Notification.ToBeMyStudent);
		notification.setContent(getString(R.string.to_be_my_student2));
		String body = JsonUtil.parseObject2Str(notification);
		showDialogRes(R.string.loading);
		try {
			StringEntity entity = new StringEntity(body, HTTP.UTF_8);
			HttpHelper.getHelper().post(ApiUrl.JOIN_TUITION_CENTER, TutorApplication.getHeaders(), entity, new ObjectHttpResponseHandler<EditProfileResult>(EditProfileResult.class) {

				@Override
				public void onFailure(int status, String message) {
					if (0 == status) {
						joinRequest();
						return;
					}
					dismissDialog();
					// toast(R.string.toast_server_error);
				}

				@Override
				public void onSuccess(EditProfileResult t) {
					dismissDialog();
					if (HttpURLConnection.HTTP_OK == t.getStatusCode() && t.getResult()) {
						toast(R.string.label_invite_success);
						// finish();
					} else {
						toast(t.getMessage());
					}
				}
			});
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
