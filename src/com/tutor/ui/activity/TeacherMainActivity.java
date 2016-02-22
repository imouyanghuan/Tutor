package com.tutor.ui.activity;

import java.util.HashSet;
import java.util.Set;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
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
import com.tutor.ui.dialog.ActivityDialog;
import com.tutor.ui.dialog.ActivityDialog.OnOkClickListener;
import com.tutor.ui.fragment.BaseFragment;
import com.tutor.ui.fragment.blog.BlogFragment;
import com.tutor.ui.fragment.student.TuitionCentreFragment;
import com.tutor.ui.fragment.teacher.FindStudentFragment;
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
	public MenuDrawer mMenuDrawer;
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
	// private MyFragment myFragment;
	private TuitionCentreFragment tuitionCentreFragment;
	private BlogFragment blogFragment;
	/** fragment操作管理對象 */
	private FragmentTransaction ft;
	// 顯示未讀消息
	private TextView msgCount, notificationCount;
	private long count = 0;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		// Constants.Xmpp.isChatNotification = false;
		TutorApplication.isMainActivity = true;
		mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.BEHIND, Position.LEFT, MenuDrawer.MENU_DRAG_WINDOW);
		mMenuDrawer.setContentView(R.layout.activity_teacher_main);
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
		boolean isNeedShow = SharePrefUtil.getBoolean(getApplicationContext(), Constants.General.IS_NEED_SHOW_STUDENT_TIP, true);
		if (isNeedShow && currentFragment instanceof FindStudentFragment) {
			flTipFindStudent.setVisibility(View.VISIBLE);
		} else {
			flTipFindStudent.setVisibility(View.GONE);
		}
	}

	/**
	 * 判断是否是Facebook点击进来的
	 */
	private void isFromFacebook() {
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
					Intent intent = new Intent(TeacherMainActivity.this, BlogDetailActivity.class);
					intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_BLOG, blog);
					intent.putExtra(Constants.General.IS_FROM_FACEBOOK, true);
					TeacherMainActivity.this.startActivity(intent);
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
			JPushInterface.setAliasAndTags(TeacherMainActivity.this, account.getImAccount(), tags);
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
				Intent intent = new Intent(TeacherMainActivity.this, IMService.class);
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
		bar.setTitle(R.string.findstudents);
		bar.setLeftButton(R.drawable.menu_selector, new OnClickListener() {

			@Override
			public void onClick(View v) {
				mMenuDrawer.openMenu();
			}
		});
		// 有identity code才显示dialog
		if (!TextUtils.isEmpty(TutorApplication.getIdentityCode())) {
			bar.setRightButton(R.drawable.info_selector, new OnClickListener() {

				@Override
				public void onClick(View v) {
					final ActivityDialog dialog = new ActivityDialog(TeacherMainActivity.this);
					dialog.setContextAndUserInfo(TeacherMainActivity.this, null);
					dialog.setCancelable(false);
					dialog.show();
					dialog.setOnOkClickListener(new OnOkClickListener() {

						@Override
						public void onClickListener() {
							dialog.dismiss();
						}
					});
				}
			});
		}
		mRadioGroup = getView(R.id.ac_main_rg);
		// 底部菜單切換時的操作
		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			// 改變標題欄右按鈕的圖標和監聽
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
					case R.id.ac_main_rb1:
						if (currentFragment != findStudentFragment) {
							ft = getSupportFragmentManager().beginTransaction();
							if (null == findStudentFragment) {
								findStudentFragment = new FindStudentFragment();
								ft.add(R.id.ac_main_content_fragment, findStudentFragment, "findStudentFragment");
							}
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
							studentFragment.refresh();
							bar.setTitle(R.string.mystudents);
							bar.setRightTextVisibility(false);
							currentFragment = studentFragment;
						}
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
		TextView tvMystudentsOrMytutors = getView(R.id.tv_mystudents_or_mytutors);
		if (TutorApplication.getRole() == Constants.General.ROLE_STUDENT) {
			tvMystudentsOrMytutors.setText(R.string.myteachers);
		} else {
			tvMystudentsOrMytutors.setText(R.string.mystudents);
		}
		getView(R.id.menu_item_mystudents_or_mytutors).setOnClickListener(this);
		getView(R.id.menu_item_my_profile).setOnClickListener(this);
		getView(R.id.menu_item_notification).setOnClickListener(this);
		getView(R.id.menu_item_bookmark).setOnClickListener(this);
		getView(R.id.menu_item_chatlist).setOnClickListener(this);
		getView(R.id.menu_item_calendar).setOnClickListener(this);
		msgCount = getView(R.id.menu_item_tv_msgCount);
		notificationCount = getView(R.id.tv_notification_count);
		// tip
		flTipFindStudent = getView(R.id.fl_tip_find_student);
		flTipFindStudent.setOnClickListener(this);
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
	// } catch (Exception e) {
	// // TODO: handle exception
	// }
	// }
	// // TODO 请求退出登录api
	// // 斷開IM連接
	// XMPPConnectionManager.getManager().disconnect();
	// // 停止Push服务
	// JPushInterface.stopPush(TeacherMainActivity.this);
	// TutorApplication.settingManager.writeSetting(Constants.SharedPreferences.SP_ISLOGIN,
	// false);
	// Intent intent = new Intent(TeacherMainActivity.this,
	// ChoiceRoleActivity.class);
	// startActivity(intent);
	// finishNoAnim();
	// }
	// });
	// AlertDialog dialog = builder.create();
	// dialog.show();
	// }
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
		initMsgCount();
		getSystemNotification();
		// 註冊廣播
		IntentFilter filter = new IntentFilter(Constants.Action.ACTION_NEW_MESSAGE);
		registerReceiver(receiver, filter);
		JPushInterface.resumePush(TeacherMainActivity.this);
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		// if (null != tuitionCentreFragment) {
		// tuitionCentreFragment.onActivityResult(arg0, arg1, arg2);
		// }
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
	private FrameLayout flTipFindStudent;
	private String blogId;

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
		// long currentTime = System.currentTimeMillis();
		// if (currentTime - lastTime < 800) {
		// super.onBackPressed();
		// } else {
		// lastTime = currentTime;
		// toast(R.string.toast_exit);
		// }
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
		final int drawerState = mMenuDrawer.getDrawerState();
		if (drawerState == MenuDrawer.STATE_OPEN || drawerState == MenuDrawer.STATE_OPENING) {
			mMenuDrawer.closeMenu();
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
			// notification
			case R.id.menu_item_notification:
				Intent notification = new Intent(this, SystemNotificationActivity.class);
				startActivity(notification);
				break;
			// Bookmark
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
				break;
			case R.id.fl_tip_find_student:
				flTipFindStudent.setVisibility(View.GONE);
				SharePrefUtil.saveBoolean(getApplicationContext(), Constants.General.IS_NEED_SHOW_STUDENT_TIP, false);
				break;
			default:
				break;
		}
	}
}
