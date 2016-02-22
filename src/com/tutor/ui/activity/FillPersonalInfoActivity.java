package com.tutor.ui.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.HttpURLConnection;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SMSLog;

import com.hk.tutor.R;
import com.loopj.android.http.RequestParams;
import com.tutor.TutorApplication;
import com.tutor.adapter.CurrencysAdapter;
import com.tutor.model.Account;
import com.tutor.model.AccountDao;
import com.tutor.model.Currency;
import com.tutor.model.CurrencyListResult;
import com.tutor.model.UploadAvatarResult;
import com.tutor.model.UserInfo;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.dialog.ChangeAvatarDialog;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.DateTimeUtil;
import com.tutor.util.HttpHelper;
import com.tutor.util.ImageUtils;
import com.tutor.util.LogUtils;
import com.tutor.util.ObjectHttpResponseHandler;
import com.tutor.util.ScreenUtil;

/**
 * 完善資料界面
 * 
 * @author bruce.chen
 * 
 *         2015-8-24
 */
public class FillPersonalInfoActivity extends BaseActivity implements OnClickListener {

	private final String BIRTH = "1990-01-01 00:00:00";
	private int role;
	private Account account;
	/** 是否是编辑资料模式 */
	private boolean isEdit;
	private UserInfo userInfo;
	// views
	/** 姓名,證件號,電話號碼 ,生日,地址 */
	private EditText nameEditText, hKidEditText, addressEditText;
	private TextView birthEditText, hkidTextView;
	private Spinner gradeSpinner;
	private CurrencysAdapter adapter;
	private LinearLayout gradeLinearLayout;
	/** 性別 */
	private RadioGroup sexRadioGroup;
	/** 性别 */
	private Integer sex = null;
	/** 出生日期 */
	private String birth;
	private int gradeValue;
	//
	// 頭像對話框
	private ChangeAvatarDialog dialog;
	// 头像
	private ImageView avatar;
	// 保存拍照圖片uri
	private Uri imageUri, zoomUri;
	private LinearLayout llIsVoluntary;
	private CheckBox cbIsVoluntary;
	private boolean isVoluntaryTutoring;
	private EditText etEmail;
	private EditText etPhone;
	private EditText etVerifyPhone;
	private EditText etVerifyCode;
	private Button btnSendVerifyCode;
	private Button btnCountryCode;
	/**
	 * 国家地区代码, 默认是香港
	 */
	private String COUNTRY_CODE = "852";
	private Timer timer;
	private int reSendSeconds;
	private String name;
	/**
	 * 已经认证过的手机号码
	 */
	private String verifyPhone = "";

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		userInfo = new UserInfo();
		isEdit = getIntent().getBooleanExtra(Constants.IntentExtra.INTENT_EXTRA_ISEDIT, false);
		userInfo = (UserInfo) getIntent().getSerializableExtra(Constants.IntentExtra.INTENT_EXTRA_USER_INFO);
		if (!isEdit) {
			// 第一次填写
			account = TutorApplication.getAccountDao().load("1");
			if (null == account) {
				throw new IllegalArgumentException("account is null");
			}
			role = getIntent().getIntExtra(Constants.IntentExtra.INTENT_EXTRA_ROLE, -1);
			birth = BIRTH;
			gradeValue = -1;
		} else {
			isVoluntaryTutoring = getIntent().getBooleanExtra(Constants.IntentExtra.INTENT_EXTRA_IS_VOLUNTARY_TUTORING, false);
			if (null != userInfo) {
				role = userInfo.getAccountType();
				birth = userInfo.getBirth();
				sex = userInfo.getGender();
				gradeValue = userInfo.getGrade();
			}
			if (TextUtils.isEmpty(birth)) {
				birth = BIRTH;
			}
		}
		if (-1 == role) {
			throw new IllegalArgumentException("role is -1");
		}
		setContentView(R.layout.activity_fill_personal_info);
		initView();
		if (role == Constants.General.ROLE_STUDENT) {
			getGrades();
		}
		// 短信服务监听
		smsInit();
	}

	private void smsInit() {
		EventHandler eh = new EventHandler() {

			@Override
			public void afterEvent(int event, int result, Object data) {
				Message msg = new Message();
				msg.arg1 = event;
				msg.arg2 = result;
				msg.obj = data;
				mHandler.sendMessage(msg);
			}
		};
		SMSSDK.registerEventHandler(eh);
	}

	Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			int event = msg.arg1;
			int result = msg.arg2;
			Object data = msg.obj;
			Log.e("event", "event=" + event);
			if (result == SMSSDK.RESULT_COMPLETE) {
				System.out.println("--------result" + event);
				// 短信注册成功后，返回MainActivity,然后提示新好友
				if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
					// 提交验证码成功
					// Toast.makeText(getApplicationContext(), "提交验证码成功",
					// Toast.LENGTH_SHORT).show();
					nextPage();
				} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
					// 已经验证
					Toast.makeText(getApplicationContext(), R.string.toast_have_send_verify_code, Toast.LENGTH_SHORT).show();
				}
			} else {
				// ((Throwable) data).printStackTrace();
				// Toast.makeText(MainActivity.this, "验证码错误",
				// Toast.LENGTH_SHORT).show();
				// Toast.makeText(MainActivity.this, "123",
				// Toast.LENGTH_SHORT).show();
				int status = 0;
				try {
					((Throwable) data).printStackTrace();
					Throwable throwable = (Throwable) data;
					JSONObject object = new JSONObject(throwable.getMessage());
					String des = object.optString("detail");
					status = object.optInt("status");
					if (!TextUtils.isEmpty(des) && (des.contains("验证码错误") || des.contains("Invalid"))) {
						toast(des);
						return;
					}
				} catch (Exception e) {
					SMSLog.getInstance().w(e);
				}
			}
		}
	};

	private void nextPage() {
		timer.cancel();
		timer = null;
		btnSendVerifyCode.setText(R.string.label_send_verify_code);
		btnSendVerifyCode.setEnabled(true);
		// 老师和学生验证完手机走此逻辑
		transferToNextPage();
	}

	private void transferToNextPage() {
		userInfo.setGrade(gradeValue);
		userInfo.setBirth(birth);
		userInfo.setGender(sex);
		userInfo.setVoluntaryTutoring(cbIsVoluntary.isChecked());
		// 已验证的电话
		userInfo.setPhone("(" + COUNTRY_CODE + ")" + etVerifyPhone.getText().toString());
		// 标记为已经验证
		userInfo.setPhoneVerified(true);
		// 更新数据库
		AccountDao accountDao = TutorApplication.getAccountDao();
		Account account = accountDao.load("1");
		account.setPhone(etVerifyPhone.getText().toString());
		accountDao.update(account);
		//
		Intent intent = null;
		// 选中义务补习
		if (cbIsVoluntary != null && cbIsVoluntary.isChecked()) {
			intent = new Intent(this, VolunteerInfoActivity.class);
		} else {
			intent = new Intent(this, SelectCourseActivity.class);
		}
		intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_ISEDIT, isEdit);
		intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_USER_INFO, userInfo);
		startActivity(intent);
	};

	@Override
	protected void initView() {
		TitleBar bar = getView(R.id.title_bar);
		if (isEdit) {
			bar.initBack(this);
		}
		bar.setTitleTextSize(18.0f);
		if (isEdit) {
			bar.setTitle(R.string.edit_personal_info);
		} else {
			bar.setTitle(R.string.fill_personal_info);
		}
		bar.setRightText(R.string.next, new OnClickListener() {

			@Override
			public void onClick(View v) {
				next();
			}
		});
		// 头像
		avatar = getView(R.id.ac_fill_personal_info_iv_avatar);
		avatar.setOnClickListener(this);
		// 编辑也要上传头像 12/8
		if (isEdit && userInfo != null) {
			ImageUtils.loadImage(avatar, ApiUrl.DOMAIN + userInfo.getAvatar(), userInfo.getGender() != null ? userInfo.getGender() : Constants.General.MALE);
		}
		// 姓名
		nameEditText = getView(R.id.ac_fill_personal_info_et_name);
		// 根据角色显示不同的布局
		if (role == Constants.General.ROLE_TUITION_SCHOOL) {
			// 补习社的布局
			getView(R.id.ll_tuition_school_info).setVisibility(View.VISIBLE);
			etEmail = getView(R.id.et_email);
			etPhone = getView(R.id.et_phone_number);
			// 补习社地址
			addressEditText = getView(R.id.et_tuition_school_address);
		} else {
			etVerifyCode = getView(R.id.et_verify_code);
			btnSendVerifyCode = getView(R.id.btn_send_verify_code);
			btnSendVerifyCode.setOnClickListener(this);
			btnCountryCode = getView(R.id.btn_country_code);
			btnCountryCode.setOnClickListener(this);
			// 验证码
			etVerifyPhone = getView(R.id.et_verify_phone);
			if (userInfo != null && userInfo.isPhoneVerified()) {
				verifyPhone = userInfo.getPhone();
				if (!TextUtils.isEmpty(verifyPhone)) {
					int preBraceIndex = verifyPhone.indexOf("(");
					int suffixBraceIndex = verifyPhone.indexOf(")");
					if (preBraceIndex != -1 && suffixBraceIndex != -1) {
						String countryCode = verifyPhone.substring(preBraceIndex + 1, suffixBraceIndex);
						this.COUNTRY_CODE = countryCode;
						btnCountryCode.setText("+" + countryCode);
						verifyPhone = verifyPhone.substring(suffixBraceIndex + 1);
						etVerifyPhone.setText(verifyPhone);
						String curPhoneNumber = etVerifyPhone.getText().toString();
						if (curPhoneNumber.equals(verifyPhone)) {
							btnSendVerifyCode.setText(R.string.label_is_verify);
							btnSendVerifyCode.setEnabled(false);
						}
					}
				}
			}
			etVerifyPhone.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					String curPhoneNumber = etVerifyPhone.getText().toString();
					if (curPhoneNumber.equals(verifyPhone)) {
						btnSendVerifyCode.setText(R.string.label_is_verify);
						btnSendVerifyCode.setEnabled(false);
					} else {
						btnSendVerifyCode.setText(R.string.label_send_verify_code);
						btnSendVerifyCode.setEnabled(true);
					}
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					// TODO Auto-generated method stub
				}

				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub
				}
			});
			// 老师或者学生的布局
			getView(R.id.ll_tutor_or_student_info).setVisibility(View.VISIBLE);
			// 是否义务补习
			llIsVoluntary = getView(R.id.ll_isVoluntary);
			cbIsVoluntary = getView(R.id.cb_is_voluntary);
			cbIsVoluntary.setChecked(isVoluntaryTutoring);
			if (role == Constants.General.ROLE_TUTOR) {
				llIsVoluntary.setVisibility(View.VISIBLE);
			}
			hKidEditText = getView(R.id.ac_fill_personal_info_et_hkid);
			birthEditText = getView(R.id.ac_fill_personal_info_et_birth);
			gradeSpinner = getView(R.id.ac_fill_personal_info_et_grade);
			hkidTextView = getView(R.id.ac_fill_personal_tv_hkid);
			gradeLinearLayout = getView(R.id.ac_fill_personal_info_ll_grade);
			birthEditText.setOnClickListener(this);
			gradeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					if (null != adapter) {
						Currency currency = adapter.getItem(arg2);
						if (null != currency) {
							gradeValue = currency.getValue();
						}
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {}
			});
			if (Constants.General.ROLE_STUDENT == role) {
				hKidEditText.setHint(R.string.label_optional);
				gradeLinearLayout.setVisibility(View.VISIBLE);
				hkidTextView.setVisibility(View.GONE);
			}
			sexRadioGroup = getView(R.id.ac_fill_personal_info_rg);
			sexRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					switch (checkedId) {
						case R.id.ac_fill_personal_info_rb_male:
							sex = Constants.General.MALE;
							break;
						case R.id.ac_fill_personal_info_rb_female:
							sex = Constants.General.FEMALE;
							break;
						case R.id.ac_fill_personal_info_rb_ignore:
							sex = null;
							break;
					}
				}
			});
			birthEditText.setText(birth.substring(0, 11));
		}
		if (isEdit && null != userInfo) {
			// name共有字段
			String name = userInfo.getUserName();
			if (!TextUtils.isEmpty(name)) {
				nameEditText.setText(name);
				nameEditText.setSelection(name.length());
			} else {
				nameEditText.setText("");
			}
			if (role == Constants.General.ROLE_TUITION_SCHOOL) {
				etEmail.setText(userInfo.getContactEmail());
				etPhone.setText(userInfo.getPhone());
				addressEditText.setText(userInfo.getResidentialAddress());
			} else {
				hKidEditText.setText(userInfo.getHkidNumber());
				int checkId;
				if (sex == null) {
					checkId = R.id.ac_fill_personal_info_rb_ignore;
				} else if (Constants.General.MALE == sex) {
					checkId = R.id.ac_fill_personal_info_rb_male;
				} else {
					checkId = R.id.ac_fill_personal_info_rb_female;
				}
				sexRadioGroup.check(checkId);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 选择生日
			case R.id.ac_fill_personal_info_et_birth:
				Calendar calendar = Calendar.getInstance();
				Date date = DateTimeUtil.str2Date(birth, DateTimeUtil.FORMART_2);
				calendar.setTime(date);
				DatePickerDialog datePickerDialog = new DatePickerDialog(this, new OnDateSetListener() {

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
						birth = y + "-" + month + "-" + day + " 00:00:00";
						birthEditText.setText(birth.substring(0, 11));
						LogUtils.d(birth);
					}
				}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
				datePickerDialog.show();
				break;
			// 上傳頭像
			case R.id.ac_fill_personal_info_iv_avatar:
				if (null == dialog) {
					dialog = new ChangeAvatarDialog(this);
				}
				ImageUtils.clearChache();
				// 初始化uri
				String fileName = DateTimeUtil.getSystemDateTime(DateTimeUtil.FORMART_YMDHMS) + Constants.General.IMAGE_END;
				imageUri = Uri.fromFile(new File(Constants.SDCard.getImageDir(), fileName));
				dialog.setUri(imageUri);
				dialog.show();
				break;
			case R.id.btn_send_verify_code:
				// 发送验证码
				sendVerifyCode();
				break;
			case R.id.btn_country_code:
				// 改变国家代码
				showCountryCode(btnCountryCode);
				break;
			default:
				break;
		}
	}

	private void showCountryCode(Button btnChat) {
		View view = View.inflate(FillPersonalInfoActivity.this, R.layout.layout_country_code, null);
		final PopupWindow popupWindow = new PopupWindow(view);
		popupWindow.setFocusable(true);
		int itemWidth = ScreenUtil.dip2Px(FillPersonalInfoActivity.this, 60);
		int itemHeight = ScreenUtil.dip2Px(FillPersonalInfoActivity.this, 45) * 4;
		// 设置SelectPicPopupWindow弹出窗体的宽
		popupWindow.setWidth(itemWidth);
		// 设置SelectPicPopupWindow弹出窗体的高
		popupWindow.setHeight(itemHeight);
		// 点击“返回Back”也能使其消失
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new ColorDrawable(R.color.white));
		popupWindow.showAsDropDown(btnChat, 0, 0);
		view.findViewById(R.id.btn_hk).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				COUNTRY_CODE = "852";
				btnCountryCode.setText(R.string.label_852);
				popupWindow.dismiss();
			}
		});
		view.findViewById(R.id.btn_cn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				COUNTRY_CODE = "86";
				btnCountryCode.setText(R.string.label_86);
				popupWindow.dismiss();
			}
		});
		view.findViewById(R.id.btn_uk).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				COUNTRY_CODE = "44";
				btnCountryCode.setText(R.string.label_44);
				popupWindow.dismiss();
			}
		});
		view.findViewById(R.id.btn_usa).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				COUNTRY_CODE = "1";
				btnCountryCode.setText(R.string.label_1);
				popupWindow.dismiss();
			}
		});
	}

	private void sendVerifyCode() {
		String phoneNum = etVerifyPhone.getText().toString();
		if (TextUtils.isEmpty(phoneNum)) {
			toast(R.string.toast_phone_number_is_empty);
			return;
		} else if (phoneNum.length() < 8 || phoneNum.length() > 11) {
			toast(R.string.toast_phone_number_invalid);
			return;
		} else if (!HttpHelper.isNetworkConnected(FillPersonalInfoActivity.this)) {
			toast(R.string.toast_internet_disconnect);
		} else {
			reSendSeconds = 60;
			btnSendVerifyCode.setEnabled(false);
			if (timer == null) {
				timer = new Timer();
			}
			timer.schedule(new SecondTask(), 0, 1000);
			// 发送短信验证码
			SMSSDK.getVerificationCode(this.COUNTRY_CODE, phoneNum);
		}
	}

	/**
	 * 改变获取验证码按钮上面文字的线程
	 * 
	 */
	private class SecondTask extends TimerTask {

		@Override
		public void run() {
			Message message = Message.obtain();
			message.what = 1;
			handler.sendMessage(message);
			reSendSeconds--;
		}
	}

	/**
	 * 改变获取验证码按钮上面文字
	 */
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					String label = String.format(getString(R.string.label_resend_captcha), reSendSeconds);
					btnSendVerifyCode.setText(label);
					if (reSendSeconds == 0) {
						timer.cancel();
						timer = null;
						btnSendVerifyCode.setText(R.string.label_send_verify_code);
						btnSendVerifyCode.setEnabled(true);
					}
					break;
				default:
					break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case Constants.RequestResultCode.TAKE_PHOTO:
				// 拍照回來
				if (BaseActivity.RESULT_OK == resultCode && null != imageUri) {
					// 更新圖庫
					ImageUtils.updateGrally(this, imageUri);
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
		if (!HttpHelper.isNetworkConnected(this)) {
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
						// toast(R.string.toast_avatar_upload_fail);
					}

					@Override
					public void onSuccess(UploadAvatarResult t) {
						dismissDialog();
						if (HttpURLConnection.HTTP_OK == t.getStatusCode()) {
							ImageUtils.clearChache();
							ImageUtils.loadImage(avatar, ApiUrl.DOMAIN + t.getResult(), sex != null ? sex : Constants.General.MALE);
							// 把新头像设置进userInfo model(解决上传完头像马上编辑信息bug) 12/9
							userInfo.setAvatar(t.getResult());
						}
					}
				});
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				toast(R.string.toast_avatar_upload_fail);
			}
		}
	}

	@Override
	protected void onStart() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.Action.FINISH_LOGINACTIVITY);
		registerReceiver(receiver, filter);
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		SMSSDK.unregisterAllEventHandler();
		super.onDestroy();
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (Constants.Action.FINISH_LOGINACTIVITY.equals(intent.getAction())) {
				finishNoAnim();
			}
		}
	};

	private void next() {
		// 昵称
		name = nameEditText.getText().toString();
		if (TextUtils.isEmpty(name)) {
			toast(R.string.toast_name_isEmpty);
			nameEditText.requestFocus();
			return;
		}
		if (userInfo == null) {
			userInfo = new UserInfo();
		}
		userInfo.setUserName(name);
		// 不是编辑
		if (!isEdit) {
			userInfo = getUserInfo();
		}
		Intent intent = null;
		// 补习社
		if (role == Constants.General.ROLE_TUITION_SCHOOL) {
			userInfo.setContactEmail(etEmail.getText().toString());
			userInfo.setPhone(etPhone.getText().toString());
			userInfo.setResidentialAddress(addressEditText.getText().toString());
			intent = new Intent(this, SelectTuitionCenterAreaActivity.class);
			intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_ISEDIT, isEdit);
			intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_USER_INFO, userInfo);
			startActivity(intent);
		} else {
			// 学生必须填写年级
			if (Constants.General.ROLE_STUDENT == role && gradeValue == -1) {
				toast(R.string.toast_grade_noChoice);
				return;
			}
			// 手机号和验证码必填
			String phone = etVerifyPhone.getText().toString();
			if (TextUtils.isEmpty(phone)) {
				toast(R.string.toast_phone_number_is_empty);
				return;
			} else if (phone.length() < 8 || phone.length() > 11) {
				toast(R.string.toast_phone_number_invalid);
				return;
			}
			if (!TextUtils.isEmpty(verifyPhone) && verifyPhone.equals(phone)) {
				// 之前验证过 下一步
				transferToNextPage();
			} else {
				String verifyCode = etVerifyCode.getText().toString();
				if (TextUtils.isEmpty(verifyCode)) {
					toast(R.string.toast_verify_code_is_empty);
					return;
				}
				// 发送验证码到短信服务器校验
				SMSSDK.submitVerificationCode(this.COUNTRY_CODE, phone, verifyCode);
			}
		}
	}

	private UserInfo getUserInfo() {
		userInfo.setExprience(0);
		userInfo.setId(account.getMemberId());
		if (role != Constants.General.ROLE_TUITION_SCHOOL) {
			userInfo.setEmail(account.getEmail());
		}
		userInfo.setPassword(account.getPswd());
		userInfo.setFbOpenID(account.getFacebookId());
		userInfo.setImid(account.getImAccount());
		userInfo.setCreatedTime(account.getCreatedTime());
		userInfo.setAccountType(role);
		return userInfo;
	}

	private void getGrades() {
		if (!HttpHelper.isNetworkConnected(this)) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		showDialogRes(R.string.loading);
		HttpHelper.getHelper().get(ApiUrl.GRADE, TutorApplication.getHeaders(), new RequestParams(), new ObjectHttpResponseHandler<CurrencyListResult>(CurrencyListResult.class) {

			@Override
			public void onFailure(int status, String message) {
				// 连接超时,再获取一次
				if (0 == status) {
					getGrades();
					return;
				}
				dismissDialog();
				toast(R.string.toast_server_error);
			}

			@Override
			public void onSuccess(CurrencyListResult t) {
				dismissDialog();
				if (null != t) {
					adapter = new CurrencysAdapter(FillPersonalInfoActivity.this, t.getResult());
					gradeSpinner.setAdapter(adapter);
					if (null == userInfo) {
						gradeValue = 0;
					} else {
						gradeValue = userInfo.getGrade();
						int size = t.getResult().size();
						for (int i = 0; i < size; i++) {
							Currency currency = t.getResult().get(i);
							if (currency.getValue() == gradeValue) {
								gradeSpinner.setSelection(i);
								break;
							}
						}
					}
				} else {
					toast(R.string.toast_server_error);
				}
			}
		});
	}
}
