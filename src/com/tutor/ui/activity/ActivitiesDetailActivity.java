package com.tutor.ui.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ListView;

import com.hk.tutor.R;
import com.tutor.adapter.ActivitiesAdapter;
import com.tutor.model.ActivityModel;
import com.tutor.params.Constants;
import com.tutor.ui.view.TitleBar;

/**
 * 活动詳情界面
 * 
 * @author jerry.yao
 * 
 *         2015-11-16
 */
@SuppressLint("SimpleDateFormat")
public class ActivitiesDetailActivity extends BaseActivity {

	private ActivitiesAdapter activitiesAdapter;
	private SimpleDateFormat sdfAcitivity = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private ArrayList<ActivityModel> sameDayActivities;
	private String year;
	private String month;
	private int today;

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
		today = intent.getIntExtra(Constants.General.TODAY, -1);
		month = intent.getStringExtra(Constants.General.MONTH);
		year = intent.getStringExtra(Constants.General.YEAR);
		ArrayList<ActivityModel> activities = (ArrayList<ActivityModel>) intent.getSerializableExtra(Constants.General.ACTIVITIES);
		if (activities != null && activities.size() > 0) {
			sameDayActivities = new ArrayList<ActivityModel>();
			for (int i = 0; i < activities.size(); i++) {
				String heldDate = activities.get(i).getHeldDate();
				Date date = null;
				try {
					date = sdfAcitivity.parse(heldDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				@SuppressWarnings("deprecation")
				int day = date.getDate();
				if (day == today) {
					sameDayActivities.add(activities.get(i));
				}
			}
		}
		initView();
	}

	@Override
	protected void initView() {
		TitleBar bar = getView(R.id.title_bar);
		if (TextUtils.isEmpty(year) || TextUtils.isEmpty(month) || today == -1) {
			bar.setTitle(R.string.label_activity_detail);
		} else {
			bar.setTitle(year + "-" + month + "-" + today);
		}
		bar.initBack(this);
		ListView listView = (ListView) this.findViewById(R.id.listview);
		listView.setDividerHeight(0);
		Collections.sort(sameDayActivities);
		activitiesAdapter = new ActivitiesAdapter(this, sameDayActivities);
		listView.setAdapter(activitiesAdapter);
	}
}
