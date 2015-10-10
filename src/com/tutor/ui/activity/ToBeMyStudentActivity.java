package com.tutor.ui.activity;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Spinner;

import com.loopj.android.http.RequestParams;
import com.tutor.R;
import com.tutor.TutorApplication;
import com.tutor.adapter.CurrencysAdapter;
import com.tutor.model.Currency;
import com.tutor.model.CurrencyListResult;
import com.tutor.model.EditProfileResult;
import com.tutor.model.Notification;
import com.tutor.model.UserInfo;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.HttpHelper;
import com.tutor.util.JsonUtil;
import com.tutor.util.ObjectHttpResponseHandler;

public class ToBeMyStudentActivity extends BaseActivity implements OnItemSelectedListener {

	private UserInfo userInfo;
	private int priceType;
	private CurrencysAdapter adapter;
	// view
	private EditText nickEditText, courseEditText, frequencyEditText, priceEditText, addressEditText;
	private Spinner priceTypeSpinner;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_to_be_my_student);
		userInfo = (UserInfo) getIntent().getSerializableExtra(Constants.IntentExtra.INTENT_EXTRA_USER_INFO);
		if (null == userInfo) {
			finish();
			return;
		}
		initView();
		// 获取币种
		showDialogRes(R.string.loading);
		getCurrencys();
	}

	private void getCurrencys() {
		HttpHelper.get(this, ApiUrl.CURRENCYS, TutorApplication.getHeaders(), new RequestParams(), new ObjectHttpResponseHandler<CurrencyListResult>(CurrencyListResult.class) {

			@Override
			public void onFailure(int status, String message) {
				if (0 == status) {
					getCurrencys();
					return;
				}
				dismissDialog();
				toast(R.string.toast_server_error);
			}

			@Override
			public void onSuccess(CurrencyListResult t) {
				dismissDialog();
				if (HttpURLConnection.HTTP_OK == t.getStatusCode()) {
					priceType = t.getResult().get(0).getValue();
					adapter = new CurrencysAdapter(ToBeMyStudentActivity.this, t.getResult());
					priceTypeSpinner.setAdapter(adapter);
				} else {
					toast(t.getMessage());
				}
			}
		});
	}

	@Override
	protected void initView() {
		initTitleBar();
		nickEditText = getView(R.id.et_nick);
		courseEditText = getView(R.id.et_course);
		frequencyEditText = getView(R.id.et_frequency);
		priceEditText = getView(R.id.et_price);
		addressEditText = getView(R.id.et_address);
		priceTypeSpinner = getView(R.id.spinner_priceType);
		priceTypeSpinner.setOnItemSelectedListener(this);
		String username = userInfo.getNickName();
		if (TextUtils.isEmpty(username)) {
			username = userInfo.getUserName();
		}
		nickEditText.setText(username);
		nickEditText.setSelection(username.length());
	}

	private void initTitleBar() {
		TitleBar bar = getView(R.id.title_bar);
		bar.setTitle(R.string.to_be_my_student);
		bar.initBack(this);
		bar.setRightText(R.string.btn_save, new OnClickListener() {

			@Override
			public void onClick(View v) {
				onSave();
			}
		});
	}

	// 发送邀请
	private void onSave() {
		// 价格是必填项
		String price = priceEditText.getEditableText().toString().trim();
		if (TextUtils.isEmpty(price)) {
			toast(R.string.toast_price_empty);
			priceEditText.requestFocus();
			return;
		}
		Notification notification = new Notification();
		notification.setSource(TutorApplication.getMemberId());
		notification.setDestination(userInfo.getId());
		notification.setOrientation(Notification.ToBeMyStudent);
		notification.setPricePerHour(price);
		notification.setPriceCurrency(priceType);
		String course = courseEditText.getEditableText().toString().trim();
		if (null == course) {
			course = "";
		}
		notification.setCourse(course);
		String frequency = frequencyEditText.getEditableText().toString().trim();
		if (null == frequency) {
			frequency = "";
		}
		notification.setFrequent(frequency);
		String address = addressEditText.getEditableText().toString().trim();
		if (null == address) {
			address = "";
		}
		notification.setAddress(address);
		String body = JsonUtil.parseObject2Str(notification);
		showDialogRes(R.string.loading);
		send(body);
	}

	private void send(final String body) {
		try {
			StringEntity entity = new StringEntity(body, HTTP.UTF_8);
			HttpHelper.post(this, ApiUrl.TO_BE_MY_STUDENT, TutorApplication.getHeaders(), entity, new ObjectHttpResponseHandler<EditProfileResult>(EditProfileResult.class) {

				@Override
				public void onFailure(int status, String message) {
					if (0 == status) {
						send(body);
						return;
					}
					dismissDialog();
					toast(R.string.toast_server_error);
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
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (adapter != null) {
			Currency currency = adapter.getItem(arg2);
			if (null != currency) {
				priceType = currency.getValue();
			}
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {}
}
