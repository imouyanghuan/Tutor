package com.tutor.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import cn.jpush.android.api.InstrumentedActivity;

import com.loopj.android.http.RequestParams;
import com.tutor.R;
import com.tutor.TutorApplication;
import com.tutor.model.VersionUpDate;
import com.tutor.model.VersionUpDateResult;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.util.HttpHelper;
import com.tutor.util.ObjectHttpResponseHandler;
import com.tutor.util.ToastUtil;

/**
 * 歡迎界面
 * 
 * @author bruce.chen
 * 
 */
public class WelcomeActivity extends InstrumentedActivity {

	private VersionUpDate versionUpDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		// 檢查版本更新
		checkVersion();
		handler.sendEmptyMessageDelayed(0, Constants.General.WELCOME_DELAY);
	}

	private void checkVersion() {
		if (!HttpHelper.isNetworkConnected(this)) {
			ToastUtil.showToastLong(this, R.string.toast_netwrok_disconnected);
			return;
		}
		HttpHelper.get(this, ApiUrl.UPDATEVERSION, TutorApplication.getHeaders(), new RequestParams(), new ObjectHttpResponseHandler<VersionUpDateResult>(VersionUpDateResult.class) {

			@Override
			public void onFailure(int status, String message) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(VersionUpDateResult t) {
				versionUpDate = t.getResult();
			}
		});
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			//
			if (null != versionUpDate) {}
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
			WelcomeActivity.this.finish();
		};
	};

	/**
	 * 禁用返回鍵
	 */
	public void onBackPressed() {};
}
