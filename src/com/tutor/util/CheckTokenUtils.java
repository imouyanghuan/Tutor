package com.tutor.util;

import android.content.Intent;

import com.lidroid.xutils.exception.DbException;
import com.tutor.TutorApplication;
import com.tutor.model.Account;
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
			try {
				Account account = TutorApplication.dbUtils.findFirst(Account.class);
				if (null != account) {
					// token过期,发广播结束当前打开的界面
					Intent intent = new Intent();
					intent.setAction(Constants.Action.ACTION_TOKEN_INVALID);
					TutorApplication.instance.sendBroadcast(intent);
					// 跳转登录界面
					Intent intent2 = new Intent(TutorApplication.instance, LoginActivity.class);
					intent2.putExtra(Constants.IntentExtra.INTENT_EXTRA_ROLE, account.getRole());
					intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					TutorApplication.instance.startActivity(intent2);
				}
			} catch (DbException e) {
				e.printStackTrace();
				TutorApplication.isTokenInvalid = false;
			}
		}
	}

	public static void reLogin() {
		if (TutorApplication.isTokenInvalid) {
			return;
		}
		TutorApplication.isTokenInvalid = true;
		try {
			Account account = TutorApplication.dbUtils.findFirst(Account.class);
			if (null != account) {
				// token过期,发广播结束当前打开的界面
				Intent intent = new Intent();
				intent.setAction(Constants.Action.ACTION_TOKEN_INVALID);
				TutorApplication.instance.sendBroadcast(intent);
				// 跳转登录界面
				Intent intent2 = new Intent(TutorApplication.instance, LoginActivity.class);
				intent2.putExtra(Constants.IntentExtra.INTENT_EXTRA_ROLE, account.getRole());
				intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				TutorApplication.instance.startActivity(intent2);
			}
		} catch (DbException e) {
			e.printStackTrace();
			TutorApplication.isTokenInvalid = false;
		}
	}
}
