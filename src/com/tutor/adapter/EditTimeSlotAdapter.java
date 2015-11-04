package com.tutor.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.hk.tutor.R;
import com.tutor.model.EditTimeslot;

/**
 * @author bruce.chen
 * 
 *         2015-9-14
 */
public class EditTimeSlotAdapter extends TutorBaseAdapter<EditTimeslot> {

	private boolean delete;
	private String[] weeks;

	public EditTimeSlotAdapter(Context mContext, List<EditTimeslot> mData, boolean delete) {
		super(mContext, mData, R.layout.timeslot_list_item);
		this.delete = delete;
		weeks = mContext.getResources().getStringArray(R.array.weeks);
	}

	public void refresh(List<EditTimeslot> mData) {
		this.mList = mData;
		notifyDataSetChanged();
	}

	@Override
	protected void convert(ViewHolder holder, EditTimeslot t, final int position) {
		String startMinute = t.getStartMinute() + "";
		String endMinute = t.getEndMinute() + "";
		if (0 == t.getStartMinute()) {
			startMinute = "0" + startMinute;
		}
		if (0 == t.getEndMinute()) {
			endMinute = "0" + endMinute;
		}
		holder.setText(R.id.timeslot_item_tv, weeks[t.getDayOfWeek()] + ":  " + t.getStartHour() + ":" + startMinute + " -- " + t.getEndHour() + ":" + endMinute);
		if (delete) {
			Button button = holder.getView(R.id.timeslot_item_btn_delete);
			button.setVisibility(View.VISIBLE);
			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mList.remove(position);
					notifyDataSetChanged();
				}
			});
		}
	}
}
