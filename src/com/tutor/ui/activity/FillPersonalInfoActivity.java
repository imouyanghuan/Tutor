package com.tutor.ui.activity;

import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.tutor.R;
import com.tutor.TutorApplication;
import com.tutor.model.Account;
import com.tutor.model.UserInfo;
import com.tutor.params.Constants;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.DateTimeUtil;
import com.tutor.util.LogUtils;

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
	private EditText nameEditText, hKidEditText, phoneEditText, addressEditText;
	private TextView birthEditText;
	/** 性別 */
	private RadioGroup sexRadioGroup;
	/** 性别 */
	private int sex = 0;
	/** 出生日期 */
	private String birth;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		isEdit = getIntent().getBooleanExtra(Constants.IntentExtra.INTENT_EXTRA_ISEDIT, false);
		if (!isEdit) {
			// 第一次填写
			account = TutorApplication.getAccountDao().load("1");
			if (null == account) {
				throw new IllegalArgumentException("account is null");
			}
			role = getIntent().getIntExtra(Constants.IntentExtra.INTENT_EXTRA_ROLE, -1);
			birth = BIRTH;
		} else {
			userInfo = (UserInfo) getIntent().getSerializableExtra(Constants.IntentExtra.INTENT_EXTRA_USER_INFO);
			if (null != userInfo) {
				role = userInfo.getAccountType();
				birth = userInfo.getBirth();
				sex = userInfo.getGender();
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
	}

	@Override
	protected void initView() {
		TitleBar bar = getView(R.id.title_bar);
		bar.initBack(this);
		bar.setTitleTextSize(20.0f);
		if (isEdit) {
			bar.setTitle(R.string.edit_personal_info);
		} else {
			bar.setTitle(R.string.fill_personal_info);
		}
		bar.setRightText(R.string.next, new OnClickListener() {

			@Override
			public void onClick(View v) {
				onsend();
			}
		});
		nameEditText = getView(R.id.ac_fill_personal_info_et_name);
		hKidEditText = getView(R.id.ac_fill_personal_info_et_hkid);
		phoneEditText = getView(R.id.ac_fill_personal_info_et_phone);
		birthEditText = getView(R.id.ac_fill_personal_info_et_birth);
		birthEditText.setOnClickListener(this);
		addressEditText = getView(R.id.ac_fill_personal_info_et_address);
		if (Constants.General.ROLE_STUDENT == role) {
			hKidEditText.setHint(R.string.label_optional);
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
				}
			}
		});
		birthEditText.setText(birth.substring(0, 11));
		if (isEdit) {
			if (null != userInfo) {
				nameEditText.setText(userInfo.getUserName());
				hKidEditText.setText(userInfo.getHkidNumber());
				phoneEditText.setText(userInfo.getPhone());
				addressEditText.setText(userInfo.getResidentialAddress());
				sexRadioGroup.check(Constants.General.MALE == userInfo.getGender() ? R.id.ac_fill_personal_info_rb_male : R.id.ac_fill_personal_info_rb_female);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
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
			default:
				break;
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

	private void onsend() {
		// 昵称
		String name = nameEditText.getEditableText().toString().trim();
		if (TextUtils.isEmpty(name)) {
			toast(R.string.toast_name_isEmpty);
			nameEditText.requestFocus();
			return;
		}
		// hkid 教师才有
		String hkid = hKidEditText.getEditableText().toString().trim();
		if (Constants.General.ROLE_TUTOR == role) {
			if (TextUtils.isEmpty(hkid)) {
				toast(R.string.toast_hkid_isEmpty);
				hKidEditText.requestFocus();
				return;
			}
		}
		if (TextUtils.isEmpty(hkid)) {
			hkid = "";
		}
		// 电话号码
		String phone = phoneEditText.getEditableText().toString().trim();
		if (TextUtils.isEmpty(phone)) {
			toast(R.string.toast_phone_isEmpty);
			phoneEditText.requestFocus();
			return;
		}
		// 地址
		String address = addressEditText.getEditableText().toString().trim();
		if (TextUtils.isEmpty(address)) {
			toast(R.string.toast_address_isEmpty);
			addressEditText.requestFocus();
			return;
		}
		Intent intent = new Intent(this, SelectCourseActivity.class);
		intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_ISEDIT, isEdit);
		if (!isEdit) {
			userInfo = getUserInfo();
		}
		userInfo.setHkidNumber(hkid);
		userInfo.setUserName(name);
		userInfo.setPhone(phone);
		userInfo.setBirth(birth);
		userInfo.setGender(sex);
		userInfo.setResidentialAddress(address);
		intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_USER_INFO, userInfo);
		startActivity(intent);
	}

	private UserInfo getUserInfo() {
		UserInfo userInfo = new UserInfo();
		userInfo.setExprience(1);
		userInfo.setRatingGrade(5.0f);
		userInfo.setBookmarkedCount(0);
		userInfo.setStudentCount(0);
		userInfo.setId(account.getMemberId());
		userInfo.setEmail(account.getEmail());
		userInfo.setPassword(account.getPswd());
		userInfo.setFbOpenID(account.getFacebookId());
		userInfo.setImid(account.getImAccount());
		userInfo.setStatus(account.getRole());
		userInfo.setCreatedTime(account.getCreatedTime());
		userInfo.setToken(account.getToken());
		userInfo.setAccountType(role);
		return userInfo;
	}
}
