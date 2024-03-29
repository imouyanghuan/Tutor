package com.tutor.ui.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.hk.tutor.R;
import com.loopj.android.http.RequestParams;
import com.mssky.mobile.helper.SharePrefUtil;
import com.mssky.mobile.ui.view.CustomListView;
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
import com.tutor.model.SubjectModel;
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
	// 课程value
	// private int courseValue = -1;
	// private String curSchool = "";
	// private String curGrade = "";
	// private String curCourse = "";
	// 区域value
	private int areaValue = -1;
	private String curCity = "";
	private String curCountry = "";
	// gender
	private Spinner spGender;
	private ArrayList<String> genders;
	private ArrayAdapter<String> genderAdapter;
	private int genderValue = -1;
	// private EditText etSearch;
	private boolean isFromTeacher;
	private LinearLayout llGender;
	private FrameLayout flTipSearch;
	private LinearLayout llIsVoluntary;
	private CheckBox cbIsVoluntary;
	private TextView courseTextView;
	private int[] courseValues;
	private LinearLayout llIsCertified;
	private CheckBox cbIsCertified;

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
		Intent intent = getIntent();
		if (intent != null) {
			isFromTeacher = intent.getBooleanExtra(Constants.General.IS_FROM_TEACHER, false);
		}
		TitleBar bar = getView(R.id.title_bar);
		bar.setTitle(R.string.label_search_conditions);
		bar.initBack(this);
		bar.setRightText(R.string.label_search, new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 保存
				SearchCondition condition = new SearchCondition();
				condition.setVoluntaryTutoring(cbIsVoluntary.isChecked());
				condition.setCertified(cbIsCertified.isChecked());
				condition.setAreaValue(areaValue);
				String areaName = "";
				if (!curCity.equals(getString(R.string.label_please_choose))) {
					areaName = curCity + curCountry;
				} else {
					areaName = "";
				}
				condition.setAreaName(areaName);
				condition.setCourseValues(courseValues);
				String courseName = "";
				if (!courseTextView.getText().toString().equals(getString(R.string.label_please_choose_course))) {
					courseName = courseTextView.getText().toString();
				} else {
					courseName = "";
				}
				condition.setCourseName(courseName);
				if (timeslots != null && timeslots.size() > 0) {
					condition.setTimeslot(timeslots.get(0));
				}
				condition.setGender(genderValue);
				String genderName = "";
				if (genderValue == -1) {
					genderName = "";
				} else if (genderValue == Constants.General.MALE) {
					genderName = getString(R.string.label_man);
				} else {
					genderName = getString(R.string.label_woman);
				}
				condition.setGenderName(genderName);
				Intent intent = new Intent();
				intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_SEARCH_CONDITION, condition);
				setResult(Constants.RequestResultCode.SEARCH_CONDITIONS, intent);
				SearchConditionsActivity.this.finish();
			}
		});
		// 选择课程
		courseTextView = getView(R.id.et_course);
		courseTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(SearchConditionsActivity.this, SureCourseActivity.class);
				intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_ROLE, TutorApplication.getRole());
				intent.putExtra(Constants.General.IS_FROM_TO_BY_MY_STUDENT_ACTIVITY, false);
				startActivityForResult(intent, Constants.RequestResultCode.SURE_COURSES);
			}
		});
		// 是否义务补习
		llIsVoluntary = getView(R.id.ll_isVoluntary);
		cbIsVoluntary = getView(R.id.cb_is_voluntary);
		// 是否已经通过认证
		llIsCertified = getView(R.id.ll_is_certified);
		cbIsCertified = getView(R.id.cb_is_certified);
		TextView tvNote = getView(R.id.tv_note);
		if (TutorApplication.getRole() != Constants.General.ROLE_TUTOR) {
			tvNote.setVisibility(View.GONE);
			llIsVoluntary.setVisibility(View.VISIBLE);
			llIsCertified.setVisibility(View.VISIBLE);
		}
		llGender = getView(R.id.ll_gender);
		// tip
		flTipSearch = getView(R.id.fl_tip_search);
		flTipSearch.setOnClickListener(this);
		TextView tvTimeSlot = getView(R.id.tv_timeslot);
		boolean isChinese = TutorApplication.isCH();
		if (isChinese) {
			tvTimeSlot.setBackgroundResource(R.drawable.timeslot_zh);
		} else {
			tvTimeSlot.setBackgroundResource(R.drawable.timeslot);
		}
		// 当切换为这个tab的时候才显示tip
		boolean isNeedShow = SharePrefUtil.getBoolean(getApplicationContext(), Constants.General.IS_NEED_SHOW_SEARCH_TIP, true);
		if (isNeedShow) {
			flTipSearch.setVisibility(View.VISIBLE);
		} else {
			flTipSearch.setVisibility(View.GONE);
		}
		// etSearch = getView(R.id.fragment_find_student_et);
		// getView(R.id.fragment_find_student_btn).setVisibility(View.GONE);
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
		// 性別
		spGender = getView(R.id.sp_gender);
		genders = new ArrayList<String>();
		genders.add(getString(R.string.label_please_choose));
		genders.add(getString(R.string.label_man));
		genders.add(getString(R.string.label_woman));
		spGender.setSelection(0);
		genderAdapter = new ArrayAdapter<String>(SearchConditionsActivity.this, R.layout.layout_gender_item, genders);
		spGender.setAdapter(genderAdapter);
		spGender.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String currentGender = genderAdapter.getItem(position);
				if (currentGender.equals(getString(R.string.label_please_choose))) {
					genderValue = -1;
				} else if (currentGender.equals(getString(R.string.label_man))) {
					genderValue = Constants.General.MALE;
				} else {
					genderValue = Constants.General.FEMALE;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}
		});
		// 如果是老师则隐藏性别布局
		if (isFromTeacher) {
			llGender.setVisibility(View.GONE);
		} else {
			llGender.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if (arg0 == Constants.RequestResultCode.SURE_COURSES && arg1 == RESULT_OK && null != arg2) {
			if (TutorApplication.getRole() == Constants.General.ROLE_TUTOR) {
				// 老师搜索
				setTutorData(arg2);
			} else {
				setData(arg2);
			}
		}
	}

	private void setTutorData(Intent arg2) {
		@SuppressWarnings("unchecked")
		ArrayList<SubjectModel> data = (ArrayList<SubjectModel>) arg2.getSerializableExtra(Constants.IntentExtra.INTENT_EXTRA_COURSESLIST);
		if (data == null || data.size() == 0) {
			courseValues = null;
			courseTextView.setText("");
		} else {
			// courseValues = new int[data.size()];
			courseTextView.setText(getTutorShowCourse(data));
		}
	}

	private String getTutorShowCourse(ArrayList<SubjectModel> subjectModels) {
		StringBuffer sb = new StringBuffer();
		ArrayList<Integer> selectedCourseValuses = new ArrayList<Integer>();
		for (int i = 0; i < subjectModels.size(); i++) {
			ArrayList<Integer> courseValuses = subjectModels.get(i).getCourseValues();
			for (int j = 0; j < courseValuses.size(); j++) {
				selectedCourseValuses.add(courseValuses.get(j));
			}
			sb.append(subjectModels.get(i).getName() + ", ");
		}
		int size = selectedCourseValuses.size();
		courseValues = new int[size];
		for (int i = 0; i < size; i++) {
			courseValues[i] = selectedCourseValuses.get(i);
		}
		// 去掉最后一个逗号
		if (sb.length() > 2) {
			sb.delete(sb.length() - 2, sb.length());
		}
		return sb.toString();
	}

	private void setData(Intent arg2) {
		@SuppressWarnings("unchecked")
		ArrayList<CourseItem2> data = (ArrayList<CourseItem2>) arg2.getSerializableExtra(Constants.IntentExtra.INTENT_EXTRA_COURSESLIST);
		if (data == null || data.size() == 0) {
			courseValues = null;
			courseTextView.setText("");
		} else {
			courseValues = new int[data.size()];
			courseTextView.setText(getShowCourse(data));
		}
	}

	private String getShowCourse(ArrayList<CourseItem2> courses) {
		StringBuffer sb = new StringBuffer();
		if (null != courses && courses.size() > 0) {
			for (int i = 0; i < courses.size(); i++) {
				CourseItem2 item2 = courses.get(i);
				courseValues[i] = item2.getValue();
				sb.append(item2.getType() + "-" + item2.getSubType() + "-" + item2.getCourseName() + ",");
			}
		}
		// 去掉最后一个逗号
		if (sb.length() > 0) {
			sb.delete(sb.length() - 1, sb.length());
		}
		return sb.toString();
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
		HttpHelper.getHelper().get(ApiUrl.COURSELIST, TutorApplication.getHeaders(), new RequestParams(), new ObjectHttpResponseHandler<CourseListResult>(CourseListResult.class) {

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
					Course course = new Course();
					course.setName(getString(R.string.label_please_choose));
					schools.add(course);
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
		HttpHelper.getHelper().get(ApiUrl.AREALIST, TutorApplication.getHeaders(), new RequestParams(), new ObjectHttpResponseHandler<AreaListResult>(AreaListResult.class) {

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
					Area area = new Area();
					area.setName(getString(R.string.label_please_choose));
					citys.add(area);
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
		spCountry.setVisibility(View.GONE);
		//
		cityAdapter = new CityAdapter(SearchConditionsActivity.this, citys);
		spCity.setAdapter(cityAdapter);
		spCity.setSelection(0, true);
		//
		// if (citys != null && citys.size() > 0) {
		// ArrayList<Area1> tempCcountrys = citys.get(0).getResult();
		// countrys.addAll(tempCcountrys);
		// }
		countryAdapter = new CountryAdapter(SearchConditionsActivity.this, countrys);
		spCountry.setAdapter(countryAdapter);
		spCountry.setSelection(0, true);
		// curCity = citys.get(0).getName();
		// curCountry = citys.get(0).getResult().get(0).getAddress();
		// areaValue = getAreaValue();
		// toast(curCity + curCountry + areaId);
		// TODO
		spCity.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				curCity = cityAdapter.getItem(position).getName();
				if (curCity.equals(getString(R.string.label_please_choose))) {
					clearCountry();
					spCountry.setVisibility(View.GONE);
					return;
				} else {
					spCountry.setVisibility(View.VISIBLE);
				}
				if (citys == null || citys.size() <= 0) {
					return;
				}
				for (int i = 0; i < citys.size(); i++) {
					String city = citys.get(i).getName();
					if (!city.equalsIgnoreCase(curCity)) {
						continue;
					}
					ArrayList<Area1> cityItem = citys.get(i).getResult();
					clearCountry();
					countrys.addAll(cityItem);
					countryAdapter.notifyDataSetChanged();
					spCountry.setSelection(0, true);
					break;
				}
				curCountry = countryAdapter.getItem(0).getAddress();
				areaValue = getAreaValue();
				// toast(curCity + curCountry + areaValue);
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
				areaValue = getAreaValue();
				// toast(curCity + curCountry + areaValue);
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
		spGrade.setVisibility(View.GONE);
		spCourse.setVisibility(View.GONE);
		//
		schoolAdapter = new SchoolAdapter(SearchConditionsActivity.this, schools);
		spSchool.setAdapter(schoolAdapter);
		// 设置默认选中第0个值
		spSchool.setSelection(0, true);
		gradeAdapter = new GradeAdapter(SearchConditionsActivity.this, grades);
		spGrade.setAdapter(gradeAdapter);
		spGrade.setSelection(0, true);
		courseAdapter = new CourseAdapter(SearchConditionsActivity.this, courses);
		spCourse.setAdapter(courseAdapter);
		spCourse.setSelection(0, true);
		// 学校下拉框监听
		// spSchool.setOnItemSelectedListener(new
		// AdapterView.OnItemSelectedListener() {
		//
		// @Override
		// public void onItemSelected(AdapterView<?> arg0, View arg1, int
		// position, long arg3) {
		// curSchool = schoolAdapter.getItem(position).getName();
		// if (curSchool.equals(getString(R.string.label_please_choose))) {
		// clearGrade();
		// clearCourse();
		// spGrade.setVisibility(View.GONE);
		// spCourse.setVisibility(View.GONE);
		// return;
		// } else {
		// spGrade.setVisibility(View.VISIBLE);
		// spCourse.setVisibility(View.VISIBLE);
		// }
		// if (schools == null || schools.size() <= 0) {
		// return;
		// }
		// for (int i = 0; i < schools.size(); i++) {
		// String course = schools.get(i).getName();
		// if (!course.equalsIgnoreCase(curSchool) ||
		// curSchool.equals(getString(R.string.label_please_choose))) {
		// continue;
		// }
		// ArrayList<CourseItem1> courseItem = schools.get(i).getResult();
		// clearGrade();
		// grades.addAll(courseItem);
		// gradeAdapter.notifyDataSetChanged();
		// spGrade.setSelection(0, true);
		// break;
		// }
		// curGrade = gradeAdapter.getItem(0).getName();
		// curCourse =
		// gradeAdapter.getItem(0).getResult().get(0).getCourseName();
		// if (grades != null && grades.size() > 0) {
		// for (int i = 0; i < grades.size(); i++) {
		// String grade = grades.get(i).getName();
		// if (grade.equalsIgnoreCase(curGrade)) {
		// ArrayList<CourseItem2> courseItem2 = grades.get(i).getResult();
		// clearCourse();
		// courses.addAll(courseItem2);
		// courseAdapter.notifyDataSetChanged();
		// spCourse.setSelection(0, true);
		// break;
		// }
		// }
		// }
		// courseValue = getCourseValue();
		// }
		//
		// @Override
		// public void onNothingSelected(AdapterView<?> arg0) {}
		// });
		// 年级下拉监听
		// spGrade.setOnItemSelectedListener(new
		// AdapterView.OnItemSelectedListener() {
		//
		// @Override
		// public void onItemSelected(AdapterView<?> arg0, View arg1, int
		// position, long arg3) {
		// curGrade = gradeAdapter.getItem(position).getName();
		// if (grades != null && grades.size() > 0) {
		// for (int i = 0; i < grades.size(); i++) {
		// String grade = grades.get(i).getName();
		// if (grade.equalsIgnoreCase(curGrade)) {
		// ArrayList<CourseItem2> courseItem2 = grades.get(i).getResult();
		// if (courses != null && courses.size() > 0) {
		// courses.clear();
		// }
		// courses.addAll(courseItem2);
		// courseAdapter.notifyDataSetChanged();
		// spCourse.setSelection(0, true);
		// break;
		// }
		// }
		// }
		// curCourse = courseAdapter.getItem(0).getCourseName();
		// courseValue = getCourseValue();
		// // toast(curSchool + curGrade + curCourse + "id:" +
		// // courseValue);
		// }
		//
		// @Override
		// public void onNothingSelected(AdapterView<?> arg0) {}
		// });
		// 课程下拉监听
		// spCourse.setOnItemSelectedListener(new
		// AdapterView.OnItemSelectedListener() {
		//
		// @Override
		// public void onItemSelected(AdapterView<?> arg0, View arg1, int
		// position, long arg3) {
		// curCourse = courseAdapter.getItem(position).getCourseName();
		// courseValue = getCourseValue();
		// // toast(curSchool + curGrade + curCourse + "id:" +
		// // courseValue);
		// }
		//
		// @Override
		// public void onNothingSelected(AdapterView<?> arg0) {}
		// });
	}

	/**
	 * 获取课程value
	 * 
	 * @return
	 */
	// public int getCourseValue() {
	// if (schools == null || schools.size() <= 0) {
	// return -1;
	// }
	// for (int i = 0; i < schools.size(); i++) {
	// String school = schools.get(i).getName();
	// if (!school.equalsIgnoreCase(curSchool)) {
	// continue;
	// }
	// ArrayList<CourseItem1> grades = schools.get(i).getResult();
	// if (grades == null || grades.size() <= 0) {
	// return -1;
	// }
	// for (int j = 0; j < grades.size(); j++) {
	// String grade = grades.get(j).getName();
	// if (!grade.equalsIgnoreCase(curGrade)) {
	// continue;
	// }
	// ArrayList<CourseItem2> courses = grades.get(j).getResult();
	// if (courses == null || courses.size() <= 0) {
	// return -1;
	// }
	// for (int k = 0; k < courses.size(); k++) {
	// String course = courses.get(k).getCourseName();
	// if (course.equalsIgnoreCase(curCourse)) {
	// courseValue = courses.get(k).getValue();
	// break;
	// }
	// }
	// }
	// }
	// return courseValue;
	// }
	/**
	 * 获取区域value
	 * 
	 * @return
	 */
	public int getAreaValue() {
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
					areaValue = countrys.get(j).getValue();
				}
			}
		}
		return areaValue;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.ac_fill_personal_info_btn_save_timeslot:
				if (null != timeslot) {
					if (null == timeslots) {
						timeslots = new ArrayList<Timeslot>();
					}
					timeslots.clear();
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
			case R.id.fl_tip_search:
				flTipSearch.setVisibility(View.GONE);
				SharePrefUtil.saveBoolean(getApplicationContext(), Constants.General.IS_NEED_SHOW_SEARCH_TIP, false);
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

	// private void clearCourse() {
	// if (courses != null && courses.size() > 0) {
	// courses.clear();
	// }
	// }
	//
	// private void clearGrade() {
	// if (grades != null && grades.size() > 0) {
	// grades.clear();
	// }
	// }
	private void clearCountry() {
		if (countrys != null && countrys.size() > 0) {
			countrys.clear();
		}
	}
}
