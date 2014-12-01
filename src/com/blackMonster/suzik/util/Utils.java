package com.blackMonster.suzik.util;

public class Utils {

	public static String formatPhoneNumberForJson(String number) {
		if (number==null) return null;
		return number.replace("+", "%2B");
	}

}
