package com.tutor.ui.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hk.tutor.R;
import com.mssky.mobile.ui.view.CustomListView;
import com.tutor.adapter.TutorBaseAdapter;
import com.tutor.adapter.ViewHolder;
import com.tutor.model.CourseItem1;
import com.tutor.model.CourseItem2;
import com.tutor.util.ViewHelper;

/**
 * @author bruce.chen
 * 
 *         2015-9-2
 */
public class CourseItem2Layout extends LinearLayout {

	private CourseItem1 courseItem;

	public CourseItem2Layout(Context context, CourseItem1 item) {
		super(context);
		courseItem = item;
		init();
	}

	private void init() {
		setOrientation(LinearLayout.VERTICAL);
		if (null == courseItem) {
			return;
		}
		View view = LayoutInflater.from(getContext()).inflate(R.layout.course_item2, this);
		TextView title = ViewHelper.get(view, R.id.course_item_title2);
		title.setText(courseItem.getName());
		ArrayList<CourseItem2> list = courseItem.getResult();
		if (null != list) {
			CustomListView listView = ViewHelper.get(view, R.id.course_item_gv);
			CourseAdapter adapter = new CourseAdapter(getContext(), list);
			listView.setAdapter(adapter);
		}
	}

	public CourseItem2Layout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CourseItem2Layout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	private class CourseAdapter extends TutorBaseAdapter<CourseItem2> {

		public CourseAdapter(Context mContext, List<CourseItem2> mData) {
			super(mContext, mData, R.layout.course_item_layout);
		}

		@Override
		protected void convert(ViewHolder holder, final CourseItem2 t, int position) {
			holder.setText(R.id.course_item_gv_item_name, t.getCourseName());
			CheckBox box = holder.getView(R.id.course_item_gv_item_checkbox);
			box.setChecked(t.isChecked());
			box.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (t.isChecked()) {
						t.setChecked(false);
					} else {
						t.setChecked(true);
					}
					notifyDataSetChanged();
				}
			});
		}
	}
}
