package com.blackMonster.suzik.sync.music;
import android.content.Context;
import android.content.Intent;

import com.blackMonster.suzik.AppConfig;
import com.blackMonster.suzik.MainPrefs;
import com.blackMonster.suzik.musicstore.module.UserActivity;
import com.blackMonster.suzik.musicstore.userActivity.UserActivityManager;
import com.blackMonster.suzik.sync.Syncer;
import com.blackMonster.suzik.sync.music.CacheTable.CacheData;
import com.blackMonster.suzik.sync.music.QueueAddedSongs.QueueData;
import com.blackMonster.suzik.ui.UiBroadcasts;

import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.blackMonster.suzik.util.LogUtils.LOGD;
import static com.blackMonster.suzik.util.LogUtils.LOGI;

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

				long id = CacheTable.insert(
						new CacheData(null, serverId, currQd.getSong(), currQd.getfPrint(), currQd.getFileName()), this);
				LOGD(TAG,"removing from queue");

				QueueAddedSongs.remove(currQd.getId(), this);

				if (MainPrefs.isFirstTimeMusicSyncDone(this)) {
					UserActivityManager.add(new UserActivity(currQd.getSong(),null, id, UserActivity.ACTION_OUT_APP_DOWNLOAD, UserActivity.STREAMING_FALSE, System.currentTimeMillis()), this);
				}
			
			}
			


			if (!QueueAddedSongs.isEmpty(this)) {
				futureCall(this);
			}
			else {
				MainPrefs.setFirstTimeMusicSyncDone(this);
                UiBroadcasts.broadcastMusicDataChanged(this);
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

	/*private long getNewSongId() {
		int id = MainPrefs.getUnIdentifiableSongId(this) + 1;
		MainPrefs.setUnIndetifiableSongId(id, this);
		return id;
	}*/

	private HashMap<String, Long> getQueueStatusFromServer()
			throws JSONException, InterruptedException, ExecutionException {
		return ServerHelper.postFingerPrints(this);

	}

	static void futureCall(Context context) {
		long time = getRemainingTimeMs(context);
		Syncer.callFuture(AddedSongsResponseHandler.class, time, context);

	}

    public static long getRemainingTimeMs(Context context) {
//        return QueueAddedSongs.getRowCount(context)
//                * AppConfig.TIME_PROCESSING_NEW_SONG_SERVER_MS;
        return AppConfig.MINUTE_IN_MILLISEC;
    }

	@Override
	public String getBroadcastString() {
		return null;
	}

}
