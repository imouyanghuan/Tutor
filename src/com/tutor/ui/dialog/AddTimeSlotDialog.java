package com.tutor.ui.dialog;

import com.tutor.R;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

/**
 * @author bruce.chen
 * 
 *         2015-9-11
 */
public class AddTimeSlotDialog extends Dialog {

	public AddTimeSlotDialog(Context context) {
		this(context, R.style.dialog);
	}

	public AddTimeSlotDialog(Context context, int theme) {
		super(context, theme);
	}

	public AddTimeSlotDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View rootView = LayoutInflater.from(getContext()).inflate(R.layout.add_timeslot_dialog_layout, null, false);
	}
}
