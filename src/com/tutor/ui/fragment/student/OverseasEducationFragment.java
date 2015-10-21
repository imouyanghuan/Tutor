package com.tutor.ui.fragment.student;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import com.hk.tutor.R;
import com.tutor.ui.activity.AdvisoryActivity;
import com.tutor.ui.activity.CourseSelectionActivity;
import com.tutor.ui.activity.FAQActivity;
import com.tutor.ui.fragment.BaseFragment;
import com.tutor.util.ViewHelper;

/**
 * 學生首頁,消息
 * 
 * @author bruce.chen
 * 
 *         2015-8-20
 */
public class OverseasEducationFragment extends BaseFragment implements OnClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_overseas_education, null, false);
		initView(view);
		return view;
	}

	private void initView(View view) {
		ViewHelper.get(view, R.id.rl_course_select).setOnClickListener(this);
		ViewHelper.get(view, R.id.rl_advisory).setOnClickListener(this);
		ViewHelper.get(view, R.id.rl_faq).setOnClickListener(this);
	}

	private Intent intent;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.rl_course_select:
				intent = new Intent(getActivity(), CourseSelectionActivity.class);
				break;
			case R.id.rl_advisory:
				intent = new Intent(getActivity(), AdvisoryActivity.class);
				break;
			case R.id.rl_faq:
				intent = new Intent(getActivity(), FAQActivity.class);
				break;
			default:
				break;
		}
		startActivity(intent);
	}
}
