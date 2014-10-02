package com.blackMonster.suzik.sync.music;

import static com.blackMonster.suzik.util.LogUtils.LOGD;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

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
		  
		  //Map<Long, Integer> res;
		  //JsonHelper.DeletedSong.responseTo
		  
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw e;
		} catch (ExecutionException e) {
			e.printStackTrace();
			throw e;
		}
		
		
		return true;
	}
	
	
	
	
	
	
	static boolean postAddedSongs(List<AndroidData> addedSongs) throws JSONException, InterruptedException, ExecutionException {
		//if (true) return true;
		boolean result = false;
		JSONObject addedSongsJson = JsonHelper.AddedSong.toJson(addedSongs);
		LOGD("srverhelop", "josn received");
	
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
