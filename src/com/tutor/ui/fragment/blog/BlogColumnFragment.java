package com.tutor.ui.fragment.blog;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.hk.tutor.R;
import com.loopj.android.http.RequestParams;
import com.mssky.mobile.ui.view.PullToRefreshBase;
import com.mssky.mobile.ui.view.PullToRefreshBase.Mode;
import com.mssky.mobile.ui.view.PullToRefreshBase.OnRefreshListener2;
import com.mssky.mobile.ui.view.PullToRefreshListView;
import com.tutor.TutorApplication;
import com.tutor.adapter.BlogAdapter;
import com.tutor.model.BlogListResult;
import com.tutor.model.BlogModel;
import com.tutor.model.Page;
import com.tutor.params.ApiUrl;
import com.tutor.ui.fragment.BaseFragment;
import com.tutor.util.CheckTokenUtils;
import com.tutor.util.HttpHelper;
import com.tutor.util.ObjectHttpResponseHandler;
import com.tutor.util.ViewHelper;

/**
 * 子栏目ChildFragment
 * 
 * @author jerry.yao
 * 
 */
public class BlogColumnFragment extends BaseFragment implements OnRefreshListener2<ListView> {

	private View rootView;
	private int categoryValue;
	private PullToRefreshListView lvBlog;
	private BlogAdapter adapter;
	// 列表
	private ArrayList<BlogModel> blogs = new ArrayList<BlogModel>();
	// 頁碼,每頁大小
	private int pageIndex = 0, pageSize = 20;
	// 數據
	private BlogListResult listResult;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_blog_child_column, null);
		initView();
		return rootView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getBlogList(pageIndex, pageSize);
	}

	public void initData() {}

	/**
	 * 獲取我的Blog列表
	 * 
	 */
	private void getBlogList(final int pageIndex, final int pageSize) {
		if (!isAdded()) {
			return;
		}
		if (!HttpHelper.isNetworkConnected(getActivity())) {
			toast(getString(R.string.toast_netwrok_disconnected));
			return;
		}
		// showDialogRes(R.string.loading);
		RequestParams params = new RequestParams();
		params.put("pageIndex", pageIndex);
		params.put("pageSize", pageSize);
		params.put("categoryValue", categoryValue);
		HttpHelper.getHelper().get(ApiUrl.BLOG_LIST_BY_CATEGORY, TutorApplication.getHeaders(), params, new ObjectHttpResponseHandler<BlogListResult>(BlogListResult.class) {

			@Override
			public void onFailure(int status, String message) {
				// dismissDialog();
				if (0 == status) {
					getBlogList(pageIndex, pageSize);
					return;
				}
				if (CheckTokenUtils.checkToken(status)) {
					return;
				}
				toast(R.string.toast_server_error);
			}

			@Override
			public void onSuccess(BlogListResult result) {
				lvBlog.onRefreshComplete();
				// dismissDialog();
				CheckTokenUtils.checkToken(result);
				if (null != result) {
					if (200 == result.getStatusCode()) {
						listResult = result;
						if (null != result.getResult()) {
							if (0 == pageIndex) {
								// 首次加載或下拉刷新的時候清空數據
								blogs.clear();
								blogs.addAll(result.getResult());
							} else {
								// 加載更多
								blogs.addAll(result.getResult());
							}
							if (null != adapter) {
								adapter.refresh(blogs);
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

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
		// Update the LastUpdatedLabel
		refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
		pageIndex = 0;
		getBlogList(pageIndex, pageSize);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		if (null != listResult) {
			Page page = listResult.getPage();
			if (null != page && page.isHasNextPage()) {
				pageIndex += 1;
				// 加載我的學生列表
				getBlogList(pageIndex, pageSize);
			} else {
				new Handler().post(new Runnable() {

					@Override
					public void run() {
						lvBlog.onRefreshComplete();
					}
				});
				toast(R.string.toast_no_more_data);
			}
		} else {
			new Handler().post(new Runnable() {

				@Override
				public void run() {
					lvBlog.onRefreshComplete();
				}
			});
			toast(R.string.toast_no_data);
		}
	}

	private void initView() {
		lvBlog = ViewHelper.get(rootView, R.id.lv_blog);
		// 設置可上拉加載和下拉刷新
		lvBlog.setMode(Mode.BOTH);
		lvBlog.setOnRefreshListener(this);
		adapter = new BlogAdapter(getActivity(), blogs);
		lvBlog.setAdapter(adapter);
		getBlogList(pageIndex, pageSize);
	}

	/**
	 * @return blog类别编号
	 */
	public int getCategoryValue() {
		return categoryValue;
	}

	/**
	 * @param categoryValue
	 *            blog类别编号
	 */
	public void setCategoryValue(int categoryValue) {
		this.categoryValue = categoryValue;
	}
}
