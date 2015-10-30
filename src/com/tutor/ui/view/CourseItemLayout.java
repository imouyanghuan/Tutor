package com.tutor.ui.view;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hk.tutor.R;
import com.tutor.model.Course;
import com.tutor.model.CourseItem1;
import com.tutor.model.CourseItem2;
import com.tutor.util.ViewHelper;

/**
 * @author bruce.chen
 * 
 *         2015-9-2
 */
public class CourseItemLayout extends LinearLayout {

	private Course course;
	private ArrayList<CourseAdapter> adapters = new ArrayList<CourseItemLayout.CourseAdapter>();

	public CourseItemLayout(Context context, Course course, OnRefreshListener listener) {
		super(context);
		this.course = course;
		init(listener);
	}

	private void init(OnRefreshListener listener) {
		setOrientation(LinearLayout.VERTICAL);
		if (null == course) {
			return;
		}
		View root = LayoutInflater.from(getContext()).inflate(R.layout.course_item, this);
		TextView title = ViewHelper.get(root, R.id.course_item_title1);
		title.setText(course.getName());
		ArrayList<CourseItem1> arrayList = course.getResult();
		CustomExpandableListView expandableListView = ViewHelper.get(root, R.id.course_item_expandableListView);
		if (null != arrayList) {
			CourseAdapter adapter = new CourseAdapter(getContext(), arrayList, listener);
			adapters.add(adapter);
			expandableListView.setAdapter(adapter);
			// for (CourseItem1 courseItem : arrayList) {
			// CourseItem2Layout layout = new CourseItem2Layout(getContext(),
			// courseItem, listener);
			// addView(layout);
			// }
		}
	}

	public void refresh() {
		if (adapters != null) {
			for (CourseAdapter adapter : adapters) {
				adapter.notifyDataSetChanged();
			}
		}
	}

	public CourseItemLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CourseItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public interface OnRefreshListener {

		public void refresh(CourseItem2 courseItem2);
	}

	class CourseAdapter extends BaseExpandableListAdapter {

		private OnRefreshListener onRefreshListener;
		private LayoutInflater inflater;
		private ArrayList<CourseItem1> list;

		public CourseAdapter(Context context, ArrayList<CourseItem1> list, OnRefreshListener listener) {
			inflater = LayoutInflater.from(context);
			this.list = list;
			this.onRefreshListener = listener;
		}

		@Override
		public CourseItem2 getChild(int arg0, int arg1) {
			return getGroup(arg0).getResult().get(arg1);
		}

		@Override
		public long getChildId(int arg0, int arg1) {
			return arg1;
		}

		@Override
		public int getChildrenCount(int arg0) {
			return getGroup(arg0).getResult().size();
		}

		@Override
		public CourseItem1 getGroup(int arg0) {
			return list.get(arg0);
		}

		@Override
		public int getGroupCount() {
			return list.size();
		}

		@Override
		public long getGroupId(int arg0) {
			return arg0;
		}

		@Override
		public View getChildView(int arg0, int arg1, boolean arg2, View view, ViewGroup arg4) {
			ChildHolder childHolder = null;
			if (null == view) {
				view = inflater.inflate(R.layout.course_item_layout, null);
				childHolder = new ChildHolder();
				childHolder.name = ViewHelper.get(view, R.id.course_item_gv_item_name);
				childHolder.box = ViewHelper.get(view, R.id.course_item_gv_item_checkbox);
				view.setTag(childHolder);
			} else {
				childHolder = (ChildHolder) view.getTag();
			}
			final CourseItem2 courseItem2 = getChild(arg0, arg1);
			childHolder.name.setText(courseItem2.getCourseName());
			childHolder.box.setChecked(courseItem2.isChecked());
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (null == onRefreshListener) {
						courseItem2.setChecked(!courseItem2.isChecked());
						notifyDataSetChanged();
					} else {
						onRefreshListener.refresh(courseItem2);
					}
				}
			});
			return view;
		}

		@Override
		public View getGroupView(int arg0, boolean arg1, View view, ViewGroup arg3) {
			GroupHolder groupHolder = null;
			if (null == view) {
				view = inflater.inflate(R.layout.course_grade_item, null);
				groupHolder = new GroupHolder();
				groupHolder.name = ViewHelper.get(view, R.id.course_grade_item_tv);
				view.setTag(groupHolder);
			} else {
				groupHolder = (GroupHolder) view.getTag();
			}
			groupHolder.name.setText(getGroup(arg0).getName());
			return view;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int arg0, int arg1) {
			return false;
		}

		private class GroupHolder {

			public TextView name;
		}

		private class ChildHolder {

			public CheckBox box;
			public TextView name;
		}
	}
}
