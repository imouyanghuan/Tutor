package com.tutor.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;

import com.hk.tutor.R;
import com.tutor.TutorApplication;
import com.tutor.params.Constants;
import com.tutor.util.ScreenUtil;

/**
 * 選擇身份,學生/家長或老師
 * 
 * @author bruce.chen
 * 
 *         2015-8-20
 */
public class ChoiceRoleActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_choice_role);
		int flag = getIntent().getIntExtra(Constants.IntentExtra.INTENT_EXTRA_TOKENINVALID, -1);
		if (Constants.General.TOKEN_INVALID == flag) {
			toastLong(getString(R.string.toast_token_invalid));
			TutorApplication.isTokenInvalid = false;
		}
		initView();
	}

	@Override
	protected void initView() {
		// 默认箭头向下
		ivSelect = getView(R.id.iv_select);
		ivSelect.setBackgroundResource(R.drawable.select);
		// 角色文字
		tvRole = getView(R.id.tv_role);
		final LinearLayout llChoiceRole = getView(R.id.ll_choice_role);
		llChoiceRole.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ivSelect.setBackgroundResource(R.drawable.select_up);
				showPopupWindow(llChoiceRole);
			}
		});

		getView(R.id.ac_choice_role_btn_oe).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(ChoiceRoleActivity.this, StudentMainActivity.class);
				startActivity(intent);
			}
		});
	}

	/**
	 * 选择角色弹出框
	 * @param llChoiceRole
	 */
	private void showPopupWindow(LinearLayout llChoiceRole) {
		View view = View.inflate(ChoiceRoleActivity.this, R.layout.layout_role_list, null);
		final PopupWindow popupWindow = new PopupWindow(view);
		popupWindow.setFocusable(true);
		int itemWidth = ScreenUtil.getSW(this) - ScreenUtil.dip2Px(ChoiceRoleActivity.this, 40) * 2;
		int itemHeight = ScreenUtil.dip2Px(ChoiceRoleActivity.this, 48) * 3;
		// 设置SelectPicPopupWindow弹出窗体的宽
		popupWindow.setWidth(itemWidth);
		// 设置SelectPicPopupWindow弹出窗体的高
		popupWindow.setHeight(itemHeight);
		// 点击“返回Back”也能使其消失
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new ColorDrawable(R.color.white));
		popupWindow.showAsDropDown(llChoiceRole, 0, 0);
		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				ivSelect.setBackgroundResource(R.drawable.select);
			}
		});
		// 老师点击
		Button btnTutor = (Button)view.findViewById(R.id.btn_role_tutor);
		btnTutor.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tvRole.setText(R.string.label_role_tutor);
				popupWindow.dismiss();
				Intent intent = new Intent();
				intent.setClass(ChoiceRoleActivity.this, LoginActivity.class);
				intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_ROLE, Constants.General.ROLE_TUTOR);
				startActivity(intent);
			}
		});
		// 学生点击
		Button btnStudent = (Button)view.findViewById(R.id.btn_role_student);
		btnStudent.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tvRole.setText(R.string.label_role_student);
				popupWindow.dismiss();
				Intent intent = new Intent();
				intent.setClass(ChoiceRoleActivity.this, LoginActivity.class);
				intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_ROLE, Constants.General.ROLE_STUDENT);
				startActivity(intent);
			}
		});
		// 补习社点击
		Button btnTuitionSchool = (Button)view.findViewById(R.id.btn_role_tuition_school);
		btnTuitionSchool.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tvRole.setText(R.string.label_tutorial_school);
				popupWindow.dismiss();
				Intent intent = new Intent();
				intent.setClass(ChoiceRoleActivity.this, LoginActivity.class);
				intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_ROLE, Constants.General.ROLE_TUITION_SCHOOL);
				startActivity(intent);
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		JPushInterface.resumePush(ChoiceRoleActivity.this);
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
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (Constants.Action.FINISH_LOGINACTIVITY.equals(intent.getAction())) {
				finishNoAnim();
			}
		}
	};
	private ImageView ivSelect;
	private TextView tvRole;
}
