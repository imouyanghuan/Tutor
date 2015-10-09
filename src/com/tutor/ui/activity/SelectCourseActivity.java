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
import com.tutor.model.UserInfo;
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
	private int role = -1;
	private UserInfo userInfo;
	// 课程列表
	private ArrayList<Course> courses;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_select_course);
		isEdit = getIntent().getBooleanExtra(Constants.IntentExtra.INTENT_EXTRA_ISEDIT, false);
		userInfo = (UserInfo) getIntent().getSerializableExtra(Constants.IntentExtra.INTENT_EXTRA_USER_INFO);
		if (null != userInfo) {
			role = userInfo.getAccountType();
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
				int[] choiceCourses = getChoiceCourses();
				Intent intent = new Intent(SelectCourseActivity.this, SelectAreaActivity.class);
				intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_ISEDIT, isEdit);
				userInfo.setCoursesValues(choiceCourses);
				intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_USER_INFO, userInfo);
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
			CourseLayout courseLayout = new CourseLayout(SelectCourseActivity.this, courses);
			scrollView.addView(courseLayout);
		}
	}

	/**
	 * 获取选中的课程
	 * 
	 * @return
	 */
	private int[] getChoiceCourses() {
		if (null == courses) {
			return new int[] {};
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
		int size = selectCourses.size();
		if (size > 0) {
			int[] result = new int[size];
			for (int i = 0; i < size; i++) {
				result[i] = selectCourses.get(i).getValue();
			}
			LogUtils.d(result.toString());
			return result;
		}
		return new int[] {};
	}
}
