package com.tutor.ui.activity;

import com.tutor.R;
import com.tutor.ui.view.TitleBar;

import android.os.Bundle;

/**
 * 收藏夾
 * 
 * @author bruce.chen
 * 
 *         2015-8-26
 */
public class BookmarkActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_bookmark);
		initView();
	}

	@Override
	protected void initView() {
		TitleBar bar = getView(R.id.title_bar);
		bar.initBack(this);
		bar.setTitle(R.string.bookmark);
	}
}
