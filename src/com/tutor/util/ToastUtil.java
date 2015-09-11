package com.tutor.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

	private static Toast mToast = null;

	public static void showToast(Context context, String text, int duration) {
		if (mToast == null) {
			mToast = Toast.makeText(context.getApplicationContext(), text, duration);
		} else {
			mToast.setText(text);
			mToast.setDuration(duration);
		}
		mToast.show();
	}

	public static void showToastLong(Context context, String text) {
		if (mToast == null) {
			mToast = Toast.makeText(context.getApplicationContext(), text, Toast.LENGTH_LONG);
		} else {
			mToast.setText(text);
			mToast.setDuration(Toast.LENGTH_LONG);
		}
		mToast.show();
	}

	public static void showToastShort(Context context, String text) {
		if (mToast == null) {
			mToast = Toast.makeText(context.getApplicationContext(), text, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(text);
			mToast.setDuration(Toast.LENGTH_SHORT);
		}
		mToast.show();
	}

	public static void showToastLong(Context context, int resId) {
		if (mToast == null) {
			mToast = Toast.makeText(context.getApplicationContext(), resId, Toast.LENGTH_LONG);
		} else {
			mToast.setText(resId);
			mToast.setDuration(Toast.LENGTH_LONG);
		}
		mToast.show();
	}

	public static void showToastShort(Context context, int resId) {
		if (mToast == null) {
			mToast = Toast.makeText(context.getApplicationContext(), resId, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(resId);
			mToast.setDuration(Toast.LENGTH_SHORT);
		}
		mToast.show();
	}
}
