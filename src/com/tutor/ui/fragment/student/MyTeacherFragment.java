package com.tutor.ui.fragment.student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tutor.R;
import com.tutor.ui.fragment.BaseFragment;

/**
 * 學生首頁,我的老師
 * 
 * @author bruce.chen
 * 
 *         2015-8-20
 */
public class MyTeacherFragment extends BaseFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_student_myteacher, null, false);
		initView(view);
		return view;
	}

	private void initView(View view) {}
}