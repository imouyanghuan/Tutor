package com.tutor.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences管理
 * 
 * @author bruce.chen
 * 
 */
public class SettingManager {

	private static SettingManager instance;
	private SharedPreferences settings = null;

	public static SettingManager getInstance(Context context, String fileName) {
		if (instance == null) {
			instance = new SettingManager();
			instance.initialize(context.getApplicationContext(), fileName);
		}
		return instance;
	}

	/**
	 * 初始化设置
	 * 
	 * @param context
	 */
	public void initialize(Context context, String fileName) {
		settings = context.getSharedPreferences(fileName, Context.MODE_PRIVATE); // 如果本地有该文件，就不会新建
	}

	/**
	 * 写入配置
	 * 
	 * @param key
	 * @param value
	 */
	public void writeSetting(String key, Object value) {
		SharedPreferences.Editor editor = settings.edit();
		if (value instanceof Boolean) {
			editor.putBoolean(key, (Boolean) value);
		} else if (value instanceof Integer) {
			editor.putInt(key, (Integer) value);
		} else if (value instanceof Float) {
			editor.putFloat(key, (Float) value);
		} else if (value instanceof Long) {
			editor.putLong(key, (Long) value);
		} else if (value instanceof String) {
			editor.putString(key, (String) value);
		}
		editor.commit();
	}

	/**
	 * 读配置操作
	 * 
	 * @param key
	 *            key
	 * @param res
	 *            value
	 * @param defaultValue
	 *            1.默认2.用于判断类型
	 * @return
	 */
	public Object readSetting(String key, Object defaultValue) {
		Object value = null;
		if (defaultValue instanceof Boolean) {
			value = settings.getBoolean(key, (Boolean) defaultValue);
		} else if (defaultValue instanceof Integer) {
			value = settings.getInt(key, (Integer) defaultValue);
		} else if (defaultValue instanceof Float) {
			value = settings.getFloat(key, (Float) defaultValue);
		} else if (defaultValue instanceof Long) {
			value = settings.getLong(key, (Long) defaultValue);
		} else if (defaultValue instanceof String) {
			value = settings.getString(key, (String) defaultValue);
		}
		return value;
	}

	/**
	 * 清除掉已经有的设置
	 * 
	 * @param name
	 */
	public void deleteSetting(String name) {
		SharedPreferences.Editor editor = settings.edit();
		editor.remove(name);
		editor.commit();
	}

	public void deleteAll() {
		SharedPreferences.Editor editor = settings.edit();
		editor.clear();
		editor.commit();
	}
}