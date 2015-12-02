package com.tutor.adapter;

import java.util.List;

import android.content.Context;

import com.hk.tutor.R;
import com.tutor.model.FAQModel;

public class FAQAdapter extends TutorBaseAdapter<FAQModel> {

	public FAQAdapter(Context mContext, List<FAQModel> mData) {
		super(mContext, mData, R.layout.layout_faq_item);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void convert(ViewHolder holder, FAQModel faq, int position) {
		// TODO Auto-generated method stub
		holder.setText(R.id.tv_item, faq.getCategory());
	}
}
