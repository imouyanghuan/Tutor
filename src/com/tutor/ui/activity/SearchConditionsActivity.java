package com.tutor.ui.activity;

import java.util.ArrayList;

import android.os.Bundle;

import com.loopj.android.http.RequestParams;
import com.tutor.R;
import com.tutor.TutorApplication;
import com.tutor.adapter.SearchConditionsAdapter;
import com.tutor.model.Course;
import com.tutor.model.CourseListResult;
import com.tutor.params.ApiUrl;
import com.tutor.ui.view.CustomExpandableListView;
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
public class SearchConditionsActivity extends BaseActivity {

	private CustomExpandableListView lvConditions;
	// 课程列表
	private ArrayList<Course> courses = new ArrayList<Course>();
	private SearchConditionsAdapter mAdapter;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_search_conditions);
		initView();
		getCourse();
	}

	@Override
	protected void initView() {
		TitleBar bar = getView(R.id.title_bar);
		bar.setTitle(R.string.label_search_conditions);
		bar.initBack(this);
		lvConditions = getView(R.id.lv_conditions);
		// 取消默认箭头
		lvConditions.setGroupIndicator(null);
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
					if (courses != null && courses.size() > 0) {
						courses.clear();
					}
					courses.addAll(t.getResult());
					mAdapter = new SearchConditionsAdapter(SearchConditionsActivity.this, courses);
					lvConditions.setAdapter(mAdapter);
					// mAdapter.notifyDataSetChanged();
				} else {
					toast(R.string.toast_server_error);
				}
			}
		});
	}
}
