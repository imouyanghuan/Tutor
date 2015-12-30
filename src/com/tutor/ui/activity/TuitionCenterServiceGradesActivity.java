package com.tutor.ui.activity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.hk.tutor.R;
import com.loopj.android.http.RequestParams;
import com.tutor.TutorApplication;
import com.tutor.adapter.ServiceGradeAdapter;
import com.tutor.model.EditProfileResult;
import com.tutor.model.ServiceGrade;
import com.tutor.model.ServiceGradeListResult;
import com.tutor.model.UserInfo;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.HttpHelper;
import com.tutor.util.JsonUtil;
import com.tutor.util.LogUtils;
import com.tutor.util.ObjectHttpResponseHandler;

/**
 * 补习社服务年级
 * 
 * @author jerry.yao
 * 
 *         2015-12-10
 */
public class TuitionCenterServiceGradesActivity extends BaseActivity implements OnClickListener {

	/** 是否是编辑资料模式 */
	private boolean isEdit;
	private int role = -1;
	private UserInfo userInfo;
	private ArrayList<ServiceGrade> serviceGrades = new ArrayList<ServiceGrade>();
	private ListView lvGrades;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		isEdit = getIntent().getBooleanExtra(Constants.IntentExtra.INTENT_EXTRA_ISEDIT, false);
		userInfo = (UserInfo) getIntent().getSerializableExtra(Constants.IntentExtra.INTENT_EXTRA_USER_INFO);
		if (null != userInfo) {
			role = userInfo.getAccountType();
		}
		if (role == -1) {
			throw new IllegalArgumentException("role is -1");
		}
		setContentView(R.layout.activity_service_grade);
		initView();
		getServiceGrades();
	}

	private void getServiceGrades() {
		if (!HttpHelper.isNetworkConnected(this)) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		showDialogRes(R.string.loading);
		HttpHelper.getHelper().get(ApiUrl.SERVICE_GRADES, TutorApplication.getHeaders(), new RequestParams(), new ObjectHttpResponseHandler<ServiceGradeListResult>(ServiceGradeListResult.class) {

			@Override
			public void onFailure(int status, String message) {
				// 连接超时,再获取一次
				if (0 == status) {
					getServiceGrades();
					return;
				}
				dismissDialog();
				toast(R.string.toast_server_error);
			}

			@Override
			public void onSuccess(ServiceGradeListResult result) {
				dismissDialog();
				if (null != result) {
					serviceGrades = result.getResult();
					if (serviceGrades != null && serviceGrades.size() > 0) {
						if (isEdit && userInfo != null) {
							// 把之前已经选中的设置为checked
							ArrayList<Integer> selectedServiceGrades = userInfo.getServiceGradeValues();
							if (selectedServiceGrades != null && selectedServiceGrades.size() > 0) {
								for (int i = 0; i < selectedServiceGrades.size(); i++) {
									int selectedGradeValue = selectedServiceGrades.get(i);
									for (int j = 0; j < serviceGrades.size(); j++) {
										if (serviceGrades.get(j).getValue() == selectedGradeValue) {
											serviceGrades.get(j).setChecked(true);
											break;
										}
									}
								}
							}
						}
					}
					ServiceGradeAdapter adapter = new ServiceGradeAdapter(TuitionCenterServiceGradesActivity.this, serviceGrades);
					lvGrades.setAdapter(adapter);
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
		if (isEdit) {
			bar.setTitle(R.string.label_edit_service_grade);
		} else {
			bar.setTitle(R.string.label_service_grades);
		}
		bar.setRightText(R.string.btn_submit, new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (null != userInfo) {
					// 筛选选中的年级
					if (serviceGrades != null && serviceGrades.size() > 0) {
						ArrayList<Integer> gradeValues = new ArrayList<Integer>();
						for (int i = 0; i < serviceGrades.size(); i++) {
							if (serviceGrades.get(i).isChecked()) {
								int value = serviceGrades.get(i).getValue();
								gradeValues.add(value);
							}
						}
						userInfo.setServiceGradeValues(gradeValues);
					}
					submit(userInfo);
				}
			}
		});
		lvGrades = getView(R.id.lv_service_grades);
	}

	@Override
	public void onClick(View v) {}

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

	/**
	 * 進入主界面
	 */
	private void go2Main() {
		if (!isEdit || (isEdit && !TutorApplication.isMainActivity)) {
			Intent intent = new Intent();
			intent.setClass(this, TuitionCentreActivity.class);
			// 保存信息
			TutorApplication.settingManager.writeSetting(Constants.SharedPreferences.SP_ISLOGIN, true);
			TutorApplication.settingManager.writeSetting(Constants.SharedPreferences.SP_ROLE, role);
			// 進入补习社主界面
			startActivity(intent);
		}
		// 发送广播结束编辑界面
		Intent finish = new Intent();
		finish.setAction(Constants.Action.FINISH_LOGINACTIVITY);
		sendBroadcast(finish);
	}

	/**
	 * 提信息任务
	 * 
	 */
	private void submit(final UserInfo profile) {
		if (!HttpHelper.isNetworkConnected(this)) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		showDialogRes(R.string.loading);
		String json = JsonUtil.parseObject2Str(profile);
		LogUtils.d(json);
		try {
			StringEntity entity = new StringEntity(json, HTTP.UTF_8);
			HttpHelper.getHelper().put(ApiUrl.TUITION_CENTER_PROFILE_EDIT, TutorApplication.getHeaders(), entity, new ObjectHttpResponseHandler<EditProfileResult>(EditProfileResult.class) {

				@Override
				public void onFailure(int status, String message) {
					if (0 == status) {
						submit(profile);
						return;
					}
					dismissDialog();
					LogUtils.e(message);
					toast(R.string.toast_server_error);
				}

				@Override
				public void onSuccess(EditProfileResult result) {
					dismissDialog();
					if (null != result) {
						if (200 == result.getStatusCode() && result.getResult()) {
							go2Main();
						} else {
							toast(result.getMessage());
						}
					} else {
						toast(R.string.toast_server_error);
					}
				}
			});
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			dismissDialog();
		}
	}
}
