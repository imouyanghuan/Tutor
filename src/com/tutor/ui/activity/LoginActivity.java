package com.tutor.ui.activity;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.facebook.utils.FBUser;
import com.facebook.utils.LogInStateListener;
import com.facebook.utils.LoginManager;
import com.loopj.android.http.RequestParams;
import com.mssky.mobile.helper.ValidatorHelper;
import com.tutor.R;
import com.tutor.TutorApplication;
import com.tutor.im.XmppManager;
import com.tutor.model.Account;
import com.tutor.model.CheckExistResult;
import com.tutor.model.LoginResponseResult;
import com.tutor.model.RegisterInfoResult;
import com.tutor.model.RegisterLoginModel;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.HttpHelper;
import com.tutor.util.JsonUtil;
import com.tutor.util.LogUtils;
import com.tutor.util.ObjectHttpResponseHandler;
import com.tutor.util.ScreenUtil;

/**
 * 登錄,老師學生公用
 * 
 * @author bruce.chen
 * 
 *         2015-8-20
 */
public class LoginActivity extends BaseActivity implements OnClickListener, LogInStateListener {

	// 角色
	private int role;
	// 輸入框
	private EditText email, pswd;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		role = getIntent().getIntExtra(Constants.IntentExtra.INTENT_EXTRA_ROLE, -1);
		if (-1 == role) {
			throw new IllegalArgumentException("role is error(-1)");
		}
		int flag = getIntent().getIntExtra(Constants.IntentExtra.INTENT_EXTRA_TOKENINVALID, -1);
		if (Constants.General.TOKEN_INVALID == flag) {
			toastLong(getString(R.string.toast_token_invalid));
		}
		setContentView(R.layout.activity_login);
		initView();
		initFacebook();
	}

	private void initFacebook() {
		LoginManager.initialize(this);
		LoginManager.setFaceBookLoginParams(this, getView(R.id.ac_login_btn_facebook), "user_about_me", this);
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
		LoginManager.OnDestory();
	}

	@Override
	protected void initView() {
		TitleBar bar = getView(R.id.title_bar);
		bar.initBack(this);
		bar.setTitle(R.string.login);
		FrameLayout layout = getView(R.id.ac_login_ll);
		int vh = ScreenUtil.getSH(this) - ScreenUtil.dip2Px(this, 80);
		layout.setMinimumHeight(vh);
		layout.setPadding(0, vh / 10, 0, 0);
		email = getView(R.id.ac_login_et_email);
		pswd = getView(R.id.ac_login_et_pswd);
		getView(R.id.ac_login_btn_facebook).setOnClickListener(this);
		getView(R.id.ac_login_btn_login).setOnClickListener(this);
		getView(R.id.ac_login_btn_register).setOnClickListener(this);
		getView(R.id.ac_login_btn_forgetPassword).setOnClickListener(this);
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (Constants.Action.FINISH_LOGINACTIVITY.equals(intent.getAction())) {
				finishNoAnim();
			}
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.ac_login_btn_login:
				login();
				break;
			case R.id.ac_login_btn_register:
				Intent intent = new Intent();
				intent.setClass(this, RegisterActivity.class);
				intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_ROLE, role);
				startActivity(intent);
				break;
			case R.id.ac_login_btn_forgetPassword:
				// 忘记密码
				String emailString = email.getEditableText().toString().trim();
				if (TextUtils.isEmpty(emailString) || !ValidatorHelper.isEmail(emailString)) {
					emailString = "";
				}
				Intent intent2 = new Intent();
				intent2.setClass(this, ForgetPasswordActivity.class);
				intent2.putExtra(Constants.IntentExtra.INTENT_EXTRA_EMAIL, emailString);
				startActivity(intent2);
				break;
		}
	}

	private void login() {
		String emailString = email.getEditableText().toString().trim();
		if (TextUtils.isEmpty(emailString)) {
			toast(R.string.toast_email_empty);
			return;
		}
		if (!ValidatorHelper.isEmail(emailString)) {
			toast(R.string.toast_email_formatter_error);
			return;
		}
		String password = pswd.getEditableText().toString().trim();
		if (TextUtils.isEmpty(password)) {
			toast(R.string.toast_pswd_empty);
			return;
		}
		if (password.length() < 6) {
			toast(R.string.toast_pswd_lenth_error);
			return;
		}
		RegisterLoginModel model = new RegisterLoginModel();
		model.setEmail(emailString);
		model.setPassword(password);
		model.setAccountType(role);
		model.setFBOpenID("");
		String im = role + emailString.replace("@", "").replace(".", "");
		model.setIMID(im);
		login(model);
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		LoginManager.onActivityResult(arg0, arg1, arg2);
	}

	/**
	 * 登录
	 * 
	 * @param model
	 */
	private void login(final RegisterLoginModel model) {
		if (!HttpHelper.isNetworkConnected(this)) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		showDialogRes(R.string.logining);
		String content = JsonUtil.parseObject2Str(model);
		try {
			StringEntity entity = new StringEntity(content, HTTP.UTF_8);
			HttpHelper.post(this, ApiUrl.LOGIN, null, entity, new ObjectHttpResponseHandler<LoginResponseResult>(LoginResponseResult.class) {

				@Override
				public void onFailure(int status, String message) {
					if (0 == status) {
						login(model);
						return;
					}
					dismissDialog();
					toast(R.string.toast_login_failed);
				}

				@Override
				public void onSuccess(LoginResponseResult result) {
					dismissDialog();
					switch (result.getStatusCode()) {
						case 200:
							if (null == result.getResult()) {
								toast(R.string.toast_server_error);
								return;
							}
							// 保存賬號資料
							Account account = new Account();
							account.setId("1");
							account.setMemberId(result.getResult().getId());
							account.setStatus(0);
							account.setCreatedTime(null);
							account.setRole(role);
							account.setEmail(model.getEmail());
							account.setPswd(model.getPassword());
							account.setFacebookId(model.getFBOpenID());
							account.setImAccount(model.getIMID());
							account.setImPswd(model.getIMID());
							account.setToken(result.getResult().getToken());
							TutorApplication.getAccountDao().insertOrReplace(account);
							// 临时密码登录的需要去设置新密码
							if (2 == result.getResult().getStatus()) {
								Intent i = new Intent(LoginActivity.this, ChangePasswordActivity.class);
								i.putExtra(Constants.IntentExtra.INTENT_EXTRA_PASSWORD_FLAG, Constants.General.FORGET_PASSWORD);
								i.putExtra(Constants.IntentExtra.INTENT_EXTRA_PASSWORD, model.getPassword());
								startActivity(i);
								return;
							}
							// 保存信息
							TutorApplication.settingManager.writeSetting(Constants.SharedPreferences.SP_ISLOGIN, true);
							TutorApplication.settingManager.writeSetting(Constants.SharedPreferences.SP_ROLE, role);
							// 進入主界面
							Intent intent = new Intent();
							if (Constants.General.ROLE_TUTOR == role) {
								intent.setClass(LoginActivity.this, TeacherMainActivity.class);
							} else {
								intent.setClass(LoginActivity.this, StudentMainActivity.class);
							}
							startActivity(intent);
							TutorApplication.isTokenInvalid = false;
							// 發廣播結束前面的activity
							Intent finish = new Intent();
							finish.setAction(Constants.Action.FINISH_LOGINACTIVITY);
							sendBroadcast(finish);
							break;
						default:
							toast(result.getMessage());
							break;
					}
				}
			});
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			toast(R.string.toast_login_failed);
		}
	}

	@Override
	public void OnLoginSuccess(FBUser user, String logType) {
		if (null != user) {
			LogUtils.e(user.toString());
			// 先檢查該賬號是否註冊過,已經註冊就直接登錄,否則註冊
			checkAccountExist(user.getId());
		} else {
			toast(R.string.toast_login_failed);
		}
	}

	@Override
	public void OnLoginError(String error) {
		toast(R.string.toast_login_failed);
		LogUtils.e(error);
	}

	/**
	 * 检查Facebook账户是否存在
	 * 
	 * @param id
	 */
	private void checkAccountExist(final String id) {
		if (!HttpHelper.isNetworkConnected(this)) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		showDialogRes(R.string.logining);
		RequestParams params = new RequestParams();
		params.put("accountType", role + "");
		params.put("FBOpenID", id);
		HttpHelper.get(this, ApiUrl.ACCOUNT_EXIST, null, params, new ObjectHttpResponseHandler<CheckExistResult>(CheckExistResult.class) {

			@Override
			public void onFailure(int status, String message) {
				dismissDialog();
				if (status == 0) {
					checkAccountExist(id);
					return;
				}
				toast(R.string.toast_server_error);
			}

			@Override
			public void onSuccess(CheckExistResult result) {
				dismissDialog();
				if (null != result && null != result.getResult()) {
					RegisterLoginModel model = new RegisterLoginModel();
					model.setEmail("");
					model.setPassword("");
					model.setAccountType(role);
					model.setFBOpenID(id);
					String im = role + getString(R.string.app_name) + id;
					model.setIMID(im);
					if (result.getResult()) {
						// 存在,直接登錄
						login(model);
					} else {
						// 不存在,需要註冊
						register(model);
					}
				}
			}
		});
	}

	/**
	 * 注册
	 * 
	 * @param model
	 */
	private void register(final RegisterLoginModel model) {
		if (!HttpHelper.isNetworkConnected(this)) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		showDialogRes(R.string.logining);
		String content = JsonUtil.parseObject2Str(model);
		try {
			StringEntity entity = new StringEntity(content, HTTP.UTF_8);
			HttpHelper.post(this, ApiUrl.REGISTER, null, entity, new ObjectHttpResponseHandler<RegisterInfoResult>(RegisterInfoResult.class) {

				@Override
				public void onFailure(int status, String message) {
					if (0 == status) {
						register(model);
						return;
					}
					dismissDialog();
					toast(R.string.toast_login_failed);
				}

				@Override
				public void onSuccess(RegisterInfoResult t) {
					if (200 == t.getStatusCode()) {
						new RegisterImTask(model, t).execute();
					} else {
						dismissDialog();
						toast(R.string.toast_login_failed);
					}
				}
			});
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			toast(R.string.toast_login_failed);
		}
	}

	/**
	 * 註冊IM異步任務
	 * 
	 * @author bruce.chen
	 * 
	 */
	class RegisterImTask extends AsyncTask<Void, Void, RegisterInfoResult> {

		RegisterLoginModel model;
		RegisterInfoResult result;

		public RegisterImTask(RegisterLoginModel model, RegisterInfoResult result) {
			this.model = model;
			this.result = result;
		}

		@Override
		protected RegisterInfoResult doInBackground(Void... params) {
			// 註冊IM
			String imAccount = model.getIMID();
			int status = XmppManager.getInstance().register(imAccount, imAccount);
			if (null != result.getResult()) {
				result.getResult().setStatus(status);
			}
			return result;
		}

		@Override
		protected void onPostExecute(RegisterInfoResult result) {
			super.onPostExecute(result);
			dismissDialog();
			if (null == result) {
				toast(R.string.toast_server_error);
				return;
			}
			switch (result.getStatusCode()) {
				case 200:
					if (null == result.getResult()) {
						toast(R.string.toast_server_error);
						return;
					}
					switch (result.getResult().getStatus()) {
						case Constants.Xmpp.REGISTER_SUCCESS:
						case Constants.Xmpp.REGISTER_ACCOUNT_EXIST:
							// 發廣播結束登錄界面
							Intent finishLogin = new Intent();
							finishLogin.setAction(Constants.Action.FINISH_LOGINACTIVITY);
							sendBroadcast(finishLogin);
							// 保存賬號資料
							Account account = new Account();
							account.setId("1");
							account.setMemberId(result.getResult().getId());
							account.setRole(role);
							account.setEmail(model.getEmail());
							account.setPswd(model.getPassword());
							account.setImAccount(model.getIMID());
							account.setImPswd(model.getIMID());
							String token = result.getResult().getToken();
							account.setToken(token);
							TutorApplication.getAccountDao().insertOrReplace(account);
							// 進入隱私聲明
							Intent intent = new Intent();
							intent.setClass(LoginActivity.this, TeamConditionsActivity.class);
							intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_ROLE, role);
							startActivity(intent);
							finishNoAnim();
							break;
						case Constants.Xmpp.REGISTER_ERROR:
							toast(R.string.toast_login_failed);
							break;
					}
					break;
				default:
					toast(result.getMessage());
					break;
			}
		}
	}
}
