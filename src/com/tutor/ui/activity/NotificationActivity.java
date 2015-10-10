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
import com.tutor.adapter.NotificationAdapter;
import com.tutor.adapter.NotificationAdapter.OnDeleteItemClickListener;
import com.tutor.im.IMMessageManager;
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
 * 消息
 * 
 * @author bruce.chen
 * 
 *         2015-8-26
 */
public class NotificationActivity extends BaseActivity {

	private ListView lvNotification;
	private NotificationAdapter mAdapter;
	private List<IMMessage> messages = new ArrayList<IMMessage>();

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_notification);
		initView();
	}

	@Override
	protected void initView() {
		TitleBar bar = getView(R.id.title_bar);
		bar.initBack(this);
		bar.setTitle(R.string.notification);
		lvNotification = getView(R.id.lv_notifications);
		mAdapter = new NotificationAdapter(NotificationActivity.this, getMessages());
		// 左滑删除
		mAdapter.setOnDeleteItemClickListener(new OnDeleteItemClickListener() {

			@Override
			public void onClick(String fromSubJid, int position) {
				deleteMessages(fromSubJid);
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
					Intent intent = new Intent(NotificationActivity.this, SystemNotificationActivity.class);
					startActivity(intent);
				} else {
					String imId = msg.getFromSubJid();
					if (!TextUtils.isEmpty(imId)) {
						Intent intent = new Intent(NotificationActivity.this, ChatActivity.class);
						intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_MESSAGE_TO, imId);
						intent.putExtra(Constants.General.NICKNAME, msg.getFromSubJid());
						startActivity(intent);
					}
				}
			}
		});
	}

	/**
	 * 获取消息数量 Sent 0 Accept 1 Reject 2 Acknowle 3 All 4
	 */
	private void getNotificationMsg() {
		if (!HttpHelper.isNetworkConnected(this)) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		RequestParams params = new RequestParams();
		params.put("pageIndex", "0");
		params.put("pageSize", "1");
		HttpHelper.get(this, ApiUrl.NOTIFICATIONLIST, TutorApplication.getHeaders(), params, new ObjectHttpResponseHandler<NotificationListResult>(NotificationListResult.class) {

			@Override
			public void onFailure(int status, String message) {
				if (0 == status) {
					getNotificationMsg();
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
						Notification notify = notifications.get(0);
						if (notify != null) {
							IMMessage msg = new IMMessage();
							msg.setId(notify.getId());
							msg.setContent(notify.getContent());
							msg.setAvatar(notify.getAvatar());
							msg.setTime(notify.getCreatedTime());
							msg.setMsgType(IMMessage.MESSAGE_TYPE_SYS_MSG);
							String nickName = notify.getNickName();
							if (!TextUtils.isEmpty(nickName)) {
								msg.setFromSubJid(nickName);
							} else {
								msg.setFromSubJid(notify.getUserName());
							}
							messages.add(0, msg);
							mAdapter.notifyDataSetChanged();
						}
					}
				}
			}
		});
	}

	/**
	 * 获取聊天记录
	 * 
	 * @return
	 */
	private List<IMMessage> getMessages() {
		if (messages != null && messages.size() > 0) {
			messages.clear();
		}
		messages = IMMessageManager.getManager().getRecentContactsWithLastMsg();
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
		getNotificationMsg();
		return messages;
	}

	/**
	 * 删除聊天记录
	 * 
	 * @return
	 */
	private void deleteMessages(String fromSubJid) {
		boolean isDelete = IMMessageManager.getManager().deleteMsgWhereJid(fromSubJid);
		if (isDelete) {
			getMessages();
		}
	}
}
