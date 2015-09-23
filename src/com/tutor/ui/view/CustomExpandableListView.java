package com.tutor.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

public class CustomExpandableListView extends ExpandableListView {

	public CustomExpandableListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public CustomExpandableListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public CustomExpandableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
