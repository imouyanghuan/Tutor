package com.tutor.ui.activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.hk.tutor.R;
import com.loopj.android.http.RequestParams;
import com.mssky.mobile.ui.view.PullToRefreshBase;
import com.mssky.mobile.ui.view.PullToRefreshBase.Mode;
import com.mssky.mobile.ui.view.PullToRefreshBase.OnRefreshListener2;
import com.mssky.mobile.ui.view.PullToRefreshListView;
import com.tutor.TutorApplication;
import com.tutor.adapter.TeacherListAdapter;
import com.tutor.model.Page;
import com.tutor.model.UserInfo;
import com.tutor.model.UserListResult;
import com.tutor.params.ApiUrl;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.CheckTokenUtils;
import com.tutor.util.HttpHelper;
import com.tutor.util.ObjectHttpResponseHandler;

/**
 * 學生首頁,我的老師
 * 
 * @author jerry.yao
 * 
 *         2015-12-9
 */
public class MyTutorsActivity extends BaseActivity implements OnRefreshListener2<ListView> {
	/** 頂部菜單 */
	private TitleBar bar;
	private PullToRefreshListView listView;
	// 數據
	private UserListResult listResult;
	// 列表
	private ArrayList<UserInfo> users = new ArrayList<UserInfo>();
	// 頁碼,每頁大小
	private int pageIndex = 0, pageSize = 20;
	private TeacherListAdapter adapter;
	private LinearLayout llNoMatch;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.fragment_student_myteacher);
		initView();
	}

	@Override
	protected void initView() {
		bar = getView(R.id.title_bar);
		bar.initBack(this);
		bar.setTitle(R.string.myteachers);
		// 没有匹配的提示
		llNoMatch = getView(R.id.ll_no_match);
		listView = getView(R.id.fragment_find_student_lv);
		// 設置可上拉加載和下拉刷新
		listView.setMode(Mode.BOTH);
		listView.setOnRefreshListener(this);
		adapter = new TeacherListAdapter(MyTutorsActivity.this, users);
		listView.setAdapter(adapter);
		getStudentList(pageIndex, pageSize);
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		String label = DateUtils.formatDateTime(MyTutorsActivity.this, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
		// Update the LastUpdatedLabel
		refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
		pageIndex = 0;
		getStudentList(pageIndex, pageSize);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		if (null != listResult) {
			Page page = listResult.getPage();
			if (null != page && page.isHasNextPage()) {
				pageIndex += 1;
				// 加載我的學生列表
				getStudentList(pageIndex, pageSize);
			} else {
				new Handler().post(new Runnable() {

					@Override
					public void run() {
						listView.onRefreshComplete();
					}
				});
				toast(R.string.toast_no_more_data);
			}
		} else {
			new Handler().post(new Runnable() {

				@Override
				public void run() {
					listView.onRefreshComplete();
				}
			});
			toast(R.string.toast_no_data);
		}
	}

	/**
	 * 獲取我的老师列表
	 * 
	 */
	private void getStudentList(final int pageIndex, final int pageSize) {
		if (!HttpHelper.isNetworkConnected(MyTutorsActivity.this)) {
			toast(getString(R.string.toast_netwrok_disconnected));
			return;
		}
		RequestParams params = new RequestParams();
		params.put("pageIndex", pageIndex);
		params.put("pageSize", pageSize);
		HttpHelper.getHelper().get(ApiUrl.MYTUTORLIST, TutorApplication.getHeaders(), params, new ObjectHttpResponseHandler<UserListResult>(UserListResult.class) {

			@Override
			public void onFailure(int status, String message) {
				if (0 == status) {
					getStudentList(pageIndex, pageSize);
					return;
				}
				if (CheckTokenUtils.checkToken(status)) {
					return;
				}
				toast(R.string.toast_server_error);
			}

			@Override
			public void onSuccess(UserListResult result) {
				listView.onRefreshComplete();
				CheckTokenUtils.checkToken(result);
				if (null != result) {
					if (200 == result.getStatusCode()) {
						listResult = result;
						if (null != result.getResult()) {
							if (0 == pageIndex) {
								// 首次加載或下拉刷新的時候清空數據
								users.clear();
								users.addAll(result.getResult());
							} else {
								// 加載更多
								users.addAll(result.getResult());
							}
							if (null != adapter) {
								adapter.refresh(users);
							}
							if (0 == users.size()) {
								// toast(R.string.toast_without_mytutor);
								showNoMatch();
							} else {
								hideNoMatch();
							}
						} else {
							showNoMatch();
						}
					} else {
						toast(result.getMessage());
					}
				} else {
					if (!TutorApplication.isTokenInvalid)
						toast(R.string.toast_server_error);
				}
			}
		});
	}

	private void showNoMatch() {
		listView.setVisibility(View.GONE);
		llNoMatch.setVisibility(View.VISIBLE);
	}

	private void hideNoMatch() {
		llNoMatch.setVisibility(View.GONE);
		listView.setVisibility(View.VISIBLE);
	}

	public void refresh() {
		pageIndex = 0;
		getStudentList(pageIndex, pageSize);
	}
}
