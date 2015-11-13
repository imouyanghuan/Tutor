package com.tutor.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Looper;

import com.hk.tutor.R;
import com.tutor.TutorApplication;
import com.tutor.model.Log;

/**
 * @author bruce.chen
 * 
 *         2015-10-21
 */
public class HanderException implements UncaughtExceptionHandler {

	private static HanderException instance = new HanderException();
	// 系统默认的UncaughtException处理类
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	// 用来存储设备信息和异常信息
	private Map<String, String> infos = new HashMap<String, String>();
	private Context context;
	// 用于格式化日期
	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private OnCrashException onCrashException;

	private HanderException() {}

	public static HanderException getInstance() {
		return instance;
	}

	public void init(Context context, OnCrashException crashException) {
		this.context = context;
		onCrashException = crashException;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			ScreenManager.getScreenManager().removeAllActivity();
			System.exit(0);
		}
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
	 * 
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false.
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}
		// 使用Toast来显示异常信息
		new Thread() {

			@Override
			public void run() {
				Looper.prepare();
				ToastUtil.showToastLong(context, R.string.unusual_exception);
				Looper.loop();
			}
		}.start();
		// 收集设备参数信息
		infos.put("current date", formatter.format(new Date()));
		collectDeviceInfo(context);
		String error = saveCrashInfo2File(ex);
		LogUtils.d(error);
		// 保存
		Log log = new Log(1, error);
		TutorApplication.getLogDao().insertOrReplace(log);
		if (null != onCrashException) {
			onCrashException.onCrash(error);
		}
		return true;
	}

	/**
	 * 收集设备参数信息
	 * 
	 * @param ctx
	 */
	public void collectDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				String versionName = pi.versionName == null ? "null" : pi.versionName;
				String versionCode = pi.versionCode + "";
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
			}
		} catch (NameNotFoundException e) {
			LogUtils.e("an error occured when collect package info" + e);
		}
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
			} catch (Exception e) {
				LogUtils.e("an error occured when collect crash info" + e);
			}
		}
	}

	/**
	 * 
	 * 
	 * @param ex
	 * @return 错误信息
	 */
	private String saveCrashInfo2File(Throwable ex) {
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\n");
		}
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		sb.append(result);
		return sb.toString();
	}

	public interface OnCrashException {

		public void onCrash(String error);
	}
}
