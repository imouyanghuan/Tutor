package com.tutor.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author bruce.chen
 * 
 *         2015-9-8
 */
public class Notification implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6760904849402428963L;
	// NotificationOrientation
	public static final int ToBeMyStudent = 0;
	public static final int ToBeMyTutor = 1;
	//
	private int source;
	private int destination;
	private int orientation;
	private int status;
	private String content;
	private int[] coursesValues;
	private String displayCourses;
	private String frequent;
	private String pricePerHour;
	private int priceCurrency;
	private String address;
	private String expired;
	private String nickName;
	private String userName;
	private String avatar;
	private String createdTime;
	private int id;
	private ArrayList<Timeslot> timeslots;
	private String phone;
	private String hkidNumber;
	private String residentialAddress;
	// 广播id
	private int broadcastId;

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public int getDestination() {
		return destination;
	}

	public void setDestination(int destination) {
		this.destination = destination;
	}

	public int getOrientation() {
		return orientation;
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int[] getCoursesValues() {
		return coursesValues;
	}

	public void setCoursesValues(int[] coursesValues) {
		this.coursesValues = coursesValues;
	}

	public String getDisplayCourses() {
		return displayCourses;
	}

	public void setDisplayCourses(String displayCourses) {
		this.displayCourses = displayCourses;
	}

	public String getFrequent() {
		return frequent;
	}

	public void setFrequent(String frequent) {
		this.frequent = frequent;
	}

	public String getPricePerHour() {
		return pricePerHour;
	}

	public void setPricePerHour(String pricePerHour) {
		this.pricePerHour = pricePerHour;
	}

	public int getPriceCurrency() {
		return priceCurrency;
	}

	public void setPriceCurrency(int priceCurrency) {
		this.priceCurrency = priceCurrency;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getExpired() {
		return expired;
	}

	public void setExpired(String expired) {
		this.expired = expired;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ArrayList<Timeslot> getTimeslots() {
		return timeslots;
	}

	public void setTimeslots(ArrayList<Timeslot> timeslots) {
		this.timeslots = timeslots;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getHkidNumber() {
		return hkidNumber;
	}

	public void setHkidNumber(String hkidNumber) {
		this.hkidNumber = hkidNumber;
	}

	public String getResidentialAddress() {
		return residentialAddress;
	}

	public void setResidentialAddress(String residentialAddress) {
		this.residentialAddress = residentialAddress;
	}

	public int getBroadcastId() {
		return broadcastId;
	}

	public void setBroadcastId(int broadcastId) {
		this.broadcastId = broadcastId;
	}
}
