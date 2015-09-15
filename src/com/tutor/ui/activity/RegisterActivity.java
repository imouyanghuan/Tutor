package com.tutor.ui.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.lidroid.xutils.exception.DbException;
import com.mssky.mobile.helper.ValidatorHelper;
import com.tutor.R;
import com.tutor.TutorApplication;
import com.tutor.model.Account;
import com.tutor.model.RegisterInfoResult;
import com.tutor.model.RegisterLoginModel;
import com.tutor.params.Constants;
import com.tutor.service.UserService;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.DateTimeUtil;
import com.tutor.util.UUIDUtils;

/**
 * 註冊界面
 * 
 * @author bruce.chen
 * 
 *         2015-8-20
 */
public class RegisterActivity extends BaseActivity implements OnClickListener {

	private int role;
	private EditText emailEditText, pswdEditText, repeatPswdEditText;
	private Button registerButton;
	private TextView tipTextView;
	private CheckBox checkBox;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		role = getIntent().getIntExtra(Constants.IntentExtra.INTENT_EXTRA_ROLE, -1);
		if (-1 == role) {
			throw new IllegalArgumentException("role is -1");
		}
		setContentView(R.layout.activity_register);
		initView();
	}

	@Override
	protected void initView() {
		TitleBar bar = getView(R.id.title_bar);
		bar.setTitle(R.string.register);
		bar.initBack(this);
		emailEditText = getView(R.id.ac_register_et_email);
		pswdEditText = getView(R.id.ac_register_et_pswd);
		repeatPswdEditText = getView(R.id.ac_register_et_repeatpswd);
		registerButton = getView(R.id.ac_register_btn_register);
		tipTextView = getView(R.id.ac_register_tv_tip);
		checkBox = getView(R.id.ac_register_cb);
		getView(R.id.ac_register_tv_team).setOnClickListener(this);
		registerButton.setOnClickListener(this);
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				registerButton.setEnabled(isChecked);
			}
		});
		if (Constants.General.ROLE_TUTOR == role) {
			registerButton.setText(R.string.btn_register_as_tutor);
			tipTextView.setText(R.string.register_tip_tutor);
		} else if (Constants.General.ROLE_STUDENT == role) {
			registerButton.setText(R.string.btn_register_as_student);
			tipTextView.setText(R.string.register_tip_student);
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.ac_register_btn_register) {
			// 註冊
			String email = emailEditText.getEditableText().toString().trim();
			if (TextUtils.isEmpty(email)) {
				toast(R.string.toast_email_empty);
				return;
			}
			if (!ValidatorHelper.isEmail(email)) {
				toast(R.string.toast_email_formatter_error);
				return;
			}
			String pswd = pswdEditText.getEditableText().toString().trim();
			if (TextUtils.isEmpty(pswd)) {
				toast(R.string.toast_pswd_empty);
				return;
			}
			if (pswd.length() < 6) {
				toast(R.string.toast_pswd_lenth_error);
				return;
			}
			String repeatPswd = repeatPswdEditText.getEditableText().toString().trim();
			if (!repeatPswd.equals(pswd)) {
				toast(R.string.toast_pswd_repeat_error);
				return;
			}
			RegisterLoginModel model = new RegisterLoginModel();
			model.setEmail(email);
			model.setPassword(pswd);
			model.setAccountType(role);
			model.setFBOpenID("");
			String im = email.replace("@", "").replace(".", "");
			model.setIMID(im);
			new RegisterTask(model).execute();
		} else if (R.id.ac_register_tv_team == v.getId()) {
			Intent intent = new Intent(this, TeamConditionsActivity.class);
			startActivity(intent);
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
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//showDialogRes(R.string.registering);
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
							account.setStatus(result.getResult().getStatus());
							account.setCreatedTime(DateTimeUtil.str2Date(result.getResult().getCreatedTime(), DateTimeUtil.FORMART_2));
							account.setRole(role);
							account.setEmail(model.getEmail());
							account.setPswd(model.getPassword());
							account.setImAccount(model.getIMID());
							account.setImPswd(model.getIMID());
							String token = result.getResult().getToken();
							account.setToken(token);
							try {
								TutorApplication.dbUtils.deleteAll(Account.class);
								TutorApplication.dbUtils.save(account);
							} catch (DbException e) {
								e.printStackTrace();
							}
							// 進入完善资料
							Intent intent = new Intent();
							intent.setClass(RegisterActivity.this, FillPersonalInfoActivity.class);
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
