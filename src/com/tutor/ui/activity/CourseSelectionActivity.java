package com.tutor.ui.activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.Spinner;

import com.hk.tutor.R;
import com.tutor.ui.view.ObservableScrollView;
import com.tutor.ui.view.ObservableScrollView.ScrollViewListener;
import com.tutor.ui.view.TitleBar;

/**
 * Course Selection
 * 
 * @author jerry.yao
 * 
 *         2015-10-19
 */
public class CourseSelectionActivity extends BaseActivity implements ScrollViewListener, OnClickListener {

	private Spinner spGrade;
	private Spinner spCountry;
	private ObservableScrollView scrollView;
	private FrameLayout flToolbar;
	private boolean isGone = true;
	private CheckBox cbIsPrivacy;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_course_selection);
		initView();
	}

	private ArrayList<String> getCourse() {
		ArrayList<String> data = new ArrayList<String>();
		for (int i = 0; i < 6; i++) {
			data.add("Grade 1 - 1" + i);
		}
		return data;
	}

	private ArrayList<String> getCountry() {
		ArrayList<String> data = new ArrayList<String>();
		for (int i = 0; i < 6; i++) {
			data.add("England " + i);
		}
		return data;
	}

	@Override
	protected void initView() {
		initTitleBar();
		spGrade = getView(R.id.sp_grade);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(CourseSelectionActivity.this, android.R.layout.simple_list_item_1, getCourse());
		spGrade.setAdapter(adapter);
		//
		spCountry = getView(R.id.sp_country);
		ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(CourseSelectionActivity.this, android.R.layout.simple_list_item_1, getCountry());
		spCountry.setAdapter(countryAdapter);
		// is privacy
		cbIsPrivacy = getView(R.id.cb_is_privacy);
		// scrollview
		scrollView = getView(R.id.scrollView);
		scrollView.setScrollViewListener(this);
		// toolbar
		flToolbar = getView(R.id.fl_toolbar);
		getView(R.id.btn_chat_with_adviser).setOnClickListener(this);
	}

	private void initTitleBar() {
		TitleBar bar = getView(R.id.title_bar);
		bar.initBack(this);
		bar.setTitle(R.string.label_course_selection);
	}

	@Override
	public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
		//
		if (oldy < y && Math.abs(oldy - y) > 20) {
			if (isGone) {
				// 向下滚动
				flToolbar.setVisibility(View.GONE);
				TranslateAnimation goneAnim = new TranslateAnimation(0, 0, 0, 150);
				goneAnim.setDuration(200);
				goneAnim.setFillAfter(true);
				flToolbar.setAnimation(goneAnim);
				isGone = false;
			}
		} else if (oldy > y && Math.abs(oldy - y) > 20) {
			if (!isGone) {
				// 向上滚动
				flToolbar.setVisibility(View.VISIBLE);
				TranslateAnimation visibleAnim = new TranslateAnimation(0, 0, 150, 0);
				visibleAnim.setDuration(200);
				visibleAnim.setFillAfter(true);
				flToolbar.setAnimation(visibleAnim);
				isGone = true;
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_chat_with_adviser:
				// TODO
				break;
			default:
				break;
		}
	}
}
