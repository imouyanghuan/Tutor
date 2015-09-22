package com.tutor.model;

import java.io.Serializable;


public class BookmarkModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int bookmarkId;
	private UserInfo memberModel;

	public int getBookmarkId() {
		return bookmarkId;
	}

	public void setBookmarkId(int bookmarkId) {
		this.bookmarkId = bookmarkId;
	}

	public UserInfo getMemberModel() {
		return memberModel;
	}

	public void setMemberModel(UserInfo memberModel) {
		this.memberModel = memberModel;
	}
}
