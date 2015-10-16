package com.tutor.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mssky.mobile.ui.zlistview.BaseSwipeAdapter;
import com.mssky.mobile.ui.zlistview.DragEdge;
import com.mssky.mobile.ui.zlistview.ShowMode;
import com.mssky.mobile.ui.zlistview.ZSwipeItem;
import com.tutor.R;
import com.tutor.model.IMMessage;
import com.tutor.params.Constants;
import com.tutor.util.ImageUtils;

/**
 * 通知信息適配器
 * 
 * @author jerry.yao
 * 
 *         2015-9-18
 */
public class NotificationAdapter extends BaseSwipeAdapter {

	private Context mContext;
	private List<IMMessage> mList;
	private ZSwipeItem swipeItem;

	public NotificationAdapter(Context mContext, List<IMMessage> mList) {
		this.mContext = mContext;
		this.mList = mList;
	}

	public void refresh(List<IMMessage> mData) {
		this.mList = mData;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public IMMessage getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getSwipeLayoutResourceId(int position) {
		return R.id.swipe_item;
	}

	@Override
	public View generateView(int position, ViewGroup parent) {
		return View.inflate(mContext, R.layout.layout_notification_list_item, null);
	}

	@Override
	public void fillValues(final int position, View convertView) {
		ImageView imgAvatar = (ImageView) convertView.findViewById(R.id.student_list_item_avatar);
		TextView msgNickName = (TextView) convertView.findViewById(R.id.student_list_item_nick);
		TextView time = (TextView) convertView.findViewById(R.id.student_list_item_time);
		TextView msg = (TextView) convertView.findViewById(R.id.student_list_item_area);
		TextView msgCount = (TextView) convertView.findViewById(R.id.student_list_item_form);
		// 左滑删除item
		swipeItem = (ZSwipeItem) convertView.findViewById(R.id.swipe_item);
		TextView tvAccept = (TextView) convertView.findViewById(R.id.tv_accept);
		tvAccept.setVisibility(View.GONE);
		tvAccept.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (onAcceptItemClickListener != null) {
					onAcceptItemClickListener.onClick(Constants.General.ACCEPT, mList.get(position).getId());
				}
			}
		});
		TextView tvReject = (TextView) convertView.findViewById(R.id.tv_reject);
		tvReject.setVisibility(View.GONE);
		tvReject.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (onRejectItemClickListener != null) {
					onRejectItemClickListener.onClick(Constants.General.REJECT, mList.get(position).getId());
				}
			}
		});
		TextView tvDelete = (TextView) convertView.findViewById(R.id.tv_delete);
		tvDelete.setVisibility(View.GONE);
		// 删除按钮显示的模式
		swipeItem.setShowMode(ShowMode.PullOut);
		// 划出的模式
		swipeItem.setDragEdge(DragEdge.Right);
		tvDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (onDeleteItemClickListener != null) {
					String fromSubJid = mList.get(position).getFromSubJid();
					onDeleteItemClickListener.onClick(fromSubJid, position);
				}
				notifyDataSetInvalidated();
				swipeItem.close(isEmpty());
			}
		});
		if (mList != null) {
			ImageUtils.loadImage(imgAvatar, mList.get(position).getAvatar());
			if (mList.get(position).getMsgType() == IMMessage.MESSAGE_TYPE_SYS_MSG) {
				tvDelete.setVisibility(View.GONE);
			} else {
				tvDelete.setVisibility(View.VISIBLE);
			}
		}
		// 未读消息气泡
		int unReadCount = mList.get(position).getNoticeSum();
		if (unReadCount > 0) {
			msgCount.setText(String.valueOf(unReadCount));
			msgCount.setBackgroundResource(R.drawable.menu_msg_count_bg);
			msgCount.setTextColor(Color.WHITE);
			msgCount.setTextSize(12);
			msgCount.setGravity(Gravity.CENTER);
			msgCount.setVisibility(View.VISIBLE);
			msgCount.setPadding(2, 2, 2, 2);
		} else {
			msgCount.setText("");
			msgCount.setVisibility(View.INVISIBLE);
		}
		String msgFrom = mList.get(position).getFromUserName();
		if (TextUtils.isEmpty(msgFrom)) {
			msgNickName.setText("Tutor User");
		} else {
			msgNickName.setText(msgFrom);
		}
		String msgContent = mList.get(position).getContent();
		if (TextUtils.isEmpty(msgContent)) {
			msg.setText("");
		} else {
			msg.setText(msgContent);
		}
		String msgTime = mList.get(position).getTime();
		if (TextUtils.isEmpty(msgTime)) {
			time.setText("");
		} else {
			time.setText(msgTime.substring(0, 11));
		}
	}

	/**
	 * 点击左滑按钮回调
	 */
	private OnDeleteItemClickListener onDeleteItemClickListener;

	/**
	 * 点击左滑按钮回调接口
	 * 
	 * @param msgId
	 *            消息id
	 * @param position
	 *            索引
	 */
	public interface OnDeleteItemClickListener {

		public void onClick(String fromSubJid, int position);
	}

	/**
	 * 点击左滑按钮回调set方法
	 */
	public void setOnDeleteItemClickListener(OnDeleteItemClickListener onDeleteItemClickListener) {
		this.onDeleteItemClickListener = onDeleteItemClickListener;
	}

	/**
	 * 点击接受按钮回调
	 */
	private OnAcceptItemClickListener onAcceptItemClickListener;

	/**
	 * 点击接受按钮回调接口
	 * 
	 * @param msgId
	 *            消息id
	 * @param position
	 *            索引
	 */
	public interface OnAcceptItemClickListener {

		public void onClick(int status, String notificationId);
	}

	/**
	 * 点击接受按钮回调set方法
	 */
	public void setOnAcceptItemClickListener(OnAcceptItemClickListener onAcceptItemClickListener) {
		this.onAcceptItemClickListener = onAcceptItemClickListener;
	}

	/**
	 * 点击拒绝按钮回调
	 */
	private OnRejectItemClickListener onRejectItemClickListener;

	/**
	 * 点击拒绝按钮回调接口
	 * 
	 * @param msgId
	 *            消息id
	 * @param position
	 *            索引
	 */
	public interface OnRejectItemClickListener {

		public void onClick(int status, String notificationId);
	}

	/**
	 * 点击拒绝按钮回调set方法
	 */
	public void setOnRejectItemClickListener(OnRejectItemClickListener onRejectItemClickListener) {
		this.onRejectItemClickListener = onRejectItemClickListener;
	}
}
