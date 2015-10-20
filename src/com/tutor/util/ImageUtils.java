package com.tutor.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.ImageView;

import com.hk.tutor.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @author bruce.chen
 * 
 *         2015-9-7
 */
public class ImageUtils {

	private static ImageLoader imageLoader = ImageLoader.getInstance();
	private static DisplayImageOptions options;
	static {
		if (options == null) {
			options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.avatar).showImageForEmptyUri(R.drawable.avatar).showImageOnFail(R.drawable.avatar).cacheInMemory(true)
					.cacheOnDisc(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
		}
	}

	/**
	 * 加载网络图片
	 * 
	 * @param imageView
	 * @param photourl
	 */
	public static void loadImage(ImageView imageView, String photourl) {
		try {
			imageLoader.displayImage(photourl, imageView, options);
		} catch (OutOfMemoryError e) {
			imageView.setBackgroundResource(R.drawable.avatar);
		}
	}

	public static void clearChache() {
		imageLoader.clearMemoryCache();
		imageLoader.clearDiscCache();
	}

	/**
	 * 拍照完成后調用此方法通知系統更細圖庫
	 * 
	 * @param context
	 * @param uri
	 */
	public static void updateGrally(Context context, Uri uri) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		intent.setData(uri);
		context.sendBroadcast(intent);
	}

	/**
	 * 把bitmap转换成字符串
	 * 
	 * @param bitmap
	 * @return
	 */
	public static String Bitmap2String(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		byte[] photoByte = baos.toByteArray();
		LogUtils.e("photoByte.length = " + photoByte.length / 1024 + "k");
		String imgurl = "";
		if (photoByte != null && photoByte.length > 0) {
			imgurl = Base64.encodeToString(photoByte, Base64.DEFAULT);
			LogUtils.e("imgurl.length = " + imgurl.length() / 1024 + "k");
		}
		return imgurl;
	}

	/**
	 * 刪除文件
	 * 
	 * @param filepath
	 */
	public static void deleteFile(String filepath) {
		if (TextUtils.isEmpty(filepath)) {
			return;
		}
		File file = new File(filepath);
		if (null != file && file.exists() && file.isFile()) {
			file.delete();
		}
	}

	/**
	 * 把sdcard上的图片转成string类型
	 * 
	 * @param filepath
	 * @return
	 */
	public static String getBitmapStringFromFile(String filepath) {
		if (TextUtils.isEmpty(filepath)) {
			return null;
		}
		File file = new File(filepath);
		if (null == file || !file.exists()) {
			return null;
		}
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			byte[] photoByte = new byte[(int) file.length()];
			is.read(photoByte);
			LogUtils.e("photoByte.length = " + photoByte.length / 1024 + "k");
			String imgstring = "";
			if (photoByte != null && photoByte.length > 0) {
				imgstring = Base64.encodeToString(photoByte, Base64.DEFAULT);
				LogUtils.e("imgstring.length = " + imgstring.length() / 1024 + "k");
			}
			return imgstring;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			deleteFile(filepath);
		}
		return null;
	}

	/**
	 * 保存压缩后的图片到sdcard上
	 * 
	 * @param filepath
	 */
	public static Bitmap saveBitmap2Sdcard(String filepath, String filepath2, int width, int height) {
		if (TextUtils.isEmpty(filepath2) || TextUtils.isEmpty(filepath)) {
			return null;
		}
		File file = new File(filepath2);
		if (file.exists()) {
			file.delete();
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			Bitmap bitmap = compressImageFromFile(filepath, width, height);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
			fos.flush();
			return bitmap;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if (null != fos) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			deleteFile(filepath);
			deleteFile(filepath2);
		}
		return null;
	}

	/**
	 * 保存压缩后的图片到sdcard上
	 * 
	 * @param oldPath
	 * @param newPath
	 * @param width
	 * @param height
	 */
	public static boolean yaSuoImage(String oldPath, String newPath, int width, int height) {
		if (TextUtils.isEmpty(oldPath) || TextUtils.isEmpty(newPath)) {
			return false;
		}
		File file = new File(newPath);
		if (file.exists()) {
			file.delete();
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			Bitmap bitmap = compressImageFromFile(oldPath, width, height);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
			fos.flush();
			bitmap.recycle();
			bitmap = null;
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if (null != fos) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			deleteFile(oldPath);
		}
		return false;
	}

	/**
	 * 把照片进行压缩
	 */
	private static Bitmap compressImageFromFile(String filepath, int width, int height) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true; // 只读边长,不加载图片到内存中
		Bitmap bitmap = BitmapFactory.decodeFile(filepath, options);
		options.inSampleSize = calculateInSampleSize(options, width, height); // 设置采样率
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		options.inPurgeable = true;
		options.inInputShareable = true;// 当系统内存不够时候图片自动被回收
		bitmap = rotateBitmap(BitmapFactory.decodeFile(filepath, options), filepath);
		return bitmap;
	}

	/**
	 * 旋转图片
	 * 
	 * @param decodeFile
	 * @param filepath
	 * @return
	 */
	private static Bitmap rotateBitmap(Bitmap decodeFile, String filepath) {
		int digree = 0;
		ExifInterface exifInterface = null;
		try {
			exifInterface = new ExifInterface(filepath);
		} catch (IOException e) {
			e.printStackTrace();
			exifInterface = null;
		}
		if (exifInterface != null) {
			// 读取图片中相机方向信息
			int ori = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
			// 计算旋转角度
			switch (ori) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					digree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					digree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					digree = 270;
					break;
				default:
					digree = 0;
					break;
			}
		}
		if (digree != 0) {
			// 旋转图片
			Matrix m = new Matrix();
			m.postRotate(digree);
			decodeFile = Bitmap.createBitmap(decodeFile, 0, 0, decodeFile.getWidth(), decodeFile.getHeight(), m, true);
		}
		return decodeFile;
	}

	// 压缩比例
	private static int calculateInSampleSize(Options options, int width, int height) {
		int bitmap_w = options.outWidth; // 图片本身的宽
		int bitmap_h = options.outHeight;// 图片本身的高
		int proportion = 1; // 压缩比例
		if (bitmap_h > height || bitmap_w > width) {
			final int heightRatio = Math.round((float) bitmap_h / (float) height);
			final int widthRatio = Math.round((float) bitmap_w / (float) width);
			proportion = heightRatio > widthRatio ? heightRatio : widthRatio;
		}
		return proportion;
	}

	private LruCache<String, Bitmap> mMemoryCache;
	private static ImageUtils utils = new ImageUtils();
	private ExecutorService service = Executors.newSingleThreadExecutor();

	private ImageUtils() {
		// 获取应用程序的最大内存
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		// 用最大内存的1/5来存储图片
		final int cacheSize = maxMemory / 5;
		LogUtils.d("cacheSize  ---> " + cacheSize);
		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {

			// 获取每张图片的大小
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
			}
		};
	}

	public static ImageUtils getUtils() {
		return utils;
	}

	/**
	 * 清除緩存
	 */
	public void clearCache() {
		if (null != mMemoryCache)
			mMemoryCache.evictAll();
	}

	/**
	 * 往内存缓存中添加Bitmap
	 * 
	 * @param key
	 * @param bitmap
	 */
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null && bitmap != null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	/**
	 * 加載圖片
	 * 
	 * @param key
	 * @param callBack
	 * @return
	 */
	@SuppressLint("HandlerLeak")
	public Bitmap loadImage(final String key, final CallBack callBack) {
		if (TextUtils.isEmpty(key)) {
			return null;
		}
		Bitmap bitmap = getBitmapFromMemCache(key);
		if (null == bitmap) {
			// final Handler handler = new Handler() {
			//
			// @Override
			// public void handleMessage(Message msg) {
			// super.handleMessage(msg);
			// if (null != callBack) {
			// callBack.onSuccess((Bitmap) msg.obj, key);
			// }
			// }
			// };
			// 若该Bitmap不在内存缓存中，则启用线程去加载图片，并将Bitmap加入到mMemoryCache中
			service.execute(new Runnable() {

				@Override
				public void run() {}
			});
		}
		return bitmap;
	}

	/**
	 * 替換緩存中的圖片
	 * 
	 * @param key
	 * @param bitmap
	 */
	public void replaceBitmap(String key, Bitmap bitmap) {
		removeBitmapFormMemoryCache(key);
		addBitmapToMemoryCache(key, bitmap);
	}

	/**
	 * 移除某圖片
	 * 
	 * @param key
	 */
	public void removeBitmapFormMemoryCache(String key) {
		if (getBitmapFromMemCache(key) != null) {
			mMemoryCache.remove(key);
		}
	}

	/**
	 * 根据key来获取内存中的图片
	 * 
	 * @param key
	 * @return
	 */
	public Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}

	/**
	 * 加载图片的回调接口
	 * 
	 * @author xiaanming
	 * 
	 */
	public interface CallBack {

		/**
		 * 当子线程加载完了图片，将Bitmap和图片路径回调在此方法中
		 * 
		 * @param bitmap
		 * @param path
		 */
		public void onSuccess(Bitmap bitmap, String path);
	}
}
