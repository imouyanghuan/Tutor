package com.tutor.ui.activity;

import java.net.HttpURLConnection;
import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.loopj.android.http.RequestParams;
import com.mssky.mobile.ui.view.PullToRefreshBase;
import com.mssky.mobile.ui.view.PullToRefreshBase.Mode;
import com.mssky.mobile.ui.view.PullToRefreshBase.OnRefreshListener;
import com.mssky.mobile.ui.view.PullToRefreshListView;
import com.tutor.R;
import com.tutor.TutorApplication;
import com.tutor.adapter.BookmarkAdapter;
import com.tutor.adapter.BookmarkAdapter.OnDeleteItemClickListener;
import com.tutor.model.BookmarkModel;
import com.tutor.model.BookmarkStudentListResult;
import com.tutor.model.Page;
import com.tutor.model.RemoveBookmarkResult;
import com.tutor.model.UserInfo;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.HttpHelper;
import com.tutor.util.ObjectHttpResponseHandler;

/**
 * 收藏夾
 * 
 * @author bruce.chen
 * 
 *         2015-8-26
 */
public class BookmarkActivity extends BaseActivity implements OnRefreshListener<ListView> {

	// 頁碼,每頁大小
	private int pageIndex = 0, pageSize = 20;
	private PullToRefreshListView lvBookmark;
	// 列表
	private ArrayList<BookmarkModel> users = new ArrayList<BookmarkModel>();
	private BookmarkAdapter mAdapter;
	private BookmarkStudentListResult listResult;
	// 当前用户身份类型
	private int role;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_bookmark);
		role = TutorApplication.getRole();
		if (role == -1) {
			finish();
			return;
		}
		initView();
	}

	@Override
	protected void initView() {
		TitleBar bar = getView(R.id.title_bar);
		bar.initBack(this);
		bar.setTitle(R.string.bookmark);
		lvBookmark = getView(R.id.lv_bookmark);
		lvBookmark.setBackgroundColor(getResources().getColor(R.color.default_bg_color));
		lvBookmark.setShowIndicator(false);
		// 設置可上拉加載和下拉刷新
		lvBookmark.setMode(Mode.PULL_FROM_END);
		lvBookmark.setOnRefreshListener(this);
		mAdapter = new BookmarkAdapter(this, users);
		lvBookmark.setAdapter(mAdapter);
		lvBookmark.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				UserInfo info = mAdapter.getItem(position - 1).getMemberModel();
				if (info != null) {
					Intent intent = null;
					if (info.getAccountType() == Constants.General.ROLE_STUDENT) {
						intent = new Intent(BookmarkActivity.this, StudentInfoActivity.class);
					} else {
						intent = new Intent(BookmarkActivity.this, TeacherInfoActivity.class);
					}
					intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_USER_INFO, info);
					startActivity(intent);
				}
			}
		});
		mAdapter.setOnDeleteItemClickListener(new OnDeleteItemClickListener() {

			@Override
			public void onClick(int bookmarkId, int position) {
				deleteBookmark(bookmarkId);
			}
		});
		getList(pageIndex, pageSize);
	}

	/**
	 * 获取收藏的学生列表
	 * 
	 */
	private void getList(int pageIndex, int pageSize) {
		if (!HttpHelper.isNetworkConnected(this)) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		// 加載匹配學生列表
		showDialogRes(R.string.loading);
		RequestParams params = new RequestParams();
		params.put("pageIndex", pageIndex);
		params.put("pageSize", pageSize);
		String url;
		if (Constants.General.ROLE_STUDENT == role) {
			url = ApiUrl.BOOTMARK_GET_TUTOR_LIST;
		} else {
			url = ApiUrl.BOOTMARK_GET_STUDENT_LIST;
		}
		HttpHelper.get(this, url, TutorApplication.getHeaders(), params, new ObjectHttpResponseHandler<BookmarkStudentListResult>(BookmarkStudentListResult.class) {

			@Override
			public void onFailure(int status, String message) {
				toast(R.string.toast_server_error);
				dismissDialog();
			}

			@Override
			public void onSuccess(BookmarkStudentListResult result) {
				listResult = result;
				dismissDialog();
				if (users != null && users.size() > 0) {
					users.clear();
				}
				users.addAll(result.getResult());
				mAdapter.notifyDataSetChanged();
			}
		});
	}

	/**
	 * 删除书签
	 * 
	 */
	private void deleteBookmark(final int bookmarkId) {
		if (!HttpHelper.isNetworkConnected(this)) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		RequestParams params = new RequestParams();
		String url;
		if (Constants.General.ROLE_STUDENT == role) {
			url = String.format(ApiUrl.BOOTMARK_REMOVE_TUTOR, bookmarkId);
		} else {
			url = String.format(ApiUrl.BOOTMARK_REMOVE_STUDENT, bookmarkId);
		}
		HttpHelper.get(this, url, TutorApplication.getHeaders(), params, new ObjectHttpResponseHandler<RemoveBookmarkResult>(RemoveBookmarkResult.class) {

			@Override
			public void onFailure(int status, String message) {
				toast(R.string.toast_server_error);
				dismissDialog();
			}

			@Override
			public void onSuccess(RemoveBookmarkResult result) {
				if (result.getStatusCode() == HttpURLConnection.HTTP_OK) {
					if (users == null) {
						return;
					}
					for (int i = 0; i < users.size(); i++) {
						BookmarkModel bookmark = users.get(i);
						int bId = bookmark.getBookmarkId();
						if (bId == bookmarkId) {
							users.remove(i);
							mAdapter.notifyDataSetChanged();
							break;
						}
					}
				}
				dismissDialog();
			}
		});
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		if (null != listResult) {
			Page page = listResult.getPage();
			if (null != page && page.isHasNextPage()) {
				pageIndex += 1;
				getList(pageIndex, pageSize);
			} else {
				new Handler().post(new Runnable() {

					@Override
					public void run() {
						lvBookmark.onRefreshComplete();
					}
				});
				toast(R.string.toast_no_more_data);
			}
		} else {
			new Handler().post(new Runnable() {

				@Override
				public void run() {
					lvBookmark.onRefreshComplete();
				}
			});
			toast(R.string.toast_no_data);
		}
	}
}
