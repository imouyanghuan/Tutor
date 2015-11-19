package com.tutor.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hk.tutor.R;
import com.tutor.model.ActivityFlag;
import com.tutor.model.ActivityModel;
import com.tutor.model.ActivityWithWeek;
import com.tutor.model.Clander;
import com.tutor.model.TimeTable;
import com.tutor.model.TimeTableDetail;
import com.tutor.model.TimeTableTag;
import com.tutor.params.Constants;
import com.tutor.ui.activity.ActivitiesDetailActivity;
import com.tutor.util.LunarCalendar;
import com.tutor.util.ScreenUtil;
import com.tutor.util.SpecialCalendar;

/**
 * @author ChenYouBo
 * @version 创建时间：2014-8-5 下午6:30:35
 * @see 类说明 :适配器
 */
@SuppressLint("SimpleDateFormat")
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
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
	private SimpleDateFormat sdfAcitivity = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private String sysDate = "";
	private String curYear = "";
	private String curMonth = "";
	private String curDay = "";
	private Context context;
	private ArrayList<TimeTable> timeTables;
	private ArrayList<ActivityModel> activities;
	private ArrayList<ActivityFlag> flags;

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
		curYear = sysDate.split("-")[0];
		curMonth = sysDate.split("-")[1];
		curDay = sysDate.split("-")[2];
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
	 * 设置activity 数据
	 * 
	 * @param activities
	 */
	public void setActivityData(ArrayList<ActivityModel> activities, int year, int month) {
		this.activities = activities;
		notifyDataSetChanged();
		getActivities(year, month);
	}

	// 获取标记活动
	@SuppressWarnings("deprecation")
	private void getActivities(int year, int month) {
		initSysData();
		isLeapyear = sc.isLeapYear(year); // 是否为闰年
		daysOfMonth = sc.getDaysOfMonth(isLeapyear, month); // 某月的总天数
		dayOfWeek = sc.getWeekdayOfMonth(year, month); // 某月第一天为星期几
		lastDaysOfMonth = sc.getDaysOfMonth(isLeapyear, month - 1); // 上一个月的总天数
		dayNumber.clear();
		int j = 1;
		String lunarDay = ""; // 农历
		// 得到当前月的所有日程日期(这些日期需要标记)
		flags = new ArrayList<ActivityFlag>();
		for (int i = 0; i < dayNumberSize; i++) {
			Clander clander = new Clander();
			if (i < dayOfWeek) { // 前一个月
				int temp = lastDaysOfMonth - dayOfWeek + 1;
				lunarDay = lc.getLunarDate(year, month - 1, temp + i, false);
				clander.setDay(temp + i);
				clander.setLunarDay(lunarDay);
				dayNumber.add(clander);
			} else if (i < daysOfMonth + dayOfWeek) { // 本月
				int day = i - dayOfWeek + 1; // 得到的日期
				// 把当前position加入到flag里面
				ActivityFlag flag = new ActivityFlag();
				flag.setId(i);
				flag.setDay(day);
				flags.add(flag);
				// ///////////////////////////
				lunarDay = lc.getLunarDate(year, month, i - dayOfWeek + 1, false);
				clander.setDay(i - dayOfWeek + 1);
				clander.setLunarDay(lunarDay);
				clander.setLunarDay2(lc.getLunarDate(year, month, i - dayOfWeek + 1, true));
				//
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
		if (activities != null && activities.size() > 0) {
			for (int i = 0; i < activities.size(); i++) {
				String heldDate = activities.get(i).getHeldDate();
				Date date = null;
				try {
					date = sdfAcitivity.parse(heldDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				int curDay = date.getDate();
				if (flags != null && flags.size() > 0) {
					for (int f = 0; f < flags.size(); f++) {
						int day = flags.get(f).getDay();
						if (day == curDay) {
							flags.get(f).setFlag(true);
							flags.get(f).setActivityModel(activities.get(i));
							break;
						}
					}
				}
			}
		}
	}

	// 得到某年的某月的天数且这月的第一天是星期几
	public void getCalendar(int year, int month) {
		initSysData();
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
				//
				// 当前日期
				if (curYear.equals(String.valueOf(year)) && curMonth.equals(String.valueOf(month)) && curDay.equals(day)) {
					week_c = week[i % 7]; // 31
					tags.clear();
					// 把当前日期所在的一周都加入到tags里面
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
							ArrayList<TimeTableDetail> timeslots = timeTables.get(k).getTimeslots();
							if (timeslots != null && timeslots.size() > 0) {
								for (int m = 0; m < timeslots.size(); m++) {
									for (int n = 0; n < tags.size(); n++) {
										int dayOfWeek = timeslots.get(m).getDayOfWeek();
										// 如果时间段里面的星期和标记里面的星期一致，则标记为true
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
	public View getView(final int arg0, View view, ViewGroup arg2) {
		ViewHelper helper = null;
		if (view == null) {
			helper = new ViewHelper();
			view = lif.inflate(R.layout.clander_gridview_item, null);
			view.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, width));
			helper.day = (TextView) view.findViewById(R.id.clander_gridview_item_tv_day);
			helper.ibNote = (ImageView) view.findViewById(R.id.ib_note);
			view.setTag(helper);
		} else {
			helper = (ViewHelper) view.getTag();
		}
		helper.day.setText(dayNumber.get(arg0).getDay() + "");
		// 设置背景
		if (arg0 < daysOfMonth + dayOfWeek && arg0 >= dayOfWeek) { // 当前月的信息
			view.setBackgroundColor(res.getColor(R.color.btn_normal));
			helper.day.setTextColor(res.getColor(R.color.white));
		} else { // 其他月
					// 背景灰色
			view.setBackgroundColor(res.getColor(R.color.gray));
			// 字体白色
			helper.day.setTextColor(res.getColor(R.color.dark_gray));
		}
		view.setOnClickListener(null);
		final ActivityWithWeek aww = new ActivityWithWeek(false);
		// 已经被标记，并且当前position和标记的position一致，设置其背景颜色为黄色
		if (tags != null && tags.size() > 0) {
			for (TimeTableTag tag : tags) {
				if (tag.isTag() && arg0 == tag.getId()) {
					view.setBackgroundColor(res.getColor(R.color.yellow_normal));
					aww.setYellow(true);
					Loop: for (int k = 0; k < timeTables.size(); k++) {
						ArrayList<TimeTableDetail> timeslots = timeTables.get(k).getTimeslots();
						if (timeslots != null && timeslots.size() > 0) {
							for (TimeTableDetail detail : timeslots) {
								int dayOfWeek = detail.getDayOfWeek();
								if (week[(1 + dayOfWeek) % 7].equals(tag.getWeek())) {
									aww.setTimeTables(timeTables);
									aww.setWeek(week[(1 + dayOfWeek) % 7]);
									break Loop;
								}
							}
						}
					}
					view.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							if (onTimeTableClickListener != null) {
								onTimeTableClickListener.onTimeTableClick(aww.getWeek(), aww.getTimeTables());
							}
						}
					});
					break;
				}
			}
		}
		// 已经被标记，并且当前position和标记的position一致，显示小旗子
		if (flags != null && flags.size() > 0) {
			for (final ActivityFlag flag : flags) {
				if (flag.isFlag() && arg0 == flag.getId()) {
					helper.ibNote.setVisibility(View.VISIBLE);
					if (aww.isYellow()) {
						//
						view.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								final AlertDialog mAlertDialog = new AlertDialog.Builder(context).create();
								mAlertDialog.show();
								Window mWindow = mAlertDialog.getWindow();
								mWindow.setContentView(R.layout.layout_time_table_sheet);
								mWindow.setGravity(Gravity.BOTTOM);
								mWindow.setLayout(ScreenUtil.getSW(context), LayoutParams.WRAP_CONTENT);
								// time table
								mWindow.findViewById(R.id.btn_time_table).setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										//
										if (onTimeTableClickListener != null) {
											onTimeTableClickListener.onTimeTableClick(aww.getWeek(), aww.getTimeTables());
										}
										mAlertDialog.cancel();
									}
								});
								// activitiew
								mWindow.findViewById(R.id.btn_activities).setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										if (activities != null) {
											int today = flag.getDay();
											Intent intent = new Intent(context, ActivitiesDetailActivity.class);
											intent.putExtra(Constants.General.ACTIVITIES, activities);
											intent.putExtra(Constants.General.TODAY, today);
											intent.putExtra(Constants.General.MONTH, curMonth);
											intent.putExtra(Constants.General.YEAR, curYear);
											context.startActivity(intent);
										}
										mAlertDialog.cancel();
									}
								});
								// 取消
								mWindow.findViewById(R.id.btn_cancel).setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										mAlertDialog.cancel();
									}
								});
							}
						});
					} else {
						view.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								int today = flag.getDay();
								Intent intent = new Intent(context, ActivitiesDetailActivity.class);
								intent.putExtra(Constants.General.ACTIVITIES, activities);
								intent.putExtra(Constants.General.TODAY, today);
								intent.putExtra(Constants.General.MONTH, curMonth);
								intent.putExtra(Constants.General.YEAR, curYear);
								context.startActivity(intent);
							}
						});
					}
					break;
				}
			}
		}
		return view;
	}

	public void upData() {
		this.notifyDataSetChanged();
	}

	// public void setClickedItem(int index) {
	// // if (index < daysOfMonth + dayOfWeek && index >= dayOfWeek) {//
	// 只有本月的可以点击
	// // if (tags != null && tags.size() > 0) {
	// // for (TimeTableTag tag : tags) {
	// // if (tag.isTag() && index == tag.getId()) {
	// for (int k = 0; k < timeTables.size(); k++) {
	// ArrayList<TimeTableDetail> timeslots = timeTables.get(k).getTimeslots();
	// if (timeslots != null && timeslots.size() > 0) {
	// for (int m = 0; m < timeslots.size(); m++) {
	// // for (int n = 0; n < tags.size(); n++) {
	// int dayOfWeek = timeslots.get(m).getDayOfWeek();
	// if (week[(1 + dayOfWeek) % 7].equals(tag.getWeek())) {
	// if (onTimeTableClickListener != null) {
	// onTimeTableClickListener.onTimeTableClick(week[(1 + dayOfWeek) % 7],
	// timeTables);
	// return;
	// }
	// }
	// // }
	// }
	// }
	// }
	// // break;
	// // }
	// // }
	// // }
	// //this.notifyDataSetChanged();
	// // }
	// }
	public String getShowYear() {
		return dayNumber.get(dayOfWeek).getYear() + "";
	}

	public String getShowMonth() {
		return dayNumber.get(dayOfWeek).getMonth() + "";
	}

	class ViewHelper {

		TextView day;
		ImageView ibNote;
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
