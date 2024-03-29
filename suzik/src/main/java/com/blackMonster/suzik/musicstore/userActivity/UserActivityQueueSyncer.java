package com.blackMonster.suzik.musicstore.userActivity;

import android.util.Pair;

import com.blackMonster.suzik.musicstore.module.UserActivity;
import com.blackMonster.suzik.sync.Syncer;
import com.blackMonster.suzik.sync.music.MusicSyncManager;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.blackMonster.suzik.util.LogUtils.LOGD;
import static com.blackMonster.suzik.util.LogUtils.LOGI;

public class UserActivityQueueSyncer extends Syncer {
	private static final String TAG = "UserActivityQueueSyncer";

	@Override
	public boolean onPerformSync() throws Exception {
		LOGI(TAG, "onPerformSync start");

		if (QueueUserActivity.isEmpty(this))
			return true;

		List<UserActivity> userActivity = QueueUserActivity.getAllData(this);
		LOGI(TAG, "getAllDataFromQueue done");

		HashMap<Long, Integer> response = postToServer(userActivity);
		LOGI(TAG, "postToServer done");

		boolean res =  handleResponse(userActivity, response);
		LOGI(TAG, "Alldone done");
		return res;

	}

	private boolean handleResponse(List<UserActivity> userActivity,
			HashMap<Long, Integer> response) {
		boolean result = true;

		for (UserActivity ua : userActivity) {
			Integer status = response.get(ua.id());
			if (status != null
					&& status == JsonHelper.UserActivityJson.RESPONSE_OK) {
				QueueUserActivity.remove(ua.id(), this);
			} else {
				result = false;
			}
		}

		return userActivity.size() == response.size() && result;

	}

	private HashMap<Long, Integer> postToServer(List<UserActivity> userActivity) throws JSONException, InterruptedException, ExecutionException {
		List<Pair<UserActivity, Long>> postParams = new ArrayList<Pair<UserActivity, Long>>();

		for (UserActivity ua : userActivity) {

			if (!ua.isOnlinePlayedSong()) {
				Long songId = ua.songId();
                LOGD(TAG,"songid " + songId);
                if (songId == null) {
                    userActivity.remove(ua);
                    continue;
                }

                Long serverId = MusicSyncManager
                        .getServerId(songId, this);

                postParams.add(new Pair<UserActivity, Long>(ua,serverId));
			}
			else
			{
				postParams.add(new Pair<UserActivity, Long>(ua,ua.songId()));	
			}
			
			
		}

		return ServerHelper.postUserActivity(postParams);

	}

	@Override
	public String getBroadcastString() {
		return null;
	}

	
}
