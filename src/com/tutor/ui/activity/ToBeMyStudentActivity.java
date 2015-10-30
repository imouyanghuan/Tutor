package com.tutor.ui.activity;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.hk.tutor.R;
import com.loopj.android.http.RequestParams;
import com.mssky.mobile.ui.view.CustomListView;
import com.tutor.TutorApplication;
import com.tutor.adapter.CurrencysAdapter;
import com.tutor.adapter.TimeSlotAdapter;
import com.tutor.model.Account;
import com.tutor.model.CourseItem2;
import com.tutor.model.Currency;
import com.tutor.model.CurrencyListResult;
import com.tutor.model.EditProfileResult;
import com.tutor.model.Notification;
import com.tutor.model.Timeslot;
import com.tutor.model.UserInfo;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.dialog.TimeSlotDialog;
import com.tutor.ui.dialog.TimeSlotDialog.onTimeSelectedCallBack;
import com.tutor.ui.dialog.WeekDialog;
import com.tutor.ui.dialog.WeekDialog.OnWeekSelectedCallback;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.HttpHelper;
import com.tutor.util.JsonUtil;
import com.tutor.util.ObjectHttpResponseHandler;
import com.tutor.util.StringUtils;

public class ToBeMyStudentActivity extends BaseActivity implements OnItemSelectedListener, OnClickListener, onTimeSelectedCallBack, OnWeekSelectedCallback {

	private UserInfo userInfo;
	private int priceType;
	private CurrencysAdapter adapter;
	// view
	private TextView courseTextView;
	private EditText nickEditText, frequencyEditText, priceEditText, addressEditText;
	private Spinner priceTypeSpinner;
	private int accountType;
	private int[] courseValue;
	/** 时间段列表 */
	private CustomListView listView;
	private TextView weekTextView, startTimeTextView, endTimeTextView;
	/** 保存時間 */
	private Button saveTime;
	private ArrayList<Timeslot> timeslots;
	// 是否编辑开始时间
	private boolean isStrat;
	private Timeslot timeslot = null;
	private TimeSlotAdapter timeSlotAdapter;
	/** 證件號,電話號碼 ,地址 */
	private EditText hKidEditText, phoneEditText, myAddressEditText;
	private TextView hKidRequired;
	private String hkId;
	private String replaceId;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_to_be_my_student);
		userInfo = (UserInfo) getIntent().getSerializableExtra(Constants.IntentExtra.INTENT_EXTRA_USER_INFO);
		if (null == userInfo) {
			finish();
			return;
		}
		accountType = userInfo.getAccountType();
		initView();
		// 获取币种
		showDialogRes(R.string.loading);
		getCurrencys();
	}

	private void getCurrencys() {
		HttpHelper.get(this, ApiUrl.CURRENCYS, TutorApplication.getHeaders(), new RequestParams(), new ObjectHttpResponseHandler<CurrencyListResult>(CurrencyListResult.class) {

			@Override
			public void onFailure(int status, String message) {
				if (0 == status) {
					getCurrencys();
					return;
				}
				dismissDialog();
				toast(R.string.toast_server_error);
			}

			@Override
			public void onSuccess(CurrencyListResult t) {
				dismissDialog();
				if (HttpURLConnection.HTTP_OK == t.getStatusCode()) {
					ArrayList<Currency> currencys = t.getResult();
					if (currencys != null && currencys.size() > 0) {
						priceType = currencys.get(0).getValue();
					}
					adapter = new CurrencysAdapter(ToBeMyStudentActivity.this, t.getResult());
					priceTypeSpinner.setAdapter(adapter);
				} else {
					toast(t.getMessage());
				}
			}
		});
	}

	@Override
	protected void initView() {
		initTitleBar();
		hKidRequired = getView(R.id.ac_fill_personal_tv_hkid);
		hKidEditText = getView(R.id.ac_fill_personal_info_et_hkid);
		hkId = TutorApplication.getHKID();
		replaceId = "";
		if (!TextUtils.isEmpty(hkId)) {
			if (hkId.length() > 4) {
				replaceId = hkId.replace(hkId.substring(4), "****");
				hKidEditText.setText(replaceId);
				hKidEditText.setSelection(replaceId.length());
			} else {
				hKidEditText.setText(hkId);
				hKidEditText.setSelection(hkId.length());
			}
		} else {
			hKidEditText.setText("");
		}
		phoneEditText = getView(R.id.ac_fill_personal_info_et_phone);
		phoneEditText.setText(TutorApplication.getPhoneNum());
		myAddressEditText = getView(R.id.ac_fill_personal_info_et_address);
		myAddressEditText.setText(TutorApplication.getResidentialAddress());
		if (Constants.General.ROLE_TUTOR != TutorApplication.getRole()) {
			hKidRequired.setVisibility(View.INVISIBLE);
		}
		nickEditText = getView(R.id.et_nick);
		courseTextView = getView(R.id.et_course);
		frequencyEditText = getView(R.id.et_frequency);
		priceEditText = getView(R.id.et_price);
		addressEditText = getView(R.id.et_address);
		priceTypeSpinner = getView(R.id.spinner_priceType);
		priceTypeSpinner.setOnItemSelectedListener(this);
		String username = userInfo.getNickName();
		if (TextUtils.isEmpty(username)) {
			username = userInfo.getUserName();
		}
		nickEditText.setText(username);
		if (!TextUtils.isEmpty(username)) {
			nickEditText.setSelection(username.length());
		}
		courseTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(ToBeMyStudentActivity.this, SureCourseActivity.class);
				intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_ROLE, userInfo.getAccountType());
				startActivityForResult(intent, Constants.RequestResultCode.SURE_COURSES);
			}
		});
		// time slot
		weekTextView = getView(R.id.ac_fill_personal_time_tv_week);
		weekTextView.setOnClickListener(this);
		startTimeTextView = getView(R.id.ac_fill_personal_time_tv_start);
		startTimeTextView.setOnClickListener(this);
		endTimeTextView = getView(R.id.ac_fill_personal_time_tv_end);
		endTimeTextView.setOnClickListener(this);
		saveTime = getView(R.id.ac_fill_personal_info_btn_save_timeslot);
		saveTime.setOnClickListener(this);
		// 时间段列表
		listView = getView(R.id.ac_fill_personal_info_timeslot_lv);
		timeSlotAdapter = new TimeSlotAdapter(this, timeslots, true);
		listView.setAdapter(timeSlotAdapter);
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if (arg0 == Constants.RequestResultCode.SURE_COURSES && arg1 == RESULT_OK && null != arg2) {
			setData(arg2);
		}
	}

	private void initTitleBar() {
		TitleBar bar = getView(R.id.title_bar);
		if (accountType == Constants.General.ROLE_STUDENT) {
			bar.setTitle(R.string.to_be_my_student);
		} else {
			bar.setTitle(R.string.to_be_my_tutor);
		}
		bar.initBack(this);
		bar.setRightText(R.string.btn_save, new OnClickListener() {

			@Override
			public void onClick(View v) {
				onSave();
			}
		});
	}

	private void setData(Intent arg2) {
		@SuppressWarnings("unchecked")
		ArrayList<CourseItem2> data = (ArrayList<CourseItem2>) arg2.getSerializableExtra(Constants.IntentExtra.INTENT_EXTRA_COURSESLIST);
		if (data == null || data.size() == 0) {
			courseValue = null;
			courseTextView.setText("");
		} else {
			courseValue = new int[data.size()];
			courseTextView.setText(getCourse(data));
		}
	}

	private String getCourse(ArrayList<CourseItem2> courses) {
		StringBuffer sb = new StringBuffer();
		if (null != courses && courses.size() > 0) {
			for (int i = 0; i < courses.size(); i++) {
				CourseItem2 item2 = courses.get(i);
				courseValue[i] = item2.getValue();
				sb.append(item2.getType() + "-" + item2.getSubType() + "-" + item2.getCourseName() + ",");
			}
		}
		// 去掉最后一个逗号
		if (sb.length() > 0) {
			sb.delete(sb.length() - 1, sb.length());
		}
		return sb.toString();
	}

	// 发送邀请
	private void onSave() {
		Notification notification = new Notification();
		Account account = TutorApplication.getAccountDao().load("1");
		// hkid 教师才有
		String curHKID = hKidEditText.getEditableText().toString().trim();
		if (Constants.General.ROLE_TUTOR == TutorApplication.getRole()) {
			if (TextUtils.isEmpty(curHKID)) {
				toast(R.string.toast_hkid_isEmpty);
				hKidEditText.requestFocus();
				return;
			}
			if (!replaceId.equalsIgnoreCase(curHKID)) { // 修改过
				if (!StringUtils.isHKID(curHKID)) {
					toast(R.string.toast_hkid_error);
					hKidEditText.requestFocus();
					return;
				}
				notification.setHkidNumber(curHKID);
				account.setHkidNumber(curHKID);
			} else {
				// 没有修改
				notification.setHkidNumber(hkId);
				account.setHkidNumber(hkId);
			}
		} else {
			if (!TextUtils.isEmpty(curHKID)) {
				if (!StringUtils.isHKID(curHKID)) {
					toast(R.string.toast_hkid_error);
					hKidEditText.requestFocus();
					return;
				}
				notification.setHkidNumber(curHKID);
				account.setHkidNumber(curHKID);
			}
		}
		// 电话号码
		String phone = phoneEditText.getEditableText().toString().trim();
		if (TextUtils.isEmpty(phone)) {
			toast(R.string.toast_phone_isEmpty);
			phoneEditText.requestFocus();
			return;
		}
		if (!StringUtils.isHKPhone(phone)) {
			toast(R.string.toast_phone_error);
			phoneEditText.requestFocus();
			return;
		}
		// 地址
		String myAddress = myAddressEditText.getEditableText().toString().trim();
		if (TextUtils.isEmpty(myAddress)) {
			toast(R.string.toast_address_isEmpty);
			myAddressEditText.requestFocus();
			return;
		}
		// 课程是必选
		if (null == courseValue) {
			toast(R.string.toast_not_select_cours);
			return;
		}
		// 时间段是必填
		if (null == timeslots) {
			toast(R.string.toast_no_timeslot);
			return;
		}
		// 价格是必填项
		String price = priceEditText.getEditableText().toString().trim();
		if (TextUtils.isEmpty(price)) {
			toast(R.string.toast_price_empty);
			priceEditText.requestFocus();
			return;
		}
		notification.setPhone(phone);
		notification.setResidentialAddress(myAddress);
		notification.setTimeslots(timeslots);
		notification.setSource(TutorApplication.getMemberId());
		notification.setDestination(userInfo.getId());
		if (accountType == Constants.General.ROLE_STUDENT) {
			notification.setOrientation(Notification.ToBeMyStudent);
		} else {
			notification.setOrientation(Notification.ToBeMyTutor);
		}
		if (accountType == Constants.General.ROLE_STUDENT) {
			notification.setContent(getString(R.string.to_be_my_student2));
		} else {
			notification.setContent(getString(R.string.to_be_my_tutor2));
		}
		notification.setCoursesValues(courseValue);
		notification.setPricePerHour(price);
		notification.setPriceCurrency(priceType);
		String frequency = frequencyEditText.getEditableText().toString().trim();
		if (null == frequency) {
			frequency = "";
		}
		notification.setFrequent(frequency);
		String address = addressEditText.getEditableText().toString().trim();
		if (null == address) {
			address = "";
		}
		notification.setAddress(address);
		String body = JsonUtil.parseObject2Str(notification);
		showDialogRes(R.string.loading);
		send(body);
		// 重新保存到数据库
		account.setPhone(phone);
		account.setResidentialAddress(myAddress);
		TutorApplication.getAccountDao().insertOrReplace(account);
	}

	private void send(final String body) {
		try {
			StringEntity entity = new StringEntity(body, HTTP.UTF_8);
			String url = "";
			if (accountType == Constants.General.ROLE_STUDENT) {
				url = ApiUrl.TO_BE_MY_STUDENT;
			} else {
				url = ApiUrl.TO_BE_MY_TUTOR;
			}
			HttpHelper.post(this, url, TutorApplication.getHeaders(), entity, new ObjectHttpResponseHandler<EditProfileResult>(EditProfileResult.class) {

				@Override
				public void onFailure(int status, String message) {
					if (0 == status) {
						send(body);
						return;
					}
					dismissDialog();
					toast(R.string.toast_server_error);
				}

				@Override
				public void onSuccess(EditProfileResult t) {
					dismissDialog();
					if (HttpURLConnection.HTTP_OK == t.getStatusCode() && t.getResult()) {
						toast(R.string.success);
						finish();
					} else {
						toast(t.getMessage());
					}
				}
			});
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (adapter != null) {
			Currency currency = adapter.getItem(arg2);
			if (null != currency) {
				priceType = currency.getValue();
			}
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.ac_fill_personal_info_btn_save_timeslot:
				if (null != timeslot) {
					if (null == timeslots) {
						timeslots = new ArrayList<Timeslot>();
					}
					if (null != userInfo) {
						timeslot.setMemberId(userInfo.getId());
					}
					if (timeslots.size() > 0) {
						// 检查冲突时间
						for (Timeslot item : timeslots) {
							if (item.isRepeat(timeslot)) {
								// 有冲突.提示
								toast(R.string.toast_timeslot_has_a_conflict);
								return;
							}
						}
					}
					timeslots.add(timeslot);
					timeSlotAdapter.refresh(timeslots);
					saveTime.setEnabled(false);
					weekTextView.setText("");
					startTimeTextView.setText("");
					endTimeTextView.setText("");
					timeslot = null;
				}
				break;
			case R.id.ac_fill_personal_time_tv_week:
				WeekDialog weekDialog;
				if (null != timeslot) {
					weekDialog = new WeekDialog(this, timeslot.getDayOfWeek(), this);
				} else {
					weekDialog = new WeekDialog(this, 0, this);
				}
				weekDialog.show();
				break;
			case R.id.ac_fill_personal_time_tv_start:
				isStrat = true;
				TimeSlotDialog timeSlotDialog;
				if (null != timeslot) {
					timeSlotDialog = new TimeSlotDialog(this, timeslot.getEndHour(), timeslot.getEndMinute(), this);
				} else {
					timeSlotDialog = new TimeSlotDialog(this, 0, 0, this);
				}
				timeSlotDialog.show();
				break;
			case R.id.ac_fill_personal_time_tv_end:
				isStrat = false;
				TimeSlotDialog timeSlotDialog1;
				if (null != timeslot) {
					timeSlotDialog1 = new TimeSlotDialog(this, timeslot.getStartHour(), timeslot.getStartMinute(), this);
				} else {
					timeSlotDialog1 = new TimeSlotDialog(this, 0, 0, this);
				}
				timeSlotDialog1.show();
				break;
			default:
				break;
		}
	}

	@Override
	public void onWeekSelected(int index, String value) {
		if (null == timeslot) {
			timeslot = new Timeslot();
		}
		timeslot.setDayOfWeek(index);
		weekTextView.setText(value);
		check();
	}

	@Override
	public int onTimeSelected(String time, int hour, int minute) {
		if (null == timeslot) {
			timeslot = new Timeslot();
		}
		if (isStrat) {
			// 在编辑开始时间的时候已经编辑好了结束时间,需要检查开始时间是否在结束时间之前
			if (0 != timeslot.getEndHour() || 0 != timeslot.getEndMinute()) {
				if (0 != timeslot.getEndHour() && hour < timeslot.getEndHour()) {
					timeslot.setStartHour(hour);
					timeslot.setStartMinute(minute);
					startTimeTextView.setText(time);
					check();
					return 0;
				} else if (hour == timeslot.getEndHour() && minute < timeslot.getEndMinute()) {
					timeslot.setStartHour(hour);
					timeslot.setStartMinute(minute);
					startTimeTextView.setText(time);
					check();
					return 0;
				} else if (0 == timeslot.getEndHour()) {
					if (hour > 0 || hour == 0 && minute < timeslot.getEndMinute()) {
						timeslot.setStartHour(hour);
						timeslot.setStartMinute(minute);
						startTimeTextView.setText(time);
						check();
						return 0;
					}
				}
				return R.string.toast_starttime_front_endtime;
			} else {
				// 没有编辑结束时间,直接设值
				timeslot.setStartHour(hour);
				timeslot.setStartMinute(minute);
				startTimeTextView.setText(time);
				check();
			}
		} else {
			// 结束时间
			if (0 != timeslot.getStartHour() || 0 != timeslot.getStartMinute()) {
				if (0 != timeslot.getStartHour() && hour > timeslot.getStartHour()) {
					timeslot.setEndHour(hour);
					timeslot.setEndMinute(minute);
					endTimeTextView.setText(time);
					check();
					return 0;
				} else if (hour == timeslot.getStartHour() && timeslot.getStartMinute() < minute) {
					timeslot.setEndHour(hour);
					timeslot.setEndMinute(minute);
					endTimeTextView.setText(time);
					check();
					return 0;
				} else if (0 == timeslot.getStartHour()) {
					if (hour > 0 || minute > timeslot.getStartMinute()) {
						timeslot.setEndHour(hour);
						timeslot.setEndMinute(minute);
						endTimeTextView.setText(time);
						check();
						return 0;
					}
				}
				return R.string.toast_endtime_behind_starttime;
			} else {
				// 没有编辑开始时间,直接设值
				timeslot.setEndHour(hour);
				timeslot.setEndMinute(minute);
				endTimeTextView.setText(time);
				check();
			}
		}
		check();
		return 0;
	}

	private void check() {
		if (!TextUtils.isEmpty(weekTextView.getText()) && !TextUtils.isEmpty(startTimeTextView.getText()) && !TextUtils.isEmpty(endTimeTextView.getText())) {
			saveTime.setEnabled(true);
		}
	}
}
