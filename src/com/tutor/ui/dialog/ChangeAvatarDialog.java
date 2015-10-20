package com.tutor.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;

import com.hk.tutor.R;
import com.tutor.params.Constants;
import com.tutor.ui.activity.BaseActivity;
import com.tutor.util.ScreenUtil;
import com.tutor.util.ViewHelper;

/**
 * @author bruce.chen
 * 
 *         2015-9-6
 */
public class ChangeAvatarDialog extends Dialog implements android.view.View.OnClickListener {

	private BaseActivity mContext;
	private Uri uri;

	public ChangeAvatarDialog(BaseActivity activity) {
		this(activity, R.style.dialog);
		mContext = activity;
	}

	public ChangeAvatarDialog(Context context, int theme) {
		super(context, theme);
	}

	public ChangeAvatarDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View view = LayoutInflater.from(mContext).inflate(R.layout.change_avatar_dialog, null);
		setContentView(view);
		initView(view);
		LayoutParams params = getWindow().getAttributes();
		params.gravity = Gravity.BOTTOM;
		params.width = ScreenUtil.getSW(mContext);
		params.height = LayoutParams.WRAP_CONTENT;
		setCanceledOnTouchOutside(true);
	}

	private void initView(View view) {
		ViewHelper.get(view, R.id.change_avatar_btn1).setOnClickListener(this);
		ViewHelper.get(view, R.id.change_avatar_btn2).setOnClickListener(this);
		ViewHelper.get(view, R.id.change_avatar_btn_cancel).setOnClickListener(this);
	}

	public void setUri(Uri uri) {
		this.uri = uri;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.change_avatar_btn1:
				if (null == uri) {
					throw new IllegalArgumentException("uri is null");
				}
				// 調用照相機
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
				mContext.startActivityForResult(intent, Constants.RequestResultCode.TAKE_PHOTO);
				break;
			case R.id.change_avatar_btn2:
				// 調用圖庫
				Intent intentImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				mContext.startActivityForResult(intentImage, Constants.RequestResultCode.GALLERY);
				break;
			case R.id.change_avatar_btn_cancel:
				break;
		}
		dismiss();
	}
}
