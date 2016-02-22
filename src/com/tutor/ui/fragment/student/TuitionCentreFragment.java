package com.tutor.ui.fragment.student;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

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
import android.widget.ImageButton;
import android.widget.ListView;

import com.hk.tutor.R;
import com.loopj.android.http.RequestParams;
import com.mssky.mobile.ui.view.PullToRefreshBase;
import com.mssky.mobile.ui.view.PullToRefreshBase.Mode;
import com.mssky.mobile.ui.view.PullToRefreshBase.OnRefreshListener2;
import com.mssky.mobile.ui.view.PullToRefreshListView;
import com.tutor.TutorApplication;
import com.tutor.adapter.TuitionCenterAdapter;
import com.tutor.model.Page;
import com.tutor.model.SearchCondition;
import com.tutor.model.SearchTuitionCentre;
import com.tutor.model.UserInfo;
import com.tutor.model.UserListResult;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.activity.SearchConditionsOfTuitionCenterActivity;
import com.tutor.ui.fragment.BaseFragment;
import com.tutor.util.CheckTokenUtils;
import com.tutor.util.HttpHelper;
import com.tutor.util.JsonUtil;
import com.tutor.util.ObjectHttpResponseHandler;
import com.tutor.util.ViewHelper;

/**
 * 学生首页，补习社
 * 
 * @author jerry.yao
 * 
 *         2015-12-11
 */
public class TuitionCentreFragment extends BaseFragment implements OnRefreshListener2<ListView>, OnClickListener {

	private PullToRefreshListView listView;
	private EditText editText;
	// 數據
	private UserListResult listResult;
	// 列表
	private ArrayList<UserInfo> users = new ArrayList<UserInfo>();
	private TuitionCenterAdapter adapter;
	// 頁碼,每頁大小
	private int pageIndex = 0, pageSize = 5;
	// 是否是搜索狀態
	// private boolean isSearchStatus = false;
	private SearchCondition condition = null;
	private ImageButton ibDelete;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_tuition_center_list, container, false);
		initView(inflater, view);
		return view;
	}

	@SuppressLint("InflateParams")
	private void initView(LayoutInflater inflater, View view) {
		View serchView = inflater.inflate(R.layout.find_student_search_layout, null);
		editText = ViewHelper.get(serchView, R.id.fragment_find_student_et);
		editText.setHint(R.string.label_search_tuition_center);
		editText.setOnClickListener(this);
		editText.setFocusable(false);
		ibDelete = ViewHelper.get(serchView, R.id.ib_delete);
		ibDelete.setOnClickListener(this);
		ViewHelper.get(serchView, R.id.fragment_find_student_btn).setOnClickListener(this);
		listView = ViewHelper.get(view, R.id.fragment_find_student_lv);
		listView.setBackgroundColor(getResources().getColor(R.color.default_bg_color));
		listView.setShowIndicator(false);
		listView.getRefreshableView().addHeaderView(serchView);
		// 設置可上拉加載和下拉刷新
		listView.setMode(Mode.BOTH);
		listView.setOnRefreshListener(this);
		adapter = new TuitionCenterAdapter(getActivity(), users);
		listView.setAdapter(adapter);
		// 加載匹配學生列表
		getTuitionCenterList(pageIndex, pageSize);
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
		getTuitionCenterList(pageIndex, pageSize);
		// if (isSearchStatus) {
		// // 加載搜索學生列表
		// } else {
		// // 加載匹配學生列表
		// getTuitionCenterList(pageIndex, pageSize);
		// }
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
				getTuitionCenterList(pageIndex, pageSize);
				// if (isSearchStatus) {
				// // 加載搜索學生列表
				// } else {
				// // 加載匹配學生列表
				// getTuitionCenterList(pageIndex, pageSize);
				// }
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
				searchByCondition();
				break;
			// 添加条件搜索
			case R.id.fragment_find_student_et:
				Intent intent = new Intent(getActivity(), SearchConditionsOfTuitionCenterActivity.class);
				intent.putExtra(Constants.General.IS_FROM_TEACHER, true);
				startActivityForResult(intent, Constants.RequestResultCode.SEARCH_CONDITIONS);
				break;
			// 删除搜索条件
			case R.id.ib_delete:
				editText.setText("");
				condition = null;
				editText.setHint(R.string.label_search_tuition_center);
				ibDelete.setVisibility(View.GONE);
				getTuitionCenterList(pageIndex, pageSize);
				break;
			default:
				break;
		}
	}

	private void searchByCondition() {
		// 獲取搜索的學生列表
		pageIndex = 0;
		if (!HttpHelper.isNetworkConnected(getActivity())) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		getTuitionCenterList(pageIndex, pageSize);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case Constants.RequestResultCode.SEARCH_CONDITIONS:
				if (data != null) {
					condition = (SearchCondition) data.getSerializableExtra(Constants.IntentExtra.INTENT_EXTRA_SEARCH_CONDITION);
					if (condition != null) {
						String COM = ", ";
						String SPACE = "";
						String keyword = condition.getSearchKeyword();
						if (!TextUtils.isEmpty(keyword)) {
							keyword += COM;
						} else {
							keyword = SPACE;
						}
						String gradeName = condition.getGradeName();
						if (!TextUtils.isEmpty(gradeName)) {
							gradeName += COM;
						} else {
							gradeName = SPACE;
						}
						String areaName = condition.getAreaName();
						if (!TextUtils.isEmpty(areaName)) {
							areaName += COM;
						} else {
							areaName = SPACE;
						}
						String searchKeyWork = keyword + gradeName + areaName;
						if (searchKeyWork.length() > 2) {
							editText.setText(searchKeyWork.substring(0, (searchKeyWork.length() - 2)));
						} else {
							editText.setText("");
						}
						if (editText.getText().toString().length() > 0) {
							ibDelete.setVisibility(View.VISIBLE);
						} else {
							ibDelete.setVisibility(View.GONE);
						}
					}
					searchByCondition();
				}
				break;
			default:
				break;
		}
	}

	/**
	 * 獲取补习社列表
	 * 
	 */
	private void getTuitionCenterList(final int pageIndex, final int pageSize) {
		if (!HttpHelper.isNetworkConnected(getActivity())) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		showDialogRes(R.string.loading);
		RequestParams params = new RequestParams();
		params.put("pageIndex", pageIndex);
		params.put("pageSize", pageSize);
		//
		SearchTuitionCentre searchTuitionCenter = new SearchTuitionCentre();
		searchTuitionCenter.setAreaValue(1);
		searchTuitionCenter.setServiceGradeValue(2);
		//
		String body;
		if (condition == null) {
			body = JsonUtil.parseObject2Str(new Object());
		} else {
			body = JsonUtil.parseObject2Str(condition);
		}
		StringEntity entity = null;
		try {
			entity = new StringEntity(body, HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		HttpHelper.getHelper().post(ApiUrl.TUITION_CENTER_SEARCH, TutorApplication.getHeaders(), entity, new ObjectHttpResponseHandler<UserListResult>(UserListResult.class) {

			@Override
			public void onFailure(int status, String message) {
				dismissDialog();
				if (0 == status) {
					getTuitionCenterList(pageIndex, pageSize);
					return;
				}
				CheckTokenUtils.checkToken(status);
				setData(null);
			}

			@Override
			public void onSuccess(UserListResult result) {
				dismissDialog();
				CheckTokenUtils.checkToken(result);
				setData(result);
			}
		});
	}

	private void setData(UserListResult result) {
		listView.onRefreshComplete();
		dismissDialog();
		if (null != result) {
			if (200 == result.getStatusCode()) {
				listResult = result;
				if (null != result.getResult()) {
					if (0 == pageIndex) {
						// 首次加載或下拉刷新的時候清空數據
						users.clear();
						users.addAll(result.getResult());
						if (0 == users.size()) {
							toast(R.string.toast_no_match_tuition_center);
							listView.setBackgroundColor(getResources().getColor(R.color.default_bg_color));
						} else {
							if (users.size() > 2) {
								listView.setBackgroundColor(getResources().getColor(R.color.transparent));
							} else {
								listView.setBackgroundColor(getResources().getColor(R.color.default_bg_color));
							}
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
