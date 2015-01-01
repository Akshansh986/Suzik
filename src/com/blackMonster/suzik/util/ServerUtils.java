package com.blackMonster.suzik.util;

import org.json.JSONException;
import org.json.JSONObject;

import com.blackMonster.suzik.AppController;
import com.blackMonster.suzik.MainPrefs;

public class ServerUtils {

	private static final String P_MODULE = "module";
	private static final String P_MY_NUMBER = "myNumber";
	private static final String P_CMD = "cmd";


	public static JSONObject addEssentialParamToJson(JSONObject root, String module)
			throws JSONException {

		root.put(P_MY_NUMBER,Utils.formatPhoneNumberForJson(MainPrefs.getMyNo(AppController.getInstance().getApplicationContext())));
//		root.put(P_MY_NUMBER, "6666666666");

		root.put(P_MODULE, module);
		return root;

	}
	public static JSONObject addEssentialParamToJson(JSONObject root, String module, String cmd)
			throws JSONException {

		root.put(P_MY_NUMBER,Utils.formatPhoneNumberForJson(MainPrefs.getMyNo(AppController.getInstance().getApplicationContext())));
//		root.put(P_MY_NUMBER, "6666666666");

		root.put(P_MODULE, module);
		root.put(P_CMD, cmd);
		return root;

	}

}
