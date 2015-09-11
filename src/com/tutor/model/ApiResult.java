package com.tutor.model;

import java.io.Serializable;

/**
 * api返回數據model
 * 
 * @author bruce.chen
 * 
 *         2015-8-31
 */
public class ApiResult<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	private int statusCode;
	private String message;
	private String messageDetail;
	private T result;

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessageDetail() {
		return messageDetail;
	}

	public void setMessageDetail(String messageDetail) {
		this.messageDetail = messageDetail;
	}

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "ApiResult [statusCode=" + statusCode + ", message=" + message + ", messageDetail=" + messageDetail + ", result=" + result + "]";
	}
}
