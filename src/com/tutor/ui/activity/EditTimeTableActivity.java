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

	private ArrayList<TimeTableDetail> sameTimeTable;
	int curStatus = -1;
	private TextView weekTextView, startTimeTextView, endTimeTextView;
	/** 保存時間 */
	private Button saveTime;
	// private ArrayList<EditTimeslot> sameTimeslots;
	// private ArrayList<EditTimeslot> otherTimeslots;
	private TimeTableDetail timeslot = null;
	/** 时间段列表 */
	private CustomListView lvSameDay, lvOtherDay;
	private EditTimeSlotAdapter sameAdapter;
	private EditTimeSlotAdapter otherAdapter;
	// 是否编辑开始时间
	private boolean isStrat;
	private ArrayList<TimeTableDetail> otherTimetable;
	ArrayList<TimeTableDetail> allTime = new ArrayList<TimeTableDetail>();
	private String courseName;
	private String userName;
	private int curTimeTableId;
	private int curWeek;

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
		sameTimeTable = (ArrayList<TimeTableDetail>) intent.getSerializableExtra(Constants.IntentExtra.INTENT_EXTRA_TIME_TABLE_DETAIL);
		if (sameTimeTable == null || sameTimeTable.size() <= 0) {
			return;
		}
		// 同一个人 但不是今天的时间段
		otherTimetable = (ArrayList<TimeTableDetail>) intent.getSerializableExtra(Constants.IntentExtra.INTENT_EXTRA_TIME_TABLE_LIST);
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
		curTimeTableId = sameTimeTable.get(0).getId();
		curWeek = sameTimeTable.get(0).getDayOfWeek();
		userName = sameTimeTable.get(0).getUserName();
		tvRole.setText(userName);
		TextView tvCourse = getView(R.id.tv_course);
		courseName = sameTimeTable.get(0).getCourseName();
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
		lvSameDay = getView(R.id.ac_fill_personal_info_timeslot_lv);
		sameAdapter = new EditTimeSlotAdapter(this, sameTimeTable, true);
		lvSameDay.setAdapter(sameAdapter);
		// 其他时间段
		lvOtherDay = getView(R.id.other_timeslot_lv);
		otherAdapter = new EditTimeSlotAdapter(this, otherTimetable, false);
		lvOtherDay.setAdapter(otherAdapter);
		// 保存
		getView(R.id.btn_save).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.ac_fill_personal_info_btn_save_timeslot:
				if (null != timeslot) {
					if (null == sameTimeTable) {
						sameTimeTable = new ArrayList<TimeTableDetail>();
					}
					allTime.clear();
					allTime.addAll(sameTimeTable);
					if (otherTimetable != null) {
						allTime.addAll(otherTimetable);
					}
					if (allTime.size() > 0) {
						// 检查冲突时间
						for (TimeTableDetail item : allTime) {
							if (item.isRepeat(timeslot)) {
								// 有冲突.提示
								toast(R.string.toast_timeslot_has_a_conflict);
								return;
							}
						}
					}
					// 新增的timeslot 得添加课程名称和当前用户
					timeslot.setId(curTimeTableId);
					timeslot.setCourseName(courseName);
					timeslot.setUserName(userName);
					sameTimeTable.add(timeslot);
					sameAdapter.refresh(sameTimeTable);
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
		if (sameTimeTable != null) {
			allTime.addAll(sameTimeTable);
		}
		if (otherTimetable != null) {
			allTime.addAll(otherTimetable);
		}
		if (allTime != null && allTime.size() > 0) {
			if (!HttpHelper.isNetworkConnected(this)) {
				toast(R.string.toast_netwrok_disconnected);
				return;
			}
			showDialogRes(R.string.loading);
			EditTime eidt = new EditTime();
			eidt.setTimeslots(allTime);
			String body = JsonUtil.parseObject2Str(eidt);
			StringEntity entity = null;
			try {
				entity = new StringEntity(body, HTTP.UTF_8);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			String url = String.format(ApiUrl.TIME_TABLE_UPDATE, curTimeTableId);
			HttpHelper.getHelper().put(url, TutorApplication.getHeaders(), entity, new ObjectHttpResponseHandler<EditProfileResult>(EditProfileResult.class) {

				// private ArrayList<TimeTableDetail> sameDayTimeTable;
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
						// 过滤是同一天的再把结果返回
						Intent intent = new Intent();
						intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_TIME_TABLE_LIST, allTime);
						intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_TIME_TABLE_WEEK, curWeek);
						setResult(Constants.RequestResultCode.TIME_TABLE_DETAIL, intent);
						EditTimeTableActivity.this.finish();
					} else if (result.getStatusCode() == 500) {
						toast(result.getMessage());
					}
					dismissDialog();
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
		HttpHelper.getHelper().put(ApiUrl.NOTIFICATION_UPDATE, TutorApplication.getHeaders(), entity, new ObjectHttpResponseHandler<EditProfileResult>(EditProfileResult.class) {

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
			timeslot = new TimeTableDetail();
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
			timeslot = new TimeTableDetail();
		}
		timeslot.setDayOfWeek(index);
		weekTextView.setText(value);
		check();
	}
}
