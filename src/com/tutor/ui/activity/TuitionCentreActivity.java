package com.tutor.ui.activity;

import java.util.HashSet;
import java.util.Set;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import cn.jpush.android.api.JPushInterface;

import com.facebook.login.LoginManager;
import com.hk.tutor.R;
import com.tutor.TutorApplication;
import com.tutor.im.XMPPConnectionManager;
import com.tutor.im.XmppManager;
import com.tutor.model.Account;
import com.tutor.params.Constants;
import com.tutor.service.IMService;
import com.tutor.ui.fragment.BaseFragment;
import com.tutor.ui.fragment.tuitioncenter.TuitionCenterInfoFragment;
import com.tutor.ui.fragment.tuitioncenter.TuitionCenterNotificationFragment;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.ImageUtils;

/**
 * 补习社主界面,管理fragment
 * 
 * @author jerry.yao
 * 
 *         2015-12-10
 */
public class TuitionCentreActivity extends BaseActivity implements OnClickListener {

	/** 頂部菜單 */
	private TitleBar bar;
	/** 底部菜單欄 */
	private RadioGroup mRadioGroup;
	/** 當前選中的fragment */
	private BaseFragment currentFragment;
	/** 补习社个人信息fragment */
	private TuitionCenterInfoFragment tuitionInfoFragment;
	private TuitionCenterNotificationFragment notificationFragment;
	/** fragment操作管理對象 */
	private FragmentTransaction ft;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		// Constants.Xmpp.isChatNotification = false;
		TutorApplication.isMainActivity = true;
		setContentView(R.layout.activity_tuition_center_main);
		Intent intent = getIntent();
		if (intent != null) {
			// 选中通知tab
			isNotification = intent.getBooleanExtra(Constants.General.IS_NOTIFICATION, false);
		}
		initView();
		// 初始化fragment
		initFragment();
		imLogin();
	}

	private void imLogin() {
		// 執行登錄IM
		Account account = TutorApplication.getAccountDao().load("1");
		if (null != account) {
			String accountsString = account.getImAccount();
			String pswd = account.getImPswd();
			new LoginImTask(accountsString, pswd).execute();
			// 设置别名和tag
			Set<String> tags = new HashSet<String>();
			if (TutorApplication.getRole() == Constants.General.ROLE_STUDENT) {
				tags.add(Constants.General.JPUSH_TAG_STUDENT);
			} else {
				tags.add(Constants.General.JPUSH_TAG_TUTOR);
			}
			JPushInterface.setAliasAndTags(TuitionCentreActivity.this, account.getImAccount(), tags);
		}
	}

	/**
	 * 登录IM
	 * 
	 * 
	 */
	private class LoginImTask extends AsyncTask<Void, Void, Integer> {

		private String accountsString;
		private String pswd;

		public LoginImTask(String accountsString, String pswd) {
			this.accountsString = accountsString;
			this.pswd = pswd;
		}

		@Override
		protected Integer doInBackground(Void... params) {
			if (TutorApplication.connectionManager.getConnection().isConnected()) {
				TutorApplication.connectionManager.getConnection().disconnect();
			}
			return XmppManager.getInstance().login(accountsString, pswd);
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if (result == Constants.Xmpp.LOGIN_SECCESS) {
				// 登錄成功,開啟服務
				Intent intent = new Intent(TuitionCentreActivity.this, IMService.class);
				startService(intent);
			} else {
				if (result == Constants.Xmpp.LOGIN_ERROR_ACCOUNT_PASS) {
					pswd = pswd.replaceFirst("t", "T");
				}
				new LoginImTask(accountsString, pswd).execute();
			}
		}
	}

	@Override
	protected void initView() {
		bar = getView(R.id.title_bar);
		mRadioGroup = getView(R.id.ac_main_rg);
		RadioButton rbNotification = getView(R.id.ac_main_rb2);
		if (isNotification) {
			rbNotification.setChecked(true);
		}
		// 底部菜單切換時的操作
		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			// 改變標題欄右按鈕的圖標和監聽
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
					case R.id.ac_main_rb1:
						if (currentFragment != tuitionInfoFragment) {
							ft = getSupportFragmentManager().beginTransaction();
							if (null == tuitionInfoFragment) {
								tuitionInfoFragment = new TuitionCenterInfoFragment();
								ft.add(R.id.ac_main_content_fragment, tuitionInfoFragment, "tuitionInfoFragment");
							}
							ft.hide(currentFragment);
							ft.show(tuitionInfoFragment);
							ft.commit();
							bar.setTitle(R.string.label_tutorial_school);
							bar.setRightText(R.string.log_out, new OnClickListener() {

								@Override
								public void onClick(View arg0) {
									loginOut();
								}
							});
							currentFragment = tuitionInfoFragment;
						}
						break;
					case R.id.ac_main_rb2:
						if (currentFragment != notificationFragment) {
							ft = getSupportFragmentManager().beginTransaction();
							if (null == notificationFragment) {
								notificationFragment = new TuitionCenterNotificationFragment();
								ft.add(R.id.ac_main_content_fragment, notificationFragment, "notificationFragment");
							}
							ft.hide(currentFragment);
							ft.show(notificationFragment);
							ft.commit();
							bar.setTitle(R.string.label_notification);
							bar.setRightTextVisibility(false);
							currentFragment = notificationFragment;
						}
						break;
				}
			}
		});
	}

	private void loginOut() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.log_out);
		builder.setMessage(R.string.lable_log_out);
		builder.setPositiveButton(R.string.cancel, null);
		builder.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				Account account = TutorApplication.getAccountDao().load("1");
				if (account != null && !TextUtils.isEmpty(account.getFacebookId())) {
					try {
						LoginManager.getInstance().logOut();
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
				// TODO 请求退出登录api
				// 斷開IM連接
				XMPPConnectionManager.getManager().disconnect();
				// 停止Push服务
				JPushInterface.stopPush(TuitionCentreActivity.this);
				TutorApplication.settingManager.writeSetting(Constants.SharedPreferences.SP_ISLOGIN, false);
				Intent intent = new Intent(TuitionCentreActivity.this, ChoiceRoleActivity.class);
				startActivity(intent);
				finishNoAnim();
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	/**
	 * 初始化fragment
	 */
	private void initFragment() {
		if (!isNotification) {
			tuitionInfoFragment = new TuitionCenterInfoFragment();
			ft = getSupportFragmentManager().beginTransaction();
			ft.add(R.id.ac_main_content_fragment, tuitionInfoFragment, "tuitionInfoFragment");
			ft.show(tuitionInfoFragment);
			ft.commit();
			bar.setTitle(R.string.label_tutorial_school);
			bar.setRightText(R.string.log_out, new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					loginOut();
				}
			});
			currentFragment = tuitionInfoFragment;
		} else {
			notificationFragment = new TuitionCenterNotificationFragment();
			ft = getSupportFragmentManager().beginTransaction();
			ft.add(R.id.ac_main_content_fragment, notificationFragment, "notificationFragment");
			ft.show(notificationFragment);
			ft.commit();
			bar.setTitle(R.string.label_notification);
			bar.setRightText("", null);
			currentFragment = notificationFragment;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 註冊廣播
		IntentFilter filter = new IntentFilter(Constants.Action.ACTION_NEW_MESSAGE);
		registerReceiver(receiver, filter);
		JPushInterface.resumePush(TuitionCentreActivity.this);
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if (null != tuitionInfoFragment) {
			tuitionInfoFragment.onActivityResult(arg0, arg1, arg2);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}

	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {}
	};

	@Override
	protected void onDestroy() {
		// // 斷開IM連接
		// XMPPConnectionManager.getManager().disconnect();
		// // 停止服務
		// Intent intent = new Intent(this, IMService.class);
		// stopService(intent);
		// Constants.Xmpp.isChatNotification = true;
		// 清空缓存的图片
		ImageUtils.clearChache();
		TutorApplication.isMainActivity = false;
		super.onDestroy();
	}

	private static long lastTime = 0;
	private boolean isNotification;

	/**
	 * 菜單打開是按返回鍵關閉菜單,否則按兩次退出
	 */
	@Override
	public void onBackPressed() {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastTime < 800) {
			super.onBackPressed();
		} else {
			lastTime = currentTime;
			toast(R.string.toast_exit);
		}
	}

	@Override
	public void onClick(View v) {}
}
