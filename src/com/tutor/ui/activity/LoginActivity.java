package com.tutor.ui.activity;

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

import com.facebook.utils.FBUser;
import com.facebook.utils.LogInStateListener;
import com.facebook.utils.LoginManager;
import com.lidroid.xutils.exception.DbException;
import com.mssky.mobile.helper.ValidatorHelper;
import com.tutor.R;
import com.tutor.TutorApplication;
import com.tutor.model.Account;
import com.tutor.model.CheckExistResult;
import com.tutor.model.LoginResponseResult;
import com.tutor.model.RegisterLoginModel;
import com.tutor.model.RegisterInfoResult;
import com.tutor.params.Constants;
import com.tutor.service.UserService;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.LogUtils;
import com.tutor.util.UUIDUtils;

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
		email = getView(R.id.ac_login_et_email);
		pswd = getView(R.id.ac_login_et_pswd);
		getView(R.id.ac_login_btn_facebook).setOnClickListener(this);
		getView(R.id.ac_login_btn_login).setOnClickListener(this);
		getView(R.id.ac_login_btn_register).setOnClickListener(this);
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
		String im = emailString.replace("@", "").replace(".", "");
		model.setIMID(im);
		new LoginTask(model).execute();
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		LoginManager.onActivityResult(arg0, arg1, arg2);
	}

	/**
	 * 登錄異步任務
	 * 
	 * @author bruce.chen
	 * 
	 */
	class LoginTask extends AsyncTask<Void, Void, LoginResponseResult> {

		private RegisterLoginModel model;

		public LoginTask(RegisterLoginModel model) {
			this.model = model;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialogRes(R.string.logining);
		}

		@Override
		protected LoginResponseResult doInBackground(Void... params) {
			LoginResponseResult result = UserService.getService().login(model);
			return result;
		}

		@Override
		protected void onPostExecute(LoginResponseResult result) {
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
					Intent intent = new Intent();
					if (Constants.General.ROLE_TUTOR == role) {
						intent.setClass(LoginActivity.this, TeacherMainActivity.class);
					} else {
						intent.setClass(LoginActivity.this, StudentMainActivity.class);
					}
					// 保存信息
					TutorApplication.settingManager.writeSetting(Constants.SharedPreferences.SP_ISLOGIN, true);
					TutorApplication.settingManager.writeSetting(Constants.SharedPreferences.SP_ROLE, role);
					// 保存賬號資料
					Account account = new Account();
					account.set_id(UUIDUtils.getID(6));
					account.setMemberId(result.getResult().getMemberId());
					account.setStatus(0);
					account.setCreatedTime("");
					account.setRole(role);
					account.setEmail(model.getEmail());
					account.setPswd(model.getPassword());
					account.setFacebookId(model.getFBOpenID());
					account.setImAccount(model.getIMID());
					account.setImPswd(model.getIMID());
					account.setToken(result.getResult().getToken());
					try {
						TutorApplication.dbUtils.deleteAll(Account.class);
						TutorApplication.dbUtils.save(account);
					} catch (DbException e) {
						e.printStackTrace();
					}
					// 進入主界面
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
	}

	@Override
	public void OnLoginSuccess(FBUser user, String logType) {
		if (null != user) {
			LogUtils.e(user.toString());
			// 先檢查該賬號是否註冊過,已經註冊就直接登錄,否則註冊
			new CheckAccountExistTask(user.getId()).execute();
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
	 * 驗證Facebook登錄的賬戶是否存在
	 * 
	 * @author bruce.chen
	 * 
	 */
	private class CheckAccountExistTask extends AsyncTask<Void, Void, Boolean> {

		private String id;

		public CheckAccountExistTask(String id) {
			this.id = id;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialogRes(R.string.logining);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			CheckExistResult result = UserService.getService().exist(id, role);
			if (null != result && null != result.getResult()) {
				return result.getResult();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			RegisterLoginModel model = new RegisterLoginModel();
			model.setEmail("");
			model.setPassword("");
			model.setAccountType(role);
			model.setFBOpenID(id);
			String im = getString(R.string.app_name) + id;
			model.setIMID(im);
			dismissDialog();
			if (result) {
				// 存在,直接登錄
				new LoginTask(model).execute();
			} else {
				// 不存在,需要註冊
				new RegisterTask(model).execute();
			}
		}
	}

	/**
	 * 註冊異步任務
	 * 
	 * @author bruce.chen
	 * 
	 */
	class RegisterTask extends AsyncTask<Void, Void, RegisterInfoResult> {

		private RegisterLoginModel model;

		public RegisterTask(RegisterLoginModel model) {
			this.model = model;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialogRes(R.string.logining);
		}

		@Override
		protected RegisterInfoResult doInBackground(Void... params) {
			RegisterInfoResult result = UserService.getService().register(model);
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
							// 發廣播結束登錄界面
							Intent finishLogin = new Intent();
							finishLogin.setAction(Constants.Action.FINISH_LOGINACTIVITY);
							sendBroadcast(finishLogin);
							// 保存賬號資料
							Account account = new Account();
							account.set_id(UUIDUtils.getID(6));
							account.setMemberId(result.getResult().getId());
							account.setRole(role);
							account.setEmail(model.getEmail());
							account.setPswd(model.getPassword());
							account.setImAccount(model.getIMID());
							account.setImPswd(model.getIMID());
							String token = result.getResult().getToken();
							if (null == token || "".equals(token)) {
								token = "XV9hg8WahQGsVvAAHi1FILfgw4FT+wY2tMXazzKDNACA9UhjV4MTAAocdq2JI4VBCdqJTg";
							}
							account.setToken(token);
							try {
								TutorApplication.dbUtils.deleteAll(Account.class);
								TutorApplication.dbUtils.save(account);
							} catch (DbException e) {
								e.printStackTrace();
							}
							// 進入隱私聲明
							Intent intent = new Intent();
							intent.setClass(LoginActivity.this, TeamConditionsActivity.class);
							intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_ROLE, role);
							startActivity(intent);
							finishNoAnim();
							break;
						case Constants.Xmpp.REGISTER_ACCOUNT_EXIST:
							toast(R.string.toast_register_exist);
							break;
						case Constants.Xmpp.REGISTER_ERROR:
							toast(R.string.toast_register_fail);
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
