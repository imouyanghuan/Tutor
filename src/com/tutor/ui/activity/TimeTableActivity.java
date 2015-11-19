package com.tutor.ui.activity;

import java.io.Serializable;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.hk.tutor.R;
import com.loopj.android.http.RequestParams;
import com.tutor.TutorApplication;
import com.tutor.adapter.GridViewAdapter;
import com.tutor.adapter.GridViewAdapter.OnTimeTableClickListener;
import com.tutor.model.ActivityListResult;
import com.tutor.model.ActivityModel;
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
 * 时间表页面
 * 
 * @author jerry.yao
 * 
 */
public class TimeTableActivity extends BaseActivity {

	// -----------------------------组件----------------------
	private GridView mGridView;
	private TitleBar titleBar;
	private DisplayMetrics dm;
	// 适配器
	private GridViewAdapter mAdapter;
	// ------------------------------------------------------
	private int curYear = 0; // 当前--年
	private int curMonth = 0;// 当前--月
	private int curDay = 0;// 当前--日
	private String currentDate = ""; // 当前日期
	private ArrayList<TimeTable> timeTables = new ArrayList<TimeTable>();
	private AlertDialog mAlertDialog;

	/**
	 * 构造函数: 获取系统的当前时间
	 */
	public TimeTableActivity() {
		getSysData();
	}

	@SuppressLint("SimpleDateFormat")
	private void getSysData() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
		currentDate = sdf.format(date); // 当期日期
		String[] ss = currentDate.split("-");
		curYear = Integer.parseInt(ss[0]);
		curMonth = Integer.parseInt(ss[1]);
		curDay = Integer.parseInt(ss[2]);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		setContentView(R.layout.activity_clander);
		// 初始化组件
		initView();
		getTimeTable();
		// 获取活动
		getActivities();
		// 初始化适配器
		mAdapter = new GridViewAdapter(this, (dm.widthPixels - 7) / 7);
		// mAdapter.setClanderData(0, 0, curYear, curMonth, curDay);
		mAdapter.getCalendar(curYear, curMonth);
		if (TutorApplication.isCH()) {
			titleBar.setTitle(getString(R.string.yyyy_mm).replace("YYYY", mAdapter.getShowYear()).replace("MM", mAdapter.getShowMonth()));
		} else {
			titleBar.setTitle(mAdapter.getShowYear() + " - " + mAdapter.getShowMonth());
		}
		mGridView.setAdapter(mAdapter);
		mAdapter.setOnTimeTableClickListener(new OnTimeTableClickListener() {

			@Override
			public void onTimeTableClick(String week, ArrayList<TimeTable> timeTables) {
				Intent intent = new Intent(TimeTableActivity.this, TimeTableDetailActivity.class);
				intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_DAY_OF_WEEK, week);
				intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_TIME_TABLE, (Serializable) timeTables);
				startActivityForResult(intent, Constants.RequestResultCode.TIME_TABLE_REFRESH);
				// chooseTimeTableOrActivities();
			}
		});
	}

	private void chooseTimeTableOrActivities() {
		mAlertDialog = new AlertDialog.Builder(TimeTableActivity.this).create();
		mAlertDialog.show();
		Window mWindow = mAlertDialog.getWindow();
		mWindow.setContentView(R.layout.layout_time_table_sheet);
		mWindow.setGravity(Gravity.BOTTOM);
		// 点击选择本地图片
		mWindow.findViewById(R.id.btn_time_table).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//
			}
		});
		// 拍照片
		mWindow.findViewById(R.id.btn_activities).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//
			}
		});
		// 取消
		mWindow.findViewById(R.id.btn_cancel).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mAlertDialog.cancel();
			}
		});
	}

	private void getActivities() {
		RequestParams params = new RequestParams();
		params.put("month", curMonth);
		HttpHelper.get(this, ApiUrl.ACTIVITIES, TutorApplication.getHeaders(), params, new ObjectHttpResponseHandler<ActivityListResult>(ActivityListResult.class) {

			@Override
			public void onFailure(int status, String message) {
				// TODO
			}

			@Override
			public void onSuccess(ActivityListResult result) {
				ArrayList<ActivityModel> activities = result.getResult();
				if (activities != null && activities.size() > 0) {
					mAdapter.setActivityData(activities, curYear, curMonth);
				}
			}
		});
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
						timeTables.clear();
						timeTables.addAll(tempTimeTables);
						for (int i = 0; i < timeTables.size(); i++) {
							String courseName = timeTables.get(i).getCourseName();
							String userName = timeTables.get(i).getUserName();
							ArrayList<TimeTableDetail> timeTableDetails = timeTables.get(i).getTimeslots();
							if (timeTableDetails != null && timeTableDetails.size() > 0) {
								for (int j = 0; j < timeTableDetails.size(); j++) {
									timeTableDetails.get(j).setCourseName(courseName);
									timeTableDetails.get(j).setUserName(userName);
								}
							}
						}
						mAdapter.setTimeTableData(timeTables);
						mAdapter.getCalendar(curYear, curMonth);
					}
					if (TutorApplication.isCH()) {
						titleBar.setTitle(getString(R.string.yyyy_mm).replace("YYYY", mAdapter.getShowYear()).replace("MM", mAdapter.getShowMonth()));
					} else {
						titleBar.setTitle(mAdapter.getShowYear() + " - " + mAdapter.getShowMonth());
					}
				} else {
					toast(t.getMessage());
				}
			}
		});
	}

	public void initView() {
		titleBar = getView(R.id.title_bar);
		titleBar.initBack(this);
		mGridView = getView(R.id.clander_main_gridView);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (Constants.RequestResultCode.TIME_TABLE_REFRESH == resultCode) {
			getTimeTable();
		}
	}
}
