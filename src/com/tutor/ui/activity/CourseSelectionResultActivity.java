package com.tutor.ui.activity;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.hk.tutor.R;
import com.tutor.TutorApplication;
import com.tutor.model.CourseSelectionSearch;
import com.tutor.model.StringResult;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.CheckTokenUtils;
import com.tutor.util.HttpHelper;
import com.tutor.util.JsonUtil;
import com.tutor.util.ObjectHttpResponseHandler;

/**
 * @author jerry.yao
 * 
 *         2015-10-23
 */
public class CourseSelectionResultActivity extends BaseActivity {

	private WebView webView;
	private RelativeLayout rlNoInfo;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_course_selection_result);
		initView();
		showDialogRes(R.string.loading);
		initData();
	}

	protected void initView() {
		rlNoInfo = getView(R.id.rl_no_info);
		webView = getView(R.id.wv_course_selection);
		webView.getSettings().setDefaultTextEncodingName(HTTP.UTF_8);
	}

	private void initData() {
		Intent intent = getIntent();
		if (intent == null) {
			return;
		}
		int gradeValue = intent.getIntExtra(Constants.General.GRADE_VALUE, -1);
		String gradeString = intent.getStringExtra(Constants.General.GRADE_STRING);
		int countryValue = intent.getIntExtra(Constants.General.COUNTRY_VALUE, -1);
		String country = intent.getStringExtra(Constants.General.COUNTRY_NAME);
		boolean isPrivacy = intent.getBooleanExtra(Constants.General.IS_PRIVACY, false);
		// set title bar
		String titleBar = "";
		if (gradeValue != -1 && countryValue != -1) {
			titleBar = gradeString + " - " + country + " - " + (isPrivacy ? getString(R.string.label_privacy) : getString(R.string.label_non_provacy));
		} else {
			titleBar = getString(R.string.label_course_selection);
		}
		TitleBar bar = getView(R.id.title_bar);
		bar.initBack(this);
		bar.setTitle(titleBar);
		// query result
		getQueryResult(gradeValue, countryValue, isPrivacy);
	}

	/**
	 * 获取结果
	 */
	private void getQueryResult(int gradeValue, int countryValue, boolean isPrivacy) {
		if (!HttpHelper.isNetworkConnected(this)) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		CourseSelectionSearch search = new CourseSelectionSearch();
		search.setGradeValue(gradeValue);
		search.setStateValue(countryValue);
		search.setPrivicy(isPrivacy);
		String body = JsonUtil.parseObject2Str(search);
		StringEntity entity = null;
		try {
			entity = new StringEntity(body, HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpHelper.post(this, ApiUrl.STUDY_ABROAD_COURSE_QUERY, TutorApplication.getHeaders(), entity, new ObjectHttpResponseHandler<StringResult>(StringResult.class) {

			@Override
			public void onFailure(int status, String message) {
				if (0 == status) {
					return;
				}
				dismissDialog();
				CheckTokenUtils.checkToken(status);
				// toast(R.string.toast_server_error);
			}

			@Override
			public void onSuccess(StringResult result) {
				dismissDialog();
				if (result.getStatusCode() == HttpURLConnection.HTTP_OK) {
					// TODO
					String str = result.getResult();
					if (!TextUtils.isEmpty(str)) {
						str = Html.fromHtml(str).toString();
						webView.loadDataWithBaseURL("about:blank", str, "text/html", HTTP.UTF_8, null);
						webView.setVisibility(View.VISIBLE);
						rlNoInfo.setVisibility(View.GONE);
					} else {
						webView.setVisibility(View.GONE);
						rlNoInfo.setVisibility(View.VISIBLE);
					}
				} else {
					toast(result.getMessage());
				}
			}
		});
	}
}
