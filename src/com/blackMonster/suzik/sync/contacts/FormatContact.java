package com.blackMonster.suzik.sync.contacts;

public class FormatContact {

		public static String standerdizeNumber(String phoneNumber, String myCountryCode, int myStandardMobileNumberLength) {
			phoneNumber = phoneNumber.trim();
			if (phoneNumber.charAt(0) == '+') {
				phoneNumber = phoneNumber.replaceAll("\\D", "");
				return "+" + phoneNumber;
			} 
			
						
			phoneNumber = phoneNumber.replaceAll("\\D", "");
			
			if (phoneNumber.length() >= myStandardMobileNumberLength) {
				phoneNumber = myCountryCode + phoneNumber.substring(phoneNumber.length() - 10);
				return phoneNumber;
			}
				
			return null;
//			
//			
//			//phoneNumber = phoneNumber.replaceAll("-", "");
//			if (phoneNumber.length()>=10)
//				return "+91" + phoneNumber.substring(phoneNumber.length() - 10);
//			else
//				return phoneNumber;
		}
}
