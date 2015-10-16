package com.tutor.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.tutor.R;
import com.tutor.adapter.NotificationAdapter;
import com.tutor.adapter.NotificationAdapter.OnDeleteItemClickListener;
import com.tutor.im.IMMessageManager;
import com.tutor.model.IMMessage;
import com.tutor.params.Constants;
import com.tutor.ui.view.TitleBar;

/**
 * 消息
 * 
 * @author bruce.chen
 * 
 *         2015-8-26
 */
public class ChatListActivity extends BaseActivity {

	private ListView lvChatList;
	private NotificationAdapter mAdapter;
	private List<IMMessage> messages = new ArrayList<IMMessage>();

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_notification);
		initView();
	}

	/**
	 * 接收到聊天消息
	 */
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Constants.Action.ACTION_NEW_MESSAGE.equals(action)) {
				getMessages();
				mAdapter.refresh(messages);
			}
		}
	};

	@Override
	protected void initView() {
		TitleBar bar = getView(R.id.title_bar);
		bar.initBack(this);
		bar.setTitle(R.string.chatlist);
		lvChatList = getView(R.id.lv_notifications);
		mAdapter = new NotificationAdapter(ChatListActivity.this, messages);
		// 左滑删除
		mAdapter.setOnDeleteItemClickListener(new OnDeleteItemClickListener() {

			@Override
			public void onClick(String fromSubJid, int position) {
				deleteMessages(fromSubJid);
			}
		});
		lvChatList.setAdapter(mAdapter);
		lvChatList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				IMMessage msg = mAdapter.getItem(position);
				if (msg == null) {
					return;
				}
				String imId = msg.getFromSubJid();
				if (!TextUtils.isEmpty(imId)) {
					Intent intent = new Intent(ChatListActivity.this, ChatActivity.class);
					intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_MESSAGE_TO, imId);
					intent.putExtra(Constants.General.NICKNAME, msg.getFromUserName());
					intent.putExtra(Constants.General.AVATAR, msg.getAvatar());
					startActivity(intent);
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		getMessages();
		mAdapter.refresh(messages);
		IntentFilter filter = new IntentFilter(Constants.Action.ACTION_NEW_MESSAGE);
		registerReceiver(receiver, filter);
		Constants.Xmpp.isChatNotification = false;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceiver(receiver);
		Constants.Xmpp.isChatNotification = true;
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
