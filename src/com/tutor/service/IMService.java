package com.tutor.service;

import java.net.HttpURLConnection;
import java.util.Calendar;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

import android.annotation.SuppressLint;
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
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.text.TextUtils;

import com.hk.tutor.R;
import com.loopj.android.http.RequestParams;
import com.mssky.mobile.helper.ToastUtils;
import com.tutor.TutorApplication;
import com.tutor.im.ContactManager;
import com.tutor.im.IMMessageManager;
import com.tutor.im.XMPPConnectionManager;
import com.tutor.im.XmppManager;
import com.tutor.model.Account;
import com.tutor.model.Avatar;
import com.tutor.model.IMMessage;
import com.tutor.model.UserInfo;
import com.tutor.model.UserInfoResult;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.activity.ChatListActivity;
import com.tutor.util.DateTimeUtil;
import com.tutor.util.HttpHelper;
import com.tutor.util.LogUtils;
import com.tutor.util.ObjectHttpResponseHandler;
import com.tutor.util.UUIDUtils;

/**
 * 即時通訊服務,包括網絡狀態改變檢測,接收聊天消息,系統消息,好友請求消息等
 * 
 * @author bruce.chen
 * 
 *         2015-8-28
 */
public class IMService extends Service {

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
	// 震動
	private Vibrator vibrator;
	// private final static int SENSOR_SNAKE = 10;
	private long[] times = new long[] { 100, 200, 100, 200 };

	@Override
	public void onCreate() {
		super.onCreate();
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// 註冊网络连接廣播
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		receive = new IMBroadCastReceive();
		registerReceiver(receive, filter);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new LoginImTask().execute();
		return super.onStartCommand(intent, START_STICKY, startId);
	}

	private void initIMMsgListener() {
		connection = XMPPConnectionManager.getManager().getConnection();
		// 聊天消息
		connection.addPacketListener(chatPacketListener, new MessageTypeFilter(Message.Type.chat));
	}

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
				LogUtils.d("接收到聊天消息-->" + content);
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
				String to = "";
				if (message.getTo().contains("/")) {
					String[] msgTos = message.getTo().split("/");
					if (msgTos.length >= 2) {
						to = message.getTo().split("/")[0];
					} else {
						to = message.getTo();
					}
				} else {
					to = message.getTo();
				}
				String title = getString(R.string.new_message);
				imMessage.setId(id);
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
				int index = from.indexOf("@");
				String imId = from.substring(0, index);
				Avatar avatar = TutorApplication.getAvatarDao().load(imId);
				if (avatar == null) {
					IMService.this.imMessage = imMessage;
					IMService.this.imid = imId;
					handler.sendEmptyMessage(0);
					return;
				}
				imMessage.setAvatar(avatar.getAvatar());
				String name = avatar.getNickName();
				if (TextUtils.isEmpty(name)) {
					name = avatar.getUserName();
					if (TextUtils.isEmpty(name)) {
						name = "Adviser";
					}
				}
				imMessage.setFromUserName(name);
				// 保存
				long status = IMMessageManager.getManager().sava(imMessage);
				if (-1 != status) {
					// 保存成功
					// 發送廣播
					Intent intent = new Intent(Constants.Action.ACTION_NEW_MESSAGE);
					intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_IMMESSAGE_KEY, imMessage);
					sendBroadcast(intent);
					// 震動提示
					vibrator.vibrate(times, -1);
					// 發送通知
					if (Constants.Xmpp.isChatNotification) {
						setNotiType(R.drawable.icon_notification, imMessage, ChatListActivity.class);
					}
				}
			}
		}
	};
	IMMessage imMessage;
	String imid;
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		@Override
		public void handleMessage(android.os.Message msg) {
			getUserInfo(imMessage, imid);
			super.handleMessage(msg);
		}
	};

	private void getUserInfo(final IMMessage imMessage, final String imId) {
		String url = String.format(ApiUrl.IM_GET_INFO, imId);
		HttpHelper.get(TutorApplication.instance, url, TutorApplication.getHeaders(), new RequestParams(), new ObjectHttpResponseHandler<UserInfoResult>(UserInfoResult.class) {

			@Override
			public void onFailure(int status, String message) {
				if (status == 0) {
					getUserInfo(imMessage, imId);
				}
			}

			@Override
			public void onSuccess(UserInfoResult info) {
				if (info.getStatusCode() == HttpURLConnection.HTTP_OK) {
					UserInfo userInfo = info.getResult();
					Avatar avatar = new Avatar();
					String avatarStr = "";
					if (userInfo != null) {
						avatarStr = ApiUrl.DOMAIN + userInfo.getAvatar();
						avatar.setNickName(userInfo.getNickName());
						avatar.setUserName(userInfo.getUserName());
					} else {
						avatarStr = "";
						avatar.setNickName("");
						avatar.setUserName("");
					}
					avatar.setAvatar(avatarStr);
					avatar.setId(imId);
					TutorApplication.getAvatarDao().insert(avatar);
					imMessage.setAvatar(avatarStr);
					String name = avatar.getNickName();
					if (TextUtils.isEmpty(name)) {
						name = avatar.getUserName();
					}
					imMessage.setFromUserName(name);
					// 保存
					long status = IMMessageManager.getManager().sava(imMessage);
					if (-1 != status) {
						// 保存成功
						// 發送廣播
						Intent intent = new Intent(Constants.Action.ACTION_NEW_MESSAGE);
						intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_IMMESSAGE_KEY, imMessage);
						sendBroadcast(intent);
						// 震動提示
						vibrator.vibrate(times, -1);
						// 發送通知
						if (Constants.Xmpp.isChatNotification) {
							setNotiType(R.drawable.icon_notification, imMessage, ChatListActivity.class);
						}
					}
				}
			}
		});
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		// 註銷廣播接收器
		unregisterReceiver(receive);
		if (connection != null && chatPacketListener != null) {
			connection.removePacketListener(chatPacketListener);
			ContactManager.getManager().destory();
		}
		super.onDestroy();
		// 重启
		Intent intent = new Intent(this, IMService.class);
		startService(intent);
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
	private void reConnect(final XMPPConnection connection) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					if (!connection.isConnected()) {
						connection.connect();
						if (connection.isConnected()) {
							Presence presence = new Presence(Presence.Type.available);
							connection.sendPacket(presence);
						}
					}
				} catch (Exception e) {
					LogUtils.e("connection failed!" + e.toString());
					reConnect(connection);
				}
			}
		}).start();
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
	private void setNotiType(int iconId, IMMessage message, Class<?> activity) {
		/*
		 * 创建新的Intent，作为点击Notification留言条时， 会运行的Activity
		 */
		Intent notifyIntent = new Intent(this, activity);
		notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		if (null != message.getFromSubJid()) {
			notifyIntent.putExtra(Constants.IntentExtra.INTENT_EXTRA_MESSAGE_TO, message.getFromSubJid());
			notifyIntent.putExtra(Constants.General.NICKNAME, message.getFromUserName());
			notifyIntent.putExtra(Constants.General.AVATAR, message.getAvatar());
		}
		/* 创建PendingIntent作为设置递延运行的Activity */
		PendingIntent appIntent = PendingIntent.getActivity(this, 0, notifyIntent, 0);
		/* 创建Notication，并设置相关参数 */
		Notification myNoti = new Notification();
		/* 设置statusbar显示的icon */
		myNoti.icon = iconId;
		myNoti.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONLY_ALERT_ONCE;
		/* 设置statusbar显示的文字信息 */
		myNoti.tickerText = message.getTitle();
		/* 设置notification发生时同时发出默认声音 */
		myNoti.defaults = Notification.DEFAULT_SOUND;
		/* 设置Notification留言条的参数 */
		myNoti.setLatestEventInfo(this, message.getTitle(), message.getContent(), appIntent);
		/* 送出Notification */
		notificationManager.notify(0, myNoti);
	}

	/**
	 * 登录IM
	 * 
	 * 
	 */
	private class LoginImTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected Integer doInBackground(Void... params) {
			if (TutorApplication.connectionManager.getConnection().isConnected()) {
				TutorApplication.connectionManager.getConnection().disconnect();
			}
			// 執行登錄IM
			Account account = TutorApplication.getAccountDao().load("1");
			if (null != account) {
				String accountsString = account.getImAccount();
				String pswd = account.getImPswd();
				return XmppManager.getInstance().login(accountsString, pswd);
			}
			return Constants.Xmpp.LOGIN_ERROR;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if (result == Constants.Xmpp.LOGIN_SECCESS) {
				// 初始化IM消息監聽
				initIMMsgListener();
			} else {
				new LoginImTask().execute();
			}
		}
	}
}
