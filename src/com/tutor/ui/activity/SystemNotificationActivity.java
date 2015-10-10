package com.tutor.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.loopj.android.http.RequestParams;
import com.tutor.R;
import com.tutor.TutorApplication;
import com.tutor.adapter.SysNotificationAdapter.OnAcceptItemClickListener;
import com.tutor.adapter.SysNotificationAdapter.OnRejectItemClickListener;
import com.tutor.adapter.SysNotificationAdapter;
import com.tutor.model.EditProfileResult;
import com.tutor.model.IMMessage;
import com.tutor.model.Notification;
import com.tutor.model.NotificationListResult;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.CheckTokenUtils;
import com.tutor.util.HttpHelper;
import com.tutor.util.ObjectHttpResponseHandler;

/**
 * 系统消息
 * 
 * @author jerry.yao
 * 
 *         2015-10-10
 */
public class SystemNotificationActivity extends BaseActivity {

	private ListView lvNotification;
	private SysNotificationAdapter mAdapter;
	private List<IMMessage> messages = new ArrayList<IMMessage>();

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_notification);
		initView();
		getSystemNotification();
	}

	@Override
	protected void initView() {
		TitleBar bar = getView(R.id.title_bar);
		bar.initBack(this);
		bar.setTitle(R.string.sys_notification);
		lvNotification = getView(R.id.lv_notifications);
		mAdapter = new SysNotificationAdapter(SystemNotificationActivity.this, messages);
		// 接受
		mAdapter.setOnAcceptItemClickListener(new OnAcceptItemClickListener() {

			@Override
			public void onClick(int status, String notificationId) {
				acceptOrReject(status, notificationId);
			}
		});
		// 拒绝
		mAdapter.setOnRejectItemClickListener(new OnRejectItemClickListener() {

			@Override
			public void onClick(int status, String notificationId) {
				acceptOrReject(status, notificationId);
			}
		});
		lvNotification.setAdapter(mAdapter);
		lvNotification.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				IMMessage msg = mAdapter.getItem(position);
				if (msg == null) {
					return;
				}
				int msgType = msg.getMsgType();
				if (msgType == IMMessage.MESSAGE_TYPE_SYS_MSG) {
					// 系统消息
				} else if (msgType == IMMessage.MESSAGE_TYPE_CHAT_MSG) {
					String imId = msg.getFromSubJid();
					if (!TextUtils.isEmpty(imId)) {
						Intent intent = new Intent(SystemNotificationActivity.this, ChatActivity.class);
						intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_MESSAGE_TO, imId);
						startActivity(intent);
					}
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
		RequestParams params = new RequestParams();
		params.put("pageIndex", "0");
		params.put("pageSize", "20");
		HttpHelper.get(this, ApiUrl.NOTIFICATIONLIST, TutorApplication.getHeaders(), params, new ObjectHttpResponseHandler<NotificationListResult>(NotificationListResult.class) {

			@Override
			public void onFailure(int status, String message) {
				if (0 == status) {
					getSystemNotification();
					return;
				}
				CheckTokenUtils.checkToken(status);
			}

			@Override
			public void onSuccess(NotificationListResult result) {
				CheckTokenUtils.checkToken(result);
				if (null != result && 200 == result.getStatusCode()) {
					//
					ArrayList<Notification> notifications = result.getResult();
					if (notifications != null && notifications.size() > 0) {
						for (int i = 0; i < notifications.size(); i++) {
							Notification notify = notifications.get(i);
							if (notify != null) {
								IMMessage msg = new IMMessage();
								msg.setId(notify.getId());
								msg.setReadStatus(notify.getStatus());
								msg.setAvatar(notify.getAvatar());
								msg.setContent(notify.getContent());
								msg.setTime(notify.getCreatedTime());
								msg.setMsgType(IMMessage.MESSAGE_TYPE_SYS_MSG);
								String nickName = notify.getNickName();
								if (!TextUtils.isEmpty(nickName)) {
									msg.setFromSubJid(nickName);
								} else {
									msg.setFromSubJid(notify.getUserName());
								}
								messages.add(msg);
							}
						}
						mAdapter.notifyDataSetChanged();
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
	public void acceptOrReject(final int curStatus, final String notificationId) {
		RequestParams params = new RequestParams();
		params.put("notificationId", notificationId);
		params.put("status", curStatus);
		HttpHelper.get(this, ApiUrl.NOTIFICATION_UPDATE, TutorApplication.getHeaders(), params, new ObjectHttpResponseHandler<EditProfileResult>(EditProfileResult.class) {

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
				if (curStatus == Constants.General.ACCEPT) {
					toast("已经接受邀请");
				} else {
					toast("已经拒绝邀请");
				}
				
				if(messages != null && messages.size() > 0){
					for(int i = 0; i< messages.size(); i++){
						String notifyId = messages.get(i).getId();
						if(notifyId.equals(notificationId)){
							messages.get(i).setReadStatus(curStatus);
							mAdapter.notifyDataSetChanged();
							break;
						}
					}
				}
			}
		});
	}
}
