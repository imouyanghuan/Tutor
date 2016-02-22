package com.tutor.ui.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import cn.jpush.android.api.JPushInterface;

import com.facebook.login.LoginManager;
import com.hk.tutor.R;
import com.loopj.android.http.RequestParams;
import com.tutor.TutorApplication;
import com.tutor.im.XMPPConnectionManager;
import com.tutor.model.Account;
import com.tutor.model.EditProfileResult;
import com.tutor.model.UploadAvatarResult;
import com.tutor.model.UserInfo;
import com.tutor.model.UserInfoResult;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.dialog.ChangeAvatarDialog;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.CheckTokenUtils;
import com.tutor.util.DateTimeUtil;
import com.tutor.util.HttpHelper;
import com.tutor.util.ImageUtils;
import com.tutor.util.JsonUtil;
import com.tutor.util.LogUtils;
import com.tutor.util.ObjectHttpResponseHandler;
import com.tutor.util.ScreenManager;

/**
 * 学生首頁,我的
 * 
 * @author jerry.yao
 * 
 */
public class StudentProfileActivity extends BaseActivity implements OnClickListener {

	/** 頂部菜單 */
	private TitleBar bar;
	// 頭像對話框
	private ChangeAvatarDialog dialog;
	// 头像
	private ImageView avatar;
	// 保存拍照圖片uri
	private Uri imageUri, zoomUri;
	// 用户名,性别,修改资料
	private TextView userName, gender, editInfo, changepswd;
	// 昵称, 自我介绍
	private EditText nickName, introduction;
	// 保存
	private Button save;
	/** 个人信息 */
	private UserInfo userInfo;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.fragment_student_my);
		initView();
		// 获取数据
		getData();
	}

	@Override
	protected void initView() {
		bar = getView(R.id.title_bar);
		bar.initBack(this);
		bar.setTitle(R.string.my);
		bar.setRightText(R.string.log_out, new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				loginOut();
			}
		});
		avatar = getView(R.id.fragment_my_iv_avatar);
		avatar.setOnClickListener(this);
		// // 加载头像
		// ImageUtils.loadImage(avatar, ApiUrl.DOMAIN + ApiUrl.GET_OTHER_AVATAR
		// + TutorApplication.getMemberId());
		//
		userName = getView(R.id.fragment_my_tv_username);
		gender = getView(R.id.fragment_my_tv_gender);
		editInfo = getView(R.id.fragment_my_tv_edit);
		//
		nickName = getView(R.id.fragment_my_et_nickname);
		introduction = getView(R.id.fragment_my_et_introduction);
		save = getView(R.id.fragment_my_btn_save);
		changepswd = getView(R.id.fragment_my_tv_changepswd);
		changepswd.setOnClickListener(this);
		editInfo.setOnClickListener(this);
		save.setOnClickListener(this);
	}

	private void loginOut() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.log_out);
		builder.setMessage(R.string.lable_log_out);
		builder.setPositiveButton(R.string.cancel, null);
		builder.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				Account account = TutorApplication.getAccountDao().load("1");
				if (account != null) {
					// 注销角色
					account.setRole(-1);
					TutorApplication.getAccountDao().update(account);
					if (!TextUtils.isEmpty(account.getFacebookId())) {
						try {
							LoginManager.getInstance().logOut();
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				}
				// TODO 调用api退出登录
				// 斷開IM連接
				XMPPConnectionManager.getManager().disconnect();
				// 停止Push服务
				JPushInterface.stopPush(StudentProfileActivity.this);
				TutorApplication.settingManager.writeSetting(Constants.SharedPreferences.SP_ISLOGIN, false);
				ScreenManager.getScreenManager().removeAllActivity();
				Intent intent = new Intent(StudentProfileActivity.this, ChoiceRoleActivity.class);
				startActivity(intent);
				finishNoAnim();
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	/**
	 * 设置数据
	 * 
	 * @param result
	 */
	private void setData(UserInfo profile) {
		userInfo = profile;
		// 加载头像
		ImageUtils.loadImage(avatar, ApiUrl.DOMAIN + ApiUrl.GET_OTHER_AVATAR + TutorApplication.getMemberId(), userInfo.getGender() != null ? userInfo.getGender() : Constants.General.MALE);
		userName.setText(!TextUtils.isEmpty(profile.getUserName()) ? profile.getUserName() : "Tutor" + profile.getId());
		int genderStr;
		if (profile.getGender() == null) {
			genderStr = R.string.label_ignore1;
		} else if (Constants.General.MALE == profile.getGender()) {
			genderStr = R.string.label_male;
		} else {
			genderStr = R.string.label_female;
		}
		gender.setText(genderStr);
		nickName.setText(!TextUtils.isEmpty(profile.getNickName()) ? profile.getNickName() : "");
		introduction.setText(!TextUtils.isEmpty(profile.getIntroduction()) ? profile.getIntroduction() : "");
	}

	@Override
	public void onResume() {
		super.onResume();
		// 更新数据
		getData();
	}

	/**
	 * 获取数据
	 */
	private void getData() {
		RequestParams params = new RequestParams();
		params.put("memberId", TutorApplication.getMemberId());
		HttpHelper.getHelper().get(ApiUrl.STUDENTINFO, TutorApplication.getHeaders(), params, new ObjectHttpResponseHandler<UserInfoResult>(UserInfoResult.class) {

			@Override
			public void onFailure(int status, String message) {
				if (0 == status) {
					getData();
					return;
				}
				if (CheckTokenUtils.checkToken(status)) {
					return;
				}
				// toast(R.string.toast_server_error);
			}

			@Override
			public void onSuccess(UserInfoResult t) {
				if (null != t.getResult()) {
					setData(t.getResult());
				}
			}
		});
	}

	private void save() {
		if (null == userInfo) {
			toast(R.string.loading);
			return;
		}
		if (!HttpHelper.isNetworkConnected(StudentProfileActivity.this)) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		showDialogRes(R.string.loading);
		String json = JsonUtil.parseObject2Str(getUserInfo());
		LogUtils.d(json);
		try {
			StringEntity entity = new StringEntity(json, HTTP.UTF_8);
			HttpHelper.getHelper().put(ApiUrl.STUDENTPROFILE, TutorApplication.getHeaders(), entity, new ObjectHttpResponseHandler<EditProfileResult>(EditProfileResult.class) {

				@Override
				public void onFailure(int status, String message) {
					if (0 == status) {
						save();
						return;
					}
					dismissDialog();
					LogUtils.e(message);
					toast(R.string.toast_server_error);
				}

				@Override
				public void onSuccess(EditProfileResult result) {
					dismissDialog();
					if (null != result) {
						if (200 == result.getStatusCode() && result.getResult()) {
							toast(R.string.toast_save_successed);
							setData(userInfo);
						} else {
							toast(result.getMessage());
						}
					} else {
						toast(R.string.toast_server_error);
					}
				}
			});
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			dismissDialog();
		}
	}

	private UserInfo getUserInfo() {
		userInfo.setNickName(nickName.getEditableText().toString().trim());
		userInfo.setIntroduction(introduction.getEditableText().toString().trim());
		return userInfo;
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
			case R.id.fragment_my_iv_avatar:
				// 上傳頭像
				if (null == dialog) {
					dialog = new ChangeAvatarDialog((BaseActivity) StudentProfileActivity.this);
				}
				ImageUtils.clearChache();
				// 初始化uri
				String fileName = DateTimeUtil.getSystemDateTime(DateTimeUtil.FORMART_YMDHMS) + Constants.General.IMAGE_END;
				imageUri = Uri.fromFile(new File(Constants.SDCard.getImageDir(), fileName));
				dialog.setUri(imageUri);
				dialog.show();
				break;
			case R.id.fragment_my_btn_save:
				save();
				break;
			case R.id.fragment_my_tv_edit:
				if (null == userInfo) {
					toast(R.string.loading);
					return;
				}
				Intent intent = new Intent();
				intent.setClass(StudentProfileActivity.this, FillPersonalInfoActivity.class);
				intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_ISEDIT, true);
				intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_USER_INFO, userInfo);
				startActivity(intent);
				break;
			case R.id.fragment_my_tv_changepswd:
				Intent intent2 = new Intent(StudentProfileActivity.this, ChangePasswordActivity.class);
				intent2.putExtra(Constants.IntentExtra.INTENT_EXTRA_PASSWORD_FLAG, Constants.General.CHANGE_PASSWORD);
				startActivity(intent2);
				break;
			default:
				break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case Constants.RequestResultCode.TAKE_PHOTO:
				// 拍照回來
				if (BaseActivity.RESULT_OK == resultCode && null != imageUri) {
					// 更新圖庫
					ImageUtils.updateGrally(StudentProfileActivity.this, imageUri);
					// 啟動裁剪
					startZoom(imageUri);
				}
				break;
			case Constants.RequestResultCode.GALLERY:
				// 圖庫選擇
				if (BaseActivity.RESULT_OK == resultCode && null != data && null != data.getData()) {
					// 啟動裁剪
					startZoom(data.getData());
				}
				break;
			case Constants.RequestResultCode.ZOOM:
				// 裁剪完成
				// 圖庫選擇
				if (BaseActivity.RESULT_OK == resultCode && null != zoomUri) {
					// 上傳頭像
					String path = zoomUri.getPath();
					if (!TextUtils.isEmpty(path)) {
						File file = new File(path);
						if (null != file && file.exists()) {
							file = null;
							upLoadAvatar(path);
						}
					}
				}
				break;
		}
	}

	/**
	 * 裁剪照片
	 * 
	 * @param data
	 */
	private void startZoom(Uri data) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(data, "image/*");
		// crop为true是设置在开启的intent中设置显示的view可以剪裁
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX,outputY 是剪裁图片的宽高
		intent.putExtra("outputX", 400);
		intent.putExtra("outputY", 400);
		// 保持缩放
		intent.putExtra("scale", true);
		String path = data.getPath();
		String fileName = path.substring(path.lastIndexOf(File.separatorChar) + 1);
		zoomUri = Uri.fromFile(new File(Constants.SDCard.getCacheDir(), fileName + Constants.General.IMAGE_END));
		intent.putExtra(MediaStore.EXTRA_OUTPUT, zoomUri);
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true);
		startActivityForResult(intent, Constants.RequestResultCode.ZOOM);
	}

	/**
	 * 上传头像
	 * 
	 * @param path
	 */
	private void upLoadAvatar(String path) {
		if (!HttpHelper.isNetworkConnected(StudentProfileActivity.this)) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		// 先壓縮圖片
		String newPath = Constants.SDCard.getCacheDir() + DateTimeUtil.getSystemDateTime(DateTimeUtil.FORMART_YMDHMS) + Constants.General.IMAGE_END;
		if (ImageUtils.yaSuoImage(path, newPath, 400, 400)) {
			File file = new File(newPath);
			RequestParams params = new RequestParams();
			try {
				params.put("file", file, "form-data");
				showDialogRes(R.string.uploading_avatar);
				HttpHelper.getHelper().post(ApiUrl.UPLOAD_AVATAR, TutorApplication.getHeaders(), params, new ObjectHttpResponseHandler<UploadAvatarResult>(UploadAvatarResult.class) {

					@Override
					public void onFailure(int status, String message) {
						dismissDialog();
						if (CheckTokenUtils.checkToken(status)) {
							return;
						}
						toast(R.string.toast_avatar_upload_fail);
					}

					@Override
					public void onSuccess(UploadAvatarResult t) {
						dismissDialog();
						CheckTokenUtils.checkToken(t);
						if (HttpURLConnection.HTTP_OK == t.getStatusCode()) {
							ImageUtils.clearChache();
							ImageUtils.loadImage(avatar, ApiUrl.DOMAIN + t.getResult(), userInfo.getGender() != null ? userInfo.getGender() : Constants.General.MALE);
							// 把新头像设置进userInfo model(解决上传完头像马上编辑信息bug) 12/9
							userInfo.setAvatar(t.getResult());
						} else {
							toast(R.string.toast_avatar_upload_fail);
						}
					}
				});
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				toast(R.string.toast_avatar_upload_fail);
			}
		}
	}
}
