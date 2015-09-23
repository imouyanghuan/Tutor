package com.tutor.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tutor.R;
import com.tutor.model.Course;
import com.tutor.model.CourseItem1;
import com.tutor.ui.view.CustomExpandableListView;

public class SearchConditionsAdapter extends BaseExpandableListAdapter {

	private Context mContext;
	private ArrayList<Course> groups;

	public SearchConditionsAdapter(Context mContext, ArrayList<Course> groups) {
		this.mContext = mContext;
		this.groups = groups;
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return groups.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return groups.get(groupPosition).getResult().size();
	}

	@Override
	public Course getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return groups.get(groupPosition);
	}

	@Override
	public CourseItem1 getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return groups.get(groupPosition).getResult().get(childPosition);
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
		//Course course = getGroup(groupPosition);
		holder.course.setText(getGroup(groupPosition).getName());
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
//		SubGroupHolder holder = null;
//		if (convertView == null) {
//			holder = new SubGroupHolder();
			convertView = View.inflate(mContext, R.layout.layout_course_subgroup_item, null);
			CustomExpandableListView lvGrade = (CustomExpandableListView) convertView.findViewById(R.id.lv_subGroup);
//			convertView.setTag(holder);
//		} else {
//			holder = (SubGroupHolder) convertView.getTag();
//		}
		GradeAdapter adapter = new GradeAdapter(mContext, getGroup(groupPosition).getResult());
		 lvGrade.setAdapter(adapter);
		// 取消默认箭头
		 lvGrade.setGroupIndicator(null);
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
		CustomExpandableListView lvGrade;
	}
}
