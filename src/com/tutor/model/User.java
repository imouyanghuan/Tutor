package com.tutor.model;

import java.io.Serializable;

import org.jivesoftware.smack.packet.RosterPacket;

public class User implements Serializable {

	private static final long serialVersionUID = 1L;
	private String _id;
	private String JID; // 用户的ID
	private String name; // 名字
	private static RosterPacket.ItemType type;
	private String status;
	private String froms;
	private String groupName;
	/**
	 * 用户状态对应的图片
	 */
	private int imgId;
	/**
	 * group的size
	 */
	private int size;
	private boolean available;

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getJID() {
		return JID;
	}

	public void setJID(String jID) {
		JID = jID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static RosterPacket.ItemType getType() {
		return type;
	}

	public void setType(RosterPacket.ItemType type) {
		User.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFroms() {
		return froms;
	}

	public void setFroms(String froms) {
		this.froms = froms;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public int getImgId() {
		return imgId;
	}

	public void setImgId(int imgId) {
		this.imgId = imgId;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public User clone() {
		User user = new User();
		user.setAvailable(User.this.available);
		user.setFroms(User.this.froms);
		user.setGroupName(User.this.groupName);
		user.setImgId(User.this.imgId);
		user.setJID(User.this.JID);
		user.setName(User.this.name);
		user.setSize(User.this.size);
		user.setStatus(User.this.status);
		return user;
	}

	@Override
	public String toString() {
		return "User [JID=" + JID + ", name=" + name + ", status=" + status + ", froms=" + froms + ", groupName=" + groupName + ", imgId=" + imgId + ", size=" + size + ", available=" + available
				+ "]";
	}
}
