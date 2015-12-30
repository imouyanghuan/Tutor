package com.tutor.ui.fragment.tuitioncenter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.hk.tutor.R;
import com.loopj.android.http.RequestParams;
import com.mssky.mobile.ui.view.PullToRefreshBase;
import com.mssky.mobile.ui.view.PullToRefreshBase.Mode;
import com.mssky.mobile.ui.view.PullToRefreshBase.OnRefreshListener2;
import com.mssky.mobile.ui.view.PullToRefreshListView;
import com.tutor.TutorApplication;
import com.tutor.adapter.TuitionCenterNotificationAdapter;
import com.tutor.adapter.TuitionCenterNotificationAdapter.OnAcceptItemClickListener;
import com.tutor.adapter.TuitionCenterNotificationAdapter.OnRejectItemClickListener;
import com.tutor.model.EditProfileResult;
import com.tutor.model.Notification;
import com.tutor.model.NotificationListResult;
import com.tutor.model.Page;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.fragment.BaseFragment;
import com.tutor.util.CheckTokenUtils;
import com.tutor.util.HttpHelper;
import com.tutor.util.ObjectHttpResponseHandler;
import com.tutor.util.ViewHelper;

/**
 * 补习社通知页面
 * 
 * @author jerry.yao
 * 
 *         2015-12-10
 */
public class TuitionCenterNotificationFragment extends BaseFragment implements OnClickListener {

	private PullToRefreshListView lvNotification;
	private TuitionCenterNotificationAdapter mAdapter;
	private List<Notification> messages = new ArrayList<Notification>();
	private int pageIndex = 0;
	private int pageSize = 20;
	// 數據
	private NotificationListResult listResult;

	// private FrameLayout flTipNotification;
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_tuition_center_notification, container, false);
		initView(inflater, view);
		return view;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SuppressLint("InflateParams")
	private void initView(LayoutInflater inflater, View view) {
		// flTipNotification = ViewHelper.get(view, R.id.fl_tip_notification);
		// flTipNotification.setOnClickListener(this);
		lvNotification = ViewHelper.get(view, R.id.lv_notifications);
		lvNotification.setShowIndicator(false);
		lvNotification.setMode(Mode.BOTH);
		mAdapter = new TuitionCenterNotificationAdapter(getActivity(), messages);
		// 接受
		mAdapter.setOnAcceptItemClickListener(new OnAcceptItemClickListener() {

			@Override
			public void onClick(int status, int notificationId) {
				acceptOrRejectForTuitionCenter(notificationId, Constants.General.ACCEPT);
			}
		});
		// 拒绝
		mAdapter.setOnRejectItemClickListener(new OnRejectItemClickListener() {

			@Override
			public void onClick(int status, int notificationId) {
				acceptOrRejectForTuitionCenter(notificationId, Constants.General.REJECT);
			}
		});
		lvNotification.setAdapter(mAdapter);
		// lvNotification.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view, int
		// position, long id) {
		// if (mAdapter != null) {
		// Intent intent = new Intent(getActivity(),
		// TuitionCenterDetailActivity.class);
		// intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_NOTIFICATION,
		// mAdapter.getItem(position - 1));
		// startActivityForResult(intent,
		// Constants.RequestResultCode.NOTIFICATION_STATUS);
		// }
		// }
		// });
		lvNotification.setOnRefreshListener(new OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				pageIndex = 0;
				getTuitionCenterNotification();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				if (null != listResult) {
					Page page = listResult.getPage();
					if (null != page && page.isHasNextPage()) {
						pageIndex++;
						getTuitionCenterNotification();
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
								lvNotification.onRefreshComplete();
							}
						});
						toast(R.string.toast_no_more_data);
					}
				} else {
					new Handler().post(new Runnable() {

						@Override
						public void run() {
							lvNotification.onRefreshComplete();
						}
					});
					toast(R.string.toast_no_data);
				}
			}
		});
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		getTuitionCenterNotification();
	}

	/**
	 * 获取消息数量 Sent 0 Accept 1 Reject 2 Acknowle 3 All 4
	 */
	private void getTuitionCenterNotification() {
		if (!HttpHelper.isNetworkConnected(getActivity())) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		showDialogRes(R.string.loading);
		RequestParams params = new RequestParams();
		params.put("pageIndex", pageIndex);
		params.put("pageSize", pageSize);
		HttpHelper.getHelper().get(ApiUrl.NOTIFICATIONLIST, TutorApplication.getHeaders(), params, new ObjectHttpResponseHandler<NotificationListResult>(NotificationListResult.class) {

			@Override
			public void onFailure(int status, String message) {
				if (0 == status) {
					getTuitionCenterNotification();
					return;
				}
				CheckTokenUtils.checkToken(status);
				dismissDialog();
				lvNotification.onRefreshComplete();
			}

			@Override
			public void onSuccess(NotificationListResult result) {
				CheckTokenUtils.checkToken(result);
				dismissDialog();
				if (null != result && 200 == result.getStatusCode()) {
					listResult = result;
					ArrayList<Notification> notifications = result.getResult();
					if (notifications != null && notifications.size() > 0) {
						if (pageIndex == 0) {
							messages.clear();
							messages.addAll(notifications);
						} else {
							messages.addAll(notifications);
						}
						mAdapter.notifyDataSetChanged();
					}
				}
				lvNotification.onRefreshComplete();
			}
		});
	}

	/**
	 * 接受或者拒绝邀请
	 * 
	 * @param status
	 *            处理状态： 1接受 2拒绝
	 * @param notificationId
	 *            消息id
	 */
	public void acceptOrRejectForTuitionCenter(final int notificationId, final int status) {
		String url = String.format(ApiUrl.TUITION_CENTER_NOTIFICATION_UPDATE, notificationId, status);
		HttpHelper.getHelper().get( url, TutorApplication.getHeaders(), new RequestParams(), new ObjectHttpResponseHandler<EditProfileResult>(EditProfileResult.class) {

			@Override
			public void onFailure(int status, String message) {
				if (0 == status) {
					acceptOrRejectForTuitionCenter(notificationId, status);
					return;
				}
				CheckTokenUtils.checkToken(status);
			}

			@Override
			public void onSuccess(EditProfileResult result) {
				CheckTokenUtils.checkToken(result);
				if (result.getStatusCode() == 200) {
					if (messages != null && messages.size() > 0) {
						for (int i = 0; i < messages.size(); i++) {
							int notifyId = messages.get(i).getId();
							if (notifyId == notificationId) {
								messages.get(i).setStatus(status);
								mAdapter.notifyDataSetChanged();
								break;
							}
						}
					}
				}
			}
		});
	}

	// /**
	// * 接受或者拒绝邀请
	// *
	// * @param status
	// * 处理状态： 1接受 2拒绝
	// * @param notificationId
	// * 消息id
	// */
	// public void acceptOrReject(final int curStatus, final int notificationId)
	// {
	// UpdateNotification notify = new UpdateNotification();
	// notify.setId(notificationId);
	// notify.setStatus(curStatus);
	// notify.setHkidNumber("");
	// notify.setPhone("");
	// notify.setResidentialAddress("");
	// String body = JsonUtil.parseObject2Str(notify);
	// StringEntity entity = null;
	// try {
	// entity = new StringEntity(body, HTTP.UTF_8);
	// } catch (UnsupportedEncodingException e) {
	// e.printStackTrace();
	// }
	// HttpHelper.put(getActivity(), ApiUrl.NOTIFICATION_UPDATE,
	// TutorApplication.getHeaders(), entity, new
	// ObjectHttpResponseHandler<EditProfileResult>(EditProfileResult.class) {
	//
	// @Override
	// public void onFailure(int status, String message) {
	// if (0 == status) {
	// acceptOrReject(curStatus, notificationId);
	// return;
	// }
	// CheckTokenUtils.checkToken(status);
	// }
	//
	// @Override
	// public void onSuccess(EditProfileResult result) {
	// CheckTokenUtils.checkToken(result);
	// if (result.getStatusCode() == 200) {
	// if (messages != null && messages.size() > 0) {
	// for (int i = 0; i < messages.size(); i++) {
	// int notifyId = messages.get(i).getId();
	// if (notifyId == notificationId) {
	// messages.get(i).setStatus(curStatus);
	// mAdapter.notifyDataSetChanged();
	// break;
	// }
	// }
	// }
	// }
	// }
	// });
	// }
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.fl_tip_notification:
				// flTipNotification.setVisibility(View.GONE);
				// SharePrefUtil.saveBoolean(getActivity(),
				// Constants.General.IS_NEED_SHOW_NOTIFICATION_TIP, false);
				break;
			default:
				break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == Constants.RequestResultCode.NOTIFICATION_STATUS) {
			if (intent == null) {
				return;
			}
			int notificationId = intent.getIntExtra(Constants.IntentExtra.INTENT_EXTRA_NOTIFICATION_ID, -1);
			int curStatus = intent.getIntExtra(Constants.IntentExtra.INTENT_EXTRA_NOTIFICATION_STATUS, -1);
			if (messages != null && messages.size() > 0) {
				for (int i = 0; i < messages.size(); i++) {
					int notifyId = messages.get(i).getId();
					if (notifyId == notificationId) {
						messages.get(i).setStatus(curStatus);
						mAdapter.refresh(messages);
						break;
					}
				}
			}
		}
	}
}
