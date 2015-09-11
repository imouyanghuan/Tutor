package com.tutor.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.tutor.R;
import com.tutor.params.Constants;
import com.tutor.ui.view.TitleBar;

/**
 * 隱私聲明
 * 
 * @author bruce.chen
 * 
 *         2015-8-24
 */
public class TeamConditionsActivity extends BaseActivity implements OnClickListener, OnCheckedChangeListener {

	private int role;
	private Button button;
	private TextView textView;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		role = getIntent().getIntExtra(Constants.IntentExtra.INTENT_EXTRA_ROLE, -1);
		if (-1 == role) {
			throw new IllegalArgumentException("role is -1");
		}
		setContentView(R.layout.activity_team_conditions);
		initView();
	}

	@Override
	protected void initView() {
		TitleBar bar = getView(R.id.title_bar);
		bar.initBack(this);
		bar.setTitle(R.string.team_conditions);
		textView = getView(R.id.ac_teamcondition_tv);
		button = getView(R.id.ac_teamcondition_btn);
		button.setOnClickListener(this);
		CheckBox box = getView(R.id.ac_teamcondition_cb);
		box.setOnCheckedChangeListener(this);
		textView.setText(R.string.team_conditions_txt);
	}

	@Override
	public void onClick(View v) {
		if (v == button) {
			Intent intent = new Intent(this, FillPersonalInfoActivity.class);
			intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_ROLE, role);
			startActivity(intent);
			finishNoAnim();
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		button.setEnabled(isChecked);
	}
}
