package com.blackMonster.suzik.util;

import org.json.JSONException;
import org.json.JSONObject;

public class ServerUtils {

	private static final String P_MODULE = "module";
	private static final String P_MY_NUMBER = "myNumber";

	public static JSONObject addEssentialParamToJson(JSONObject root, String module)
			throws JSONException {
		root.put(P_MY_NUMBER, "9898741000");
		root.put(P_MODULE, module);
		return root;

	}

}
