package com.tutor.util;

import android.content.Intent;

import com.tutor.TutorApplication;
import com.tutor.model.ApiResult;
import com.tutor.params.Constants;
import com.tutor.ui.activity.LoginActivity;

/**
 * @author bruce.chen
 * 
 *         2015-9-10
 */
public class CheckTokenUtils {

	public static <T> void checkToken(ApiResult<T> result) {
		if (TutorApplication.isTokenInvalid) {
			return;
		}
		TutorApplication.isTokenInvalid = true;
		if (null != result && Constants.General.TOKEN_INVALID == result.getStatusCode()) {
			if (-1 != TutorApplication.getRole()) {
				// token过期,发广播结束当前打开的界面
				Intent intent = new Intent();
				intent.setAction(Constants.Action.ACTION_TOKEN_INVALID);
				TutorApplication.instance.sendBroadcast(intent);
				// 跳转登录界面
				Intent intent2 = new Intent(TutorApplication.instance, LoginActivity.class);
				intent2.putExtra(Constants.IntentExtra.INTENT_EXTRA_ROLE, TutorApplication.getRole());
				intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent2.putExtra(Constants.IntentExtra.INTENT_EXTRA_TOKENINVALID, Constants.General.TOKEN_INVALID);
				TutorApplication.instance.startActivity(intent2);
			}
		}
	}

	public static void checkToken(int status) {
		if (TutorApplication.isTokenInvalid) {
			return;
		}
		if (Constants.General.TOKEN_INVALID == status) {
			TutorApplication.isTokenInvalid = true;
			if (-1 != TutorApplication.getRole()) {
				// token过期,发广播结束当前打开的界面
				Intent intent = new Intent();
				intent.setAction(Constants.Action.ACTION_TOKEN_INVALID);
				TutorApplication.instance.sendBroadcast(intent);
				// 跳转登录界面
				Intent intent2 = new Intent(TutorApplication.instance, LoginActivity.class);
				intent2.putExtra(Constants.IntentExtra.INTENT_EXTRA_ROLE, TutorApplication.getRole());
				intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent2.putExtra(Constants.IntentExtra.INTENT_EXTRA_TOKENINVALID, Constants.General.TOKEN_INVALID);
				TutorApplication.instance.startActivity(intent2);
			}
		}
	}
}
