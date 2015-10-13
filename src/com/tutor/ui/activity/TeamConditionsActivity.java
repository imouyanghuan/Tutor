package com.tutor.ui.activity;

import java.io.InputStream;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.tutor.R;
import com.tutor.TutorApplication;
import com.tutor.ui.view.TitleBar;

/**
 * 隱私聲明
 * 
 * @author bruce.chen
 * 
 *         2015-8-24
 */
public class TeamConditionsActivity extends BaseActivity implements OnClickListener, OnCheckedChangeListener {

	private Button button;
	private TextView textView;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_team_conditions);
		initView();
		setText();
	}

	private void setText() {
		String txt;
		try {
			String fileName;
			if (TutorApplication.isCH()) {
				fileName = "term_ch.txt";
			} else {
				fileName = "term_en.txt";
			}
			// 获得AssetManger 对象, 调用其open 方法取得 对应的inputStream对象
			InputStream in = getAssets().open(fileName);
			int size = in.available();// 取得数据流的数据大小
			byte[] buffer = new byte[size];
			in.read(buffer);
			in.close();
			txt = new String(buffer);
		} catch (Exception e) {
			txt = "";
		}
		textView.setText(txt);
	}

	@Override
	protected void initView() {
		TitleBar bar = getView(R.id.title_bar);
		bar.setTitle(R.string.team_conditions);
		textView = getView(R.id.ac_teamcondition_tv);
		button = getView(R.id.ac_teamcondition_btn);
		button.setOnClickListener(this);
		CheckBox box = getView(R.id.ac_teamcondition_cb);
		box.setOnCheckedChangeListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == button) {
			finish();
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		button.setEnabled(isChecked);
	}

	@Override
	public void onBackPressed() {}
}
