package com.tutor.ui.activity;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.hk.tutor.R;
import com.loopj.android.http.RequestParams;
import com.tutor.TutorApplication;
import com.tutor.adapter.SelectServiceAreaAdapter;
import com.tutor.model.Area;
import com.tutor.model.Area1;
import com.tutor.model.FirstLevelArea;
import com.tutor.model.FirstLevelAreaListResult;
import com.tutor.model.UserInfo;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.view.CustomGridView;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.HttpHelper;
import com.tutor.util.ObjectHttpResponseHandler;
import com.tutor.util.ViewHelper;

/**
 * 补习社选择地区
 * 
 * @author Jerry.Yao
 * 
 *         2015-12-23
 */
public class SelectTuitionCenterAreaActivity extends BaseActivity {

	/** 是否是编辑资料模式 */
	private boolean isEdit;
	private int role = -1;
	private UserInfo userInfo;

	private ArrayList<FirstLevelArea> serviceAreas = new ArrayList<FirstLevelArea>();
	private CustomGridView gvArea;
	private SelectServiceAreaAdapter serviceAreaAdapter;

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
		setContentView(R.layout.activity_select_tuition_center_area);
		initView();
		getServiceAreas();
	}

	/**
	 * 获取18大区列表
	 */
	private void getServiceAreas() {
		if (!HttpHelper.isNetworkConnected(this)) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		HttpHelper.getHelper().get(ApiUrl.FIRST_LEVEL_AREAS, TutorApplication.getHeaders(), new RequestParams(),
				new ObjectHttpResponseHandler<FirstLevelAreaListResult>(FirstLevelAreaListResult.class) {

					@Override
					public void onFailure(int status, String message) {
						if (0 == status) {
							getServiceAreas();
							return;
						}
						dismissDialog();
						toast(R.string.toast_server_error);
					}

					@Override
					public void onSuccess(FirstLevelAreaListResult result) {
						dismissDialog();
						if (null != result) {
							serviceAreas = result.getResult();
							serviceAreaAdapter = new SelectServiceAreaAdapter(SelectTuitionCenterAreaActivity.this, serviceAreas);
							gvArea.setAdapter(serviceAreaAdapter);
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
				Intent intent = null;
				if (role != Constants.General.ROLE_TUITION_SCHOOL) {
					intent = new Intent(SelectTuitionCenterAreaActivity.this, SelectTimeSlotActivity.class);
				} else {
					intent = new Intent(SelectTuitionCenterAreaActivity.this, TuitionCenterServiceGradesActivity.class);
				}
				// 区域列表
				int[] choiceAreas = getChoiceAreas();
				userInfo.setAreaValues(choiceAreas);
				intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_ISEDIT, isEdit);
				intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_USER_INFO, userInfo);
				startActivity(intent);
			}
		});
		gvArea = getView(R.id.gv_area);
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

	// private void getAreas() {
	// if (!HttpHelper.isNetworkConnected(this)) {
	// toast(R.string.toast_netwrok_disconnected);
	// return;
	// }
	// showDialogRes(R.string.loading);
	// HttpHelper.getHelper().get(ApiUrl.AREALIST,
	// TutorApplication.getHeaders(), new RequestParams(), new
	// ObjectHttpResponseHandler<AreaListResult>(AreaListResult.class) {
	//
	// @Override
	// public void onFailure(int status, String message) {
	// if (0 == status) {
	// getAreas();
	// return;
	// }
	// dismissDialog();
	// toast(R.string.toast_server_error);
	// }
	//
	// @Override
	// public void onSuccess(AreaListResult t) {
	// dismissDialog();
	// if (null != t) {
	// areas = t.getResult();
	// setData();
	// } else {
	// toast(R.string.toast_server_error);
	// }
	// }
	// });
	// }
	// private void setData() {
	// if (null != areas && areas.size() > 0) {
	// AreaAdapter adapter = new AreaAdapter(this, areas);
	// gvArea.setAdapter(adapter);
	// }
	// }
	/**
	 * 获取选中的地区
	 * 
	 * @return
	 */
	private int[] getChoiceAreas() {
		if (null == serviceAreas) {
			return new int[] {};
		}
		ArrayList<FirstLevelArea> selectAreas = new ArrayList<FirstLevelArea>();
		for (FirstLevelArea area : serviceAreas) {
			if (area.isChecked()) {
				selectAreas.add(area);
			}
		}
		int size = selectAreas.size();
		if (size > 0) {
			int[] result = new int[size];
			for (int i = 0; i < size; i++) {
				result[i] = selectAreas.get(i).getValue();
			}
			return result;
		}
		return new int[] {};
	}

	class AreaAdapter extends BaseExpandableListAdapter {

		private LayoutInflater inflater;
		private ArrayList<Area> list;

		public AreaAdapter(Context context, ArrayList<Area> list) {
			inflater = LayoutInflater.from(context);
			this.list = list;
		}

		@Override
		public Area1 getChild(int arg0, int arg1) {
			return getGroup(arg0).getResult().get(arg1);
		}

		@Override
		public long getChildId(int arg0, int arg1) {
			return arg1;
		}

		@Override
		public int getChildrenCount(int arg0) {
			return getGroup(arg0).getResult().size();
		}

		@Override
		public Area getGroup(int arg0) {
			return list.get(arg0);
		}

		@Override
		public int getGroupCount() {
			return list.size();
		}

		@Override
		public long getGroupId(int arg0) {
			return arg0;
		}

		@Override
		public View getChildView(int arg0, int arg1, boolean arg2, View view, ViewGroup arg4) {
			ChildHolder childHolder = null;
			if (null == view) {
				view = inflater.inflate(R.layout.course_item_layout, null);
				childHolder = new ChildHolder();
				childHolder.name = ViewHelper.get(view, R.id.course_item_gv_item_name);
				childHolder.box = ViewHelper.get(view, R.id.course_item_gv_item_checkbox);
				view.setTag(childHolder);
			} else {
				childHolder = (ChildHolder) view.getTag();
			}
			final Area1 area1 = getChild(arg0, arg1);
			childHolder.name.setText(area1.getAddress());
			childHolder.box.setChecked(area1.isChecked());
			childHolder.box.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					area1.setChecked(!area1.isChecked());
					notifyDataSetChanged();
				}
			});
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					area1.setChecked(!area1.isChecked());
					notifyDataSetChanged();
				}
			});
			return view;
		}

		@Override
		public View getGroupView(int arg0, boolean arg1, View view, ViewGroup arg3) {
			GroupHolder groupHolder = null;
			if (null == view) {
				view = inflater.inflate(R.layout.course_grade_item, null);
				groupHolder = new GroupHolder();
				groupHolder.name = ViewHelper.get(view, R.id.course_grade_item_tv);
				view.setTag(groupHolder);
			} else {
				groupHolder = (GroupHolder) view.getTag();
			}
			groupHolder.name.setText(getGroup(arg0).getName());
			return view;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int arg0, int arg1) {
			return false;
		}

		private class GroupHolder {

			public TextView name;
		}

		private class ChildHolder {

			public CheckBox box;
			public TextView name;
		}
	}
}
