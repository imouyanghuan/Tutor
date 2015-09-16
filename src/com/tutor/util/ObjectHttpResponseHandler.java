package com.tutor.util;

import java.net.HttpURLConnection;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import android.text.TextUtils;

import com.loopj.android.http.TextHttpResponseHandler;

public abstract class ObjectHttpResponseHandler<T> extends TextHttpResponseHandler {

	private Class<T> clazz;

	public ObjectHttpResponseHandler(Class<T> clazz) {
		this(clazz, HTTP.UTF_8);
	}

	public ObjectHttpResponseHandler(Class<T> clazz, String encoding) {
		super(encoding);
		this.clazz = clazz;
	}

	@Override
	public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
		onFailure(arg0, arg2);
	}

	@Override
	public void onSuccess(int arg0, Header[] arg1, String arg2) {
		if (HttpURLConnection.HTTP_OK == arg0 && !TextUtils.isEmpty(arg2)) {
			try {
				T t = JsonUtil.parseJStr2Object(clazz, arg2);
				if (null != t) {
					onSuccess(t);
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			onFailure(arg0, arg2);
		} else {
			onFailure(arg0, arg2);
		}
	}

	public abstract void onFailure(int status, String message);

	public abstract void onSuccess(T t);
}
