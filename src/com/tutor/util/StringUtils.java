package com.tutor.util;

public class StringUtils {

	/**
	 * 请选择
	 */
	final static String PLEASE_SELECT = "请选择...";

	/**
	 * 通过jid返回用户名
	 * 
	 * @param jid
	 * @return
	 */
	public static String getUserNameByJid(String jid) {
		if (empty(jid)) {
			return null;
		}
		if (!jid.contains("@")) {
			return jid;
		}
		return jid.split("@")[0];
	}

	public static boolean empty(Object o) {
		return o == null || "".equals(o.toString().trim()) || "null".equalsIgnoreCase(o.toString().trim()) || "undefined".equalsIgnoreCase(o.toString().trim())
				|| PLEASE_SELECT.equals(o.toString().trim());
	}

	public static boolean notEmpty(Object o) {
		return o != null && !"".equals(o.toString().trim()) && !"null".equalsIgnoreCase(o.toString().trim()) && !"undefined".equalsIgnoreCase(o.toString().trim())
				&& !PLEASE_SELECT.equals(o.toString().trim());
	}
}
