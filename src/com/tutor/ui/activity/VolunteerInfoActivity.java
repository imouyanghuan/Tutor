package com.tutor.ui.activity;

import org.apache.http.protocol.HTTP;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;

import com.hk.tutor.R;
import com.loopj.android.http.RequestParams;
import com.tutor.TutorApplication;
import com.tutor.model.StringResult;
import com.tutor.model.UserInfo;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.HttpHelper;
import com.tutor.util.ObjectHttpResponseHandler;

/**
 * 义务补习信息
 * 
 * @author jerry.yao
 * 
 *         2015-12-17
 */
public class VolunteerInfoActivity extends BaseActivity {

	private WebView webView;
	private String infoString;
	private int role = -1;
	private boolean isEdit;
	private UserInfo userInfo;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_faq_detail);
		initData();
	}

	private void initData() {
		Intent intent = getIntent();
		if (intent == null) {
			return;
		}
		isEdit = getIntent().getBooleanExtra(Constants.IntentExtra.INTENT_EXTRA_ISEDIT, false);
		userInfo = (UserInfo) getIntent().getSerializableExtra(Constants.IntentExtra.INTENT_EXTRA_USER_INFO);
		if (null != userInfo) {
			role = userInfo.getAccountType();
		}
		if (role == -1) {
			throw new IllegalArgumentException("role is -1");
		}
		getVolunteerInfo();
	}

	private void getVolunteerInfo() {
		if (!HttpHelper.isNetworkConnected(this)) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		showDialogRes(R.string.loading);
		HttpHelper.getHelper().get(ApiUrl.VOLUNTEER_INFO, TutorApplication.getHeaders(), new RequestParams(), new ObjectHttpResponseHandler<StringResult>(StringResult.class) {

			@Override
			public void onFailure(int status, String message) {
				// 连接超时,再获取一次
				if (0 == status) {
					getVolunteerInfo();
					return;
				}
				dismissDialog();
			}

			@Override
			public void onSuccess(StringResult result) {
				dismissDialog();
				if (null != result) {
					infoString = result.getResult();
					initView();
				} else {
					toast(R.string.toast_server_error);
				}
			}
		});
	}

	@Override
	protected void initView() {
		TitleBar bar = getView(R.id.title_bar);
		bar.initBack(this);
		bar.setTitle(R.string.label_volunteer_information);
		bar.setRightText(R.string.next, new OnClickListener() {

			@Override
			public void onClick(View v) {
				next();
			}
		});
		webView = getView(R.id.webview);
		webView.getSettings().setDefaultTextEncodingName(HTTP.UTF_8);
		if (!TextUtils.isEmpty(infoString)) {
			webView.loadDataWithBaseURL("about:blank", Html.fromHtml(infoString).toString(), "text/html", HTTP.UTF_8, null);
		}
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
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (Constants.Action.FINISH_LOGINACTIVITY.equals(intent.getAction())) {
				finishNoAnim();
			}
		}
	};

	private void next() {
		Intent intent = null;
		if (role == Constants.General.ROLE_TUITION_SCHOOL) {
			intent = new Intent(this, SelectAreaActivity.class);
		} else {
			intent = new Intent(this, SelectCourseActivity.class);
		}
		intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_ISEDIT, isEdit);
		intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_USER_INFO, userInfo);
		startActivity(intent);
	}
}
