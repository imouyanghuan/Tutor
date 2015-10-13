package com.tutor.ui.activity;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.loopj.android.http.RequestParams;
import com.mssky.mobile.ui.view.PullToRefreshBase;
import com.mssky.mobile.ui.view.PullToRefreshBase.Mode;
import com.mssky.mobile.ui.view.PullToRefreshBase.OnRefreshListener;
import com.mssky.mobile.ui.view.PullToRefreshListView;
import com.tutor.R;
import com.tutor.TutorApplication;
import com.tutor.adapter.RateCommentsAdapter;
import com.tutor.model.RatingModel;
import com.tutor.model.RatingModelResult;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.HttpHelper;
import com.tutor.util.ObjectHttpResponseHandler;

/**
 * 评论列表界面
 * 
 * @author jerry.yao
 * 
 *         2015-10-12
 */
public class RateCommentListActivity extends BaseActivity implements OnClickListener {

	private PullToRefreshListView lvComments;
	private List<RatingModel> rateComments = new ArrayList<RatingModel>();
	private RateCommentsAdapter mAdapter;
	private int pageIndex = 0;
	private int pageSize = 20;
	private boolean hasNextPage;
	private int tutorId;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_rate_comment_list);
		initView();
		initData();
	}

	private void initData() {
		Intent intent = getIntent();
		if (intent == null) {
			return;
		}
		tutorId = intent.getIntExtra(Constants.General.TUTOR_ID, -1);
		if (tutorId != -1) {
			getRateComments(tutorId);
		}
	}

	@Override
	protected void initView() {
		TitleBar bar = getView(R.id.title_bar);
		bar.setTitle(R.string.label_other_comments);
		bar.initBack(this);
		// comments listview
		lvComments = getView(R.id.lv_comments);
		lvComments.setMode(Mode.PULL_FROM_END);
		mAdapter = new RateCommentsAdapter(this, rateComments);
		lvComments.setAdapter(mAdapter);
		lvComments.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				pageIndex++;
				if (hasNextPage) {
					getRateComments(tutorId);
				} else {
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							lvComments.onRefreshComplete();
							toast(R.string.toast_no_more_data);
						}
					}, 1000);
				}
			}
		});
	}

	/**
	 * 保存评分
	 * 
	 */
	private void getRateComments(int tutorId) {
		if (!HttpHelper.isNetworkConnected(this)) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		RequestParams params = new RequestParams();
		params.put("pageIndex", pageIndex);
		params.put("pageSize", pageSize);
		String url = String.format(ApiUrl.RATING_COMMENT_LIST, String.valueOf(tutorId));
		HttpHelper.get(this, url, TutorApplication.getHeaders(), params, new ObjectHttpResponseHandler<RatingModelResult>(RatingModelResult.class) {

			@Override
			public void onFailure(int status, String message) {
				toast(R.string.toast_server_error);
				lvComments.onRefreshComplete();
			}

			@Override
			public void onSuccess(RatingModelResult result) {
				if (result.getStatusCode() == HttpURLConnection.HTTP_OK) {
					hasNextPage = result.getPage().isHasNextPage();
					ArrayList<RatingModel> tempComments = result.getResult();
					if (tempComments != null && tempComments.size() > 0) {
						rateComments.addAll(tempComments);
						mAdapter.notifyDataSetChanged();
					}
				}
				lvComments.onRefreshComplete();
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// chat with tutor
			case R.id.btn_chat_with_tutor:
				break;
			// to be my tutor
			case R.id.btn_to_be_my_tutor:
				break;
			default:
				break;
		}
	}
}
