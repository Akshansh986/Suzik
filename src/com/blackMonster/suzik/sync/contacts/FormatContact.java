package com.blackMonster.suzik.sync.contacts;

public class FormatContact {

		public static String standerdizeNumber(String phoneNumber) {
			phoneNumber = phoneNumber.replaceAll(" ", "");
			phoneNumber = phoneNumber.replaceAll("-", "");
			if (phoneNumber.length()>=10)
				return "+91" + phoneNumber.substring(phoneNumber.length() - 10);
			else
				return phoneNumber;
		}
}
