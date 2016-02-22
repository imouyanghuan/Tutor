package com.tutor.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * 用戶登錄后或註冊后返回的信息
 * 
 * @author bruce.chen
 * 
 *         2015-8-31
 */
public class UserInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 学习中
	 */
	public static final int STUDYING = 0;
	/**
	 * 已经毕业
	 */
	public static final int GRADUATED = 1;
	// 教师才有的字段
	private String hkidNumber;
	private int exprience;// 辅导经验年数
	private String registrationTime;// 教师登记时间
	private float ratingGrade;
	private int bookmarkedCount;
	private int studentCount;
	private int educationDegree;
	private String graduateSchool;
	private int educationStatus;
	private String major;
	private String introductionVideo;
	private boolean isCertified; // 是否已经认证
	private String followCount; // 关注度
	// 学生字段
	private int grade;
	// 教师和学生都有的字段
	private int id;
	private String nickName;
	private String userName;
	private String phone;
	private String email;
	private String residentialAddress;
	private String imid;
	private String password;
	private String fbOpenID;
	private String avatar;
	private String birth;
	private Integer gender;
	private String introduction;
	private String token;
	private String expirationTime;
	private String createdTime;
	private int status;
	private int accountType;
	private int bookmarkID;
	private int[] coursesValues;
	private int[] areaValues;
	private ArrayList<Course> courses;
	private ArrayList<Area> areas;
	private ArrayList<Timeslot> timeslots;
	private ArrayList<Timeslot> appointmentTimeslots;
	private boolean isMatched;
	private String gradeName;
	private boolean isVoluntaryTutoring;
	// 补习社服务年级
	private ArrayList<Integer> serviceGradeValues;
	private ArrayList<ServiceGrade> serviceGrades;
	// 补习社联系邮箱
	private String contactEmail;
	// 老师唯一标识码
	private String identityCode;
	// 活动相关
	private String alertTitle;
	private String alertContent;
	private String alertLink;
	// 手机号是否已经认证
	private boolean isPhoneVerified;

	public String getHkidNumber() {
		return hkidNumber;
	}

	public void setHkidNumber(String hkidNumber) {
		this.hkidNumber = hkidNumber;
	}

	public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}

	public int getExprience() {
		return exprience;
	}

	public void setExprience(int exprience) {
		this.exprience = exprience;
	}

	public String getRegistrationTime() {
		return registrationTime;
	}

	public void setRegistrationTime(String registrationTime) {
		this.registrationTime = registrationTime;
	}

	public float getRatingGrade() {
		return ratingGrade;
	}

	public void setRatingGrade(float ratingGrade) {
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

	public int getEducationStatus() {
		return educationStatus;
	}

	public void setEducationStatus(int educationStatus) {
		this.educationStatus = educationStatus;
	}

	public String getMajor() {
		return major;
	}

	public void setMajor(String major) {
		this.major = major;
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

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
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

	public String getResidentialAddress() {
		return residentialAddress;
	}

	public void setResidentialAddress(String residentialAddress) {
		this.residentialAddress = residentialAddress;
	}

	public int[] getCoursesValues() {
		return coursesValues;
	}

	public void setCoursesValues(int[] coursesValues) {
		this.coursesValues = coursesValues;
	}

	public int[] getAreaValues() {
		return areaValues;
	}

	public void setAreaValues(int[] areaValues) {
		this.areaValues = areaValues;
	}

	@Override
	public String toString() {
		return "UserInfo [hkidNumber=" + hkidNumber + ", exprience=" + exprience + ", registrationTime=" + registrationTime + ", ratingGrade=" + ratingGrade + ", bookmarkedCount=" + bookmarkedCount
				+ ", studentCount=" + studentCount + ", educationDegree=" + educationDegree + ", graduateSchool=" + graduateSchool + ", educationStatus=" + educationStatus + ", major=" + major
				+ ", introductionVideo=" + introductionVideo + ", grade=" + grade + ", id=" + id + ", nickName=" + nickName + ", userName=" + userName + ", phone=" + phone + ", email=" + email
				+ ", residentialAddress=" + residentialAddress + ", imid=" + imid + ", password=" + password + ", fbOpenID=" + fbOpenID + ", avatar=" + avatar + ", birth=" + birth + ", gender="
				+ gender + ", introduction=" + introduction + ", token=" + token + ", expirationTime=" + expirationTime + ", createdTime=" + createdTime + ", status=" + status + ", accountType="
				+ accountType + ", bookmarkID=" + bookmarkID + ", coursesValues=" + Arrays.toString(coursesValues) + ", areaValues=" + Arrays.toString(areaValues) + ", courses=" + courses
				+ ", areas=" + areas + ", timeslots=" + timeslots + "]";
	}

	public boolean isMatched() {
		return isMatched;
	}

	public void setMatched(boolean isMatched) {
		this.isMatched = isMatched;
	}

	public String getGradeName() {
		return gradeName;
	}

	public void setGradeName(String gradeName) {
		this.gradeName = gradeName;
	}

	public ArrayList<Timeslot> getAppointmentTimeslots() {
		return appointmentTimeslots;
	}

	public void setAppointmentTimeslots(ArrayList<Timeslot> appointmentTimeslots) {
		this.appointmentTimeslots = appointmentTimeslots;
	}

	public boolean isVoluntaryTutoring() {
		return isVoluntaryTutoring;
	}

	public void setVoluntaryTutoring(boolean isVoluntaryTutoring) {
		this.isVoluntaryTutoring = isVoluntaryTutoring;
	}

	public ArrayList<Integer> getServiceGradeValues() {
		return serviceGradeValues;
	}

	public void setServiceGradeValues(ArrayList<Integer> serviceGradeValues) {
		this.serviceGradeValues = serviceGradeValues;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public ArrayList<ServiceGrade> getServiceGrades() {
		return serviceGrades;
	}

	public void setServiceGrades(ArrayList<ServiceGrade> serviceGrades) {
		this.serviceGrades = serviceGrades;
	}

	public boolean isCertified() {
		return isCertified;
	}

	public void setCertified(boolean isCertified) {
		this.isCertified = isCertified;
	}

	public String getFollowCount() {
		return followCount;
	}

	public void setFollowCount(String followCount) {
		this.followCount = followCount;
	}

	public String getIdentityCode() {
		return identityCode;
	}

	public void setIdentityCode(String identityCode) {
		this.identityCode = identityCode;
	}

	public String getAlertTitle() {
		return alertTitle;
	}

	public void setAlertTitle(String alertTitle) {
		this.alertTitle = alertTitle;
	}

	public String getAlertContent() {
		return alertContent;
	}

	public void setAlertContent(String alertContent) {
		this.alertContent = alertContent;
	}

	public String getAlertLink() {
		return alertLink;
	}

	public void setAlertLink(String alertLink) {
		this.alertLink = alertLink;
	}

	public boolean isPhoneVerified() {
		return isPhoneVerified;
	}

	public void setPhoneVerified(boolean isPhoneVerified) {
		this.isPhoneVerified = isPhoneVerified;
	}
}
