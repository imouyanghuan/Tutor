package com.tutor;

import java.net.HttpURLConnection;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;

import com.hk.tutor.R;
import com.loopj.android.http.RequestParams;
import com.tutor.im.IMMessageManager;
import com.tutor.model.Avatar;
import com.tutor.model.BroadCastModel;
import com.tutor.model.IMMessage;
import com.tutor.model.UserInfo;
import com.tutor.model.UserInfoResult;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.activity.ChatListActivity;
import com.tutor.ui.activity.StudentInfoActivity;
import com.tutor.ui.activity.SystemNotificationActivity;
import com.tutor.ui.activity.TeacherInfoActivity;
import com.tutor.ui.activity.TimeTableActivity;
import com.tutor.util.DateTimeUtil;
import com.tutor.util.HttpHelper;
import com.tutor.util.JsonUtil;
import com.tutor.util.LogUtils;
import com.tutor.util.ObjectHttpResponseHandler;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则： 1) 默认用户会打开主界面 2) 接收不到自定义消息
 */
public class JPushReceiver extends BroadcastReceiver {

	private static final String TAG = "JPush";
	private Context mContext;
	private BroadCastModel broadCastModel;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.mContext = context;
		Bundle bundle = intent.getExtras();
		Log.d(TAG, "[JPushReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
		/**
		 * 处理time table和分组发送信息
		 */
		processTimeTableAndTagsMessage(context, bundle);
		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
			String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
			Log.d(TAG, "[JPushReceiver] 接收Registration Id : " + regId);
			// send the Registration Id to your server...
		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
			Log.d(TAG, "[JPushReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
			processChatAndNotificationMessage(context, bundle);
		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
			Log.d(TAG, "[JPushReceiver] 接收到推送下来的通知");
			int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
			Log.d(TAG, "[JPushReceiver] 接收到推送下来的通知的ID: " + notifactionId);
		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
			Log.d(TAG, "[JPushReceiver] 用户点击打开了通知");
			// 打开自定义的Activity 点击手机状态栏通知要跳转的页面
			Intent openIntent = null;
			if (TutorApplication.jPushMessageType == Constants.General.JPUSH_MESSAGE_TYPE_CHAT) {
				openIntent = new Intent(context, ChatListActivity.class);
			} else if (TutorApplication.jPushMessageType == Constants.General.JPUSH_MESSAGE_TYPE_BROADCAST) {
				// 如果当前登录的是学生：则跳转到老师详情，如果登录的老师：则跳转到学生详情
				if (TutorApplication.getRole() == Constants.General.ROLE_STUDENT) {
					openIntent = new Intent(context, TeacherInfoActivity.class);
				} else {
					openIntent = new Intent(context, StudentInfoActivity.class);
				}
				openIntent.putExtra(Constants.General.BROADCAST_MODEL, broadCastModel);
			} else if (TutorApplication.jPushMessageType == Constants.General.JPUSH_MESSAGE_TYPE_TIME_TABLE) {
				openIntent = new Intent(context, TimeTableActivity.class);
			} else {
				openIntent = new Intent(context, SystemNotificationActivity.class);
			}
			openIntent.putExtras(bundle);
			// i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			context.startActivity(openIntent);
		} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
			Log.d(TAG, "[JPushReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
			// 在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity，
			// 打开一个网页等..
		} else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
			boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
			Log.w(TAG, "[JPushReceiver]" + intent.getAction() + " connected state change to " + connected);
		} else {
			Log.d(TAG, "[JPushReceiver] Unhandled intent - " + intent.getAction());
		}
	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			} else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}

	// process Chat And Notification Message
	private void processChatAndNotificationMessage(Context context, Bundle bundle) {
		String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
		String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
		LogUtils.e("processCustomMessage extras = " + extras);
		if (!TextUtils.isEmpty(extras)) {
			if (extras.contains("isChat")) {
				// 1
				TutorApplication.jPushMessageType = Constants.General.JPUSH_MESSAGE_TYPE_CHAT;
				if (!TextUtils.isEmpty(message)) {
					IMMessage imMessage = JsonUtil.parseJStr2Object(IMMessage.class, message);
					if (imMessage != null) {
						saveMessage(imMessage);
					}
				}
			} else if (extras.contains("broadcastId")) {
				// 按照tag推送
				TutorApplication.jPushMessageType = Constants.General.JPUSH_MESSAGE_TYPE_BROADCAST;
			} else {
				// 0
				TutorApplication.jPushMessageType = Constants.General.JPUSH_MESSAGE_TYPE_NOTIFICATION;
			}
		} else {
			// 0
			TutorApplication.jPushMessageType = Constants.General.JPUSH_MESSAGE_TYPE_NOTIFICATION;
		}
	}

	// process TimeTable And Tags Message
	private void processTimeTableAndTagsMessage(Context context, Bundle bundle) {
		String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
		LogUtils.e("processTimeTableAndTagsMessage extras = " + extras);
		if (!TextUtils.isEmpty(extras)) {
			broadCastModel = JsonUtil.parseJStr2Object(BroadCastModel.class, extras);
			if (extras.contains("time")) {
				// 2
				TutorApplication.jPushMessageType = Constants.General.JPUSH_MESSAGE_TYPE_TIME_TABLE;
			} else if (extras.contains("broadcastId")) {
				// 3 按照tag推送
				TutorApplication.jPushMessageType = Constants.General.JPUSH_MESSAGE_TYPE_BROADCAST;
			} else {
				// 0
				TutorApplication.jPushMessageType = Constants.General.JPUSH_MESSAGE_TYPE_NOTIFICATION;
			}
		} else {
			// 0
			TutorApplication.jPushMessageType = Constants.General.JPUSH_MESSAGE_TYPE_NOTIFICATION;
		}
	}

	private void saveMessage(IMMessage imMessage) {
		// 時間
		String time = DateTimeUtil.date2Str(Calendar.getInstance(), Constants.Xmpp.MS_FORMART);
		// 來自于誰
		String from = imMessage.getFromSubJid();
		if (from.contains("/")) {
			from = imMessage.getFromSubJid().split("/")[0];
		}
		// 發送給誰
		String to = imMessage.getToJid();
		if (to.contains("/")) {
			String[] msgTos = to.split("/");
			if (msgTos.length >= 2) {
				to = to.split("/")[0];
			}
		}
		String title = mContext.getString(R.string.new_message);
		imMessage.setTitle(title);
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
			this.imMessage = imMessage;
			imid = imId;
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
		IMMessageManager.getManager().sava(imMessage);
	}

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
		HttpHelper.getHelper().get(url, TutorApplication.getHeaders(), new RequestParams(), new ObjectHttpResponseHandler<UserInfoResult>(UserInfoResult.class) {

			@Override
			public void onFailure(int status, String message) {
				// TODO Auto-generated method stub
				LogUtils.e("-----status:" + status + ",message +" + message);
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
					IMMessageManager.getManager().sava(imMessage);
				}
			}
		});
	}
}
