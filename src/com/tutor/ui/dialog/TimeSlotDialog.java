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

	public TimeSlotDialog(Context context, onTimeSelectedCallBack callBack) {
		this(context, R.style.dialog);
		this.callBack = callBack;
		minutes = getContext().getResources().getStringArray(R.array.minutes);
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
		hourPicker.setValue(7);
		// 分
		minutePicker.setDisplayedValues(minutes);
		minutePicker.setMinValue(0);
		minutePicker.setMaxValue(minutes.length - 1);
		minutePicker.setValue(3);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.dialog_title_tv_right:
				if (null != callBack) {
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

	public void setParams(int hour, int minute) {
		hourIndex = hour;
		hourPicker.setValue(hourIndex);
		// 分
		for (int i = 0; i < minutes.length; i++) {
			if (minutes[i].equals(minute + "")) {
				minuteIndex = i;
				minutePicker.setValue(minuteIndex);
				break;
			}
		}
	}

	@Override
	public void onValueChange(NumberPicker arg0, int arg1, int arg2) {
		switch (arg0.getId()) {
			case R.id.ac_fill_personal_time_np_hour:
				hourIndex = arg2;
				hour = String.valueOf(arg2);
				if (arg2 < 10) {
					hour = "0" + hour;
				}
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

	public onTimeSelectedCallBack getCallBack() {
		return callBack;
	}

	public void setCallBack(onTimeSelectedCallBack callBack) {
		this.callBack = callBack;
	}

	public int getHourIndex() {
		return hourIndex;
	}

	public void setHourIndex(int hourIndex) {
		this.hourIndex = hourIndex;
		hourPicker.setValue(hourIndex);
	}

	public int getMinuteIndex() {
		return minuteIndex;
	}

	public void setMinuteIndex(int minuteIndex) {
		this.minuteIndex = minuteIndex;
		minutePicker.setValue(minuteIndex);
	}
}
