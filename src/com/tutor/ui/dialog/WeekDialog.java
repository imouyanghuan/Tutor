package com.tutor.ui.dialog;

import com.tutor.R;
import com.tutor.util.ScreenUtil;
import com.tutor.util.ViewHelper;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.NumberPicker;

public class WeekDialog extends Dialog {

	private NumberPicker mNumberPicker;
	/** 星期 */
	private String[] weeks;

	public WeekDialog(Context context) {
		this(context, R.style.dialog);
		weeks = getContext().getResources().getStringArray(R.array.weeks);
	}

	public WeekDialog(Context context, int theme) {
		super(context, theme);
	}

	public WeekDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View view = LayoutInflater.from(getContext()).inflate(R.layout.week_dialog_layout, null, false);
		setContentView(view);
		initView(view);
		// set LayoutParams
		LayoutParams params = getWindow().getAttributes();
		params.gravity = Gravity.BOTTOM;
		params.width = ScreenUtil.getSW(getContext());
		params.height = LayoutParams.WRAP_CONTENT;
		setCanceledOnTouchOutside(true);
	}

	private void initView(View view) {
		mNumberPicker = ViewHelper.get(view, R.id.ac_fill_personal_time_np_week);
	}
}
