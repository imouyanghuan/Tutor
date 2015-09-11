package com.tutor.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.mssky.mobile.core.JsonHelper;
import com.tutor.TutorApplication;
import com.tutor.im.XmppManager;
import com.tutor.model.CheckExistResult;
import com.tutor.model.LoginResponseResult;
import com.tutor.model.RegisterInfoResult;
import com.tutor.model.RegisterLoginModel;
import com.tutor.model.UploadAvatarResult;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.util.CheckTokenUtils;
import com.tutor.util.ImageUtils;

/**
 * 用戶服務
 * 
 * @author bruce.chen
 * 
 *         2015-8-25
 */
public class UserService extends TutorBaseService {

	private static UserService instance;

	private UserService() {}

	public static UserService getService() {
		if (instance == null) {
			instance = new UserService();
		}
		return instance;
	}

	/**
	 * 登錄
	 * 
	 * @param model
	 * @return
	 */
	public LoginResponseResult login(RegisterLoginModel model) {
		StringEntity entity = null;
		String content = JsonHelper.tojson(model);
		try {
			entity = new StringEntity(content, HTTP.UTF_8);
			RequestParams params = TutorApplication.getDefaultPostParams();
			params.setBodyEntity(entity);
			LoginResponseResult result = executeEntityPost(ApiUrl.LOGIN, params, LoginResponseResult.class);
			return result;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 註冊,同時要註冊IM賬號
	 * 
	 * @param model
	 * @return
	 */
	public RegisterInfoResult register(RegisterLoginModel model) {
		StringEntity entity = null;
		String content = JsonHelper.tojson(model);
		try {
			entity = new StringEntity(content, HTTP.UTF_8);
			RequestParams params = TutorApplication.getDefaultPostParams();
			params.setBodyEntity(entity);
			RegisterInfoResult result = executeEntityPost(ApiUrl.REGISTER, params, RegisterInfoResult.class);
			if (null != result && 200 == result.getStatusCode()) {
				// 註冊IM
				String imAccount = model.getIMID();
				int status = XmppManager.getInstance().register(imAccount, imAccount);
				if (null != result.getResult()) {
					result.getResult().setStatus(status);
				}
			}
			return result;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 驗證賬號是否存在
	 * 
	 * @param account
	 * @param accountType
	 * @return
	 */
	public CheckExistResult exist(String FBid, int accountType) {
		RequestParams params = TutorApplication.getDefaultGetParams();
		try {
			params.addQueryStringParameter("accountType", accountType + "");
			params.addQueryStringParameter("email", "");
			params.addQueryStringParameter("FBOpenID", FBid);
			CheckExistResult result = executeEntityGet(ApiUrl.ACCOUNT_EXIST, params, CheckExistResult.class);
			return result;
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 上傳頭像
	 * 
	 * @param avatar
	 * @return
	 */
	public Bitmap uploadAvatar(File avatar) {
		Bitmap bitmap = null;
		try {
			RequestParams params = TutorApplication.getDefaultGetParams();
			params.addBodyParameter("file", avatar, "form-data");
			UploadAvatarResult result = executeEntityPost(ApiUrl.UPLOAD_AVATAR, params, UploadAvatarResult.class);
			if (null != result && 200 == result.getStatusCode() && !TextUtils.isEmpty(result.getResult())) {
				String path = ApiUrl.DOMAIN + result.getResult();
				// 移除緩存中的bitmap
				ImageUtils.getUtils().removeBitmapFormMemoryCache(path);
				bitmap = getAvatar(path);
			}
			CheckTokenUtils.checkToken(result);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (HttpException e) {
			e.printStackTrace();
			if (Constants.General.UNAUTHORIZED.equals(e.getMessage())) {
				CheckTokenUtils.reLogin();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * 獲取頭像
	 * 
	 * @param urlPath
	 * @return
	 */
	public Bitmap getAvatar(String urlPath) {
		Bitmap bitmap = ImageUtils.getUtils().getBitmapFromMemCache(urlPath);
		if (null == bitmap) {
			HttpURLConnection httpURLConnection = null;
			try {
				URL url = new URL(urlPath);
				if (null != url) {
					httpURLConnection = (HttpURLConnection) url.openConnection();
					// 設置連接超時時間5s
					httpURLConnection.setConnectTimeout(5000);
					httpURLConnection.setDoInput(true);
					httpURLConnection.setRequestMethod("GET");
					httpURLConnection.setRequestProperty("token", TutorApplication.getToken());
					httpURLConnection.setRequestProperty("lang", "en_US");
					int responseCode = httpURLConnection.getResponseCode();
					if (200 == responseCode) {
						// 獲取輸入流
						InputStream is = httpURLConnection.getInputStream();
						if (null != is) {
							bitmap = BitmapFactory.decodeStream(is);
							// 添加進緩存
							ImageUtils.getUtils().addBitmapToMemoryCache(urlPath, bitmap);
							is.close();
						}
					}
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (null != httpURLConnection) {
					httpURLConnection.disconnect();
				}
			}
		}
		return bitmap;
	}
}
