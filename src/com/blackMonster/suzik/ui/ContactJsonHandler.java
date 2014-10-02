package com.blackMonster.suzik.ui;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ContactJsonHandler {
	private static final String TAG = "ContactJsonHandler";

	private static final String P_MODULE = "module";
	private static final String P_MY_NUMBER = "myNumber";
	private static final String P_CONTACT_LIST = "contactList";

	private static final String P_NUMBER = "number";
	private static final String P_ACTION = "action";

	private static final String V_ACTION_ADD = "add";
	private static final String V_ACTION_DELETE = "delete";
	private static final String V_MODULE = "contact";

	public static JSONObject getJSON() {
		JSONObject root = new JSONObject();
		JSONArray contactArray = new JSONArray();

		try {
		
			contactArray.put(getSingleContactJsonObject("9560558203",
					V_ACTION_ADD));
			contactArray.put(getSingleContactJsonObject("9868454788",
					V_ACTION_ADD));
			contactArray.put(getSingleContactJsonObject("7894562102",
					V_ACTION_ADD));
			contactArray.put(getSingleContactJsonObject("9566566647",
					V_ACTION_ADD));
			contactArray.put(getSingleContactJsonObject("9560558203",
					V_ACTION_ADD));
			contactArray.put(getSingleContactJsonObject("9568958203",
					V_ACTION_ADD));

			root.put(P_CONTACT_LIST, contactArray);
			root.put(P_MY_NUMBER, "9898741000");
			root.put(P_MODULE, V_MODULE);


		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Log.d(TAG, root.toString());
		return root;
	}

	private static JSONObject getSingleContactJsonObject(String number,
			String action) throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put(P_NUMBER, number);
		obj.put(P_ACTION, action);
		return obj;

	}

}
