package com.tutor;

import java.util.HashMap;
import java.util.Locale;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import cn.jpush.android.api.JPushInterface;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.tutor.im.XMPPConnectionManager;
import com.tutor.model.Account;
import com.tutor.model.AccountDao;
import com.tutor.model.AvatarDao;
import com.tutor.model.DaoMaster;
import com.tutor.model.DaoMaster.OpenHelper;
import com.tutor.model.DaoSession;
import com.tutor.model.IMMessageDao;
import com.tutor.model.LogDao;
import com.tutor.params.Constants;
import com.tutor.util.DataCleanManager;
import com.tutor.util.HanderException;
import com.tutor.util.SettingManager;
//
//import com.google.android.gms.analytics.GoogleAnalytics;
//import com.google.android.gms.analytics.Tracker;

/**
 * 程序的入口,在这里做一些初始化的操作
 * 
 * @author bruce.chen
 * 
 */
public class TutorApplication extends Application {

	// 用来判断是否是进入到了主界面
	public static boolean isMainActivity = false;
	public static boolean DEBUG = false;
	public static SettingManager settingManager;
	// 数据库相关
	private static AccountDao accountDao;
	private static IMMessageDao imMessageDao;
	private static AvatarDao avatarDao;
	private static LogDao logDao;
	private static SQLiteDatabase db;
	//
	public static TutorApplication instance;
	public static boolean isTokenInvalid = false;
	/** xmpp连接管理对象 */
	public static XMPPConnectionManager connectionManager;
	public static int jPushMessageType;
	public static boolean isTimeTableMessage;

	// public static GoogleAnalytics analytics;
	// public static Tracker tracker;
	// private static String GOOGLE_TRACKER_ID = "UA-69608349-1";
	//
	// public static GoogleAnalytics analytics() {
	// return analytics;
	// }
	//
	// public static Tracker tracker() {
	// return tracker;
	// }
	// @Override
	// protected void attachBaseContext(Context base) {
	// // TODO Auto-generated method stub
	// super.attachBaseContext(base);
	// MultiDex.install(this);
	// }
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		// TODO 打包发布时请将DEBUG设为false
		DEBUG = true;
		// 配置工具類
		settingManager = SettingManager.getInstance(this, Constants.SharedPreferences.NAME);
		int oldVersionCode = (Integer) settingManager.readSetting(Constants.SharedPreferences.VERSIONCODE, -1);
		int versionCode = getVersionCode(instance);
		if (oldVersionCode == -1 || versionCode != oldVersionCode) {
			settingManager.deleteAll();
			DataCleanManager.cleanAllFiles(instance);
			settingManager.writeSetting(Constants.SharedPreferences.VERSIONCODE, versionCode);
		}
		connectionManager = XMPPConnectionManager.getManager();
		initDao();
		initImageLoader(this);
		// 设置开启日志,发布时请关闭日志
		JPushInterface.setDebugMode(true);
		// 初始化 JPush
		JPushInterface.init(this);
		HanderException.getInstance().init(this, null);
		// 初始化谷歌分析工具
		// initGoogleAnalytics();
	}
	public int getVersionCode(Context activity) {
		String pName = activity.getPackageName();
		int versionCode = -1;
		try {
			PackageInfo pinfo = activity.getPackageManager().getPackageInfo(pName, PackageManager.GET_CONFIGURATIONS);
			versionCode = pinfo.versionCode;
		} catch (NameNotFoundException e) {}
		return versionCode;
	}

	// private void initGoogleAnalytics() {
	// analytics = GoogleAnalytics.getInstance(this);
	// analytics.setLocalDispatchPeriod(3600); // 每小时调度一次
	// tracker = analytics.newTracker(GOOGLE_TRACKER_ID); // Replace with
	// // actual
	// // tracker/property
	// // Id
	// // Provide unhandled exceptions reports. Do that first after creating
	// // the tracker
	// tracker.enableExceptionReporting(true);
	// // Enable Remarketing, Demographics & Interests reports
	// //
	// https: //
	// developers.google.com/analytics/devguides/collection/android/display-features
	// tracker.enableAdvertisingIdCollection(true);
	// // Enable automatic activity tracking for your app
	// tracker.enableAutoActivityTracking(true);
	// }
	private void initDao() {
		OpenHelper helper = new DaoMaster.DevOpenHelper(this, Constants.General.DBNAME, null);
		db = helper.getWritableDatabase();
		DaoMaster daoMaster = new DaoMaster(db);
		DaoSession daoSession = daoMaster.newSession();
		accountDao = daoSession.getAccountDao();
		imMessageDao = daoSession.getIMMessageDao();
		avatarDao = daoSession.getAvatarDao();
		logDao = daoSession.getLogDao();
	}

	/**
	 * 初始化imageloader
	 * 
	 * @param applicationContext
	 */
	private void initImageLoader(Context applicationContext) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(applicationContext).threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO).build();// 发布APP时删除writeDebugLogs()
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

	public static AccountDao getAccountDao() {
		return accountDao;
	}

	public static AvatarDao getAvatarDao() {
		return avatarDao;
	}

	public static IMMessageDao getImMessageDao() {
		return imMessageDao;
	}

	public static SQLiteDatabase getDatabase() {
		return db;
	}

	public static LogDao getLogDao() {
		return logDao;
	}

	/**
	 * 獲取token
	 * 
	 * @return
	 */
	public static String getToken() {
		String token = "";
		Account account = accountDao.load("1");
		if (null != account) {
			token = account.getToken();
		}
		return token;
	}

	/**
	 * 获取自己的id
	 * 
	 * @return
	 */
	public static int getMemberId() {
		Account account = accountDao.load("1");
		if (null != account) {
			return account.getMemberId();
		}
		return -1;
	}

	/**
	 * 获取自己的角色
	 * 
	 * @return
	 */
	public static int getRole() {
		Account account = accountDao.load("1");
		if (null != account) {
			return account.getRole();
		}
		return -1;
	}

	/**
	 * 获取自己的HKID
	 * 
	 * @return
	 */
	public static String getHKID() {
		Account account = accountDao.load("1");
		if (null != account) {
			return account.getHkidNumber();
		}
		return "";
	}

	/**
	 * 获取自己的电话
	 * 
	 * @return
	 */
	public static String getPhoneNum() {
		Account account = accountDao.load("1");
		if (null != account) {
			return account.getPhone();
		}
		return "";
	}

	/**
	 * 获取自己的居住地址
	 * 
	 * @return
	 */
	public static String getResidentialAddress() {
		Account account = accountDao.load("1");
		if (null != account) {
			return account.getResidentialAddress();
		}
		return "";
	}

	public static HashMap<String, String> getHeaders() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("token", getToken());
		map.put("lang", getLang());
		return map;
	}

	private static String getLang() {
		String lang = Locale.getDefault().getLanguage();
		System.out.println(lang);
		if (null != lang && lang.endsWith("zh")) {
			lang = "zh-hk";
		} else {
			lang = "en-us";
		}
		return lang;
	}

	public static boolean isCH() {
		String lang = Locale.getDefault().getLanguage();
		System.out.println(lang);
		if (null != lang && lang.endsWith("zh")) {
			return true;
		} else {
			return false;
		}
	}
}
