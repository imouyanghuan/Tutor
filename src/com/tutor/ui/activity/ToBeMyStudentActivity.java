package com.tutor.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.tutor.R;
import com.tutor.ui.view.TitleBar;

public class ToBeMyStudentActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle bundle) {
		// TODO Auto-generated method stub
		super.onCreate(bundle);
		setContentView(R.layout.activity_to_be_my_student);
		
		initView();
	}

	@Override
	protected void initView() {
		initTitleBar();
	}

	private void initTitleBar() {
		TitleBar bar = getView(R.id.title_bar);
		bar.setTitle("To Be My Student");
		bar.initBack(this);
		bar.setRightText("Save", new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				toast("Save");
			}
		});
	}
}
