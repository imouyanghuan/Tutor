package com.tutor.params;

import java.io.File;

import android.os.Environment;

/**
 * 常量管理类
 * 
 * @author bruce.chen
 * 
 *         2015-8-20
 */
public final class Constants {

	/**
	 * 通用常量
	 * 
	 * @author bruce.chen
	 * 
	 */
	public static final class General {

		/** 歡迎頁顯示時間 2.5s */
		public static final long WELCOME_DELAY = 2500;
		/** 數據庫名稱 */
		public static final String DBNAME = "tutor.db";
		/** 數據庫版本 */
		public static final int DBVERSION = 1;
		/** 學生或家長 */
		public static final int ROLE_STUDENT = 0X0;
		/** 老師 */
		public static final int ROLE_TUTOR = 0X1;
		/** 男 */
		public static final String MALE = "male";
		/** 女 */
		public static final String FEMALE = "female";
		/** 圖片結尾 */
		public static final String IMAGE_END = ".jpg";
		/** Token过期状态码 */
		public static final int TOKEN_INVALID = 401;
		/** 精确到毫秒 */
		public static final String MS_FORMART = "yyyy-MM-dd HH:mm:ss SSS";
	}

	/**
	 * 配置文件相關
	 * 
	 * @author bruce.chen
	 * 
	 */
	public static final class SharedPreferences {

		/** 文件名稱 */
		public static final String NAME = "tutor";
		/** 是否登錄 */
		public static final String SP_ISLOGIN = "islogin";
		/** 身份標識 */
		public static final String SP_ROLE = "role";
		/** 在線狀態 */
		public static final String SP_IS_ONLINE = "is_online";
	}

	/**
	 * 請求碼,結果碼
	 * 
	 * @author bruce.chen
	 * 
	 */
	public static final class RequestResultCode {

		/** 拍照 */
		public static final int TAKE_PHOTO = 0X1;
		/** 圖庫 */
		public static final int GALLERY = 0x2;
		/** 裁剪 */
		public static final int ZOOM = 0x3;
	}

	/**
	 * intent 攜帶參數key
	 * 
	 * @author bruce.chen
	 * 
	 */
	public static final class IntentExtra {

		/** 身份 */
		public final static String INTENT_EXTRA_ROLE = "role.key";
		/** 描述重连接状态的关键字，寄放的intent的关键字 */
		public static final String INTENT_EXTRA_RECONNECT_STATE = "reconnect_state";
		/** 新消息 */
		public static final String INTENT_EXTRA_IMMESSAGE_KEY = "immessage.key";
		/** 消息时间 */
		public static final String INTENT_EXTRA_IMMESSAGE_TIME = "immessage.time";
		/** 消息的联系人 */
		public static final String INTENT_EXTRA_IMMESSAGE_FORM = "immessage.form";
		/** 消息来源 */
		public static final String INTENT_EXTRA_MESSAGE_TO = "immessage.to";
		/** 联系人状态改变的key */
		public static final String INTENT_EXTRA_ROSTER_PRESENCE_CHANGED = "roster.presence.changed.key";
		/** user Key */
		public static final String INTENT_EXTRA_USER_KEY = "user.key";
		/** 联系人删除 */
		public static final String INTENT_EXTRA_ROSTER_DELETED = "roster.deleted.key";
		/** 联系人更新 */
		public static final String INTENT_EXTRA_ROSTER_UPDATED = "roster.updated.key";
		/** 联系人增加 */
		public static final String INTENT_EXTRA_ROSTER_ADDED = "roster.added.key";
		/** 好友分组 */
		public static final String INTENT_EXTRA_GROUP = "group.key";
		/** 用戶信息 */
		public static final String INTENT_EXTRA_USER_INFO = "userInfo";
	}

	/**
	 * 文件相關
	 * 
	 * @author bruce.chen
	 * 
	 */
	public static final class SDCard {

		/** SDCARD根目录 */
		public final static String SDCARD = Environment.getExternalStorageDirectory().getPath();
		/** 项目文件夹 */
		private static final String STORAGE_DIR = SDCARD + File.separatorChar + "Tutor" + File.separatorChar;
		/** 图片缓存文件夹 */
		public final static String CACHE = "cache" + File.separatorChar;
		/** 图片文件夹 */
		public final static String IMAGE = "image" + File.separatorChar;

		public static String getCacheDir() {
			File file = new File(STORAGE_DIR + CACHE);
			if (!file.exists()) {
				file.mkdirs();
			}
			file = null;
			return STORAGE_DIR + CACHE;
		}

		public static String getImageDir() {
			File file = new File(STORAGE_DIR + IMAGE);
			if (!file.exists()) {
				file.mkdirs();
			}
			file = null;
			return STORAGE_DIR + IMAGE;
		}
	}

	/**
	 * 自定義action
	 * 
	 * @author bruce.chen
	 * 
	 */
	public static final class Action {

		/** 銷毀登錄界面 */
		public static final String FINISH_LOGINACTIVITY = "com.tutor.action.finishloginactivity";
		/** 新消息action */
		public static final String ACTION_NEW_MESSAGE = "roster.newmessage";
		/** 重连接状态acttion */
		public static final String ACTION_RECONNECT_STATE = "action_reconnect_state";
		/** 消息类型关键字 */
		public static final String ACTION_SYS_MSG = "action_sys_msg";
		/** 收到好友申请 */
		public static final String ACTION_ROSTER_SUBSCRIPTION = "roster.subscribe";
		/** 联系人状态改变的action */
		public static final String ACTION_ROSTER_PRESENCE_CHANGED = "roster.presence.changed";
		/** 联系人有删除的ACTION */
		public static final String ACTION_ROSTER_DELETED = "roster.deleted";
		/** 联系人有更新的ACTION */
		public static final String ACTION_ROSTER_UPDATED = "roster.updated";
		/** 联系人有增加的ACTION */
		public static final String ACTION_ROSTER_ADDED = "roster.added";
		/** 刷新联系人列表 */
		public static final String ACTION_REFRESH_CONTACT = "refresh.contact";
		/** Token过期 */
		public static final String ACTION_TOKEN_INVALID = "token.invalid";
	}

	/**
	 * xmpp即時通訊相關
	 * 
	 * @author bruce.chen
	 * 
	 */
	public static final class Xmpp {

		/** 描述重连接 */
		public static final boolean RECONNECT_STATE_SUCCESS = true;
		public static final boolean RECONNECT_STATE_FAIL = false;
		/** 聊天消息是否通知 */
		public static boolean isChatNotification = true;
		/** 连接超时时间 30S */
		public static final int CONNECT_TIMEOUT = 30 * 1000;
		/** 在聊天activity加载聊天记录时的每次加载的数量 */
		public static final int CHAT_PAGESIZE = 10;
		/** 所有好友 */
		public static final String ALL_FRIEND = "所有好友";
		/** 未分组好友 */
		public static final String NO_GROUP_FRIEND = "未分组好友";
		/** 登陆成功 */
		public static final int LOGIN_SECCESS = 0XF4;
		/** 账号或者密码错误 */
		public static final int LOGIN_ERROR_ACCOUNT_PASS = 0XF5;
		/** 无法连接到服务器 */
		public static final int SERVER_UNAVAILABLE = 0XF6;
		/** 连接失败 */
		public static final int LOGIN_ERROR = 0XF7;
		/** 註冊失敗 */
		public static final int REGISTER_ERROR = 0XF8;
		/** 註冊時賬戶存在 */
		public static final int REGISTER_ACCOUNT_EXIST = 0XF9;
		/** 註冊成功 */
		public static final int REGISTER_SUCCESS = 0XF10;
		/** 精确到毫秒 */
		public static final String MS_FORMART = "yyyy-MM-dd HH:mm:ss SSS";
	}
}
