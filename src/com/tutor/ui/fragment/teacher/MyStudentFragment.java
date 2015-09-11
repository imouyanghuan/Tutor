package com.tutor.ui.fragment.teacher;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
 * 老師首頁,我的學生
 * 
 * @author bruce.chen
 * 
 *         2015-8-20
 */
public class MyStudentFragment extends BaseFragment implements OnRefreshListener2<ListView>, OnItemClickListener {

	private PullToRefreshListView listView;
	// 數據
	private MatchStudentListResult listResult;
	// 列表
	private ArrayList<UserInfo> users = new ArrayList<UserInfo>();
	private MatchStudentAdapter adapter;
	// 頁碼,每頁大小
	private int pageIndex = 0, pageSize = 20;

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
		listView.setOnItemClickListener(this);
		adapter = new MatchStudentAdapter(getActivity(), users, listView.getRefreshableView());
		listView.setAdapter(adapter);
		new GetStudentListTask(pageIndex, pageSize).execute();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
		// Update the LastUpdatedLabel
		refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
		pageIndex = 0;
		new GetStudentListTask(pageIndex, pageSize).execute();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		if (null != listResult) {
			Page page = listResult.getPage();
			if (null != page && page.isHasNextPage()) {
				pageIndex += 1;
				// 加載我的學生列表
				new GetStudentListTask(pageIndex, pageSize).execute();
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
		protected MatchStudentListResult doInBackground(Void... arg0) {
			return TutorService.getService().getMyStudentList(pageIndex + "", pageSize + "");
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
	}
}