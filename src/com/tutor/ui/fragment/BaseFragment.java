package com.tutor.ui.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.tutor.R;

public class BaseFragment extends Fragment {

	protected static final int SHORT = Toast.LENGTH_SHORT;
	protected static final int LONG = Toast.LENGTH_LONG;
	private Toast toast;
	/** 进度对话框 */
	private ProgressDialog dialog;

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		getActivity().overridePendingTransition(R.anim.start_activity_in, R.anim.start_activity_out);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
		getActivity().overridePendingTransition(R.anim.start_activity_in, R.anim.start_activity_out);
	}

	/**
	 * 显示短提示
	 * 
	 * @param txt
	 */
	protected void toast(String txt) {
		try {
			if (null == toast) {
				toast = Toast.makeText(getActivity(), txt, SHORT);
			}
			toast.setText(txt);
			toast.setDuration(SHORT);
			toast.show();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 显示长提示
	 * 
	 * @param txt
	 */
	protected void toastLong(String txt) {
		try {
			if (null == toast) {
				toast = Toast.makeText(getActivity(), txt, LONG);
			}
			toast.setText(txt);
			toast.setDuration(LONG);
			toast.show();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 显示短提示
	 * 
	 * @param txt
	 */
	protected void toast(int txt) {
		try {
			if (null == toast) {
				toast = Toast.makeText(getActivity(), txt, SHORT);
			}
			toast.setText(txt);
			toast.setDuration(SHORT);
			toast.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 显示对话框
	 * 
	 * @param text
	 */
	protected void showDialog(String text) {
		if (dialog == null) {
			dialog = new ProgressDialog(getActivity());
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
			dialog = new ProgressDialog(getActivity());
			dialog.setCancelable(false);
		}
		dialog.setMessage(getString(resId));
		dialog.show();
	}

	/**
	 * 隐藏对话框
	 */
	protected void dismissDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}
}
