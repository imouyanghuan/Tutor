package com.tutor.ui.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
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
 * 老师首頁,我的
 * 
 * @author jerry.yao
 * 
 */
public class TutorProfileActivity extends BaseActivity implements OnClickListener {

	/** 頂部菜單 */
	private TitleBar bar;
	// 頭像對話框
	private ChangeAvatarDialog dialog;
	// 头像
	private ImageView avatar;
	// 保存拍照圖片uri
	private Uri imageUri, zoomUri;
	// 用户名,性别,修改资料
	private TextView userName, gender, editInfo, registrationTime, changepswd;
	private RadioGroup educationStatus;
	// 昵称,大学,专业,年限,自我介绍,视频地址
	private EditText nickName, school, major, year, introduction, videoLink;
	// 播放视频
	private ImageButton play;
	// 保存
	private Button save;
	/** 个人信息 */
	private UserInfo userInfo;
	private int es;
	private String rTime;
	private boolean isVoluntaryTutoring;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.fragment_teacher_my);
		initView();
		// 获取数据
		getTutorProfile();
	}

	/**
	 * 获取资料
	 */
	private void getTutorProfile() {
		RequestParams params = new RequestParams();
		params.put("memberId", TutorApplication.getMemberId());
		HttpHelper.getHelper().get(ApiUrl.TUTORINFO, TutorApplication.getHeaders(), params, new ObjectHttpResponseHandler<UserInfoResult>(UserInfoResult.class) {

			@Override
			public void onFailure(int status, String message) {
				if (0 == status) {
					getTutorProfile();
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

	@Override
	public void onResume() {
		super.onResume();
		// 更新数据
		getTutorProfile();
	}

	/**
	 * 设置数据
	 * 
	 * @param profile
	 */
	private void setData(UserInfo profile) {
		isVoluntaryTutoring = profile.isVoluntaryTutoring();
		userInfo = profile;
		// 加载头像
		ImageUtils.loadImage(avatar, ApiUrl.DOMAIN + ApiUrl.GET_OTHER_AVATAR + TutorApplication.getMemberId(), userInfo.getGender() != null ? userInfo.getGender() : Constants.General.MALE);
		userName.setText(!TextUtils.isEmpty(profile.getUserName()) ? profile.getUserName() : "Tutor" + profile.getId());
		int genderStr;
		if (userInfo.getGender() == null) {
			genderStr = R.string.label_ignore1;
		} else if (Constants.General.MALE == userInfo.getGender()) {
			genderStr = R.string.label_male;
		} else {
			genderStr = R.string.label_female;
		}
		gender.setText(genderStr);
		nickName.setText(!TextUtils.isEmpty(profile.getNickName()) ? profile.getNickName() : "");
		school.setText(!TextUtils.isEmpty(profile.getGraduateSchool()) ? profile.getGraduateSchool() : "");
		major.setText(!TextUtils.isEmpty(profile.getMajor()) ? profile.getMajor() : "");
		int expYear = profile.getExprience();
		year.setText((expYear > 0 ? String.valueOf(expYear) : ""));
		introduction.setText(!TextUtils.isEmpty(profile.getIntroduction()) ? profile.getIntroduction() : "");
		videoLink.setText(!TextUtils.isEmpty(profile.getIntroductionVideo()) ? profile.getIntroductionVideo() : "");
		educationStatus.check(Constants.General.STUDYING == profile.getEducationStatus() ? R.id.fragment_my_rb_studying : R.id.fragment_my_rb_graduated);
		registrationTime.setText(TextUtils.isEmpty(profile.getRegistrationTime()) ? getString(R.string.label_none) : profile.getRegistrationTime().substring(0, 11));
		rTime = profile.getRegistrationTime();
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
		school = getView(R.id.fragment_my_et_school);
		educationStatus = getView(R.id.fragment_my_rg_es);
		major = getView(R.id.fragment_my_et_major);
		year = getView(R.id.fragment_my_et_year);
		registrationTime = getView(R.id.fragment_my_tv_registration_time);
		videoLink = getView(R.id.fragment_my_et_video_path);
		//
		play = getView(R.id.fragment_my_ib_play);
		save = getView(R.id.fragment_my_btn_save);
		changepswd = getView(R.id.fragment_my_tv_changepswd);
		educationStatus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
					case R.id.fragment_my_rb_studying:
						es = Constants.General.STUDYING;
						break;
					case R.id.fragment_my_rb_graduated:
						es = Constants.General.GRADUATED;
						break;
				}
			}
		});
		editInfo.setOnClickListener(this);
		changepswd.setOnClickListener(this);
		play.setOnClickListener(this);
		save.setOnClickListener(this);
		registrationTime.setOnClickListener(this);
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
				if(account != null){
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

				// TODO 请求退出登录api
				// 斷開IM連接
				XMPPConnectionManager.getManager().disconnect();
				// 停止Push服务
				JPushInterface.stopPush(TutorProfileActivity.this);
				ScreenManager.getScreenManager().removeAllActivity();
				TutorApplication.settingManager.writeSetting(Constants.SharedPreferences.SP_ISLOGIN, false);
				Intent intent = new Intent(TutorProfileActivity.this, ChoiceRoleActivity.class);
				startActivity(intent);
				finishNoAnim();
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
			case R.id.fragment_my_iv_avatar:
				// 上傳頭像 android 6.0 无法保存更改
				if (null == dialog) {
					dialog = new ChangeAvatarDialog((BaseActivity) TutorProfileActivity.this);
				}
				ImageUtils.clearChache();
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
				if (!link.startsWith("http")) {
					toast(R.string.toast_video_unplay);
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
				if (null == userInfo) {
					toast(R.string.loading);
					return;
				}
				Intent intent = new Intent();
				intent.setClass(TutorProfileActivity.this, FillPersonalInfoActivity.class);
				intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_ISEDIT, true);
				intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_IS_VOLUNTARY_TUTORING, isVoluntaryTutoring);
				intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_USER_INFO, userInfo);
				startActivity(intent);
				break;
			case R.id.fragment_my_tv_registration_time:
				Calendar calendar = Calendar.getInstance();
				if (!TextUtils.isEmpty(rTime)) {
					Date date = DateTimeUtil.str2Date(rTime, DateTimeUtil.FORMART_2);
					calendar.setTime(date);
				}
				DatePickerDialog datePickerDialog = new DatePickerDialog(TutorProfileActivity.this, new OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int y, int m, int d) {
						String month = (m + 1) + "";
						if (m < 9) {
							month = "0" + month;
						}
						String day = d + "";
						if (d < 10) {
							day = "0" + day;
						}
						rTime = y + "-" + month + "-" + day + " 00:00:00";
						registrationTime.setText(rTime.substring(0, 11));
						LogUtils.d(rTime);
					}
				}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
				datePickerDialog.show();
				break;
			case R.id.fragment_my_tv_changepswd:
				Intent intent2 = new Intent(TutorProfileActivity.this, ChangePasswordActivity.class);
				intent2.putExtra(Constants.IntentExtra.INTENT_EXTRA_PASSWORD_FLAG, Constants.General.CHANGE_PASSWORD);
				startActivity(intent2);
				break;
			default:
				break;
		}
	}

	/**
	 * 提交老师修改资料
	 */
	private void save() {
		if (null == userInfo) {
			toast(R.string.loading);
			return;
		}
		if (!HttpHelper.isNetworkConnected(TutorProfileActivity.this)) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		showDialogRes(R.string.loading);
		String json = JsonUtil.parseObject2Str(getUserInfo());
		LogUtils.d(json);
		try {
			StringEntity entity = new StringEntity(json, HTTP.UTF_8);
			HttpHelper.getHelper().put(ApiUrl.TUTORPROFILE, TutorApplication.getHeaders(), entity, new ObjectHttpResponseHandler<EditProfileResult>(EditProfileResult.class) {

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
		userInfo.setGraduateSchool(school.getEditableText().toString().trim());
		userInfo.setMajor(major.getEditableText().toString().trim());
		if (!TextUtils.isEmpty(year.getEditableText())) {
			userInfo.setExprience(Integer.parseInt(year.getEditableText().toString()));
		} else {
			userInfo.setExprience(0);
		}
		userInfo.setIntroduction(introduction.getEditableText().toString().trim());
		userInfo.setIntroductionVideo(videoLink.getEditableText().toString().trim());
		userInfo.setEducationStatus(es);
		userInfo.setRegistrationTime(rTime);
		return userInfo;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case Constants.RequestResultCode.TAKE_PHOTO:
				// 拍照回來
				if (BaseActivity.RESULT_OK == resultCode && null != imageUri) {
					// 更新圖庫
					ImageUtils.updateGrally(TutorProfileActivity.this, imageUri);
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
				if (BaseActivity.RESULT_OK == resultCode && null != zoomUri) { // resultCode
																				// =
																				// 0
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
		String path = data.getPath();// /-1/1/content://media/external/images/media/10609/ACTUAL/1268057965
		String fileName = path.substring(path.lastIndexOf(File.separatorChar) + 1);
		zoomUri = Uri.fromFile(new File(Constants.SDCard.getCacheDir(), fileName + Constants.General.IMAGE_END));// file:///storage/emulated/0/Tutor/cache/1268057965.jpg
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
		if (!HttpHelper.isNetworkConnected(TutorProfileActivity.this)) {
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
