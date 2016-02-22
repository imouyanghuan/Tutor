package com.tutor.ui.activity;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hk.tutor.R;
import com.tutor.TutorApplication;
import com.tutor.model.Account;
import com.tutor.model.EditProfileResult;
import com.tutor.model.Notification;
import com.tutor.model.UpdateNotification;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.CheckTokenUtils;
import com.tutor.util.HttpHelper;
import com.tutor.util.JsonUtil;
import com.tutor.util.ObjectHttpResponseHandler;
import com.tutor.util.StringUtils;

/**
 * 邀请詳情界面
 * 
 * @author jerry.yao
 * 
 *         2015-10-27
 */
public class InvitationDetailActivity extends BaseActivity implements OnClickListener {

	private Notification notificaiton;
	private Button btnAccept;
	private Button btnReject;
	/** 證件號,電話號碼 ,地址 */
	private EditText phoneEditText;
	// private EditText hKidEditText;
	// private EditText myAddressEditText;
	int curStatus = -1;
	private String curHKID;
	private String phone;

	// private String myAddress;
	// private String replaceId;
	// private String hkId;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_invitation_detail);
		initData();
	}

	private void initData() {
		Intent intent = getIntent();
		if (intent == null) {
			return;
		}
		notificaiton = (Notification) intent.getSerializableExtra(Constants.IntentExtra.INTENT_EXTRA_NOTIFICATION);
		if (notificaiton == null) {
			return;
		}
		initView();
	}

	@Override
	protected void initView() {
		TitleBar bar = getView(R.id.title_bar);
		bar.setTitle(R.string.label_invitation_detail);
		bar.initBack(this);
		// view 如果之前有填寫則自動填寫上去
		// hKidEditText = getView(R.id.ac_fill_personal_info_et_hkid);
		// hkId = TutorApplication.getHKID();
		// replaceId = "";
		// if (!TextUtils.isEmpty(hkId)) {
		// if (hkId.length() > 4) {
		// replaceId = hkId.replace(hkId.substring(4), "****");
		// hKidEditText.setText(replaceId);
		// hKidEditText.setSelection(replaceId.length());
		// } else {
		// hKidEditText.setText(hkId);
		// hKidEditText.setSelection(hkId.length());
		// }
		// } else {
		// hKidEditText.setText("");
		// }
		phoneEditText = getView(R.id.ac_fill_personal_info_et_phone);
		String savedNum = TutorApplication.getPhoneNum();
		if (!TextUtils.isEmpty(savedNum)) {
			phoneEditText.setText(savedNum);
			phoneEditText.setSelection(savedNum.length());
		} else {
			phoneEditText.setText("");
		}
		// myAddressEditText = getView(R.id.ac_fill_personal_info_et_address);
		// myAddressEditText.setText(TutorApplication.getResidentialAddress());
		// TextView hKidRequired = getView(R.id.ac_fill_personal_tv_hkid);
		// if (Constants.General.ROLE_TUTOR != TutorApplication.getRole()) {
		// hKidRequired.setVisibility(View.INVISIBLE);
		// }
		TextView tvInviteGuy = getView(R.id.tv_invite_guys);
		tvInviteGuy.setText(!TextUtils.isEmpty(notificaiton.getUserName()) ? notificaiton.getUserName() : notificaiton.getNickName());
		TextView tvCourse = getView(R.id.tv_course);
		tvCourse.setText(notificaiton.getDisplayCourses());
		// TextView tvFrequent = getView(R.id.tv_frequent);
		// tvFrequent.setText(notificaiton.getFrequent());
		TextView tvPrice = getView(R.id.tv_price);
		tvPrice.setText(notificaiton.getPricePerHour());
		// TextView tvAddress = getView(R.id.tv_address);
		// tvAddress.setText(notificaiton.getAddress());
		TextView tvTime = getView(R.id.tv_time);
		String nofityTime = notificaiton.getCreatedTime();
		if (nofityTime.length() > 11) {
			tvTime.setText(nofityTime.substring(0, 11));
		} else {
			tvTime.setText(nofityTime);
		}
		// accept
		btnAccept = getView(R.id.btn_accept);
		btnAccept.setOnClickListener(this);
		btnAccept.setVisibility(View.VISIBLE);
		// reject
		btnReject = getView(R.id.btn_reject);
		btnReject.setOnClickListener(this);
		btnReject.setVisibility(View.VISIBLE);
		int status = notificaiton.getStatus();
		if (status == Constants.General.ACCEPT || status == Constants.General.REJECT) {
			btnAccept.setVisibility(View.GONE);
			btnReject.setVisibility(View.GONE);
			// hKidEditText.setEnabled(false);
			phoneEditText.setEnabled(false);
			// myAddressEditText.setEnabled(false);
		}
	}

	@Override
	public void onClick(View v) {
		// 重新保存到数据库
		Account account = TutorApplication.getAccountDao().load("1");
		UpdateNotification notify = new UpdateNotification();
		notify.setId(notificaiton.getId());
		switch (v.getId()) {
			case R.id.btn_accept:
				curStatus = Constants.General.ACCEPT;
				// hkid 教师才有
				// curHKID = hKidEditText.getEditableText().toString().trim();
				// if (Constants.General.ROLE_TUTOR ==
				// TutorApplication.getRole()) {
				// if (TextUtils.isEmpty(curHKID)) {
				// toast(R.string.toast_hkid_isEmpty);
				// hKidEditText.requestFocus();
				// return;
				// }
				// if (!replaceId.equalsIgnoreCase(curHKID)) { // 修改过
				// if (!StringUtils.isHKID(curHKID)) {
				// toast(R.string.toast_hkid_error);
				// hKidEditText.requestFocus();
				// return;
				// }
				// notify.setHkidNumber(curHKID);
				// account.setHkidNumber(curHKID);
				// } else {
				// // 没有修改
				// notify.setHkidNumber(hkId);
				// account.setHkidNumber(hkId);
				// }
				// } else {
				// if (!TextUtils.isEmpty(curHKID)) {
				// if (!StringUtils.isHKID(curHKID)) {
				// toast(R.string.toast_hkid_error);
				// hKidEditText.requestFocus();
				// return;
				// }
				// notify.setHkidNumber(curHKID);
				// account.setHkidNumber(curHKID);
				// }
				// }
				// 电话号码
				phone = phoneEditText.getEditableText().toString().trim();
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
				// myAddress =
				// myAddressEditText.getEditableText().toString().trim();
				// if (TextUtils.isEmpty(myAddress)) {
				// toast(R.string.toast_address_isEmpty);
				// myAddressEditText.requestFocus();
				// return;
				// }
				notify.setHkidNumber(curHKID);
				notify.setPhone(phone);
				// notify.setResidentialAddress(myAddress);
				notify.setStatus(curStatus);
				acceptOrReject(notify);
				account.setPhone(phone);
				account.setHkidNumber(curHKID);
				// account.setResidentialAddress(myAddress);
				TutorApplication.getAccountDao().insertOrReplace(account);
				break;
			case R.id.btn_reject:
				curStatus = Constants.General.REJECT;
				notify.setHkidNumber("");
				notify.setPhone("");
				notify.setResidentialAddress("");
				notify.setStatus(curStatus);
				acceptOrReject(notify);
				break;
			default:
				break;
		}
	}

	/**
	 * 接受或者拒绝邀请
	 * 
	 * @param status
	 *            处理状态： 1接受 2拒绝
	 * @param notificationId
	 *            消息id
	 */
	public void acceptOrReject(final UpdateNotification notify) {
		String body = JsonUtil.parseObject2Str(notify);
		StringEntity entity = null;
		try {
			entity = new StringEntity(body, HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		HttpHelper.getHelper().put(ApiUrl.NOTIFICATION_UPDATE, TutorApplication.getHeaders(), entity, new ObjectHttpResponseHandler<EditProfileResult>(EditProfileResult.class) {

			@Override
			public void onFailure(int status, String message) {
				if (0 == status) {
					acceptOrReject(notify);
					return;
				}
				CheckTokenUtils.checkToken(status);
			}

			@Override
			public void onSuccess(EditProfileResult result) {
				CheckTokenUtils.checkToken(result);
				if (result.getStatusCode() == 200) {
					Intent intent = new Intent();
					intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_NOTIFICATION_ID, notificaiton.getId());
					intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_NOTIFICATION_STATUS, curStatus);
					setResult(Constants.RequestResultCode.NOTIFICATION_STATUS, intent);
					btnAccept.setEnabled(false);
					btnReject.setEnabled(false);
					if (curStatus == Constants.General.ACCEPT) {
						toast(R.string.label_accepted);
					} else {
						toast(R.string.label_rejected);
					}
				}
			}
		});
	}
}
