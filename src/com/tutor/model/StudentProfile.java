package com.tutor.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author bruce.chen
 * 
 *         2015-9-9
 */
public class StudentProfile implements Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private String nickName;
	private String userName;
	private String phone;
	private String email;
	private String password;
	private String fbOpenID;
	private String imid;
	private String avatar;
	private String birth;
	private int gender;
	private String introduction;
	// 个人简介视频地址
	private String introductionVideo;
	private String token;
	private String expirationTime;
	private int status;
	/**
	 * Student 0 Tutor 1 Both 2
	 **/
	private int accountType;
	// 注册时间
	private String createdTime;
	private String grade;
	private int[] courseIds;
	private ArrayList<Course> courses;
	private int[] areaIds;
	private ArrayList<Area> areas;
	private ArrayList<Timeslot> timeslots;

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

	public String getImid() {
		return imid;
	}

	public void setImid(String imid) {
		this.imid = imid;
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

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
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

	public String getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

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

	public int[] getCourseIds() {
		return courseIds;
	}

	public void setCourseIds(int[] courseIds) {
		this.courseIds = courseIds;
	}

	public int[] getAreaIds() {
		return areaIds;
	}

	public void setAreaIds(int[] areaIds) {
		this.areaIds = areaIds;
	}

	@Override
	public String toString() {
		return "StudentProfile [id=" + id + ", nickName=" + nickName + ", userName=" + userName + ", phone=" + phone + ", email=" + email + ", password=" + password + ", fbOpenID=" + fbOpenID
				+ ", imid=" + imid + ", avatar=" + avatar + ", birth=" + birth + ", gender=" + gender + ", introduction=" + introduction + ", introductionVideo=" + introductionVideo + ", token="
				+ token + ", expirationTime=" + expirationTime + ", status=" + status + ", accountType=" + accountType + ", createdTime=" + createdTime + ", grade=" + grade + ", courseIds="
				+ Arrays.toString(courseIds) + ", courses=" + courses + ", areaIds=" + Arrays.toString(areaIds) + ", areas=" + areas + ", timeslots=" + timeslots + "]";
	}
}
