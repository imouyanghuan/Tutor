package com.tutor.ui.activity;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ListView;

import com.hk.tutor.R;
import com.loopj.android.http.RequestParams;
import com.tutor.TutorApplication;
import com.tutor.adapter.TimeTableAdapter;
import com.tutor.adapter.TimeTableAdapter.OnEditBtnClickListener;
import com.tutor.model.TimeTable;
import com.tutor.model.TimeTableDetail;
import com.tutor.model.TimeTableListResult;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.CheckTokenUtils;
import com.tutor.util.HttpHelper;
import com.tutor.util.ObjectHttpResponseHandler;

/**
 * 詳情界面
 * 
 * @author jerry.yao
 * 
 *         2015-10-28
 */
public class TimeTableDetailActivity extends BaseActivity {

	private ArrayList<TimeTableDetail> timeTableDetails;
	private String curDayOfWeek;
	private String weeks[] = null;
	private ArrayList<TimeTable> tables;
	private TimeTableAdapter timeTableAdapter;
	private ArrayList<TimeTableDetail> otherTimeTableDetails;
	private ArrayList<TimeTableDetail> sameTimeTables;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_time_table_detail);
		initData();
	}

	@SuppressWarnings("unchecked")
	private void initData() {
		Intent intent = getIntent();
		if (intent == null) {
			return;
		}
		weeks = getResources().getStringArray(R.array.time_table_detail_weeks);
		curDayOfWeek = intent.getStringExtra(Constants.IntentExtra.INTENT_EXTRA_DAY_OF_WEEK);
		tables = (ArrayList<TimeTable>) intent.getSerializableExtra(Constants.IntentExtra.INTENT_EXTRA_TIME_TABLE);
		if (tables != null && tables.size() > 0) {
			// 同一天的所有数据
			timeTableDetails = new ArrayList<TimeTableDetail>();
			for (int i = 0; i < tables.size(); i++) {
				ArrayList<TimeTableDetail> timeslots = tables.get(i).getTimeslots();
				if (timeslots != null && timeslots.size() > 0) {
					for (int j = 0; j < timeslots.size(); j++) {
						String dayOfWeek = weeks[timeslots.get(j).getDayOfWeek()];
						if (curDayOfWeek.equalsIgnoreCase(dayOfWeek)) {
							TimeTableDetail detail = new TimeTableDetail();
							detail.setId(tables.get(i).getId());
							detail.setCourseName(tables.get(i).getCourseName());
							detail.setUserName(tables.get(i).getUserName());
							detail.setDayOfWeek(timeslots.get(j).getDayOfWeek());
							detail.setEndHour(timeslots.get(j).getEndHour());
							detail.setEndMinute(timeslots.get(j).getEndMinute());
							detail.setStartHour(timeslots.get(j).getStartHour());
							detail.setStartMinute(timeslots.get(j).getStartMinute());
							timeTableDetails.add(detail);
						}
					}
				}
			}
			initView();
		}
	}

	@Override
	protected void initView() {
		TitleBar bar = getView(R.id.title_bar);
		if (!TextUtils.isEmpty(curDayOfWeek)) {
			bar.setTitle(curDayOfWeek);
		} else {
			bar.setTitle(R.string.label_time_table_detail);
		}
		bar.initBack(this);
		ListView listView = (ListView) this.findViewById(R.id.listview);
		listView.setDividerHeight(0);
		Collections.sort(timeTableDetails);
		timeTableAdapter = new TimeTableAdapter(this, timeTableDetails);
		listView.setAdapter(timeTableAdapter);
		// 编辑时间回调
		timeTableAdapter.setOnEditBtnClickListener(new OnEditBtnClickListener() {

			@Override
			public void onEditBtnClick(TimeTableDetail detail) {
				int currentId = detail.getId();
				// 同一个人的数据
				sameTimeTables = new ArrayList<TimeTableDetail>();
				for (int i = 0; i < timeTableDetails.size(); i++) {
					if (timeTableDetails.get(i).getId() == currentId) {
						sameTimeTables.add(timeTableDetails.get(i));
					}
				}
				// 过滤不是今天的
				if (tables != null && tables.size() > 0) {
					otherTimeTableDetails = new ArrayList<TimeTableDetail>();
					for (int i = 0; i < tables.size(); i++) {
						TimeTable table = tables.get(i);
						if (table.getId() == currentId) {
							ArrayList<TimeTableDetail> timeSlots = table.getTimeslots();
							if (timeSlots != null && timeSlots.size() > 0) {
								for (int j = 0; j < timeSlots.size(); j++) {
									TimeTableDetail editTime = timeSlots.get(j);
									if (!weeks[editTime.getDayOfWeek()].equalsIgnoreCase(curDayOfWeek)) {
										TimeTableDetail otherDetail = new TimeTableDetail();
										otherDetail.setId(table.getId());
										otherDetail.setCourseName(table.getCourseName());
										otherDetail.setUserName(table.getUserName());
										otherDetail.setDayOfWeek(editTime.getDayOfWeek());
										otherDetail.setEndHour(editTime.getEndHour());
										otherDetail.setEndMinute(editTime.getEndMinute());
										otherDetail.setStartHour(editTime.getStartHour());
										otherDetail.setStartMinute(editTime.getStartMinute());
										otherTimeTableDetails.add(otherDetail);
									}
								}
							}
						}
					}
				}
				Intent intent = new Intent(TimeTableDetailActivity.this, EditTimeTableActivity.class);
				intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_TIME_TABLE_LIST, otherTimeTableDetails);
				intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_TIME_TABLE_DETAIL, sameTimeTables);
				startActivityForResult(intent, Constants.RequestResultCode.TIME_TABLE_DETAIL);
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Constants.RequestResultCode.TIME_TABLE_DETAIL) {
			// refresh UI
			if (data == null) {
				return;
			}
			// 已经修改过的同一个人的所有time table
			ArrayList<TimeTableDetail> editedTimeTableDetail = (ArrayList<TimeTableDetail>) data.getSerializableExtra(Constants.IntentExtra.INTENT_EXTRA_TIME_TABLE_LIST);
			int currentWeek = data.getIntExtra(Constants.IntentExtra.INTENT_EXTRA_TIME_TABLE_WEEK, -1);
			if (editedTimeTableDetail != null && editedTimeTableDetail.size() > 0) {
				timeTableDetails.clear();
				for (int i = 0; i < editedTimeTableDetail.size(); i++) {
					if (editedTimeTableDetail.get(i).getDayOfWeek() == currentWeek) {
						timeTableDetails.add(editedTimeTableDetail.get(i));
					}
				}
				Collections.sort(timeTableDetails);
				timeTableAdapter.refresh(timeTableDetails);
				getTimeTable();
			} else {
				timeTableAdapter.refresh(null);
			}
			setResult(Constants.RequestResultCode.TIME_TABLE_REFRESH);
		}
	}

	private void getTimeTable() {
		RequestParams params = new RequestParams();
		HttpHelper.get(this, ApiUrl.TIME_TABLE, TutorApplication.getHeaders(), params, new ObjectHttpResponseHandler<TimeTableListResult>(TimeTableListResult.class) {

			@Override
			public void onFailure(int status, String message) {
				if (0 == status) {
					getTimeTable();
					return;
				}
				dismissDialog();
				if (CheckTokenUtils.checkToken(status)) {
					return;
				}
				toast(R.string.toast_server_error);
			}

			@Override
			public void onSuccess(TimeTableListResult t) {
				dismissDialog();
				if (t.getStatusCode() == HttpURLConnection.HTTP_OK) {
					ArrayList<TimeTable> tempTimeTables = t.getResult();
					if (tempTimeTables != null && tempTimeTables.size() > 0) {
						tables.clear();
						tables.addAll(tempTimeTables);
					}
				} else {
					toast(t.getMessage());
				}
			}
		});
	}
}
