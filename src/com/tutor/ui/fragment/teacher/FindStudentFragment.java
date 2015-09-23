package com.tutor.ui.fragment.teacher;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.loopj.android.http.RequestParams;
import com.mssky.mobile.ui.view.PullToRefreshBase;
import com.mssky.mobile.ui.view.PullToRefreshBase.Mode;
import com.mssky.mobile.ui.view.PullToRefreshBase.OnRefreshListener2;
import com.mssky.mobile.ui.view.PullToRefreshListView;
import com.tutor.R;
import com.tutor.TutorApplication;
import com.tutor.adapter.MatchStudentAdapter;
import com.tutor.model.MatchStudentListResult;
import com.tutor.model.Page;
import com.tutor.model.UserInfo;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.activity.SearchConditionsActivity;
import com.tutor.ui.fragment.BaseFragment;
import com.tutor.util.CheckTokenUtils;
import com.tutor.util.HttpHelper;
import com.tutor.util.ObjectHttpResponseHandler;
import com.tutor.util.ViewHelper;

/**
 * 老師首頁,查找學生
 * 
 * @author bruce.chen
 * 
 *         2015-8-20
 */
public class FindStudentFragment extends BaseFragment implements OnRefreshListener2<ListView>, OnClickListener {

	private PullToRefreshListView listView;
	private EditText editText;
	// 數據
	private MatchStudentListResult listResult;
	// 列表
	private ArrayList<UserInfo> users = new ArrayList<UserInfo>();
	private MatchStudentAdapter adapter;
	// 頁碼,每頁大小
	private int pageIndex = 0, pageSize = 20;
	// 搜索學生關鍵字
	private String keyWords;
	// 是否是搜索狀態
	private boolean isSearchStatus = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_teacher_findstudent, container, false);
		initView(inflater, view);
		return view;
	}

	@SuppressLint("InflateParams")
	private void initView(LayoutInflater inflater, View view) {
		View serchView = inflater.inflate(R.layout.find_student_search_layout, null);
		ViewHelper.get(serchView, R.id.btn_search_conditions).setOnClickListener(this);
		editText = ViewHelper.get(serchView, R.id.fragment_find_student_et);
		ViewHelper.get(serchView, R.id.fragment_find_student_btn).setOnClickListener(this);
		listView = ViewHelper.get(view, R.id.fragment_find_student_lv);
		listView.setBackgroundColor(getResources().getColor(R.color.default_bg_color));
		listView.setShowIndicator(false);
		listView.getRefreshableView().addHeaderView(serchView);
		// 設置可上拉加載和下拉刷新
		listView.setMode(Mode.BOTH);
		listView.setOnRefreshListener(this);
		adapter = new MatchStudentAdapter(getActivity(), users);
		listView.setAdapter(adapter);
		// 加載匹配學生列表
		showDialogRes(R.string.loading);
		getStudentList(pageIndex, pageSize);
	}

	/**
	 * 下拉刷新
	 */
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
		// Update the LastUpdatedLabel
		refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
		pageIndex = 0;
		if (isSearchStatus) {
			// 加載搜索學生列表
			getSearchStudent(keyWords, pageIndex, pageSize);
		} else {
			// 加載匹配學生列表
			getStudentList(pageIndex, pageSize);
		}
	}

	/**
	 * 上拉加載更多
	 */
	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		if (null != listResult) {
			Page page = listResult.getPage();
			if (null != page && page.isHasNextPage()) {
				pageIndex += 1;
				if (isSearchStatus) {
					// 加載搜索學生列表
					getSearchStudent(keyWords, pageIndex, pageSize);
				} else {
					// 加載匹配學生列表
					getStudentList(pageIndex, pageSize);
				}
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.fragment_find_student_btn:
				// 關鍵字搜索
				keyWords = editText.getEditableText().toString().trim();
				if (TextUtils.isEmpty(keyWords)) {
					if (!isSearchStatus) {
						toast(R.string.toast_no_keywords);
						return;
					} else {
						isSearchStatus = false;
						// 獲取匹配學生列表
						pageIndex = 0;
						getStudentList(pageIndex, pageSize);
						return;
					}
				}
				isSearchStatus = true;
				// 獲取搜索的學生列表
				pageIndex = 0;
				getSearchStudent(keyWords, pageIndex, pageSize);
				break;
			case R.id.btn_search_conditions:
				Intent intent = new Intent(getActivity(), SearchConditionsActivity.class);
				startActivityForResult(intent, Constants.RequestResultCode.SEARCH_CONDITIONS);
				break;
			default:
				break;
		}
	}

	/**
	 * 獲取匹配學生列表
	 * 
	 */
	private void getStudentList(final int pageIndex, int pageSize) {
		if (!HttpHelper.isNetworkConnected(getActivity())) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		RequestParams params = new RequestParams();
		params.put("pageIndex", pageIndex);
		params.put("pageSize", pageSize);
		HttpHelper.get(getActivity(), ApiUrl.STUDENTMATCH, TutorApplication.getHeaders(), params, new ObjectHttpResponseHandler<MatchStudentListResult>(MatchStudentListResult.class) {

			@Override
			public void onFailure(int status, String message) {
				dismissDialog();
				CheckTokenUtils.checkToken(status);
				setData(null);
			}

			@Override
			public void onSuccess(MatchStudentListResult result) {
				dismissDialog();
				CheckTokenUtils.checkToken(result);
				setData(result);
			}
		});
	}

	/**
	 * 獲取搜索學生列表
	 * 
	 */
	private void getSearchStudent(String keyWord, int pageIndex, int pageSize) {
		if (!HttpHelper.isNetworkConnected(getActivity())) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		RequestParams params = new RequestParams();
		params.put("keywords", keyWord);
		params.put("pageIndex", pageIndex);
		params.put("pageSize", pageSize);
		HttpHelper.get(getActivity(), ApiUrl.SEARCHSTUDENT, TutorApplication.getHeaders(), params, new ObjectHttpResponseHandler<MatchStudentListResult>(MatchStudentListResult.class) {

			@Override
			public void onFailure(int status, String message) {
				setData(null);
				CheckTokenUtils.checkToken(status);
				listView.setBackgroundColor(getResources().getColor(R.color.default_bg_color));
			}

			@Override
			public void onSuccess(MatchStudentListResult t) {
				CheckTokenUtils.checkToken(t);
				setData(t);
			}
		});
	}

	private void setData(MatchStudentListResult result) {
		listView.onRefreshComplete();
		if (null != result) {
			if (200 == result.getStatusCode()) {
				listResult = result;
				if (null != result.getResult()) {
					if (0 == pageIndex) {
						// 首次加載或下拉刷新的時候清空數據
						users.clear();
						users.addAll(result.getResult());
						if (0 == users.size()) {
							toast(R.string.toast_no_match_student);
							listView.setBackgroundColor(getResources().getColor(R.color.default_bg_color));
						} else {
							listView.setBackgroundColor(getResources().getColor(R.color.transparent));
						}
					} else {
						// 加載更多
						users.addAll(result.getResult());
						listView.setBackgroundColor(getResources().getColor(R.color.transparent));
					}
					if (null != adapter) {
						adapter.refresh(users);
					}
				}
			} else {
				toast(result.getMessage());
			}
		} else {
			if (!TutorApplication.isTokenInvalid) {
				toast(R.string.toast_server_error);
			}
		}
	}
}
