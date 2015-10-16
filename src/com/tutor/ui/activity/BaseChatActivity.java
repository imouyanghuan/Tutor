package com.tutor.ui.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.tutor.TutorApplication;
import com.tutor.im.IMMessageManager;
import com.tutor.im.XMPPConnectionManager;
import com.tutor.model.IMMessage;
import com.tutor.params.Constants;
import com.tutor.util.DateTimeUtil;
import com.tutor.util.UUIDUtils;

/**
 * 聊天activity父类
 * 
 * @author bruce.chen
 * 
 */
public abstract class BaseChatActivity extends BaseActivity {

	private Chat chat;
	protected String chatWithJid;
	/** 聊天消息 */
	private List<IMMessage> imMessages;
	private String title;
	// 对方的头像
	private String toAvatar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		if (intent == null) {
			return;
		}
		chatWithJid = intent.getStringExtra(Constants.IntentExtra.INTENT_EXTRA_MESSAGE_TO);
		if (chatWithJid == null) {
			return;
		}
		chat = TutorApplication.connectionManager.getConnection().getChatManager().createChat(chatWithJid, null);
		title = intent.getStringExtra(Constants.General.NICKNAME);
		toAvatar = intent.getStringExtra(Constants.General.AVATAR);
		// 第一次加载数据
		imMessages = IMMessageManager.getManager().searchChatMessages(chatWithJid, 0, Constants.Xmpp.CHAT_PAGESIZE);
		if (null != imMessages && imMessages.size() > 0) {
			Collections.sort(imMessages);
		} else {
			imMessages = new ArrayList<IMMessage>();
		}
		// 更新好友的所有消息读取状态
		IMMessageManager.getManager().updateStatusByFrom(chatWithJid, IMMessage.READ_STATUS_READ);
	}

	@Override
	protected void onResume() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.Action.ACTION_NEW_MESSAGE);
		registerReceiver(receiver, filter);
		Constants.Xmpp.isChatNotification = false;
		super.onResume();
	}

	@Override
	protected void onPause() {
		unregisterReceiver(receiver);
		Constants.Xmpp.isChatNotification = true;
		super.onPause();
	}

	/**
	 * 接收到聊天消息
	 */
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Constants.Action.ACTION_NEW_MESSAGE.equals(action)) {
				IMMessage message = (IMMessage) intent.getSerializableExtra(Constants.IntentExtra.INTENT_EXTRA_IMMESSAGE_KEY);
				if (null != message && message.getFromSubJid().equals(chatWithJid) && IMMessage.MESSAGE_TYPE_CHAT_MSG == message.getNoticeType()) {
					imMessages.add(message);
					receiveNewMessage(message);
					refreshMessage(imMessages);
					// 更新为已读状态
					IMMessageManager.getManager().updateStatus(message.getId(), IMMessage.READ_STATUS_READ);
				}
			}
		}
	};

	protected abstract void receiveNewMessage(IMMessage message);

	protected abstract void refreshMessage(List<IMMessage> messages);

	protected List<IMMessage> getMessages() {
		return imMessages;
	}

	protected void resh() {
		// 刷新视图
		refreshMessage(imMessages);
	}

	/**
	 * 发送消息
	 * 
	 * @param msg
	 * @throws XMPPException
	 */
	protected void sendMessage(String msg) {
		Message message = new Message();
		message.setBody(msg);
		String id = UUIDUtils.getID(6);
		message.setPacketID(id);
		String time = DateTimeUtil.date2Str(Calendar.getInstance(), Constants.Xmpp.MS_FORMART);
		message.setProperty(Constants.IntentExtra.INTENT_EXTRA_IMMESSAGE_TIME, time);
		IMMessage imMessage = new IMMessage();
		try {
			chat.sendMessage(message);
			imMessage.setId(id);
			imMessage.setContent(msg);
			imMessage.setMsgType(IMMessage.CHAT_MESSAGE_TYPE_SEND);
			imMessage.setNoticeSum(0);
			imMessage.setNoticeTime(time);
			imMessage.setTime(time);
			imMessage.setReadStatus(IMMessage.READ_STATUS_READ);
			imMessage.setTitle("");
			imMessage.setFromSubJid(chatWithJid);
			imMessage.setFromUserName(title);
			imMessage.setAvatar(toAvatar);
			//
			String toJid = TutorApplication.getAccountDao().load("1").getImAccount() + "@" + XMPPConnectionManager.getManager().getServiceName();
			imMessage.setToJid(toJid);
			imMessage.setNoticeType(IMMessage.MESSAGE_TYPE_CHAT_MSG);
			imMessage.setSendStatus(IMMessage.SEND_STATUS_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			imMessage.setSendStatus(IMMessage.SEND_STATUS_ERROR);
		}
		imMessages.add(imMessage);
		IMMessageManager.getManager().sava(imMessage);
		// 刷新
		refreshMessage(imMessages);
	}

	/**
	 * 下滑加载信息,true 返回成功，false 数据已经全部加载，全部查完了，
	 * 
	 * @return
	 */
	protected boolean addNewMsg() {
		List<IMMessage> list = IMMessageManager.getManager().searchChatMessages(chatWithJid, null == imMessages || 0 == imMessages.size() ? 0 : imMessages.size(), Constants.Xmpp.CHAT_PAGESIZE);
		if (null != list && list.size() > 0) {
			imMessages.addAll(0, list);
			Collections.sort(imMessages);
			return true;
		}
		return false;
	}
}
