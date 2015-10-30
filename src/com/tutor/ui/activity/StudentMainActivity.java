package com.tutor.ui.activity;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;
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
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.hk.tutor.R;
import com.mssky.mobile.helper.SharePrefUtil;
import com.tutor.TutorApplication;
import com.tutor.im.IMMessageManager;
import com.tutor.im.XMPPConnectionManager;
import com.tutor.im.XmppManager;
import com.tutor.model.Account;
import com.tutor.params.Constants;
import com.tutor.service.IMService;
import com.tutor.ui.fragment.BaseFragment;
import com.tutor.ui.fragment.student.FindTeacherFragment;
import com.tutor.ui.fragment.student.MyFragment;
import com.tutor.ui.fragment.student.MyTeacherFragment;
import com.tutor.ui.fragment.student.OverseasEducationFragment;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.ImageUtils;
import com.tutor.util.ScreenUtil;

/**
 * 學生首頁,管理fragment
 * 
 * @author bruce.chen
 * 
 *         2015-8-20
 */
public class StudentMainActivity extends BaseActivity implements OnClickListener {

	/** 滑動菜單 */
	private MenuDrawer mMenuDrawer;
	/** 頂部菜單 */
	private TitleBar bar;
	/** 底部菜單欄 */
	private RadioGroup mRadioGroup;
	/** 當前選中的fragment */
	private BaseFragment currentFragment;
	/** 其他fragment */
	private FindTeacherFragment findTeacherFragment;
	private OverseasEducationFragment overseasEducationFragment;
	private MyTeacherFragment teacherFragment;
	private MyFragment myFragment;
	/** fragment操作管理對象 */
	private FragmentTransaction ft;
	// 显示消息条数
	private TextView msgCount;
	private long count = 0;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		// Constants.Xmpp.isChatNotification = false;
		TutorApplication.isMainActivity = true;
		mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.BEHIND, Position.LEFT, MenuDrawer.MENU_DRAG_WINDOW);
		mMenuDrawer.setContentView(R.layout.activity_student_main);
		mMenuDrawer.setMenuView(R.layout.menu_layout);
		mMenuDrawer.setMenuSize(ScreenUtil.getSW(this) * 3 / 10);
		initView();
		// 初始化fragment
		initFragment();
		new LoginImTask().execute();
	}

	@Override
	protected void initView() {
		bar = getView(R.id.title_bar);
		bar.setTitle(R.string.findteacher);
		bar.setLeftButton(R.drawable.menu_selector, new OnClickListener() {

			@Override
			public void onClick(View v) {
				mMenuDrawer.openMenu();
			}
		});
		mRadioGroup = getView(R.id.ac_main_rg);
		// 底部菜單切換時的操作
		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			// 改變標題欄右按鈕的圖標和監聽
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
					case R.id.ac_main_rb1:
						if (currentFragment != findTeacherFragment) {
							ft = getSupportFragmentManager().beginTransaction();
							if (null == findTeacherFragment) {
								findTeacherFragment = new FindTeacherFragment();
								ft.add(R.id.ac_main_content_fragment, findTeacherFragment, "findTeacherFragment");
							}
							ft.hide(currentFragment);
							ft.show(findTeacherFragment);
							ft.commit();
							findTeacherFragment.reFresh();
							bar.setTitle(R.string.findteacher);
							bar.setRightTextVisibility(false);
							currentFragment = findTeacherFragment;
						}
						break;
					case R.id.ac_main_rb2:
						if (currentFragment != overseasEducationFragment) {
							ft = getSupportFragmentManager().beginTransaction();
							if (null == overseasEducationFragment) {
								overseasEducationFragment = new OverseasEducationFragment();
								ft.add(R.id.ac_main_content_fragment, overseasEducationFragment, "overseasEducationFragment");
							}
							ft.hide(currentFragment);
							ft.show(overseasEducationFragment);
							ft.commit();
							bar.setTitle(R.string.study_abroad);
							bar.setRightTextVisibility(false);
							currentFragment = overseasEducationFragment;
						}
						break;
					case R.id.ac_main_rb3:
						if (currentFragment != teacherFragment) {
							ft = getSupportFragmentManager().beginTransaction();
							if (null == teacherFragment) {
								teacherFragment = new MyTeacherFragment();
								ft.add(R.id.ac_main_content_fragment, teacherFragment, "teacherFragment");
							}
							ft.hide(currentFragment);
							ft.show(teacherFragment);
							ft.commit();
							bar.setTitle(R.string.myteachers);
							bar.setRightTextVisibility(false);
							currentFragment = teacherFragment;
						}
						break;
					case R.id.ac_main_rb4:
						if (currentFragment != myFragment) {
							ft = getSupportFragmentManager().beginTransaction();
							if (null == myFragment) {
								myFragment = new MyFragment();
								ft.add(R.id.ac_main_content_fragment, myFragment, "myFragment");
							}
							ft.hide(currentFragment);
							ft.show(myFragment);
							ft.commit();
							bar.setTitle(R.string.my);
							bar.setRightText(R.string.log_out, new OnClickListener() {

								@Override
								public void onClick(View arg0) {
									loginOut();
								}
							});
							currentFragment = myFragment;
						}
						break;
				}
			}
		});
		// 菜單項
		getView(R.id.menu_item_notification).setOnClickListener(this);
		getView(R.id.menu_item_bookmark).setOnClickListener(this);
		getView(R.id.menu_item_chatlist).setOnClickListener(this);
		getView(R.id.menu_item_calendar).setOnClickListener(this);
		msgCount = getView(R.id.menu_item_tv_msgCount);
		flTipFindTutor = getView(R.id.fl_tip_find_tutor);
		flTipFindTutor.setOnClickListener(this);
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
					LoginManager.getInstance().logOut();
				}
				// TODO 调用api退出登录
				TutorApplication.settingManager.writeSetting(Constants.SharedPreferences.SP_ISLOGIN, false);
				Intent intent = new Intent(StudentMainActivity.this, ChoiceRoleActivity.class);
				startActivity(intent);
				finishNoAnim();
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	private void initFragment() {
		findTeacherFragment = new FindTeacherFragment();
		ft = getSupportFragmentManager().beginTransaction();
		ft.add(R.id.ac_main_content_fragment, findTeacherFragment, "findTeacherFragment");
		ft.show(findTeacherFragment);
		ft.commit();
		currentFragment = findTeacherFragment;
		// 当切换为这个tab的时候才显示tip
		boolean isNeedShow = SharePrefUtil.getBoolean(getApplicationContext(), Constants.General.IS_NEED_SHOW_TUTOR_TIP, true);
		if (isNeedShow) {
			flTipFindTutor.setVisibility(View.VISIBLE);
		} else {
			flTipFindTutor.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		initMsgCount();
		// 註冊廣播
		IntentFilter filter = new IntentFilter(Constants.Action.ACTION_NEW_MESSAGE);
		registerReceiver(receiver, filter);
	}

	/**
	 * 檢查未讀消息
	 */
	private void initMsgCount() {
		count = IMMessageManager.getManager().getUnReadNoticeCount();
		if (0 < count) {
			msgCount.setText("" + count);
			msgCount.setVisibility(View.VISIBLE);
		} else {
			msgCount.setText("");
			msgCount.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}

	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Constants.Action.ACTION_NEW_MESSAGE.equals(action)) {
				count++;
				msgCount.setText("" + count);
				msgCount.setVisibility(View.VISIBLE);
			}
		}
	};

	@Override
	protected void onDestroy() {
		// 斷開IM連接
		XMPPConnectionManager.getManager().disconnect();
		// 停止服務
		Intent intent = new Intent(this, IMService.class);
		stopService(intent);
		// 清空缓存的图片
		ImageUtils.clearChache();
		TutorApplication.isMainActivity = false;
		// Constants.Xmpp.isChatNotification = true;
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if (null != myFragment) {
			myFragment.onActivityResult(arg0, arg1, arg2);
		}
	}

	private static long lastTime = 0;
	private FrameLayout flTipFindTutor;

	/**
	 * 菜單打開是按返回鍵關閉菜單,否則按兩次退出
	 */
	@Override
	public void onBackPressed() {
		final int drawerState = mMenuDrawer.getDrawerState();
		if (drawerState == MenuDrawer.STATE_OPEN || drawerState == MenuDrawer.STATE_OPENING) {
			mMenuDrawer.closeMenu();
			return;
		}
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastTime < 800) {
			super.onBackPressed();
		} else {
			lastTime = currentTime;
			toast(R.string.toast_exit);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		final int drawerState = mMenuDrawer.getDrawerState();
		if (drawerState == MenuDrawer.STATE_OPEN || drawerState == MenuDrawer.STATE_OPENING) {
			mMenuDrawer.closeMenu();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.menu_item_notification:
				Intent notification = new Intent(this, SystemNotificationActivity.class);// NotificationActivity.class
				startActivity(notification);
				break;
			case R.id.menu_item_bookmark:
				Intent bookmark = new Intent(this, BookmarkActivity.class);
				startActivity(bookmark);
				break;
			case R.id.menu_item_chatlist:
				Intent chatlist = new Intent(this, ChatListActivity.class);
				startActivity(chatlist);
				break;
			case R.id.menu_item_calendar:
				Intent calenderIntent = new Intent(this, TimeTableActivity.class);
				startActivity(calenderIntent);
			case R.id.fl_tip_find_tutor:
				flTipFindTutor.setVisibility(View.GONE);
				SharePrefUtil.saveBoolean(getApplicationContext(), Constants.General.IS_NEED_SHOW_TUTOR_TIP, false);
				break;
			default:
				break;
		}
	}

	/**
	 * 登录IM
	 * 
	 * 
	 */
	private class LoginImTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected Integer doInBackground(Void... params) {
			// 執行登錄IM
			Account account = TutorApplication.getAccountDao().load("1");
			if (null != account) {
				String accountsString = account.getImAccount();
				String pswd = account.getImPswd();
				return XmppManager.getInstance().login(accountsString, pswd);
			}
			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if (result == Constants.Xmpp.LOGIN_SECCESS) {
				// 登錄成功,開啟服務
				Intent intent = new Intent(StudentMainActivity.this, IMService.class);
				startService(intent);
			} else {
				new LoginImTask().execute();
			}
		}
	}
}
