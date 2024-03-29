package com.tutor.util;

import android.util.SparseArray;
import android.view.View;

public class ViewHelper {

	@SuppressWarnings("unchecked")
	public static <T extends View> T get(View view, int id) {
		SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
		if (viewHolder == null) {
			viewHolder = new SparseArray<View>();
			view.setTag(viewHolder);
		}
		View childView = viewHolder.get(id);
		if (childView == null) {
			childView = view.findViewById(id);
			viewHolder.put(id, childView);
		}
		T t = null;
		try {
			t = (T) childView;
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
		return t;
	}
}
