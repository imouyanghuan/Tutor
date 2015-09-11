package com.tutor;

import org.apache.http.protocol.HTTP;

import android.app.Application;
import android.content.Context;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.DbUtils.DaoConfig;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.http.RequestParams;
import com.mssky.mobile.core.CoreUtils;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.tutor.model.Account;
import com.tutor.model.IMMessage;
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
	public static DbUtils dbUtils;
	public static TutorApplication instance;
	public static boolean isTokenInvalid = false;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		// TODO 打包发布时请将DEBUG设为false
		DEBUG = true;
		// 配置工具類
		settingManager = SettingManager.getInstance(this, Constants.SharedPreferences.NAME);
		// 配置db工具
		initDbUtils();
		initImageLoader(this);
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

	private void initDbUtils() {
		if (null == dbUtils) {
			DaoConfig config = new DaoConfig(this);
			config.setDbName(Constants.General.DBNAME);
			config.setDbVersion(Constants.General.DBVERSION);
			dbUtils = CoreUtils.getDbUtils(config);
			// TODO 發佈時關閉
			dbUtils.configDebug(true);
			// 創建表
			try {
				dbUtils.createTableIfNotExist(IMMessage.class);
				dbUtils.createTableIfNotExist(Account.class);
			} catch (DbException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 返回一個帶Content-Type header的requestparams
	 * 
	 * @return
	 */
	public static RequestParams getDefaultPostParams() {
		RequestParams params = new RequestParams(HTTP.UTF_8);
		params.addHeader("Content-Type", "application/json");
		return params;
	}

	/**
	 * 返回一個帶token和lang header的requestparams
	 * 
	 * @return
	 */
	public static RequestParams getDefaultGetParams() {
		try {
			Account account = dbUtils.findFirst(Account.class);
			RequestParams params = new RequestParams(HTTP.UTF_8);
			String token = "";
			if (null != account) {
				token = account.getToken();
			}
			params.addHeader("token", token);
			params.addHeader("lang", "en_US");
			return params;
		} catch (DbException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 獲取token
	 * 
	 * @return
	 */
	public static String getToken() {
		String token = null;
		try {
			Account account = dbUtils.findFirst(Account.class);
			if (null != account) {
				token = account.getToken();
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
		return token;
	}
}
