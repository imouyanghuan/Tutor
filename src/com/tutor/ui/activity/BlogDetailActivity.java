package com.tutor.ui.activity;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.http.protocol.HTTP;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.hk.tutor.R;
import com.mssky.mobile.helper.SharePrefUtil;
import com.tutor.model.BlogModel;
import com.tutor.params.Constants;
import com.tutor.ui.view.TitleBar;

/**
 * 博客详情页面
 * 
 * @author jerry.yao
 * 
 *         2015-12-14
 */
public class BlogDetailActivity extends BaseActivity {

	private WebView webView;
	private String contentHtml;
	private BlogModel blogModel;
	private boolean canPresentShareDialog;
	private CallbackManager callbackManager;
	private ProfileTracker profileTracker;
	private ShareDialog shareDialog;
	private final String PENDING_ACTION_BUNDLE_KEY = "com.tutor.ui.activity:PendingAction";
	private static final String PERMISSION = "publish_actions";
	private PendingAction pendingAction = PendingAction.NONE;

	private enum PendingAction {
		NONE, POST_PHOTO, POST_STATUS_UPDATE
	}

	private FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {

		@Override
		public void onCancel() {
			Log.e("Tutor", "Facebook share Canceled");
			toast(R.string.toast_share_cancel);
		}

		@Override
		public void onError(FacebookException error) {
			Log.e("Tutor", String.format("Facebook share Error: %s", error.toString()));
			toast(R.string.toast_share_failed);
		}

		@Override
		public void onSuccess(Sharer.Result result) {
			Log.e("Tutor", "Facebook share Success!");
			toast(R.string.label_share_success);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FacebookSdk.sdkInitialize(this.getApplicationContext());
		callbackManager = CallbackManager.Factory.create();
		LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

			@Override
			public void onSuccess(LoginResult loginResult) {
				handlePendingAction();
				// updateUI();
			}

			@Override
			public void onCancel() {
				if (pendingAction != PendingAction.NONE) {
					showAlert();
					pendingAction = PendingAction.NONE;
				}
				// updateUI();
			}

			@Override
			public void onError(FacebookException exception) {
				if (pendingAction != PendingAction.NONE && exception instanceof FacebookAuthorizationException) {
					showAlert();
					pendingAction = PendingAction.NONE;
				}
				// updateUI();
			}

			private void showAlert() {
				new AlertDialog.Builder(BlogDetailActivity.this).setTitle(R.string.cancelled).setMessage(R.string.permission_not_granted).setPositiveButton(R.string.ok, null).show();
			}
		});
		shareDialog = new ShareDialog(this);
		shareDialog.registerCallback(callbackManager, shareCallback);
		if (savedInstanceState != null) {
			String name = savedInstanceState.getString(PENDING_ACTION_BUNDLE_KEY);
			pendingAction = PendingAction.valueOf(name);
		}
		setContentView(R.layout.activity_faq_detail);
		profileTracker = new ProfileTracker() {

			@Override
			protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
				// updateUI();
				// It's possible that we were waiting for Profile to be
				// populated in order to
				// post a status update.
				handlePendingAction();
			}
		};
		initData();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Call the 'activateApp' method to log an app event for use in
		// analytics and advertising
		// reporting. Do so in the onResume methods of the primary Activities
		// that an app may be
		// launched into.
		AppEventsLogger.activateApp(this);
		// updateUI();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(PENDING_ACTION_BUNDLE_KEY, pendingAction.name());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		callbackManager.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
		super.onPause();
		// Call the 'deactivateApp' method to log an app event for use in
		// analytics and advertising
		// reporting. Do so in the onPause methods of the primary Activities
		// that an app may be
		// launched into.
		AppEventsLogger.deactivateApp(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		profileTracker.stopTracking();
	}

	private void initData() {
		Intent intent = getIntent();
		if (intent == null) {
			return;
		}
		boolean isFromFacebook = intent.getBooleanExtra(Constants.General.IS_FROM_FACEBOOK, false);
		if (isFromFacebook) {
			SharePrefUtil.remove(BlogDetailActivity.this, Constants.General.BLOG_ID);
		}
		blogModel = (BlogModel) intent.getSerializableExtra(Constants.IntentExtra.INTENT_EXTRA_BLOG);
		contentHtml = blogModel.getContent();
		initView();
	}

	@Override
	protected void initView() {
		TitleBar bar = getView(R.id.title_bar);
		bar.initBack(this);
		bar.setTitle(blogModel.getTitle());
		bar.setRightButton(R.drawable.share, new OnClickListener() {

			@Override
			public void onClick(View v) {
				shareToFacebook();
			}
		});
		webView = getView(R.id.webview);
		webView.getSettings().setDefaultTextEncodingName(HTTP.UTF_8);
		if (!TextUtils.isEmpty(contentHtml)) {
			try {
				webView.loadDataWithBaseURL("about:blank", Html.fromHtml(contentHtml).toString(), "text/html", HTTP.UTF_8, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// 分享到facebook
	private void shareToFacebook() {
		initCallBack();
		performPublish(PendingAction.POST_STATUS_UPDATE, canPresentShareDialog);
	}

	private void initCallBack() {
		// facebook 分享
		FacebookSdk.sdkInitialize(this.getApplicationContext());
		callbackManager = CallbackManager.Factory.create();
		LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

			@Override
			public void onSuccess(LoginResult loginResult) {
				handlePendingAction();
				// updateUI();
			}

			@Override
			public void onCancel() {
				if (pendingAction != PendingAction.NONE) {
					showAlert();
					pendingAction = PendingAction.NONE;
				}
				// updateUI();
			}

			@Override
			public void onError(FacebookException exception) {
				if (pendingAction != PendingAction.NONE && exception instanceof FacebookAuthorizationException) {
					showAlert();
					pendingAction = PendingAction.NONE;
				}
				// updateUI();
			}

			private void showAlert() {
				new AlertDialog.Builder(BlogDetailActivity.this).setTitle(R.string.cancelled).setMessage(R.string.permission_not_granted).setPositiveButton(R.string.ok, null).show();
			}
		});
		shareDialog = new ShareDialog(this);
		shareDialog.registerCallback(callbackManager, shareCallback);
		// Can we present the share dialog for photos?
		canPresentShareDialog = ShareDialog.canShow(ShareLinkContent.class);
	}

	private void performPublish(PendingAction action, boolean allowNoToken) {
		AccessToken accessToken = AccessToken.getCurrentAccessToken();
		if (accessToken != null || allowNoToken) {
			pendingAction = action;
			handlePendingAction();
		}
	}

	private void handlePendingAction() {
		PendingAction previouslyPendingAction = pendingAction;
		// These actions may re-set pendingAction if they are still pending, but
		// we assume they
		// will succeed.
		pendingAction = PendingAction.NONE;
		switch (previouslyPendingAction) {
			case NONE:
				break;
			case POST_PHOTO:
				postPhoto();
				break;
			case POST_STATUS_UPDATE:
				postStatusUpdate();
				break;
		}
	}

	private void postStatusUpdate() {
		Profile profile = Profile.getCurrentProfile();
		Uri uri = Uri.parse("http://ec2-54-255-164-232.ap-southeast-1.compute.amazonaws.com/tutorPortal/Content/images/2utor.png");
		// Uri uri = Uri.parse(blogModel.getMainImage());
		String linkUrl = blogModel.getLinkUrl();
		// Log.e("Tutor", "linkUrl === : " + linkUrl);
		ShareLinkContent linkContent = new ShareLinkContent.Builder().setImageUrl(uri).setContentTitle(blogModel.getTitle()).setContentDescription(blogModel.getSummary())
				.setContentUrl(Uri.parse(linkUrl)).build();
		if (canPresentShareDialog) {
			shareDialog.show(linkContent);
		} else if (profile != null && hasPublishPermission()) {
			ShareApi.share(linkContent, shareCallback);
		} else {
			pendingAction = PendingAction.POST_STATUS_UPDATE;
		}
	}

	private void postPhoto() {
		// Bitmap bitmap = ShareUtils.getBitmapByPath(filePath);
		SharePhoto sharePhoto = new SharePhoto.Builder().setBitmap(null).build();
		ArrayList<SharePhoto> photos = new ArrayList<SharePhoto>();
		photos.add(sharePhoto);
		SharePhotoContent sharePhotoContent = new SharePhotoContent.Builder().setPhotos(photos).build();
		// if (canPresentShareDialogWithPhotos) {
		// shareDialog.show(sharePhotoContent);
		// } else {
		if (hasPublishPermission()) {
			ShareApi shareApi = new ShareApi(sharePhotoContent);
			// TODO test message
			shareApi.setMessage("");
			shareApi.share(shareCallback);
		} else {
			pendingAction = PendingAction.POST_PHOTO;
			// We need to get new permissions, then complete the action when
			// we get called back.
			LoginManager.getInstance().logInWithPublishPermissions(this, Arrays.asList(PERMISSION));
		}
		// }
	}

	private boolean hasPublishPermission() {
		AccessToken accessToken = AccessToken.getCurrentAccessToken();
		return accessToken != null && accessToken.getPermissions().contains("publish_actions");
	}
}
