package com.tutor.ui.fragment.teacher;

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
import com.tutor.adapter.MatchStudentAdapter;
import com.tutor.model.UserListResult;
import com.tutor.model.Page;
import com.tutor.model.SearchCondition;
import com.tutor.model.Timeslot;
import com.tutor.model.UserInfo;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.activity.SearchConditionsActivity;
import com.tutor.ui.fragment.BaseFragment;
import com.tutor.util.CheckTokenUtils;
import com.tutor.util.HttpHelper;
import com.tutor.util.JsonUtil;
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
	private UserListResult listResult;
	// 列表
	private ArrayList<UserInfo> users = new ArrayList<UserInfo>();
	private MatchStudentAdapter adapter;
	// 頁碼,每頁大小
	private int pageIndex = 0, pageSize = 20;
	// 是否是搜索狀態
	private boolean isSearchStatus = false;
	private SearchCondition condition = null;
	private ImageButton ibDelete;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_teacher_findstudent, container, false);
		initView(inflater, view);
		return view;
	}

	@SuppressLint("InflateParams")
	private void initView(LayoutInflater inflater, View view) {
		View serchView = inflater.inflate(R.layout.find_student_search_layout, null);
		editText = ViewHelper.get(serchView, R.id.fragment_find_student_et);
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
			getSearchStudent(pageIndex, pageSize);
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
					getSearchStudent(pageIndex, pageSize);
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
				searchByCondication();
				break;
			// 添加条件搜索
			case R.id.fragment_find_student_et:
				Intent intent = new Intent(getActivity(), SearchConditionsActivity.class);
				intent.putExtra(Constants.General.IS_FROM_TEACHER, true);
				startActivityForResult(intent, Constants.RequestResultCode.SEARCH_CONDITIONS);
				break;
			// 删除搜索条件
			case R.id.ib_delete:
				editText.setText("");
				condition = null;
				editText.setHint(R.string.hint_search_student_keywords);
				ibDelete.setVisibility(View.GONE);
				getStudentList(pageIndex, pageSize);
				break;
			default:
				break;
		}
	}

	private void searchByCondication() {
		isSearchStatus = true;
		// 獲取搜索的學生列表
		pageIndex = 0;
		if (!HttpHelper.isNetworkConnected(getActivity())) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		showDialogRes(R.string.loading);
		getSearchStudent(pageIndex, pageSize);
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
						String courseName = condition.getCourseName();
						if (!TextUtils.isEmpty(courseName)) {
							courseName += COM;
						} else {
							courseName = SPACE;
						}
						String areaName = condition.getAreaName();
						if (!TextUtils.isEmpty(areaName)) {
							areaName += COM;
						} else {
							areaName = SPACE;
						}
						String genderName = condition.getGenderName();
						if (!TextUtils.isEmpty(genderName)) {
							genderName += COM;
						} else {
							genderName = SPACE;
						}
						Timeslot time = condition.getTimeslot();
						String timeStr = SPACE;
						if (time != null) {
							int week = time.getDayOfWeek();
							String weekStr = "";
							String[] weeks = getResources().getStringArray(R.array.weeks);
							switch (week) {
								case 0:
									weekStr = weeks[0];
									break;
								case 1:
									weekStr = weeks[1];
									break;
								case 2:
									weekStr = weeks[2];
									break;
								case 3:
									weekStr = weeks[3];
									break;
								case 4:
									weekStr = weeks[4];
									break;
								case 5:
									weekStr = weeks[5];
									break;
								case 6:
									weekStr = weeks[6];
									break;
							}
							int endHour = time.getEndHour();
							int endMinute = time.getEndMinute();
							String endM = "";
							if (endMinute < 10) {
								endM = "0" + endMinute;
							} else {
								endM = String.valueOf(endMinute);
							}
							int startHour = time.getStartHour();
							int startMinute = time.getStartMinute();
							String startM = "";
							if (startMinute < 10) {
								startM = "0" + startMinute;
							} else {
								startM = String.valueOf(startMinute);
							}
							String COLON = ":";
							timeStr = weekStr + " " + startHour + COLON + startM + " - " + endHour + COLON + endM + COM;
						}
						String searchKeyWork = keyword + courseName + areaName + genderName + timeStr;
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
					searchByCondication();
				}
				break;
			default:
				break;
		}
	}

	/**
	 * 獲取匹配學生列表
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
		HttpHelper.getHelper().get(ApiUrl.STUDENTMATCH, TutorApplication.getHeaders(), params, new ObjectHttpResponseHandler<UserListResult>(UserListResult.class) {

			@Override
			public void onFailure(int status, String message) {
				if (0 == status) {
					getStudentList(pageIndex, pageSize);
					return;
				}
				CheckTokenUtils.checkToken(status);
				setData(null);
			}

			@Override
			public void onSuccess(UserListResult result) {
				ArrayList<UserInfo> info = result.getResult();
				if (info != null && info.size() > 0) {
					CheckTokenUtils.checkToken(result);
					setData(result);
				} else {
					getSearchStudent(pageIndex, pageSize);
				}
			}
		});
	}

	/**
	 * 獲取搜索學生列表
	 * 
	 */
	private void getSearchStudent(final int pageIndex, final int pageSize) {
		if (!HttpHelper.isNetworkConnected(getActivity())) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		StringBuffer sb = new StringBuffer();
		sb.append(ApiUrl.SEARCHSTUDENT);
		sb.append("?pageIndex=");
		sb.append(pageIndex);
		sb.append("&pageSize=");
		sb.append(pageSize);
		String body;
		if (condition != null) {
			body = JsonUtil.parseObject2Str(condition);
		} else {
			body = JsonUtil.parseObject2Str(new Object());
		}
		try {
			StringEntity entity = new StringEntity(body, HTTP.UTF_8);
			HttpHelper.getHelper().post(sb.toString(), TutorApplication.getHeaders(), entity, new ObjectHttpResponseHandler<UserListResult>(UserListResult.class) {

				@Override
				public void onFailure(int status, String message) {
					if (0 == status) {
						getSearchStudent(pageIndex, pageSize);
						return;
					}
					setData(null);
					CheckTokenUtils.checkToken(status);
					listView.setBackgroundColor(getResources().getColor(R.color.default_bg_color));
				}

				@Override
				public void onSuccess(UserListResult t) {
					CheckTokenUtils.checkToken(t);
					setData(t);
				}
			});
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			setData(null);
		}
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
							toast(R.string.toast_no_match_student);
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
