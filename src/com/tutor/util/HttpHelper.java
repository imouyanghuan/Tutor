package com.tutor.util;

import java.util.HashMap;

import org.apache.http.HttpEntity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.tutor.params.ApiUrl;

/**
 * 
 * @author
 * 
 */
public class HttpHelper {

	private Context context;
	private static HttpHelper helper;
	/** 默认请求超时时间 15s */
	protected static int httpConnectTimeOut = 30 * 1000;
	private AsyncHttpClient client;

	public static void init(Context context) {
		helper = new HttpHelper(context);
	}

	private HttpHelper(Context contex) {
		this.context = contex;
		client = new AsyncHttpClient();
		client.setConnectTimeout(httpConnectTimeOut);
		client.setResponseTimeout(httpConnectTimeOut);
	}

	public static HttpHelper getHelper() {
		return helper;
	}

	/**
	 * post请求
	 * 
	 * @param url
	 *            访问URL
	 * @param params
	 *            请求参数
	 * @param responseHandler
	 *            响应监听
	 */
	public <T> void post(String url, HashMap<String, String> headers, RequestParams params, ObjectHttpResponseHandler<T> responseHandler) {
		addHeader(headers);
		LogUtils.d(url);
		client.post(ApiUrl.DOMAIN + url, params, responseHandler);
	}

	/**
	 * post请求
	 * 
	 * @param context
	 * @param url
	 * @param headers
	 * @param entity
	 * @param responseHandler
	 */
	public <T> void post(String url, HashMap<String, String> headers, HttpEntity entity, ObjectHttpResponseHandler<T> responseHandler) {
		addHeader(headers);
		LogUtils.d(url);
		client.post(context, ApiUrl.DOMAIN + url, null, entity, "application/json", responseHandler);
	}

	/**
	 * get请求
	 * 
	 * @param url
	 *            访问URL
	 * @param params
	 *            请求参数
	 * @param responseHandler
	 *            响应监听
	 */
	public <T> void get(String url, HashMap<String, String> headers, RequestParams params, ObjectHttpResponseHandler<T> responseHandler) {
		addHeader(headers);
		LogUtils.d(url);
		client.get(context, ApiUrl.DOMAIN + url, params, responseHandler);
	}

	/**
	 * put请求
	 * 
	 * @param url
	 *            访问URL
	 * @param params
	 *            请求参数
	 * @param responseHandler
	 *            响应监听
	 */
	public <T> void put(String url, HashMap<String, String> headers, RequestParams params, ObjectHttpResponseHandler<T> responseHandler) {
		addHeader(headers);
		LogUtils.d(url);
		client.put(context, ApiUrl.DOMAIN + url, params, responseHandler);
	}

	/**
	 * put请求
	 * 
	 * @param url
	 *            访问URL
	 * @param params
	 *            请求参数
	 * @param responseHandler
	 *            响应监听
	 */
	public <T> void put(String url, HashMap<String, String> headers, HttpEntity entity, ObjectHttpResponseHandler<T> responseHandler) {
		addHeader(headers);
		LogUtils.d(url);
		client.put(context, ApiUrl.DOMAIN + url, null, entity, "application/json", responseHandler);
	}

	/**
	 * 添加header
	 * 
	 * @param headers
	 */
	private void addHeader(HashMap<String, String> headers) {
		if (null == client) {
			return;
		}
		client.removeAllHeaders();
		if (null == headers || 0 == headers.size()) {
			return;
		}
		for (String key : headers.keySet()) {
			client.addHeader(key, headers.get(key));
		}
	}

	/**
	 * 判断是否有网络
	 * 
	 * @param context
	 * @return true:有网络，false：没有
	 */
	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}
}
