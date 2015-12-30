package com.tutor.ui.fragment.tuitioncenter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hk.tutor.R;
import com.loopj.android.http.RequestParams;
import com.tutor.TutorApplication;
import com.tutor.model.EditProfileResult;
import com.tutor.model.UploadAvatarResult;
import com.tutor.model.UserInfo;
import com.tutor.model.UserInfoResult;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.activity.BaseActivity;
import com.tutor.ui.activity.ChangePasswordActivity;
import com.tutor.ui.activity.FillPersonalInfoActivity;
import com.tutor.ui.dialog.ChangeAvatarDialog;
import com.tutor.ui.fragment.BaseFragment;
import com.tutor.util.CheckTokenUtils;
import com.tutor.util.DateTimeUtil;
import com.tutor.util.HttpHelper;
import com.tutor.util.ImageUtils;
import com.tutor.util.JsonUtil;
import com.tutor.util.LogUtils;
import com.tutor.util.ObjectHttpResponseHandler;
import com.tutor.util.ViewHelper;

/**
 * 补习社个人信息页面
 * 
 * @author jerry.yao
 * 
 *         2015-12-10
 */
public class TuitionCenterInfoFragment extends BaseFragment implements OnClickListener {

	// 頭像對話框
	private ChangeAvatarDialog dialog;
	// 头像
	private ImageView avatar;
	// 保存拍照圖片uri
	private Uri imageUri, zoomUri;
	// 用户名,性别,修改资料
	private TextView userName, editInfo, changepswd;
	// 昵称, 自我介绍
	private EditText introduction;
	// 保存
	private Button save;
	/** 个人信息 */
	private UserInfo userInfo;
	private EditText etEmail;
	private EditText etPhone;
	private EditText etAddress;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_tuition_center_info, container, false);
		initView(view);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// 获取数据
		getData();
	}

	@Override
	public void onResume() {
		super.onResume();
		// 更新数据
		getData();
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
		userName.setText(!TextUtils.isEmpty(userInfo.getUserName()) ? userInfo.getUserName() : "Tution Center" + userInfo.getId());
		etEmail.setText(userInfo.getContactEmail());
		etPhone.setText(userInfo.getPhone());
		etAddress.setText(!TextUtils.isEmpty(userInfo.getResidentialAddress()) ? userInfo.getResidentialAddress() : "");
		introduction.setText(!TextUtils.isEmpty(userInfo.getIntroduction()) ? userInfo.getIntroduction() : "");
	}

	/**
	 * 获取数据
	 */
	private void getData() {
		RequestParams params = new RequestParams();
		params.put("memberId", TutorApplication.getMemberId());
		HttpHelper.getHelper().get(ApiUrl.TUITION_CENTER_INFO, TutorApplication.getHeaders(), params, new ObjectHttpResponseHandler<UserInfoResult>(UserInfoResult.class) {

			@Override
			public void onFailure(int status, String message) {
				if (0 == status) {
					getData();
					return;
				}
				if (CheckTokenUtils.checkToken(status)) {
					return;
				}
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
		if (!HttpHelper.isNetworkConnected(getActivity())) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		showDialogRes(R.string.loading);
		String json = JsonUtil.parseObject2Str(getUserInfo());
		LogUtils.d(json);
		try {
			StringEntity entity = new StringEntity(json, HTTP.UTF_8);
			HttpHelper.getHelper().put(ApiUrl.TUITION_CENTER_PROFILE_EDIT, TutorApplication.getHeaders(), entity, new ObjectHttpResponseHandler<EditProfileResult>(EditProfileResult.class) {

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
		userInfo.setContactEmail(etEmail.getText().toString());
		userInfo.setPhone(etPhone.getText().toString());
		userInfo.setResidentialAddress(etAddress.getText().toString());
		userInfo.setIntroduction(introduction.getEditableText().toString());
		return userInfo;
	}

	private void initView(View view) {
		// 头像
		avatar = ViewHelper.get(view, R.id.fragment_my_iv_avatar);
		avatar.setOnClickListener(this);
		// 姓名
		userName = ViewHelper.get(view, R.id.fragment_my_tv_username);
		// email
		etEmail = ViewHelper.get(view, R.id.et_email);
		// 电话
		etPhone = ViewHelper.get(view, R.id.et_phone_number);
		// 编辑按钮
		editInfo = ViewHelper.get(view, R.id.fragment_my_tv_edit);
		editInfo.setOnClickListener(this);
		// 地址
		etAddress = ViewHelper.get(view, R.id.et_tuition_school_address);
		// 自我介绍
		introduction = ViewHelper.get(view, R.id.fragment_my_et_introduction);
		// 修改密码
		changepswd = ViewHelper.get(view, R.id.fragment_my_tv_changepswd);
		changepswd.setOnClickListener(this);
		// 保存
		save = ViewHelper.get(view, R.id.fragment_my_btn_save);
		save.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		// 上傳頭像
			case R.id.fragment_my_iv_avatar:
				if (null == dialog) {
					dialog = new ChangeAvatarDialog((BaseActivity) getActivity());
				}
				ImageUtils.clearChache();
				// 初始化uri
				String fileName = DateTimeUtil.getSystemDateTime(DateTimeUtil.FORMART_YMDHMS) + Constants.General.IMAGE_END;
				imageUri = Uri.fromFile(new File(Constants.SDCard.getImageDir(), fileName));
				dialog.setUri(imageUri);
				dialog.show();
				break;
			// 保存
			case R.id.fragment_my_btn_save:
				save();
				break;
			// 编辑我的信息
			case R.id.fragment_my_tv_edit:
				if (null == userInfo) {
					toast(R.string.loading);
					return;
				}
				Intent intent = new Intent();
				intent.setClass(getActivity(), FillPersonalInfoActivity.class);
				intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_ISEDIT, true);
				intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_USER_INFO, userInfo);
				startActivity(intent);
				break;
			// 修改密码
			case R.id.fragment_my_tv_changepswd:
				Intent intent2 = new Intent(getActivity(), ChangePasswordActivity.class);
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
					ImageUtils.updateGrally(getActivity(), imageUri);
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
		if (!HttpHelper.isNetworkConnected(getActivity())) {
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
				HttpHelper.getHelper().post( ApiUrl.UPLOAD_AVATAR, TutorApplication.getHeaders(), params, new ObjectHttpResponseHandler<UploadAvatarResult>(UploadAvatarResult.class) {

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
