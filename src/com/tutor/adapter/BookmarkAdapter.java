package com.tutor.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hk.tutor.R;
import com.mssky.mobile.ui.zlistview.BaseSwipeAdapter;
import com.mssky.mobile.ui.zlistview.DragEdge;
import com.mssky.mobile.ui.zlistview.ShowMode;
import com.mssky.mobile.ui.zlistview.ZSwipeItem;
import com.tutor.model.BookmarkModel;
import com.tutor.model.UserInfo;
import com.tutor.params.ApiUrl;
import com.tutor.util.ImageUtils;

/**
 * Bookmark適配器
 * 
 * @author jerry.yao
 * 
 *         2015-9-22
 */
public class BookmarkAdapter extends BaseSwipeAdapter {

	private Context mContext;
	private List<BookmarkModel> mList;
	private ZSwipeItem swipeItem;

	public BookmarkAdapter(Context mContext, List<BookmarkModel> mList) {
		this.mContext = mContext;
		this.mList = mList;
	}

	public void refresh(List<BookmarkModel> mData) {
		this.mList = mData;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public BookmarkModel getItem(int position) {
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
		return View.inflate(mContext, R.layout.layout_bookmark_list_item, null);
	}

	@Override
	public void fillValues(final int position, View convertView) {
		ImageView imgAvatar = (ImageView) convertView.findViewById(R.id.student_list_item_avatar);
		TextView msgNickName = (TextView) convertView.findViewById(R.id.student_list_item_nick);
		TextView time = (TextView) convertView.findViewById(R.id.student_list_item_time);
		TextView msg = (TextView) convertView.findViewById(R.id.student_list_item_area);
		// 左滑删除item
		swipeItem = (ZSwipeItem) convertView.findViewById(R.id.swipe_item);
		TextView tvDelete = (TextView) convertView.findViewById(R.id.tv_delete);
		// 删除按钮显示的模式
		swipeItem.setShowMode(ShowMode.PullOut);
		// 划出的模式
		swipeItem.setDragEdge(DragEdge.Right);
		tvDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (onDeleteItemClickListener != null) {
					int bookmarkId = mList.get(position).getBookmarkId();
					onDeleteItemClickListener.onClick(bookmarkId, position);
				}
				notifyDataSetInvalidated();
				swipeItem.close(isEmpty());
			}
		});
		UserInfo userInfo = getItem(position).getMemberModel();
		// 头像
		if (TextUtils.isEmpty(userInfo.getAvatar())) {
			ImageUtils.loadImage(imgAvatar, "drawable://" + R.drawable.avatar);
		} else {
			String path = ApiUrl.DOMAIN + userInfo.getAvatar();
			ImageUtils.loadImage(imgAvatar, path);
		}
		if (!TextUtils.isEmpty(userInfo.getNickName())) {
			msgNickName.setText(userInfo.getNickName());
		} else if (!TextUtils.isEmpty(userInfo.getUserName())) {
			msgNickName.setText(userInfo.getUserName());
		}
		if (!TextUtils.isEmpty(userInfo.getResidentialAddress())) {
			msg.setText(userInfo.getResidentialAddress());
		} else {
			msg.setText("");
		}
		String createTime = userInfo.getCreatedTime();
		if (TextUtils.isEmpty(createTime)) {
			time.setText("");
		} else {
			time.setText(createTime);
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

		public void onClick(int bookmarkId, int position);
	}

	/**
	 * 点击左滑按钮回调set方法
	 */
	public void setOnDeleteItemClickListener(OnDeleteItemClickListener onDeleteItemClickListener) {
		this.onDeleteItemClickListener = onDeleteItemClickListener;
	}
}
