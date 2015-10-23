package com.tutor.ui.activity;

import java.net.HttpURLConnection;
import java.util.ArrayList;

import org.apache.http.protocol.HTTP;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.AdapterView.OnItemClickListener;

import com.hk.tutor.R;
import com.loopj.android.http.RequestParams;
import com.tutor.TutorApplication;
import com.tutor.adapter.AdviserAdapter;
import com.tutor.im.ContactManager;
import com.tutor.im.XMPPConnectionManager;
import com.tutor.model.AbroadConfig;
import com.tutor.model.AbroadConfigListResult;
import com.tutor.model.StringResult;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.view.ObservableScrollView;
import com.tutor.ui.view.ObservableScrollView.ScrollViewListener;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.CheckTokenUtils;
import com.tutor.util.HttpHelper;
import com.tutor.util.ObjectHttpResponseHandler;
import com.tutor.util.ScreenUtil;

/**
 * @author bruce.chen
 * 
 *         2015-10-21
 */
public class AdvisoryActivity extends BaseActivity implements OnClickListener, ScrollViewListener {

	private WebView webView;
	private FrameLayout flToolbar;
	private Button btnChat;
	private ObservableScrollView scrollView;
	private ArrayList<AbroadConfig> imIds = new ArrayList<AbroadConfig>();
	private boolean isGone = true;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_advisory);
		initView();
		showDialogRes(R.string.loading);
		getData();
	}

	private void getData() {
		if (!HttpHelper.isNetworkConnected(this)) {
			toast(R.string.toast_netwrok_disconnected);
			dismissDialog();
			return;
		}
		// 获取客服
		getIMIds();
		HttpHelper.get(this, ApiUrl.STUDY_ABROAD_ADVISORY, TutorApplication.getHeaders(), new RequestParams(), new ObjectHttpResponseHandler<StringResult>(StringResult.class) {

			@Override
			public void onFailure(int status, String message) {
				if (0 == status) {
					getData();
					return;
				}
				dismissDialog();
				CheckTokenUtils.checkToken(status);
			}

			@Override
			public void onSuccess(StringResult t) {
				dismissDialog();
				if (HttpURLConnection.HTTP_OK == t.getStatusCode() && !TextUtils.isEmpty(t.getResult())) {
					try {
						webView.loadDataWithBaseURL("about:blank", Html.fromHtml(t.getResult()).toString(), "text/html", HTTP.UTF_8, null);
					} catch (OutOfMemoryError e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	@Override
	protected void initView() {
		TitleBar bar = getView(R.id.title_bar);
		bar.initBack(this);
		bar.setTitle(R.string.label_advisory_for_study_abroad);
		webView = getView(R.id.webview);
		webView.getSettings().setDefaultTextEncodingName(HTTP.UTF_8);
		// scrollview
		scrollView = getView(R.id.scrollView);
		scrollView.setScrollViewListener(this);
		// toolbar
		flToolbar = getView(R.id.fl_toolbar);
		btnChat = getView(R.id.btn_chat_with_adviser);
		btnChat.setOnClickListener(this);
	}

	/**
	 * 获取所有的客服
	 */
	private void getIMIds() {
		if (!HttpHelper.isNetworkConnected(this)) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		RequestParams params = new RequestParams();
		HttpHelper.get(this, ApiUrl.STUDY_ABROAD_IM_IDS, TutorApplication.getHeaders(), params, new ObjectHttpResponseHandler<AbroadConfigListResult>(AbroadConfigListResult.class) {

			@Override
			public void onFailure(int status, String message) {
				if (0 == status) {
					return;
				}
				dismissDialog();
				CheckTokenUtils.checkToken(status);
				toast(R.string.toast_server_error);
			}

			@Override
			public void onSuccess(AbroadConfigListResult result) {
				dismissDialog();
				if (result.getStatusCode() == HttpURLConnection.HTTP_OK) {
					imIds.addAll(result.getResult());
					if (imIds != null && imIds.size() > 0) {
						for (int i = 0; i < imIds.size(); i++) {
							imIds.get(i).setAlias("Adviser" + (i + 1));
						}
					}
				} else {
					toast(result.getMessage());
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_chat_with_adviser:
				showPopupWindow(btnChat);
				break;
			default:
				break;
		}
	}

	private void showPopupWindow(Button btnChat) {
		View view = View.inflate(AdvisoryActivity.this, R.layout.layout_adviser_list, null);
		ListView lvAdviser = (ListView) view.findViewById(R.id.lv_adviser);
		final AdviserAdapter adapter = new AdviserAdapter(AdvisoryActivity.this, imIds);
		lvAdviser.setAdapter(adapter);
		int itemCount = adapter.getCount();
		final PopupWindow popupWindow = new PopupWindow(view);
		popupWindow.setFocusable(true);
		int itemWidth = ScreenUtil.getSW(this);
		int itemHeight = ScreenUtil.dip2Px(AdvisoryActivity.this, 40) * itemCount;
		// 设置SelectPicPopupWindow弹出窗体的宽
		popupWindow.setWidth(itemWidth / 2);
		// 设置SelectPicPopupWindow弹出窗体的高
		popupWindow.setHeight(itemHeight);
		// 点击“返回Back”也能使其消失
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new ColorDrawable(R.color.white));
		popupWindow.showAsDropDown(btnChat, itemWidth / 2, 0);
		lvAdviser.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				AbroadConfig config = adapter.getItem(position);
				String imId = config.getText();
				String adviserName = config.getAlias();
				if (!TextUtils.isEmpty(imId)) {
					boolean isFriend = ContactManager.getManager().addFriend(imId, imId);
					if (isFriend) {
						Intent intent = new Intent(AdvisoryActivity.this, ChatActivity.class);
						intent.putExtra(Constants.General.IS_FROM_COURSE_SELECTION, true);
						intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_MESSAGE_TO, imId + "@" + XMPPConnectionManager.getManager().getServiceName());
						intent.putExtra(Constants.General.NICKNAME, adviserName);
						intent.putExtra(Constants.General.AVATAR, ApiUrl.DOMAIN + ApiUrl.GET_OTHER_AVATAR + TutorApplication.getMemberId());
						startActivity(intent);
					}
				}
				if (popupWindow != null) {
					popupWindow.dismiss();
				}
			}
		});
	}

	@Override
	public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
		//
		if (oldy < y && Math.abs(oldy - y) > 20) {
			if (isGone) {
				// 向下滚动
				TranslateAnimation goneAnim = new TranslateAnimation(0, 0, 0, 150);
				goneAnim.setDuration(200);
				// goneAnim.setFillAfter(true);
				flToolbar.setAnimation(goneAnim);
				isGone = false;
				flToolbar.setVisibility(View.GONE);
			}
		} else if (oldy > y && Math.abs(oldy - y) > 20) {
			if (!isGone) {
				// 向上滚动
				TranslateAnimation visibleAnim = new TranslateAnimation(0, 0, 150, 0);
				visibleAnim.setDuration(200);
				visibleAnim.setFillAfter(true);
				flToolbar.setAnimation(visibleAnim);
				isGone = true;
				flToolbar.setVisibility(View.VISIBLE);
			}
		}
	}
}
