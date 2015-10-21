package com.tutor.ui.activity;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.Spinner;

import com.hk.tutor.R;
import com.loopj.android.http.RequestParams;
import com.tutor.TutorApplication;
import com.tutor.adapter.AbroadConfigAdapter;
import com.tutor.im.ContactManager;
import com.tutor.im.XMPPConnectionManager;
import com.tutor.model.AbroadConfig;
import com.tutor.model.AbroadConfigListResult;
import com.tutor.model.CourseSelectionSearch;
import com.tutor.model.StringResult;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.view.ObservableScrollView;
import com.tutor.ui.view.ObservableScrollView.ScrollViewListener;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.CheckTokenUtils;
import com.tutor.util.HttpHelper;
import com.tutor.util.JsonUtil;
import com.tutor.util.ObjectHttpResponseHandler;

/**
 * Course Selection
 * 
 * @author jerry.yao
 * 
 *         2015-10-19
 */
public class CourseSelectionActivity extends BaseActivity implements ScrollViewListener, OnClickListener {

	private Spinner spGrade;
	private Spinner spCountry;
	private ObservableScrollView scrollView;
	private FrameLayout flToolbar;
	private boolean isGone = true;
	private CheckBox cbIsPrivacy;
	private ArrayList<AbroadConfig> grades = new ArrayList<AbroadConfig>();
	private ArrayList<AbroadConfig> countrys = new ArrayList<AbroadConfig>();
	private AbroadConfigAdapter gradeAdapter;
	private AbroadConfigAdapter countryAdapter;
	private int curGradeValue = -1;
	private int curCountryValue = -1;
	private boolean isPrivacy = false;
	private WebView webView;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_course_selection);
		initView();
		initData();
	}

	private void initData() {
		getGrade();
		getCountrys();
	}

	/**
	 * 获取年级
	 */
	private void getGrade() {
		if (!HttpHelper.isNetworkConnected(this)) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		RequestParams params = new RequestParams();
		HttpHelper.get(this, ApiUrl.STUDY_ABROAD_GRADES, TutorApplication.getHeaders(), params, new ObjectHttpResponseHandler<AbroadConfigListResult>(AbroadConfigListResult.class) {

			@Override
			public void onFailure(int status, String message) {
				if (0 == status) {
					return;
				}
				dismissDialog();
				CheckTokenUtils.checkToken(status);
				toast(R.string.toast_server_error);
			}

			@Override
			public void onSuccess(AbroadConfigListResult result) {
				dismissDialog();
				if (result.getStatusCode() == HttpURLConnection.HTTP_OK) {
					// TODO
					grades.addAll(result.getResult());
					gradeAdapter.notifyDataSetChanged();
				} else {
					toast(result.getMessage());
				}
			}
		});
	}

	/**
	 * 获取国家
	 */
	private void getCountrys() {
		if (!HttpHelper.isNetworkConnected(this)) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		RequestParams params = new RequestParams();
		HttpHelper.get(this, ApiUrl.STUDY_ABROAD_STATES, TutorApplication.getHeaders(), params, new ObjectHttpResponseHandler<AbroadConfigListResult>(AbroadConfigListResult.class) {

			@Override
			public void onFailure(int status, String message) {
				if (0 == status) {
					return;
				}
				dismissDialog();
				CheckTokenUtils.checkToken(status);
				toast(R.string.toast_server_error);
			}

			@Override
			public void onSuccess(AbroadConfigListResult result) {
				dismissDialog();
				if (result.getStatusCode() == HttpURLConnection.HTTP_OK) {
					// TODO
					countrys.addAll(result.getResult());
					countryAdapter.notifyDataSetChanged();
				} else {
					toast(result.getMessage());
				}
			}
		});
	}

	/**
	 * 获取结果
	 */
	private void getQueryResult() {
		if (!HttpHelper.isNetworkConnected(this)) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		showDialogRes(R.string.loading);
		CourseSelectionSearch search = new CourseSelectionSearch();
		search.setGradeValue(curGradeValue);
		search.setStateValue(curCountryValue);
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
					} else {
						webView.setVisibility(View.GONE);
					}
				} else {
					toast(result.getMessage());
				}
			}
		});
	}

	private ArrayList<String> getCourse() {
		ArrayList<String> data = new ArrayList<String>();
		for (int i = 0; i < 6; i++) {
			data.add("Grade 1 - 1" + i);
		}
		return data;
	}

	private ArrayList<String> getCountry() {
		ArrayList<String> data = new ArrayList<String>();
		for (int i = 0; i < 6; i++) {
			data.add("England " + i);
		}
		return data;
	}

	@Override
	protected void initView() {
		initTitleBar();
		// 年级
		spGrade = getView(R.id.sp_grade);
		gradeAdapter = new AbroadConfigAdapter(this, grades);
		spGrade.setAdapter(gradeAdapter);
		spGrade.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				AbroadConfig config = gradeAdapter.getItem(position);
				curGradeValue = config.getValue();
				if (curGradeValue != -1) {
					getQueryResult();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});
		// 国家
		spCountry = getView(R.id.sp_country);
		countryAdapter = new AbroadConfigAdapter(this, countrys);
		spCountry.setAdapter(countryAdapter);
		spCountry.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				AbroadConfig config = countryAdapter.getItem(position);
				curCountryValue = config.getValue();
				if (curCountryValue != -1) {
					getQueryResult();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});
		// is privacy
		cbIsPrivacy = getView(R.id.cb_is_privacy);
		cbIsPrivacy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isPrivacy = !isPrivacy;
				getQueryResult();
			}
		});
		// scrollview
		scrollView = getView(R.id.scrollView);
		scrollView.setScrollViewListener(this);
		// toolbar
		flToolbar = getView(R.id.fl_toolbar);
		getView(R.id.btn_chat_with_adviser).setOnClickListener(this);
		// webview
		webView = getView(R.id.webView);
		webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
	}

	private void initTitleBar() {
		TitleBar bar = getView(R.id.title_bar);
		bar.initBack(this);
		bar.setTitle(R.string.label_course_selection);
	}

	@Override
	public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
		//
		if (oldy < y && Math.abs(oldy - y) > 20) {
			if (isGone) {
				// 向下滚动
				flToolbar.setVisibility(View.GONE);
				TranslateAnimation goneAnim = new TranslateAnimation(0, 0, 0, 150);
				goneAnim.setDuration(200);
				goneAnim.setFillAfter(true);
				flToolbar.setAnimation(goneAnim);
				isGone = false;
			}
		} else if (oldy > y && Math.abs(oldy - y) > 20) {
			if (!isGone) {
				// 向上滚动
				flToolbar.setVisibility(View.VISIBLE);
				TranslateAnimation visibleAnim = new TranslateAnimation(0, 0, 150, 0);
				visibleAnim.setDuration(200);
				visibleAnim.setFillAfter(true);
				flToolbar.setAnimation(visibleAnim);
				isGone = true;
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_chat_with_adviser:
				// TODO
				// if (!TextUtils.isEmpty(imId)) {
				// boolean isFriend =
				// ContactManager.getManager().addFriend(imId, imId);
				// if (isFriend) {
				// Intent intent = new Intent(StudentInfoActivity.this,
				// ChatActivity.class);
				// intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_MESSAGE_TO,
				// imId + "@" +
				// XMPPConnectionManager.getManager().getServiceName());
				// intent.putExtra(Constants.General.NICKNAME, titleName);
				// intent.putExtra(Constants.General.AVATAR, ApiUrl.DOMAIN +
				// userInfo.getAvatar());
				// startActivity(intent);
				// }
				// }
				break;
			default:
				break;
		}
	}
}
