package com.blackMonster.suzik.sync.music;

import java.util.List;
import java.util.concurrent.ExecutionException;
import static com.blackMonster.suzik.util.LogUtils.*;

import org.json.JSONException;
import org.json.JSONObject;

import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.blackMonster.suzik.AppConfig;
import com.blackMonster.suzik.AppController;
import com.blackMonster.suzik.sync.music.AndroidHelper.AndroidData;


public class ServerHelper {
	
	static boolean postDeletedSongs(List<Long> ids) throws JSONException, InterruptedException, ExecutionException {
		
		JSONObject deletedSongs = JsonHelper.DeletedSong.toJson(ids);
		
		
		RequestFuture<JSONObject> future = RequestFuture.newFuture();
		JsonObjectRequest request = new JsonObjectRequest(AppConfig.MAIN_URL, deletedSongs, future, future);
		AppController.getInstance().addToRequestQueue(request);

		try {
		  JSONObject response = future.get(); 
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw e;
		} catch (ExecutionException e) {
			e.printStackTrace();
			throw e;
		}
		
		
		return true;
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
/*		
		// Post params to be sent to the server
	JsonObjectRequest req = new JsonObjectRequest(AppConfig.MAIN_URL,deletedSongs, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					VolleyLog.v("Response:%n %s", response.toString(4));
					//Toast.makeText(getBaseContext(), "Done!! :  " + response.toString(4),
					//		Toast.LENGTH_LONG).show();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
							
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyLog.e("Error: ", error.getMessage());
				
			}
		});

		// add the request object to the queue to be executed
		AppController.getInstance().addToRequestQueue(req);
		
*/		
		//return false;
		
	}
	
	static boolean postAddedSongs(List<AndroidData> addedSongs) throws JSONException, InterruptedException, ExecutionException {
		//if (true) return true;
		boolean result = false;
		JSONObject addedSongsJson = JsonHelper.AddedSong.toJson(addedSongs);
		LOGD("srverhelop", "josn received");
	
/*
		JsonObjectRequest req = new JsonObjectRequest(AppConfig.MAIN_URL,addedSongsJson, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					VolleyLog.v("Response:%n %s", response.toString(4));
					LOGD("Server" ,response.toString(4));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyLog.e("Error: ", error.getMessage());
				LOGD("Server" ,"error  " + error.getMessage());

			}
		});
		AppController.getInstance().addToRequestQueue(req);

		
		*/
		
		
		
		
		
		
		
		
		
		
		
		RequestFuture<JSONObject> future = RequestFuture.newFuture();
		JsonObjectRequest request = new JsonObjectRequest(AppConfig.MAIN_URL, addedSongsJson, future, future);
		AppController.getInstance().addToRequestQueue(request);


		try {
			LOGD("serverHeloper", "added song sending");
		  JSONObject response = future.get();
		  LOGD("SErverheloper","response : " + response.toString());
		  result=true;
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw e;
		} catch (ExecutionException e) {
			e.printStackTrace();
			throw e;
		}
		
		
		return result;
		
		
		
	}

}
