package com.tutor.service;

import java.io.IOException;

import android.text.TextUtils;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.mssky.mobile.core.JsonHelper;
import com.mssky.mobile.core.services.BaseService;
import com.tutor.TutorApplication;
import com.tutor.params.ApiUrl;
import com.tutor.util.HttpUtils;
import com.tutor.util.LogUtils;

/**
 * @author bruce.chen
 * 
 *         2015-8-31
 */
public class TutorBaseService extends BaseService {

	/**
	 * get請求
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	protected String executeGet(String url, RequestParams params) throws HttpException, IOException {
		if (!HttpUtils.isNetworkConnected(TutorApplication.instance)) {
			return null;
		}
		String result = super.execute(HttpMethod.GET, ApiUrl.DOMAIN, url, params);
		LogUtils.d(result);
		return result;
	}

	/**
	 * post請求
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	protected String executePost(String url, RequestParams params) throws HttpException, IOException {
		if (!HttpUtils.isNetworkConnected(TutorApplication.instance)) {
			return null;
		}
		String result = super.execute(HttpMethod.POST, ApiUrl.DOMAIN, url, params);
		LogUtils.d(result);
		return result;
	}

	/**
	 * put請求
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	protected String executePut(String url, RequestParams params) throws HttpException, IOException {
		if (!HttpUtils.isNetworkConnected(TutorApplication.instance)) {
			return null;
		}
		String result = super.execute(HttpMethod.PUT, ApiUrl.DOMAIN, url, params);
		LogUtils.d(result);
		return result;
	}

	/**
	 * get請求
	 * 
	 * @param url
	 * @param params
	 * @param t
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	protected <T> T executeEntityGet(String url, RequestParams params, Class<T> type) throws HttpException, IOException {
		if (!HttpUtils.isNetworkConnected(TutorApplication.instance)) {
			return null;
		}
		String content = executeGet(url, params);
		if (!TextUtils.isEmpty(content)) {
			T result = JsonHelper.convert(content, type);
			return result;
		}
		return null;
	}

	/**
	 * post請求
	 * 
	 * @param url
	 * @param params
	 * @param t
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	protected <T> T executeEntityPost(String url, RequestParams params, Class<T> type) throws HttpException, IOException {
		if (!HttpUtils.isNetworkConnected(TutorApplication.instance)) {
			return null;
		}
		String content = executePost(url, params);
		if (!TextUtils.isEmpty(content)) {
			T result = JsonHelper.convert(content, type);
			return result;
		}
		return null;
	}

	/**
	 * put請求
	 * 
	 * @param url
	 * @param params
	 * @param t
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	protected <T> T executeEntityPut(String url, RequestParams params, Class<T> type) throws HttpException, IOException {
		if (!HttpUtils.isNetworkConnected(TutorApplication.instance)) {
			return null;
		}
		String content = executePut(url, params);
		if (!TextUtils.isEmpty(content)) {
			T result = JsonHelper.convert(content, type);
			return result;
		}
		return null;
	}
}
