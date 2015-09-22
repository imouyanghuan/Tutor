package com.tutor.adapter;

import java.util.ArrayList;
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
import com.tutor.model.Area;
import com.tutor.model.Area1;
import com.tutor.model.BookmarkModel;
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
		LinearLayout llDelete = (LinearLayout) convertView.findViewById(R.id.ll_delete);
		// 删除按钮显示的模式
		swipeItem.setShowMode(ShowMode.PullOut);
		// 划出的模式
		swipeItem.setDragEdge(DragEdge.Right);
		llDelete.setOnClickListener(new OnClickListener() {

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
		// 头像
		if (TextUtils.isEmpty(mList.get(position).getMemberModel().getAvatar())) {
			ImageUtils.loadImage(imgAvatar, "drawable://" + R.drawable.avatar);
		} else {
			String path = ApiUrl.DOMAIN + mList.get(position).getMemberModel().getAvatar();
			ImageUtils.loadImage(imgAvatar, path);
		}
		
		if (TextUtils.isEmpty(mList.get(position).getMemberModel().getEmail())) {
			msgNickName.setText("Student");
		} else {
			if (mList.get(position).getMemberModel().getEmail().contains("@")) {
				msgNickName.setText(mList.get(position).getMemberModel().getEmail().substring(0, mList.get(position).getMemberModel().getEmail().lastIndexOf("@")));
			} else {
				msgNickName.setText(mList.get(position).getMemberModel().getEmail());
			}
		}
		
		ArrayList<Area> areas = mList.get(position).getMemberModel().getAreas();
		if (null != areas && 0 != areas.size()) {
			StringBuffer sb = new StringBuffer();
			for (Area area : areas) {
				ArrayList<Area1> area1s = area.getResult();
				for (Area1 area1 : area1s) {
					sb.append(area1.getAddress() + ",");
				}
			}
			// 刪除最後一個符號
			sb.deleteCharAt(sb.length() - 1);
			msg.setText(sb.toString());
		} else {
			msg.setText(R.string.label_none_area);
		}
		
		String createTime = mList.get(position).getMemberModel().getCreatedTime();
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
