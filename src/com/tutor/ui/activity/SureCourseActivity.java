package com.tutor.ui.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ScrollView;

import com.hk.tutor.R;
import com.loopj.android.http.RequestParams;
import com.tutor.TutorApplication;
import com.tutor.model.Course;
import com.tutor.model.CourseItem1;
import com.tutor.model.CourseItem2;
import com.tutor.model.CourseListResult;
import com.tutor.model.SubjectCourseListResult;
import com.tutor.model.SubjectCourseModel;
import com.tutor.model.SubjectModel;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.view.CourseItemLayout.OnRefreshListener;
import com.tutor.ui.view.CourseLayout;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.HttpHelper;
import com.tutor.util.ObjectHttpResponseHandler;

/**
 * 选择课程
 * 
 * @author bruce.chen
 * 
 *         2015-9-18
 */
public class SureCourseActivity extends BaseActivity implements OnRefreshListener {

	private ScrollView scrollView;
	private int role = -1;
	// 课程列表
	private ArrayList<Course> studentCourses;
	private boolean isFromToBeMyStudent;
	// 老师课程列表
	private ArrayList<SubjectCourseModel> tutorCourses;
	private CourseLayout courseLayout;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_select_course);
		Intent intent = getIntent();
		if (intent == null) {
			return;
		}
		role = intent.getIntExtra(Constants.IntentExtra.INTENT_EXTRA_ROLE, -1);
		if (role == -1) {
			throw new IllegalArgumentException("role is -1");
		}
		isFromToBeMyStudent = intent.getBooleanExtra(Constants.General.IS_FROM_TO_BY_MY_STUDENT_ACTIVITY, false);
		initView();
		// 获取数据
		if (TutorApplication.getRole() == Constants.General.ROLE_STUDENT || isFromToBeMyStudent) {
			getStudentCourse();
		} else {
			getTutorCourse();
		}
	}

	private void getTutorCourse() {
		if (!HttpHelper.isNetworkConnected(this)) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		showDialogRes(R.string.loading);
		HttpHelper.getHelper().get(ApiUrl.COURSE_LIST_FOR_TUTOR, TutorApplication.getHeaders(), new RequestParams(), new ObjectHttpResponseHandler<SubjectCourseListResult>(SubjectCourseListResult.class) {

			@Override
			public void onFailure(int status, String message) {
				// 连接超时,再获取一次
				if (0 == status) {
					getTutorCourse();
					return;
				}
				dismissDialog();
				toast(R.string.toast_server_error);
			}

			@Override
			public void onSuccess(SubjectCourseListResult t) {
				dismissDialog();
				if (null != t) {
					tutorCourses = t.getResult();
					setData();
				} else {
					toast(R.string.toast_server_error);
				}
			}
		});
	}

	@Override
	protected void initView() {
		TitleBar bar = getView(R.id.title_bar);
		bar.initBack(this);
		if (Constants.General.ROLE_STUDENT == role) {
			bar.setTitle(R.string.label_teaching_couress);
		} else {
			bar.setTitle(R.string.label_select_couress);
		}
		bar.setRightText(R.string.btn_save, new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				if (TutorApplication.getRole() == Constants.General.ROLE_STUDENT || isFromToBeMyStudent) {
					// 学生课程列表
					ArrayList<CourseItem2> choiceCourses = getStudentChoiceCourses();
					if(choiceCourses == null || choiceCourses.size() <= 0){
						toast(R.string.label_please_choose_a_course);
						return;
					}
					intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_COURSESLIST, choiceCourses);
				} else {
					// 老师课程列表
					ArrayList<SubjectModel> choiceCourses = getTutorChoiceCourses();
					if(choiceCourses == null || choiceCourses.size() <= 0){
						toast(R.string.label_please_choose_a_course);
						return;
					}
					intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_COURSESLIST, choiceCourses);
			
				}
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		
		scrollView = getView(R.id.ac_fill_personal_course_scrollview);
	}

	/**
	 * 获取选择课程列表
	 */
	private void getStudentCourse() {
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
					getStudentCourse();
					return;
				}
				dismissDialog();
				toast(R.string.toast_server_error);
			}

			@Override
			public void onSuccess(CourseListResult t) {
				dismissDialog();
				if (null != t) {
					studentCourses = t.getResult();
					setData();
				} else {
					toast(R.string.toast_server_error);
				}
			}
		});
	}

	/**
	 * 为老师和学生设置不同的数据
	 */
	private void setData() {
		if (TutorApplication.getRole() == Constants.General.ROLE_STUDENT || isFromToBeMyStudent) {
			if (null != studentCourses && 0 != studentCourses.size()) {
				for (Course course : studentCourses) {
					ArrayList<CourseItem1> courseItem1s = course.getResult();
					if (courseItem1s != null && courseItem1s.size() > 0) {
						for (CourseItem1 courseItem1 : courseItem1s) {
							ArrayList<CourseItem2> courseItem2s = courseItem1.getResult();
							if (courseItem2s != null && courseItem2s.size() > 0) {
								for (CourseItem2 courseItem2 : courseItem2s) {
									courseItem2.setChecked(false);
								}
							}
						}
					}
				}
				courseLayout = new CourseLayout(SureCourseActivity.this);
				courseLayout.setCourse(studentCourses, this, null, isFromToBeMyStudent);
			}
		} else {
			if (null != tutorCourses && 0 != tutorCourses.size()) {
				for (SubjectCourseModel subjectCourseModel : tutorCourses) {
					ArrayList<SubjectModel> subjectModels = subjectCourseModel.getSubjects();
					if (subjectModels != null && subjectModels.size() > 0) {
						for (SubjectModel subjectModel : subjectModels) {
							subjectModel.setChecked(false);
							if (TextUtils.isEmpty(subjectModel.getName())) {
								subjectModel.setName(subjectCourseModel.getCategory());
							}
						}
					}
				}
				courseLayout = new CourseLayout(SureCourseActivity.this);
				courseLayout.setCourse(tutorCourses, this, null, isFromToBeMyStudent);
			}
		}
		scrollView.addView(courseLayout);
	}

	/**
	 * 获取学生选中的课程
	 * 
	 * @return
	 */
	private ArrayList<CourseItem2> getStudentChoiceCourses() {
		if (null == studentCourses) {
			return null;
		}
		// 选中的课程集合
		ArrayList<CourseItem2> selectCourses = new ArrayList<CourseItem2>();
		for (Course course : studentCourses) {
			ArrayList<CourseItem1> item1s = course.getResult();
			for (CourseItem1 item1 : item1s) {
				ArrayList<CourseItem2> item2s = item1.getResult();
				for (CourseItem2 item2 : item2s) {
					if (item2.isChecked()) {
						selectCourses.add(item2);
					}
				}
			}
		}
		return selectCourses;
	}

	/**
	 * 获取老师选中的课程
	 * 
	 * @return
	 */
	private ArrayList<SubjectModel> getTutorChoiceCourses() {
		if (null == tutorCourses) {
			return null;
		}
		// 选中的课程集合
		ArrayList<SubjectModel> selectCourses = new ArrayList<SubjectModel>();
		for (SubjectCourseModel subjectCourseModel : tutorCourses) {
			ArrayList<SubjectModel> subjectModels = subjectCourseModel.getSubjects();
			for (SubjectModel subjectModel : subjectModels) {
				if (subjectModel.isChecked()) {
					selectCourses.add(subjectModel);
				}
			}
		}
		return selectCourses;
	}

	@Override
	public void refresh(Object obj) {
		if (TutorApplication.getRole() == Constants.General.ROLE_STUDENT || isFromToBeMyStudent) {
			if (null == studentCourses || studentCourses.size() <= 0) {
				return;
			}
			CourseItem2 courseItem = (CourseItem2) obj;
			for (Course course : studentCourses) {
				ArrayList<CourseItem1> courseItem1s = course.getResult();
				if (courseItem1s == null || courseItem1s.size() <= 0) {
					return;
				}
				for (CourseItem1 courseItem1 : courseItem1s) {
					ArrayList<CourseItem2> courseItem2s = courseItem1.getResult();
					if (courseItem2s == null || courseItem2s.size() <= 0) {
						return;
					}
					for (CourseItem2 courseItem2 : courseItem2s) {
						if (courseItem2.getValue() == courseItem.getValue()) {
							courseItem2.setChecked(true);
						} else {
							courseItem2.setChecked(false);
						}
					}
				}
			}
			if (courseLayout != null) {
				courseLayout.reFresh();
			}
		} else {
			if (null == tutorCourses || tutorCourses.size() <= 0) {
				return;
			}
			SubjectModel mSubjectModel = (SubjectModel) obj;
			for (SubjectCourseModel subjectCourseModel : tutorCourses) {
				ArrayList<SubjectModel> subjectModels = subjectCourseModel.getSubjects();
				if (subjectModels == null || subjectModels.size() <= 0) {
					return;
				}
				for (SubjectModel subjectModel : subjectModels) {
					ArrayList<Integer> originValues = subjectModel.getCourseValues();
					ArrayList<Integer> newValues = mSubjectModel.getCourseValues();
					for (int i = 0; i < originValues.size(); i++) {
						int originValue = originValues.get(i);
						for (int j = 0; j < newValues.size(); j++) {
							if (originValue == newValues.get(j)) {
								subjectModel.setChecked(true);
							} else {
								subjectModel.setChecked(false);
							}
						}
					}
				}
			}
			if (courseLayout != null) {
				courseLayout.reFresh();
			}
		}
	}

//	@SuppressWarnings("unchecked")
//	@Override
//	public void onSelectedValues(Object obj) {
//		if (TutorApplication.getRole() == Constants.General.ROLE_STUDENT) {
//			studentCourses = (ArrayList<Course>) obj;
//		} else {
//			tutorCourses = (ArrayList<SubjectCourseModel>) obj;
//		}
//	}
}
