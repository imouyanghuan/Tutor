package com.tutor.ui.activity;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.tutor.R;
import com.tutor.adapter.NotificationAdapter;
import com.tutor.adapter.NotificationAdapter.OnDeleteItemClickListener;
import com.tutor.im.IMMessageManager;
import com.tutor.model.IMMessage;
import com.tutor.params.Constants;
import com.tutor.ui.view.TitleBar;

/**
 * 消息
 * 
 * @author bruce.chen
 * 
 *         2015-8-26
 */
public class NotificationActivity extends BaseActivity {

	private ListView lvNotification;
	private NotificationAdapter mAdapter;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_notification);
		initView();
	}

	@Override
	protected void initView() {
		TitleBar bar = getView(R.id.title_bar);
		bar.initBack(this);
		bar.setTitle(R.string.notification);
		lvNotification = getView(R.id.lv_notifications);
		mAdapter = new NotificationAdapter(NotificationActivity.this, getMessages());
		// 左滑删除
		mAdapter.setOnDeleteItemClickListener(new OnDeleteItemClickListener() {

			@Override
			public void onClick(String fromSubJid, int position) {
				deleteMessages(fromSubJid);
			}
		});
		lvNotification.setAdapter(mAdapter);
		lvNotification.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				IMMessage msg = mAdapter.getItem(position);
				if (msg == null) {
					return;
				}
				String imId = msg.getFromSubJid();
				if (!TextUtils.isEmpty(imId)) {
					Intent intent = new Intent(NotificationActivity.this, ChatActivity.class);
					intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_MESSAGE_TO, imId);
					startActivity(intent);
				}
			}
		});
	}

	/**
	 * 获取聊天记录
	 * 
	 * @return
	 */
	private List<IMMessage> getMessages() {
		List<IMMessage> messages = IMMessageManager.getManager().getRecentContactsWithLastMsg();
		return messages;
	}

	/**
	 * 删除聊天记录
	 * 
	 * @return
	 */
	private void deleteMessages(String fromSubJid) {
		boolean isDelete = IMMessageManager.getManager().deleteMsgWhereJid(fromSubJid);
		if (isDelete) {
			if (mAdapter != null) {
				mAdapter.refresh(getMessages());
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mAdapter != null) {
			mAdapter.refresh(getMessages());
		}
	}
}
