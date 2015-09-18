package com.tutor.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mssky.mobile.ui.zlistview.BaseSwipeAdapter;
import com.mssky.mobile.ui.zlistview.DragEdge;
import com.mssky.mobile.ui.zlistview.ShowMode;
import com.mssky.mobile.ui.zlistview.ZSwipeItem;
import com.tutor.R;
import com.tutor.model.IMMessage;

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
		// 左滑删除item
		swipeItem = (ZSwipeItem) convertView.findViewById(R.id.swipe_item);
		LinearLayout llDelete = (LinearLayout) convertView.findViewById(R.id.ll_delete);
		// 删除按钮显示的模式
		swipeItem.setShowMode(ShowMode.PullOut);
		// 划出的模式
		swipeItem.setDragEdge(DragEdge.Right);
		llDelete.setOnClickListener(new OnClickListener() {

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
		imgAvatar.setImageResource(R.drawable.def_avatar);
		String msgFrom = mList.get(position).getFromSubJid();
		;
		if (TextUtils.isEmpty(msgFrom)) {
			msgNickName.setText("Tutor Student");
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
			time.setText(msgTime);
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
}
