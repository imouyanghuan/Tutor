package com.tutor.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.tutor.R;
import com.tutor.model.RatingModel;
import com.tutor.params.ApiUrl;
import com.tutor.util.ImageUtils;

public class RateCommentsAdapter extends TutorBaseAdapter<RatingModel> {

	public RateCommentsAdapter(Context mContext, List<RatingModel> mData) {
		super(mContext, mData, R.layout.layout_rate_comment_item);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void convert(ViewHolder holder, RatingModel rateModel, int position) {
		// avatar
		ImageView ivAvatar = holder.getView(R.id.iv_avatar);
		ImageUtils.loadImage(ivAvatar, ApiUrl.DOMAIN + rateModel.getAvatar());
		// name
		TextView tvName = holder.getView(R.id.tv_user_name);
		String name = rateModel.getUserName();
		if (TextUtils.isEmpty(name)) {
			name = rateModel.getNickName();
		}
		tvName.setText(name);
		// create time
		TextView tvCreateTime = holder.getView(R.id.tv_create_time);
		tvCreateTime.setText(rateModel.getCreatedTime());
		//
		RatingBar rbTeachingContent = holder.getView(R.id.rate_teaching_content);
		rbTeachingContent.setRating(rateModel.getTeachingContentGrade());
		RatingBar rateAttitudeAndPatience = holder.getView(R.id.rate_attitude_and_patience);
		rateAttitudeAndPatience.setRating(rateModel.getAttributeAndPatienceGrade());
		RatingBar ratePunctuality = holder.getView(R.id.rate_punctuality);
		ratePunctuality.setRating(rateModel.getPunctualityGrade());
		RatingBar rbCommunicationSkill = holder.getView(R.id.rate_communication_skill);
		rbCommunicationSkill.setRating(rateModel.getCommunicationSkillGrade());
		// comment
		TextView tvComment = holder.getView(R.id.tv_comment);
		String comment = rateModel.getComment();
		if (!TextUtils.isEmpty(comment)) {
			tvComment.setText(comment);
			tvComment.setVisibility(View.VISIBLE);
		} else {
			tvComment.setText("");
			tvComment.setVisibility(View.GONE);
		}
	}
}
