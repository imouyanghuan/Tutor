package com.tutor.ui.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.SparseArray;
import android.view.View;
import android.widget.Toast;

import com.tutor.R;
import com.tutor.params.Constants;

/**
 * BaseActivity 其他activity繼承自該類
 * 
 * @author bruce.chen
 * 
 */
public abstract class BaseActivity extends FragmentActivity {

	protected static final int SHORT = Toast.LENGTH_SHORT;
	protected static final int LONG = Toast.LENGTH_LONG;
	private Toast toast;
	/** view 集合 */
	private SparseArray<View> views;
	/** 进度对话框 */
	private ProgressDialog dialog;
	private MyBroadcastReceiver receiver;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		receiver = new MyBroadcastReceiver();
		// 注册广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.Action.ACTION_TOKEN_INVALID);
		registerReceiver(receiver, filter);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 注销广播
		unregisterReceiver(receiver);
	}

	private class MyBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Constants.Action.ACTION_TOKEN_INVALID.equals(action)) {
				finish();
			}
		}
	}

	protected abstract void initView();

	/**
	 * 显示短提示
	 * 
	 * @param txt
	 */
	protected void toast(String txt) {
		if (null == toast) {
			toast = Toast.makeText(this, txt, SHORT);
		}
		toast.setText(txt);
		toast.setDuration(SHORT);
		toast.show();
	}

	/**
	 * 显示长提示
	 * 
	 * @param txt
	 */
	protected void toastLong(String txt) {
		if (null == toast) {
			toast = Toast.makeText(this, txt, LONG);
		}
		toast.setText(txt);
		toast.setDuration(LONG);
		toast.show();
	}

	/**
	 * 显示短提示
	 * 
	 * @param txt
	 */
	protected void toast(int txt) {
		if (null == toast) {
			toast = Toast.makeText(this, txt, SHORT);
		}
		toast.setText(txt);
		toast.setDuration(SHORT);
		toast.show();
	}

	/**
	 * 显示对话框
	 * 
	 * @param text
	 */
	protected void showDialog(String text) {
		if (dialog == null) {
			dialog = new ProgressDialog(this);
			dialog.setCancelable(false);
		}
		dialog.setMessage(text);
		dialog.show();
	}

	/**
	 * 显示对话框
	 * 
	 * @param resId
	 */
	protected void showDialogRes(int resId) {
		if (dialog == null) {
			dialog = new ProgressDialog(this);
			dialog.setCancelable(false);
		}
		dialog.setMessage(getString(resId));
		dialog.show();
	}

	/**
	 * 隐藏对话框
	 */
	protected void dismissDialog() {
		if (dialog != null) {
			dialog.dismiss();
		}
	}

	/**
	 * 获取widget,不用每次都findViewById()
	 * 
	 * @param resId
	 *            组件对应的ID
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <T extends View> T getView(int resId) {
		if (null == views) {
			views = new SparseArray<View>();
		}
		View view = views.get(resId);
		if (null == view) {
			view = findViewById(resId);
			views.put(resId, view);
		}
		T t = null;
		try {
			t = (T) view;
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
		return t;
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		// 设置跳转动画
		overridePendingTransition(R.anim.start_activity_in, R.anim.start_activity_out);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
		// 设置跳转动画
		overridePendingTransition(R.anim.start_activity_in, R.anim.start_activity_out);
	}

	@Override
	public void finish() {
		super.finish();
		// 设置跳转动画
		overridePendingTransition(R.anim.start_activity_in, R.anim.start_activity_out);
	}

	/**
	 * 不伴随结束动画关闭activity
	 */
	public void finishNoAnim() {
		super.finish();
	}
}
