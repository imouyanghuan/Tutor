package com.tutor.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.hk.tutor.R;
import com.tutor.model.RatingModel;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.util.ImageUtils;

public class RateCommentsAdapter extends TutorBaseAdapter<RatingModel> {

	private int role = Constants.General.ROLE_TUTOR;

	public RateCommentsAdapter(Context mContext, List<RatingModel> mData) {
		super(mContext, mData, R.layout.layout_rate_comment_item);
	}

	public void setRole(int role) {
		this.role = role;
	}

	@Override
	protected void convert(ViewHolder holder, RatingModel rateModel, int position) {
		// avatar
		ImageView ivAvatar = holder.getView(R.id.iv_avatar);
		ImageUtils.loadImage(ivAvatar, ApiUrl.DOMAIN + rateModel.getAvatar(), Constants.General.MALE);
		// name
		TextView tvName = holder.getView(R.id.tv_user_name);
		String name = rateModel.getUserName();
		if (TextUtils.isEmpty(name)) {
			name = rateModel.getNickName();
		}
		tvName.setText(name);
		// create time
		String createTime = rateModel.getCreatedTime();
		if (!TextUtils.isEmpty(createTime) && createTime.length() > 11) {
			holder.setText(R.id.tv_create_time, createTime.substring(0, 11));
		} else {
			holder.setText(R.id.tv_create_time, createTime);
		}
		//
		RatingBar rbTeachingContent = holder.getView(R.id.rate_teaching_content);
		rbTeachingContent.setRating(rateModel.getTeachingContentGrade());
		
		RatingBar rateAttitudeAndPatience = holder.getView(R.id.rate_attitude_and_patience);
		TextView tvAttitudeAndPatience = holder.getView(R.id.tvAttitudeAndPatience);
		
		RatingBar ratePunctuality = holder.getView(R.id.rate_punctuality);
		TextView tvPunctuality = holder.getView(R.id.tvPunctuality);
		
		RatingBar rbCommunicationSkill = holder.getView(R.id.rate_communication_skill);
		TextView tvCommunicationSkill = holder.getView(R.id.tvCommunicationSkill);
		
		if (role == Constants.General.ROLE_TUTOR) {
			rateAttitudeAndPatience.setRating(rateModel.getAttributeAndPatienceGrade());
			ratePunctuality.setRating(rateModel.getPunctualityGrade());
			rbCommunicationSkill.setRating(rateModel.getCommunicationSkillGrade());
			
			tvAttitudeAndPatience.setText(R.string.label_attitude_and_patience);
			tvPunctuality.setText(R.string.label_punctuality);
			tvCommunicationSkill.setText(R.string.label_communication_skill);
			tvCommunicationSkill.setVisibility(View.VISIBLE);
			rbCommunicationSkill.setVisibility(View.VISIBLE);
		} else if (role == Constants.General.ROLE_TUITION_SCHOOL) {
			rateAttitudeAndPatience.setRating(rateModel.getQualityOfTutors());
			ratePunctuality.setRating(rateModel.getLearningEnvironment());
			
			tvAttitudeAndPatience.setText(R.string.label_quality_of_tutors);
			tvPunctuality.setText(R.string.label_learning_environment);
			tvCommunicationSkill.setVisibility(View.GONE);
			rbCommunicationSkill.setVisibility(View.GONE);
		}
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
