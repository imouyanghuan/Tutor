package com.tutor.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.tutor.R;
import com.tutor.TutorApplication;
import com.tutor.params.Constants;
import com.tutor.util.HttpHelper;

/**
 * 歡迎界面
 * 
 * @author bruce.chen
 * 
 */
public class WelcomeActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		handler.sendEmptyMessageDelayed(0, Constants.General.WELCOME_DELAY);
		if (!HttpHelper.isNetworkConnected(this)) {
			toast(R.string.toast_netwrok_disconnected);
		}
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			Intent intent = new Intent();
			// 是否登錄
			boolean isLogin = (Boolean) TutorApplication.settingManager.readSetting(Constants.SharedPreferences.SP_ISLOGIN, false);
			if (isLogin) {
				int role = (Integer) TutorApplication.settingManager.readSetting(Constants.SharedPreferences.SP_ROLE, -1);
				if (Constants.General.ROLE_STUDENT == role) {
					// 學生
					intent.setClass(WelcomeActivity.this, StudentMainActivity.class);
				} else if (Constants.General.ROLE_TUTOR == role) {
					// 老師
					intent.setClass(WelcomeActivity.this, TeacherMainActivity.class);
				}
			} else {
				// 未登錄
				intent.setClass(WelcomeActivity.this, ChoiceRoleActivity.class);
			}
			startActivity(intent);
			finishNoAnim();
		};
	};

	/**
	 * 禁用返回鍵
	 */
	public void onBackPressed() {};

	@Override
	protected void initView() {}
}
