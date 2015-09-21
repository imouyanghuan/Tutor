package com.tutor.util;

import com.tutor.TutorApplication;

import android.util.Log;

public class LogUtils {

	private final static String TAG = "Tutor";

	public static void e(String s) {
		if (TutorApplication.DEBUG) {
			Log.e(TAG, ""+s);
		}
	}

	public static void d(String s) {
		if (TutorApplication.DEBUG) {
			Log.d(TAG, ""+s);
		}
	}

	public static void i(String s) {
		if (TutorApplication.DEBUG) {
			Log.i(TAG, ""+s);
		}
	}
}
