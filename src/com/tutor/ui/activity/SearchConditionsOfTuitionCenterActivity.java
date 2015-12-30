package com.tutor.ui.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.hk.tutor.R;
import com.loopj.android.http.RequestParams;
import com.tutor.TutorApplication;
import com.tutor.adapter.SearchServiceAreaAdapter;
import com.tutor.adapter.SearchServiceAreaAdapter.OnCheckedChangeListener;
import com.tutor.adapter.SearchServiceGradeAdapter;
import com.tutor.adapter.SearchServiceGradeAdapter.OnCheckedListener;
import com.tutor.model.CourseItem2;
import com.tutor.model.FirstLevelArea;
import com.tutor.model.FirstLevelAreaListResult;
import com.tutor.model.SearchCondition;
import com.tutor.model.ServiceGrade;
import com.tutor.model.ServiceGradeListResult;
import com.tutor.model.SubjectModel;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.view.CustomGridView;
import com.tutor.ui.view.CustomListView;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.HttpHelper;
import com.tutor.util.LogUtils;
import com.tutor.util.ObjectHttpResponseHandler;

/**
 * 补习社搜索条件界面
 * 
 * @author jerry.yao
 * 
 *         2015-12-11
 */
public class SearchConditionsOfTuitionCenterActivity extends BaseActivity implements OnClickListener {

	// 区域value
	private ArrayList<Integer> areaValues = new ArrayList<Integer>();
	private StringBuffer areaNames = new StringBuffer();
	private TextView courseTextView;
	private int[] courseValues;
	private ArrayList<ServiceGrade> serviceGrades = new ArrayList<ServiceGrade>();
	private ArrayList<FirstLevelArea> serviceAreas = new ArrayList<FirstLevelArea>();
	private CustomListView lvGrades;
	private CustomGridView gvAreas;
	private int curServiceGradeValue = -1;
	private String curServiceGradeName = "";
	private SearchServiceGradeAdapter serviceGradeAdapter;
	private SearchServiceAreaAdapter serviceAreaAdapter;
	private int curServiceAreaValue = -1;
	private String curServiceAreaName = "";

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_search_conditions_of_tuition_center);
		initView();
		getServiceGrades();
		getServiceAreas();
	}

	@Override
	protected void initView() {
		TitleBar bar = getView(R.id.title_bar);
		bar.setTitle(R.string.label_search_conditions);
		bar.initBack(this);
		bar.setRightText(R.string.btn_save, new OnClickListener() {

			@Override
			public void onClick(View v) {
				SearchCondition condition = new SearchCondition();
				// 地区名称和值
				getAreaNameAndValue();
				condition.setAreaValues(areaValues);
				condition.setAreaName(areaNames.toString());
				// 服务年级和值
				condition.setGradeName(curServiceGradeName);
				condition.setServiceGradeValue(curServiceGradeValue);
				Intent intent = new Intent();
				intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_SEARCH_CONDITION, condition);
				setResult(Constants.RequestResultCode.SEARCH_CONDITIONS, intent);
				SearchConditionsOfTuitionCenterActivity.this.finish();
			}
		});
		// 服务年级listview
		lvGrades = getView(R.id.lv_service_grades);
		// 18大区listview
		gvAreas = getView(R.id.gv_area);
	}

	private void getAreaNameAndValue() {
		if (!serviceAreas.isEmpty()) {
			for (int i = 0; i < serviceAreas.size(); i++) {
				FirstLevelArea area = serviceAreas.get(i);
				if (area.isChecked()) {
					areaNames.append(area.getName()).append(", ");
					areaValues.add(area.getValue());
				}
			}
			int areaNameLength = areaNames.length();
			if (areaNameLength >= 2) {
				areaNames.delete(areaNameLength - 2, areaNameLength);
			}
		}
	}

	/**
	 * 获取18大区列表
	 */
	private void getServiceAreas() {
		if (!HttpHelper.isNetworkConnected(this)) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		HttpHelper.getHelper().get(ApiUrl.FIRST_LEVEL_AREAS, TutorApplication.getHeaders(), new RequestParams(),
				new ObjectHttpResponseHandler<FirstLevelAreaListResult>(FirstLevelAreaListResult.class) {

					@Override
					public void onFailure(int status, String message) {
						if (0 == status) {
							getServiceAreas();
							return;
						}
						dismissDialog();
						toast(R.string.toast_server_error);
					}

					@Override
					public void onSuccess(FirstLevelAreaListResult result) {
						dismissDialog();
						if (null != result) {
							serviceAreas = result.getResult();
							if (!serviceAreas.isEmpty()) {
								for (int i = 0; i < serviceAreas.size(); i++) {
									FirstLevelArea area = serviceAreas.get(i);
									if (area != null) {
										area.setChecked(false);
									}
								}
							}
							serviceAreaAdapter = new SearchServiceAreaAdapter(SearchConditionsOfTuitionCenterActivity.this, serviceAreas);
							serviceAreaAdapter.setOnCheckedChangeListener(new OnCheckedChangeListener() {

								@Override
								public void checkedChanged(FirstLevelArea area) {
									for (int i = 0; i < serviceAreas.size(); i++) {
										FirstLevelArea originArea = serviceAreas.get(i);
										if (area.getValue() == originArea.getValue()) {
											if (area.isChecked()) {
												originArea.setChecked(true);
												curServiceAreaValue = area.getValue();
												curServiceAreaName = area.getName();
											} else {
												originArea.setChecked(false);
												curServiceAreaValue = -1;
												curServiceAreaName = "";
											}
										} else {
											originArea.setChecked(false);
										}
									}
									serviceAreaAdapter.notifyDataSetChanged();
								}
							});
							gvAreas.setAdapter(serviceAreaAdapter);
						} else {
							toast(R.string.toast_server_error);
						}
					}
				});
	}

	private void getServiceGrades() {
		if (!HttpHelper.isNetworkConnected(this)) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		HttpHelper.getHelper().get(ApiUrl.SERVICE_GRADES, TutorApplication.getHeaders(), new RequestParams(), new ObjectHttpResponseHandler<ServiceGradeListResult>(ServiceGradeListResult.class) {

			@Override
			public void onFailure(int status, String message) {
				// 连接超时,再获取一次
				if (0 == status) {
					getServiceGrades();
					return;
				}
				toast(R.string.toast_server_error);
			}

			@Override
			public void onSuccess(ServiceGradeListResult result) {
				if (null != result) {
					serviceGrades = result.getResult();
					serviceGradeAdapter = new SearchServiceGradeAdapter(SearchConditionsOfTuitionCenterActivity.this, serviceGrades);
					serviceGradeAdapter.setOnCheckedListener(new OnCheckedListener() {

						@Override
						public void checkedChanged(ServiceGrade serviceGrade) {
							for (int i = 0; i < serviceGrades.size(); i++) {
								ServiceGrade originGrade = serviceGrades.get(i);
								if (serviceGrade.getValue() == originGrade.getValue()) {
									if (serviceGrade.isChecked()) {
										originGrade.setChecked(true);
										curServiceGradeValue = serviceGrade.getValue();
										curServiceGradeName = serviceGrade.getText();
									} else {
										originGrade.setChecked(false);
										curServiceGradeValue = -1;
										curServiceGradeName = "";
									}
								} else {
									originGrade.setChecked(false);
								}
							}
							serviceGradeAdapter.notifyDataSetChanged();
						}
					});
					lvGrades.setAdapter(serviceGradeAdapter);
				} else {
					toast(R.string.toast_server_error);
				}
			}
		});
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

	@Override
	public void onClick(View v) {}
}
