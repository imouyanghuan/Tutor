package com.tutor.util;

import android.content.Context;

/**
 * @author ChenYouBo
 * @version 创建时间：2015-5-19 上午10:30:14
 * @see 类说明 :
 */
public class ScreenUtil {

	/**
	 * 获取屏幕的宽
	 * 
	 * @param context
	 * @return
	 */
	public static int getSW(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	/**
	 * 获取屏幕的高
	 * 
	 * @param context
	 * @return
	 */
	public static int getSH(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}

	/**
	 * 将dip转换成px
	 * 
	 * @param context
	 * @param defDip
	 * @return
	 */
	public static int dip2Px(Context context, float defDip) {
		float f = context.getResources().getDisplayMetrics().density;
		return (int) (defDip * f + 0.5f);
	}

	/**
	 * 将px转换成dip
	 * 
	 * @param context
	 * @param defDip
	 * @return
	 */
	public static int px2Dip(Context context, float defPx) {
		float f = context.getResources().getDisplayMetrics().density;
		return (int) (defPx / f + 0.5f);
	}
}
