package com.blackMonster.suzik.sync.music;
import static com.blackMonster.suzik.util.LogUtils.LOGD;
import static com.blackMonster.suzik.util.LogUtils.LOGI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;

import android.content.Context;
import android.content.Intent;

import com.blackMonster.suzik.AppConfig;
import com.blackMonster.suzik.MainPrefs;
import com.blackMonster.suzik.musicstore.module.UserActivity;
import com.blackMonster.suzik.musicstore.timeline.UserActivityQueue;
import com.blackMonster.suzik.sync.Syncer;
import com.blackMonster.suzik.sync.music.CacheTable.CacheData;
import com.blackMonster.suzik.sync.music.QueueAddedSongs.QueueData;

public class AddedSongsResponseHandler extends Syncer {
private static final  String TAG = "AddedSongsResponseHandler";
	@Override
	public  boolean onPerformSync() throws Exception {

		synchronized (SongsSyncer.LOCK) {
			return startSync();
		}
	
	}

	private boolean startSync() throws JSONException, InterruptedException, ExecutionException {
		LOGI(TAG,"onPerformSync start");

		if (QueueAddedSongs.isEmpty(this))
			return true;

		HashMap<String, Long> fPrintIdMap;
		LOGD(TAG,"going to contact server");
		fPrintIdMap = getQueueStatusFromServer();
		LOGD(TAG,"server response and json parsing done");

		if (fPrintIdMap.size() > 0) {
			LOGD(TAG,"replied " + fPrintIdMap.size());
			List<QueueData> queueDataList = QueueAddedSongs.getAllData(this);
			
			
			for (QueueData currQd : queueDataList) {
				Long serverId = fPrintIdMap.get(currQd.getfPrint());
				if (serverId == null) continue;
				
		
				LOGD(TAG,"inserting " + currQd.getFileName() + " to cachel table");

				CacheTable.insert(
						new CacheData(null, serverId, currQd.getSong(), currQd.getfPrint(), currQd.getFileName()), this);
				LOGD(TAG,"removing from queue");

				QueueAddedSongs.remove(currQd.getId(), this);
				UserActivityQueue.add(new UserActivity(serverId, UserActivity.ACTION_OUT_APP_DOWNLOAD), this);
			}
			
			
		/*	for (Map.Entry<String, Long> entry : fPrintIdMap.entrySet()) {

				long serverId = entry.getValue();
				if (serverId == 0) {
					//serverId = getNewSongId();
					LOGD(TAG,"id 0 for " + entry.getKey());

					
				}
				QueueData qData = QueueAddedSongs.search(entry.getKey(), this);
				
				LOGD(TAG,"inserting " + qData.getFileName() + " to cachel table");

				CacheTable.insert(
						new CacheData(null, serverId, qData.getSong(), qData.getfPrint(), qData.getFileName()), this);
				LOGD(TAG,"removing from queue");

				QueueAddedSongs.remove(entry.getKey(), this);
				UserActivityQueue.add(new UserActivity(serverId, UserActivity.ACTION_OUT_APP_DOWNLOAD), this);

			}*/

			if (!QueueAddedSongs.isEmpty(this)) {
				futureCall(this);
			}
		} else {
			LOGD(TAG,"reply size zero");
			QueueAddedSongs.clearAll(this);
			// SongsSyncer.syncNow(context); //test this
			startService(new Intent(this, SongsSyncer.class));
		}
		LOGI(TAG,"onperformSync done");

		return true;		
	}

	private long getNewSongId() {
		int id = MainPrefs.getUnIdentifiableSongId(this) + 1;
		MainPrefs.setUnIndetifiableSongId(id, this);
		return id;
	}

	private HashMap<String, Long> getQueueStatusFromServer()
			throws JSONException, InterruptedException, ExecutionException {
		return ServerHelper.postFingerPrints(this);

	}

	static void futureCall(Context context) {
		long time = QueueAddedSongs.getRowCount(context)
				* AppConfig.TIME_PROCESSING_NEW_SONG_SERVER_MS;
		Syncer.callFuture(AddedSongsResponseHandler.class, time, context);

	}

}
