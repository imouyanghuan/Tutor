package com.tutor.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author bruce.chen
 * 
 *         2015-9-9
 */
public class TeacherProfile implements Serializable {

	private static final long serialVersionUID = 1L;
	private int Id;
	// 教师证ID
	private String HKIDNumber;
	// 辅导经验年数
	private int Exprience;
	// 综合评分 decimal number
	private float DecRatingGrade;
	// 收藏次数
	private int BookmarkedCount;
	// 学生数
	private int StudentCount;
	// 文凭
	private String EducationDegree;
	// 毕业学校
	private String GraduateSchool;
	// 主修专业
	private String Major;
	//
	private String NickName;
	private String UserName;
	private String Phone;
	private String Email;
	private String Password;
	private String FBOpenID;
	private String IMID;
	private Date Birth;
	private String Gender;
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

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public String getHKIDNumber() {
		return HKIDNumber;
	}

	public void setHKIDNumber(String hKIDNumber) {
		HKIDNumber = hKIDNumber;
	}

	public int getExprience() {
		return Exprience;
	}

	public void setExprience(int exprience) {
		Exprience = exprience;
	}

	public float getDecRatingGrade() {
		return DecRatingGrade;
	}

	public void setDecRatingGrade(float decRatingGrade) {
		DecRatingGrade = decRatingGrade;
	}

	public int getBookmarkedCount() {
		return BookmarkedCount;
	}

	public void setBookmarkedCount(int bookmarkedCount) {
		BookmarkedCount = bookmarkedCount;
	}

	public int getStudentCount() {
		return StudentCount;
	}

	public void setStudentCount(int studentCount) {
		StudentCount = studentCount;
	}

	public String getEducationDegree() {
		return EducationDegree;
	}

	public void setEducationDegree(String educationDegree) {
		EducationDegree = educationDegree;
	}

	public String getGraduateSchool() {
		return GraduateSchool;
	}

	public void setGraduateSchool(String graduateSchool) {
		GraduateSchool = graduateSchool;
	}

	public String getMajor() {
		return Major;
	}

	public void setMajor(String major) {
		Major = major;
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

	public String getGender() {
		return Gender;
	}

	public void setGender(String gender) {
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
