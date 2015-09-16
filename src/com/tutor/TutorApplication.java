package com.tutor;

import java.util.HashMap;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.tutor.im.XMPPConnectionManager;
import com.tutor.model.Account;
import com.tutor.model.AccountDao;
import com.tutor.model.DaoMaster;
import com.tutor.model.DaoMaster.OpenHelper;
import com.tutor.model.DaoSession;
import com.tutor.model.IMMessageDao;
import com.tutor.params.Constants;
import com.tutor.util.SettingManager;

/**
 * 程序的入口,在这里做一些初始化的操作
 * 
 * @author bruce.chen
 * 
 */
public class TutorApplication extends Application {

	public static boolean DEBUG = false;
	public static SettingManager settingManager;
	// 数据库相关
	private static AccountDao accountDao;
	private static IMMessageDao imMessageDao;
	private static SQLiteDatabase db;
	//
	public static TutorApplication instance;
	public static boolean isTokenInvalid = false;
	/** xmpp连接管理对象 */
	public static XMPPConnectionManager connectionManager;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		// TODO 打包发布时请将DEBUG设为false
		DEBUG = true;
		// 配置工具類
		settingManager = SettingManager.getInstance(this, Constants.SharedPreferences.NAME);
		connectionManager = XMPPConnectionManager.getManager();
		initDao();
		initImageLoader(this);
	}

	private void initDao() {
		OpenHelper helper = new DaoMaster.DevOpenHelper(this, Constants.General.DBNAME, null);
		db = helper.getWritableDatabase();
		DaoMaster daoMaster = new DaoMaster(db);
		DaoSession daoSession = daoMaster.newSession();
		accountDao = daoSession.getAccountDao();
		imMessageDao = daoSession.getIMMessageDao();
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

	public static IMMessageDao getImMessageDao() {
		return imMessageDao;
	}

	public static SQLiteDatabase getDatabase() {
		return db;
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

	public static HashMap<String, String> getHeaders() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("token", getToken());
		map.put("lang", "en_US");
		return map;
	}
}
