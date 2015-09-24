package com.tutor.ui.fragment.teacher;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.tutor.R;
import com.tutor.TutorApplication;
import com.tutor.model.EditProfileResult;
import com.tutor.model.TeacherInfoResult;
import com.tutor.model.TeacherProfile;
import com.tutor.model.UploadAvatarResult;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.activity.BaseActivity;
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
 * 老師首頁,我的
 * 
 * @author bruce.chen
 * 
 *         2015-8-20
 */
public class MyFragment extends BaseFragment implements OnClickListener {

	// 頭像對話框
	private ChangeAvatarDialog dialog;
	// 头像
	private ImageView avatar;
	// 保存拍照圖片uri
	private Uri imageUri, zoomUri;
	// 用户名,性别,修改资料
	private TextView userName, gender, editInfo;
	// 昵称,大学,专业,年限,自我介绍,视频地址
	private EditText nickName, school, major, year, introduction, videoLink;
	// 播放视频
	private ImageButton play;
	// 保存
	private Button save;
	/** 个人信息 */
	private TeacherProfile teacherProfile;
	private boolean isUpdate = false;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_teacher_my, container, false);
		initView(view);
		return view;
	}

	private void initView(View view) {
		avatar = ViewHelper.get(view, R.id.fragment_my_iv_avatar);
		avatar.setOnClickListener(this);
		// 加载头像
		ImageUtils.loadImage(avatar, ApiUrl.DOMAIN + ApiUrl.GET_OTHER_AVATAR + TutorApplication.getMemberId());
		//
		userName = ViewHelper.get(view, R.id.fragment_my_tv_username);
		gender = ViewHelper.get(view, R.id.fragment_my_tv_gender);
		editInfo = ViewHelper.get(view, R.id.fragment_my_tv_edit);
		//
		nickName = ViewHelper.get(view, R.id.fragment_my_et_nickname);
		school = ViewHelper.get(view, R.id.fragment_my_et_school);
		major = ViewHelper.get(view, R.id.fragment_my_et_major);
		year = ViewHelper.get(view, R.id.fragment_my_et_year);
		introduction = ViewHelper.get(view, R.id.fragment_my_et_introduction);
		videoLink = ViewHelper.get(view, R.id.fragment_my_et_video_path);
		//
		play = ViewHelper.get(view, R.id.fragment_my_ib_play);
		save = ViewHelper.get(view, R.id.fragment_my_btn_save);
		editInfo.setOnClickListener(this);
		play.setOnClickListener(this);
		save.setOnClickListener(this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// 获取数据
		getTutorProfile();
	}

	/**
	 * 设置数据
	 * 
	 * @param profile
	 */
	private void setData(TeacherProfile profile) {
		teacherProfile = profile;
		userName.setText(!TextUtils.isEmpty(profile.getUserName()) ? profile.getUserName() : "Tutor" + profile.getId());
		gender.setText(Constants.General.MALE == profile.getGender() ? R.string.label_male : R.string.label_female);
		nickName.setText(!TextUtils.isEmpty(profile.getNickName()) ? profile.getNickName() : "");
		school.setText(!TextUtils.isEmpty(profile.getGraduateSchool()) ? profile.getGraduateSchool() : "");
		major.setText(!TextUtils.isEmpty(profile.getMajor()) ? profile.getMajor() : "");
		year.setText(profile.getExprience() + "");
		introduction.setText(!TextUtils.isEmpty(profile.getIntroduction()) ? profile.getIntroduction() : "");
		videoLink.setText(!TextUtils.isEmpty(profile.getIntroductionVideo()) ? profile.getIntroductionVideo() : "");
	}

	@Override
	public void onResume() {
		super.onResume();
		if (isUpdate) {
			// 更新数据
			getTutorProfile();
			isUpdate = false;
		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
			case R.id.fragment_my_iv_avatar:
				// 上傳頭像
				if (null == dialog) {
					dialog = new ChangeAvatarDialog((BaseActivity) getActivity());
				}
				// 初始化uri
				String fileName = DateTimeUtil.getSystemDateTime(DateTimeUtil.FORMART_YMDHMS) + Constants.General.IMAGE_END;
				imageUri = Uri.fromFile(new File(Constants.SDCard.getImageDir(), fileName));
				dialog.setUri(imageUri);
				dialog.show();
				break;
			case R.id.fragment_my_ib_play:
				// 播放视频
				String link = videoLink.getEditableText().toString().trim();
				if (TextUtils.isEmpty(link)) {
					toast(R.string.toast_video_link_empety);
					return;
				}
				try {
					Intent it = new Intent(Intent.ACTION_VIEW);
					Uri uri = Uri.parse(link);
					it.setDataAndType(uri, "video/*");
					startActivity(it);
				} catch (Exception e) {
					e.printStackTrace();
					toast(R.string.toast_video_unplay);
				}
				break;
			case R.id.fragment_my_btn_save:
				save();
				break;
			case R.id.fragment_my_tv_edit:
				if (null == teacherProfile) {
					toast(R.string.loading);
					return;
				}
				Intent intent = new Intent();
				intent.setClass(getActivity(), FillPersonalInfoActivity.class);
				intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_ISEDIT, true);
				intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_TUTORPRIFILE, teacherProfile);
				startActivity(intent);
				isUpdate = true;
				break;
			default:
				break;
		}
	}

	/**
	 * 提交老师修改资料
	 */
	private void save() {
		if (null == teacherProfile) {
			toast(R.string.loading);
			return;
		}
		if (!HttpHelper.isNetworkConnected(getActivity())) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		showDialogRes(R.string.loading);
		String json = JsonUtil.parseObject2Str(getTeacherProfile());
		LogUtils.d(json);
		try {
			StringEntity entity = new StringEntity(json, HTTP.UTF_8);
			HttpHelper.put(getActivity(), ApiUrl.TUTORPROFILE, TutorApplication.getHeaders(), entity, new ObjectHttpResponseHandler<EditProfileResult>(EditProfileResult.class) {

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
							setData(teacherProfile);
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

	private TeacherProfile getTeacherProfile() {
		teacherProfile.setNickName(nickName.getEditableText().toString().trim());
		teacherProfile.setGraduateSchool(school.getEditableText().toString().trim());
		teacherProfile.setMajor(major.getEditableText().toString().trim());
		teacherProfile.setExprience(Integer.parseInt(year.getEditableText().toString()));
		teacherProfile.setIntroduction(introduction.getEditableText().toString().trim());
		teacherProfile.setIntroductionVideo(videoLink.getEditableText().toString().trim());
		return teacherProfile;
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
				HttpHelper.post(getActivity(), ApiUrl.UPLOAD_AVATAR, TutorApplication.getHeaders(), params, new ObjectHttpResponseHandler<UploadAvatarResult>(UploadAvatarResult.class) {

					@Override
					public void onFailure(int status, String message) {
						dismissDialog();
						toast(R.string.toast_avatar_upload_fail);
					}

					@Override
					public void onSuccess(UploadAvatarResult t) {
						dismissDialog();
						CheckTokenUtils.checkToken(t);
						if (HttpURLConnection.HTTP_OK == t.getStatusCode()) {
							ImageUtils.loadImage(avatar, ApiUrl.DOMAIN + t.getResult());
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

	/**
	 * 获取资料
	 */
	private void getTutorProfile() {
		RequestParams params = new RequestParams();
		params.put("memberId", TutorApplication.getMemberId());
		HttpHelper.get(getActivity(), ApiUrl.TUTORINFO, TutorApplication.getHeaders(), params, new ObjectHttpResponseHandler<TeacherInfoResult>(TeacherInfoResult.class) {

			@Override
			public void onFailure(int status, String message) {
				if (0 == status) {
					getTutorProfile();
					return;
				}
				CheckTokenUtils.checkToken(status);
				toast(R.string.toast_server_error);
			}

			@Override
			public void onSuccess(TeacherInfoResult t) {
				if (null != t.getResult()) {
					setData(t.getResult());
				} else {
					toast(R.string.toast_server_error);
				}
			}
		});
	}
}
