package com.blackMonster.contacts;

public class ContactspublicFunctions {
	public static String getNumberWithCountryCode(String phoneNumber) {
		phoneNumber = phoneNumber.replaceAll(" ", "");
		phoneNumber = phoneNumber.replaceAll("-", "");
		if (phoneNumber.length()>=10)
			return "+91" + phoneNumber.substring(phoneNumber.length() - 10);
		else
			return phoneNumber;
	}
}
