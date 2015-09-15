package com.tutor.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mssky.mobile.ui.view.CircleImageView;
import com.tutor.R;
import com.tutor.model.IMMessage;
import com.tutor.util.ImageUtils;

public class ChatAdapter extends TutorBaseAdapter<IMMessage> {

	private OnReSendListener reSendListener;

	public ChatAdapter(Context mContext, List<IMMessage> mData) {
		super(mContext, mData, R.layout.chat_item);
	}

	public void refreshData(List<IMMessage> mData) {
		this.mList = mData;
		notifyDataSetChanged();
	}

	public void setReSendListener(OnReSendListener reSendListener) {
		this.reSendListener = reSendListener;
	}

	@Override
	protected void convert(ViewHolder holder, final IMMessage t, int position) {
		// 布局
		LinearLayout receiveLayout = holder.getView(R.id.chat_item_layout_receive);
		LinearLayout sendLayout = holder.getView(R.id.chat_item_layout_send);
		// 时间
		TextView time = holder.getView(R.id.chat_item_tv_time);
		// 头像
		CircleImageView avatarReceive = holder.getView(R.id.chat_item_civ_receive);
		CircleImageView avatarSend = holder.getView(R.id.chat_item_civ_send);
		// 消息
		TextView receiveTv = holder.getView(R.id.chat_item_tv_receive);
		TextView sendTv = holder.getView(R.id.chat_item_tv_send);
		// 状态
		ImageView status = holder.getView(R.id.chat_item_iv_sendStatus);
		status.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (null != reSendListener) {
					v.setVisibility(View.GONE);
					reSendListener.onReSend(t);
				}
			}
		});
		time.setText(t.getTime().substring(5, t.getTime().length() - 7));
		if (IMMessage.CHAT_MESSAGE_TYPE_RECEIVE == t.getMsgType()) {
			sendLayout.setVisibility(View.GONE);
			receiveLayout.setVisibility(View.VISIBLE);
			ImageUtils.loadImage(avatarReceive, "drawable://" + R.drawable.def_avatar);
			receiveTv.setText(t.getContent());
		} else if (IMMessage.CHAT_MESSAGE_TYPE_SEND == t.getMsgType()) {
			receiveLayout.setVisibility(View.GONE);
			sendLayout.setVisibility(View.VISIBLE);
			ImageUtils.loadImage(avatarSend, "drawable://" + R.drawable.def_avatar);
			sendTv.setText(t.getContent());
			if (IMMessage.SEND_STATUS_SUCCESS == t.getSendStatus()) {
				status.setVisibility(View.GONE);
			} else {
				status.setVisibility(View.VISIBLE);
			}
		}
	}

	public interface OnReSendListener {

		public void onReSend(IMMessage message);
	}
}
