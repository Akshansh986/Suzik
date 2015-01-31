package com.blackMonster.suzik.sync.music;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.blackMonster.suzik.AppConfig;
import com.blackMonster.suzik.AppController;
import com.blackMonster.suzik.sync.music.AndroidMusicHelper.AndroidData;
import com.blackMonster.suzik.sync.music.InAapSongTable.InAppSongData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.blackMonster.suzik.util.LogUtils.LOGD;


class ServerHelper {
	
	public static final String TAG ="ServerHelper";
	
	

	static HashMap<Long, Integer> postDeletedSongs(List<Long> ids) throws JSONException,
			InterruptedException, ExecutionException {

		JSONObject deletedSongs = JsonHelper.DeletedSong.toJson(ids);

		RequestFuture<JSONObject> future = RequestFuture.newFuture();
		JsonObjectRequest request = new JsonObjectRequest(AppConfig.MAIN_URL,
				deletedSongs, future, future);
		AppController.getInstance().addToRequestQueue(request);

			JSONObject response = future.get();

			return JsonHelper.DeletedSong.parseResponse(response);
	
	}

	static boolean postAddedSongs(List<AndroidData> addedSongs)
			throws JSONException, InterruptedException, ExecutionException {
		// if (true) return true;
		 
		if (addedSongs.isEmpty()) return true;
		boolean result = false;
		JSONObject addedSongsJson = JsonHelper.AddedSong.toJson(addedSongs);
		LOGD("srverhelop", "josn received");
		
		
		postAsyncronousJson(addedSongsJson);
		return true;
		
//		
//		RequestFuture<JSONObject> future = RequestFuture.newFuture();
//		JsonObjectRequest request = new JsonObjectRequest(AppConfig.MAIN_URL,
//				addedSongsJson, future, future);
//		AppController.getInstance().addToRequestQueue(request);
//
//		try {
//			LOGD("serverHeloper", "added song sending");
//			JSONObject response = future.get();
//			LOGD("SErverheloper", "response : " + response.toString());
//			return JsonHelper.AddedSong.parseResponse(response);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//			throw e;
//		} catch (ExecutionException e) {
//			e.printStackTrace();
//			throw e;
//		}


	}

	private static void postAsyncronousJson(JSONObject addedSongsJson) {
	
		JsonObjectRequest jsonReq = new JsonObjectRequest(Method.POST,
				AppConfig.MAIN_URL, addedSongsJson, new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						Log.d(TAG, "Response: " + response.toString());
											
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						VolleyLog.d(TAG, "Error: " + error.getMessage());
						Log.d(TAG, "Error: " + error.getMessage());

					}
				});

		AppController.getInstance().addToRequestQueue(jsonReq);
		
	}

	static HashMap<String, Long> postFingerPrints(Context context) throws JSONException, InterruptedException,
			ExecutionException {

		JSONObject fPrintsJson = JsonHelper.AddedSongsQueue
				.toJson(QueueAddedSongs.getAllFprints(context));

		RequestFuture<JSONObject> future = RequestFuture.newFuture();
		JsonObjectRequest request = new JsonObjectRequest(AppConfig.MAIN_URL,
				fPrintsJson, future, future);
		AppController.getInstance().addToRequestQueue(request);

		JSONObject response = future.get();
		LOGD(TAG,response.toString());
		HashMap<String, Long> parsedResponse = JsonHelper.AddedSongsQueue
				.parseResponse(response);
		return parsedResponse;

	}

	 static List<InAppSongData> getAllMySongs(Context context) throws InterruptedException, ExecutionException, JSONException {
		JSONObject credintials = JsonHelper.ServerAllSongs.getCredentials();

		RequestFuture<JSONObject> future = RequestFuture.newFuture();
		JsonObjectRequest request = new JsonObjectRequest(AppConfig.MAIN_URL,
				credintials, future, future);
		AppController.getInstance().addToRequestQueue(request);

		JSONObject response = future.get();
		List<InAppSongData> parsedResponse = JsonHelper.ServerAllSongs.parseResponse(response);
		return parsedResponse;
		
	}

}
