package com.tutor.ui.fragment.teacher;

import java.io.File;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lidroid.xutils.exception.DbException;
import com.tutor.R;
import com.tutor.TutorApplication;
import com.tutor.model.Account;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.service.UserService;
import com.tutor.ui.activity.BaseActivity;
import com.tutor.ui.dialog.ChangeAvatarDialog;
import com.tutor.ui.fragment.BaseFragment;
import com.tutor.util.DateTimeUtil;
import com.tutor.util.ImageUtils;
import com.tutor.util.ViewHelper;

/**
 * 老師首頁,我的
 * 
 * @author bruce.chen
 * 
 *         2015-8-20
 */
public class MyFragment extends BaseFragment implements OnClickListener {

	// 頭像對話框
	private ChangeAvatarDialog dialog;
	private ImageView avatar;
	private Bitmap avatarbBitmap;
	// 保存拍照圖片uri
	private Uri imageUri, zoomUri;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_teacher_my, container, false);
		initView(view);
		return view;
	}

	private void initView(View view) {
		avatar = ViewHelper.get(view, R.id.fragment_my_iv_avatar);
		avatar.setOnClickListener(this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// 獲取頭像
		new GetAvatarTask().execute();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
			case R.id.fragment_my_iv_avatar:
				// 上傳頭像
				if (null == dialog) {
					dialog = new ChangeAvatarDialog((BaseActivity) getActivity());
				}
				// 初始化uri
				String fileName = DateTimeUtil.getSystemDateTime(DateTimeUtil.FORMART_YMDHMS) + Constants.General.IMAGE_END;
				imageUri = Uri.fromFile(new File(Constants.SDCard.getImageDir(), fileName));
				dialog.setUri(imageUri);
				dialog.show();
				break;
			default:
				break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case Constants.RequestResultCode.TAKE_PHOTO:
				// 拍照回來
				if (BaseActivity.RESULT_OK == resultCode && null != imageUri) {
					// 更新圖庫
					ImageUtils.updateGrally(getActivity(), imageUri);
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
			// 壓縮后的圖片路徑
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
				if (null != avatarbBitmap) {
					avatarbBitmap.recycle();
					avatarbBitmap = null;
				}
				avatarbBitmap = result;
				avatar.setImageBitmap(avatarbBitmap);
			} else {
				toast(R.string.toast_avatar_upload_fail);
			}
		}
	}

	/**
	 * 獲取頭像任務
	 * 
	 * @author bruce.chen
	 * 
	 */
	private class GetAvatarTask extends AsyncTask<Void, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(Void... params) {
			try {
				Account account = TutorApplication.dbUtils.findFirst(Account.class);
				if (null != account) {
					return UserService.getService().getAvatar(ApiUrl.DOMAIN + ApiUrl.GET_OTHER_AVATAR + account.getMemberId());
				}
			} catch (DbException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			if (result != null) {
				if (null != avatarbBitmap) {
					avatarbBitmap.recycle();
					avatarbBitmap = null;
				}
				avatarbBitmap = result;
				avatar.setImageBitmap(avatarbBitmap);
			}
		}
	}
}
