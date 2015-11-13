package com.tutor.util;

import java.util.Stack;

import android.app.Activity;

/**
 * Screen Manager
 * 
 * 
 */
public class ScreenManager {

	private static Stack<Activity> activityStack;
	private static ScreenManager instance;

	private ScreenManager() {}

	public static ScreenManager getScreenManager() {
		if (instance == null) {
			instance = new ScreenManager();
		}
		return instance;
	}

	public void removeActivity() {
		Activity activity = activityStack.lastElement();
		if (activity != null) {
			activity.finish();
			activity = null;
		}
	}

	public void removeActivity(Activity activity) {
		if (activity != null) {
			activity.finish();
			activityStack.remove(activity);
			activity = null;
		}
	}

	public Activity currentActivity() {
		Activity activity = null;
		if (activityStack != null) {
			if (!activityStack.empty()) {
				activity = activityStack.lastElement();
			}
		}
		return activity;
	}

	public void addActivity(Activity activity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		activityStack.add(activity);
	}

	public void removeAllActivity() {
		while (true) {
			Activity activity = currentActivity();
			if (activity == null) {
				break;
			}
			removeActivity(activity);
		}
	}

	public void removeAllActivityExceptOne(Class<Activity> cls) {
		while (true) {
			Activity activity = currentActivity();
			if (activity == null) {
				break;
			}
			if (activity.getClass().equals(cls)) {
				break;
			}
			removeActivity(activity);
		}
	}
}