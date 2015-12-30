package com.tutor.ui.activity;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.hk.tutor.R;
import com.tutor.TutorApplication;
import com.tutor.model.AddBookmarkResult;
import com.tutor.model.RatingModel;
import com.tutor.model.UserInfo;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.HttpHelper;
import com.tutor.util.ImageUtils;
import com.tutor.util.JsonUtil;
import com.tutor.util.ObjectHttpResponseHandler;

/**
 * 评价老师界面
 * 
 * @author jerry.yao
 * 
 *         2015-10-12
 */
public class RateTutorActivity extends BaseActivity implements OnClickListener {

	private UserInfo userInfo;
	private EditText etComment;
	private RatingBar rbTeachingContent;
	private RatingBar rbAttitudeAndPatience;
	private RatingBar rbPunctuality;
	private RatingBar rbCommunicationSkill;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_rate_teacher);
		initData();
	}

	private void initData() {
		Intent intent = getIntent();
		if (intent == null) {
			return;
		}
		userInfo = (UserInfo) intent.getSerializableExtra(Constants.IntentExtra.INTENT_EXTRA_USER_INFO);
		if (userInfo == null) {
			return;
		}
		initView();
	}

	@Override
	protected void initView() {
		// 头像和昵称
		ImageView ivAvatar = getView(R.id.iv_avatar);
		ImageUtils.loadImage(ivAvatar, ApiUrl.DOMAIN + userInfo.getAvatar(), userInfo.getGender() != null ? userInfo.getGender() : Constants.General.MALE);
		TextView tvUserName = getView(R.id.tv_user_name);
		etComment = getView(R.id.et_comment);
		// rate
		rbTeachingContent = getView(R.id.rate_teaching_content);
		rbAttitudeAndPatience = getView(R.id.rate_attitude_and_patience);
		TextView tvAttitudeAndPatience = getView(R.id.tv_attitude_and_patience);
		rbPunctuality = getView(R.id.rate_punctuality);
		TextView tvPunctuality = getView(R.id.tv_punctuality);
		rbCommunicationSkill = getView(R.id.rate_communication_skill);
		TextView tvCommunicationSkill = getView(R.id.tv_communication_skill);
		if (userInfo.getAccountType() == Constants.General.ROLE_TUTOR) {
			tvAttitudeAndPatience.setText(R.string.label_attitude_and_patience);
			tvPunctuality.setText(R.string.label_punctuality);
			tvCommunicationSkill.setText(R.string.label_communication_skill);
		} else if (userInfo.getAccountType() == Constants.General.ROLE_TUITION_SCHOOL) {
			tvAttitudeAndPatience.setText(R.string.label_quality_of_tutors);
			tvPunctuality.setText(R.string.label_learning_environment);
			tvCommunicationSkill.setVisibility(View.GONE);
			rbCommunicationSkill.setVisibility(View.GONE);
		}
		String nickName = userInfo.getNickName();
		if (TextUtils.isEmpty(nickName)) {
			nickName = userInfo.getUserName();
			if (TextUtils.isEmpty(nickName)) {
				nickName = "Tutor";
			}
		}
		tvUserName.setText(nickName);
		getView(R.id.btn_other_comments).setOnClickListener(this);
		// title bar
		TitleBar bar = getView(R.id.title_bar);
		bar.setTitle(R.string.label_rating);
		bar.initBack(this);
		bar.setRightText(R.string.label_rate, new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (rbTeachingContent.getRating() == 0 && rbAttitudeAndPatience.getRating() == 0 && rbPunctuality.getRating() == 0 && rbCommunicationSkill.getRating() == 0) {
					toast(R.string.toast_please_add_rating);
					return;
				} else if (etComment.getText().toString().length() <= 0) {
					toast(R.string.toast_please_enter_comment);
					return;
				}
				// save
				if (userInfo != null) {
					saveRating(userInfo.getId());
				}
			}
		});
	}

	/**
	 * 保存评分
	 * 
	 */
	private void saveRating(int tutorId) {
		if (!HttpHelper.isNetworkConnected(this)) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		RatingModel rating = new RatingModel();
		String url = "";
		if (userInfo.getAccountType() == Constants.General.ROLE_TUTOR) {
			url = String.format(ApiUrl.RATING, String.valueOf(tutorId));
			// rate
			rating.setAttributeAndPatienceGrade((int) rbAttitudeAndPatience.getRating());
			rating.setPunctualityGrade((int) rbPunctuality.getRating());
			rating.setCommunicationSkillGrade((int) rbCommunicationSkill.getRating());
		} else if (userInfo.getAccountType() == Constants.General.ROLE_TUITION_SCHOOL) {
			url = String.format(ApiUrl.RATING_TUITION_CENTER, String.valueOf(tutorId));
			// 评分
			rating.setQualityOfTutors((int) rbAttitudeAndPatience.getRating());
			rating.setLearningEnvironment((int) rbPunctuality.getRating());
		}
		// rating.setUserName("");
		// rating.setNickName("");
		rating.setTeachingContentGrade((int) rbTeachingContent.getRating());
		rating.setAvatar(ApiUrl.DOMAIN + ApiUrl.GET_OTHER_AVATAR + TutorApplication.getMemberId());
		rating.setComment(etComment.getText().toString().trim());
		String content = JsonUtil.parseObject2Str(rating);
		try {
			StringEntity entity = new StringEntity(content, HTTP.UTF_8);
			HttpHelper.getHelper().post(url, TutorApplication.getHeaders(), entity, new ObjectHttpResponseHandler<AddBookmarkResult>(AddBookmarkResult.class) {

				@Override
				public void onFailure(int status, String message) {
					toast(R.string.toast_server_error);
				}

				@Override
				public void onSuccess(AddBookmarkResult result) {
					if (result.getStatusCode() == HttpURLConnection.HTTP_OK) {
						toast(R.string.toast_rating_success);
						RateTutorActivity.this.finish();
					}
				}
			});
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// see other comments
			case R.id.btn_other_comments:
				if (userInfo != null) {
					Intent intent = new Intent(RateTutorActivity.this, RateCommentListActivity.class);
					intent.putExtra(Constants.General.TUTOR_ID, userInfo.getId());
					intent.putExtra("role", userInfo.getAccountType());
					startActivity(intent);
				}
				break;
			default:
				break;
		}
	}
}
