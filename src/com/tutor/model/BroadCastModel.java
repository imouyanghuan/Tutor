package com.tutor.model;

import java.io.Serializable;

/**
 * 发送tags的广播
 * 
 * @author jerry.yao
 * 
 */
public class BroadCastModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int broadcastId;
	private int memberId;

	public int getBroadcastId() {
		return broadcastId;
	}

	public void setBroadcastId(int broadcastId) {
		this.broadcastId = broadcastId;
	}

	public int getMemberId() {
		return memberId;
	}

	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}
}
