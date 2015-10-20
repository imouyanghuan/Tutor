package com.tutor.ui.activity;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.hk.tutor.R;
import com.loopj.android.http.RequestParams;
import com.tutor.TutorApplication;
import com.tutor.model.Area;
import com.tutor.model.Area1;
import com.tutor.model.AreaListResult;
import com.tutor.model.UserInfo;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.view.AreaItemLayout;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.HttpHelper;
import com.tutor.util.LogUtils;
import com.tutor.util.ObjectHttpResponseHandler;

/**
 * 选择地区
 * 
 * @author bruce.chen
 * 
 *         2015-9-18
 */
public class SelectAreaActivity extends BaseActivity {

	/** 是否是编辑资料模式 */
	private boolean isEdit;
	private int role = -1;
	private UserInfo userInfo;
	private ArrayList<Area> areas;
	private LinearLayout linearLayout;

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
		setContentView(R.layout.activity_select_area);
		initView();
		getAreas();
	}

	@Override
	protected void initView() {
		TitleBar bar = getView(R.id.title_bar);
		bar.initBack(this);
		if (isEdit) {
			bar.setTitle(R.string.label_edit_area);
		} else {
			if (Constants.General.ROLE_TUTOR == role) {
				bar.setTitle(R.string.label_select_area);
			} else {
				bar.setTitle(R.string.label_select_area_title);
			}
		}
		bar.setRightText(R.string.next, new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 区域列表
				int[] choiceAreas = getChoiceAreas();
				Intent intent = new Intent(SelectAreaActivity.this, SelectTimeSlotActivity.class);
				intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_ISEDIT, isEdit);
				userInfo.setAreaValues(choiceAreas);
				intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_USER_INFO, userInfo);
				startActivity(intent);
			}
		});
		linearLayout = getView(R.id.ac_fill_personal_info_ll_areas);
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

	private void getAreas() {
		if (!HttpHelper.isNetworkConnected(this)) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		showDialogRes(R.string.loading);
		HttpHelper.get(this, ApiUrl.AREALIST, TutorApplication.getHeaders(), new RequestParams(), new ObjectHttpResponseHandler<AreaListResult>(AreaListResult.class) {

			@Override
			public void onFailure(int status, String message) {
				if (0 == status) {
					getAreas();
					return;
				}
				dismissDialog();
				toast(R.string.toast_server_error);
			}

			@Override
			public void onSuccess(AreaListResult t) {
				dismissDialog();
				if (null != t) {
					areas = t.getResult();
					setData(areas);
				} else {
					toast(R.string.toast_server_error);
				}
			}
		});
	}

	private void setData(ArrayList<Area> areas) {
		if (null != areas && 0 != areas.size()) {
			for (Area area : areas) {
				AreaItemLayout areaItemLayout = new AreaItemLayout(SelectAreaActivity.this, area);
				linearLayout.addView(areaItemLayout);
			}
		}
	}

	/**
	 * 获取选中的地区
	 * 
	 * @return
	 */
	private int[] getChoiceAreas() {
		if (null == areas) {
			return new int[] {};
		}
		ArrayList<Area1> selectAreas = new ArrayList<Area1>();
		for (Area area : areas) {
			ArrayList<Area1> area1s = area.getResult();
			for (Area1 area1 : area1s) {
				if (area1.isChecked()) {
					selectAreas.add(area1);
				}
			}
		}
		int size = selectAreas.size();
		if (size > 0) {
			int[] result = new int[size];
			for (int i = 0; i < size; i++) {
				result[i] = selectAreas.get(i).getValue();
			}
			LogUtils.d(result.toString());
			return result;
		}
		return new int[] {};
	}
}
