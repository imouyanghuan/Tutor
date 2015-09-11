package com.tutor.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 自定义基本适配器
 * 
 * @author bruce.chen
 * 
 * @param <T>
 *            Java bean
 */
public abstract class TutorBaseAdapter<T> extends BaseAdapter {

	protected List<T> mList;
	private LayoutInflater inflater;
	private int layoutId;
	protected Context mContext;

	public TutorBaseAdapter(Context mContext, List<T> mData, int layoutId) {
		this.mContext = mContext;
		inflater = LayoutInflater.from(mContext);
		this.mList = mData;
		this.layoutId = layoutId;
	}

	@Override
	public int getCount() {
		return mList != null ? mList.size() : 0;
	}

	@Override
	public T getItem(int arg0) {
		return mList != null ? mList.get(arg0) : null;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		ViewHolder holder = null;
		if (view == null) {
			view = inflater.inflate(layoutId, null);
			holder = new ViewHolder(view);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		convert(holder, getItem(position), position);
		return view;
	}

	protected abstract void convert(ViewHolder holder, T t, int position);
}
