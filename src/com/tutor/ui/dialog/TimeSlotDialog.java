package com.tutor.ui.dialog;

import java.util.Arrays;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.tutor.R;
import com.tutor.TutorApplication;
import com.tutor.adapter.TutorBaseAdapter;
import com.tutor.adapter.ViewHolder;
import com.tutor.model.Timeslot;
import com.tutor.util.ScreenUtil;
import com.tutor.util.ViewHelper;

/**
 * @author bruce.chen
 * 
 *         2015-9-11
 */
public class TimeSlotDialog extends Dialog implements android.view.View.OnClickListener, OnItemSelectedListener, OnTimeChangedListener {

	private TimePicker fromTimePicker, toTimePicker;
	private CallBack callBack;
	Timeslot timeslot = new Timeslot();

	public TimeSlotDialog(Context context, CallBack callBack) {
		this(context, R.style.dialog);
		this.callBack = callBack;
		timeslot.setDayOfWeek(0);
		timeslot.setStarHour(8);
		timeslot.setStartMinute(30);
		timeslot.setEndHour(10);
		timeslot.setEndMinute(30);
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
		params.gravity = Gravity.CENTER;
		params.width = ScreenUtil.getSW(getContext()) - ScreenUtil.getSW(getContext()) / 6;
		params.height = ScreenUtil.getSH(getContext()) - ScreenUtil.getSH(getContext()) / 4;
		setCanceledOnTouchOutside(true);
	}

	private void initView(View rootView) {
		ViewHelper.get(rootView, R.id.add_timeslot_dialog_btn_ok).setOnClickListener(this);
		ViewHelper.get(rootView, R.id.add_timeslot_dialog_btn_cancle).setOnClickListener(this);
		fromTimePicker = ViewHelper.get(rootView, R.id.add_timeslot_dialog_date1);
		fromTimePicker.setOnTimeChangedListener(this);
		toTimePicker = ViewHelper.get(rootView, R.id.add_timeslot_dialog_date2);
		toTimePicker.setOnTimeChangedListener(this);
		//
		// 设置时间格式:24小时
		fromTimePicker.setIs24HourView(true);
		toTimePicker.setIs24HourView(true);
		fromTimePicker.setCurrentHour(8);
		fromTimePicker.setCurrentMinute(30);
		toTimePicker.setCurrentHour(10);
		toTimePicker.setCurrentMinute(30);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.add_timeslot_dialog_btn_ok:
				if (null != callBack) {
					if (-1 != TutorApplication.getMemberId()) {
						timeslot.setMemberId(TutorApplication.getMemberId());
						callBack.onAddTimeSlot(timeslot);
						dismiss();
					}
				}
				break;
			case R.id.add_timeslot_dialog_btn_cancle:
				dismiss();
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
	public interface CallBack {

		public void onAddTimeSlot(Timeslot timeslot);
	}

	private class WeekAdapter extends TutorBaseAdapter<String> {

		public WeekAdapter(Context mContext, List<String> mData) {
			super(mContext, mData, R.layout.spinner_item);
		}

		@Override
		protected void convert(ViewHolder holder, String t, int position) {
			holder.setText(R.id.add_timeslot_dialog_spinner_item, t);
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// selectWeek = weeks[arg2];
		timeslot.setDayOfWeek(arg2);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {}

	@Override
	public void onTimeChanged(TimePicker arg0, int hourOfDay, int minute) {
		if (arg0 == fromTimePicker) {
			timeslot.setStarHour(hourOfDay);
			timeslot.setStartMinute(minute);
		} else if (arg0 == toTimePicker) {
			timeslot.setEndHour(hourOfDay);
			timeslot.setEndMinute(minute);
		}
	}
}
