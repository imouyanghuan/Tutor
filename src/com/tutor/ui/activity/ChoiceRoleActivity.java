package com.tutor.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.hk.tutor.R;
import com.tutor.TutorApplication;
import com.tutor.params.Constants;

/**
 * 選擇身份,學生/家長或老師
 * 
 * @author bruce.chen
 * 
 *         2015-8-20
 */
public class ChoiceRoleActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_choice_role);
		int flag = getIntent().getIntExtra(Constants.IntentExtra.INTENT_EXTRA_TOKENINVALID, -1);
		if (Constants.General.TOKEN_INVALID == flag) {
			toastLong(getString(R.string.toast_token_invalid));
			TutorApplication.isTokenInvalid = false;
		}
		initView();
	}

	@Override
	protected void initView() {
		getView(R.id.ac_choice_role_btn_student).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(ChoiceRoleActivity.this, LoginActivity.class);
				intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_ROLE, Constants.General.ROLE_STUDENT);
				startActivity(intent);
			}
		});
		getView(R.id.ac_choice_role_btn_tutor).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(ChoiceRoleActivity.this, LoginActivity.class);
				intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_ROLE, Constants.General.ROLE_TUTOR);
				startActivity(intent);
			}
		});
		getView(R.id.ac_choice_role_btn_oe).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(ChoiceRoleActivity.this, StudentMainActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	protected void onStart() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.Action.FINISH_LOGINACTIVITY);
		registerReceiver(receiver, filter);
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (Constants.Action.FINISH_LOGINACTIVITY.equals(intent.getAction())) {
				finishNoAnim();
			}
		}
	};
}
