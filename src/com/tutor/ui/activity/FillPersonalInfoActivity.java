package com.tutor.ui.activity;

import java.util.Calendar;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.tutor.R;
import com.tutor.TutorApplication;
import com.tutor.model.Account;
import com.tutor.model.StudentProfile;
import com.tutor.model.TeacherProfile;
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
public class FillPersonalInfoActivity extends BaseActivity implements OnDateChangedListener {

	private int role;
	private Account account;
	/** 是否是编辑资料模式 */
	private boolean isEdit;
	private TeacherProfile teacherProfile;
	private StudentProfile studentProfile;
	// views
	/** 姓名,證件號,電話號碼 */
	private EditText nameEditText, hKidEditText, phoneEditText;
	/** 證件佈局,課程佈局 */
	private LinearLayout hkidLinearLayout;
	/** 性別 */
	private RadioGroup sexRadioGroup;
	private DatePicker datePicker;
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
			birth = "1990-01-01 00:00:00";
		} else {
			teacherProfile = (TeacherProfile) getIntent().getSerializableExtra(Constants.IntentExtra.INTENT_EXTRA_TUTORPRIFILE);
			studentProfile = (StudentProfile) getIntent().getSerializableExtra(Constants.IntentExtra.INTENT_EXTRA_STUDENTPROFILE);
			if (null != teacherProfile) {
				role = teacherProfile.getAccountType();
				birth = teacherProfile.getBirth();
				sex = teacherProfile.getGender();
			} else {
				role = studentProfile.getAccountType();
				birth = studentProfile.getBirth();
				sex = studentProfile.getGender();
			}
			if (TextUtils.isEmpty(birth)) {
				birth = "1990-01-01 00:00:00";
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
		hkidLinearLayout = getView(R.id.ac_fill_personal_info_ll_hkid);
		if (Constants.General.ROLE_TUTOR == role) {
			hkidLinearLayout.setVisibility(View.VISIBLE);
		} else {
			hkidLinearLayout.setVisibility(View.GONE);
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
		if (isEdit) {
			if (null != teacherProfile) {
				nameEditText.setText(teacherProfile.getUserName());
				hKidEditText.setText(teacherProfile.getHkidNumber());
				phoneEditText.setText(teacherProfile.getPhone());
				sexRadioGroup.check(Constants.General.MALE == teacherProfile.getGender() ? R.id.ac_fill_personal_info_rb_male : R.id.ac_fill_personal_info_rb_female);
			} else {
				nameEditText.setText(studentProfile.getUserName());
				phoneEditText.setText(studentProfile.getPhone());
				sexRadioGroup.check(Constants.General.MALE == studentProfile.getGender() ? R.id.ac_fill_personal_info_rb_male : R.id.ac_fill_personal_info_rb_female);
			}
		}
		// 初始化生日选择组件
		datePicker = getView(R.id.ac_fill_personal_info_datePicker);
		Calendar calendar = Calendar.getInstance();
		Date date = DateTimeUtil.str2Date(birth, DateTimeUtil.FORMART_2);
		calendar.setTime(date);
		datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), this);
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

	@Override
	public void onDateChanged(DatePicker view, int y, int m, int d) {
		String month = (m + 1) + "";
		if (m < 9) {
			month = "0" + month;
		}
		String day = d + "";
		if (d < 10) {
			day = "0" + day;
		}
		birth = y + "-" + month + "-" + day + " 00:00:00";
		LogUtils.d(birth);
	}

	private void onsend() {
		// 昵称
		String name = nameEditText.getEditableText().toString().trim();
		if (TextUtils.isEmpty(name)) {
			toast(R.string.toast_name_isEmpty);
			nameEditText.requestFocus();
			return;
		}
		// hkid 教师才有
		String hkid = null;
		if (Constants.General.ROLE_TUTOR == role) {
			hkid = hKidEditText.getEditableText().toString().trim();
			if (TextUtils.isEmpty(hkid)) {
				toast(R.string.toast_hkid_isEmpty);
				hKidEditText.requestFocus();
				return;
			}
		}
		// 电话号码
		String phone = phoneEditText.getEditableText().toString().trim();
		if (TextUtils.isEmpty(phone)) {
			toast(R.string.toast_phone_isEmpty);
			phoneEditText.requestFocus();
			return;
		}
		Intent intent = new Intent(this, SelectCourseActivity.class);
		intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_ISEDIT, isEdit);
		if (Constants.General.ROLE_TUTOR == role) {
			if (!isEdit) {
				teacherProfile = getTeacherProfile();
			}
			teacherProfile.setHkidNumber(hkid);
			teacherProfile.setUserName(name);
			teacherProfile.setPhone(phone);
			intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_TUTORPRIFILE, teacherProfile);
		} else {
			if (!isEdit) {
				studentProfile = getStudentProfile();
			}
			studentProfile.setUserName(name);
			studentProfile.setPhone(phone);
			intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_STUDENTPROFILE, studentProfile);
		}
		startActivity(intent);
	}

	private StudentProfile getStudentProfile() {
		StudentProfile profile = new StudentProfile();
		profile.setId(account.getMemberId());
		profile.setEmail(account.getEmail());
		profile.setPassword(account.getPswd());
		profile.setFbOpenID(account.getFacebookId());
		profile.setImid(account.getImAccount());
		profile.setStatus(account.getRole());
		profile.setCreatedTime(account.getCreatedTime());
		profile.setToken(account.getToken());
		profile.setAccountType(role);
		profile.setBirth(birth);
		profile.setGender(sex);
		return profile;
	}

	private TeacherProfile getTeacherProfile() {
		TeacherProfile profile = new TeacherProfile();
		profile.setExprience(1);
		profile.setRatingGrade(5.0d);
		profile.setBookmarkedCount(0);
		profile.setStudentCount(0);
		profile.setId(account.getMemberId());
		profile.setEmail(account.getEmail());
		profile.setPassword(account.getPswd());
		profile.setFbOpenID(account.getFacebookId());
		profile.setImid(account.getImAccount());
		profile.setStatus(account.getRole());
		profile.setCreatedTime(account.getCreatedTime());
		profile.setToken(account.getToken());
		profile.setAccountType(role);
		profile.setBirth(birth);
		profile.setGender(sex);
		return profile;
	}
}
