package com.tutor.ui.activity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.hk.tutor.R;
import com.loopj.android.http.RequestParams;
import com.mssky.mobile.helper.SharePrefUtil;
import com.tutor.TutorApplication;
import com.tutor.adapter.SysNotificationAdapter;
import com.tutor.adapter.SysNotificationAdapter.OnRejectItemClickListener;
import com.tutor.model.EditProfileResult;
import com.tutor.model.Notification;
import com.tutor.model.NotificationListResult;
import com.tutor.model.UpdateNotification;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.CheckTokenUtils;
import com.tutor.util.HttpHelper;
import com.tutor.util.JsonUtil;
import com.tutor.util.ObjectHttpResponseHandler;

/**
 * 系统消息
 * 
 * @author jerry.yao
 * 
 *         2015-10-10
 */
public class SystemNotificationActivity extends BaseActivity implements OnClickListener {

	private ListView lvNotification;
	private SysNotificationAdapter mAdapter;
	private List<Notification> messages = new ArrayList<Notification>();
	private FrameLayout flTipNotification;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_notification);
		initView();
		getSystemNotification();
	}

	@Override
	protected void initView() {
		flTipNotification = getView(R.id.fl_tip_notification);
		flTipNotification.setOnClickListener(this);
		TitleBar bar = getView(R.id.title_bar);
		bar.initBack(this);
		bar.setTitle(R.string.sys_notification);
		lvNotification = getView(R.id.lv_notifications);
		mAdapter = new SysNotificationAdapter(SystemNotificationActivity.this, messages);
		// 接受
//		mAdapter.setOnAcceptItemClickListener(new OnAcceptItemClickListener() {
//
//			@Override
//			public void onClick(int status, int notificationId) {
//				acceptOrReject(status, notificationId);
//			}
//		});
		// 拒绝
		mAdapter.setOnRejectItemClickListener(new OnRejectItemClickListener() {

			@Override
			public void onClick(int status, int notificationId) {
				acceptOrReject(status, notificationId);
			}
		});
		lvNotification.setAdapter(mAdapter);
		lvNotification.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (mAdapter != null) {
					Intent intent = new Intent(SystemNotificationActivity.this, InvitationDetailActivity.class);
					intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_NOTIFICATION, mAdapter.getItem(position));
					startActivityForResult(intent, Constants.RequestResultCode.NOTIFICATION_STATUS);
				}
			}
		});
	}

	/**
	 * 获取消息数量 Sent 0 Accept 1 Reject 2 Acknowle 3 All 4
	 */
	private void getSystemNotification() {
		if (!HttpHelper.isNetworkConnected(this)) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		showDialogRes(R.string.loading);
		RequestParams params = new RequestParams();
		params.put("pageIndex", "0");
		params.put("pageSize", "30");
		HttpHelper.get(this, ApiUrl.NOTIFICATIONLIST, TutorApplication.getHeaders(), params, new ObjectHttpResponseHandler<NotificationListResult>(NotificationListResult.class) {

			@Override
			public void onFailure(int status, String message) {
				if (0 == status) {
					getSystemNotification();
					return;
				}
				CheckTokenUtils.checkToken(status);
				dismissDialog();
			}

			@Override
			public void onSuccess(NotificationListResult result) {
				CheckTokenUtils.checkToken(result);
				dismissDialog();
				if (null != result && 200 == result.getStatusCode()) {
					//
					ArrayList<Notification> notifications = result.getResult();
					if (notifications != null && notifications.size() > 0) {
						messages.addAll(notifications);
						mAdapter.notifyDataSetChanged();
						// 当切换为这个tab的时候才显示tip
						boolean isNeedShow = SharePrefUtil.getBoolean(getApplicationContext(), Constants.General.IS_NEED_SHOW_NOTIFICATION_TIP, true);
						if (isNeedShow) {
							flTipNotification.setVisibility(View.VISIBLE);
						} else {
							flTipNotification.setVisibility(View.GONE);
						}
					}
				}
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
	public void acceptOrReject(final int curStatus, final int notificationId) {
		UpdateNotification notify = new UpdateNotification();
		notify.setId(notificationId);
		notify.setStatus(curStatus);
		notify.setHkidNumber("");
		notify.setPhone("");
		notify.setResidentialAddress("");
		String body = JsonUtil.parseObject2Str(notify);
		StringEntity entity = null;
		try {
			entity = new StringEntity(body, HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		HttpHelper.put(this, ApiUrl.NOTIFICATION_UPDATE, TutorApplication.getHeaders(), entity, new ObjectHttpResponseHandler<EditProfileResult>(EditProfileResult.class) {

			@Override
			public void onFailure(int status, String message) {
				if (0 == status) {
					acceptOrReject(curStatus, notificationId);
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
								messages.get(i).setStatus(curStatus);
								mAdapter.notifyDataSetChanged();
								break;
							}
						}
					}
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.fl_tip_notification:
				flTipNotification.setVisibility(View.GONE);
				SharePrefUtil.saveBoolean(getApplicationContext(), Constants.General.IS_NEED_SHOW_NOTIFICATION_TIP, false);
				break;
			default:
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
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
