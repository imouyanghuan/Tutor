package com.tutor.ui.activity;

import java.net.HttpURLConnection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.hk.tutor.R;
import com.loopj.android.http.RequestParams;
import com.tutor.TutorApplication;
import com.tutor.adapter.FAQAdapter;
import com.tutor.model.FAQListResult;
import com.tutor.model.FAQModel;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
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

	private ListView lvFaq;
	private FAQAdapter faqAdapter;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_faq);
		initView();
		getFAQList();
	}

	private void getFAQList() {
		if (!HttpHelper.isNetworkConnected(this)) {
			toast(R.string.toast_netwrok_disconnected);
			dismissDialog();
			return;
		}
		showDialogRes(R.string.loading);
		HttpHelper.getHelper().get(ApiUrl.STUDY_ABROAD_FAQ, TutorApplication.getHeaders(), new RequestParams(), new ObjectHttpResponseHandler<FAQListResult>(FAQListResult.class) {

			@Override
			public void onFailure(int status, String message) {
				if (0 == status) {
					getFAQList();
					return;
				}
				dismissDialog();
				CheckTokenUtils.checkToken(status);
			}

			@Override
			public void onSuccess(FAQListResult result) {
				dismissDialog();
				if (HttpURLConnection.HTTP_OK == result.getStatusCode() && result.getResult() != null) {
					faqAdapter = new FAQAdapter(FAQActivity.this, result.getResult());
					lvFaq.setAdapter(faqAdapter);
				}
			}
		});
	}

	@Override
	protected void initView() {
		TitleBar bar = getView(R.id.title_bar);
		bar.initBack(this);
		bar.setTitle(R.string.label_faq);
		lvFaq = getView(R.id.lv_faq);
		lvFaq.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				FAQModel faqModel = faqAdapter.getItem(position);
				if (faqModel != null) {
					Intent intent = new Intent(FAQActivity.this, FAQDetailActivity.class);
					intent.putExtra(Constants.General.FAQ_MODEL, faqModel);
					startActivity(intent);
				}
			}
		});
	}
}
