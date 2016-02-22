package com.tutor.ui.fragment.blog;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.OnTabSelectedListener;
import android.support.design.widget.TabLayout.Tab;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hk.tutor.R;
import com.loopj.android.http.RequestParams;
import com.tutor.TutorApplication;
import com.tutor.model.BlogCategory;
import com.tutor.model.BlogCategoryListResult;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.activity.StudentMainActivity;
import com.tutor.ui.activity.TeacherMainActivity;
import com.tutor.ui.fragment.BaseFragment;
import com.tutor.util.HttpHelper;
import com.tutor.util.ObjectHttpResponseHandler;

/**
 * blog
 * 
 * @author jerry.yao
 * 
 *         2015-12-8
 */
public class BlogFragment extends BaseFragment {

	private LayoutInflater layoutInflater;
	private View rootView;
	private List<BlogCategory> blogCategorys = new ArrayList<BlogCategory>();
	private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
	/**
	 * childFragment
	 */
	private BlogColumnFragment blogColumnFragment;
	private ViewPager viewPager;
	private TabFragmentPagerAdapter mAdapter;
	private TabLayout indicator;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		initIndicatorTheme(inflater, container);
		initView();
		return rootView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		getBlogCategory();
	}

	/**
	 * 获取博客类别
	 */
	private void getBlogCategory() {
		if (!HttpHelper.isNetworkConnected(getActivity())) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		showDialogRes(R.string.loading);
		HttpHelper.getHelper().get(ApiUrl.BLOG_CATEGORY, TutorApplication.getHeaders(), new RequestParams(), new ObjectHttpResponseHandler<BlogCategoryListResult>(BlogCategoryListResult.class) {

			@Override
			public void onFailure(int status, String message) {
				if (0 == status) {
					getBlogCategory();
					return;
				}
				dismissDialog();
				toast(R.string.toast_server_error);
			}

			@Override
			public void onSuccess(BlogCategoryListResult result) {
				dismissDialog();
				if (null != result) {
					blogCategorys.addAll(result.getResult());
					setCategorys();
				} else {
					toast(R.string.toast_server_error);
				}
			}
		});
	}

	private void initIndicatorTheme(LayoutInflater inflater, ViewGroup container) {
		// 设置Fragment样式
		final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.Theme_PageIndicatorDefaults);
		layoutInflater = inflater.cloneInContext(contextThemeWrapper);
		rootView = layoutInflater.inflate(R.layout.fragment_blog, container, false);
	}

	private void initView() {
		viewPager = (ViewPager) rootView.findViewById(R.id.pager);
		// 设置缓存页数
		// viewPager.setOffscreenPageLimit(columns.size());
		indicator = (TabLayout) rootView.findViewById(R.id.indicator);
		indicator.setBackgroundColor(getResources().getColor(R.color.gray_light));
		indicator.setSelectedTabIndicatorColor(getResources().getColor(R.color.tab_txt_color_checked));
		indicator.setTabTextColors(getResources().getColor(R.color.black1), getResources().getColor(R.color.tab_txt_color_checked));
		indicator.setOnTabSelectedListener(new OnTabSelectedListener() {

			@Override
			public void onTabUnselected(Tab arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTabSelected(Tab tab) {
				if (TutorApplication.getRole() == Constants.General.ROLE_STUDENT) {
					if (((StudentMainActivity) getActivity()) != null && ((StudentMainActivity) getActivity()).mMenuDrawer != null) {
						if (tab.getPosition() == 0) {
							((StudentMainActivity) getActivity()).mMenuDrawer.setTouchBezelSize(160);
						} else {
							((StudentMainActivity) getActivity()).mMenuDrawer.setTouchBezelSize(0);
						}
						((StudentMainActivity) getActivity()).mMenuDrawer.invalidate();
					}
				} else if (TutorApplication.getRole() == Constants.General.ROLE_TUTOR) {
					if (((TeacherMainActivity) getActivity()) != null && ((TeacherMainActivity) getActivity()).mMenuDrawer != null) {
						if (tab.getPosition() == 0) {
							((TeacherMainActivity) getActivity()).mMenuDrawer.setTouchBezelSize(160);
						} else {
							((TeacherMainActivity) getActivity()).mMenuDrawer.setTouchBezelSize(0);
						}
						((TeacherMainActivity) getActivity()).mMenuDrawer.invalidate();
					}
				}
				viewPager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabReselected(Tab arg0) {
				// TODO Auto-generated method stub
			}
		});
	}

	private void setCategorys() {
		if (blogCategorys != null && blogCategorys.size() > 0) {
			for (int i = 0, j = blogCategorys.size(); i < j; i++) {
				blogColumnFragment = new BlogColumnFragment();
				blogColumnFragment.setCategoryValue(blogCategorys.get(i).getValue());
				fragments.add(blogColumnFragment);
			}
			mAdapter = new TabFragmentPagerAdapter(getChildFragmentManager(), fragments);
			viewPager.setAdapter(mAdapter);
			indicator.setupWithViewPager(viewPager);
			if (fragments.size() > 4) {
				indicator.setTabMode(TabLayout.MODE_SCROLLABLE);
			} else {
				indicator.setTabMode(TabLayout.MODE_FIXED);
			}
		}
	}

	// 适配器
	class TabFragmentPagerAdapter extends FragmentPagerAdapter {

		private ArrayList<Fragment> fragments = null;

		public TabFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		public TabFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
			super(fm);
			this.fragments = fragments;
		}

		@Override
		public Fragment getItem(int position) {
			return fragments.get(position);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return blogCategorys.get(position).getText().toUpperCase();
		}
	}
}
