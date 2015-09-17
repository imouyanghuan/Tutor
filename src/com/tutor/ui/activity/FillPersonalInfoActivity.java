package com.tutor.ui.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
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
import com.tutor.model.UploadAvatarResult;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.ui.dialog.AddTimeSlotDialog;
import com.tutor.ui.dialog.AddTimeSlotDialog.CallBack;
import com.tutor.ui.dialog.ChangeAvatarDialog;
import com.tutor.ui.view.AreaItemLayout;
import com.tutor.ui.view.CourseLayout;
import com.tutor.ui.view.TitleBar;
import com.tutor.util.CheckTokenUtils;
import com.tutor.util.DateTimeUtil;
import com.tutor.util.HttpHelper;
import com.tutor.util.ImageUtils;
import com.tutor.util.JsonUtil;
import com.tutor.util.LogUtils;
import com.tutor.util.ObjectHttpResponseHandler;

/**
 * 完善資料界面
 * 
 * @author bruce.chen
 * 
 *         2015-8-24
 */
public class FillPersonalInfoActivity extends BaseActivity implements OnClickListener, CallBack, OnDateSetListener {

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
	// 选择生日对话框
	private DatePickerDialog datePickerDialog;
	// 保存拍照圖片uri
	private Uri imageUri, zoomUri;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		role = getIntent().getIntExtra(Constants.IntentExtra.INTENT_EXTRA_ROLE, -1);
		if (-1 == role) {
			throw new IllegalArgumentException("role is -1");
		}
		account = TutorApplication.getAccountDao().load("1");
		if (null == account) {
			throw new IllegalArgumentException("account is null");
		}
		setContentView(R.layout.activity_fill_personal_info);
		initView();
		birth = getString(R.string.label_date);
		eb = getString(R.string.eb_1);
		timeSlotDialog = new AddTimeSlotDialog(this, this);
		getdata();
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
					submitTutorProfile(profile);
				} else {
					StudentProfile profile = getStudentProfile();
					profile.setNickName(name);
					profile.setUserName(name);
					profile.setPhone(phone);
					profile.setCourses(choiceCourses);
					profile.setAreas(choiceAreas);
					profile.setTimeslots(timeslots);
					submitStudentProfile(profile);
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
			case R.id.ac_fill_personal_info_tv_birth:
				if (null == datePickerDialog) {
					datePickerDialog = new DatePickerDialog(this, this, 1990, 0, 1);
				}
				datePickerDialog.show();
				break;
		}
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		birth = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
		brithTextView.setText(birth);
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
		profile.setBirth(DateTimeUtil.str2Date(birth, DateTimeUtil.FORMART_YMD));
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
		ArrayList<Area1> selectAreas = new ArrayList<Area1>();
		for (Area area : areas) {
			ArrayList<Area1> area1s = area.getResult();
			for (Area1 area1 : area1s) {
				if (area1.getSelected()) {
					selectAreas.add(area1);
				}
			}
		}
		// 还原结构
		if (0 < selectAreas.size()) {
			ArrayList<Area> result = new ArrayList<Area>();
			isHaveArea = true;
			for (Area1 area1 : selectAreas) {
				if (result.size() == 0) {
					Area area = new Area();
					area.setName(area1.getDistrict());
					ArrayList<Area1> a = new ArrayList<Area1>();
					a.add(area1);
					area.setResult(a);
					result.add(area);
				} else {
					// 已有数据
					int size = result.size();
					Area area = null;
					for (int i = 0; i < size; i++) {
						area = result.get(i);
						if (area.getName().equals(area1.getDistrict())) {
							break;
						} else {
							area = null;
						}
					}
					if (null != area) {
						// 添加选择的地区
						area.getResult().add(area1);
					} else {
						// 添加新的
						area = new Area();
						area.setName(area1.getDistrict());
						ArrayList<Area1> a = new ArrayList<Area1>();
						a.add(area1);
						area.setResult(a);
						result.add(area);
					}
				}
			}
			LogUtils.d(result.toString());
			LogUtils.d(areas.toString());
			return result;
		}
		return null;
	}

	private boolean isHaveCourse = false;

	/**
	 * 获取选中的课程
	 * 
	 * @return
	 */
	private ArrayList<Course> getChoiceCourses() {
		if (null == courses) {
			return null;
		}
		// 选中的课程集合
		ArrayList<CourseItem2> selectCourses = new ArrayList<CourseItem2>();
		for (Course course : courses) {
			ArrayList<CourseItem1> item1s = course.getResult();
			for (CourseItem1 item1 : item1s) {
				ArrayList<CourseItem2> item2s = item1.getResult();
				for (CourseItem2 item2 : item2s) {
					if (item2.getSelected()) {
						selectCourses.add(item2);
					}
				}
			}
		}
		// 将选中的课程整理成原来的结构
		int selectSize = selectCourses.size();
		if (selectSize > 0) {
			isHaveCourse = true;
			ArrayList<Course> result = new ArrayList<Course>();
			for (CourseItem2 item2 : selectCourses) {
				// 首次添加
				if (result.size() == 0) {
					// 1
					Course course = new Course();
					course.setName(item2.getType());
					ArrayList<CourseItem1> item1s = new ArrayList<CourseItem1>();
					// 2
					CourseItem1 item1 = new CourseItem1();
					item1.setName(item2.getSubType());
					// 3
					ArrayList<CourseItem2> item2s = new ArrayList<CourseItem2>();
					item2s.add(item2);
					// 2
					item1.setResult(item2s);
					item1s.add(item1);
					// 1
					course.setResult(item1s);
					result.add(course);
				} else {
					// 列表已经有数据了
					int size = result.size();
					Course course = null;
					for (int i = 0; i < size; i++) {
						course = result.get(i);
						if (course.getName().equals(item2.getType())) {
							break;
						} else {
							course = null;
						}
					}
					// 已存在该分类
					if (null != course) {
						// 取出子list
						ArrayList<CourseItem1> item1s = course.getResult();
						int size1 = item1s.size();
						// 取出CourseItem1
						CourseItem1 item1 = null;
						for (int i = 0; i < size1; i++) {
							item1 = item1s.get(i);
							if (item1.getName().equals(item2.getSubType())) {
								break;
							} else {
								item1 = null;
							}
						}
						if (null != item1) {
							// 存在,把item2加入item1的result集合里
							item1.getResult().add(item2);
						} else {
							// 不存在,添加一个
							CourseItem1 newitem1 = new CourseItem1();
							newitem1.setName(item2.getSubType());
							// 3
							ArrayList<CourseItem2> item2s = new ArrayList<CourseItem2>();
							item2s.add(item2);
							// 2
							newitem1.setResult(item2s);
							item1s.add(newitem1);
						}
					} else {
						// 不存在该分类的时候,添加
						// 1
						course = new Course();
						course.setName(item2.getType());
						ArrayList<CourseItem1> item1s = new ArrayList<CourseItem1>();
						// 2
						CourseItem1 item1 = new CourseItem1();
						item1.setName(item2.getSubType());
						// 3
						ArrayList<CourseItem2> item2s = new ArrayList<CourseItem2>();
						item2s.add(item2);
						// 2
						item1.setResult(item2s);
						item1s.add(item1);
						// 1
						course.setResult(item1s);
						result.add(course);
					}
				}
			}
			LogUtils.d(result.toString());
			LogUtils.d(courses.toString());
			return result;
		}
		return null;
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
	 */
	private void getdata() {
		if (!HttpHelper.isNetworkConnected(this)) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		showDialogRes(R.string.loading);
		getCourse();
		getAreas();
		getTimeSlot();
	}

	private void getCourse() {
		HttpHelper.get(this, ApiUrl.COURSELIST, TutorApplication.getHeaders(), new RequestParams(), new ObjectHttpResponseHandler<CourseListResult>(CourseListResult.class) {

			@Override
			public void onFailure(int status, String message) {
				dismissDialog();
			}

			@Override
			public void onSuccess(CourseListResult t) {
				dismissDialog();
				if (null != t) {
					courses = t.getResult();
				}
				if (null != courses && 0 != courses.size()) {
					CourseLayout courseLayout = new CourseLayout(FillPersonalInfoActivity.this, courses);
					couressLinearLayout.addView(courseLayout);
				}
			}
		});
	}

	private void getAreas() {
		HttpHelper.get(this, ApiUrl.AREALIST, TutorApplication.getHeaders(), new RequestParams(), new ObjectHttpResponseHandler<AreaListResult>(AreaListResult.class) {

			@Override
			public void onFailure(int status, String message) {}

			@Override
			public void onSuccess(AreaListResult t) {
				if (null != t) {
					areas = t.getResult();
				}
				if (null != areas && 0 != areas.size()) {
					for (Area area : areas) {
						AreaItemLayout areaItemLayout = new AreaItemLayout(FillPersonalInfoActivity.this, area);
						areaLinearLayout.addView(areaItemLayout);
					}
				}
			}
		});
	}

	private void getTimeSlot() {
		RequestParams params = new RequestParams();
		params.put("memberId", "" + account.getMemberId());
		params.put("pageIndex", "" + 0);
		params.put("pageSize", "" + 5);
		HttpHelper.get(this, ApiUrl.TIMESLOTLIST, TutorApplication.getHeaders(), params, new ObjectHttpResponseHandler<TimeSlotListResult>(TimeSlotListResult.class) {

			@Override
			public void onFailure(int status, String message) {}

			@Override
			public void onSuccess(TimeSlotListResult t) {
				if (null != t) {
					timeslots = t.getResult();
				}
				if (null != timeslots) {
					adapter.refresh(timeslots);
				}
			}
		});
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
				HttpHelper.post(this, ApiUrl.UPLOAD_AVATAR, TutorApplication.getHeaders(), params, new ObjectHttpResponseHandler<UploadAvatarResult>(UploadAvatarResult.class) {

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
							avatar = t.getResult();
							ImageUtils.loadImage(avatarImageView, ApiUrl.DOMAIN + avatar);
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
	 * 提交老师信息任务
	 * 
	 */
	private void submitTutorProfile(TeacherProfile profile) {
		if (!HttpHelper.isNetworkConnected(this)) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		showDialogRes(R.string.loading);
		String json = JsonUtil.parseObject2Str(profile);
		try {
			StringEntity entity = new StringEntity(json, HTTP.UTF_8);
			HttpHelper.put(this, ApiUrl.TUTORPROFILE, TutorApplication.getHeaders(), entity, new ObjectHttpResponseHandler<EditProfileResult>(EditProfileResult.class) {

				@Override
				public void onFailure(int status, String message) {
					dismissDialog();
					LogUtils.e(message);
					toast(R.string.toast_server_error);
				}

				@Override
				public void onSuccess(EditProfileResult result) {
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
			});
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			dismissDialog();
		}
	}

	/**
	 * 提交学生信息任务
	 * 
	 */
	private void submitStudentProfile(StudentProfile profile) {
		if (!HttpHelper.isNetworkConnected(this)) {
			toast(R.string.toast_netwrok_disconnected);
			return;
		}
		showDialogRes(R.string.loading);
		String json = JsonUtil.parseObject2Str(profile);
		try {
			StringEntity entity = new StringEntity(json, HTTP.UTF_8);
			HttpHelper.put(this, ApiUrl.STUDENTPROFILE, TutorApplication.getHeaders(), entity, new ObjectHttpResponseHandler<EditProfileResult>(EditProfileResult.class) {

				@Override
				public void onFailure(int status, String message) {
					dismissDialog();
					LogUtils.e(message);
					toast(R.string.toast_server_error);
				}

				@Override
				public void onSuccess(EditProfileResult result) {
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
			});
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			dismissDialog();
		}
	}
}
