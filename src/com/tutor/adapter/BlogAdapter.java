package com.tutor.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.hk.tutor.R;
import com.tutor.model.BlogModel;
import com.tutor.params.Constants;
import com.tutor.ui.activity.BlogDetailActivity;
import com.tutor.util.ImageUtils;

/**
 * blog適配器
 * 
 * @author jerry.yao
 * 
 *         2015-12-9
 */
public class BlogAdapter extends TutorBaseAdapter<BlogModel> {

	public BlogAdapter(Context mContext, List<BlogModel> mData) {
		super(mContext, mData, R.layout.layout_blog);
	}

	public void refresh(List<BlogModel> mData) {
		this.mList = mData;
		notifyDataSetChanged();
	}

	@Override
	protected void convert(ViewHolder holder, final BlogModel blog, int position) {
		// blog图片
		ImageView avatar = holder.getView(R.id.iv_blog);
		ImageUtils.loadImage(avatar, blog.getMainImage(), Constants.General.MALE);
		// 标题
		holder.setText(R.id.tv_title, blog.getTitle());
		// 简介
		holder.setText(R.id.tv_content, blog.getSummary());
		// 时间
		String time = blog.getCreatedTime();
		if (!TextUtils.isEmpty(time) && time.length() > 11) {
			time = time.substring(0, 11);
		}
		holder.setText(R.id.tv_time, time);
		// item點擊事件
		holder.getView(R.id.ll_blog_list_item).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(mContext, BlogDetailActivity.class);
				intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_BLOG, blog);
				mContext.startActivity(intent);
			}
		});
	}
}
