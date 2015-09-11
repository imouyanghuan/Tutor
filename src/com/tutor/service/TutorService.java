package com.tutor.service;

import java.io.IOException;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.tutor.TutorApplication;
import com.tutor.model.AreaListResult;
import com.tutor.model.CourseListResult;
import com.tutor.model.MatchStudentListResult;
import com.tutor.model.NotificationListResult;
import com.tutor.model.TimeSlotListResult;
import com.tutor.params.ApiUrl;
import com.tutor.params.Constants;
import com.tutor.util.CheckTokenUtils;

/**
 * @author bruce.chen
 * 
 *         2015-9-1
 */
public class TutorService extends TutorBaseService {

	private static TutorService service;

	private TutorService() {}

	public static TutorService getService() {
		if (null == service) {
			service = new TutorService();
		}
		return service;
	}

	/**
	 * 獲取課程列表
	 * 
	 * @return
	 */
	public CourseListResult getCourseList() {
		try {
			CourseListResult result = executeEntityGet(ApiUrl.COURSELIST, TutorApplication.getDefaultGetParams(), CourseListResult.class);
			CheckTokenUtils.checkToken(result);
			return result;
		} catch (HttpException e) {
			e.printStackTrace();
			if (Constants.General.UNAUTHORIZED.equals(e.getMessage())) {
				CheckTokenUtils.reLogin();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 獲取地區列表
	 * 
	 * @return
	 */
	public AreaListResult getAreaList() {
		try {
			AreaListResult result = executeEntityGet(ApiUrl.AREALIST, TutorApplication.getDefaultGetParams(), AreaListResult.class);
			CheckTokenUtils.checkToken(result);
			return result;
		} catch (HttpException e) {
			e.printStackTrace();
			if (Constants.General.UNAUTHORIZED.equals(e.getMessage())) {
				CheckTokenUtils.reLogin();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 獲取可用時間列表
	 * 
	 * @return
	 */
	public TimeSlotListResult getTimeSlotList(int memberId, int pageIndex, int pageSize) {
		try {
			RequestParams params = TutorApplication.getDefaultGetParams();
			params.addQueryStringParameter("memberId", "" + memberId);
			params.addQueryStringParameter("pageIndex", "" + pageIndex);
			params.addQueryStringParameter("pageSize", "" + pageSize);
			TimeSlotListResult result = executeEntityGet(ApiUrl.TIMESLOTLIST, params, TimeSlotListResult.class);
			CheckTokenUtils.checkToken(result);
			return result;
		} catch (HttpException e) {
			e.printStackTrace();
			if (Constants.General.UNAUTHORIZED.equals(e.getMessage())) {
				CheckTokenUtils.reLogin();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 獲取未讀消息數量 Sent 0 Accept 1 Reject 2 Acknowle 3 All 4
	 * 
	 * @return
	 */
	public NotificationListResult getNotificationCount() {
		RequestParams params = TutorApplication.getDefaultGetParams();
		params.addQueryStringParameter("status", "1");
		params.addQueryStringParameter("pageIndex", "0");
		params.addQueryStringParameter("pageSize", "1");
		try {
			NotificationListResult result = executeEntityGet(ApiUrl.NOTIFICATIONLIST, params, NotificationListResult.class);
			CheckTokenUtils.checkToken(result);
			return result;
		} catch (HttpException e) {
			e.printStackTrace();
			if (Constants.General.UNAUTHORIZED.equals(e.getMessage())) {
				CheckTokenUtils.reLogin();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 獲取匹配的學生列表
	 * 
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public MatchStudentListResult getMatchStudentList(String pageIndex, String pageSize) {
		RequestParams params = TutorApplication.getDefaultGetParams();
		params.addQueryStringParameter("pageIndex", pageIndex);
		params.addQueryStringParameter("pageSize", pageSize);
		try {
			MatchStudentListResult result = executeEntityGet(ApiUrl.STUDENTMATCH, params, MatchStudentListResult.class);
			CheckTokenUtils.checkToken(result);
			return result;
		} catch (HttpException e) {
			e.printStackTrace();
			if (Constants.General.UNAUTHORIZED.equals(e.getMessage())) {
				CheckTokenUtils.reLogin();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 通過關鍵字搜索學生
	 * 
	 * @param keyWord
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public MatchStudentListResult geSearchStudentList(String keyWord, String pageIndex, String pageSize) {
		MatchStudentListResult result = null;
		RequestParams params = TutorApplication.getDefaultGetParams();
		params.addQueryStringParameter("keywords", keyWord);
		params.addQueryStringParameter("pageIndex", pageIndex);
		params.addQueryStringParameter("pageSize", pageSize);
		try {
			result = executeEntityGet(ApiUrl.SEARCHSTUDENT, params, MatchStudentListResult.class);
			CheckTokenUtils.checkToken(result);
		} catch (HttpException e) {
			e.printStackTrace();
			if (Constants.General.UNAUTHORIZED.equals(e.getMessage())) {
				CheckTokenUtils.reLogin();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 獲取我的學生列表
	 * 
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public MatchStudentListResult getMyStudentList(String pageIndex, String pageSize) {
		MatchStudentListResult result = null;
		RequestParams params = TutorApplication.getDefaultGetParams();
		params.addQueryStringParameter("pageIndex", pageIndex);
		params.addQueryStringParameter("pageSize", pageSize);
		try {
			result = executeEntityGet(ApiUrl.MYSTUDENTLIST, params, MatchStudentListResult.class);
			CheckTokenUtils.checkToken(result);
		} catch (HttpException e) {
			e.printStackTrace();
			if (Constants.General.UNAUTHORIZED.equals(e.getMessage())) {
				CheckTokenUtils.reLogin();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}
