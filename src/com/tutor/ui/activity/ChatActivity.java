package com.tutor.ui.activity;

import com.tutor.params.Constants;

/**
 * @author bruce.chen
 * 
 *         2015-8-28
 */
public class ChatActivity extends BaseActivity {

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onResume() {
		super.onResume();
		Constants.Xmpp.isChatNotification = false;
	}

	@Override
	protected void onPause() {
		super.onPause();
		Constants.Xmpp.isChatNotification = true;
	}
}
