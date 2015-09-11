package com.tutor.ui.fragment.teacher;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tutor.R;
import com.tutor.ui.fragment.BaseFragment;

/**
 * 老師首頁,消息
 * 
 * @author bruce.chen
 * 
 *         2015-8-20
 */
public class OverseasEducationFragment extends BaseFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_overseas_education, null, false);
		initView(view);
		return view;
	}

	private void initView(View view) {}
}
