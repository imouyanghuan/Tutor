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
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.loopj.android.http.RequestParams;
import com.mssky.mobile.ui.view.PullToRefreshBase;
import com.mssky.mobile.ui.view.PullToRefreshBase.Mode;
import com.mssky.mobile.ui.view.PullToRefreshBase.OnRefreshListener2;
import com.mssky.mobile.ui.view.PullToRefreshListView;
import com.tutor.R;
import com.tutor.TutorApplication;
import com.tutor.adapter.TeacherListAdapter;
import com.tutor.model.Page;
import com.tutor.model.SearchCondition;
import com.tutor.model.Timeslot;
import com.tutor.model.UserInfo;
import com.tutor.model.UserListResult;
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
 * 學生首頁,找老師
 * 
 * @author bruce.chen
 * 
 *         2015-8-20
 */
public class FindTeacherFragment extends BaseFragment implements OnRefreshListener2<ListView>, OnClickListener {

	private PullToRefreshListView listView;
	private EditText editText;
	private ImageButton ibDelete;
	private RadioGroup group;
	// 數據
	private UserListResult listResult;
	// 列表
	private ArrayList<UserInfo> users = new ArrayList<UserInfo>();
	// adapter
	private TeacherListAdapter adapter;
	// 頁碼,每頁大小
	private int pageIndex = 0, pageSize = 20;
	// 是否是搜索狀態
	private boolean isSearchStatus = false;
	private int type;
	private SearchCondition condition = null;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_student_findteacher, container, false);
		initView(inflater, view);
		return view;
	}

	@SuppressLint("InflateParams")
	private void initView(LayoutInflater inflater, View view) {
		View serchView = inflater.inflate(R.layout.find_teacher_header_layout, null);
		//
		editText = ViewHelper.get(serchView, R.id.fragment_find_student_et);
		editText.setFocusable(false);
		editText.setOnClickListener(this);
		//
		ibDelete = ViewHelper.get(serchView, R.id.ib_delete);
		ibDelete.setOnClickListener(this);
		ViewHelper.get(serchView, R.id.fragment_find_student_btn).setOnClickListener(this);
		//
		group = ViewHelper.get(serchView, R.id.fragment_find_teacher_rg);
		group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
					case R.id.fragment_find_teacher_rb1:
						// 根据评分
						pageIndex = 0;
						type = 0;
						getTeachersByRating(pageIndex, pageSize);
						break;
					case R.id.fragment_find_teacher_rb2:
						// 根据学生数量
						pageIndex = 0;
						type = 1;
						getTeachersByPopularity(pageIndex, pageSize);
						break;
				}
			}
		});
		listView = ViewHelper.get(view, R.id.fragment_find_teacher_lv);
		listView.setBackgroundColor(getResources().getColor(R.color.default_bg_color));
		listView.setShowIndicator(false);
		listView.getRefreshableView().addHeaderView(serchView);
		// 設置可上拉加載和下拉刷新
		listView.setMode(Mode.BOTH);
		listView.setOnRefreshListener(this);
		adapter = new TeacherListAdapter(getActivity(), users);
		listView.setAdapter(adapter);
		// 按评分加载老师列表
		showDialogRes(R.string.loading);
		getTeachersByRating(pageIndex, pageSize);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.fragment_find_student_et:
				Intent intent = new Intent(getActivity(), SearchConditionsActivity.class);
				intent.putExtra(Constants.General.IS_FROM_TEACHER, false);
				startActivityForResult(intent, Constants.RequestResultCode.SEARCH_CONDITIONS);
				break;
			case R.id.ib_delete:
				editText.setText("");
				condition = null;
				editText.setHint(R.string.hint_search_student_keywords);
				ibDelete.setVisibility(View.GONE);
				group.check(R.id.fragment_find_teacher_rb1);
				if (type == 0) {
					getTeachersByRating(pageIndex, pageSize);
				}
				break;
			case R.id.fragment_find_student_btn:
				isSearchStatus = true;
				// 獲取搜索的老师列表
				pageIndex = 0;
				if (!HttpHelper.isNetworkConnected(getActivity())) {
					toast(R.string.toast_netwrok_disconnected);
					return;
				}
				showDialogRes(R.string.loading);
				getSearchTeachers(pageIndex, pageSize);
				break;
			default:
				break;
		}
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
							int startHour = time.getStartHour();
							int startMinute = time.getStartMinute();
							timeStr = weekStr + " " + startHour + getString(R.string.hour) + startMinute + getString(R.string.minute) + " - " + endHour + getString(R.string.hour) + endMinute
									+ getString(R.string.minute) + COM;
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
				}
				break;
			default:
				break;
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
		// Update the LastUpdatedLabel
		refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
		pageIndex = 0;
		if (isSearchStatus) {
			getSearchTeachers(pageIndex, pageSize);
		} else {
			if (type == 0) {
				getTeachersByRating(pageIndex, pageSize);
			} else {
				getTeachersByPopularity(pageIndex, pageSize);
			}
		}
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		if (null != listResult) {
			Page page = listResult.getPage();
			if (null != page && page.isHasNextPage()) {
				pageIndex += 1;
				if (isSearchStatus) {
					getSearchTeachers(pageIndex, pageSize);
				} else {
					if (type == 0) {
						getTeachersByRating(pageIndex, pageSize);
					} else {
						getTeachersByPopularity(pageIndex, pageSize);
					}
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

	private void getTeachersByRating(final int index, final int size) {
		if (!HttpHelper.isNetworkConnected(getActivity())) {
			toast(R.string.toast_netwrok_disconnected);
			setData(null);
			return;
		}
		RequestParams params = new RequestParams();
		params.put("pageIndex", pageIndex);
		params.put("pageSize", pageSize);
		HttpHelper.get(getActivity(), ApiUrl.TUTORRATING, TutorApplication.getHeaders(), params, new ObjectHttpResponseHandler<UserListResult>(UserListResult.class) {

			@Override
			public void onFailure(int status, String message) {
				if (0 == status) {
					getTeachersByRating(pageIndex, pageSize);
					return;
				}
				CheckTokenUtils.checkToken(status);
				setData(null);
			}

			@Override
			public void onSuccess(UserListResult result) {
				CheckTokenUtils.checkToken(result);
				setData(result);
			}
		});
	}

	private void getTeachersByPopularity(final int index, final int size) {
		if (!HttpHelper.isNetworkConnected(getActivity())) {
			toast(R.string.toast_netwrok_disconnected);
			setData(null);
			return;
		}
		RequestParams params = new RequestParams();
		params.put("pageIndex", pageIndex);
		params.put("pageSize", pageSize);
		HttpHelper.get(getActivity(), ApiUrl.TUTORPOPULARITY, TutorApplication.getHeaders(), params, new ObjectHttpResponseHandler<UserListResult>(UserListResult.class) {

			@Override
			public void onFailure(int status, String message) {
				if (0 == status) {
					getTeachersByPopularity(pageIndex, pageSize);
					return;
				}
				CheckTokenUtils.checkToken(status);
				setData(null);
			}

			@Override
			public void onSuccess(UserListResult result) {
				CheckTokenUtils.checkToken(result);
				setData(result);
			}
		});
	}

	private void getSearchTeachers(final int index, final int size) {
		if (!HttpHelper.isNetworkConnected(getActivity())) {
			toast(R.string.toast_netwrok_disconnected);
			setData(null);
			return;
		}
		StringBuffer sb = new StringBuffer();
		sb.append(ApiUrl.SEARCHTUTOR);
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
			HttpHelper.post(getActivity(), sb.toString(), TutorApplication.getHeaders(), entity, new ObjectHttpResponseHandler<UserListResult>(UserListResult.class) {

				@Override
				public void onFailure(int status, String message) {
					if (0 == status) {
						getSearchTeachers(pageIndex, pageSize);
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
