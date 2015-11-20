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
	// public static final String XMPP_HOST = "192.168.0.187";
	// public static final int XMPP_PORT = 5222;
	// public static final String XMPP_SERVERNAME = "vm-server4";
	// 公网
	public static final String XMPP_HOST = "54.255.164.232";
	public static final int XMPP_PORT = 5222;
	public static final String XMPP_SERVERNAME = "win-iimo7nuvj0r";
	// ------------------------------------------------------------------
	/** api */
	public static final String DOMAIN = "http://54.255.164.232/TutorApi/";
	// http://192.168.0.187:8081/Tutor/ 本地
	// http://54.255.164.232/TutorApi/
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
	/** 编辑学生信息 */
	public static final String STUDENTPROFILE = "api/v1/members/student/profile";
	/** 獲取学生信息 */
	public static final String STUDENTINFO = "api/v1/members/student";
	/** 關鍵字搜索学生信息 */
	public static final String SEARCHSTUDENT = "api/v1/members/student/search";
	/** 获取匹配的学生信息列表 */
	public static final String STUDENTMATCH = "api/v1/members/student/match";
	/** 根据综合评分排序获取老师信息列表 */
	public static final String TUTORRATING = "api/v1/members/tutor/orderbyrating";
	/** 根据学生数量排序获取老师列表 */
	public static final String TUTORPOPULARITY = "api/v1/members/tutor/orderbypopularity";
	/** 搜索老师 */
	public static final String SEARCHTUTOR = "api/v1/members/tutor/search";
	/** 获取我的學生列表 */
	public static final String MYSTUDENTLIST = "api/v1/notification/me/students";
	/** 获取消息列表 */
	public static final String NOTIFICATIONLIST = "api/v1/notification/list";
	/** 增加学生到收藏夹 */
	public static final String BOOTMARK_ADD_STUDENT = "api/v1/bookmark/student/%s/add";
	/** 增加老师到收藏夹 */
	public static final String BOOTMARK_ADD_TUTOR = "api/v1/bookmark/tutor/%s/add";
	/** 获取收藏的学生列表 */
	public static final String BOOTMARK_GET_STUDENT_LIST = "api/v1/bookmark/student/list";
	/** 获取收藏的老师列表 */
	public static final String BOOTMARK_GET_TUTOR_LIST = "api/v1/bookmark/tutor/list";
	/** 从收藏夹移除学生 */
	public static final String BOOTMARK_REMOVE_STUDENT = "api/v1/bookmark/student/remove/%s";
	/** 从收藏夹移除老师 */
	public static final String BOOTMARK_REMOVE_TUTOR = "api/v1/bookmark/tutor/remove/%s";
	/** 接受或者拒绝邀请成为学生 */
	public static final String NOTIFICATION_UPDATE = "api/v1/notification/update";
	/** 获取币种 */
	public static final String CURRENCYS = "api/v1/config/currencys";
	/** 邀请成为我的学生 (发送者：辅导老师) */
	public static final String TO_BE_MY_STUDENT = "api/v1/notification/student";
	/** 邀请成为我的辅导老师 (发送者：学生) */
	public static final String TO_BE_MY_TUTOR = "api/v1/notification/tutor";
	/** 获取我的老师列表 */
	public static final String MYTUTORLIST = "api/v1/notification/me/tutors";
	/** 增加对老师的评价 */
	public static final String RATING = "api/v1/rating/add/%s";
	/** 获取对某老师的评论列表 */
	public static final String RATING_COMMENT_LIST = "api/v1/rating/%s/list";
	/** 获取年级 */
	public static final String GRADE = "api/v1/config/grades";
	/** 檢查新版本 */
	public static final String UPDATEVERSION = "api/v1/config/appversion/android";
	/** 根据imid 获取用户信息 */
	public static final String IM_GET_INFO = "api/v1/members/im/%s";
	/** 海外求学-获取年级 */
	public static final String STUDY_ABROAD_GRADES = "api/v1/config/abroadgrades";
	/** 海外求学-获取国家 */
	public static final String STUDY_ABROAD_STATES = "api/v1/config/states";
	/** 海外求学-根据年级和国家查询 */
	public static final String STUDY_ABROAD_COURSE_QUERY = "api/v1/studyabroad/course";
	/** 海外求学-FAQ */
	public static final String STUDY_ABROAD_FAQ = "api/v1/studyabroad/faq";
	/** 海外求学-求学顾问 */
	public static final String STUDY_ABROAD_ADVISORY = "api/v1/studyabroad/advisory";
	/** 海外求学-登记聊天会员信息:已经聊过天了就调用这个API */
	public static final String STUDY_ABROAD_LOGCHAT = "api/v1/studyabroad/logchat";
	/** 海外求学-获取海外求学顾问的IMIds */
	public static final String STUDY_ABROAD_IM_IDS = "api/v1/config/studyabroadIMids";
	/** 时间表 */
	public static final String TIME_TABLE = "api/v1/notification/timetables";
	/** 时间表更新 */
	public static final String TIME_TABLE_UPDATE = "api/v1/notification/timetables/%s";
	/** 检查IMID是否在openfire服务器存在，如果存在返回true */
	public static final String IM_ID_VALID = "api/v1/members/im/exist";
	/** 记录日志信息 */
	public static final String LOG = "api/v1/log/loginfo";
	/** 获取活动(按月,1-12) */
	public static final String ACTIVITIES = "api/v1/notification/activities";
}
