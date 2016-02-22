package com.tutor.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.hk.tutor.R;
import com.tutor.TutorApplication;
import com.tutor.model.Account;
import com.tutor.model.UserInfo;
import com.tutor.util.ScreenUtil;
import com.tutor.util.ViewHelper;

/**
 * 活动对话框
 * 
 * @author jerry.yao
 * 
 *         2016-01-08
 */
public class ActivityDialog extends Dialog {

	private Context context;
	private UserInfo userInfo;
	private TextView tvTitle;
	private TextView tvContent;
	private TextView tvCode;
	private Button btnUrl;

	public ActivityDialog(Context context) {
		this(context, R.style.dialog);
	}

	public void setContextAndUserInfo(Context context, UserInfo userInfo) {
		this.context = context;
		this.userInfo = userInfo;
	}

	private void refreshActivityInfo() {
		if (userInfo != null) {
			tvTitle.setText(userInfo.getAlertTitle());
			tvContent.setText(userInfo.getAlertContent());
			btnUrl.setText(userInfo.getAlertLink());
			btnUrl.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
			String code = userInfo.getIdentityCode();
			code = String.format(context.getString(R.string.label_identity_code), code);
			tvCode.setText(code);
		} else {
			Account account = TutorApplication.getAccountDao().load("1");
			if (null != account) {
				tvTitle.setText(account.getAlertTitle());
				tvContent.setText(account.getAlertContent());
				btnUrl.setText(account.getAlertLink());
				btnUrl.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
				String code = account.getIdentityCode();
				code = String.format(context.getString(R.string.label_identity_code), code);
				tvCode.setText(code);
			}
		}
	}

	public ActivityDialog(Context context, int theme) {
		super(context, theme);
	}

	public ActivityDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_activity_dialog, null);
		setContentView(view);
		initView(view);
		LayoutParams params = getWindow().getAttributes();
		params.gravity = Gravity.CENTER;
		params.width = ScreenUtil.getSW(getContext()) - ScreenUtil.dip2Px(getContext(), 30);
		params.height = ScreenUtil.getSH(getContext()) - ScreenUtil.dip2Px(getContext(), 180);
	}

	private void initView(View view) {
		ViewHelper.get(view, R.id.btn_ok).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (onOkClickListener != null) {
					onOkClickListener.onClickListener();
				}
			}
		});
		tvTitle = ViewHelper.get(view, R.id.tv_activity_title);
		tvContent = ViewHelper.get(view, R.id.tv_activity_content);
		tvCode = ViewHelper.get(view, R.id.tv_activity_code);
		btnUrl = ViewHelper.get(view, R.id.btn_activity_url);
		btnUrl.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String url = btnUrl.getText().toString();
				if (!TextUtils.isEmpty(url)) {
					url = url.trim();
				}
				Uri uri = Uri.parse(url);
				Intent it = new Intent(Intent.ACTION_VIEW, uri);
				context.startActivity(it);
			}
		});
		refreshActivityInfo();
	}

	public interface OnOkClickListener {

		public void onClickListener();
	}

	public OnOkClickListener onOkClickListener;

	public void setOnOkClickListener(OnOkClickListener onOkClickListener) {
		this.onOkClickListener = onOkClickListener;
	}
}
