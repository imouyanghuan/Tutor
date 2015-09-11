package com.tutor.ui.activity;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.lidroid.xutils.exception.DbException;
import com.tutor.R;
import com.tutor.TutorApplication;
import com.tutor.im.IMMessageManager;
import com.tutor.im.XMPPConnectionManager;
import com.tutor.im.XmppManager;
import com.tutor.model.Account;
import com.tutor.model.NotificationListResult;
import com.tutor.params.Constants;
import com.tutor.service.IMService;
import com.tutor.service.TutorService;
import com.tutor.ui.fragment.BaseFragment;
import com.tutor.ui.fragment.teacher.FindStudentFragment;
import com.tutor.ui.fragment.teacher.MyFragment;
import com.tutor.ui.fragment.teacher.MyStudentFragment;
import com.tutor.ui.fragment.teacher.OverseasEducationFragment;
import com.tutor.ui.view.TitleBar;

/**
 * 教師主界面,管理fragment
 * 
 * @author bruce.chen
 * 
 *         2015-8-20
 */
public class TeacherMainActivity extends BaseActivity implements OnClickListener {

	/** 滑動菜單 */
	private MenuDrawer mMenuDrawer;
	/** 頂部菜單 */
	private TitleBar bar;
	/** 底部菜單欄 */
	private RadioGroup mRadioGroup;
	/** 當前選中的fragment */
	private BaseFragment currentFragment;
	/** 其他fragment */
	private FindStudentFragment findStudentFragment;
	private OverseasEducationFragment overseasEducationFragment;
	private MyStudentFragment studentFragment;
	private MyFragment myFragment;
	/** fragment操作管理對象 */
	private FragmentTransaction ft;
	// 顯示未讀消息
	private TextView msgCount;
	private long count = 0;
	// 是否需要登錄
	private boolean isLogin;
	private boolean isGetdata;
	// 通過handler更新
	private MyHandler handler = new MyHandler();

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.BEHIND, Position.LEFT, MenuDrawer.MENU_DRAG_WINDOW);
		mMenuDrawer.setContentView(R.layout.activity_teacher_main);
		mMenuDrawer.setMenuView(R.layout.menu_layout);
		initView();
		// 初始化fragment
		initFragment();
		mMenuDrawer.peekDrawer();
		isLogin = true;
		isGetdata = false;
	}

	@Override
	protected void initView() {
		bar = getView(R.id.title_bar);
		bar.setTitle(R.string.findstudents);
		bar.setLeftButton(R.drawable.menu_selector, new OnClickListener() {

			@Override
			public void onClick(View v) {
				mMenuDrawer.openMenu();
			}
		});
		mRadioGroup = getView(R.id.ac_main_rg);
		// 底部菜單切換時的操作
		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			// TODO 改變標題欄右按鈕的圖標和監聽
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
					case R.id.ac_main_rb1:
						if (currentFragment != findStudentFragment) {
							ft = getSupportFragmentManager().beginTransaction();
							ft.hide(currentFragment);
							ft.show(findStudentFragment);
							ft.commit();
							bar.setTitle(R.string.findstudents);
							currentFragment = findStudentFragment;
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
							bar.setTitle(R.string.overseas_education);
							currentFragment = overseasEducationFragment;
						}
						break;
					case R.id.ac_main_rb3:
						if (currentFragment != studentFragment) {
							ft = getSupportFragmentManager().beginTransaction();
							if (null == studentFragment) {
								studentFragment = new MyStudentFragment();
								ft.add(R.id.ac_main_content_fragment, studentFragment, "studentFragment");
							}
							ft.hide(currentFragment);
							ft.show(studentFragment);
							ft.commit();
							bar.setTitle(R.string.mystudents);
							currentFragment = studentFragment;
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
							currentFragment = myFragment;
						}
						break;
				}
			}
		});
		// 菜單項
		getView(R.id.menu_item_notification).setOnClickListener(this);
		getView(R.id.menu_item_bookmark).setOnClickListener(this);
		msgCount = getView(R.id.menu_item_tv_msgCount);
	}

	/**
	 * 初始化fragment
	 */
	private void initFragment() {
		findStudentFragment = new FindStudentFragment();
		ft = getSupportFragmentManager().beginTransaction();
		ft.add(R.id.ac_main_content_fragment, findStudentFragment, "findStudentFragment");
		ft.show(findStudentFragment);
		ft.commit();
		currentFragment = findStudentFragment;
	}

	@Override
	protected void onResume() {
		super.onResume();
		Constants.Xmpp.isChatNotification = false;
		initMsgCount();
		// TODO 註冊廣播接收IM消息
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if (null != myFragment) {
			myFragment.onActivityResult(arg0, arg1, arg2);
		}
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
		if (!isGetdata) {
			isGetdata = true;
			new GetNotificationCountTask().execute();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		Constants.Xmpp.isChatNotification = true;
		// TODO 註銷廣播
	}

	@Override
	protected void onDestroy() {
		// 斷開IM連接
		XMPPConnectionManager.getManager().disconnect();
		// 停止服務
		Intent intent = new Intent(this, IMService.class);
		stopService(intent);
		super.onDestroy();
	}

	private static long lastTime = 0;

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
				Intent notification = new Intent(this, NotificationActivity.class);
				startActivity(notification);
				break;
			case R.id.menu_item_bookmark:
				Intent bookmark = new Intent(this, BookmarkActivity.class);
				startActivity(bookmark);
				break;
			default:
				break;
		}
	}

	@SuppressLint("HandlerLeak")
	private class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int what = msg.what;
			if (what == Constants.Xmpp.LOGIN_SECCESS) {
				// 登錄成功,開啟服務
				isLogin = false;
				Intent intent = new Intent(TeacherMainActivity.this, IMService.class);
				startService(intent);
			} else {
				isLogin = true;
				if (!TutorApplication.isTokenInvalid) {
					toast(R.string.toast_login_im_fail);
				}
			}
		}
	}

	/**
	 * 獲取未讀邀請消息數量
	 * 
	 * @author bruce.chen
	 * 
	 */
	private class GetNotificationCountTask extends AsyncTask<Void, Void, NotificationListResult> {

		@Override
		protected NotificationListResult doInBackground(Void... params) {
			// 執行登錄IM
			if (isLogin) {
				isLogin = false;
				try {
					Account account = TutorApplication.dbUtils.findFirst(Account.class);
					if (null != account) {
						String accountsString = account.getImAccount();
						String pswd = account.getImPswd();
						int status = XmppManager.getInstance().login(accountsString, pswd);
						handler.sendEmptyMessage(status);
					}
				} catch (DbException e) {
					e.printStackTrace();
				}
			}
			return TutorService.getService().getNotificationCount();
		}

		@Override
		protected void onPostExecute(NotificationListResult result) {
			super.onPostExecute(result);
			isGetdata = false;
			if (null != result && 200 == result.getStatusCode()) {
				count += result.getPage().getTotalCount();
				if (0 < count) {
					msgCount.setText("" + count);
					msgCount.setVisibility(View.VISIBLE);
				} else {
					msgCount.setText("");
					msgCount.setVisibility(View.GONE);
				}
			}
		}
	}
}
