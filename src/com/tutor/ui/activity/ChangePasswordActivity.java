package com.tutor.ui.activity;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.tutor.R;
import com.tutor.TutorApplication;
import com.tutor.model.ChangePassword;
import com.tutor.model.EditProfileResult;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.HttpHelper;
import com.tutor.util.JsonUtil;
import com.tutor.util.ObjectHttpResponseHandler;

/**
 * 修改密碼或重置密碼
 * 
 * @author bruce.chen
 * 
 *         2015-10-8
 */
public class ChangePasswordActivity extends BaseActivity implements OnClickListener {

	private int flag;
	private String oldPassword;
	//
	private EditText old, new1, new2;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		flag = getIntent().getIntExtra(Constants.IntentExtra.INTENT_EXTRA_PASSWORD_FLAG, Constants.General.CHANGE_PASSWORD);
		oldPassword = getIntent().getStringExtra(Constants.IntentExtra.INTENT_EXTRA_PASSWORD);
		setContentView(R.layout.activity_changepassword);
		initView();
	}

	@Override
	protected void initView() {
		TitleBar bar = getView(R.id.title_bar);
		bar.initBack(this);
		if (Constants.General.FORGET_PASSWORD == flag) {
			bar.setTitle(R.string.set_pswd);
		} else {
			bar.setTitle(R.string.change_pswd);
		}
		old = getView(R.id.ac_change_pswd_et_old);
		new1 = getView(R.id.ac_change_pswd_et_new);
		new2 = getView(R.id.ac_change_pswd_et_new2);
		getView(R.id.ac_change_pswd_btn).setOnClickListener(this);
		if (Constants.General.FORGET_PASSWORD == flag) {
			old.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View arg0) {
		if (Constants.General.CHANGE_PASSWORD == flag) {
			oldPassword = old.getEditableText().toString().trim();
		}
		if (TextUtils.isEmpty(oldPassword)) {
			toast(R.string.toast_pswd_empty);
			return;
		}
		if (oldPassword.length() < 6) {
			toast(R.string.toast_pswd_lenth_error);
			return;
		}
		String newPswd = new1.getEditableText().toString().trim();
		if (TextUtils.isEmpty(newPswd)) {
			toast(R.string.toast_newpswd_empty);
			return;
		}
		if (newPswd.length() < 6) {
			toast(R.string.toast_pswd_lenth_error);
			return;
		}
		String newPswd2 = new2.getEditableText().toString().trim();
		if (!newPswd.equals(newPswd2)) {
			toast(R.string.toast_pswd_repeat_error);
			return;
		}
		ChangePassword changePassword = new ChangePassword(oldPassword, newPswd);
		showDialogRes(R.string.loading);
		change(changePassword);
	}

	private void change(final ChangePassword changePassword) {
		String body = JsonUtil.parseObject2Str(changePassword);
		try {
			StringEntity entity = new StringEntity(body, HTTP.UTF_8);
			HttpHelper.post(this, ApiUrl.CHANGE_PASSWORD, TutorApplication.getHeaders(), entity, new ObjectHttpResponseHandler<EditProfileResult>(EditProfileResult.class) {

				@Override
				public void onFailure(int status, String message) {
					if (0 == status) {
						change(changePassword);
						return;
					}
					dismissDialog();
					toast(message);
				}

				@Override
				public void onSuccess(EditProfileResult t) {
					dismissDialog();
					if (HttpURLConnection.HTTP_OK == t.getStatusCode() && t.getResult()) {
						toast(R.string.success);
						finish();
					} else {
						toast(t.getMessage());
					}
				}
			});
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			dismissDialog();
		}
	}
}
