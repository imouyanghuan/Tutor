package com.tutor.ui.dialog;

import com.tutor.R;
import com.tutor.util.ScreenUtil;
import com.tutor.util.ViewHelper;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.NumberPicker;

public class WeekDialog extends Dialog implements android.view.View.OnClickListener {

	private NumberPicker mNumberPicker;
	/** 星期 */
	private String[] weeks;
	private int selectIndex;
	private OnWeekSelectedCallback callback;

	public WeekDialog(Context context, OnWeekSelectedCallback callback) {
		this(context, R.style.dialog);
		weeks = getContext().getResources().getStringArray(R.array.weeks);
		this.callback = callback;
	}

	public WeekDialog(Context context, int theme) {
		super(context, theme);
	}

	public WeekDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	@SuppressLint("InflateParams")
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
		ViewHelper.get(view, R.id.dialog_title_tv_left).setOnClickListener(this);
		ViewHelper.get(view, R.id.dialog_title_tv_right).setOnClickListener(this);
		mNumberPicker.setDisplayedValues(weeks);
		mNumberPicker.setMinValue(0);
		mNumberPicker.setMaxValue(weeks.length - 1);
		selectIndex = 0;
		mNumberPicker.setValue(selectIndex);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
			case R.id.dialog_title_tv_left:
				cancel();
				break;
			case R.id.dialog_title_tv_right:
				if (null != callback) {
					cancel();
					callback.onWeekSelected(selectIndex, weeks[selectIndex]);
				}
				break;
			default:
				break;
		}
	}

	public int getSelectIndex() {
		return selectIndex;
	}

	public void setSelectIndex(int selectIndex) {
		this.selectIndex = selectIndex;
	}

	public OnWeekSelectedCallback getCallback() {
		return callback;
	}

	public void setCallback(OnWeekSelectedCallback callback) {
		this.callback = callback;
	}

	public interface OnWeekSelectedCallback {

		public void onWeekSelected(int index, String value);
	}
}
