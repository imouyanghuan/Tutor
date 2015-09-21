package com.tutor.ui.activity;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ScrollView;

import com.loopj.android.http.RequestParams;
import com.tutor.R;
import com.tutor.TutorApplication;
import com.tutor.model.Course;
import com.tutor.model.CourseItem1;
import com.tutor.model.CourseItem2;
import com.tutor.model.CourseListResult;
import com.tutor.model.StudentProfile;
import com.tutor.model.TeacherProfile;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.view.CourseLayout;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.HttpHelper;
import com.tutor.util.LogUtils;
import com.tutor.util.ObjectHttpResponseHandler;

/**
 * 选择课程
 * 
 * @author bruce.chen
 * 
 *         2015-9-18
 */
public class SelectCourseActivity extends BaseActivity {

	private ScrollView scrollView;
	/** 是否是编辑资料模式 */
	private boolean isEdit;
	private int role;
	private TeacherProfile teacherProfile;
	private StudentProfile studentProfile;
	// 课程列表
	private ArrayList<Course> courses;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_select_course);
		isEdit = getIntent().getBooleanExtra(Constants.IntentExtra.INTENT_EXTRA_ISEDIT, false);
		teacherProfile = (TeacherProfile) getIntent().getSerializableExtra(Constants.IntentExtra.INTENT_EXTRA_TUTORPRIFILE);
		studentProfile = (StudentProfile) getIntent().getSerializableExtra(Constants.IntentExtra.INTENT_EXTRA_STUDENTPROFILE);
		if (null != teacherProfile) {
			role = teacherProfile.getAccountType();
		} else {
			role = studentProfile.getAccountType();
		}
		if (role == -1) {
			throw new IllegalArgumentException("role is -1");
		}
		initView();
		// 获取数据
		getCourse();
	}

	@Override
	protected void initView() {
		TitleBar bar = getView(R.id.title_bar);
		bar.initBack(this);
		if (isEdit) {
			bar.setTitle(R.string.label_edit_couress);
		} else {
			if (Constants.General.ROLE_TUTOR == role) {
				bar.setTitle(R.string.label_teaching_couress);
			} else {
				bar.setTitle(R.string.label_select_couress);
			}
		}
		bar.setRightText(R.string.next, new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 课程列表
				ArrayList<Course> choiceCourses = getChoiceCourses();
				if (!isHaveCourse) {
					toast(R.string.toast_not_select_cours);
					return;
				}
				Intent intent = new Intent(SelectCourseActivity.this, SelectAreaActivity.class);
				intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_ISEDIT, isEdit);
				if (Constants.General.ROLE_TUTOR == role) {
					teacherProfile.setCourses(choiceCourses);
					intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_TUTORPRIFILE, teacherProfile);
				} else {
					studentProfile.setCourses(choiceCourses);
					intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_STUDENTPROFILE, studentProfile);
				}
				startActivity(intent);
			}
		});
		scrollView = getView(R.id.ac_fill_personal_course_scrollview);
	}

	@Override
	protected void onStart() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.Action.FINISH_LOGINACTIVITY);
		registerReceiver(receiver, filter);
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (Constants.Action.FINISH_LOGINACTIVITY.equals(intent.getAction())) {
				finishNoAnim();
			}
		}
	};

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
					courses = t.getResult();
					setData(courses);
				} else {
					toast(R.string.toast_server_error);
				}
			}
		});
	}

	private void setData(ArrayList<Course> courses) {
		if (null != courses && 0 != courses.size()) {
			if (isEdit) {
				// 对比改变一下数据
				ArrayList<Course> ownCourses = null;
				if (null != teacherProfile) {
					ownCourses = teacherProfile.getCourses();
				} else {
					ownCourses = studentProfile.getCourses();
				}
				if (null != ownCourses && ownCourses.size() > 0) {
					ArrayList<CourseItem2> ownitem2s = new ArrayList<CourseItem2>();
					for (Course course : ownCourses) {
						ArrayList<CourseItem1> item1s = course.getResult();
						for (CourseItem1 item1 : item1s) {
							ownitem2s.addAll(item1.getResult());
						}
					}
					change(courses, ownitem2s);
				}
			}
			CourseLayout courseLayout = new CourseLayout(SelectCourseActivity.this, courses);
			scrollView.addView(courseLayout);
		}
	}

	private void change(ArrayList<Course> courses, ArrayList<CourseItem2> ownitem2s) {
		if (null != ownitem2s && ownitem2s.size() > 0) {
			for (CourseItem2 item2 : ownitem2s) {
				for (Course course : courses) {
					if (change(item2, course)) {
						break;
					}
				}
			}
		}
	}

	private boolean change(CourseItem2 item2, Course course) {
		ArrayList<CourseItem1> item1s = course.getResult();
		for (CourseItem1 item1 : item1s) {
			ArrayList<CourseItem2> item2s = item1.getResult();
			for (CourseItem2 item22 : item2s) {
				if (item22.getId() == item2.getId()) {
					item22.setSelected(true);
					return true;
				}
			}
		}
		return false;
	}

	private boolean isHaveCourse = false;

	/**
	 * 获取选中的课程
	 * 
	 * @return
	 */
	private ArrayList<Course> getChoiceCourses() {
		if (null == courses) {
			return null;
		}
		// 选中的课程集合
		ArrayList<CourseItem2> selectCourses = new ArrayList<CourseItem2>();
		for (Course course : courses) {
			ArrayList<CourseItem1> item1s = course.getResult();
			for (CourseItem1 item1 : item1s) {
				ArrayList<CourseItem2> item2s = item1.getResult();
				for (CourseItem2 item2 : item2s) {
					if (item2.getSelected()) {
						selectCourses.add(item2);
					}
				}
			}
		}
		// 将选中的课程整理成原来的结构
		int selectSize = selectCourses.size();
		if (selectSize > 0) {
			isHaveCourse = true;
			ArrayList<Course> result = new ArrayList<Course>();
			for (CourseItem2 item2 : selectCourses) {
				// 首次添加
				if (result.size() == 0) {
					// 1
					Course course = new Course();
					course.setName(item2.getType());
					ArrayList<CourseItem1> item1s = new ArrayList<CourseItem1>();
					// 2
					CourseItem1 item1 = new CourseItem1();
					item1.setName(item2.getSubType());
					// 3
					ArrayList<CourseItem2> item2s = new ArrayList<CourseItem2>();
					item2s.add(item2);
					// 2
					item1.setResult(item2s);
					item1s.add(item1);
					// 1
					course.setResult(item1s);
					result.add(course);
				} else {
					// 列表已经有数据了
					int size = result.size();
					Course course = null;
					for (int i = 0; i < size; i++) {
						course = result.get(i);
						if (course.getName().equals(item2.getType())) {
							break;
						} else {
							course = null;
						}
					}
					// 已存在该分类
					if (null != course) {
						// 取出子list
						ArrayList<CourseItem1> item1s = course.getResult();
						int size1 = item1s.size();
						// 取出CourseItem1
						CourseItem1 item1 = null;
						for (int i = 0; i < size1; i++) {
							item1 = item1s.get(i);
							if (item1.getName().equals(item2.getSubType())) {
								break;
							} else {
								item1 = null;
							}
						}
						if (null != item1) {
							// 存在,把item2加入item1的result集合里
							item1.getResult().add(item2);
						} else {
							// 不存在,添加一个
							CourseItem1 newitem1 = new CourseItem1();
							newitem1.setName(item2.getSubType());
							// 3
							ArrayList<CourseItem2> item2s = new ArrayList<CourseItem2>();
							item2s.add(item2);
							// 2
							newitem1.setResult(item2s);
							item1s.add(newitem1);
						}
					} else {
						// 不存在该分类的时候,添加
						// 1
						course = new Course();
						course.setName(item2.getType());
						ArrayList<CourseItem1> item1s = new ArrayList<CourseItem1>();
						// 2
						CourseItem1 item1 = new CourseItem1();
						item1.setName(item2.getSubType());
						// 3
						ArrayList<CourseItem2> item2s = new ArrayList<CourseItem2>();
						item2s.add(item2);
						// 2
						item1.setResult(item2s);
						item1s.add(item1);
						// 1
						course.setResult(item1s);
						result.add(course);
					}
				}
			}
			LogUtils.d(result.toString());
			LogUtils.d(courses.toString());
			return result;
		}
		return null;
	}
}
