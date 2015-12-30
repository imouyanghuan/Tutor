package com.tutor.ui.activity;

import java.util.HashSet;
import java.util.Set;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;

import com.hk.tutor.R;
import com.loopj.android.http.RequestParams;
import com.mssky.mobile.helper.SharePrefUtil;
import com.tutor.TutorApplication;
import com.tutor.im.IMMessageManager;
import com.tutor.im.XmppManager;
import com.tutor.model.Account;
import com.tutor.model.BlogDetailResult;
import com.tutor.model.BlogModel;
import com.tutor.model.NotificationListResult;
import com.tutor.model.Page;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.service.IMService;
import com.tutor.ui.fragment.BaseFragment;
import com.tutor.ui.fragment.blog.BlogFragment;
import com.tutor.ui.fragment.student.FindTeacherFragment;
import com.tutor.ui.fragment.student.OverseasEducationFragment;
import com.tutor.ui.fragment.student.TuitionCentreFragment;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.CheckTokenUtils;
import com.tutor.util.HttpHelper;
import com.tutor.util.ImageUtils;
import com.tutor.util.ObjectHttpResponseHandler;
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
	public MenuDrawer mMenuDrawer;
	/** 頂部菜單 */
	private TitleBar bar;
	/** 底部菜單欄 */
	private RadioGroup mRadioGroup;
	/** 當前選中的fragment */
	private BaseFragment currentFragment;
	/** 其他fragment */
	private FindTeacherFragment findTeacherFragment;
	private OverseasEducationFragment overseasEducationFragment;
	private BlogFragment blogFragment;
	private TuitionCentreFragment tuitionCentreFragment;
	/** fragment操作管理對象 */
	private FragmentTransaction ft;
	// 显示消息条数
	private TextView msgCount;
	private TextView notificationCount;
	private long count = 0;
	// 是否是未登录的标识
	private boolean isNoLogin;
	private static long lastTime = 0;
	private FrameLayout flTipFindTutor;
	private int lastCheckedTab = -1;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		// Constants.Xmpp.isChatNotification = false;
		TutorApplication.isMainActivity = true;
		isNoLogin = (Boolean) TutorApplication.settingManager.readSetting(Constants.SharedPreferences.SP_ISLOGIN, false);
		if (!isNoLogin) {
			setContentView(R.layout.activity_student_main);
			bar = getView(R.id.title_bar);
			bar.setTitle(R.string.study_abroad);
			mRadioGroup = getView(R.id.ac_main_rg);
			mRadioGroup.check(R.id.ac_main_rb2);
			mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(RadioGroup arg0, int arg1) {
					switch (arg1) {
						case R.id.ac_main_rb2:
							setOverSeaAsTab();
							lastCheckedTab = R.id.ac_main_rb2;
							break;
						case R.id.ac_main_rb4:
							setBlogAsSelectedTab();
							lastCheckedTab = R.id.ac_main_rb4;
							break;
						case R.id.ac_main_rb1:
						case R.id.ac_main_rb5:
							mRadioGroup.check(lastCheckedTab);
							showDialog();
							break;
					}
				}
			});
			//
			overseasEducationFragment = new OverseasEducationFragment();
			ft = getSupportFragmentManager().beginTransaction();
			ft.add(R.id.ac_main_content_fragment, overseasEducationFragment, "overseasEducationFragment");
			ft.show(overseasEducationFragment);
			ft.commit();
			currentFragment = overseasEducationFragment;
			lastCheckedTab = R.id.ac_main_rb2;
			return;
		}
		mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.BEHIND, Position.LEFT, MenuDrawer.MENU_DRAG_WINDOW);
		mMenuDrawer.setContentView(R.layout.activity_student_main);
		mMenuDrawer.setMenuView(R.layout.menu_layout);
		mMenuDrawer.setMenuSize(ScreenUtil.getSW(this) * 3 / 10);
		initView();
		// 初始化fragment
		initFragment();
		imLogin();
		isFromFacebook();
	}

	private void showTip() {
		// 当切换为这个tab的时候才显示tip
		boolean isNeedShow = SharePrefUtil.getBoolean(getApplicationContext(), Constants.General.IS_NEED_SHOW_TUTOR_TIP, true);
		if (isNeedShow && currentFragment instanceof FindTeacherFragment) {
			flTipFindTutor.setVisibility(View.VISIBLE);
		} else {
			flTipFindTutor.setVisibility(View.GONE);
		}
	}

	/**
	 * 判断是否是Facebook点击进来的
	 */
	private void isFromFacebook() {
		Intent intent = getIntent();
		if (intent != null) {
			Uri uri = intent.getData();
			if (uri != null) {
				String URI = uri.toString();
				int lastIndex = URI.lastIndexOf("/");
				String blogId = URI.substring(lastIndex + 1);
				Log.e("Tutor", "********************************** 再次进入 id = " + blogId);
				if (!TextUtils.isEmpty(blogId)) {
					SharePrefUtil.saveString(StudentMainActivity.this, Constants.General.BLOG_ID, blogId);
				}
			}
		} else {
			Log.e("Tutor", "********************************** 再次进入 沒有 intent");
		}
		blogId = SharePrefUtil.getString(this, Constants.General.BLOG_ID, "");
		if (!TextUtils.isEmpty(blogId)) {
			getBlogDetail();
		} else {
			showTip();
		}
	}

	/**
	 * 获取博客详情
	 */
	private void getBlogDetail() {
		if (!HttpHelper.isNetworkConnected(this)) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		String url = String.format(ApiUrl.BLOG_DETAIL, blogId);
		HttpHelper.getHelper().get(url, TutorApplication.getHeaders(), new RequestParams(), new ObjectHttpResponseHandler<BlogDetailResult>(BlogDetailResult.class) {

			@Override
			public void onFailure(int status, String message) {
				if (0 == status) {
					getBlogDetail();
					return;
				}
				// CheckTokenUtils.checkToken(status);
			}

			@Override
			public void onSuccess(BlogDetailResult result) {
				// CheckTokenUtils.checkToken(result);
				if (null != result && 200 == result.getStatusCode()) {
					BlogModel blog = result.getResult();
					if (blog == null) {
						return;
					}
					setBlogAsSelectedTab();
					mRadioGroup.check(R.id.ac_main_rb4);
					showTip();
					Intent intent = new Intent(StudentMainActivity.this, BlogDetailActivity.class);
					intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_BLOG, blog);
					intent.putExtra(Constants.General.IS_FROM_FACEBOOK, true);
					StudentMainActivity.this.startActivity(intent);
				}
			}
		});
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
			JPushInterface.setAliasAndTags(StudentMainActivity.this, account.getImAccount(), tags);
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
				Intent intent = new Intent(StudentMainActivity.this, IMService.class);
				startService(intent);
			} else {
				if (result == Constants.Xmpp.LOGIN_ERROR_ACCOUNT_PASS) {
					pswd = pswd.replaceFirst("t", "T");
				}
				new LoginImTask(accountsString, pswd).execute();
			}
		}
	}

	private void showDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.tips);
		builder.setMessage(R.string.label_nologin);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				finish();
			}
		});
		builder.setNegativeButton(R.string.cancel, null);
		AlertDialog dialog = builder.create();
		dialog.show();
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
						setOverSeaAsTab();
						break;
					case R.id.ac_main_rb4:
						setBlogAsSelectedTab();
						break;
					case R.id.ac_main_rb5:
						if (currentFragment != tuitionCentreFragment) {
							ft = getSupportFragmentManager().beginTransaction();
							if (null == tuitionCentreFragment) {
								tuitionCentreFragment = new TuitionCentreFragment();
								ft.add(R.id.ac_main_content_fragment, tuitionCentreFragment, "tuitionCentreFragment");
							}
							ft.hide(currentFragment);
							ft.show(tuitionCentreFragment);
							ft.commit();
							bar.setTitle(R.string.label_tutorial_school);
							bar.setRightTextVisibility(false);
							currentFragment = tuitionCentreFragment;
						}
						break;
				}
			}
		});
		// 菜單項
		getView(R.id.menu_item_mystudents_or_mytutors).setVisibility(View.VISIBLE);
		getView(R.id.menu_item_mystudents_or_mytutors).setOnClickListener(this);
		getView(R.id.menu_item_my_profile).setOnClickListener(this);
		getView(R.id.menu_item_notification).setOnClickListener(this);
		getView(R.id.menu_item_bookmark).setOnClickListener(this);
		getView(R.id.menu_item_chatlist).setOnClickListener(this);
		getView(R.id.menu_item_calendar).setOnClickListener(this);
		msgCount = getView(R.id.menu_item_tv_msgCount);
		notificationCount = getView(R.id.tv_notification_count);
		flTipFindTutor = getView(R.id.fl_tip_find_tutor);
		flTipFindTutor.setOnClickListener(this);
	}

	private void setOverSeaAsTab() {
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
	}

	private void setBlogAsSelectedTab() {
		if (currentFragment != blogFragment) {
			ft = getSupportFragmentManager().beginTransaction();
			if (null == blogFragment) {
				blogFragment = new BlogFragment();
				ft.add(R.id.ac_main_content_fragment, blogFragment, "blogFragment");
			}
			ft.hide(currentFragment);
			ft.show(blogFragment);
			ft.commit();
			// blogFragment.refresh();
			bar.setTitle(R.string.label_blog);
			bar.setRightTextVisibility(false);
			currentFragment = blogFragment;
		}
	}

	// private void loginOut() {
	// AlertDialog.Builder builder = new AlertDialog.Builder(this);
	// builder.setTitle(R.string.log_out);
	// builder.setMessage(R.string.lable_log_out);
	// builder.setPositiveButton(R.string.cancel, null);
	// builder.setNegativeButton(R.string.ok, new
	// DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface arg0, int arg1) {
	// Account account = TutorApplication.getAccountDao().load("1");
	// if (account != null && !TextUtils.isEmpty(account.getFacebookId())) {
	// try {
	// LoginManager.getInstance().logOut();
	// } catch (Exception e) {}
	// }
	// // TODO 调用api退出登录
	// // 斷開IM連接
	// XMPPConnectionManager.getManager().disconnect();
	// // 停止Push服务
	// JPushInterface.stopPush(StudentMainActivity.this);
	// TutorApplication.settingManager.writeSetting(Constants.SharedPreferences.SP_ISLOGIN,
	// false);
	// Intent intent = new Intent(StudentMainActivity.this,
	// ChoiceRoleActivity.class);
	// startActivity(intent);
	// finishNoAnim();
	// }
	// });
	// AlertDialog dialog = builder.create();
	// dialog.show();
	// }
	private void initFragment() {
		findTeacherFragment = new FindTeacherFragment();
		ft = getSupportFragmentManager().beginTransaction();
		ft.add(R.id.ac_main_content_fragment, findTeacherFragment, "findTeacherFragment");
		ft.show(findTeacherFragment);
		ft.commit();
		currentFragment = findTeacherFragment;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isNoLogin) {
			initMsgCount();
			getSystemNotification();
			// 註冊廣播
			IntentFilter filter = new IntentFilter(Constants.Action.ACTION_NEW_MESSAGE);
			registerReceiver(receiver, filter);
		}
		JPushInterface.resumePush(StudentMainActivity.this);
	}

	/**
	 * 获取消息数量 Sent 0 Accept 1 Reject 2 Acknowle 3 All 4
	 */
	private void getSystemNotification() {
		if (!HttpHelper.isNetworkConnected(this)) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		RequestParams params = new RequestParams();
		params.put("pageIndex", "0");
		params.put("pageSize", "1");
		HttpHelper.getHelper().get(ApiUrl.NOTIFICATIONLIST, TutorApplication.getHeaders(), params, new ObjectHttpResponseHandler<NotificationListResult>(NotificationListResult.class) {

			@Override
			public void onFailure(int status, String message) {
				if (0 == status) {
					getSystemNotification();
					return;
				}
				CheckTokenUtils.checkToken(status);
			}

			@Override
			public void onSuccess(NotificationListResult result) {
				CheckTokenUtils.checkToken(result);
				if (null != result && 200 == result.getStatusCode()) {
					Page page = result.getPage();
					if (page != null) {
						if (page.getUntreatedCount() > 0) {
							notificationCount.setVisibility(View.VISIBLE);
							notificationCount.setText("" + page.getUntreatedCount());
						} else {
							notificationCount.setVisibility(View.GONE);
						}
					}
				}
			}
		});
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
		if (isNoLogin) {
			unregisterReceiver(receiver);
		}
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
	private String blogId;

	@Override
	protected void onDestroy() {
		if (isNoLogin) {
			// 斷開IM連接
			// XMPPConnectionManager.getManager().disconnect();
			// // 停止服務
			// Intent intent = new Intent(this, IMService.class);
			// stopService(intent);
			// 清空缓存的图片
			ImageUtils.clearChache();
		}
		TutorApplication.isMainActivity = false;
		// Constants.Xmpp.isChatNotification = true;
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		// if (null != myFragment) {
		// myFragment.onActivityResult(arg0, arg1, arg2);
		// }
	}

	/**
	 * 菜單打開是按返回鍵關閉菜單,否則按兩次退出
	 */
	@Override
	public void onBackPressed() {
		if (isNoLogin) {
			final int drawerState = mMenuDrawer.getDrawerState();
			if (drawerState == MenuDrawer.STATE_OPEN || drawerState == MenuDrawer.STATE_OPENING) {
				mMenuDrawer.closeMenu();
				return;
			}
			// long currentTime = System.currentTimeMillis();
			// if (currentTime - lastTime < 800) {
			// super.onBackPressed();
			// } else {
			// lastTime = currentTime;
			// toast(R.string.toast_exit);
			// }
			moveTaskToBack(true);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		PackageManager pm = getPackageManager();
		ResolveInfo homeInfo = pm.resolveActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME), 0);
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ActivityInfo ai = homeInfo.activityInfo;
			Intent startIntent = new Intent(Intent.ACTION_MAIN);
			startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			startIntent.setComponent(new ComponentName(ai.packageName, ai.name));
			startActivity(startIntent);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (isNoLogin) {
			final int drawerState = mMenuDrawer.getDrawerState();
			if (drawerState == MenuDrawer.STATE_OPEN || drawerState == MenuDrawer.STATE_OPENING) {
				mMenuDrawer.closeMenu();
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// my students or my tutors
			case R.id.menu_item_mystudents_or_mytutors:
				Intent intent = null;
				if (TutorApplication.getRole() == Constants.General.ROLE_STUDENT) {
					intent = new Intent(this, MyTutorsActivity.class);
				} else if (TutorApplication.getRole() == Constants.General.ROLE_TUTOR) {
					intent = new Intent(this, MyStudentsActivity.class);
				}
				startActivity(intent);
				break;
			// profile
			case R.id.menu_item_my_profile:
				Intent profile = null;
				if (TutorApplication.getRole() == Constants.General.ROLE_STUDENT) {
					profile = new Intent(this, StudentProfileActivity.class);
				} else if (TutorApplication.getRole() == Constants.General.ROLE_TUTOR) {
					profile = new Intent(this, TutorProfileActivity.class);
				}
				startActivity(profile);
				break;
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
}
