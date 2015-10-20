package com.tutor.ui.activity;

import java.net.HttpURLConnection;

import com.hk.tutor.R;
import com.loopj.android.http.RequestParams;
import com.mssky.mobile.helper.ValidatorHelper;
import com.tutor.model.EditProfileResult;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.HttpHelper;
import com.tutor.util.ObjectHttpResponseHandler;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

/**
 * 忘记密码
 * 
 * @author bruce.chen
 * 
 *         2015-10-8
 */
public class ForgetPasswordActivity extends BaseActivity implements OnClickListener {

	private String email;
	private EditText emailEditText;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		email = getIntent().getStringExtra(Constants.IntentExtra.INTENT_EXTRA_EMAIL);
		setContentView(R.layout.activity_forgetpassword);
		initView();
	}

	@Override
	protected void initView() {
		TitleBar bar = getView(R.id.title_bar);
		bar.initBack(this);
		bar.setTitle(R.string.forget_password);
		emailEditText = getView(R.id.ac_login_et_email);
		emailEditText.setText(email);
		getView(R.id.ac_login_btn_login).setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		email = emailEditText.getEditableText().toString().trim();
		if (TextUtils.isEmpty(email)) {
			toast(R.string.toast_email_empty);
			return;
		}
		if (!ValidatorHelper.isEmail(email)) {
			toast(R.string.toast_email_formatter_error);
			return;
		}
		showDialogRes(R.string.loading);
		sendEmail();
	}

	private void sendEmail() {
		RequestParams params = new RequestParams();
		params.put("userName", email);
		HttpHelper.get(this, ApiUrl.FORGET_PASSWORD, null, params, new ObjectHttpResponseHandler<EditProfileResult>(EditProfileResult.class) {

			@Override
			public void onFailure(int status, String message) {
				if (0 == status) {
					sendEmail();
					return;
				}
				dismissDialog();
				toast(R.string.toast_server_error);
			}

			@Override
			public void onSuccess(EditProfileResult t) {
				dismissDialog();
				if (HttpURLConnection.HTTP_OK == t.getStatusCode() && t.getResult()) {
					success();
				} else {
					toast(t.getMessage());
				}
			}
		});
	}

	private void success() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.tips);
		builder.setMessage(R.string.toast_forgetpswd_sendemail_success);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				finish();
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}
}
