package com.tutor.ui.activity;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.mssky.mobile.ui.view.PullToRefreshBase;
import com.mssky.mobile.ui.view.PullToRefreshBase.Mode;
import com.mssky.mobile.ui.view.PullToRefreshBase.OnRefreshListener;
import com.mssky.mobile.ui.view.PullToRefreshListView;
import com.tutor.R;
import com.tutor.TutorApplication;
import com.tutor.adapter.ChatAdapter;
import com.tutor.adapter.ChatAdapter.OnReSendListener;
import com.tutor.im.ContactManager;
import com.tutor.model.IMMessage;
import com.tutor.model.User;
import com.tutor.params.Constants;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.StringUtils;
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
	private User user;

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
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							index = adapter.getCount();
							return addNewMsg();
						}

						protected void onPostExecute(Boolean result) {
							lvChat.onRefreshComplete();
							if (!addNewMsg()) {
								ToastUtil.showToastShort(ChatActivity.this, "没有更多聊天记录了!");
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
		bar.setTitle("聊天");
		bar.initBack(this);
		user = ContactManager.getManager().getByUserJid(chatWithJid, TutorApplication.connectionManager.getConnection());
		String title = null;
		if (null != user) {
			title = user.getName() == null ? StringUtils.getUserNameByJid(chatWithJid) : user.getName();
			if (null != user.getStatus()) {
				title = title + "(" + user.getStatus() + ")";
			} else {
				title = StringUtils.getUserNameByJid(chatWithJid) + "(离线)";
			}
		} else {
			title = StringUtils.getUserNameByJid(chatWithJid) + "(离线)";
		}
		bar.setTitle(title);
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
			System.out.println(acion);
			if (!Constants.Action.ACTION_ROSTER_PRESENCE_CHANGED.equals(acion)) {
				return;
			}
			User user1 = (User) intent.getSerializableExtra(Constants.IntentExtra.INTENT_EXTRA_USER_KEY);
			System.out.println(user1);
			if (user1 != null) {
				if (user1.getJID().equals(user.getJID())) {
					String title = user1.getName() == null ? StringUtils.getUserNameByJid(chatWithJid) : user1.getName();
					if (null != user1.getStatus()) {
						title = title + "(" + user1.getStatus() + ")";
					} else {
						title = title + "(离线)";
					}
					bar.setTitle(title);
					user = user1;
				}
			}
		}
	};
	private TitleBar bar;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.edit_chat_btn_send:
				String msg = etMessage.getText().toString().trim();
				// 發送聊天內容
				sendMessage(msg);
				etMessage.setText("");
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
		ToastUtil.showToastShort(this, "重新发送");
	}
}
