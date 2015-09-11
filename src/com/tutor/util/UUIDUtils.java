package com.tutor.util;

import java.util.UUID;

public class UUIDUtils {

	public static String getUUIDString() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}

	public static String getID(int len) {
		UUID uuid = UUID.randomUUID();
		return uuid.toString().replace("-", "").substring(0, len);
	}

	public static String getID(int start, int end) {
		UUID uuid = UUID.randomUUID();
		return uuid.toString().replace("-", "").substring(start, end);
	}
}
