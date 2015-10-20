package com.tutor.ui.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
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
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
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
public class SureCourseActivity extends BaseActivity {

	private ScrollView scrollView;
	private int role = -1;
	// 课程列表
	private ArrayList<Course> courses;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_select_course);
		role = getIntent().getIntExtra(Constants.IntentExtra.INTENT_EXTRA_ROLE, -1);
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
		if (Constants.General.ROLE_STUDENT == role) {
			bar.setTitle(R.string.label_teaching_couress);
		} else {
			bar.setTitle(R.string.label_select_couress);
		}
		bar.setRightText(R.string.btn_save, new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 课程列表
				ArrayList<CourseItem2> choiceCourses = getChoiceCourses();
				Intent intent = new Intent();
				intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_COURSESLIST, choiceCourses);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		scrollView = getView(R.id.ac_fill_personal_course_scrollview);
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
			CourseLayout courseLayout = new CourseLayout(SureCourseActivity.this, courses);
			scrollView.addView(courseLayout);
		}
	}

	/**
	 * 获取选中的课程
	 * 
	 * @return
	 */
	private ArrayList<CourseItem2> getChoiceCourses() {
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
					if (item2.isChecked()) {
						selectCourses.add(item2);
					}
				}
			}
		}
		return selectCourses;
	}
}
