package com.tutor.ui.fragment.teacher;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
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
import com.tutor.service.TutorService;
import com.tutor.ui.fragment.BaseFragment;
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
		editText = ViewHelper.get(serchView, R.id.fragment_find_student_et);
		ViewHelper.get(serchView, R.id.fragment_find_student_btn).setOnClickListener(this);
		listView = ViewHelper.get(view, R.id.fragment_find_student_lv);
		listView.getRefreshableView().addHeaderView(serchView);
		// 設置可上拉加載和下拉刷新
		listView.setMode(Mode.BOTH);
		listView.setOnRefreshListener(this);
		adapter = new MatchStudentAdapter(getActivity(), users);
		listView.setAdapter(adapter);
		// 加載匹配學生列表
		new GetStudentListTask(pageIndex, pageSize).execute();
		showDialogRes(R.string.loading);
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
			new GetSearchStudentListTask(keyWords, pageIndex, pageSize).execute();
		} else {
			// 加載匹配學生列表
			new GetStudentListTask(pageIndex, pageSize).execute();
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
					new GetSearchStudentListTask(keyWords, pageIndex, pageSize).execute();
				} else {
					// 加載匹配學生列表
					new GetStudentListTask(pageIndex, pageSize).execute();
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
						new GetStudentListTask(pageIndex, pageSize).execute();
						return;
					}
				}
				isSearchStatus = true;
				// 獲取搜索的學生列表
				pageIndex = 0;
				new GetSearchStudentListTask(keyWords, pageIndex, pageSize).execute();
				break;
			default:
				break;
		}
	}

	/**
	 * 獲取匹配學生列表
	 * 
	 * @author bruce.chen
	 * 
	 */
	private class GetStudentListTask extends AsyncTask<Void, Void, MatchStudentListResult> {

		int pageIndex, pageSize;

		public GetStudentListTask(int pageIndex, int pageSize) {
			this.pageIndex = pageIndex;
			this.pageSize = pageSize;
		}

		@Override
		protected MatchStudentListResult doInBackground(Void... params) {
			return TutorService.getService().getMatchStudentList(pageIndex + "", pageSize + "");
		}

		@Override
		protected void onPostExecute(MatchStudentListResult result) {
			super.onPostExecute(result);
			dismissDialog();
			listView.onRefreshComplete();
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
					}
				} else {
					toast(result.getMessage());
				}
			} else {
				if (!TutorApplication.isTokenInvalid)
					toast(R.string.toast_server_error);
			}
		}
	}

	/**
	 * 獲取搜索學生列表
	 * 
	 * @author bruce.chen
	 * 
	 */
	private class GetSearchStudentListTask extends AsyncTask<Void, Void, MatchStudentListResult> {

		String keyWord;
		int pageIndex, pageSize;

		public GetSearchStudentListTask(String keyWord, int pageIndex, int pageSize) {
			this.keyWord = keyWord;
			this.pageIndex = pageIndex;
			this.pageSize = pageSize;
		}

		@Override
		protected MatchStudentListResult doInBackground(Void... params) {
			return TutorService.getService().geSearchStudentList(keyWord, pageIndex + "", pageSize + "");
		}

		@Override
		protected void onPostExecute(MatchStudentListResult result) {
			super.onPostExecute(result);
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
							}
						} else {
							// 加載更多
							users.addAll(result.getResult());
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
}
