package com.tutor.service;

import java.util.Calendar;
import java.util.Collection;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.Roster.SubscriptionMode;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.text.TextUtils;

import com.mssky.mobile.helper.ToastUtils;
import com.tutor.R;
import com.tutor.TutorApplication;
import com.tutor.im.ContactManager;
import com.tutor.im.IMMessageManager;
import com.tutor.im.XMPPConnectionManager;
import com.tutor.model.IMMessage;
import com.tutor.model.User;
import com.tutor.params.Constants;
import com.tutor.ui.activity.ChatActivity;
import com.tutor.ui.activity.NoticeActivity;
import com.tutor.util.DateTimeUtil;
import com.tutor.util.LogUtils;
import com.tutor.util.StringUtils;
import com.tutor.util.ToastUtil;
import com.tutor.util.UUIDUtils;

/**
 * 即時通訊服務,包括網絡狀態改變檢測,接收聊天消息,系統消息,好友請求消息等
 * 
 * @author bruce.chen
 * 
 *         2015-8-28
 */
public class IMService extends Service {

	// 上下文
	private Context mContext;
	// 通知欄消息管理器
	private NotificationManager notificationManager;
	// 網絡連接管理器
	private ConnectivityManager connectivityManager;
	// 網絡信息
	private NetworkInfo networkInfo;
	// 廣播
	private IMBroadCastReceive receive;
	// XMPPConnect
	private XMPPConnection connection;
	private Roster roster;

	@Override
	public void onCreate() {
		super.onCreate();
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// 註冊网络连接廣播
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(receive, filter);
		// 初始化IM消息監聽
		initIMMsgListener();
	}

	private void initIMMsgListener() {
		connection = XMPPConnectionManager.getManager().getConnection();
		// 系統消息
		connection.addPacketListener(normalPacketListener, new MessageTypeFilter(Message.Type.normal));
		// 聊天消息
		connection.addPacketListener(chatPacketListener, new MessageTypeFilter(Message.Type.chat));
		// 好友添加請求
		PacketFilter filter = new PacketFilter() {

			@Override
			public boolean accept(Packet arg0) {
				if (arg0 instanceof Presence) {
					Presence presence = (Presence) arg0;
					if (presence.getType().equals(Presence.Type.subscribe)) {
						return true;
					}
				}
				return false;
			}
		};
		connection.addPacketListener(addSubscriptionListener, filter);
	}

	/**
	 * 非聊天消息監聽
	 */
	private PacketListener normalPacketListener = new PacketListener() {

		@Override
		public void processPacket(Packet packet) {
			Message message = (Message) packet;
			if (null != message && Message.Type.normal == message.getType()) {
				IMMessage imMessage = new IMMessage();
				// 內容
				String content = message.getBody();
				LogUtils.d("接收到新消息-->" + content);
				// 時間
				String time = (String) message.getProperty(Constants.IntentExtra.INTENT_EXTRA_IMMESSAGE_TIME);
				if (null == time) {
					time = DateTimeUtil.date2Str(Calendar.getInstance(), Constants.Xmpp.MS_FORMART);
				}
				// id
				String id = message.getPacketID();
				if (TextUtils.isEmpty(id)) {
					id = UUIDUtils.getID(6);
				}
				// 來自于誰
				String from = message.getFrom().split("/")[0];
				// 發送給誰
				String to = message.getTo();
				String title = getString(R.string.system_message);
				imMessage.set_id(id);
				imMessage.setTitle(title);
				imMessage.setContent(content);
				imMessage.setTime(time);
				imMessage.setNoticeTime(time);
				imMessage.setFromSubJid(from);
				imMessage.setToJid(to);
				imMessage.setReadStatus(IMMessage.READ_STATUS_UNREAD);
				imMessage.setMsgType(IMMessage.CHAT_MESSAGE_TYPE_RECEIVE);
				imMessage.setNoticeSum(1);
				imMessage.setNoticeType(IMMessage.MESSAGE_TYPE_SYS_MSG);
				// 保存消息
				long status = IMMessageManager.getManager().sava(imMessage);
				if (-1 != status) {
					// 發送廣播
					Intent intent = new Intent(Constants.Action.ACTION_SYS_MSG);
					intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_IMMESSAGE_KEY, imMessage);
					sendBroadcast(intent);
					// 發送通知
					if (Constants.Xmpp.isChatNotification) {
						setNotiType(R.drawable.icon_notification, title, content, NoticeActivity.class, null);
					}
				}
			}
		}
	};
	/**
	 * 聊天消息監聽
	 */
	private PacketListener chatPacketListener = new PacketListener() {

		@Override
		public void processPacket(Packet packet) {
			Message message = (Message) packet;
			if (null != message && null != message.getBody()) {
				IMMessage imMessage = new IMMessage();
				// 內容
				String content = message.getBody();
				LogUtils.d("接收到新消息-->" + content);
				// 時間
				String time = (String) message.getProperty(Constants.IntentExtra.INTENT_EXTRA_IMMESSAGE_TIME);
				if (null == time) {
					time = DateTimeUtil.date2Str(Calendar.getInstance(), Constants.Xmpp.MS_FORMART);
				}
				// id
				String id = message.getPacketID();
				if (TextUtils.isEmpty(id)) {
					id = UUIDUtils.getID(6);
				}
				// 來自于誰
				String from = message.getFrom().split("/")[0];
				// 發送給誰
				String to = message.getTo();
				String title = getString(R.string.new_message);
				imMessage.set_id(id);
				imMessage.setTitle(title);
				imMessage.setContent(content);
				imMessage.setTime(time);
				imMessage.setNoticeTime(time);
				imMessage.setFromSubJid(from);
				imMessage.setToJid(to);
				imMessage.setReadStatus(IMMessage.READ_STATUS_UNREAD);
				imMessage.setMsgType(IMMessage.CHAT_MESSAGE_TYPE_RECEIVE);
				imMessage.setNoticeSum(1);
				imMessage.setNoticeType(IMMessage.MESSAGE_TYPE_CHAT_MSG);
				// 保存
				long status = IMMessageManager.getManager().sava(imMessage);
				if (-1 != status) {
					// 保存成功
					// 發送廣播
					Intent intent = new Intent(Constants.Action.ACTION_NEW_MESSAGE);
					intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_IMMESSAGE_KEY, imMessage);
					sendBroadcast(intent);
					// 發送通知
					if (Constants.Xmpp.isChatNotification) {
						setNotiType(R.drawable.icon_notification, title, content, ChatActivity.class, from);
					}
				}
			}
		}
	};
	/**
	 * 好友請求監聽
	 */
	private PacketListener addSubscriptionListener = new PacketListener() {

		@Override
		public void processPacket(Packet packet) {
			try {
				if ("".contains(packet.getFrom())) {
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			// 如果是自动接收所有请求，则回复一个添加信息
			if (Roster.getDefaultSubscriptionMode().equals(SubscriptionMode.accept_all)) {
				Presence presence = new Presence(Presence.Type.subscribe);
				presence.setTo(packet.getFrom());
				connection.sendPacket(presence);
			} else {
				// 發送通知
				Message message = (Message) packet;
				if (null != message) {
					IMMessage imMessage = new IMMessage();
					// 內容
					String content = StringUtils.getUserNameByJid(packet.getFrom()) + getString(R.string.add_friend_lable);
					LogUtils.d("接收到新消息-->" + content);
					// 時間
					String time = (String) message.getProperty(Constants.IntentExtra.INTENT_EXTRA_IMMESSAGE_TIME);
					if (null == time) {
						time = DateTimeUtil.date2Str(Calendar.getInstance(), Constants.Xmpp.MS_FORMART);
					}
					// id
					String id = message.getPacketID();
					if (TextUtils.isEmpty(id)) {
						id = UUIDUtils.getID(6);
					}
					// 來自于誰
					String from = message.getFrom().split("/")[0];
					// 發送給誰
					String to = message.getTo();
					String title = getString(R.string.add_friend_request);
					imMessage.set_id(id);
					imMessage.setTitle(title);
					imMessage.setContent(content);
					imMessage.setTime(time);
					imMessage.setNoticeTime(time);
					imMessage.setFromSubJid(from);
					imMessage.setToJid(to);
					imMessage.setReadStatus(IMMessage.READ_STATUS_UNREAD);
					imMessage.setMsgType(IMMessage.CHAT_MESSAGE_TYPE_RECEIVE);
					imMessage.setNoticeSum(1);
					imMessage.setNoticeType(IMMessage.MESSAGE_TYPE_ADD_FRIEND);
					// 保存消息
					long status = IMMessageManager.getManager().sava(imMessage);
					if (-1 != status) {
						// 發送廣播
						Intent intent = new Intent(Constants.Action.ACTION_ROSTER_SUBSCRIPTION);
						intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_IMMESSAGE_KEY, imMessage);
						sendBroadcast(intent);
						// 發送通知
						if (Constants.Xmpp.isChatNotification) {
							setNotiType(R.drawable.icon_notification, title, content, NoticeActivity.class, null);
						}
					}
				}
			}
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		initRoster();
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 初始化联系人 服务重启时，更新联系人列表
	 */
	private void initRoster() {
		roster = connection.getRoster();
		roster.removeRosterListener(rosterListener);
		roster.addRosterListener(rosterListener);
		if (ContactManager.getManager().init(connection)) {
			Intent intent = new Intent();
			intent.setAction(Constants.Action.ACTION_REFRESH_CONTACT);
			sendBroadcast(intent);
		}
	}

	private RosterListener rosterListener = new RosterListener() {

		@Override
		public void presenceChanged(Presence arg0) {
			Intent intent = new Intent();
			intent.setAction(Constants.Action.ACTION_ROSTER_PRESENCE_CHANGED);
			String subscriber = arg0.getFrom().substring(0, arg0.getFrom().indexOf("/"));
			RosterEntry entry = roster.getEntry(subscriber);
			if (ContactManager.getManager().getContacters().containsKey(subscriber)) {
				ContactManager.getManager().getContacters().remove(subscriber);
				ContactManager.getManager().getContacters().put(subscriber, ContactManager.getManager().transEntryToUser(entry, roster));
				// 将状态改变的user广播出去
				intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_USER_KEY, ContactManager.getManager().getContacters().get(subscriber));
			}
			sendBroadcast(intent);
		}

		@Override
		public void entriesUpdated(Collection<String> addresses) {
			Intent intent = new Intent();
			intent.setAction(Constants.Action.ACTION_ROSTER_UPDATED);
			for (String address : addresses) {
				// 获得状态改变的entry
				RosterEntry userEntry = roster.getEntry(address);
				User user = ContactManager.getManager().transEntryToUser(userEntry, roster);
				if (ContactManager.getManager().getContacters().get(address) != null) {
					// 将发生改变的用户更新到userManager
					ContactManager.getManager().getContacters().remove(address);
					ContactManager.getManager().getContacters().put(address, user);
					// 这里发布的是更新前的user
					// intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_USER_KEY,
					// ContactManager.getManager().getContacters().get(address));
				}
			}
			sendBroadcast(intent);
			// 用户更新，getEntries会更新
			// roster.getUnfiledEntries中的entry不会更新
		}

		@Override
		public void entriesDeleted(Collection<String> addresses) {
			Intent intent = new Intent();
			intent.setAction(Constants.Action.ACTION_ROSTER_DELETED);
			for (String address : addresses) {
				// User user = null;
				if (ContactManager.getManager().getContacters().containsKey(address)) {
					// user =
					// ContactManager.getManager().getContacters().get(address);
					ContactManager.getManager().getContacters().remove(address);
				}
				// intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_USER_KEY,
				// user);
			}
			sendBroadcast(intent);
		}

		@Override
		public void entriesAdded(Collection<String> addresses) {
			Intent intent = new Intent();
			intent.setAction(Constants.Action.ACTION_ROSTER_ADDED);
			for (String address : addresses) {
				RosterEntry userEntry = roster.getEntry(address);
				User user = ContactManager.getManager().transEntryToUser(userEntry, roster);
				ContactManager.getManager().getContacters().put(address, user);
				// intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_USER_KEY,
				// user);
			}
			sendBroadcast(intent);
		}
	};

	@Override
	public void onDestroy() {
		// 註銷廣播接收器
		unregisterReceiver(receive);
		connection.removePacketListener(addSubscriptionListener);
		connection.removePacketListener(chatPacketListener);
		connection.removePacketListener(normalPacketListener);
		ContactManager.getManager().destory();
		super.onDestroy();
	}

	/**
	 * 廣播接收器
	 * 
	 * @author bruce.chen
	 * 
	 */
	private class IMBroadCastReceive extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
				// 網絡狀態
				LogUtils.d("network status has change");
				// 獲取管理器
				connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
				// 初始化網絡狀態對象
				networkInfo = connectivityManager.getActiveNetworkInfo();
				// XMPP連接對象
				XMPPConnection connection = XMPPConnectionManager.getManager().getConnection();
				if (null != networkInfo && networkInfo.isAvailable()) {
					// 有網絡但是連接斷開,遞歸連接
					if (!connection.isConnected()) {
						reConnect(connection);
					} else {
						sendIntentAndPre(Constants.Xmpp.RECONNECT_STATE_SUCCESS);
					}
				} else {
					ToastUtils.showShort(context, R.string.toast_offline);
				}
			}
		}
	}

	/**
	 * 递归重连，直连上为止.
	 * 
	 * @param connection
	 */
	private void reConnect(XMPPConnection connection) {
		try {
			connection.connect();
			if (connection.isConnected()) {
				Presence presence = new Presence(Presence.Type.available);
				connection.sendPacket(presence);
				ToastUtil.showToastShort(mContext, R.string.toast_online);
			}
		} catch (XMPPException e) {
			LogUtils.e("connection failed!" + e.toString());
			reConnect(connection);
		}
	}

	/**
	 * 保存状态
	 * 
	 * @param isSuccess
	 */
	private void sendIntentAndPre(boolean isSuccess) {
		// 保存在线连接信息
		TutorApplication.settingManager.writeSetting(Constants.SharedPreferences.SP_IS_ONLINE, isSuccess);
		// 发送广播
		Intent intent = new Intent(Constants.Action.ACTION_RECONNECT_STATE);
		intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_RECONNECT_STATE, isSuccess);
		sendBroadcast(intent);
	}

	/**
	 * 在狀態欄彈出通知
	 * 
	 * @param iconId
	 *            圖標
	 * @param contentTitle
	 *            標題
	 * @param contentText
	 *            內容
	 * @param activity
	 *            要進入的activity
	 */
	@SuppressWarnings("deprecation")
	private void setNotiType(int iconId, String contentTitle, String contentText, Class<?> activity, String fromJid) {
		/*
		 * 创建新的Intent，作为点击Notification留言条时， 会运行的Activity
		 */
		Intent notifyIntent = new Intent(this, activity);
		notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (null != fromJid) {
			notifyIntent.putExtra(Constants.IntentExtra.INTENT_EXTRA_MESSAGE_TO, fromJid);
		}
		/* 创建PendingIntent作为设置递延运行的Activity */
		PendingIntent appIntent = PendingIntent.getActivity(this, 0, notifyIntent, 0);
		/* 创建Notication，并设置相关参数 */
		Notification myNoti = new Notification();
		/* 设置statusbar显示的icon */
		myNoti.icon = iconId;
		/* 设置statusbar显示的文字信息 */
		myNoti.tickerText = contentTitle;
		/* 设置notification发生时同时发出默认声音 */
		myNoti.defaults = Notification.DEFAULT_SOUND;
		/* 设置Notification留言条的参数 */
		myNoti.setLatestEventInfo(this, contentTitle, contentText, appIntent);
		/* 送出Notification */
		notificationManager.notify(0, myNoti);
	}
}