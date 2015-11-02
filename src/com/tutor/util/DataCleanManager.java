package com.tutor.util;

import java.io.File;

import android.content.Context;
import android.os.Environment;

/**
 * @author ChenYouBo
 * @version 创建时间：2014-11-5 上午10:19:32
 * @see 类说明 : 清除数据
 */
public class DataCleanManager {

	/** * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache) * * @param context */
	public static void cleanInternalCache(Context context) {
		deleteFilesByDirectory(context.getCacheDir());
	}

	/** * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases) * * @param context */
	public static void cleanDatabases(Context context) {
		deleteFilesByDirectory(new File("/data/data/" + context.getPackageName() + "/databases"));
	}

	/**
	 * * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs) * * @param
	 * context
	 */
	public static void cleanSharedPreference(Context context, String starteWith) {
		deleteFilesByDirectory(new File("/data/data/" + context.getPackageName() + "/shared_prefs"), starteWith);
	}

	/** * 按名字清除本应用数据库 * * @param context * @param dbName */
	public static void cleanDatabaseByName(Context context, String dbName) {
		context.deleteDatabase(dbName);
	}

	/** * 清除/data/data/com.xxx.xxx/files下的内容 * * @param context */
	public static void cleanFiles(Context context) {
		deleteFilesByDirectory(context.getFilesDir());
	}

	/** * 清除/data/data/com.xxx.xxx/files下的内容 * * @param context */
	public static void cleanAllFiles(Context context) {
		deleteFilesByDirectory(new File("/data/data/" + context.getPackageName()));
		cleanExternalCache(context);
	}

	/**
	 * * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache) * * @param
	 * context
	 */
	public static void cleanExternalCache(Context context) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			deleteFilesByDirectory(context.getExternalCacheDir());
		}
	}

	/** * 清除自定义路径下的文件，使用需小心，请不要误删。而且只支持目录下的文件删除 * * @param filePath */
	public static void cleanCustomCache(String filePath) {
		deleteFilesByDirectory(new File(filePath));
	}

	/** * 删除方法 * @param directory */
	private static void deleteFilesByDirectory(File directory) {
		if (directory != null && directory.exists()) {
			if (directory.isFile()) {
				directory.delete();
				LogUtils.e("delete = " + directory);
				return;
			} else {
				File[] files = directory.listFiles();
				if (files != null && files.length > 0) {
					for (File item : files) {
						deleteFilesByDirectory(item);
						item.delete();
					}
				} else {
					directory.delete();
				}
			}
		}
	}

	/** * 删除方法 * @param directory */
	private static void deleteFilesByDirectory(File directory, String starteWith) {
		if (directory != null && directory.exists()) {
			if (directory.isFile() && directory.getName().startsWith(starteWith)) {
				directory.delete();
				LogUtils.e("delete = " + directory);
				return;
			} else {
				File[] files = directory.listFiles();
				if (files != null && files.length > 0) {
					for (File item : files) {
						deleteFilesByDirectory(item);
						if (item.getName().startsWith(starteWith)) {
							item.delete();
						}
					}
				} else {
					directory.delete();
				}
			}
		}
	}
}
