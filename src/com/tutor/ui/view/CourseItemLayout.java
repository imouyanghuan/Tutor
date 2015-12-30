package com.tutor.ui.view;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hk.tutor.R;
import com.tutor.TutorApplication;
import com.tutor.model.Course;
import com.tutor.model.CourseItem1;
import com.tutor.model.CourseItem2;
import com.tutor.model.SubjectCourseModel;
import com.tutor.model.SubjectModel;
import com.tutor.params.Constants;
import com.tutor.util.ViewHelper;

/**
 * @author bruce.chen
 * 
 *         2015-9-2
 */
public class CourseItemLayout extends LinearLayout {

	private Course course;
	private ArrayList<SubjectCourseModel> subject;
	private boolean isFromToBeMyStudent;
	private ArrayList<CourseAdapter> adapters = new ArrayList<CourseItemLayout.CourseAdapter>();
	private ArrayList<SubjdectAdapter> subjectAdapters = new ArrayList<CourseItemLayout.SubjdectAdapter>();

	public CourseItemLayout(Context context) {
		super(context);
	}

	@SuppressWarnings("unchecked")
	public void setData(Object obj, OnRefreshListener listener, OnSelectedCourseValuesListener selectedValueListener, boolean isFromToBeMyStudent) {
		this.isFromToBeMyStudent = isFromToBeMyStudent;
		if (TutorApplication.getRole() == Constants.General.ROLE_STUDENT || isFromToBeMyStudent) {
			this.course = (Course) obj;
		} else {
			this.subject = (ArrayList<SubjectCourseModel>) obj;
		}
		init(listener, selectedValueListener);
	}

	private void init(OnRefreshListener listener, OnSelectedCourseValuesListener selectedValueListener) {
		setOrientation(LinearLayout.VERTICAL);
		View root = LayoutInflater.from(getContext()).inflate(R.layout.course_item, this);
		TextView title = ViewHelper.get(root, R.id.course_item_title1);
		CustomExpandableListView expandableListView = ViewHelper.get(root, R.id.course_item_expandableListView);
		//
		if (TutorApplication.getRole() == Constants.General.ROLE_STUDENT || isFromToBeMyStudent) {
			if (null == course) {
				return;
			}
			// View root =
			// LayoutInflater.from(getContext()).inflate(R.layout.course_item,
			// this);
			// TextView title = ViewHelper.get(root, R.id.course_item_title1);
			title.setText(course.getName());
			ArrayList<CourseItem1> arrayList = course.getResult();
			// CustomExpandableListView expandableListView =
			// ViewHelper.get(root, R.id.course_item_expandableListView);
			if (null != arrayList) {
				CourseAdapter adapter = new CourseAdapter(getContext(), arrayList, listener, selectedValueListener);
				adapters.add(adapter);
				expandableListView.setAdapter(adapter);
			}
		} else {
			if (null == subject) {
				return;
			}
			// View root =
			// LayoutInflater.from(getContext()).inflate(R.layout.course_item,
			// this);
			// TextView title = ViewHelper.get(root, R.id.course_item_title1);
			// 不需要显示一级title
			title.setVisibility(View.GONE);
			// title.setText(course.getName());
			// ArrayList<SubjectModel> arrayList = subject.getSubjects();
			// CustomExpandableListView expandableListView =
			// ViewHelper.get(root, R.id.course_item_expandableListView);
			SubjdectAdapter subjectAdapter = new SubjdectAdapter(getContext(), subject, listener, selectedValueListener);
			subjectAdapters.add(subjectAdapter);
			expandableListView.setAdapter(subjectAdapter);
		}
	}

	public void refresh() {
		if (TutorApplication.getRole() == Constants.General.ROLE_STUDENT || isFromToBeMyStudent) {
			if (adapters != null) {
				for (CourseAdapter adapter : adapters) {
					adapter.notifyDataSetChanged();
				}
			}
		} else {
			if (subjectAdapters != null) {
				for (SubjdectAdapter subjectAdapter : subjectAdapters) {
					subjectAdapter.notifyDataSetChanged();
				}
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

		public void refresh(Object obj);
	}

	class CourseAdapter extends BaseExpandableListAdapter {

		private OnRefreshListener onRefreshListener;
		public OnSelectedCourseValuesListener onSelectedCourseValuesListener;
		private LayoutInflater inflater;
		private ArrayList<CourseItem1> list;

		public CourseAdapter(Context context, ArrayList<CourseItem1> list, OnRefreshListener listener, OnSelectedCourseValuesListener selectedValueListener) {
			inflater = LayoutInflater.from(context);
			this.list = list;
			this.onRefreshListener = listener;
			this.onSelectedCourseValuesListener = selectedValueListener;
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
			childHolder.box.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (null == onRefreshListener) {
						courseItem2.setChecked(!courseItem2.isChecked());
						notifyDataSetChanged();
					} else {
						onRefreshListener.refresh(courseItem2);
					}
					// if (onSelectedCourseValuesListener != null) {
					// onSelectedCourseValuesListener.onSelectedValues(list);
					// }
				}
			});
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (null == onRefreshListener) {
						courseItem2.setChecked(!courseItem2.isChecked());
						notifyDataSetChanged();
					} else {
						onRefreshListener.refresh(courseItem2);
					}
					// if (onSelectedCourseValuesListener != null) {
					// onSelectedCourseValuesListener.onSelectedValues(list);
					// }
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

	class SubjdectAdapter extends BaseExpandableListAdapter {

		private OnRefreshListener onRefreshListener;
		public OnSelectedCourseValuesListener onSelectedCourseValuesListener;
		private LayoutInflater inflater;
		private ArrayList<SubjectCourseModel> subjectList;

		public SubjdectAdapter(Context context, ArrayList<SubjectCourseModel> list, OnRefreshListener listener, OnSelectedCourseValuesListener selectedValueListener) {
			inflater = LayoutInflater.from(context);
			this.subjectList = list;
			this.onRefreshListener = listener;
			this.onSelectedCourseValuesListener = selectedValueListener;
		}

		@Override
		public SubjectModel getChild(int arg0, int arg1) {
			return getGroup(arg0).getSubjects().get(arg1);
		}

		@Override
		public long getChildId(int arg0, int arg1) {
			return arg1;
		}

		@Override
		public int getChildrenCount(int arg0) {
			return getGroup(arg0).getSubjects().size();
		}

		@Override
		public SubjectCourseModel getGroup(int arg0) {
			return subjectList.get(arg0);
		}

		@Override
		public int getGroupCount() {
			return subjectList.size();
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
			final SubjectModel subjectModel = getChild(arg0, arg1);
			childHolder.name.setText(TextUtils.isEmpty(subjectModel.getName()) ? getGroup(arg0).getCategory() : subjectModel.getName());
			childHolder.box.setChecked(subjectModel.isChecked());
			childHolder.box.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (null == onRefreshListener) {
						subjectModel.setChecked(!subjectModel.isChecked());
						notifyDataSetChanged();
					} else {
						onRefreshListener.refresh(subjectModel);
					}
					if (onSelectedCourseValuesListener != null) {
						onSelectedCourseValuesListener.onSelectedValues(subjectList);
					}
				}
			});
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (null == onRefreshListener) {
						subjectModel.setChecked(!subjectModel.isChecked());
						notifyDataSetChanged();
					} else {
						onRefreshListener.refresh(subjectModel);
					}
					if (onSelectedCourseValuesListener != null) {
						onSelectedCourseValuesListener.onSelectedValues(subjectList);
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
			groupHolder.name.setText(getGroup(arg0).getCategory());
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

	public interface OnSelectedCourseValuesListener {

		public void onSelectedValues(Object obj);
	}
}
