package com.tutor.ui.activity;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.tutor.R;
import com.tutor.TutorApplication;
import com.tutor.im.IMMessageManager;
import com.tutor.im.XMPPConnectionManager;
import com.tutor.im.XmppManager;
import com.tutor.model.Account;
import com.tutor.model.NotificationListResult;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.service.IMService;
import com.tutor.ui.fragment.BaseFragment;
import com.tutor.ui.fragment.teacher.FindStudentFragment;
import com.tutor.ui.fragment.teacher.MyFragment;
import com.tutor.ui.fragment.teacher.MyStudentFragment;
import com.tutor.ui.fragment.teacher.OverseasEducationFragment;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.CheckTokenUtils;
import com.tutor.util.HttpHelper;
import com.tutor.util.ImageUtils;
import com.tutor.util.ObjectHttpResponseHandler;
import com.tutor.util.ScreenUtil;

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
	private boolean isGetdata;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		TutorApplication.isMainActivity = true;
		mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.BEHIND, Position.LEFT, MenuDrawer.MENU_DRAG_WINDOW);
		mMenuDrawer.setContentView(R.layout.activity_teacher_main);
		mMenuDrawer.setMenuView(R.layout.menu_layout);
		mMenuDrawer.setMenuSize(ScreenUtil.getSW(this) * 3 / 10);
		initView();
		// 初始化fragment
		initFragment();
		isGetdata = false;
		new LoginImTask().execute();
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
							bar.setRightTextVisibility(false);
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
							bar.setTitle(R.string.study_abroad);
							bar.setRightTextVisibility(false);
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
							bar.setRightTextVisibility(false);
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
		msgCount = getView(R.id.menu_item_tv_msgCount);
	}

	private void loginOut() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.log_out);
		builder.setMessage(R.string.lable_log_out);
		builder.setPositiveButton(R.string.cancel, null);
		builder.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO 请求退出登录api
				TutorApplication.settingManager.writeSetting(Constants.SharedPreferences.SP_ISLOGIN, false);
				Intent intent = new Intent(TeacherMainActivity.this, ChoiceRoleActivity.class);
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
			getNotificationCount();
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
		// 清空缓存的图片
		ImageUtils.clearChache();
		TutorApplication.isMainActivity = false;
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
		// notification
			case R.id.menu_item_notification:
				Intent notification = new Intent(this, NotificationActivity.class);
				startActivity(notification);
				break;
			// Bookmark
			case R.id.menu_item_bookmark:
				Intent bookmark = new Intent(this, BookmarkActivity.class);
				startActivity(bookmark);
				break;
			default:
				break;
		}
	}

	/**
	 * 获取消息数量 Sent 0 Accept 1 Reject 2 Acknowle 3 All 4
	 */
	private void getNotificationCount() {
		if (!HttpHelper.isNetworkConnected(this)) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		RequestParams params = new RequestParams();
		params.put("status", "1");
		params.put("pageIndex", "0");
		params.put("pageSize", "1");
		HttpHelper.get(this, ApiUrl.NOTIFICATIONLIST, TutorApplication.getHeaders(), params, new ObjectHttpResponseHandler<NotificationListResult>(NotificationListResult.class) {

			@Override
			public void onFailure(int status, String message) {
				if (0 == status) {
					getNotificationCount();
					return;
				}
				isGetdata = false;
				CheckTokenUtils.checkToken(status);
			}

			@Override
			public void onSuccess(NotificationListResult result) {
				isGetdata = false;
				CheckTokenUtils.checkToken(result);
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
		});
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
				Intent intent = new Intent(TeacherMainActivity.this, IMService.class);
				startService(intent);
			} else {
				if (!TutorApplication.isTokenInvalid) {
					toast(R.string.toast_login_im_fail);
				}
				new LoginImTask().execute();
			}
		}
	}
}
