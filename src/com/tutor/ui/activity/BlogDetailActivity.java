package com.tutor.ui.activity;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.http.protocol.HTTP;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.TextView;

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
import com.mssky.mobile.helper.UIUtils;
import com.tutor.model.BlogModel;
import com.tutor.params.Constants;
import com.tutor.ui.view.OverScrollView;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.ScreenUtil;

/**
 * 博客详情页面
 * 
 * @author jerry.yao
 * 
 *         2015-12-14
 */
public class BlogDetailActivity extends BaseActivity {

	public static final String ICON_GIF = "data:image/gif;base64,";
	public static final String ICON_PNG = "data:image/png;base64,";
	public static final String ICON_JPEG = "data:image/jpeg;base64,";
	public static final String ICON_XICON = "data:image/x-icon;base64,";
	private static int imageHeight = 600;
	private static int imageWidth = 300;
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
		int imageW = ScreenUtil.getSW(this) - 2 * ScreenUtil.dip2Px(this, 15);
		int imageH = imageW * 2 / 3;
		imageWidth = imageW;
		imageHeight = imageH;
	}

	@Override
	protected void initView() {
		TitleBar bar = getView(R.id.title_bar);
		bar.initBack(this);
		bar.setTitle(blogModel.getTitle());
		bar.setRightButton(R.drawable.fb_share, new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (blogModel != null) {
					shareToFacebook();
				}
			}
		});
		webView = getView(R.id.webview);
		webView.setVisibility(View.GONE);
		webView.getSettings().setDefaultTextEncodingName(HTTP.UTF_8);
		OverScrollView scrollView = getView(R.id.scrollView);
		scrollView.setVisibility(View.VISIBLE);
		content = getView(R.id.tv_content);
		new MyThread().start();
		// if (!TextUtils.isEmpty(contentHtml)) {
		// try {
		// webView.loadDataWithBaseURL("about:blank", Html.fromHtml(contentHtml,
		// imgGetter, null).toString(), "text/html", HTTP.UTF_8, null);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
	}

	public class MyThread extends Thread {

		public void run() {
			Spanned spanned = Html.fromHtml(Html.fromHtml(contentHtml).toString(), imgGetter, null);
			Message msg = new Message();
			msg.obj = spanned;
			handler.sendMessage(msg);
		}
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			content.setText((Spanned) msg.obj);
		}
	};
	ImageGetter imgGetter = new Html.ImageGetter() {

		public Drawable getDrawable(String source) {
			if (TextUtils.isEmpty(source)) {
				return null;
			}
			Log.e("ImagePath", source);
			if (source.startsWith(ICON_GIF) || source.startsWith(ICON_PNG) || source.startsWith(ICON_JPEG) || source.startsWith(ICON_XICON)) {
				Drawable htmlDrawable = setHtmlImage(source);
				return (htmlDrawable == null) ? getResources().getDrawable(R.drawable.post_pic_default) : htmlDrawable;
			} else {
				if (!source.toLowerCase().startsWith("http")) {
					// source = Constants.BASE_URL + "/" + source;
				}
				URL url;
				Drawable drawable = getResources().getDrawable(R.drawable.post_pic_default);
				Bitmap bitmap = null;
				try {
					String filePath = UIUtils.makeFilePath(BlogDetailActivity.this, "cache") + "/" + Uri.parse(source).getLastPathSegment();
					bitmap = getBitmapFromFile(BlogDetailActivity.this, filePath);
					if (bitmap == null) {
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inSampleSize = 5;
						options.inDither = false;
						options.inPreferredConfig = Bitmap.Config.RGB_565;
						url = new URL(source);
						bitmap = BitmapFactory.decodeStream(url.openStream(), null, options); // 获取网路图片
						// 2.缓存bitmap至/data/data/packageName/cache/文件夹中
						FileOutputStream fos = new FileOutputStream(filePath);
						bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
						fos.close();
					}
					if (bitmap != null) {
						drawable = new BitmapDrawable(bitmap);
					}
					if (drawable != null) {
						// 设置图片边界
						imageHeight = (int) (imageWidth / ((float) drawable.getIntrinsicWidth() / (float) drawable.getIntrinsicHeight()));
					}
					if ((int) (imageWidth / ((float) drawable.getIntrinsicWidth())) > 2) {
						drawable.setBounds(0, 0, imageWidth / 2, imageWidth / 2);
					} else {
						drawable.setBounds(0, 0, imageWidth, imageHeight);
					}
				} catch (Throwable e) {
					e.printStackTrace();
					return drawable;
				}
				return drawable;
			}
		}
	};
	private TextView content;

	/**
	 * 从外部文件缓存中获取bitmap
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getBitmapFromFile(Context mContext, String fileName) {
		Bitmap bitmap = null;
		if (fileName == null)
			return null;
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 1;
			options.inDither = false;
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			FileInputStream fis = new FileInputStream(fileName);
			bitmap = BitmapFactory.decodeStream(fis, null, options);
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			bitmap = null;
		} catch (Throwable e) {
			System.gc();
			bitmap = null;
		}
		return bitmap;
	}

	public static Drawable setHtmlImage(String source) {
		if (!TextUtils.isEmpty(source)) {
			Drawable drawable = null;
			String icon = "";
			if (source.startsWith(ICON_GIF)) {
				icon = ICON_GIF;
			} else if (source.startsWith(ICON_PNG)) {
				icon = ICON_PNG;
			} else if (source.startsWith(ICON_JPEG)) {
				icon = ICON_JPEG;
			} else if (source.startsWith(ICON_XICON)) {
				icon = ICON_XICON;
			}
			try {
				if (!TextUtils.isEmpty(icon)) {
					icon = source.replace(icon, "");
					byte[] decodedString = Base64.decode(icon.getBytes(), Base64.DEFAULT);
					System.gc();
					Bitmap bitmap = null;
					if (decodedString != null) {
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inSampleSize = 1;
						// if (source.getBytes().length > 0.5 * 1024 * 1024) {
						options.inSampleSize = 4;
						// }
						options.inDither = false;
						options.inPreferredConfig = Bitmap.Config.RGB_565;
						InputStream input = new ByteArrayInputStream(decodedString);
						bitmap = BitmapFactory.decodeStream(input, null, options);
						// bitmap = BitmapFactory.decodeByteArray(img,
						// 0, img.length);
						if (bitmap != null) {
							drawable = new BitmapDrawable(bitmap);
							if (drawable != null) {
								// 设置图片边界
								imageHeight = (int) (imageWidth / ((float) drawable.getIntrinsicWidth() / (float) drawable.getIntrinsicHeight()));
							}
							drawable.setBounds(0, 0, imageWidth, imageHeight);
						}
						input.close();
					}
				} else {
					drawable = Drawable.createFromPath(source); // Or fetch it
																// from
					// the URL
					if (drawable != null) {
						// Important
						// 设置图片边界
						imageHeight = (int) (imageWidth / ((float) drawable.getIntrinsicWidth() / (float) drawable.getIntrinsicHeight()));
						drawable.setBounds(0, 0, imageWidth, imageHeight);
					}
				}
				return drawable;
			} catch (Throwable e) {
				System.gc();
			}
		}
		return null;
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
		Uri uri = Uri.parse(blogModel.getMainImage());
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
