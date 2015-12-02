package com.tutor.ui.activity;

import org.apache.http.protocol.HTTP;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.webkit.WebView;

import com.hk.tutor.R;
import com.tutor.model.FAQModel;
import com.tutor.params.Constants;
import com.tutor.ui.view.TitleBar;

/**
 * @author jerry.yao
 * 
 *         2015-12-1
 */
public class FAQDetailActivity extends BaseActivity {

	private WebView webView;
	private String faqString;
	private FAQModel faqModel;

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
		faqModel = (FAQModel) intent.getSerializableExtra(Constants.General.FAQ_MODEL);
		faqString = faqModel.getRenderText();
		initView();
	}

	@Override
	protected void initView() {
		TitleBar bar = getView(R.id.title_bar);
		bar.initBack(this);
		bar.setTitle(faqModel.getCategory());
		webView = getView(R.id.webview);
		webView.getSettings().setDefaultTextEncodingName(HTTP.UTF_8);
		if (!TextUtils.isEmpty(faqString)) {
			webView.loadDataWithBaseURL("about:blank", Html.fromHtml(faqString).toString(), "text/html", HTTP.UTF_8, null);
		}
	}
}
