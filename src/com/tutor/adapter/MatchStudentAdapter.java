package com.tutor.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.tutor.R;
import com.tutor.model.Area;
import com.tutor.model.Area1;
import com.tutor.model.UserInfo;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.activity.PersonInfoActivity;
import com.tutor.util.ImageUtils;
import com.tutor.util.ImageUtils.CallBack;

/**
 * 匹配學生列表適配器
 * 
 * @author bruce.chen
 * 
 *         2015-9-7
 */
public class MatchStudentAdapter extends TutorBaseAdapter<UserInfo> {

	private ListView listView;

	public MatchStudentAdapter(Context mContext, List<UserInfo> mData, ListView listView) {
		super(mContext, mData, R.layout.student_list_item);
		this.listView = listView;
	}

	public void refresh(List<UserInfo> mData) {
		this.mList = mData;
		notifyDataSetChanged();
	}

	@Override
	protected void convert(ViewHolder holder, final UserInfo t, int position) {
		ImageView avatar = holder.getView(R.id.student_list_item_avatar);
		// ImageUtils.loadImageSync(avatar, ApiUrl.DOMAIN + t.getAvatar());
		String path = ApiUrl.DOMAIN + t.getAvatar();
		avatar.setTag(path);
		Bitmap bitmap = ImageUtils.getUtils().loadImage(path, new CallBack() {

			@Override
			public void onSuccess(Bitmap bitmap, String path) {
				ImageView imageView = (ImageView) listView.findViewWithTag(path);
				if (null != imageView) {
					imageView.setImageBitmap(bitmap);
				}
			}
		});
		if (null != bitmap) {
			avatar.setImageBitmap(bitmap);
		} else {
			avatar.setImageResource(R.drawable.avatar);
		}
		holder.setText(R.id.student_list_item_nick, t.getEmail().substring(0, t.getEmail().indexOf("@")));
		ArrayList<Area> areas = t.getAreas();
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
			holder.setText(R.id.student_list_item_area, sb.toString());
		} else {
			holder.setText(R.id.student_list_item_area, R.string.label_none_area);
		}
		holder.setText(R.id.student_list_item_form, t.getGender());
		holder.setText(R.id.student_list_item_time, t.getCreatedTime().substring(0, 11));
		// 學生item點擊事件
		holder.getView(R.id.student_list_item_layout).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(mContext, PersonInfoActivity.class);
				intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_USER_INFO, t);
				mContext.startActivity(intent);
			}
		});
	}
}
