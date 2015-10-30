package com.tutor.model;

/**
 * @author ChenYouBo
 * @version 创建时间：2014-8-15 下午2:18:29
 * @see 类说明 : 日历实体类
 */
public class Clander
{
	private int year;
	private int month;
	private int day;
	private int lunarYear;
	private String lunarMonth;
	private String lunarDay;// 农历的天(包含节假日)
	private String lunarDay2; // 农历的天(没有包含节假日)
	private String animalsYear; // 生肖
	private String cyclical; // 天干地支
	private boolean isLeapyear; // 是否是闰年
	private String leapMonth; // 闰哪一个月

	public Clander()
	{

	}

	public int getYear()
	{
		return year;
	}

	public void setYear(int year)
	{
		this.year = year;
	}

	public int getMonth()
	{
		return month;
	}

	public void setMonth(int month)
	{
		this.month = month;
	}

	public int getDay()
	{
		return day;
	}

	public void setDay(int day)
	{
		this.day = day;
	}

	public int getLunarYear()
	{
		return lunarYear;
	}

	public void setLunarYear(int lunarYear)
	{
		this.lunarYear = lunarYear;
	}

	public String getLunarMonth()
	{
		return lunarMonth;
	}

	public void setLunarMonth(String lunarMonth)
	{
		this.lunarMonth = lunarMonth;
	}

	public String getLunarDay2()
	{
		return lunarDay2;
	}

	public void setLunarDay2(String lunarDay2)
	{
		this.lunarDay2 = lunarDay2;
	}

	public String getLunarDay()
	{
		return lunarDay;
	}

	public void setLunarDay(String lunarDay)
	{
		this.lunarDay = lunarDay;
	}

	public String getAnimalsYear()
	{
		return animalsYear;
	}

	public void setAnimalsYear(String animalsYear)
	{
		this.animalsYear = animalsYear;
	}

	public String getCyclical()
	{
		return cyclical;
	}

	public void setCyclical(String cyclical)
	{
		this.cyclical = cyclical;
	}

	public boolean isLeapyear()
	{
		return isLeapyear;
	}

	public void setLeapyear(boolean isLeapyear)
	{
		this.isLeapyear = isLeapyear;
	}

	public String getLeapMonth()
	{
		return leapMonth;
	}

	public void setLeapMonth(String leapMonth)
	{
		this.leapMonth = leapMonth;
	}

}
