package com.tutor.ui.activity;

import java.net.HttpURLConnection;

import org.apache.http.protocol.HTTP;

import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.webkit.WebView;

import com.hk.tutor.R;
import com.loopj.android.http.RequestParams;
import com.tutor.TutorApplication;
import com.tutor.model.StringResult;
import com.tutor.params.ApiUrl;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.CheckTokenUtils;
import com.tutor.util.HttpHelper;
import com.tutor.util.ObjectHttpResponseHandler;

/**
 * @author bruce.chen
 * 
 *         2015-10-21
 */
public class FAQActivity extends BaseActivity {

	private WebView webView;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_faq);
		initView();
		showDialogRes(R.string.loading);
		getFAQ();
	}

	private void getFAQ() {
		if (!HttpHelper.isNetworkConnected(this)) {
			toast(R.string.toast_netwrok_disconnected);
			dismissDialog();
			return;
		}
		HttpHelper.get(this, ApiUrl.STUDY_ABROAD_FAQ, TutorApplication.getHeaders(), new RequestParams(), new ObjectHttpResponseHandler<StringResult>(StringResult.class) {

			@Override
			public void onFailure(int status, String message) {
				if (0 == status) {
					getFAQ();
					return;
				}
				dismissDialog();
				CheckTokenUtils.checkToken(status);
			}

			@Override
			public void onSuccess(StringResult t) {
				dismissDialog();
				if (HttpURLConnection.HTTP_OK == t.getStatusCode() && !TextUtils.isEmpty(t.getResult())) {
					try {
						webView.loadDataWithBaseURL("about:blank", Html.fromHtml(t.getResult()).toString(), "text/html", HTTP.UTF_8, null);
					} catch (OutOfMemoryError e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	@Override
	protected void initView() {
		TitleBar bar = getView(R.id.title_bar);
		bar.initBack(this);
		bar.setTitle(R.string.label_faq);
		webView = getView(R.id.webview);
		webView.getSettings().setDefaultTextEncodingName(HTTP.UTF_8);
	}
}
