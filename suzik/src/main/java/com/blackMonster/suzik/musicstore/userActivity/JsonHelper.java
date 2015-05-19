package com.blackMonster.suzik.musicstore.userActivity;

import android.util.Pair;

import com.blackMonster.suzik.musicstore.module.UserActivity;
import com.blackMonster.suzik.util.ServerUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import static com.blackMonster.suzik.util.LogUtils.LOGD;

class JsonHelper {
	private static final String TAG = "userActivity.JsonHelper";

	static class UserActivityJson {
		private static final String P_MAIN_TAG = "userActivity";

		private static final String P_POST_ID = "postId";
		private static final String P_SERVER_ID = "songId";
		private static final String P_ACTION = "action";
		private static final String P_COMPLETED_TS = "ts";
		private static final String P_DISCOVERY_SOURCE = "whoPlayed";

		private static final String P_MODULE = "storeTimelineData";

		private static final String P_R_MAIN_TAG = "response";
		private static final String P_R_POST_ID = "postId";
		private static final String P_R_STATUS = "songStatus";

		public static final int RESPONSE_OK = 1;
		public static final int RESPONSE_FAILED = 0;


		static JSONObject toJson(List<Pair<UserActivity, Long>> postParams)
				throws JSONException {
			JSONObject root = new JSONObject();
			JSONArray uaArray = new JSONArray();

			for (Pair<UserActivity, Long> activity : postParams) {
				uaArray.put(getSingleObject(activity));
			}

			root.put(P_MAIN_TAG, uaArray);
			ServerUtils.addEssentialParamToJson(root, P_MODULE);
			LOGD(TAG + "hello", root.toString());
			return root;
		}

		private static JSONObject getSingleObject(
				Pair<UserActivity, Long> activity) throws JSONException {

			JSONObject obj = new JSONObject();
			obj.put(P_POST_ID, activity.first.id());
			obj.put(P_SERVER_ID, activity.second);
			obj.put(P_ACTION, activity.first.getActionString());
			obj.put(P_COMPLETED_TS, activity.first.completedTS());
            obj.put(P_DISCOVERY_SOURCE, new JSONArray(activity.first.getFriends()));
			return obj;
		}

	
		static HashMap<Long, Integer> parseResponse(JSONObject response)
				throws JSONException {

			HashMap<Long, Integer> idStatus = new HashMap<Long, Integer>();
			JSONArray responseArray = response.getJSONArray(P_R_MAIN_TAG);

			for (int i = 0; i < responseArray.length(); i++) {
				JSONObject responseObj = (JSONObject) responseArray.get(i);

				idStatus.put(responseObj.getLong(P_R_POST_ID),
						responseObj.getInt(P_R_STATUS));
			}

			return idStatus;

		}

	}

}
