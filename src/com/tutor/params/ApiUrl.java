package com.tutor.params;

/**
 * 网络请求api管理类
 * 
 * @author bruce.chen
 * 
 *         2015-8-20
 */
public class ApiUrl {

	/** 即时通讯相关 */
	public static final String XMPP_HOST = "192.168.0.187";
	public static final int XMPP_PORT = 5222;
	public static final String XMPP_SERVERNAME = "vm-server4";
	// ------------------------------------------------------------------
	/** api */
	public static final String DOMAIN = "http://192.168.0.187:8081/Tutor/";
	// http://192.168.0.187:8081/Tutor/
	// http://kevin-zhao.missionsky.com/Tutor.Api/
	/** 用戶註冊 */
	public static final String REGISTER = "api/v1/accounts/register";
	/** 用戶登錄 */
	public static final String LOGIN = "api/v1/accounts/login";
	/** 忘记密码 */
	public static final String FORGET_PASSWORD = "api/v1/password/forget";
	/** 修改密码 */
	public static final String CHANGE_PASSWORD = "api/v1/password/change";
	/** 用戶facebook登錄 */
	public static final String LOGIN_FACEBOOK = "api/v1/accounts/login/fb";
	/** 郵箱验证用户是否存在 */
	public static final String ACCOUNT_EXIST = "api/v1/accounts/exist";
	/** 註銷登錄 */
	public static final String LOGINOUT = "api/v1/accounts/logout";
	/** 上傳頭像 */
	public static final String UPLOAD_AVATAR = "api/v1/avatars/upload";
	/** 獲取他人頭像鏈接 直接接上id */
	public static final String GET_OTHER_AVATAR = "api/v1/avatars/download/";
	/** 獲取課程列表 */
	public static final String COURSELIST = "api/v1/course/list";
	/** 獲取區域列表 */
	public static final String AREALIST = "api/v1/area/list";
	/** 獲取時段列表 */
	public static final String TIMESLOTLIST = "api/v1/timeslot/list";
	/** 编辑辅导老师信息 */
	public static final String TUTORPROFILE = "api/v1/members/tutor/profile";
	/** 获取辅导老师信息 */
	public static final String TUTORINFO = "api/v1/members/tutor";
	/** 搜索辅导老师信息 */
	public static final String SEARCHTUTOR = "api/v1/members/tutor/search";
	/** 编辑学生信息 */
	public static final String STUDENTPROFILE = "api/v1/members/student/profile";
	/** 獲取学生信息 */
	public static final String STUDENTINFO = "api/v1/members/student";
	/** 關鍵字搜索学生信息 */
	public static final String SEARCHSTUDENT = "api/v1/members/student/search";
	/** 获取匹配的学生信息列表 */
	public static final String STUDENTMATCH = "api/v1/members/student/match";
	/** 获取匹配的老師信息列表 */
	public static final String TUTORMATCH = "api/v1/members/tutor/match";
	/** 获取我的學生列表 */
	public static final String MYSTUDENTLIST = "api/v1/notification/me/students";
	/** 获取消息列表 */
	public static final String NOTIFICATIONLIST = "api/v1/notification/list";
	/** 增加学生到收藏夹 */
	public static final String BOOTMARK_ADD_STUDENT = "api/v1/bookmark/student/%s/add";
	/** 获取收藏的学生列表 */
	public static final String BOOTMARK_GET_STUDENT_LIST = "api/v1/bookmark/student/list";
	/** 从收藏夹移除学生 */
	public static final String BOOTMARK_REMOVE_STUDENT = "api/v1/bookmark/student/remove/%s";
}
