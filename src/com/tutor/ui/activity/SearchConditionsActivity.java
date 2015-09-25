package com.tutor.ui.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.mssky.mobile.ui.view.CustomListView;
import com.tutor.R;
import com.tutor.TutorApplication;
import com.tutor.adapter.CityAdapter;
import com.tutor.adapter.CountryAdapter;
import com.tutor.adapter.CourseAdapter;
import com.tutor.adapter.GradeAdapter;
import com.tutor.adapter.SchoolAdapter;
import com.tutor.adapter.TimeSlotAdapter;
import com.tutor.model.Area;
import com.tutor.model.Area1;
import com.tutor.model.AreaListResult;
import com.tutor.model.Course;
import com.tutor.model.CourseItem1;
import com.tutor.model.CourseItem2;
import com.tutor.model.CourseListResult;
import com.tutor.model.SearchCondition;
import com.tutor.model.Timeslot;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.dialog.TimeSlotDialog;
import com.tutor.ui.dialog.TimeSlotDialog.onTimeSelectedCallBack;
import com.tutor.ui.dialog.WeekDialog;
import com.tutor.ui.dialog.WeekDialog.OnWeekSelectedCallback;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.HttpHelper;
import com.tutor.util.ObjectHttpResponseHandler;

/**
 * 搜索条件界面
 * 
 * @author jerry.yao
 * 
 *         2015-9-23
 */
public class SearchConditionsActivity extends BaseActivity implements OnClickListener, onTimeSelectedCallBack, OnWeekSelectedCallback {

	// 学校
	private ArrayList<Course> schools = new ArrayList<Course>();
	// 年级列表
	private ArrayList<CourseItem1> grades = new ArrayList<CourseItem1>();
	// 课程列表
	private ArrayList<CourseItem2> courses = new ArrayList<CourseItem2>();
	private Spinner spSchool = null; // 小学 中学 大学
	private Spinner spGrade = null; // 年级
	private Spinner spCourse = null; // 课程
	private SchoolAdapter schoolAdapter = null;
	private GradeAdapter gradeAdapter = null;
	private CourseAdapter courseAdapter = null;
	// area
	private ArrayList<Area> citys = new ArrayList<Area>();
	private ArrayList<Area1> countrys = new ArrayList<Area1>();
	private Spinner spCity = null; // 市
	private Spinner spCountry = null; // 镇
	//
	private CityAdapter cityAdapter = null;
	private CountryAdapter countryAdapter = null;
	/** 时间段列表 */
	private CustomListView listView;
	private TimeSlotAdapter adapter;
	//
	private TextView weekTextView, startTimeTextView, endTimeTextView;
	/** 保存時間 */
	private Button saveTime;
	private Timeslot timeslot = null;
	private ArrayList<Timeslot> timeslots;
	// 是否编辑开始时间
	private boolean isStrat;
	// 课程id
	private int courseId;
	private String curSchool;
	private String curGrade;
	private String curCourse;
	// 区域id
	private int areaId;
	private String curCity;
	private String curCountry;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_search_conditions);
		initView();
		getCourse();
		getAreas();
	}

	@Override
	protected void initView() {
		TitleBar bar = getView(R.id.title_bar);
		bar.setTitle(R.string.label_search_conditions);
		bar.initBack(this);
		bar.setRightText(R.string.btn_save, new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (timeslot != null) {
					SearchCondition condition = new SearchCondition();
					condition.setAreaId(areaId);
					condition.setCourseId(courseId);
					condition.setTimeslot(timeslot);
					Intent intent = new Intent();
					intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_SEARCH_CONDITION, condition);
					setResult(Constants.RequestResultCode.SEARCH_CONDITIONS, intent);
					SearchConditionsActivity.this.finish();
				}
			}
		});
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
		adapter = new TimeSlotAdapter(this, timeslots, true);
		listView.setAdapter(adapter);
	}

	/**
	 * 获取选择课程列表
	 */
	private void getCourse() {
		if (!HttpHelper.isNetworkConnected(this)) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		showDialogRes(R.string.loading);
		HttpHelper.get(this, ApiUrl.COURSELIST, TutorApplication.getHeaders(), new RequestParams(), new ObjectHttpResponseHandler<CourseListResult>(CourseListResult.class) {

			@Override
			public void onFailure(int status, String message) {
				// 连接超时,再获取一次
				if (0 == status) {
					getCourse();
					return;
				}
				dismissDialog();
				toast(R.string.toast_server_error);
			}

			@Override
			public void onSuccess(CourseListResult t) {
				dismissDialog();
				if (null != t) {
					if (schools != null && schools.size() > 0) {
						schools.clear();
					}
					schools.addAll(t.getResult());
					setCourseSpinner();
				} else {
					toast(R.string.toast_server_error);
				}
			}
		});
	}

	/**
	 * 获取区域列表
	 */
	private void getAreas() {
		if (!HttpHelper.isNetworkConnected(this)) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		HttpHelper.get(this, ApiUrl.AREALIST, TutorApplication.getHeaders(), new RequestParams(), new ObjectHttpResponseHandler<AreaListResult>(AreaListResult.class) {

			@Override
			public void onFailure(int status, String message) {
				if (0 == status) {
					getAreas();
					return;
				}
				dismissDialog();
				toast(R.string.toast_server_error);
			}

			@Override
			public void onSuccess(AreaListResult t) {
				if (null != t) {
					citys.addAll(t.getResult());
					setAreaSpinner();
				} else {
					toast(R.string.toast_server_error);
				}
			}
		});
	}

	/**
	 * 地区下拉框
	 */
	private void setAreaSpinner() {
		spCity = getView(R.id.sp_city);
		spCountry = getView(R.id.sp_county);
		//
		cityAdapter = new CityAdapter(SearchConditionsActivity.this, citys);
		spCity.setAdapter(cityAdapter);
		spCity.setSelection(0, true);
		//
		if (citys != null && citys.size() > 0) {
			ArrayList<Area1> tempCcountrys = citys.get(0).getResult();
			countrys.addAll(tempCcountrys);
		}
		countryAdapter = new CountryAdapter(SearchConditionsActivity.this, countrys);
		spCountry.setAdapter(countryAdapter);
		spCountry.setSelection(0, true);
		curCity = citys.get(0).getName();
		curCountry = citys.get(0).getResult().get(0).getAddress();
		areaId = getAreaId();
		toast(curCity + curCountry + areaId);
		// TODO
		spCity.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				curCity = cityAdapter.getItem(position).getName();
				if (citys == null || citys.size() <= 0) {
					return;
				}
				for (int i = 0; i < citys.size(); i++) {
					String city = citys.get(i).getName();
					if (!city.equalsIgnoreCase(curCity)) {
						continue;
					}
					ArrayList<Area1> cityItem = citys.get(i).getResult();
					if (countrys != null && countrys.size() > 0) {
						countrys.clear();
					}
					countrys.addAll(cityItem);
					countryAdapter.notifyDataSetChanged();
					spCountry.setSelection(0, true);
					break;
				}
				curCountry = countryAdapter.getItem(0).getAddress();
				areaId = getAreaId();
				toast(curCity + curCountry + areaId);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}
		});
		// TODO
		spCountry.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				curCountry = countryAdapter.getItem(position).getAddress();
				areaId = getAreaId();
				toast(curCity + curCountry + areaId);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}
		});
	}

	/*
	 * 设置下拉框
	 */
	private void setCourseSpinner() {
		spSchool = getView(R.id.spin_school);
		spGrade = getView(R.id.spin_grade);
		spCourse = getView(R.id.spin_course);
		//
		schoolAdapter = new SchoolAdapter(SearchConditionsActivity.this, schools);
		spSchool.setAdapter(schoolAdapter);
		// 设置默认选中第0个值
		spSchool.setSelection(0, true);
		//
		if (schools != null && schools.size() > 0) {
			ArrayList<CourseItem1> courseItem = schools.get(0).getResult();
			grades.addAll(courseItem);
		}
		gradeAdapter = new GradeAdapter(SearchConditionsActivity.this, grades);
		spGrade.setAdapter(gradeAdapter);
		spGrade.setSelection(0, true);
		//
		if (grades != null && grades.size() > 0) {
			ArrayList<CourseItem2> courseItem = grades.get(0).getResult();
			courses.addAll(courseItem);
		}
		courseAdapter = new CourseAdapter(SearchConditionsActivity.this, courses);
		spCourse.setAdapter(courseAdapter);
		spCourse.setSelection(0, true);
		//
		Course school = schools.get(0);
		curSchool = school.getName();
		CourseItem1 grade = school.getResult().get(0);
		curGrade = grade.getName();
		curCourse = grade.getResult().get(0).getCourseName();
		courseId = getCourseId();
		// toast(curSchool + curGrade + curCourse + "id:" + courseId);
		// 学校下拉框监听
		spSchool.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				curSchool = schoolAdapter.getItem(position).getName();
				if (schools == null || schools.size() <= 0) {
					return;
				}
				for (int i = 0; i < schools.size(); i++) {
					String course = schools.get(i).getName();
					if (!course.equalsIgnoreCase(curSchool)) {
						continue;
					}
					ArrayList<CourseItem1> courseItem = schools.get(i).getResult();
					if (grades != null && grades.size() > 0) {
						grades.clear();
					}
					grades.addAll(courseItem);
					gradeAdapter.notifyDataSetChanged();
					spGrade.setSelection(0, true);
					break;
				}
				curGrade = gradeAdapter.getItem(0).getName();
				curCourse = gradeAdapter.getItem(0).getResult().get(0).getCourseName();
				if (grades != null && grades.size() > 0) {
					for (int i = 0; i < grades.size(); i++) {
						String grade = grades.get(i).getName();
						if (grade.equalsIgnoreCase(curGrade)) {
							ArrayList<CourseItem2> courseItem2 = grades.get(i).getResult();
							if (courses != null && courses.size() > 0) {
								courses.clear();
							}
							courses.addAll(courseItem2);
							courseAdapter.notifyDataSetChanged();
							spCourse.setSelection(0, true);
							break;
						}
					}
				}
				courseId = getCourseId();
				toast(curSchool + curGrade + curCourse + "id:" + courseId);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		// 年级下拉监听
		spGrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				curGrade = gradeAdapter.getItem(position).getName();
				if (grades != null && grades.size() > 0) {
					for (int i = 0; i < grades.size(); i++) {
						String grade = grades.get(i).getName();
						if (grade.equalsIgnoreCase(curGrade)) {
							ArrayList<CourseItem2> courseItem2 = grades.get(i).getResult();
							if (courses != null && courses.size() > 0) {
								courses.clear();
							}
							courses.addAll(courseItem2);
							courseAdapter.notifyDataSetChanged();
							spCourse.setSelection(0, true);
							break;
						}
					}
				}
				curCourse = courseAdapter.getItem(0).getCourseName();
				courseId = getCourseId();
				toast(curSchool + curGrade + curCourse + "id:" + courseId);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		// 课程下拉监听
		spCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				curCourse = courseAdapter.getItem(position).getCourseName();
				courseId = getCourseId();
				toast(curSchool + curGrade + curCourse + "id:" + courseId);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
	}

	/**
	 * 获取课程id
	 * 
	 * @return
	 */
	public int getCourseId() {
		if (schools == null || schools.size() <= 0) {
			return -1;
		}
		for (int i = 0; i < schools.size(); i++) {
			String school = schools.get(i).getName();
			if (!school.equalsIgnoreCase(curSchool)) {
				continue;
			}
			ArrayList<CourseItem1> grades = schools.get(i).getResult();
			if (grades == null || grades.size() <= 0) {
				return -1;
			}
			for (int j = 0; j < grades.size(); j++) {
				String grade = grades.get(j).getName();
				if (!grade.equalsIgnoreCase(curGrade)) {
					continue;
				}
				ArrayList<CourseItem2> courses = grades.get(j).getResult();
				if (courses == null || courses.size() <= 0) {
					return -1;
				}
				for (int k = 0; k < courses.size(); k++) {
					String course = courses.get(k).getCourseName();
					if (course.equalsIgnoreCase(curCourse)) {
						courseId = courses.get(k).getId();
						break;
					}
				}
			}
		}
		return courseId;
	}

	/**
	 * 获取区域id
	 * 
	 * @return
	 */
	public int getAreaId() {
		if (citys == null || citys.size() <= 0) {
			return -1;
		}
		for (int i = 0; i < citys.size(); i++) {
			String city = citys.get(i).getName();
			if (!city.equalsIgnoreCase(curCity)) {
				continue;
			}
			ArrayList<Area1> countrys = citys.get(i).getResult();
			if (countrys == null || countrys.size() <= 0) {
				return -1;
			}
			for (int j = 0; j < countrys.size(); j++) {
				String country = countrys.get(j).getAddress();
				if (country.equalsIgnoreCase(curCountry)) {
					areaId = countrys.get(j).getId();
				}
			}
		}
		return areaId;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.ac_fill_personal_info_btn_save_timeslot:
				if (null != timeslot) {
					if (null == timeslots) {
						timeslots = new ArrayList<Timeslot>();
					}
					// if (null != teacherProfile) {
					// timeslot.setMemberId(teacherProfile.getId());
					// } else {
					// timeslot.setMemberId(studentProfile.getId());
					// }
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
			default:
				break;
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
	public int onTimeSelected(String time, int hour, int minute) {
		if (null == timeslot) {
			timeslot = new Timeslot();
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
}
