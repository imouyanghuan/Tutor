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

import com.tutor.R;
import com.tutor.adapter.TutorBaseAdapter;
import com.tutor.adapter.ViewHolder;
import com.tutor.model.Area;
import com.tutor.model.Area1;
import com.tutor.util.ViewHelper;

/**
 * @author bruce.chen
 * 
 *         2015-9-2
 */
public class AreaItemLayout extends LinearLayout {

	private Area area;

	public AreaItemLayout(Context context, Area area) {
		super(context);
		this.area = area;
		init();
	}

	private void init() {
		setOrientation(LinearLayout.VERTICAL);
		if (null == area) {
			return;
		}
		View root = LayoutInflater.from(getContext()).inflate(R.layout.area_item, this);
		TextView title = ViewHelper.get(root, R.id.area_item_title);
		title.setText(area.getName());
		ArrayList<Area1> arrayList = area.getResult();
		if (null != arrayList) {
			CustomGridView gridView = ViewHelper.get(root, R.id.area_item_gv);
			AreaAdapter adapter = new AreaAdapter(getContext(), arrayList);
			gridView.setAdapter(adapter);
		}
	}

	public AreaItemLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AreaItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	private class AreaAdapter extends TutorBaseAdapter<Area1> {

		public AreaAdapter(Context mContext, List<Area1> mData) {
			super(mContext, mData, R.layout.area_item_layout);
		}

		@Override
		protected void convert(ViewHolder holder, Area1 t, int position) {
			holder.setText(R.id.area_item_gv_item_name, t.getAddress());
			CheckBox box = holder.getView(R.id.area_item_gv_item_checkbox);
			box.setChecked(t.getSelected());
		}
	}
}
