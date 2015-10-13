package com.tutor.ui.fragment.teacher;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.loopj.android.http.RequestParams;
import com.mssky.mobile.ui.view.PullToRefreshBase;
import com.mssky.mobile.ui.view.PullToRefreshBase.Mode;
import com.mssky.mobile.ui.view.PullToRefreshBase.OnRefreshListener2;
import com.mssky.mobile.ui.view.PullToRefreshListView;
import com.tutor.R;
import com.tutor.TutorApplication;
import com.tutor.adapter.MatchStudentAdapter;
import com.tutor.model.UserListResult;
import com.tutor.model.Page;
import com.tutor.model.UserInfo;
import com.tutor.params.ApiUrl;
import com.tutor.ui.fragment.BaseFragment;
import com.tutor.util.CheckTokenUtils;
import com.tutor.util.HttpHelper;
import com.tutor.util.ObjectHttpResponseHandler;
import com.tutor.util.ViewHelper;

/**
 * 老師首頁,我的學生
 * 
 * @author bruce.chen
 * 
 *         2015-8-20
 */
public class MyStudentFragment extends BaseFragment implements OnRefreshListener2<ListView> {

	private PullToRefreshListView listView;
	// 數據
	private UserListResult listResult;
	// 列表
	private ArrayList<UserInfo> users = new ArrayList<UserInfo>();
	// 頁碼,每頁大小
	private int pageIndex = 0, pageSize = 20;
	private MatchStudentAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_teacher_mystudent, container, false);
		initView(view);
		return view;
	}

	private void initView(View view) {
		listView = ViewHelper.get(view, R.id.fragment_find_student_lv);
		// 設置可上拉加載和下拉刷新
		listView.setMode(Mode.BOTH);
		listView.setOnRefreshListener(this);
		adapter = new MatchStudentAdapter(getActivity(), users);
		listView.setAdapter(adapter);
		getStudentList(pageIndex, pageSize);
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
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
	 * 獲取我的學生列表
	 * 
	 */
	private void getStudentList(final int pageIndex, final int pageSize) {
		if (!HttpHelper.isNetworkConnected(getActivity())) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		RequestParams params = new RequestParams();
		params.put("pageIndex", pageIndex);
		params.put("pageSize", pageSize);
		HttpHelper.get(getActivity(), ApiUrl.MYSTUDENTLIST, TutorApplication.getHeaders(), params, new ObjectHttpResponseHandler<UserListResult>(UserListResult.class) {

			@Override
			public void onFailure(int status, String message) {
				if (0 == status) {
					getStudentList(pageIndex, pageSize);
					return;
				}
				CheckTokenUtils.checkToken(status);
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
								toast(R.string.toast_without_mystudent);
							}
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
}
