package com.tutor.ui.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ListView;

import com.hk.tutor.R;
import com.tutor.adapter.TimeTableAdapter;
import com.tutor.adapter.TimeTableAdapter.OnEditBtnClickListener;
import com.tutor.model.EditTimeslot;
import com.tutor.model.TimeTable;
import com.tutor.model.TimeTableDetail;
import com.tutor.params.Constants;
import com.tutor.ui.view.TitleBar;

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
		ArrayList<TimeTable> tables = (ArrayList<TimeTable>) intent.getSerializableExtra(Constants.IntentExtra.INTENT_EXTRA_TIME_TABLE);
		if (tables != null && tables.size() > 0) {
			timeTableDetails = new ArrayList<TimeTableDetail>();
			for (int i = 0; i < tables.size(); i++) {
				ArrayList<EditTimeslot> timeslots = tables.get(i).getTimeslots();
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
		TimeTableAdapter timelineAdapter = new TimeTableAdapter(this, timeTableDetails);
		listView.setAdapter(timelineAdapter);
		// 编辑时间回调
		timelineAdapter.setOnEditBtnClickListener(new OnEditBtnClickListener() {

			@Override
			public void onEditBtnClick(TimeTableDetail detail) {
				int currentId = detail.getId();
				ArrayList<TimeTableDetail> sameTimeTables = new ArrayList<TimeTableDetail>();
				for (int i = 0; i < timeTableDetails.size(); i++) {
					if (timeTableDetails.get(i).getId() == currentId) {
						sameTimeTables.add(timeTableDetails.get(i));
					}
				}
				Intent intent = new Intent(TimeTableDetailActivity.this, EditTimeTableActivity.class);
				intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_TIME_TABLE_DETAIL, sameTimeTables);
				startActivityForResult(intent, Constants.RequestResultCode.TIME_TABLE_DETAIL);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Constants.RequestResultCode.TIME_TABLE_DETAIL) {
			// refresh UI
			setResult(Constants.RequestResultCode.TIME_TABLE_REFRESH);
			finish();
		}
	}
}
