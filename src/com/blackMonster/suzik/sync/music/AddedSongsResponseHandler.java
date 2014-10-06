package com.blackMonster.suzik.sync.music;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;

import android.content.Context;
import android.content.Intent;

import com.blackMonster.suzik.AppConfig;
import com.blackMonster.suzik.MainPrefs;
import com.blackMonster.suzik.sync.Syncer;
import com.blackMonster.suzik.sync.music.CacheTable.CacheData;
import com.blackMonster.suzik.sync.music.QueueAddedSongs.QueueData;

public class AddedSongsResponseHandler extends Syncer {

	@Override
	public synchronized boolean onPerformSync() throws Exception {
		if (QueueAddedSongs.isEmpty(this))
			return true;

		HashMap<String, Long> fPrintIdMap;

		fPrintIdMap = getQueueStatusFromServer();

		if (fPrintIdMap.size() > 0) {

			for (Map.Entry<String, Long> entry : fPrintIdMap.entrySet()) {

				long id = entry.getValue();
				if (id == 0)
					id = getNewSongId();
				QueueData qData = QueueAddedSongs.search(entry.getKey(), this);

				CacheTable.insert(
						new CacheData(id, qData.getSong(), qData.getfPrint(),
								qData.getFileName()), this);
				QueueAddedSongs.remove(entry.getKey(), this);

			}

			if (!QueueAddedSongs.isEmpty(this)) {
				futureCall(this);
			}
		} else {
			QueueAddedSongs.clearAll(this);
			// SongsSyncer.syncNow(context); //test this
			startService(new Intent(this, SongsSyncer.class));
		}

		return true;
	}

	private long getNewSongId() {
		int id = MainPrefs.getUnIdentifiableSongId(this) + 1;
		MainPrefs.setUnIndetifiableSongId(id, this);
		return id;
	}

	private HashMap<String, Long> getQueueStatusFromServer()
			throws JSONException, InterruptedException, ExecutionException {
		return ServerHelper.postFingerPrints(
				QueueAddedSongs.getAllFprints(this), this);

	}

	static void futureCall(Context context) {
		long time = QueueAddedSongs.getRowCount(context)
				* AppConfig.TIME_PROCESSING_NEW_SONG_SERVER_MS;
		Syncer.callFuture(AddedSongsResponseHandler.class, time, context);

	}

}
