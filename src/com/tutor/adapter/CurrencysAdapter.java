package com.tutor.adapter;

import java.util.List;

import android.content.Context;

import com.tutor.R;
import com.tutor.model.Currency;

public class CurrencysAdapter extends TutorBaseAdapter<Currency> {

	public CurrencysAdapter(Context mContext, List<Currency> mData) {
		super(mContext, mData, R.layout.layout_course_item);
	}

	@Override
	protected void convert(ViewHolder holder, Currency t, int position) {
		holder.setText(R.id.tv_item, t.getText());
	}
}
