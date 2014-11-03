package com.blackMonster.suzik.musicstore.timeline;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Pair;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.blackMonster.suzik.AppConfig;
import com.blackMonster.suzik.AppController;
import com.blackMonster.suzik.musicstore.module.UserActivity;

class ServerHelper {

	public static final String TAG = "Timeline.ServerHelper";

	static HashMap<Long, Integer> postUserActivity(
			List<Pair<UserActivity, Long>> postParams) throws JSONException,
			InterruptedException, ExecutionException {
		if (true) return dummyResponse(postParams);
		JSONObject postJson = JsonHelper.UserActivityJson.toJson(postParams);

		RequestFuture<JSONObject> future = RequestFuture.newFuture();
		JsonObjectRequest request = new JsonObjectRequest(AppConfig.MAIN_URL,
				postJson, future, future);
		AppController.getInstance().addToRequestQueue(request);

		JSONObject response = future.get();
		return JsonHelper.UserActivityJson.parseResponse(response);

	}

	private static HashMap<Long, Integer> dummyResponse(
			List<Pair<UserActivity, Long>> postParams) {
		HashMap<Long, Integer> result = new HashMap<Long, Integer>();
		
		for (Pair<UserActivity, Long> i : postParams) {
			result.put(i.first.id(),JsonHelper.UserActivityJson.RESPONSE_OK);
		}
		return result;
	}

}
