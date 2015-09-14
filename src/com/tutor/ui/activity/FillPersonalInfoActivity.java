package com.tutor.ui.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.lidroid.xutils.exception.DbException;
import com.mssky.mobile.ui.view.CustomListView;
import com.tutor.R;
import com.tutor.TutorApplication;
import com.tutor.adapter.TimeSlotAdapter;
import com.tutor.model.Account;
import com.tutor.model.Area;
import com.tutor.model.Area1;
import com.tutor.model.AreaListResult;
import com.tutor.model.Course;
import com.tutor.model.CourseItem1;
import com.tutor.model.CourseItem2;
import com.tutor.model.CourseListResult;
import com.tutor.model.EditProfileResult;
import com.tutor.model.StudentProfile;
import com.tutor.model.TeacherProfile;
import com.tutor.model.TimeSlotListResult;
import com.tutor.model.Timeslot;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.service.TutorService;
import com.tutor.service.UserService;
import com.tutor.ui.dialog.AddTimeSlotDialog;
import com.tutor.ui.dialog.AddTimeSlotDialog.CallBack;
import com.tutor.ui.dialog.ChangeAvatarDialog;
import com.tutor.ui.view.AreaItemLayout;
import com.tutor.ui.view.CourseLayout;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.DateTimeUtil;
import com.tutor.util.ImageUtils;
import com.tutor.util.LogUtils;

/**
 * 完善資料界面
 * 
 * @author bruce.chen
 * 
 *         2015-8-24
 */
public class FillPersonalInfoActivity extends BaseActivity implements OnClickListener, CallBack {

	private int role;
	private Account account;
	// views
	/** 頭像 */
	private ImageView avatarImageView;
	/** 姓名,證件號,電話號碼 */
	private EditText nameEditText, hKidEditText, phoneEditText;
	/** 證件佈局,課程佈局 */
	private LinearLayout hkidLinearLayout, couressLinearLayout, areaLinearLayout;
	/** 教育背景 布局 */
	private FrameLayout ebFrameLayout;
	/** 性別 ,教育背景 */
	private RadioGroup sexRadioGroup, ebRadioGroup;
	/** 出生日期, 課程標題 */
	private TextView brithTextView, couressTextView;
	/** 提交,添加時間 */
	private Button submit, addTimeSlot;
	/** 时间段列表 */
	private CustomListView listView;
	private TimeSlotAdapter adapter;
	// 數據
	private ArrayList<Course> courses;
	private ArrayList<Area> areas;
	private ArrayList<Timeslot> timeslots;
	private String avatar;
	private String sex = Constants.General.MALE;
	private String birth;
	/** 教育背景 */
	private String eb;
	// 頭像對話框
	private ChangeAvatarDialog avatarDialog;
	// 添加时间对话框
	private AddTimeSlotDialog timeSlotDialog;
	// 保存拍照圖片uri
	private Uri imageUri, zoomUri;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		role = getIntent().getIntExtra(Constants.IntentExtra.INTENT_EXTRA_ROLE, -1);
		if (-1 == role) {
			throw new IllegalArgumentException("role is -1");
		}
		try {
			account = TutorApplication.dbUtils.findFirst(Account.class);
		} catch (DbException e) {
			e.printStackTrace();
		}
		if (null == account) {
			throw new IllegalArgumentException("account is null");
		}
		setContentView(R.layout.activity_fill_personal_info);
		initView();
		birth = getString(R.string.label_date);
		eb = getString(R.string.eb_1);
		timeSlotDialog = new AddTimeSlotDialog(this, this);
		new GetDataTask().execute();
	}

	@Override
	protected void initView() {
		TitleBar bar = getView(R.id.title_bar);
		bar.initBack(this);
		bar.setTitle(R.string.fill_personal_info);
		bar.setTitleTextSize(20.0f);
		submit = getView(R.id.ac_fill_personal_info_btn_submit);
		submit.setOnClickListener(this);
		if (Constants.General.ROLE_TUTOR == role) {
			submit.setText(R.string.btn_fill_info_tutor);
		} else {
			submit.setText(R.string.btn_fill_info_student);
		}
		avatarImageView = getView(R.id.ac_fill_personal_info_iv_avatar);
		avatarImageView.setOnClickListener(this);
		nameEditText = getView(R.id.ac_fill_personal_info_et_name);
		hKidEditText = getView(R.id.ac_fill_personal_info_et_hkid);
		phoneEditText = getView(R.id.ac_fill_personal_info_et_phone);
		hkidLinearLayout = getView(R.id.ac_fill_personal_info_ll_hkid);
		if (Constants.General.ROLE_TUTOR == role) {
			hkidLinearLayout.setVisibility(View.VISIBLE);
		} else {
			hkidLinearLayout.setVisibility(View.GONE);
		}
		couressLinearLayout = getView(R.id.ac_fill_personal_info_ll_couress);
		areaLinearLayout = getView(R.id.ac_fill_personal_info_ll_area);
		ebFrameLayout = getView(R.id.ac_fill_personal_info_fl_eb);
		if (Constants.General.ROLE_TUTOR == role) {
			ebFrameLayout.setVisibility(View.VISIBLE);
		} else {
			ebFrameLayout.setVisibility(View.GONE);
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
		brithTextView = getView(R.id.ac_fill_personal_info_tv_birth);
		brithTextView.setOnClickListener(this);
		couressTextView = getView(R.id.ac_fill_personal_info_tv_couress);
		if (Constants.General.ROLE_TUTOR == role) {
			couressTextView.setText(R.string.label_teaching_couress);
		} else {
			couressTextView.setText(R.string.label_select_couress);
		}
		ebRadioGroup = getView(R.id.ac_fill_personal_info_rg_eb);
		if (Constants.General.ROLE_TUTOR == role) {
			ebRadioGroup.setVisibility(View.VISIBLE);
			ebRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					switch (checkedId) {
						case R.id.ac_fill_personal_info_rb_eb1:
							eb = getString(R.string.eb_1);
							break;
						case R.id.ac_fill_personal_info_rb_eb2:
							eb = getString(R.string.eb_2);
							break;
						case R.id.ac_fill_personal_info_rb_eb3:
							eb = getString(R.string.eb_3);
							break;
						case R.id.ac_fill_personal_info_rb_eb4:
							eb = getString(R.string.eb_4);
							break;
					}
				}
			});
		} else {
			ebRadioGroup.setVisibility(View.GONE);
		}
		listView = getView(R.id.ac_fill_personal_info_timeslot_lv);
		adapter = new TimeSlotAdapter(this, timeslots, true);
		listView.setAdapter(adapter);
		addTimeSlot = getView(R.id.ac_fill_personal_info_btn_add_timeslot);
		addTimeSlot.setOnClickListener(this);
	}

	/**
	 * 顯示獲取到的數據
	 */
	private void showData() {
		if (null != courses && 0 != courses.size()) {
			CourseLayout courseLayout = new CourseLayout(this, courses);
			couressLinearLayout.addView(courseLayout);
		}
		if (null != areas && 0 != areas.size()) {
			for (Area area : areas) {
				AreaItemLayout areaItemLayout = new AreaItemLayout(this, area);
				areaLinearLayout.addView(areaItemLayout);
			}
		}
		if (null != timeslots) {
			adapter.refresh(timeslots);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.ac_fill_personal_info_btn_submit:
				// 提交按鈕
				if (TextUtils.isEmpty(avatar)) {
					toast(R.string.toast_avatar_isEmpty);
					return;
				}
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
				// 课程列表
				ArrayList<Course> choiceCourses = getChoiceCourses();
				if (!isHaveCourse) {
					toast(R.string.toast_not_select_cours);
					return;
				}
				// 地区列表
				ArrayList<Area> choiceAreas = getChoiceAreas();
				if (!isHaveArea) {
					toast(R.string.toast_not_select_area);
					return;
				}
				// 时间列表
				if (null == timeslots || 0 == timeslots.size()) {
					toast(R.string.toast_no_timeslot);
					// 弹出选择框
					timeSlotDialog.show();
					return;
				}
				if (Constants.General.ROLE_TUTOR == role) {
					TeacherProfile profile = getTeacherProfile();
					profile.setHKIDNumber(hkid);
					profile.setNickName(name);
					profile.setUserName(name);
					profile.setPhone(phone);
					profile.setCourses(choiceCourses);
					profile.setAreas(choiceAreas);
					profile.setTimeslots(timeslots);
					new TutorProfileTask(profile).execute();
				} else {
					StudentProfile profile = getStudentProfile();
					profile.setNickName(name);
					profile.setUserName(name);
					profile.setPhone(phone);
					profile.setCourses(choiceCourses);
					profile.setAreas(choiceAreas);
					profile.setTimeslots(timeslots);
					new StudentProfileTask(profile).execute();
				}
				break;
			case R.id.ac_fill_personal_info_iv_avatar:
				// 上傳頭像
				if (null == avatarDialog) {
					avatarDialog = new ChangeAvatarDialog(this);
				}
				// 初始化uri
				String fileName = DateTimeUtil.getSystemDateTime(DateTimeUtil.FORMART_YMDHMS) + Constants.General.IMAGE_END;
				imageUri = Uri.fromFile(new File(Constants.SDCard.getImageDir(), fileName));
				avatarDialog.setUri(imageUri);
				avatarDialog.show();
				break;
			case R.id.ac_fill_personal_info_btn_add_timeslot:
				timeSlotDialog.show();
				break;
		}
	}

	private StudentProfile getStudentProfile() {
		StudentProfile profile = new StudentProfile();
		profile.setId(account.getMemberId());
		profile.setEmail(account.getEmail());
		profile.setPassword(account.getPswd());
		profile.setFBOpenID(account.getFacebookId());
		profile.setIMID(account.getImAccount());
		profile.setStatus(account.getRole());
		profile.setCreatedTime(account.getCreatedTime());
		profile.setToken(account.getToken());
		profile.setAvatar(avatar);
		profile.setAccountType(role);
		profile.setBirth(DateTimeUtil.str2Date(birth, DateTimeUtil.FORMART_YMD));
		;
		profile.setGender(sex);
		return profile;
	}

	@SuppressWarnings("deprecation")
	private TeacherProfile getTeacherProfile() {
		TeacherProfile profile = new TeacherProfile();
		profile.setExprience(1);
		profile.setRatingGrade(5.0d);
		profile.setBookmarkedCount(0);
		profile.setStudentCount(0);
		profile.setEducationDegree(eb);
		profile.setId(account.getMemberId());
		profile.setEmail(account.getEmail());
		profile.setPassword(account.getPswd());
		profile.setFBOpenID(account.getFacebookId());
		profile.setIMID(account.getImAccount());
		profile.setStatus(account.getRole());
		profile.setCreatedTime(account.getCreatedTime());
		profile.setToken(account.getToken());
		profile.setAvatar(avatar);
		profile.setAccountType(role);
		profile.setBirth(new Date(birth));
		profile.setGender(sex);
		return profile;
	}

	@Override
	public void onAddTimeSlot(Timeslot timeslot) {
		if (null == timeslots) {
			timeslots = new ArrayList<Timeslot>();
		}
		timeslots.add(timeslot);
		adapter.refresh(timeslots);
	}

	private boolean isHaveArea = false;

	/**
	 * 获取选中的地区
	 * 
	 * @return
	 */
	private ArrayList<Area> getChoiceAreas() {
		ArrayList<Area> areas = new ArrayList<Area>(this.areas);
		for (int i = 0; i < areas.size(); i++) {
			boolean b = false;
			ArrayList<Area1> area1s = areas.get(i).getResult();
			for (int j = 0; j < area1s.size(); j++) {
				if (area1s.get(j).getSelected()) {
					b = true;
					isHaveArea = true;
					continue;
				}
				area1s.remove(j);
				j--;
			}
			if (!b) {
				areas.remove(i);
				i--;
			}
		}
		LogUtils.d(areas.toString());
		return areas;
	}

	private boolean isHaveCourse = false;

	/**
	 * 获取选中的课程
	 * 
	 * @return
	 */
	private ArrayList<Course> getChoiceCourses() {
		ArrayList<Course> courses = new ArrayList<Course>(this.courses);
		for (int i = 0; i < courses.size(); i++) {
			boolean b = false;
			ArrayList<CourseItem1> item1s = courses.get(i).getResult();
			for (int j = 0; j < item1s.size(); j++) {
				boolean a = false;
				ArrayList<CourseItem2> item2s = item1s.get(j).getResult();
				for (int k = 0; k < item2s.size(); k++) {
					if (item2s.get(k).getSelected()) {
						a = true;
						b = true;
						isHaveCourse = true;
						continue;
					}
					item2s.remove(k);
					k--;
				}
				if (!a) {
					item1s.remove(j);
					j--;
				}
			}
			if (!b) {
				courses.remove(i);
				i--;
			}
		}
		LogUtils.d(courses.toString());
		return courses;
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent data) {
		super.onActivityResult(arg0, arg1, data);
		switch (arg0) {
			case Constants.RequestResultCode.TAKE_PHOTO:
				// 拍照回來
				if (RESULT_OK == arg1 && null != imageUri) {
					// 更新圖庫
					ImageUtils.updateGrally(this, imageUri);
					// 啟動裁剪
					startZoom(imageUri);
				}
				break;
			case Constants.RequestResultCode.GALLERY:
				// 圖庫選擇
				if (RESULT_OK == arg1 && null != data && null != data.getData()) {
					// 啟動裁剪
					startZoom(data.getData());
				}
				break;
			case Constants.RequestResultCode.ZOOM:
				// 裁剪完成
				// 圖庫選擇
				if (RESULT_OK == arg1 && null != zoomUri) {
					// 上傳頭像
					String path = zoomUri.getPath();
					if (!TextUtils.isEmpty(path)) {
						File file = new File(path);
						if (null != file && file.exists()) {
							file = null;
							new UpLoadAvatarTask(path).execute();
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
	 * 進入主界面
	 */
	private void go2Main() {
		Intent intent = new Intent();
		if (Constants.General.ROLE_TUTOR == role) {
			intent.setClass(this, TeacherMainActivity.class);
		} else {
			intent.setClass(this, StudentMainActivity.class);
		}
		// 保存信息
		TutorApplication.settingManager.writeSetting(Constants.SharedPreferences.SP_ISLOGIN, true);
		TutorApplication.settingManager.writeSetting(Constants.SharedPreferences.SP_ROLE, role);
		// 進入主界面
		startActivity(intent);
		finishNoAnim();
	}

	/**
	 * 獲取課程,地區等信息
	 * 
	 * @author bruce.chen
	 * 
	 */
	private class GetDataTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialogRes(R.string.loading);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			CourseListResult courseListResult = TutorService.getService().getCourseList();
			if (null != courseListResult) {
				courses = courseListResult.getResult();
			}
			AreaListResult areaListResult = TutorService.getService().getAreaList();
			if (null != areaListResult) {
				areas = areaListResult.getResult();
			}
			TimeSlotListResult timeSlotListResult = TutorService.getService().getTimeSlotList(account.getMemberId(), 0, 5);
			if (null != timeSlotListResult) {
				timeslots = timeSlotListResult.getResult();
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			dismissDialog();
			showData();
		}
	}

	/**
	 * 上傳照片任務
	 * 
	 * @author bruce.chen
	 * 
	 */
	private class UpLoadAvatarTask extends AsyncTask<Void, Void, Bitmap> {

		String path;

		public UpLoadAvatarTask(String path) {
			this.path = path;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialogRes(R.string.uploading_avatar);
		}

		@Override
		protected Bitmap doInBackground(Void... params) {
			// 先壓縮圖片
			String newPath = Constants.SDCard.getCacheDir() + DateTimeUtil.getSystemDateTime(DateTimeUtil.FORMART_YMDHMS) + Constants.General.IMAGE_END;
			if (ImageUtils.yaSuoImage(path, newPath, 400, 400)) {
				File file = new File(newPath);
				Bitmap result = UserService.getService().uploadAvatar(file);
				return result;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			dismissDialog();
			if (result != null) {
				avatar = ApiUrl.GET_OTHER_AVATAR + account.getMemberId();
				avatarImageView.setImageBitmap(result);
			} else {
				toast(R.string.toast_avatar_upload_fail);
			}
		}
	}

	/**
	 * 提交老师信息任务
	 * 
	 * @author bruce.chen
	 * 
	 */
	private class TutorProfileTask extends AsyncTask<Void, Void, EditProfileResult> {

		private TeacherProfile profile;

		public TutorProfileTask(TeacherProfile profile) {
			this.profile = profile;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialogRes(R.string.loading);
		}

		@Override
		protected EditProfileResult doInBackground(Void... params) {
			return TutorService.getService().submitTutorProfile(profile);
		}

		@Override
		protected void onPostExecute(EditProfileResult result) {
			super.onPostExecute(result);
			dismissDialog();
			if (null != result) {
				if (200 == result.getStatusCode()) {
					go2Main();
				} else {
					toast(result.getMessage());
				}
			} else {
				toast(R.string.toast_server_error);
			}
		}
	}

	/**
	 * 提交学生信息任务
	 * 
	 * @author bruce.chen
	 * 
	 */
	private class StudentProfileTask extends AsyncTask<Void, Void, EditProfileResult> {

		private StudentProfile profile;

		public StudentProfileTask(StudentProfile profile) {
			this.profile = profile;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialogRes(R.string.loading);
		}

		@Override
		protected EditProfileResult doInBackground(Void... params) {
			return TutorService.getService().submitStudentProfile(profile);
		}

		@Override
		protected void onPostExecute(EditProfileResult result) {
			super.onPostExecute(result);
			dismissDialog();
			if (null != result) {
				if (200 == result.getStatusCode() && result.getResult()) {
					go2Main();
				} else {
					toast(result.getMessage());
				}
			} else {
				toast(R.string.toast_server_error);
			}
		}
	}
}
