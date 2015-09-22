package com.tutor.util;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateFormat;

/**
 * 日期和时间工具
 * 
 * @author chenyoubo
 * 
 */
@SuppressLint("SimpleDateFormat")
public class DateTimeUtil {

	/** 精确到毫秒 */
	public static final String FORMART = "yyyy-MM-dd HH:mm:ss SSS";
	/** 無間隔符號 */
	public static final String FORMART_YMDHMS = "yyyyMMddHHmmss";
	/** 精确到秒 */
	public static final String FORMART_2 = "yyyy-MM-dd hh:mm:ss";
	/** 年月日 */
	public static final String FORMART_YMD = "yyyy-MM-dd";

	/**
	 * 获取日期时间信息
	 * 
	 * @param format
	 *            日期时间样式 如:"kk:mm" ,yyyy年M月d日, yyyy-MM-dd-kk-mm-ss
	 *            详细请参考SimpleDateFormat 年-月-日-时-分-秒 刚方法可以频繁调用,不会严重延时.
	 */
	public static String getSystemDateTime(String format) {
		//
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(System.currentTimeMillis());
		CharSequence newTime = DateFormat.format(format, mCalendar);
		return (String) newTime;
	}

	public static String getCurDateStr() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		return c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH) + "-" + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":"
				+ c.get(Calendar.SECOND);
	}

	public static String getSystemDateTime(String format, long systemTime) {
		//
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(systemTime);
		CharSequence newTime = DateFormat.format(format, mCalendar);
		return (String) newTime;
	}

	/**
	 * 系统时间是否是24小时制
	 */
	public static boolean get24HourMode(final Context context) {
		return android.text.format.DateFormat.is24HourFormat(context);
	}

	/**
	 * 查看当前时间是上午还是下午
	 */
	public static String[] getTimeAMorPm() {
		/** 查看当前时间是上午还是下午 */
		String[] ampm = new DateFormatSymbols().getAmPmStrings();
		/*
		 * mAmString = ampm[0]; mPmString = ampm[1];
		 */
		return ampm;
	}

	static String[] weeks = { "周日", "周一", "周二", "周三", "周四", "周五", "周六", };

	/**
	 * 通过日期获取星期
	 * 
	 * @param date
	 * @return
	 */
	public static String getWeek(String date, String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.CHINA);
		long time = 0;
		try {
			time = dateFormat.parse(date).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar mCalendar = Calendar.getInstance();
		if (time != 0) {
			mCalendar.setTimeInMillis(time);
			int number = mCalendar.get(Calendar.DAY_OF_WEEK) - 1;
			return weeks[number];
		} else {
			return "";
		}
	}

	public static Date str2Date(String str, String format) {
		if (str == null || str.length() == 0) {
			return null;
		}
		if (format == null || format.length() == 0) {
			format = FORMART;
		}
		Date date = null;
		try {
			SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
			sdf.applyPattern(format);
			date = sdf.parse(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}

	public static String date2Str(Calendar c, String format) {
		if (c == null) {
			return null;
		}
		return date2Str(c.getTime(), format);
	}

	public static String date2Str(Date d, String format) {// yyyy-MM-dd HH:mm:ss
		if (d == null) {
			return null;
		}
		if (format == null || format.length() == 0) {
			format = FORMART;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String s = sdf.format(d);
		return s;
	}
}
