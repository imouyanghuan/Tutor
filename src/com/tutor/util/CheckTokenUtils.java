package com.tutor.util;

import android.content.Intent;

import com.tutor.TutorApplication;
import com.tutor.model.ApiResult;
import com.tutor.params.Constants;
import com.tutor.ui.activity.ChoiceRoleActivity;

/**
 * @author bruce.chen
 * 
 *         2015-9-10
 */
public class CheckTokenUtils {

	public static <T> boolean checkToken(ApiResult<T> result) {
		if (TutorApplication.isTokenInvalid) {
			return false;
		}
		if (null != result && Constants.General.TOKEN_INVALID == result.getStatusCode()) {
			if (-1 != TutorApplication.getRole()) {
				TutorApplication.isTokenInvalid = true;
				// token过期,发广播结束当前打开的界面
				Intent intent = new Intent();
				intent.setAction(Constants.Action.ACTION_TOKEN_INVALID);
				TutorApplication.instance.sendBroadcast(intent);
				// 更改配置信息
				TutorApplication.settingManager.writeSetting(Constants.SharedPreferences.SP_ISLOGIN, false);
				// 跳转登录界面
				Intent intent2 = new Intent(TutorApplication.instance, ChoiceRoleActivity.class);// LoginActivity.class
				// intent2.putExtra(Constants.IntentExtra.INTENT_EXTRA_ROLE,
				// TutorApplication.getRole());
				intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent2.putExtra(Constants.IntentExtra.INTENT_EXTRA_TOKENINVALID, Constants.General.TOKEN_INVALID);
				TutorApplication.instance.startActivity(intent2);
				return true;
			}
		}
		return false;
	}

	public static boolean checkToken(int status) {
		if (TutorApplication.isTokenInvalid) {
			return false;
		}
		if (Constants.General.TOKEN_INVALID == status) {
			if (-1 != TutorApplication.getRole()) {
				TutorApplication.isTokenInvalid = true;
				// token过期,发广播结束当前打开的界面
				Intent intent = new Intent();
				intent.setAction(Constants.Action.ACTION_TOKEN_INVALID);
				TutorApplication.instance.sendBroadcast(intent);
				// 更改配置信息
				TutorApplication.settingManager.writeSetting(Constants.SharedPreferences.SP_ISLOGIN, false);
				// 跳转登录界面
				Intent intent2 = new Intent(TutorApplication.instance, ChoiceRoleActivity.class);
				// intent2.putExtra(Constants.IntentExtra.INTENT_EXTRA_ROLE,
				// TutorApplication.getRole());
				intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent2.putExtra(Constants.IntentExtra.INTENT_EXTRA_TOKENINVALID, Constants.General.TOKEN_INVALID);
				TutorApplication.instance.startActivity(intent2);
				return true;
			}
		}
		return false;
	}
}
