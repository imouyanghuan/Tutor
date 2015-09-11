package com.tutor.ui.activity;

import android.os.Bundle;

import com.tutor.R;
import com.tutor.ui.view.TitleBar;

/**
 * 消息
 * 
 * @author bruce.chen
 * 
 *         2015-8-26
 */
public class NotificationActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_notification);
		initView();
	}

	@Override
	protected void initView() {
		TitleBar bar = getView(R.id.title_bar);
		bar.initBack(this);
		bar.setTitle(R.string.notification);
	}
}
