package com.tutor.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author bruce.chen
 * 
 *         2015-9-9
 */
public class StudentProfile implements Serializable {

	private static final long serialVersionUID = 1L;
	private int Id;
	private String NickName;
	private String UserName;
	private String Phone;
	private String Email;
	private String Password;
	private String FBOpenID;
	private String IMID;
	private String Avatar;
	private Date Birth;
	private int Gender;
	private String Introduction;
	// 个人简介视频地址
	private String IntroductionVideo;
	private String Token;
	private Date ExpirationTime;
	private int Status;
	/**
	 * Student 0 Tutor 1 Both 2
	 **/
	private int AccountType;
	// 注册时间
	private Date CreatedTime;
	private String Grade;
	private ArrayList<Course> Courses;
	private ArrayList<Area> Areas;
	private ArrayList<Timeslot> Timeslots;

	public String getAvatar() {
		return Avatar;
	}

	public void setAvatar(String avatar) {
		Avatar = avatar;
	}

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public String getNickName() {
		return NickName;
	}

	public void setNickName(String nickName) {
		NickName = nickName;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	public String getPhone() {
		return Phone;
	}

	public void setPhone(String phone) {
		Phone = phone;
	}

	public String getEmail() {
		return Email;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public String getPassword() {
		return Password;
	}

	public void setPassword(String password) {
		Password = password;
	}

	public String getFBOpenID() {
		return FBOpenID;
	}

	public void setFBOpenID(String fBOpenID) {
		FBOpenID = fBOpenID;
	}

	public String getIMID() {
		return IMID;
	}

	public void setIMID(String iMID) {
		IMID = iMID;
	}

	public Date getBirth() {
		return Birth;
	}

	public void setBirth(Date birth) {
		Birth = birth;
	}

	public int getGender() {
		return Gender;
	}

	public void setGender(int gender) {
		Gender = gender;
	}

	public String getIntroduction() {
		return Introduction;
	}

	public void setIntroduction(String introduction) {
		Introduction = introduction;
	}

	public String getIntroductionVideo() {
		return IntroductionVideo;
	}

	public void setIntroductionVideo(String introductionVideo) {
		IntroductionVideo = introductionVideo;
	}

	public String getToken() {
		return Token;
	}

	public void setToken(String token) {
		Token = token;
	}

	public Date getExpirationTime() {
		return ExpirationTime;
	}

	public void setExpirationTime(Date expirationTime) {
		ExpirationTime = expirationTime;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public int getAccountType() {
		return AccountType;
	}

	public void setAccountType(int accountType) {
		AccountType = accountType;
	}

	public Date getCreatedTime() {
		return CreatedTime;
	}

	public void setCreatedTime(Date createdTime) {
		CreatedTime = createdTime;
	}

	public String getGrade() {
		return Grade;
	}

	public void setGrade(String grade) {
		Grade = grade;
	}

	public ArrayList<Course> getCourses() {
		return Courses;
	}

	public void setCourses(ArrayList<Course> courses) {
		Courses = courses;
	}

	public ArrayList<Area> getAreas() {
		return Areas;
	}

	public void setAreas(ArrayList<Area> areas) {
		Areas = areas;
	}

	public ArrayList<Timeslot> getTimeslots() {
		return Timeslots;
	}

	public void setTimeslots(ArrayList<Timeslot> timeslots) {
		Timeslots = timeslots;
	}
}
