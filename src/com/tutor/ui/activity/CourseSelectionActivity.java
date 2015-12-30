package com.tutor.ui.activity;

import java.net.HttpURLConnection;
import java.util.ArrayList;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;

import com.hk.tutor.R;
import com.loopj.android.http.RequestParams;
import com.tutor.TutorApplication;
import com.tutor.adapter.AbroadConfigAdapter;
import com.tutor.adapter.AdviserAdapter;
import com.tutor.im.ContactManager;
import com.tutor.im.XMPPConnectionManager;
import com.tutor.model.AbroadConfig;
import com.tutor.model.AbroadConfigListResult;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.CheckTokenUtils;
import com.tutor.util.HttpHelper;
import com.tutor.util.ObjectHttpResponseHandler;
import com.tutor.util.ScreenUtil;

/**
 * Course Selection
 * 
 * @author jerry.yao
 * 
 *         2015-10-19
 */
public class CourseSelectionActivity extends BaseActivity implements OnClickListener {

	private Spinner spGrade;
	private Spinner spCountry;
	private CheckBox cbIsPrivacy;
	private ArrayList<AbroadConfig> grades = new ArrayList<AbroadConfig>();
	private ArrayList<AbroadConfig> countrys = new ArrayList<AbroadConfig>();
	private ArrayList<AbroadConfig> imIds = new ArrayList<AbroadConfig>();
	private AbroadConfigAdapter gradeAdapter;
	private AbroadConfigAdapter countryAdapter;
	private int curGradeValue = -1;
	private String curGradeString = "";
	private int curCountryValue = -1;
	private boolean isPrivacy = false;
	private Button btnChat;
	private String curCountryName;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_course_selection);
		initView();
		initData();
	}

	private void initData() {
		getGrade();
		getCountrys();
		getIMIds();
	}

	/**
	 * 获取年级
	 */
	private void getGrade() {
		if (!HttpHelper.isNetworkConnected(this)) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		RequestParams params = new RequestParams();
		HttpHelper.getHelper().get(ApiUrl.STUDY_ABROAD_GRADES, TutorApplication.getHeaders(), params, new ObjectHttpResponseHandler<AbroadConfigListResult>(AbroadConfigListResult.class) {

			@Override
			public void onFailure(int status, String message) {
				if (0 == status) {
					return;
				}
				dismissDialog();
				if (CheckTokenUtils.checkToken(status)) {
					return;
				}
				toast(R.string.toast_server_error);
			}

			@Override
			public void onSuccess(AbroadConfigListResult result) {
				dismissDialog();
				if (result.getStatusCode() == HttpURLConnection.HTTP_OK) {
					// TODO
					grades.addAll(result.getResult());
					gradeAdapter.notifyDataSetChanged();
				} else {
					toast(result.getMessage());
				}
			}
		});
	}

	/**
	 * 获取国家
	 */
	private void getCountrys() {
		if (!HttpHelper.isNetworkConnected(this)) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		RequestParams params = new RequestParams();
		HttpHelper.getHelper().get(ApiUrl.STUDY_ABROAD_STATES, TutorApplication.getHeaders(), params, new ObjectHttpResponseHandler<AbroadConfigListResult>(AbroadConfigListResult.class) {

			@Override
			public void onFailure(int status, String message) {
				if (0 == status) {
					return;
				}
				dismissDialog();
				if (CheckTokenUtils.checkToken(status)) {
					return;
				}
				toast(R.string.toast_server_error);
			}

			@Override
			public void onSuccess(AbroadConfigListResult result) {
				dismissDialog();
				if (result.getStatusCode() == HttpURLConnection.HTTP_OK) {
					// TODO
					countrys.addAll(result.getResult());
					countryAdapter.notifyDataSetChanged();
				} else {
					toast(result.getMessage());
				}
			}
		});
	}

	/**
	 * 获取所有的客服
	 */
	private void getIMIds() {
		if (!HttpHelper.isNetworkConnected(this)) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		RequestParams params = new RequestParams();
		HttpHelper.getHelper().get(ApiUrl.STUDY_ABROAD_IM_IDS, TutorApplication.getHeaders(), params, new ObjectHttpResponseHandler<AbroadConfigListResult>(AbroadConfigListResult.class) {

			@Override
			public void onFailure(int status, String message) {
				if (0 == status) {
					return;
				}
				dismissDialog();
				if (CheckTokenUtils.checkToken(status)) {
					return;
				}
				toast(R.string.toast_server_error);
			}

			@Override
			public void onSuccess(AbroadConfigListResult result) {
				dismissDialog();
				if (result.getStatusCode() == HttpURLConnection.HTTP_OK) {
					imIds.addAll(result.getResult());
					if (imIds != null && imIds.size() > 0) {
						for (int i = 0; i < imIds.size(); i++) {
							imIds.get(i).setAlias("Adviser" + (i + 1));
						}
					}
				} else {
					toast(result.getMessage());
				}
			}
		});
	}

	@Override
	protected void initView() {
		initTitleBar();
		// 年级
		spGrade = getView(R.id.sp_grade);
		gradeAdapter = new AbroadConfigAdapter(this, grades);
		spGrade.setAdapter(gradeAdapter);
		spGrade.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				AbroadConfig config = gradeAdapter.getItem(position);
				curGradeValue = config.getValue();
				curGradeString = config.getText();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});
		// 国家
		spCountry = getView(R.id.sp_country);
		countryAdapter = new AbroadConfigAdapter(this, countrys);
		spCountry.setAdapter(countryAdapter);
		spCountry.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				AbroadConfig config = countryAdapter.getItem(position);
				curCountryValue = config.getValue();
				curCountryName = config.getText();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});
		// is privacy
		cbIsPrivacy = getView(R.id.cb_is_privacy);
		cbIsPrivacy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isPrivacy = !isPrivacy;
			}
		});
		btnChat = getView(R.id.btn_chat_with_adviser);
		btnChat.setOnClickListener(this);
		boolean isLogin = (Boolean) TutorApplication.settingManager.readSetting(Constants.SharedPreferences.SP_ISLOGIN, false);
		if (!isLogin) {
			btnChat.setVisibility(View.GONE);
		}
		getView(R.id.btn_confirm).setOnClickListener(this);
	}

	private void initTitleBar() {
		TitleBar bar = getView(R.id.title_bar);
		bar.initBack(this);
		bar.setTitle(R.string.label_course_selection);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_confirm:
				Intent intent = new Intent(this, CourseSelectionResultActivity.class);
				intent.putExtra(Constants.General.GRADE_VALUE, curGradeValue);
				intent.putExtra(Constants.General.GRADE_STRING, curGradeString);
				intent.putExtra(Constants.General.COUNTRY_VALUE, curCountryValue);
				intent.putExtra(Constants.General.COUNTRY_NAME, curCountryName);
				intent.putExtra(Constants.General.IS_PRIVACY, isPrivacy);
				startActivity(intent);
				break;
			case R.id.btn_chat_with_adviser:
				showPopupWindow(btnChat);
				break;
		}
	}

	private void showPopupWindow(Button btnChat) {
		View view = View.inflate(CourseSelectionActivity.this, R.layout.layout_adviser_list, null);
		ListView lvAdviser = (ListView) view.findViewById(R.id.lv_adviser);
		final AdviserAdapter adapter = new AdviserAdapter(CourseSelectionActivity.this, imIds);
		lvAdviser.setAdapter(adapter);
		int itemCount = adapter.getCount();
		final PopupWindow popupWindow = new PopupWindow(view);
		popupWindow.setFocusable(true);
		int itemWidth = ScreenUtil.getSW(this) - ScreenUtil.dip2Px(CourseSelectionActivity.this, 40) * 2;
		int itemHeight = ScreenUtil.dip2Px(CourseSelectionActivity.this, 40) * itemCount;
		// 设置SelectPicPopupWindow弹出窗体的宽
		popupWindow.setWidth(itemWidth);
		// 设置SelectPicPopupWindow弹出窗体的高
		popupWindow.setHeight(itemHeight);
		// 点击“返回Back”也能使其消失
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new ColorDrawable(R.color.white));
		popupWindow.showAsDropDown(btnChat, 0, 0);
		lvAdviser.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				AbroadConfig config = adapter.getItem(position);
				String imId = config.getText();
				String adviserName = config.getAlias();
				if (!TextUtils.isEmpty(imId)) {
					boolean isFriend = ContactManager.getManager().addFriend(CourseSelectionActivity.this, imId, imId);
					if (isFriend) {
						Intent intent = new Intent(CourseSelectionActivity.this, ChatActivity.class);
						intent.putExtra(Constants.General.IS_FROM_COURSE_SELECTION, true);
						intent.putExtra(Constants.General.GRADE_VALUE, curGradeValue);
						intent.putExtra(Constants.IntentExtra.INTENT_EXTRA_MESSAGE_TO, imId + "@" + XMPPConnectionManager.getManager().getServiceName());
						intent.putExtra(Constants.General.NICKNAME, adviserName);
						startActivity(intent);
					}
				}
				if (popupWindow != null) {
					popupWindow.dismiss();
				}
			}
		});
	}
}
