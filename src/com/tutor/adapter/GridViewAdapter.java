package com.tutor.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.hk.tutor.R;
import com.tutor.model.Clander;
import com.tutor.model.EditTimeslot;
import com.tutor.model.TimeTable;
import com.tutor.model.TimeTableTag;
import com.tutor.util.LunarCalendar;
import com.tutor.util.SpecialCalendar;

/**
 * @author ChenYouBo
 * @version 创建时间：2014-8-5 下午6:30:35
 * @see 类说明 :适配器
 */
public class GridViewAdapter extends BaseAdapter {

	private LayoutInflater lif;
	private Resources res;
	// 每一项的宽高
	private int width;
	private boolean isLeapyear = false; // 是否为闰年
	private int daysOfMonth = 0; // 某月的天数
	private int dayOfWeek = 0; // 具体某一天是星期几
	private int lastDaysOfMonth = 0; // 上一个月的总天数
	/** 一个gridview中的日期存入此集合中 */
	private List<Clander> dayNumber = new ArrayList<Clander>();
	private int dayNumberSize = 42;
	// 两个工具对象
	private SpecialCalendar sc = null;
	private LunarCalendar lc = null;
	private ArrayList<TimeTableTag> tags = new ArrayList<TimeTableTag>();
	private String week_c;// 当前日期是星期几
	private static String week[] = null;
	// 系统当前时间
	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
	private String sysDate = "";
	private String sys_year = "";
	private String sys_month = "";
	private String sys_day = "";
	private Context context;
	private ArrayList<TimeTable> timeTables;

	/**
	 * 构造器
	 * 
	 * @param context
	 * @param itemWidth
	 */
	public GridViewAdapter(Context context, int itemWidth) {
		this.context = context;
		res = context.getResources();
		lif = LayoutInflater.from(context);
		width = itemWidth;
		sc = new SpecialCalendar();
		lc = new LunarCalendar();
		week = context.getResources().getStringArray(R.array.time_table_weeks);
	}

	/**
	 * 初始化系统时间
	 */
	private void initSysData() {
		Date date = new Date();
		sysDate = sdf.format(date); // 当期日期
		sys_year = sysDate.split("-")[0];
		sys_month = sysDate.split("-")[1];
		sys_day = sysDate.split("-")[2];
	}

	/**
	 * 设置time table 数据
	 * 
	 * @param timeTables
	 */
	public void setTimeTableData(ArrayList<TimeTable> timeTables) {
		this.timeTables = timeTables;
		notifyDataSetChanged();
	}

	/**
	 * 在实例化本类对象之后,必须先调用此方法来初始化本类中的成员变量
	 */
	public void setClanderData(int jumpMonth, int jumpYear, int year_c, int month_c, int day_c) {// （jumpMonth为滑动的次数，每滑动一次就增加一月或减一月）
		initSysData();
		int stepYear = year_c + jumpYear;
		int stepMonth = month_c + jumpMonth;
		if (stepMonth > 0) {
			// 往下一个月滑动
			if (stepMonth % 12 == 0) {
				stepYear = year_c + stepMonth / 12 - 1;
				stepMonth = 12;
			} else {
				stepYear = year_c + stepMonth / 12;
				stepMonth = stepMonth % 12;
			}
		} else {
			// 往上一个月滑动
			stepYear = year_c - 1 + stepMonth / 12;
			stepMonth = stepMonth % 12 + 12;
		}
		getCalendar(stepYear, stepMonth);
	}

	// 得到某年的某月的天数且这月的第一天是星期几
	public void getCalendar(int year, int month) {
		if (year > 2049 || year < 1901) {
			Toast.makeText(context, "所选的年份超过了范围!", Toast.LENGTH_LONG).show();
			return;
		}
		isLeapyear = sc.isLeapYear(year); // 是否为闰年
		daysOfMonth = sc.getDaysOfMonth(isLeapyear, month); // 某月的总天数
		dayOfWeek = sc.getWeekdayOfMonth(year, month); // 某月第一天为星期几
		lastDaysOfMonth = sc.getDaysOfMonth(isLeapyear, month - 1); // 上一个月的总天数
		getweek(year, month);
	}

	// 将一个月中的每一天的值添加入数组dayNuber中
	private void getweek(int year, int month) {
		dayNumber.clear();
		int j = 1;
		String lunarDay = ""; // 农历
		// 得到当前月的所有日程日期(这些日期需要标记)
		for (int i = 0; i < dayNumberSize; i++) {
			Clander clander = new Clander();
			if (i < dayOfWeek) { // 前一个月
				int temp = lastDaysOfMonth - dayOfWeek + 1;
				lunarDay = lc.getLunarDate(year, month - 1, temp + i, false);
				clander.setDay(temp + i);
				clander.setLunarDay(lunarDay);
				dayNumber.add(clander);
			} else if (i < daysOfMonth + dayOfWeek) { // 本月
				String day = String.valueOf(i - dayOfWeek + 1); // 得到的日期
				lunarDay = lc.getLunarDate(year, month, i - dayOfWeek + 1, false);
				clander.setDay(i - dayOfWeek + 1);
				clander.setLunarDay(lunarDay);
				clander.setLunarDay2(lc.getLunarDate(year, month, i - dayOfWeek + 1, true));
				// 对于当前月才去标记当前日期
				if (sys_year.equals(String.valueOf(year)) && sys_month.equals(String.valueOf(month)) && sys_day.equals(day)) {
					week_c = week[i % 7]; // 31
					tags.clear();
					tags.add(new TimeTableTag(i, week_c, false));
					switch (i % 7) {
						case 0:
							tags.add(new TimeTableTag(i + 1, week[1], false));
							tags.add(new TimeTableTag(i + 2, week[2], false));
							tags.add(new TimeTableTag(i + 3, week[3], false));
							tags.add(new TimeTableTag(i + 4, week[4], false));
							tags.add(new TimeTableTag(i + 5, week[5], false));
							tags.add(new TimeTableTag(i + 6, week[6], false));
							break;
						case 1:
							tags.add(new TimeTableTag(i - 1, week[0], false));
							tags.add(new TimeTableTag(i + 1, week[2], false));
							tags.add(new TimeTableTag(i + 2, week[3], false));
							tags.add(new TimeTableTag(i + 3, week[4], false));
							tags.add(new TimeTableTag(i + 4, week[5], false));
							tags.add(new TimeTableTag(i + 5, week[6], false));
							break;
						case 2:
							tags.add(new TimeTableTag(i - 2, week[0], false));
							tags.add(new TimeTableTag(i - 1, week[1], false));
							tags.add(new TimeTableTag(i + 1, week[3], false));
							tags.add(new TimeTableTag(i + 2, week[4], false));
							tags.add(new TimeTableTag(i + 3, week[5], false));
							tags.add(new TimeTableTag(i + 4, week[6], false));
							break;
						case 3:
							tags.add(new TimeTableTag(i - 3, week[0], false));
							tags.add(new TimeTableTag(i - 2, week[1], false));
							tags.add(new TimeTableTag(i - 1, week[2], false));
							tags.add(new TimeTableTag(i + 1, week[4], false));
							tags.add(new TimeTableTag(i + 2, week[5], false));
							tags.add(new TimeTableTag(i + 3, week[6], false));
							break;
						case 4:
							tags.add(new TimeTableTag(i - 4, week[0], false));
							tags.add(new TimeTableTag(i - 3, week[1], false));
							tags.add(new TimeTableTag(i - 2, week[2], false));
							tags.add(new TimeTableTag(i - 1, week[3], false));
							tags.add(new TimeTableTag(i + 1, week[5], false));
							tags.add(new TimeTableTag(i + 2, week[6], false));
							break;
						case 5:
							tags.add(new TimeTableTag(i - 5, week[0], false));
							tags.add(new TimeTableTag(i - 4, week[1], false));
							tags.add(new TimeTableTag(i - 3, week[2], false));
							tags.add(new TimeTableTag(i - 2, week[3], false));
							tags.add(new TimeTableTag(i - 1, week[4], false));
							tags.add(new TimeTableTag(i + 1, week[6], false));
							break;
						case 6:
							tags.add(new TimeTableTag(i - 6, week[0], false));
							tags.add(new TimeTableTag(i - 5, week[1], false));
							tags.add(new TimeTableTag(i - 4, week[2], false));
							tags.add(new TimeTableTag(i - 3, week[3], false));
							tags.add(new TimeTableTag(i - 2, week[4], false));
							tags.add(new TimeTableTag(i - 1, week[5], false));
							break;
					}
					if (timeTables != null && timeTables.size() > 0) {
						for (int k = 0; k < timeTables.size(); k++) {
							ArrayList<EditTimeslot> timeslots = timeTables.get(k).getTimeslots();
							if (timeslots != null && timeslots.size() > 0) {
								for (int m = 0; m < timeslots.size(); m++) {
									for (int n = 0; n < tags.size(); n++) {
										int dayOfWeek = timeslots.get(m).getDayOfWeek();
										if (week[(dayOfWeek + 1) % 7].equals(tags.get(n).getWeek())) {
											tags.get(n).setTag(true);
										}
									}
								}
							}
						}
					}
				}
				// 标记本月的日程
				clander.setLeapyear(isLeapyear);
				clander.setYear(year);
				clander.setMonth(month);
				clander.setAnimalsYear(lc.animalsYear(year));
				clander.setLeapMonth(lc.leapMonth == 0 ? "" : String.valueOf(lc.leapMonth));
				clander.setCyclical(lc.cyclical(year));
				clander.setLunarYear(lc.getYear());
				clander.setLunarMonth(lc.getLunarMonth());
				dayNumber.add(clander);
			} else { // 下一个月
				lunarDay = lc.getLunarDate(year, month + 1, j, false);
				clander.setDay(j);
				clander.setLunarDay(lunarDay);
				dayNumber.add(clander);
				j++;
			}
			clander = null;
		}
	}

	@Override
	public int getCount() {
		return dayNumber.size();
	}

	@Override
	public Object getItem(int arg0) {
		return dayNumber.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int arg0, View view, ViewGroup arg2) {
		ViewHelper helper = null;
		if (view == null) {
			helper = new ViewHelper();
			view = lif.inflate(R.layout.clander_gridview_item, null);
			view.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, width));
			helper.day = (TextView) view.findViewById(R.id.clander_gridview_item_tv_day);
			view.setTag(helper);
		} else {
			helper = (ViewHelper) view.getTag();
		}
		helper.day.setText(dayNumber.get(arg0).getDay() + "");
		// 设置背景
		if (arg0 < daysOfMonth + dayOfWeek && arg0 >= dayOfWeek) { // 当前月的信息
			/*
			 * if (arg0 == currentFlag) {// 当前
			 * view.setBackgroundResource(R.drawable.current_day_bgc); } else
			 */// if (arg0 == clickedData && arg0 != currentFlag) {// 点击并不是当前
				// view.setBackgroundColor(res.getColor(R.color.grid_item_seleted));
			// } else {
			view.setBackgroundColor(res.getColor(R.color.btn_normal));
			// }
			// helper.lunar.setTextColor(res.getColor(R.color.white));
			// 设置周六周日日期的字体颜色为红色
			// if (arg0 % 7 == 0 || arg0 % 7 == 6)
			// {
			// helper.day.setTextColor(res.getColor(R.color.red));
			// }
			// else
			// {
			// helper.day.setTextColor(res.getColor(R.color.white));
			// }
			helper.day.setTextColor(res.getColor(R.color.white));
		} else { // 其他月
					// 背景灰色
			view.setBackgroundColor(res.getColor(R.color.gray));
			// 字体白色
			helper.day.setTextColor(res.getColor(R.color.dark_gray));
		}
		if (tags != null && tags.size() > 0) {
			for (TimeTableTag tag : tags) {
				if (tag.isTag() && arg0 == tag.getId()) {
					view.setBackgroundColor(res.getColor(R.color.yellow_normal));
				}
			}
		}
		return view;
	}

	public void upData() {
		this.notifyDataSetChanged();
	}

	public void setClickedItem(int index) {
		if (index < daysOfMonth + dayOfWeek && index >= dayOfWeek) {// 只有本月的可以点击
			if (tags != null && tags.size() > 0) {
				for (TimeTableTag tag : tags) {
					if (tag.isTag() && index == tag.getId()) {
						for (int k = 0; k < timeTables.size(); k++) {
							ArrayList<EditTimeslot> timeslots = timeTables.get(k).getTimeslots();
							if (timeslots != null && timeslots.size() > 0) {
								for (int m = 0; m < timeslots.size(); m++) {
									for (int n = 0; n < tags.size(); n++) {
										int dayOfWeek = timeslots.get(m).getDayOfWeek();
										if (week[(1 + dayOfWeek) % 7].equals(tag.getWeek())) {
											if (onTimeTableClickListener != null) {
												onTimeTableClickListener.onTimeTableClick(week[(1 + dayOfWeek) % 7], timeTables);
												return;
											}
										}
									}
								}
							}
						}
						break;
					}
				}
			}
			this.notifyDataSetChanged();
		}
	}

	public String getShowYear() {
		return dayNumber.get(dayOfWeek).getYear() + "";
	}

	public String getShowMonth() {
		return dayNumber.get(dayOfWeek).getMonth() + "";
	}

	class ViewHelper {

		TextView day;
	}

	public interface OnTimeTableClickListener {

		public void onTimeTableClick(String week, ArrayList<TimeTable> timeTables);
	}

	public OnTimeTableClickListener onTimeTableClickListener;

	/**
	 * 点击time table 回调
	 * 
	 * @param onTimeTableClickListener
	 */
	public void setOnTimeTableClickListener(OnTimeTableClickListener onTimeTableClickListener) {
		this.onTimeTableClickListener = onTimeTableClickListener;
	}
}
