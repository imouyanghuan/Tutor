package com.tutor.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tutor.R;
import com.tutor.model.CourseItem1;
import com.tutor.model.CourseItem2;

public class GradeAdapter extends BaseExpandableListAdapter {

	private Context mContext;
	private ArrayList<CourseItem1> courseItems;

	public GradeAdapter(Context mContext, ArrayList<CourseItem1> courseItems) {
		this.mContext = mContext;
		this.courseItems = courseItems;
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return courseItems.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return courseItems.get(groupPosition).getResult().size();
	}

	@Override
	public CourseItem1 getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return courseItems.get(groupPosition);
	}

	@Override
	public CourseItem2 getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return courseItems.get(groupPosition).getResult().get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		GroupHolder holder = null;
		if (convertView == null) {
			holder = new GroupHolder();
			convertView = View.inflate(mContext, R.layout.layout_course_group_item, null);
			holder.course = (TextView) convertView.findViewById(R.id.friend_group_item_tv_name);
			holder.arrow = (ImageView) convertView.findViewById(R.id.friend_group_item_iv_arrow);
			convertView.setTag(holder);
		} else {
			holder = (GroupHolder) convertView.getTag();
		}
		if (isExpanded) {
			holder.arrow.setImageResource(R.drawable.spinner_down);
		} else {
			holder.arrow.setImageResource(R.drawable.spinner_right);
		}
		CourseItem1 courseItem = getGroup(groupPosition);
		holder.course.setText(courseItem.getName());
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		SubGroupHolder holder = null;
		if (convertView == null) {
			holder = new SubGroupHolder();
			convertView = View.inflate(mContext, R.layout.layout_course_item, null);
			holder.tvItem = (TextView) convertView.findViewById(R.id.tv_item);
			convertView.setTag(holder);
		} else {
			holder = (SubGroupHolder) convertView.getTag();
		}
		CourseItem2 courseItem2 = getChild(groupPosition, childPosition);
		holder.tvItem.setText(courseItem2.getCourseName());
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

	class GroupHolder {

		ImageView arrow;
		TextView course;
	}

	class SubGroupHolder {

		ImageView arrow;
		TextView tvItem;
	}
}
