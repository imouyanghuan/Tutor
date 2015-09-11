package com.tutor.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 用戶登錄后或註冊后返回的信息
 * 
 * @author bruce.chen
 * 
 *         2015-8-31
 */
public class UserInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private String nickName;
	private String userName;
	private String phone;
	private String email;
	private String imid;
	private String password;
	private String fbOpenID;
	private String avatar;
	private String birth;
	private String gender;
	private String introduction;
	private String introductionVideo;
	private String token;
	private String expirationTime;
	private String createdTime;
	private int status;
	private int accountType;
	private int bookmarkID;
	private ArrayList<Course> courses;
	private ArrayList<Area> areas;
	private ArrayList<Timeslot> timeslots;

	public ArrayList<Course> getCourses() {
		return courses;
	}

	public void setCourses(ArrayList<Course> courses) {
		this.courses = courses;
	}

	public ArrayList<Area> getAreas() {
		return areas;
	}

	public void setAreas(ArrayList<Area> areas) {
		this.areas = areas;
	}

	public ArrayList<Timeslot> getTimeslots() {
		return timeslots;
	}

	public void setTimeslots(ArrayList<Timeslot> timeslots) {
		this.timeslots = timeslots;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getImid() {
		return imid;
	}

	public void setImid(String imid) {
		this.imid = imid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFbOpenID() {
		return fbOpenID;
	}

	public void setFbOpenID(String fbOpenID) {
		this.fbOpenID = fbOpenID;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getBirth() {
		return birth;
	}

	public void setBirth(String birth) {
		this.birth = birth;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public String getIntroductionVideo() {
		return introductionVideo;
	}

	public void setIntroductionVideo(String introductionVideo) {
		this.introductionVideo = introductionVideo;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(String expirationTime) {
		this.expirationTime = expirationTime;
	}

	public String getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getAccountType() {
		return accountType;
	}

	public void setAccountType(int accountType) {
		this.accountType = accountType;
	}

	public int getBookmarkID() {
		return bookmarkID;
	}

	public void setBookmarkID(int bookmarkID) {
		this.bookmarkID = bookmarkID;
	}

	@Override
	public String toString() {
		return "UserInfo [nickName=" + nickName + ", userName=" + userName + ", phone=" + phone + ", email=" + email + ", imid=" + imid + ", password=" + password + ", fbOpenID=" + fbOpenID
				+ ", avatar=" + avatar + ", birth=" + birth + ", gender=" + gender + ", introduction=" + introduction + ", introductionVideo=" + introductionVideo + ", token=" + token
				+ ", expirationTime=" + expirationTime + ", createdTime=" + createdTime + ", status=" + status + ", accountType=" + accountType + ", bookmarkID=" + bookmarkID + "]";
	}
}
