package com.tutor.ui.activity;

import java.net.HttpURLConnection;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.loopj.android.http.RequestParams;
import com.tutor.R;
import com.tutor.TutorApplication;
import com.tutor.im.ContactManager;
import com.tutor.im.XMPPConnectionManager;
import com.tutor.model.AddBookmarkResult;
import com.tutor.model.UserInfo;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.HttpHelper;
import com.tutor.util.ObjectHttpResponseHandler;

/**
 * 學生詳情界面
 * 
 * @author bruce.chen
 * 
 *         2015-8-24
 */
public class StudentInfoActivity extends BaseActivity implements OnClickListener {

	private String imId;
	private UserInfo userInfo;

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
		// 聊天id
		imId = userInfo.getImid();
		initView();
	}

	@Override
	protected void initView() {
		TitleBar bar = getView(R.id.title_bar);
		String titleName = userInfo.getNickName();
		if (TextUtils.isEmpty(titleName)) {
			titleName = "PersonInfo";
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
		getView(R.id.tv_user_name);
		getView(R.id.tv_gender);
		getView(R.id.tv_major);
		getView(R.id.tv_student_count);
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
			// to be my tutor
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
}
