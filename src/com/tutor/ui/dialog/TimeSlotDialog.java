package com.tutor.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.NumberPicker;
import android.widget.NumberPicker.Formatter;
import android.widget.NumberPicker.OnValueChangeListener;

import com.tutor.R;
import com.tutor.util.ScreenUtil;
import com.tutor.util.ToastUtil;
import com.tutor.util.ViewHelper;

/**
 * @author bruce.chen
 * 
 *         2015-9-11
 */
public class TimeSlotDialog extends Dialog implements android.view.View.OnClickListener, OnValueChangeListener {

	private NumberPicker hourPicker, minutePicker;
	private onTimeSelectedCallBack callBack;
	private int hourIndex, minuteIndex;
	private String hour;
	private String minutes[];

	public TimeSlotDialog(Context context, int hour, int minute, onTimeSelectedCallBack callBack) {
		this(context, R.style.dialog);
		this.callBack = callBack;
		minutes = getContext().getResources().getStringArray(R.array.minutes);
		if (hour == 0 && minute == 0) {
			hourIndex = 7;
			minuteIndex = 3;
			return;
		}
		hourIndex = hour;
		// 分
		String minuteString = minute + "";
		if (minute == 0) {
			minuteString = 0 + minuteString;
		}
		for (int i = 0; i < minutes.length; i++) {
			if (minutes[i].equals(minute + "")) {
				minuteIndex = i;
				break;
			}
		}
	}

	public TimeSlotDialog(Context context, int theme) {
		super(context, theme);
	}

	public TimeSlotDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View rootView = LayoutInflater.from(getContext()).inflate(R.layout.timeslot_dialog_layout, null, false);
		setContentView(rootView);
		initView(rootView);
		// set LayoutParams
		LayoutParams params = getWindow().getAttributes();
		params.gravity = Gravity.BOTTOM;
		params.width = ScreenUtil.getSW(getContext());
		params.height = LayoutParams.WRAP_CONTENT;
		setCanceledOnTouchOutside(true);
	}

	private void initView(View rootView) {
		ViewHelper.get(rootView, R.id.dialog_title_tv_left).setOnClickListener(this);
		ViewHelper.get(rootView, R.id.dialog_title_tv_right).setOnClickListener(this);
		hourPicker = ViewHelper.get(rootView, R.id.ac_fill_personal_time_np_hour);
		hourPicker.setOnValueChangedListener(this);
		minutePicker = ViewHelper.get(rootView, R.id.ac_fill_personal_time_np_minute);
		minutePicker.setOnValueChangedListener(this);
		// 时
		hourPicker.setFormatter(new Formatter() {

			@Override
			public String format(int value) {
				String tmpStr = String.valueOf(value);
				if (value < 10) {
					tmpStr = "0" + tmpStr;
				}
				return tmpStr;
			}
		});
		hourPicker.setMaxValue(23);
		hourPicker.setMinValue(0);
		hourPicker.setValue(hourIndex);
		// 分
		minutePicker.setDisplayedValues(minutes);
		minutePicker.setMinValue(0);
		minutePicker.setMaxValue(minutes.length - 1);
		minutePicker.setValue(minuteIndex);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.dialog_title_tv_right:
				if (null != callBack) {
					hour = String.valueOf(hourIndex);
					if (hourIndex < 10) {
						hour = "0" + hour;
					}
					int i = callBack.onTimeSelected(hour + ":" + minutes[minuteIndex], hourIndex, Integer.parseInt(minutes[minuteIndex]));
					if (0 == i) {
						cancel();
					} else {
						ToastUtil.showToastShort(getContext(), i);
					}
				}
				break;
			case R.id.dialog_title_tv_left:
				cancel();
				break;
			default:
				break;
		}
	}

	@Override
	public void onValueChange(NumberPicker arg0, int arg1, int arg2) {
		switch (arg0.getId()) {
			case R.id.ac_fill_personal_time_np_hour:
				hourIndex = arg2;
				break;
			case R.id.ac_fill_personal_time_np_minute:
				minuteIndex = arg2;
				break;
			default:
				break;
		}
	}

	/**
	 * 添加回调接口
	 * 
	 * @author bruce.chen
	 * 
	 */
	public interface onTimeSelectedCallBack {

		public int onTimeSelected(String time, int hour, int minute);
	}
}
