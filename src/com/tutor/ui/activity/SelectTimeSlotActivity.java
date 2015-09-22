package com.tutor.ui.activity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.mssky.mobile.ui.view.CustomListView;
import com.tutor.R;
import com.tutor.TutorApplication;
import com.tutor.adapter.TimeSlotAdapter;
import com.tutor.model.EditProfileResult;
import com.tutor.model.StudentProfile;
import com.tutor.model.TeacherProfile;
import com.tutor.model.Timeslot;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.dialog.TimeSlotDialog;
import com.tutor.ui.dialog.TimeSlotDialog.onTimeSelectedCallBack;
import com.tutor.ui.dialog.WeekDialog;
import com.tutor.ui.dialog.WeekDialog.OnWeekSelectedCallback;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.HttpHelper;
import com.tutor.util.JsonUtil;
import com.tutor.util.LogUtils;
import com.tutor.util.ObjectHttpResponseHandler;

/**
 * 选择时间段
 * 
 * @author bruce.chen
 * 
 *         2015-9-18
 */
public class SelectTimeSlotActivity extends BaseActivity implements OnClickListener, onTimeSelectedCallBack, OnWeekSelectedCallback {

	/** 是否是编辑资料模式 */
	private boolean isEdit;
	private int role;
	private TeacherProfile teacherProfile;
	private StudentProfile studentProfile;
	private ArrayList<Timeslot> timeslots;
	/** 时间段列表 */
	private CustomListView listView;
	private TimeSlotAdapter adapter;
	// 添加时间对话框
	private TimeSlotDialog timeSlotDialog;
	// 星期对话框
	private WeekDialog weekDialog;
	//
	private TextView weekTextView, startTimeTextView, endTimeTextView;
	/** 保存時間 */
	private Button saveTime;
	/** 教育背景 */
	private RadioGroup ebRadioGroup;
	/** 教育背景 布局 */
	private LinearLayout ebFrameLayout;
	/** 教育背景 */
	private int eb;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		isEdit = getIntent().getBooleanExtra(Constants.IntentExtra.INTENT_EXTRA_ISEDIT, false);
		teacherProfile = (TeacherProfile) getIntent().getSerializableExtra(Constants.IntentExtra.INTENT_EXTRA_TUTORPRIFILE);
		studentProfile = (StudentProfile) getIntent().getSerializableExtra(Constants.IntentExtra.INTENT_EXTRA_STUDENTPROFILE);
		if (null != teacherProfile) {
			role = teacherProfile.getAccountType();
		} else {
			role = studentProfile.getAccountType();
		}
		if (role == -1) {
			throw new IllegalArgumentException("role is -1");
		}
		setContentView(R.layout.activity_select_timeslot);
		initView();
		if (isEdit) {
			if (null != teacherProfile) {
				eb = teacherProfile.getEducationDegree();
			}
		} else {
			eb = 0;
		}
	}

	@Override
	protected void initView() {
		TitleBar bar = getView(R.id.title_bar);
		bar.initBack(this);
		if (isEdit) {
			bar.setTitle(R.string.label_edit_timeslot);
		} else {
			bar.setTitle(R.string.label_add_timeslot);
		}
		bar.setRightText(R.string.btn_submit, new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 时间列表
				if (null == timeslots || 0 == timeslots.size()) {
					toast(R.string.toast_no_timeslot);
					return;
				}
				if (null != teacherProfile) {
					teacherProfile.setTimeslots(timeslots);
					teacherProfile.setEducationDegree(eb);
					submitTutorProfile(teacherProfile);
				} else {
					studentProfile.setTimeslots(timeslots);
					submitStudentProfile(studentProfile);
				}
			}
		});
		ebFrameLayout = getView(R.id.ac_fill_personal_info_ll_eb);
		ebRadioGroup = getView(R.id.ac_fill_personal_info_rg_eb);
		if (Constants.General.ROLE_TUTOR == role) {
			ebFrameLayout.setVisibility(View.VISIBLE);
			ebRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					switch (checkedId) {
						case R.id.ac_fill_personal_info_rb_eb1:
							eb = 0;
							break;
						case R.id.ac_fill_personal_info_rb_eb2:
							eb = 1;
							break;
						case R.id.ac_fill_personal_info_rb_eb3:
							eb = 2;
							break;
						case R.id.ac_fill_personal_info_rb_eb4:
							eb = 3;
							break;
					}
				}
			});
			if (isEdit && null != teacherProfile) {
				ebRadioGroup.check(teacherProfile.getEducationDegree());
			}
		} else {
			ebFrameLayout.setVisibility(View.GONE);
		}
		weekTextView = getView(R.id.ac_fill_personal_time_tv_week);
		weekTextView.setOnClickListener(this);
		startTimeTextView = getView(R.id.ac_fill_personal_time_tv_start);
		startTimeTextView.setOnClickListener(this);
		endTimeTextView = getView(R.id.ac_fill_personal_time_tv_end);
		endTimeTextView.setOnClickListener(this);
		saveTime = getView(R.id.ac_fill_personal_info_btn_save_timeslot);
		saveTime.setOnClickListener(this);
		// 时间段列表
		listView = getView(R.id.ac_fill_personal_info_timeslot_lv);
		if (isEdit) {
			if (null != teacherProfile) {
				timeslots = teacherProfile.getTimeslots();
			} else {
				timeslots = studentProfile.getTimeslots();
			}
		}
		adapter = new TimeSlotAdapter(this, timeslots, true);
		listView.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.ac_fill_personal_info_btn_save_timeslot:
				if (null != timeslot) {
					if (null == timeslots) {
						timeslots = new ArrayList<Timeslot>();
					}
					if (null != teacherProfile) {
						timeslot.setMemberId(teacherProfile.getId());
					} else {
						timeslot.setMemberId(studentProfile.getId());
					}
					timeslots.add(timeslot);
					adapter.refresh(timeslots);
					saveTime.setEnabled(false);
					weekTextView.setText("");
					startTimeTextView.setText("");
					endTimeTextView.setText("");
					timeslot = null;
				}
				break;
			case R.id.ac_fill_personal_time_tv_week:
				weekDialog = new WeekDialog(this, this);
				weekDialog.show();
				if (null != timeslot) {
					weekDialog.setSelectIndex(timeslot.getDayOfWeek());
				}
				break;
			case R.id.ac_fill_personal_time_tv_start:
				isStrat = true;
				timeSlotDialog = new TimeSlotDialog(this, this);
				timeSlotDialog.show();
				if (null != timeslot) {
					timeSlotDialog.setParams(timeslot.getEndHour(), timeslot.getEndMinute());
				}
				break;
			case R.id.ac_fill_personal_time_tv_end:
				isStrat = false;
				timeSlotDialog = new TimeSlotDialog(this, this);
				timeSlotDialog.show();
				if (null != timeslot) {
					timeSlotDialog.setParams(timeslot.getStarHour(), timeslot.getStartMinute());
				}
				break;
			default:
				break;
		}
	}

	// 是否编辑开始时间
	private boolean isStrat;
	private Timeslot timeslot = null;

	@Override
	public int onTimeSelected(String time, int hour, int minute) {
		if (null == timeslot) {
			timeslot = new Timeslot();
		}
		if (isStrat) {
			// 在编辑开始时间的时候已经编辑好了结束时间,需要检查开始时间是否在结束时间之前
			if (0 != timeslot.getEndHour() || 0 != timeslot.getEndMinute()) {
				if (0 != timeslot.getEndHour() && hour < timeslot.getEndHour()) {
					timeslot.setStarHour(hour);
					timeslot.setStartMinute(minute);
					startTimeTextView.setText(time);
					check();
					return 0;
				} else if (hour == timeslot.getEndHour() && minute < timeslot.getEndMinute()) {
					timeslot.setStarHour(hour);
					timeslot.setStartMinute(minute);
					startTimeTextView.setText(time);
					check();
					return 0;
				} else if (0 == timeslot.getEndHour()) {
					if (hour > 0 || hour == 0 && minute < timeslot.getEndMinute()) {
						timeslot.setStarHour(hour);
						timeslot.setStartMinute(minute);
						startTimeTextView.setText(time);
						check();
						return 0;
					}
				}
				return R.string.toast_starttime_front_endtime;
			} else {
				// 没有编辑结束时间,直接设值
				timeslot.setStarHour(hour);
				timeslot.setStartMinute(minute);
				startTimeTextView.setText(time);
				check();
			}
		} else {
			// 结束时间
			if (0 != timeslot.getStarHour() || 0 != timeslot.getStartMinute()) {
				if (0 != timeslot.getStarHour() && hour > timeslot.getStarHour()) {
					timeslot.setEndHour(hour);
					timeslot.setEndMinute(minute);
					endTimeTextView.setText(time);
					check();
					return 0;
				} else if (hour == timeslot.getStarHour() && timeslot.getStartMinute() < minute) {
					timeslot.setEndHour(hour);
					timeslot.setEndMinute(minute);
					endTimeTextView.setText(time);
					check();
					return 0;
				} else if (0 == timeslot.getStarHour()) {
					if (hour > 0 || minute > timeslot.getStartMinute()) {
						timeslot.setEndHour(hour);
						timeslot.setEndMinute(minute);
						endTimeTextView.setText(time);
						check();
						return 0;
					}
				}
				return R.string.toast_endtime_behind_starttime;
			} else {
				// 没有编辑开始时间,直接设值
				timeslot.setEndHour(hour);
				timeslot.setEndMinute(minute);
				endTimeTextView.setText(time);
				check();
			}
		}
		check();
		return 0;
	}

	private void check() {
		if (!TextUtils.isEmpty(weekTextView.getText()) && !TextUtils.isEmpty(startTimeTextView.getText()) && !TextUtils.isEmpty(endTimeTextView.getText())) {
			saveTime.setEnabled(true);
		}
	}

	@Override
	public void onWeekSelected(int index, String value) {
		if (null == timeslot) {
			timeslot = new Timeslot();
		}
		timeslot.setDayOfWeek(index);
		weekTextView.setText(value);
		check();
	}

	@Override
	protected void onStart() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.Action.FINISH_LOGINACTIVITY);
		registerReceiver(receiver, filter);
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (Constants.Action.FINISH_LOGINACTIVITY.equals(intent.getAction())) {
				finishNoAnim();
			}
		}
	};

	/**
	 * 進入主界面
	 */
	private void go2Main() {
		if (!isEdit) {
			Intent intent = new Intent();
			if (Constants.General.ROLE_TUTOR == role) {
				intent.setClass(this, TeacherMainActivity.class);
			} else {
				intent.setClass(this, StudentMainActivity.class);
			}
			// 保存信息
			TutorApplication.settingManager.writeSetting(Constants.SharedPreferences.SP_ISLOGIN, true);
			TutorApplication.settingManager.writeSetting(Constants.SharedPreferences.SP_ROLE, role);
			// 進入主界面
			startActivity(intent);
		}
		// 发送广播结束编辑界面
		Intent finish = new Intent();
		finish.setAction(Constants.Action.FINISH_LOGINACTIVITY);
		sendBroadcast(finish);
	}

	/**
	 * 提交老师信息任务
	 * 
	 */
	private void submitTutorProfile(final TeacherProfile profile) {
		if (!HttpHelper.isNetworkConnected(this)) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		showDialogRes(R.string.loading);
		String json = JsonUtil.parseObject2Str(profile);
		LogUtils.d(json);
		try {
			StringEntity entity = new StringEntity(json, HTTP.UTF_8);
			HttpHelper.put(this, ApiUrl.TUTORPROFILE, TutorApplication.getHeaders(), entity, new ObjectHttpResponseHandler<EditProfileResult>(EditProfileResult.class) {

				@Override
				public void onFailure(int status, String message) {
					if (0 == status) {
						submitTutorProfile(profile);
						return;
					}
					dismissDialog();
					LogUtils.e(message);
					toast(R.string.toast_server_error);
				}

				@Override
				public void onSuccess(EditProfileResult result) {
					dismissDialog();
					if (null != result) {
						if (200 == result.getStatusCode() && result.getResult()) {
							go2Main();
						} else {
							toast(result.getMessage());
						}
					} else {
						toast(R.string.toast_server_error);
					}
				}
			});
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			dismissDialog();
		}
	}

	/**
	 * 提交学生信息任务
	 * 
	 */
	private void submitStudentProfile(final StudentProfile profile) {
		if (!HttpHelper.isNetworkConnected(this)) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		showDialogRes(R.string.loading);
		String json = JsonUtil.parseObject2Str(profile);
		LogUtils.d(json);
		try {
			StringEntity entity = new StringEntity(json, HTTP.UTF_8);
			HttpHelper.put(this, ApiUrl.STUDENTPROFILE, TutorApplication.getHeaders(), entity, new ObjectHttpResponseHandler<EditProfileResult>(EditProfileResult.class) {

				@Override
				public void onFailure(int status, String message) {
					if (0 == status) {
						submitStudentProfile(profile);
						return;
					}
					dismissDialog();
					LogUtils.e(message);
					toast(R.string.toast_server_error);
				}

				@Override
				public void onSuccess(EditProfileResult result) {
					dismissDialog();
					if (null != result) {
						if (200 == result.getStatusCode() && result.getResult()) {
							go2Main();
						} else {
							toast(result.getMessage());
						}
					} else {
						toast(R.string.toast_server_error);
					}
				}
			});
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			dismissDialog();
		}
	}
}
