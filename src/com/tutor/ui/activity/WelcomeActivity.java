package com.tutor.ui.activity;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import cn.jpush.android.api.InstrumentedActivity;
import cn.jpush.android.api.JPushInterface;

import com.hk.tutor.R;
import com.loopj.android.http.RequestParams;
import com.mssky.mobile.helper.SharePrefUtil;
import com.tutor.TutorApplication;
import com.tutor.model.EditProfileResult;
import com.tutor.model.Log;
import com.tutor.model.VersionUpDate;
import com.tutor.model.VersionUpDateResult;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.util.HttpHelper;
import com.tutor.util.JsonUtil;
import com.tutor.util.LogUtils;
import com.tutor.util.ObjectHttpResponseHandler;
import com.tutor.util.ToastUtil;

/**
 * 歡迎界面
 * 
 * @author bruce.chen
 * 
 */
public class WelcomeActivity extends InstrumentedActivity {

	private VersionUpDate versionUpDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		Intent intent = getIntent();
		if (intent != null) {
			Uri uri = intent.getData();
			if (uri != null) {
				String URI = uri.toString();
				int lastIndex = URI.lastIndexOf("/");
				String blogId = URI.substring(lastIndex + 1);
				android.util.Log.e("Tutor", "============== 来自Facebook WelcomeActivity, blogId = " + blogId);
				if(!TextUtils.isEmpty(blogId)){
					SharePrefUtil.saveString(WelcomeActivity.this, Constants.General.BLOG_ID, blogId);
				}
			}
		}
		// 檢查版本更新
		checkVersion();
		// 发送错误日志
		sendLog();
		handler.sendEmptyMessageDelayed(0, Constants.General.WELCOME_DELAY);
		JPushInterface.init(this);
	}

	private void sendLog() {
		Log log = TutorApplication.getLogDao().load(1l);
		if (log == null) {
			return;
		}
		String body = JsonUtil.parseObject2Str(log);
		StringEntity entity = null;
		try {
			entity = new StringEntity(body, HTTP.UTF_8);
			HttpHelper.getHelper().post(ApiUrl.LOG, null, entity, new ObjectHttpResponseHandler<EditProfileResult>(EditProfileResult.class) {

				@Override
				public void onFailure(int status, String message) {
					LogUtils.d(status + "   " + message);
				}

				@Override
				public void onSuccess(EditProfileResult t) {
					LogUtils.d(t.toString());
					TutorApplication.getLogDao().deleteByKey(1l);
				}
			});
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private void checkVersion() {
		if (!HttpHelper.isNetworkConnected(this)) {
			ToastUtil.showToastLong(this, R.string.toast_netwrok_disconnected);
			return;
		}
		HttpHelper.getHelper().get(ApiUrl.UPDATEVERSION, TutorApplication.getHeaders(), new RequestParams(), new ObjectHttpResponseHandler<VersionUpDateResult>(VersionUpDateResult.class) {

			@Override
			public void onFailure(int status, String message) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(VersionUpDateResult t) {
				versionUpDate = t.getResult();
			}
		});
	}

	public String getVersionName(Context activity) {
		String pName = activity.getPackageName();
		String versionName = "";
		try {
			PackageInfo pinfo = activity.getPackageManager().getPackageInfo(pName, PackageManager.GET_CONFIGURATIONS);
			versionName = pinfo.versionName;
		} catch (NameNotFoundException e) {}
		return versionName;
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			//
			if (null != versionUpDate && !TextUtils.isEmpty(versionUpDate.getVersion())) {
				if (!versionUpDate.getVersion().equals(getVersionName(WelcomeActivity.this)) && versionUpDate.isForceUpdate()) {
					// 新版本強制更新
					AlertDialog.Builder builder = new AlertDialog.Builder(WelcomeActivity.this);
					builder.setCancelable(false);
					builder.setTitle(R.string.tips);
					builder.setMessage(versionUpDate.getDescription());
					builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(versionUpDate.getUrl()));
							startActivity(intent);
							WelcomeActivity.this.finish();
						}
					});
					AlertDialog dialog = builder.create();
					dialog.show();
					return;
				}
			}
			Intent intent = new Intent();
			// 是否登錄
			boolean isLogin = (Boolean) TutorApplication.settingManager.readSetting(Constants.SharedPreferences.SP_ISLOGIN, false);
			if (isLogin) {
				int role = (Integer) TutorApplication.settingManager.readSetting(Constants.SharedPreferences.SP_ROLE, -1);
				if (Constants.General.ROLE_STUDENT == role) {
					// 學生
					intent.setClass(WelcomeActivity.this, StudentMainActivity.class);
				} else if (Constants.General.ROLE_TUTOR == role) {
					// 老師
					intent.setClass(WelcomeActivity.this, TeacherMainActivity.class);
				} else if (Constants.General.ROLE_TUITION_SCHOOL == role) {
					intent.setClass(WelcomeActivity.this, TuitionCentreActivity.class);
				}
			} else {
				// 未登錄
				intent.setClass(WelcomeActivity.this, ChoiceRoleActivity.class);
			}
			startActivity(intent);
			WelcomeActivity.this.finish();
		};
	};

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		overridePendingTransition(R.anim.start_activity_in, R.anim.start_activity_out);
	};

	/**
	 * 禁用返回鍵
	 */
	public void onBackPressed() {};
}
