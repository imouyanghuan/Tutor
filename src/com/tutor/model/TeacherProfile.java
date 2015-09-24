package com.tutor.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author bruce.chen
 * 
 *         2015-9-9
 */
public class TeacherProfile implements Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	// 教师证ID
	private String hkidNumber;
	// 辅导经验年数
	private int exprience;
	// 综合评分 decimal number
	private double ratingGrade;
	// 收藏次数
	private int bookmarkedCount;
	// 学生数
	private int studentCount;
	// 文凭
	private int educationDegree;
	// 毕业学校
	private String graduateSchool;
	// 主修专业
	private String major;
	//
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
	private String txpirationTime;
	private int status;
	/**
	 * Student 0 Tutor 1 Both 2
	 **/
	private int accountType;
	// 注册时间
	private String createdTime;
	private ArrayList<Course> courses;
	private ArrayList<Area> areas;
	private ArrayList<Timeslot> timeslots;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getHkidNumber() {
		return hkidNumber;
	}

	public void setHkidNumber(String hkidNumber) {
		this.hkidNumber = hkidNumber;
	}

	public int getExprience() {
		return exprience;
	}

	public void setExprience(int exprience) {
		this.exprience = exprience;
	}

	public double getRatingGrade() {
		return ratingGrade;
	}

	public void setRatingGrade(double ratingGrade) {
		this.ratingGrade = ratingGrade;
	}

	public int getBookmarkedCount() {
		return bookmarkedCount;
	}

	public void setBookmarkedCount(int bookmarkedCount) {
		this.bookmarkedCount = bookmarkedCount;
	}

	public int getStudentCount() {
		return studentCount;
	}

	public void setStudentCount(int studentCount) {
		this.studentCount = studentCount;
	}

	public int getEducationDegree() {
		return educationDegree;
	}

	public void setEducationDegree(int educationDegree) {
		this.educationDegree = educationDegree;
	}

	public String getGraduateSchool() {
		return graduateSchool;
	}

	public void setGraduateSchool(String graduateSchool) {
		this.graduateSchool = graduateSchool;
	}

	public String getMajor() {
		return major;
	}

	public void setMajor(String major) {
		this.major = major;
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

	public String getTxpirationTime() {
		return txpirationTime;
	}

	public void setTxpirationTime(String txpirationTime) {
		this.txpirationTime = txpirationTime;
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

	@Override
	public String toString() {
		return "TeacherProfile [id=" + id + ", hkidNumber=" + hkidNumber + ", exprience=" + exprience + ", ratingGrade=" + ratingGrade + ", bookmarkedCount=" + bookmarkedCount + ", studentCount="
				+ studentCount + ", educationDegree=" + educationDegree + ", graduateSchool=" + graduateSchool + ", major=" + major + ", nickName=" + nickName + ", userName=" + userName + ", phone="
				+ phone + ", email=" + email + ", password=" + password + ", fbOpenID=" + fbOpenID + ", imid=" + imid + ", avatar=" + avatar + ", birth=" + birth + ", gender=" + gender
				+ ", introduction=" + introduction + ", introductionVideo=" + introductionVideo + ", token=" + token + ", txpirationTime=" + txpirationTime + ", status=" + status + ", accountType="
				+ accountType + ", createdTime=" + createdTime + ", courses=" + courses + ", areas=" + areas + ", timeslots=" + timeslots + "]";
	}
}
