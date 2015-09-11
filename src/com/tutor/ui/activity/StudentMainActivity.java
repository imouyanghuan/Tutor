package com.tutor.ui.activity;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.tutor.R;
import com.tutor.params.Constants;
import com.tutor.ui.fragment.BaseFragment;
import com.tutor.ui.fragment.student.FindTeacherFragment;
import com.tutor.ui.fragment.student.MyFragment;
import com.tutor.ui.fragment.student.MyTeacherFragment;
import com.tutor.ui.fragment.student.OverseasEducationFragment;
import com.tutor.ui.view.TitleBar;

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

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.BEHIND, Position.LEFT, MenuDrawer.MENU_DRAG_WINDOW);
		mMenuDrawer.setContentView(R.layout.activity_student_main);
		mMenuDrawer.setMenuView(R.layout.menu_layout);
		initView();
		// 初始化fragment
		initFragment();
		mMenuDrawer.peekDrawer();
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

			// TODO 改變標題欄右按鈕的圖標和監聽
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
					case R.id.ac_main_rb1:
						if (currentFragment != findTeacherFragment) {
							ft = getSupportFragmentManager().beginTransaction();
							ft.hide(currentFragment);
							ft.show(findTeacherFragment);
							ft.commit();
							bar.setTitle(R.string.findteacher);
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
							bar.setTitle(R.string.overseas_education);
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
							currentFragment = myFragment;
						}
						break;
				}
			}
		});
		// 菜單項
		getView(R.id.menu_item_notification).setOnClickListener(this);
		getView(R.id.menu_item_bookmark).setOnClickListener(this);
	}

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
		Constants.Xmpp.isChatNotification = false;
	}

	@Override
	protected void onPause() {
		super.onPause();
		Constants.Xmpp.isChatNotification = true;
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
}
