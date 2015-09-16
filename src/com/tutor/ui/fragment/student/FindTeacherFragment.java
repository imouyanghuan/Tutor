package com.tutor.ui.fragment.student;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mssky.mobile.ui.view.PullToRefreshListView;
import com.mssky.mobile.ui.view.PullToRefreshBase.Mode;
import com.tutor.R;
import com.tutor.adapter.MatchStudentAdapter;
import com.tutor.model.UserInfo;
import com.tutor.ui.fragment.BaseFragment;
import com.tutor.util.ViewHelper;

/**
 * 學生首頁,我的老師
 * 
 * @author bruce.chen
 * 
 *         2015-8-20
 */
public class FindTeacherFragment extends BaseFragment {

	private PullToRefreshListView listView;
	// 列表
	private ArrayList<UserInfo> users = new ArrayList<UserInfo>();
	private MatchStudentAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_student_findteacher, container, false);
		initView(inflater, view);
		return view;
	}

	@SuppressLint("InflateParams")
	private void initView(LayoutInflater inflater, View view) {
		View headerView = inflater.inflate(R.layout.find_teacher_header_layout, null);
		listView = ViewHelper.get(view, R.id.fragment_find_teacher_lv);
		listView.getRefreshableView().addHeaderView(headerView);
		// 設置可上拉加載和下拉刷新
		listView.setMode(Mode.BOTH);
		adapter = new MatchStudentAdapter(getActivity(), users);
		listView.setAdapter(adapter);
	}
}
