package com.tutor.ui.activity;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

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
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mssky.mobile.helper.ValidatorHelper;
import com.tutor.R;
import com.tutor.TutorApplication;
import com.tutor.im.XmppManager;
import com.tutor.model.Account;
import com.tutor.model.RegisterInfoResult;
import com.tutor.model.RegisterLoginModel;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.HttpHelper;
import com.tutor.util.JsonUtil;
import com.tutor.util.ObjectHttpResponseHandler;
import com.tutor.util.ScreenUtil;

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
		FrameLayout layout = getView(R.id.ac_login_ll);
		int vh = ScreenUtil.getSH(this) - ScreenUtil.dip2Px(this, 80);
		layout.setMinimumHeight(vh);
		layout.setPadding(0, vh / 10, 0, 0);
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
		checkBox.setChecked(false);
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
			register(model);
		} else if (R.id.ac_register_tv_team == v.getId()) {
			Intent intent = new Intent(this, TeamConditionsActivity.class);
			startActivity(intent);
		}
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
		showDialogRes(R.string.registering);
		String content = JsonUtil.parseObject2Str(model);
		try {
			StringEntity entity = new StringEntity(content, HTTP.UTF_8);
			HttpHelper.post(this, ApiUrl.REGISTER, null, entity, new ObjectHttpResponseHandler<RegisterInfoResult>(RegisterInfoResult.class) {

				@Override
				public void onFailure(int status, String message) {
					dismissDialog();
					toast(R.string.toast_register_fail);
				}

				@Override
				public void onSuccess(RegisterInfoResult t) {
					if (HttpURLConnection.HTTP_OK == t.getStatusCode()) {
						new RegisterImTask(model, t).execute();
					} else {
						dismissDialog();
						toast(t.getMessage());
					}
				}
			});
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			toast(R.string.toast_register_fail);
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
							// 發廣播結束登錄界面
							Intent finishLogin = new Intent();
							finishLogin.setAction(Constants.Action.FINISH_LOGINACTIVITY);
							sendBroadcast(finishLogin);
							// 保存賬號資料
							Account account = new Account();
							account.setId("1");
							account.setMemberId(result.getResult().getId());
							account.setStatus(result.getResult().getStatus());
							account.setCreatedTime(result.getResult().getCreatedTime());
							account.setRole(role);
							account.setEmail(model.getEmail());
							account.setPswd(model.getPassword());
							account.setImAccount(model.getIMID());
							account.setImPswd(model.getIMID());
							String token = result.getResult().getToken();
							account.setToken(token);
							TutorApplication.getAccountDao().insertOrReplace(account);
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
