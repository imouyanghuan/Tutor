package com.tutor.ui.activity;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.List;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.hk.tutor.R;
import com.loopj.android.http.RequestParams;
import com.mssky.mobile.ui.view.PullToRefreshBase;
import com.mssky.mobile.ui.view.PullToRefreshBase.Mode;
import com.mssky.mobile.ui.view.PullToRefreshBase.OnRefreshListener;
import com.mssky.mobile.ui.view.PullToRefreshListView;
import com.tutor.TutorApplication;
import com.tutor.adapter.ChatAdapter;
import com.tutor.adapter.ChatAdapter.OnReSendListener;
import com.tutor.adapter.ChatAdapter.OnReceiveAvatarClickListener;
import com.tutor.model.EditProfileResult;
import com.tutor.model.IMMessage;
import com.tutor.model.LogChat;
import com.tutor.model.UserInfo;
import com.tutor.model.UserInfoResult;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.CheckTokenUtils;
import com.tutor.util.HttpHelper;
import com.tutor.util.JsonUtil;
import com.tutor.util.LogUtils;
import com.tutor.util.ObjectHttpResponseHandler;
import com.tutor.util.ToastUtil;

/**
 * @author bruce.chen
 * 
 *         2015-8-28
 */
public class ChatActivity extends BaseChatActivity implements OnClickListener, OnReSendListener {

	private PullToRefreshListView lvChat;
	private EditText etMessage;
	private ChatAdapter adapter;
	private boolean isRefresh = false;
	// 与谁聊天
	// private User user;
	private TitleBar bar;
	private String toAvatar;
	private boolean isFromCourseSelection;
	private int gradeValue = -1;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_chat);
		initView();
	}

	@Override
	protected void initView() {
		initTitleBar();
		initListView();
		initCommonView();
	}

	private void initCommonView() {
		getView(R.id.edit_chat_btn_send).setOnClickListener(this);
		etMessage = getView(R.id.edit_chat_et);
	}

	private void initListView() {
		lvChat = getView(R.id.pullToRefresh);
		// 設置可上拉加載和下拉刷新
		lvChat.setMode(Mode.PULL_FROM_START);
		lvChat.setShowIndicator(false);
		adapter = new ChatAdapter(this, getMessages());
		adapter.setReSendListener(this);
		adapter.setOnReceiveAvatarClickListener(new OnReceiveAvatarClickListener() {

			@Override
			public void onClick(String imId) {
				if (imId.startsWith("0", 0) || imId.startsWith("1", 0)) {
					getUserInfo(imId);
				}
			}
		});
		// 设置对方的头像
		if (!TextUtils.isEmpty(toAvatar)) {
			adapter.setToAvatar(toAvatar);
		}
		lvChat.setAdapter(adapter);
		lvChat.getRefreshableView().setSelection(adapter.getCount() - 1);
		lvChat.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				if (!isRefresh) {
					isRefresh = true;
					new AsyncTask<Void, Void, Boolean>() {

						private int index;

						@Override
						protected Boolean doInBackground(Void... arg0) {
							index = adapter.getCount();
							return addNewMsg();
						}

						protected void onPostExecute(Boolean result) {
							lvChat.onRefreshComplete();
							if (!result) {
								ToastUtil.showToastShort(ChatActivity.this, R.string.toast_no_more_history);
								isRefresh = false;
								return;
							}
							resh();
							lvChat.getRefreshableView().setTranscriptMode(ListView.TRANSCRIPT_MODE_DISABLED);
							lvChat.getRefreshableView().setSelection(adapter.getCount() - index);
							isRefresh = false;
						};
					}.execute();
				}
			}
		});
	}

	private void initTitleBar() {
		bar = getView(R.id.title_bar);
		bar.setTitle(getString(R.string.label_chat));
		bar.initBack(this);
		String title = null;
		Intent intent = getIntent();
		if (intent != null) {
			// title bar
			title = intent.getStringExtra(Constants.General.NICKNAME);
			toAvatar = intent.getStringExtra(Constants.General.AVATAR);
			isFromCourseSelection = intent.getBooleanExtra(Constants.General.IS_FROM_COURSE_SELECTION, false);
			if (isFromCourseSelection) {
				gradeValue = intent.getIntExtra(Constants.General.GRADE_VALUE, -1);
			}
		}
		if (!TextUtils.isEmpty(title)) {
			bar.setTitle(title);
		} else {
			bar.setTitle("Tutor User");
		}
	}

	@Override
	protected void onResume() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.Action.ACTION_ROSTER_PRESENCE_CHANGED);
		registerReceiver(receiver, filter);
		super.onResume();
	}

	@Override
	protected void onPause() {
		unregisterReceiver(receiver);
		super.onPause();
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			String acion = intent.getAction();
			if (!Constants.Action.ACTION_ROSTER_PRESENCE_CHANGED.equals(acion)) {
				return;
			}
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.edit_chat_btn_send:
				String msg = etMessage.getText().toString().trim();
				if (!TextUtils.isEmpty(msg)) {
					// 發送聊天內容
					sendMessage(msg);
					etMessage.setText("");
					if (isFromCourseSelection) {
						// 发送log
						logChat();
					}
				} else {
					toast(R.string.toast_msg_cant_empty);
				}
				break;
			default:
				break;
		}
	}

	@Override
	protected void receiveNewMessage(IMMessage message) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void refreshMessage(List<IMMessage> messages) {
		adapter.refreshData(messages);
	}

	@Override
	public void onReSend(IMMessage message) {
		// TODO
		// ToastUtil.showToastShort(this, "重新发送");
	}

	/**
	 * 登记聊天会员信息
	 */
	private void logChat() {
		if (!HttpHelper.isNetworkConnected(this)) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		LogChat log = new LogChat();
		log.setGradeValue(gradeValue);
		log.setMemberId(TutorApplication.getMemberId());
		String body = JsonUtil.parseObject2Str(log);
		StringEntity entity = null;
		try {
			entity = new StringEntity(body, HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpHelper.post(this, ApiUrl.STUDY_ABROAD_LOGCHAT, TutorApplication.getHeaders(), entity, new ObjectHttpResponseHandler<EditProfileResult>(EditProfileResult.class) {

			@Override
			public void onFailure(int status, String message) {
				if (0 == status) {
					return;
				}
				CheckTokenUtils.checkToken(status);
			}

			@Override
			public void onSuccess(EditProfileResult result) {
				if (result.getStatusCode() == HttpURLConnection.HTTP_OK) {
					// TODO
					boolean isLog = result.getResult();
					LogUtils.e("Is Log Success?--------> " + isLog);
				} else {
					toast(result.getMessage());
				}
			}
		});
	}

	private void getUserInfo(final String imId) {
		String url = String.format(ApiUrl.IM_GET_INFO, imId);
		HttpHelper.get(TutorApplication.instance, url, TutorApplication.getHeaders(), new RequestParams(), new ObjectHttpResponseHandler<UserInfoResult>(UserInfoResult.class) {

			@Override
			public void onFailure(int status, String message) {
				// TODO Auto-generated method stub
				LogUtils.e("-----status:" + status + ",message +" + message);
			}

			@Override
			public void onSuccess(UserInfoResult info) {
				if (info.getStatusCode() == HttpURLConnection.HTTP_OK) {
					UserInfo userInfo = info.getResult();
					if (userInfo.getId() > 0) {
						Intent intent = null;
						if (TutorApplication.getRole() == Constants.General.ROLE_STUDENT) {
							intent = new Intent(ChatActivity.this, TeacherInfoActivity.class);
						} else {
							intent = new Intent(ChatActivity.this, StudentInfoActivity.class);
						}
						intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_USER_INFO, userInfo);
						startActivity(intent);
					}
				}
			}
		});
	}
}
