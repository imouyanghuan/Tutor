package com.tutor.ui.dialog;

import java.io.InputStream;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.hk.tutor.R;
import com.tutor.TutorApplication;
import com.tutor.util.ScreenUtil;
import com.tutor.util.ViewHelper;

/**
 * @author bruce.chen
 * 
 *         2015-10-30
 */
public class TeamConditionsDialog extends Dialog {

	private TextView textView;

	public TeamConditionsDialog(Context context) {
		this(context, R.style.dialog);
	}

	public TeamConditionsDialog(Context context, int theme) {
		super(context, theme);
	}

	public TeamConditionsDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View view = LayoutInflater.from(getContext()).inflate(R.layout.team_condition_dialog, null);
		setContentView(view);
		initView(view);
		LayoutParams params = getWindow().getAttributes();
		params.gravity = Gravity.CENTER;
		params.width = ScreenUtil.getSW(getContext()) - ScreenUtil.dip2Px(getContext(), 30);
		params.height = ScreenUtil.getSH(getContext()) - ScreenUtil.dip2Px(getContext(), 130);
		setCanceledOnTouchOutside(true);
		setText();
	}

	private void initView(View view) {
		ViewHelper.get(view, R.id.team_btn).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dismiss();
			}
		});
		textView = ViewHelper.get(view, R.id.team_tv);
	}

	private void setText() {
		String txt;
		try {
			String fileName;
			if (TutorApplication.isCH()) {
				fileName = "term_ch.txt";
			} else {
				fileName = "term_en.txt";
			}
			// 获得AssetManger 对象, 调用其open 方法取得 对应的inputStream对象
			InputStream in = getContext().getAssets().open(fileName);
			int size = in.available();// 取得数据流的数据大小
			byte[] buffer = new byte[size];
			in.read(buffer);
			in.close();
			txt = new String(buffer);
		} catch (Exception e) {
			txt = "";
		}
		textView.setText(txt);
	}
}
