package com.tutor.ui.activity;

import java.net.HttpURLConnection;
import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.mssky.mobile.ui.view.CustomListView;
import com.tutor.R;
import com.tutor.TutorApplication;
import com.tutor.adapter.TimeSlotAdapter;
import com.tutor.im.ContactManager;
import com.tutor.im.XMPPConnectionManager;
import com.tutor.model.AddBookmarkResult;
import com.tutor.model.Timeslot;
import com.tutor.model.UserInfo;
import com.tutor.model.UserInfoResult;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.CheckTokenUtils;
import com.tutor.util.HttpHelper;
import com.tutor.util.ImageUtils;
import com.tutor.util.ObjectHttpResponseHandler;

/**
 * 學生詳情界面
 * 
 * @author bruce.chen
 * 
 *         2015-8-24
 */
public class StudentInfoActivity extends BaseActivity implements OnClickListener {

	private int id;
	private String imId;
	private UserInfo userInfo;
	private ImageView avatarImageView;
	private TextView nickTextView, genderTextView, phoneTextView, gradeTextView, addressTextView;
	private CustomListView timeListView, couresListView;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_student_info);
		initData();
	}

	private void initData() {
		Intent intent = getIntent();
		if (intent == null) {
			return;
		}
		userInfo = (UserInfo) intent.getSerializableExtra(Constants.IntentExtra.INTENT_EXTRA_USER_INFO);
		if (userInfo == null) {
			return;
		}
		id = userInfo.getId();
		initView();
		showDialogRes(R.string.loading);
		getDetails();
	}

	private void setData() {
		if (userInfo == null) {
			return;
		}
		genderTextView.setText(Constants.General.MALE == userInfo.getGender() ? R.string.label_male : R.string.label_female);
		String phone = userInfo.getPhone();
		if (TextUtils.isEmpty(phone)) {
			phoneTextView.setText(R.string.label_unknow_phone);
		} else {
			phoneTextView.setText(phone);
		}
		gradeTextView.setText(TextUtils.isEmpty(userInfo.getGrade()) ? getString(R.string.label_unknow) : userInfo.getGrade());
		addressTextView.setText(TextUtils.isEmpty(userInfo.getResidentialAddress()) ? getString(R.string.label_unknow_address) : userInfo.getResidentialAddress());
		ArrayList<Timeslot> timeslots = userInfo.getTimeslots();
		if (timeslots != null) {
			TimeSlotAdapter timeSlotAdapter = new TimeSlotAdapter(this, timeslots, false);
			timeListView.setAdapter(timeSlotAdapter);
		}
		
	}

	@Override
	protected void initView() {
		TitleBar bar = getView(R.id.title_bar);
		String titleName = userInfo.getNickName();
		if (TextUtils.isEmpty(titleName)) {
			titleName = userInfo.getUserName();
			if (TextUtils.isEmpty(titleName)) {
				titleName = "PersonInfo";
			}
		}
		bar.setTitle(titleName);
		bar.initBack(this);
		bar.setRightButton(R.drawable.selector_like, new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (userInfo != null) {
					int userId = userInfo.getId();
					addToBookmark(userId);
				}
			}
		});
		getView(R.id.btn_chat_with_tutor).setOnClickListener(this);
		getView(R.id.btn_to_be_my_tutor).setOnClickListener(this);
		avatarImageView = getView(R.id.iv_avatar);
		nickTextView = getView(R.id.tv_user_name);
		genderTextView = getView(R.id.tv_gender);
		gradeTextView = getView(R.id.tv_grade);
		phoneTextView = getView(R.id.tv_phone);
		addressTextView = getView(R.id.tv_address);
		timeListView = getView(R.id.lv_timeslot);
		couresListView = getView(R.id.lv_coures);
		nickTextView.setText(titleName);
		ImageUtils.loadImage(avatarImageView, ApiUrl.DOMAIN + userInfo.getAvatar());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// chat with tutor
			case R.id.btn_chat_with_tutor:
				if (!TextUtils.isEmpty(imId)) {
					ContactManager.getManager().addFriend(imId, imId);
					Intent intent = new Intent(StudentInfoActivity.this, ChatActivity.class);
					intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_MESSAGE_TO, imId + "@" + XMPPConnectionManager.getManager().getServiceName());
					startActivity(intent);
				}
				break;
			// to be my tutor TODO
			case R.id.btn_to_be_my_tutor:
				if (!TextUtils.isEmpty(imId)) {
					Intent intent = new Intent(StudentInfoActivity.this, ToBeMyStudentActivity.class);
					startActivity(intent);
				}
				break;
			default:
				break;
		}
	}

	/**
	 * 添加到Bookmark
	 * 
	 */
	private void addToBookmark(int userId) {
		if (!HttpHelper.isNetworkConnected(this)) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		RequestParams params = new RequestParams();
		String url = String.format(ApiUrl.BOOTMARK_ADD_STUDENT, String.valueOf(userId));
		HttpHelper.get(this, url, TutorApplication.getHeaders(), params, new ObjectHttpResponseHandler<AddBookmarkResult>(AddBookmarkResult.class) {

			@Override
			public void onFailure(int status, String message) {
				toast(R.string.toast_server_error);
			}

			@Override
			public void onSuccess(AddBookmarkResult result) {
				if (result.getStatusCode() == HttpURLConnection.HTTP_OK) {
					toast(R.string.toast_add_to_bookmark_success);
				}
			}
		});
	}

	/**
	 * 获取学生详细资料
	 * 
	 * @param id
	 */
	private void getDetails() {
		RequestParams params = new RequestParams();
		params.put("memberId", id);
		HttpHelper.get(this, ApiUrl.STUDENTINFO, TutorApplication.getHeaders(), params, new ObjectHttpResponseHandler<UserInfoResult>(UserInfoResult.class) {

			@Override
			public void onFailure(int status, String message) {
				if (0 == status) {
					getDetails();
					return;
				}
				dismissDialog();
				CheckTokenUtils.checkToken(status);
				toast(R.string.toast_server_error);
			}

			@Override
			public void onSuccess(UserInfoResult t) {
				dismissDialog();
				if (t.getStatusCode() == HttpURLConnection.HTTP_OK) {
					userInfo = t.getResult();
					setData();
				} else {
					toast(t.getMessage());
				}
			}
		});
	}
}
