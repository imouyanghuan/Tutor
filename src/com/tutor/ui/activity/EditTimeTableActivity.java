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
import android.widget.TextView;

import com.hk.tutor.R;
import com.mssky.mobile.ui.view.CustomListView;
import com.tutor.TutorApplication;
import com.tutor.adapter.EditTimeSlotAdapter;
import com.tutor.model.EditProfileResult;
import com.tutor.model.EditTime;
import com.tutor.model.EditTimeslot;
import com.tutor.model.TimeTableDetail;
import com.tutor.model.UpdateNotification;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.dialog.TimeSlotDialog;
import com.tutor.ui.dialog.TimeSlotDialog.onTimeSelectedCallBack;
import com.tutor.ui.dialog.WeekDialog;
import com.tutor.ui.dialog.WeekDialog.OnWeekSelectedCallback;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.CheckTokenUtils;
import com.tutor.util.HttpHelper;
import com.tutor.util.JsonUtil;
import com.tutor.util.ObjectHttpResponseHandler;

/**
 * 编辑time table界面
 * 
 * @author jerry.yao
 * 
 *         2015-11-03
 */
public class EditTimeTableActivity extends BaseActivity implements OnClickListener, onTimeSelectedCallBack, OnWeekSelectedCallback {

	private ArrayList<TimeTableDetail> details;
	int curStatus = -1;
	private TextView weekTextView, startTimeTextView, endTimeTextView;
	/** 保存時間 */
	private Button saveTime;
	private ArrayList<EditTimeslot> timeslots;
	private ArrayList<EditTimeslot> otherTimeslots;
	private EditTimeslot timeslot = null;
	/** 时间段列表 */
	private CustomListView listView, lvOthers;
	private EditTimeSlotAdapter adapter;
	private EditTimeSlotAdapter otherAdapter;
	// 是否编辑开始时间
	private boolean isStrat;
	private ArrayList<TimeTableDetail> otherTimetable;
	ArrayList<EditTimeslot> allTime = new ArrayList<EditTimeslot>();

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_edit_time_table);
		initTitleBar();
		initData();
	}

	private void initTitleBar() {
		TitleBar bar = getView(R.id.title_bar);
		bar.setTitle(R.string.label_edit_time_table);
		bar.initBack(this);
	}

	@SuppressWarnings("unchecked")
	private void initData() {
		Intent intent = getIntent();
		if (intent == null) {
			return;
		}
		details = (ArrayList<TimeTableDetail>) intent.getSerializableExtra(Constants.IntentExtra.INTENT_EXTRA_TIME_TABLE_DETAIL);
		if (details == null) {
			return;
		}
		// 同一个人 但不是今天的时间段
		otherTimetable = (ArrayList<TimeTableDetail>) intent.getSerializableExtra(Constants.IntentExtra.INTENT_EXTRA_TIME_TABLE_LIST);
		timeslots = new ArrayList<EditTimeslot>();
		for (int i = 0; i < details.size(); i++) {
			EditTimeslot timeslot = new EditTimeslot();
			TimeTableDetail detail = details.get(i);
			timeslot.setId(detail.getId());
			timeslot.setDayOfWeek(detail.getDayOfWeek());
			timeslot.setEndHour(detail.getEndHour());
			timeslot.setEndMinute(detail.getEndMinute());
			timeslot.setStartHour(detail.getStartHour());
			timeslot.setStartMinute(detail.getStartMinute());
			timeslots.add(timeslot);
		}
		if (otherTimetable != null && otherTimetable.size() > 0) {
			otherTimeslots = new ArrayList<EditTimeslot>();
			for (int i = 0; i < otherTimetable.size(); i++) {
				EditTimeslot timeslot = new EditTimeslot();
				TimeTableDetail detail = otherTimetable.get(i);
				timeslot.setId(detail.getId());
				timeslot.setDayOfWeek(detail.getDayOfWeek());
				timeslot.setEndHour(detail.getEndHour());
				timeslot.setEndMinute(detail.getEndMinute());
				timeslot.setStartHour(detail.getStartHour());
				timeslot.setStartMinute(detail.getStartMinute());
				otherTimeslots.add(timeslot);
			}
		}
		initView();
	}

	@Override
	protected void initView() {
		TextView tvRoleLabel = getView(R.id.tv_role_label);
		// adjust role
		int role = TutorApplication.getRole();
		if (role == Constants.General.ROLE_STUDENT) {
			tvRoleLabel.setText(R.string.label_teacher);
		} else {
			tvRoleLabel.setText(R.string.label_label_student);
		}
		TextView tvRole = getView(R.id.tv_role);
		tvRole.setText(details.get(0).getUserName());
		TextView tvCourse = getView(R.id.tv_course);
		String courseName = details.get(0).getCourseName();
		String p = "Primary School";
		String s = "Secondary School";
		if (courseName.contains(p)) {
			courseName = courseName.replace(p, "P.");
		} else if (courseName.contains(s)) {
			courseName = courseName.replace(s, "S.");
		}
		tvCourse.setText(courseName);		
		// time slot
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
		adapter = new EditTimeSlotAdapter(this, timeslots, true);
		listView.setAdapter(adapter);
		// 其他时间段
		lvOthers = getView(R.id.other_timeslot_lv);
		otherAdapter = new EditTimeSlotAdapter(this, otherTimeslots, false);
		lvOthers.setAdapter(otherAdapter);
		// 保存
		getView(R.id.btn_save).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.ac_fill_personal_info_btn_save_timeslot:
				if (null != timeslot) {
					if (null == timeslots) {
						timeslots = new ArrayList<EditTimeslot>();
					}
					allTime.clear();
					allTime.addAll(timeslots);
					if (otherTimeslots != null) {
						allTime.addAll(otherTimeslots);
					}
					if (allTime.size() > 0) {
						// 检查冲突时间
						for (EditTimeslot item : allTime) {
							if (item.isRepeat(timeslot)) {
								// 有冲突.提示
								toast(R.string.toast_timeslot_has_a_conflict);
								return;
							}
						}
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
				WeekDialog weekDialog;
				if (null != timeslot) {
					weekDialog = new WeekDialog(this, timeslot.getDayOfWeek(), this);
				} else {
					weekDialog = new WeekDialog(this, 0, this);
				}
				weekDialog.show();
				break;
			case R.id.ac_fill_personal_time_tv_start:
				isStrat = true;
				TimeSlotDialog timeSlotDialog;
				if (null != timeslot) {
					timeSlotDialog = new TimeSlotDialog(this, timeslot.getEndHour(), timeslot.getEndMinute(), this);
				} else {
					timeSlotDialog = new TimeSlotDialog(this, 0, 0, this);
				}
				timeSlotDialog.show();
				break;
			case R.id.ac_fill_personal_time_tv_end:
				isStrat = false;
				TimeSlotDialog timeSlotDialog1;
				if (null != timeslot) {
					timeSlotDialog1 = new TimeSlotDialog(this, timeslot.getStartHour(), timeslot.getStartMinute(), this);
				} else {
					timeSlotDialog1 = new TimeSlotDialog(this, 0, 0, this);
				}
				timeSlotDialog1.show();
				break;
			case R.id.btn_save:
				// save
				saveTimeTable();
				break;
			default:
				break;
		}
	}

	/**
	 * 保存修改
	 */
	private void saveTimeTable() {
		allTime.clear();
		if(timeslots != null){
			allTime.addAll(timeslots);			
		}
		if (otherTimeslots != null) {
			allTime.addAll(otherTimeslots);
		}
		if (allTime != null && allTime.size() > 0) {
			EditTime eidt = new EditTime();
			eidt.setTimeslots(allTime);
			String body = JsonUtil.parseObject2Str(eidt);
			StringEntity entity = null;
			try {
				entity = new StringEntity(body, HTTP.UTF_8);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			String url = String.format(ApiUrl.TIME_TABLE_UPDATE, details.get(0).getId());
			HttpHelper.put(this, url, TutorApplication.getHeaders(), entity, new ObjectHttpResponseHandler<EditProfileResult>(EditProfileResult.class) {

				@Override
				public void onFailure(int status, String message) {
					if (status == 0) {
						saveTimeTable();
						return;
					}
					toast(R.string.label_edit_failed);
				}

				@Override
				public void onSuccess(EditProfileResult result) {
					if (result.getStatusCode() == HttpURLConnection.HTTP_OK) {
						toast(R.string.toast_save_successed);
						setResult(Constants.RequestResultCode.TIME_TABLE_DETAIL);
						EditTimeTableActivity.this.finish();
					} else if (result.getStatusCode() == 500) {
						toast(result.getMessage());
					}
				}
			});
		} else {
			toast(R.string.toast_no_timeslot);
		}
	}

	/**
	 * 修改时间段
	 */
	public void updateTimeTable(final UpdateNotification notify) {
		String body = JsonUtil.parseObject2Str(notify);
		StringEntity entity = null;
		try {
			entity = new StringEntity(body, HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		HttpHelper.put(this, ApiUrl.NOTIFICATION_UPDATE, TutorApplication.getHeaders(), entity, new ObjectHttpResponseHandler<EditProfileResult>(EditProfileResult.class) {

			@Override
			public void onFailure(int status, String message) {
				if (0 == status) {
					updateTimeTable(notify);
					return;
				}
				CheckTokenUtils.checkToken(status);
			}

			@Override
			public void onSuccess(EditProfileResult result) {
				CheckTokenUtils.checkToken(result);
				if (result.getStatusCode() == 200) {}
			}
		});
	}

	@Override
	public int onTimeSelected(String time, int hour, int minute) {
		if (null == timeslot) {
			timeslot = new EditTimeslot();
		}
		if (isStrat) {
			// 在编辑开始时间的时候已经编辑好了结束时间,需要检查开始时间是否在结束时间之前
			if (0 != timeslot.getEndHour() || 0 != timeslot.getEndMinute()) {
				if (0 != timeslot.getEndHour() && hour < timeslot.getEndHour()) {
					timeslot.setStartHour(hour);
					timeslot.setStartMinute(minute);
					startTimeTextView.setText(time);
					check();
					return 0;
				} else if (hour == timeslot.getEndHour() && minute < timeslot.getEndMinute()) {
					timeslot.setStartHour(hour);
					timeslot.setStartMinute(minute);
					startTimeTextView.setText(time);
					check();
					return 0;
				} else if (0 == timeslot.getEndHour()) {
					if (hour > 0 || hour == 0 && minute < timeslot.getEndMinute()) {
						timeslot.setStartHour(hour);
						timeslot.setStartMinute(minute);
						startTimeTextView.setText(time);
						check();
						return 0;
					}
				}
				return R.string.toast_starttime_front_endtime;
			} else {
				// 没有编辑结束时间,直接设值
				timeslot.setStartHour(hour);
				timeslot.setStartMinute(minute);
				startTimeTextView.setText(time);
				check();
			}
		} else {
			// 结束时间
			if (0 != timeslot.getStartHour() || 0 != timeslot.getStartMinute()) {
				if (0 != timeslot.getStartHour() && hour > timeslot.getStartHour()) {
					timeslot.setEndHour(hour);
					timeslot.setEndMinute(minute);
					endTimeTextView.setText(time);
					check();
					return 0;
				} else if (hour == timeslot.getStartHour() && timeslot.getStartMinute() < minute) {
					timeslot.setEndHour(hour);
					timeslot.setEndMinute(minute);
					endTimeTextView.setText(time);
					check();
					return 0;
				} else if (0 == timeslot.getStartHour()) {
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
			timeslot = new EditTimeslot();
		}
		timeslot.setDayOfWeek(index);
		weekTextView.setText(value);
		check();
	}
}
